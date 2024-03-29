package com.actor.testapplication.utils.retrofit;

import androidx.annotation.NonNull;

import com.actor.testapplication.utils.retrofit.api.DownloadFileApi;
import com.blankj.utilcode.util.GsonUtils;

import java.util.HashMap;
import java.util.Map;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description: Retrofit网络请求 <br />
 * 1.由于本人用不习惯Retrofit, ∴if你要使用的话, 需要自己添加以下依赖:
 * <pre>
 * //https://github.com/square/retrofit
 * implementation 'com.squareup.retrofit2:retrofit:2.9.0'
 * //https://github.com/square/retrofit/tree/master/retrofit-converters/gson (已经依赖了Gson2.8.5)
 * implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
 * </pre>
 * 2.使用前先配置
 * <ul>
 *     <li>{@link #setBaseUrl(String)}: 设置baseUrl</li>
 *     <li>{@link #setOkHttpClient(OkHttpClient)}: 设置OkHttpClient</li>
 * </ul>
 *
 * @Author     : ldf <br />
 * @Date       : 2019/3/15 on 9:20
 * @deprecated 建议使用轮子哥的 {@link com.hjq.http.EasyHttp}
 */
@Deprecated
public class RetrofitNetwork {

    /**
     * baseUrl, 需要自己设置
     */
    protected static String       baseUrl = "";
    protected static OkHttpClient okHttpClient;
    protected static Converter.Factory converterFactory;
    protected static CallAdapter.Factory       callAdapterFactory;
    protected static final Map<String, Object> APIS    = new HashMap<>();

    private static DownloadFileApi downloadFileApi;

    /**
     * 初始化
     */
    public static void setBaseUrl(@NonNull String baseUrl) {
        if (baseUrl != null) RetrofitNetwork.baseUrl = baseUrl;
    }

    public static void setOkHttpClient(OkHttpClient okHttpClient) {
        RetrofitNetwork.okHttpClient = okHttpClient;
    }

    //返回json转换成Bean的Facory
    protected static Converter.Factory getConverterFactory() {
        if (converterFactory == null) {
//            converterFactory = new Retrofit2ConverterFactory();//FastJson
            converterFactory = GsonConverterFactory.create(GsonUtils.getGson());//Gson
        }
        return converterFactory;
    }

    //返回CallAdapterFactory, 如果需要, 可重写此方法
    protected static CallAdapter.Factory getCallAdapterFactory() {
        if (callAdapterFactory == null) {
//            callAdapterFactory = RxJava2CallAdapterFactory.create();
        }
        return callAdapterFactory;
    }

    /**
     * Okhttp/Retofit 上传进度监听, 如果使用:
     * 1.需要添加依赖
     * //https://github.com/JessYanCoding/ProgressManager Okhttp/Retofit/Glide下载进度监听
     * implementation 'me.jessyan:progressmanager:1.5.0'
     * 2.Application中初始化
     * //可监听Glide,Download,Upload进度
     * ProgressManager.getInstance().with(okHttpClientBuilder);
     */
    public static void addOnUploadListener(String url, ProgressListener progressListener) {
        ProgressManager.getInstance().addRequestListener(url, progressListener);
    }

    /**
     * Okhttp/Retofit/Glide下载进度监听,此操作请在页面初始化时进行,切勿多次注册同一个(内容相同)监听器.就算多注册也不报错...
     */
    public static void addOnDownloadListener(String url, ProgressListener progressListener) {
        ProgressManager.getInstance().addResponseListener(url, progressListener);
    }

    public static <T> T getApi(Class<T> apiClass) {
        Object aClass = APIS.get(apiClass.getName());
        if (aClass == null) {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(getConverterFactory());
            if (getCallAdapterFactory() != null) builder.addCallAdapterFactory(getCallAdapterFactory());
            Retrofit retrofit = builder.build();
            aClass = retrofit.create(apiClass);
            APIS.put(apiClass.getName(), aClass);
        }
        return (T) aClass;
    }

    //下载文件示例
    public static DownloadFileApi getDownloadFileApi() {
//        if (downloadFileApi == null) {
//            Retrofit.Builder builder = new Retrofit.Builder()
//                    .client(okHttpClient)
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(converterFactory);
//            if (callAdapterFactory != null) builder.addCallAdapterFactory(callAdapterFactory);
//            Retrofit retrofit = builder.build();
//            downloadFileApi = retrofit.create(DownloadFileApi.class);
//        }
//        return downloadFileApi;
        return getApi(DownloadFileApi.class);
    }
}
