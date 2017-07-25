package com.ume.update.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface APIService {
    String BASE_URL = "http://www.baidu.com";


//    downUrl ="http://www.wandoujia.com/apps/com.aspire.mm/binding?source=web_inner_referral_binded";

    @Streaming
    @GET
    Call<ResponseBody> downLoadFile(@Url String fileUrl);


    @GET("http://android.myapp.com/myapp/detail.htm?apkName=com.huimai365")
    Call<String> getAPKInfoFromTencent();

    @GET("http://www.wandoujia.com/apps/com.huimai365")
    Call<String> getAPKInfoFromPure();
}
