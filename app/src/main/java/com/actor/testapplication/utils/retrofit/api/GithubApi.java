package com.actor.testapplication.utils.retrofit.api;

import com.actor.testapplication.info.GithubInfo;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Description: 类的描述
 * Author     : ldf
 * Date       : 2019/8/18 on 20:53
 */
public interface GithubApi {

    //获取Response: Call<Response<GithubInfo>>
    @GET("/")
    Call<GithubInfo> get();
}
