package com.example.companyfind.gus;

// SidInterceptor.java
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

public class SidInterceptor implements Interceptor {
    @Override public Response intercept(Chain chain) throws IOException {
        String sid = SessionStore.getSid();
        if (sid != null && !sid.isEmpty()) {
            return chain.proceed(
                    chain.request().newBuilder()
                            .header("sid", sid)  // GUS expects SID as HTTP header for authenticated methods
                            .build()
            );
        }
        return chain.proceed(chain.request());
    }
}