package com.flippbidd.Model.Service;

import com.flippbidd.Model.request.ChatUser;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitAPI {

    @Headers({"Api-Token: b2580ec44bcf03b7d2519bd8099b391fa3d7e339","Content-Type: application/json"})
    @PUT("users/{user_id}")
    Call<JsonElement> updateData(@Path("user_id") String userId, @Body ChatUser dataModal);
}
