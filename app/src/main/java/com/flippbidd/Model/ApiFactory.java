package com.flippbidd.Model;

import android.content.Context;
import com.flippbidd.BuildConfig;
import com.flippbidd.baseclass.BaseApplication;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {
    //v6 live
    //v6 Development
    private static final String BASE_URL = BuildConfig.BASE_URL + "v8/";
    private Context context;
    private ApiFactory(Context context) {
        this.context = context;
    }
    public static ApiFactory getInstance(Context context) {
        return new ApiFactory(context);
    }
    private Retrofit provideRestAdapter() {


        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(OkClientFactory.provideOkHttpClient(context, BuildConfig.DEBUG))
                .client(BaseApplication.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private Retrofit provideRestAdapterEncryptDecrypt() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(new EncryptDecryptConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create())))
                .client(BaseApplication.getClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }

    public <S> S provideService(Class<S> serviceClass) {
        return provideRestAdapter().create(serviceClass);
    }

    public <S> S provideEncryptDecryptService(Class<S> serviceClass) {
        return provideRestAdapterEncryptDecrypt().create(serviceClass);
    }


}