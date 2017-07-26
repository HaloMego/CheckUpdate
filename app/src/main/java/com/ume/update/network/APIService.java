package com.ume.update.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface APIService {
    String BASE_URL = "http://www.baidu.com";

    @Streaming
    @GET
    Call<ResponseBody> downLoadFile(@Url String fileUrl);


    @GET
    Call<String> getAPKInfoFromTencent(@Url String fileUrl);

    @GET
    Call<String> getAPKInfoFromPure(@Url String fileUrl);
}
