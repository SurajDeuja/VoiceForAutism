package com.bsu.android.acd.rpc;

import com.bsu.android.acd.rpc.RpcRequest;
import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by surajdeuja on 3/24/16.
 */
public class RpcClient {
    private final String TAG = "RpcClient";
    private OkHttpClient mOkhttpClient;
    private Gson mGson;
    private String mUri;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Inject
    public  RpcClient(OkHttpClient client, Gson gson) {
        this.mOkhttpClient = client;
        this.mGson = gson;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public void sendRequest(RpcRequest rpcObject, RpcCallback callback) {
        String json = mGson.toJson(rpcObject);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().
                url(mUri)
                .addHeader("Content-Type","application/json")
                .post(requestBody)
                .build();

        Call call = mOkhttpClient.newCall(request);
        rpcCallback = callback;
        call.enqueue(this.callback);
    }

    private RpcCallback rpcCallback;
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            RpcResponse rpcResponse = mGson.fromJson(response.body().string(), RpcResponse.class);
            rpcCallback.onResponse(rpcResponse);
        }
    };
}
