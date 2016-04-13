package com.bsu.android.acd.pojo;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by surajdeuja on 4/5/16.
 */
public interface ImageUploadService {
    @Multipart
    @POST("upload")
    Call<Void> upload(@Part("image\"; filename=\"btn_image.jpg\" ") RequestBody file,
                      @Part("btn_name") String name,
                      @Part("btn_id")int btnId,
                      @Part("btn_text") String text);
}
