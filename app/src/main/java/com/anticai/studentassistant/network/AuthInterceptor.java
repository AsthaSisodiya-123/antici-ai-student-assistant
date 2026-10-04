package com.anticai.studentassistant.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        "AnticiPrefs",
                        Context.MODE_PRIVATE
                );

        String token = prefs.getString("jwt_token", null);

        Request originalRequest = chain.request();

        Request.Builder requestBuilder =
                originalRequest.newBuilder();

        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader(
                    "Authorization",
                    "Bearer " + token
            );
        }

        return chain.proceed(requestBuilder.build());
    }
}

