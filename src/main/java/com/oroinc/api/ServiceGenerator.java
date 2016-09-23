package com.oroinc.api;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Base64;
import java.security.MessageDigest;

public class ServiceGenerator {

    protected static String API_SCHEMA = "http";
    protected static String API_HOST = "orocrm.loc";
    public static final String API_BASE_URL = API_SCHEMA + "://" + API_HOST + "/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    protected static String getApiUser()
    {
        // OroApplication api user
        return "admin";
    }

    protected static String getApiKey()
    {
        // OroApplication api user secret
        return "552d0efc11cc2b881a603e61a862964915b63bd7";
    }

    public static <S> S createService(Class<S> serviceClass) {

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {

                try {
                    String user = getApiUser();
                    String key = getApiKey();

                    Date date = new Date();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    String iso8601datetime = df.format(date);

                    String nonce = (new HexBinaryAdapter()).marshal(MessageDigest.getInstance("MD5").digest(String.valueOf(date.getTime()).getBytes())).toLowerCase().substring(0, 16);
                    byte[] sha1 = MessageDigest.getInstance("SHA1").digest((nonce + iso8601datetime + key).getBytes());
                    String digest = Base64.getEncoder().encodeToString(sha1);
                    String nonceBase64 = Base64.getEncoder().encodeToString(nonce.getBytes());

                    Request original = chain.request();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .addHeader("Authorization", "WSSE profile=\"UsernameToken\"")
                            .addHeader("X-WSSE", "UsernameToken Username=\"" + user
                                            + "\", PasswordDigest=\"" + digest
                                            + "\", Nonce=\"" + nonceBase64
                                            + "\", Created=\"" + iso8601datetime + "\"");

                    Request request = requestBuilder.build();

                    // Debug info
                    System.out.println("DEBUG AUTH: User = " + user);
                    System.out.println("DEBUG AUTH: Key = " + key);
                    System.out.println("DEBUG AUTH: Created = " + iso8601datetime);
                    System.out.println("DEBUG AUTH: Nonce = " + nonce);
                    System.out.println("DEBUG AUTH: NonceBase64 = " + nonceBase64);
                    System.out.println("DEBUG AUTH: Digest = " + digest);
                    System.out.println("DEBUG AUTH: Headers = " + request.headers());

                    return chain.proceed(request);

                } catch (Exception e) {
                    System.out.println("ERROR:" + e.toString());
                }

                return null;
            }
        });

        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}