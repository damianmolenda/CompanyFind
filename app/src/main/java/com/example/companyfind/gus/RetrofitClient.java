package com.example.companyfind.gus;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/";

    private static final OkHttpClient http = new OkHttpClient.Builder()
            .addInterceptor(new SidInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public static Retrofit get() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                // we’ll parse login response manually; SimpleXML remains available for later typed calls
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .client(http)
                .build();
    }
}
