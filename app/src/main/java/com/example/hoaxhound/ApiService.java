package com.example.hoaxhound;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("fake-news-detection")
    Call<ApiResponseModel> postData(@Body RequestBodyModel requestBody);
}
