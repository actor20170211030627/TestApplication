package com.actor.testapplication.utils.okhttputils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.actor.myandroidframework.utils.ConfigUtils;
import com.actor.myandroidframework.utils.TextUtils2;
import com.actor.myandroidframework.utils.okhttputils.OkHttpConfigUtils;
import com.actor.testapplication.utils.okhttputils.lifecycle.MyOkHttpLifecycleUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * description: 这是对鸿洋大神okhttputils的简单封装, get/post方式请求数据, 上传单个/多个文件, 下载文件, getBitmap <br />
 * 1.使用前需要添加依赖
 * <pre>
 *     //https://github.com/hongyangAndroid/okhttputils 张鸿洋的okhttp, 55.9KB
 *     implementation ('com.zhy:okhttputils:2.6.2') {
 *         exclude group: 'com.squareup.okhttp3', module: 'okhttp'//3.3.1
 *     }
 * </pre>
 * 2.使用前先配置
 * <ul>
 *     <li>{@link #setBaseUrl(String)}: 设置baseUrl(也可不设置)</li>
 *     <li>{@link #initOkHttp(boolean)}: 初始化OkHttp</li>
 *     <li>{@link OkHttpConfigUtils#addLogInterceptor(OkHttpClient.Builder, boolean)}: 配置完成后, 才添加日志拦截器</li>
 *     <li>{@link #setOkHttpClient(OkHttpClient)}: 设置{@link OkHttpUtils}的{@link OkHttpClient}</li>
 * </ul>
 * 3.如果你要混淆代码, 需要添加以下混淆规则:
 * <pre>
 *     #############################################################################
 *     ## okhttputils
 *     -dontwarn com.zhy.http.**
 *     -keep class com.zhy.http.**{*;}
 * </pre>
 *
 * @Author     : ldf
 * @date       : 2019/3/13 on 17:37
 *
 * @deprecated 建议使用轮子哥的 {@link com.hjq.http.EasyHttp}
 */
@Deprecated
public class MyOkHttpUtils {

    /**
     * baseUrl, 需要自己设置
     */
    protected static String baseUrl = "";

    /**
     * 设置BaseUrl
     */
    public static void setBaseUrl(@NonNull String baseUrl) {
        if (baseUrl != null) MyOkHttpUtils.baseUrl = baseUrl;
    }

    /**
     * 配置okhttp
     */
    public static OkHttpClient.Builder initOkHttp(boolean isDebugMode) {
        return OkHttpConfigUtils.initOkHttp(isDebugMode)
                //cookie
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(ConfigUtils.APPLICATION)));
    }

    /**
     * 配置张鸿洋的OkHttpUtils
     */
    public static void setOkHttpClient(@NonNull OkHttpClient okhttpClient) {
        OkHttpUtils.initClient(okhttpClient);
    }

    public static OkHttpClient getOkHttpClient() {
        return OkHttpUtils.getInstance().getOkHttpClient();
    }

    /**
     * 获取 BASE_URL
     * @param url 如果是"http/ws"开头, 直接返回url.
     *           如果不是"http/ws"开头, 会在前面加上 BASE_URL
     */
    protected static String getUrl(String url) {
        if (url == null) return baseUrl;
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ws://") || url.startsWith("wss://")) return url;
        return baseUrl + url;
    }



    ///////////////////////////////////////////////////////////////////////////
    // 2.下方是请求
    ///////////////////////////////////////////////////////////////////////////
    public static <T> void get(String url, Map<String, Object> params,BaseCallback<T> callback) {
        get(url, null, params, callback);
    }

    /**
     * get方式请求数据
     * @param url       地址
     * @param headers   请求头
     * @param params    参数,一般用LinkedHashMap<String, Object>
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象,示例:
     *                      JSONObject, String, List<UserInfo>,
     *                      BaseInfo, BaseInfo<UserInfo>, BaseInfo<List<UserInfo>>, ...
     */
    public static <T> void get(String url, Map<String, Object> headers, Map<String, Object> params, BaseCallback<T> callback) {
        Request.Builder builder = new Request.Builder()
                .get()
                .url(urlAppendParams(getUrl(url), params))
                .tag(callback == null ? null : callback.tag);
        addHeaders(headers, builder);
        getOkHttpClient()//.newBuilder().connectTimeout()...
                .newCall(builder.build())
                .enqueue(callback == null ? new NullCallback() : callback);//不能为空
        if (callback != null) callback.onBefore(null, callback.getRequestId());//okhttp3.Callback需要手动调一下...

        /**
         * 不能使用{@link OkHttpUtils#get()}, 因为↓ 这个方法组合成的url不对
         * @see com.zhy.http.okhttp.builder.GetBuilder#appendParams(String, Map)
         * 会导致请求时url一些不该被Encode的字符被Encode(: => %3A), 例:
         * @see com.actor.map.baidu.BaiduMapUtils#getAddressByNet(double, double, BaseCallback)//"报错: APP Mcode码校验失败"
         */
//        OkHttpUtils.get().url(getUrl(url))
//                .tag(callback == null ? null : callback.tag)
//                .headers(cleanNullParamMap(headers))
//                .params(cleanNullParamMap(params))
//                //请求id, 会在回调中返回, 可用于列表请求中传入item的position, 然后在回调中根据id修改对应的item的值
//                .id(callback == null ? 0 : callback.id)
//                .build()
////                .connTimeOut(20000).readTimeOut(20000).writeTimeOut(20000)
//                .execute(callback);
    }

    /**
     * get同步请求
     */
    public static @Nullable Response getSync(String url, Map<String, Object> headers, Map<String, Object> params, Object tag) {
        Request.Builder builder = new Request.Builder()
                .get()
                .url(urlAppendParams(getUrl(url), params))
                .tag(tag);
        addHeaders(headers, builder);
        try {
            return getOkHttpClient().newCall(builder.build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void post(String url, Map<String, Object> params, BaseCallback<T> callback) {
        post(url, null, params, callback);
    }

    /**
     * post方式请求网络
     * @param url       地址
     * @param params    请求参数, 放在请求体中
     * @param callback  监听
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void post(String url, Map<String, Object> headers, Map<String, Object> params, BaseCallback<T> callback) {
        OkHttpUtils.post().url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .headers(cleanNullParamMap(headers))
                .params(cleanNullParamMap(params))
                //请求id, 会在回调中返回, 可用于列表请求中传入item的position, 然后在回调中根据id修改对应的item的值
                .id(callback == null ? 0 : callback.getRequestId())
                .build()
                //.connTimeOut(15000).readTimeOut(15000).writeTimeOut(15000)
                .execute(callback);
    }

    /**
     * post同步请求, 比如token过期后重新获取token, 在同一线程内刷新token
     */
    public static @Nullable Response postSync(String url, Map<String, Object> headers, Map<String, Object> params, Object tag) {
        try {
            return OkHttpUtils.post().url(getUrl(url))
                    .tag(tag)
                    .headers(cleanNullParamMap(headers))
                    .params(cleanNullParamMap(params))
                    .build().execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过Post方法向服务器传递String,待试...
     * @param url       地址
     * @param string    传递的String放入请求体(body)
     * @param callback  回调
     */
    public static <T> void postString(String url, String string, BaseCallback<T> callback) {
        OkHttpUtils.postString().url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .content(string)
                .build().execute(callback);
    }

    public static <T> void postJson(String url, @Nullable String json, BaseCallback<T> callback) {
        postJson(url, null, json, callback);
    }

    /**
     * @param object 会将object转换成json
     */
    public static <T> void postJson(String url, @Nullable Object object, BaseCallback<T> callback) {
//        postJson(url, null, JSONObject.toJSONString(object), callback);//FastJson
        postJson(url, null, GsonUtils.toJson(object), callback);//Gson
    }

    /**
     * 通过Post方法向服务器传递Json
     * 1.设置Headers里参数:Content-Type:application/json
     * 服务端接收的时候,要加上@RequestBody注解,才能解析成实体
     * @param url       地址
     * @param params    参数
     * @param json      传递的Json放入请求体(body)
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postJson(String url, @Nullable Map<String, Object> params, @Nullable String json, BaseCallback<T> callback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        if (json == null) json = "{}";
        if (true) {
            OkHttpUtils.postString()
                    .url(urlAppendParams(getUrl(url), params))//postString的时候, 参数拼在url后面
                    .tag(callback == null ? null : callback.tag)
                    .content(json)//判空否则会报错
                    .mediaType(mediaType)//一定要设置MediaType:设置Content-Type 标头中包含的媒体类型值
                    .id(callback == null ? 0 : callback.getRequestId())
                    .build()
                    .execute(callback);
        } else {
            //也可以这样直接使用Okhttp3直接请求
            RequestBody requestBody = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(getUrl(url))
                    .post(requestBody)
                    .tag(callback == null ? null : callback.tag)
                    .build();
            getOkHttpClient()
                    .newCall(request)
                    .enqueue(callback == null ? new NullCallback() : callback);
            //okhttp3.Callback需要手动调一下...
            if (callback != null) callback.onBefore(null, callback.getRequestId());
        }
    }

    /**
     * 将文件以流的形式，上传到服务器
     * @param url
     * @param file
     * @param callback 回调
     * @param <T> 要解析成什么类型的对象
     */
    public static <T> void postFile(String url, File file, BaseCallback<T> callback) {
        OkHttpUtils.postFile().url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .file(file)
                //请求id, 会在回调中返回, 可用于列表请求中传入item的position, 然后在回调中根据id修改对应的item的值
                .id(callback == null ? 0 : callback.getRequestId())
                .build().execute(callback);
    }

    /**
     * Post表单形式上传单个文件. 注意:如果是图片/视频, 需自己压缩后在上传
     * @param url       地址
     * @param key       表单上传文件的key
     * @param file      文件
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String key, File file, Map<String, Object> headers,
                                     Map<String, Object> params, PostFileCallback<T> callback) {
        List<File> files = new ArrayList<>();
        files.add(file);
        postFiles(url, key, files, headers, params, callback);
    }

    /**
     * Post表单形式上传文件,同时上传多个文件. 注意:如果是图片/视频, 需自己压缩后在上传
     * @param url       地址
     * @param key       表单上传文件的key
     * @param filePaths 文件路径集合
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String key, Collection<String> filePaths,
                                     Map<String, Object> headers, Map<String, Object> params,
                                     PostFileCallback<T> callback) {
        List<File> files = new ArrayList<>();
        if (filePaths != null && !filePaths.isEmpty()) {
            for (String filePath : filePaths) {
                if (!TextUtils.isEmpty(filePath)) {
                    files.add(new File(filePath));
                }
            }
        }
        postFiles(url, key, files, headers, params, callback);
    }

    /**
     * Post表单形式上传文件,同时上传多个文件
     * @param url       地址
     * @param key       表单上传文件的key, 例如常用的有 file、files、mFiles, 这个字段是后台定义的
     * @param files     文件集合
     * @param headers   请求体,示例:headers.put("APP-Key", "APP-Secret222");
     *                  headers.put("APP-Secret", "APP-Secret111");
     * @param params    参数
     * @param callback  回调
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFiles(@NonNull String url, String key, List<File> files,
                                     Map<String, Object> headers, Map<String, Object> params,
                                     PostFileCallback<T> callback) {
        PostFormBuilder builder = OkHttpUtils.post().url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .headers(cleanNullParamMap(headers))
                .params(cleanNullParamMap(params));
//                .files(key, files);
        if (files != null && !files.isEmpty()) {
            for (File file : files) {
                //<input type="file" name="mFile"/>注意这个mFile要和后端协商好,否则接收不到
                if (file != null && file.isFile()) {
//                    try {
//                        /**
//                         * https://github.com/square/okhttp/issues/4564
//                         * @see okhttp3.Headers#checkValue(String, String)
//                         * fixed 文件名UTF-8转码,避免上传中文文件时以下方法抛异常问题
//                         *
//                         * 已修复: https://github.com/square/okhttp/pull/4584
//                         */
                        builder.addFile(key, /*URLEncoder.encode(*/file.getName()/*, "UTF-8")*/, file);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        builder.id(callback == null ? 0 : callback.getRequestId()).build().execute(callback);
    }

    /**
     * 把参数通过body传到服务器
     * @param url       地址
     * @param params    参数(value类型不能是文件等, 因为最后会转换成String传到服务器)
     * @param callback  回调, 如果要使用id, 在这个回调的构造方法中传入!!
     * @param <T>       要解析成什么类型的对象
     */
    public static <T> void postFormBody(@NonNull String url, Map<String, Object> headers, Map<String, Object> params, BaseCallback<T> callback) {
//        Map<String, String> headerMap = cleanNullParamMap(headers);
//        Map<String, String> paramsMap = cleanNullParamMap(params);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                if (!TextUtils.isEmpty(key)) {
                    formBodyBuilder.add(key, getNoNullString(entry.getValue()));
                }
            }
        }
        Request.Builder builder = new Request.Builder()
                .url(getUrl(url))
                .post(formBodyBuilder.build())
                .tag(callback == null ? null : callback.tag);
        addHeaders(headers, builder);
        getOkHttpClient()
                .newCall(builder.build())
                .enqueue(callback == null ? new NullCallback() : callback);
        //okhttp3.Callback需要手动调一下...
        if (callback != null) callback.onBefore(null, callback.getRequestId());
    }

    /**
     * 请求图片,返回Bitmap
     * @param url 网址
     * @param callback 回调
     */
    public static void getBitmap(@NonNull String url, Map<String, Object> headers, Map<String, Object> params, GetBitmapCallback callback) {
        get(url, headers, params, callback);
    }

    /**
     * 下载文件回调
     * @param url 网址
     * @param callback 回调
     */
    public static void getFile(@NonNull String url, Map<String, Object> headers, Map<String, Object> params, GetFileCallback callback) {
        get(url, headers, params, callback);
    }

    public static <T> void deleteJson(@NonNull String url, String json, BaseCallback<T> callback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        OkHttpUtils.delete().url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .requestBody(requestBody)
                .id(callback == null ? 0 : callback.getRequestId())
                .build().execute(callback);
    }

    public static <T> void putJson(@NonNull String url, String json, BaseCallback<T> callback) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        OkHttpUtils.put().url(getUrl(url))
                .tag(callback == null ? null : callback.tag)
                .requestBody(requestBody)
                .id(callback == null ? 0 : callback.getRequestId())
                .build().execute(callback);
    }

    /**
     * 获取Socket
     * @param wsUrl webSocket地址
     * @param headers 请求头
     * @param params 参数
     * @param tag LifecycleOwner
     * @param listener 回调监听
     * @return WebSocket
     */
    public static WebSocket getSocket(@NonNull String wsUrl,
                                      @Nullable Map<String, Object> headers,
                                      @Nullable Map<String, Object> params,
                                      @Nullable LifecycleOwner tag,
                                      @NonNull WebSocketListener listener) {
        Request.Builder builder = new Request.Builder()
//                .get()
                .url(urlAppendParams(getUrl(wsUrl), params))
                .tag(tag);
        addHeaders(headers, builder);
        MyOkHttpLifecycleUtils.addObserver(tag);
        return getOkHttpClient().newWebSocket(builder.build(), listener);
    }

    /**
     * 重连Socket
     * @param webSocket WebSocket
     * @param listener
     */
    public static void socketReConnecton(@NonNull WebSocket webSocket, @NonNull WebSocketListener listener) {
        Request request = webSocket.request();
        getOkHttpClient().newWebSocket(request, listener);
    }

    /**
     * 自定义请求示例
     */
    public static <T> void customRequest(String url, Map<String, Object> params, BaseCallback<T> callback) {
        if (true) return;
        //自定义put
        RequestBody requestBody = RequestBody.create((MediaType) null, "json or something");
        OkHttpUtils.put()//also can use delete() ,head() , patch()
                .url(getUrl(url))
                .requestBody("String")
                .requestBody(requestBody)// String/RequestBody
                .build()
                .execute(callback);

        OkHttpUtils.head()
                .url(getUrl(url))
                .addParams("name", "zhy")
                .build()
                .execute(callback);

        //自定义POST
        Request request = new Request.Builder()
                .method("POST",requestBody)
                .url(urlAppendParams(getUrl(url), params))
                .build();
        getOkHttpClient()
                .newCall(request)
                .enqueue(callback);
        if (callback != null) callback.onBefore(null, callback.getRequestId());//okhttp3.Callback需要手动调一下...
    }

    /**
     * 取消请求:RequestCall call = OkHttpUtils.get().url(url).build();
     */
    public static void cancel(RequestCall call) {
        if (call != null) call.cancel();
    }

    /**
     * 取消请求
     * @param tag 传this(activity or fragment or others),在onDestroy的时候:MyOkHttpUtils.cancelTag(this);
     */
    public static void cancelTag(Object tag) {
        OkHttpUtils.getInstance().cancelTag(tag);
    }

    /**
     * 清空Cookie & Session
     */
    public static void clearCookie$Session() {
        CookieJar cookieJar = getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl) {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }


    /**
     * 添加Headers
     * @param headers 请求头参数
     * @param builder builder
     */
    protected static void addHeaders(Map<String, Object> headers, Request.Builder builder) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                String key = entry.getKey();
                if (!TextUtils.isEmpty(key)) {
                    builder.addHeader(key, getNoNullString(entry.getValue()));
                }
            }
        }
    }
    /**
     * 将params拼接到url后面
     * @param url url
     * @param params 参数
     * @return 拼接后的url
     */
    @NonNull
    protected static String urlAppendParams(@NonNull String url, @Nullable Map<String, Object> params) {
        if (params == null || params.isEmpty()) return url;
        StringBuilder builder = new StringBuilder(url);
        //如果结尾有'&'
        if (builder.charAt(url.length() - 1) == '&') {
            builder.delete(url.length() - 1, url.length());
        }
        //是否'?'结尾
        boolean endWihtQuestionMark;
        if (!url.contains("?")) {
            builder.append("?");
            endWihtQuestionMark = true;
        } else {
            endWihtQuestionMark = url.endsWith("?");
        }
        //http://www.xxx.com/s?a=a & b=b & c=c
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (!TextUtils.isEmpty(key)) {
                String value = getNoNullString(entry.getValue());
                if (endWihtQuestionMark) {
                    builder.append(key).append("=").append(value);
                    endWihtQuestionMark = false;
                } else builder.append("&").append(key).append("=").append(value);
            }
        }
        return builder.toString();
    }

    /**
     * 清除key值为null的参数 & 保证value != null
     * 并且转换为Map<String, String>
     */
    @Nullable
    protected static Map<String, String> cleanNullParamMap(@Nullable Map<String, Object> map) {
        if (map == null || map.isEmpty()) return null;
        Map<String, String> returnMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!TextUtils.isEmpty(key)) {
                returnMap.put(key, getNoNullString(entry.getValue()));
            }
        }
        return returnMap.isEmpty() ? null : returnMap;
    }

    protected static String getNoNullString(Object object) {
        return TextUtils2.getNoNullString(object);
    }
}
