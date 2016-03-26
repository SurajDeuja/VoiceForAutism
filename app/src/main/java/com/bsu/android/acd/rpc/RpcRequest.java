package com.bsu.android.acd.rpc;

/**
 * Created by surajdeuja on 3/24/16.
 */
public class RpcRequest {
    public String method;
    public String jsonrpc;
    public int id;
    public Object params;

    public RpcRequest(String method, String jsonrpc, int id, Object params) {
        this.method = method;
        this.jsonrpc = jsonrpc;
        this.id = id;
        this.params = params;
    }

    public static RpcRequestBuilder builder() {
        return new RpcRequestBuilder();
    }

    public static class RpcRequestBuilder {
        private String method;
        private String jsonrpc = "2.0";
        private int id = 1;
        private Object params;

        public RpcRequestBuilder method(String method) {
            this.method = method;
            return this;
        }

        public RpcRequestBuilder params(Object params) {
            this.params = params;
            return this;
        }

        public RpcRequest build() {
            return new RpcRequest(method, jsonrpc, id, params);
        }

    }


}
