package com.bsu.android.acd.pojo;

import android.util.*;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by surajdeuja on 4/16/16.
 */
public interface LogData {
    @GET("log")
    Call<List<ButtonAction>> getLog();
}
