package com.example.companyfind.gus;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/";

    public static Retrofit get() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .client(GusHttpClient.createGusClient())
                .build();
    }
}
