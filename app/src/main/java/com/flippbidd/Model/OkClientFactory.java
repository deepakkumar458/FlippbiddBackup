package com.flippbidd.Model;

import android.content.Context;
import androidx.annotation.NonNull;

import com.flippbidd.Others.UserPreference;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkClientFactory {

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    private OkClientFactory() {
    }

    @NonNull
    public static OkHttpClient provideOkHttpClient(final Context context, boolean debug) {
        // Install an HTTP cache in the context cache directory.
        File cacheDir = new File(context.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);


        OkHttpClient.Builder builder = new OkHttpClient.Builder().cache(cache);
        builder.connectTimeout(5, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES);



        if (debug) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        if (UserPreference.getInstance(context).isUserLogin()) {
            builder.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    //.header("Authorization", "Bearer " + Constants.token)
                    Request newRequest = chain.request().newBuilder()
                            .build();
                    return chain.proceed(newRequest);
                }
            });
        }
        return builder.build();
    }

    static OkHttpClient provideOkHttpClient(Context context) {
        return provideOkHttpClient(context, false);
    }
}