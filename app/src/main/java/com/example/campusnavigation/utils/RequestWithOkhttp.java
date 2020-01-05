package com.example.campusnavigation.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RequestWithOkhttp {
    public static void sendokHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
