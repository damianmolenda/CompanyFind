package com.example.companyfind.gus;

// SidInterceptor.java
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

public class SidInterceptor implements Interceptor {
    @Override public Response intercept(Chain chain) throws IOException {
        String sid = SessionStore.getSid();
        String url = chain.request().url().toString();

        // Dodaj nagłówek sid tylko dla żądań wymagających uwierzytelnienia (nie dla zaloguj)
        if (sid != null && !sid.isEmpty() && !url.contains("Zaloguj")) {
            System.out.println("Adding SID header: " + sid + " to request: " + url);
            return chain.proceed(
                    chain.request().newBuilder()
                            .header("sid", sid)
                            .addHeader("Content-Type", "application/soap+xml; charset=utf-8")
                            .build()
            );
        }
        return chain.proceed(chain.request());
    }
}