package com.bsu.android.acd.rpc;

/**
 * Created by surajdeuja on 3/25/16.
 */
public class RpcResponse {
    private String jsonrpc;
    private String result;
    private String id;
    private RpcError error;

    public String getResult() {
        return result;
    }

    public String getId() {
        return id;
    }

    public RpcError getError() {
        return error;
    }

    private boolean isStatusOk() {
        return null == error;
    }

}
