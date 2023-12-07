package com.example.hoaxhound;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("fake-news-detection")
    Call<ApiResponseModel> postData(
            @Field("key1") String value1
//            @Field("key2") String value2
    );
}
