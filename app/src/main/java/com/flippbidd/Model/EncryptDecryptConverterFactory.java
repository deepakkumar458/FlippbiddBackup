package com.flippbidd.Model;

import com.flippbidd.Others.LogUtils;
import com.google.gson.Gson;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EncryptDecryptConverterFactory extends Converter.Factory {

    private GsonConverterFactory factory;

    public EncryptDecryptConverterFactory(GsonConverterFactory factory) {
        this.factory = factory;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(final Type type, Annotation[] annotations, Retrofit retrofit) {
        Converter<ResponseBody, ?> delegate = factory.responseBodyConverter(type, annotations, retrofit);
        return new DecryptConverter<>(delegate, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        Converter<?, RequestBody> delegate = factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        return new EncryptConverter<>(delegate, type);
    }

    final static class DecryptConverter<T> implements Converter<ResponseBody, T> {
        final Converter<ResponseBody, T> delegate;
        final Type type;

        public DecryptConverter(Converter<ResponseBody, T> delegate, Type type) {
            this.delegate = delegate;
            this.type = type;
        }

        @Override
        public T convert(ResponseBody responseBody) throws IOException {

            String input = responseBody.string();

            // for example it is from server
            // String encryptedText = AES.getInstance().doEncrypt(input);
            // Log.e("Converter", "Ex: Server Response in encrypted form: " + encryptedText);

            // decrypt encrypted text

            String decryptedText = AES.getInstance().doDecrypt(input);
            /*String decryptedText = null;
            try {
                ApiCrypter apiCrypter = new ApiCrypter();
                String res = new String(ApiCrypter.getInstance().decrypt(input), "UTF-8" );
                decryptedText = URLDecoder.decode(res,"UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
            }*/


            return new Gson().fromJson(decryptedText, type);
            //return delegate.convert(responseBody);
        }
    }

    final static class EncryptConverter<T> implements Converter<T, RequestBody> {
        final Converter<T, RequestBody> delegate;
        final Type type;

        public EncryptConverter(Converter<T, RequestBody> delegate, Type type) {
            this.delegate = delegate;
            this.type = type;
        }

        @Override
        public RequestBody convert(T value) throws IOException {

            String input = new Gson().toJson(value, type);
            LogUtils.LOGD("Converter", "#Encrypt input: " + input);

            // for example it is from server
            String encryptedText = AES.getInstance().doEncrypt(input);

           /* String encryptedText = null;
            try {
                encryptedText = ApiCrypter.bytesToHex(ApiCrypter.getInstance().encrypt(input));
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            LogUtils.LOGD("Converter", "Ex: Server Request in encrypted form: " + encryptedText);

            // decrypt encrypted text
            // String decryptedText = AES.getInstance().doDecrypt(encryptedText);
            // Log.e("Converter", "Ex: Server Request in decrypted form: " + decryptedText);

            //return delegate.convert(value);
            return RequestBody.create(okhttp3.MediaType.parse("text/plain"), encryptedText);
        }
    }
}