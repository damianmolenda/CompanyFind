package com.example.companyfind.gus;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Klasa zarządzająca klientem HTTP dla API GUS
 * Automatycznie dodaje nagłówek "sid" z wartością sessionId zgodnie z dokumentacją API GUS
 */
public class GusHttpClient {

    /**
     * Interceptor który dodaje nagłówek "sid" z wartością sessionId do wszystkich żądań (poza logowaniem)
     * zgodnie z wymaganiami API GUS
     */
    public static class GusAuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String url = originalRequest.url().toString();

            // Nie dodawaj nagłówka sid do żądań logowania
            if (url.contains("Zaloguj")) {
                System.out.println("GUS Login request - no SID header needed");
                return chain.proceed(originalRequest);
            }

            // Pobierz sessionId z SessionStore
            String sessionId = SessionStore.getSid();

            if (sessionId != null && !sessionId.isEmpty()) {
                // Dodaj nagłówek "sid" z wartością sessionId zgodnie z dokumentacją API GUS
                Request authenticatedRequest = originalRequest.newBuilder()
                        .addHeader("sid", sessionId)
                        .build();

                System.out.println("GUS API: Adding 'sid' header with sessionId: " + sessionId);
                System.out.println("GUS API: Request URL: " + url);

                return chain.proceed(authenticatedRequest);
            } else {
                System.out.println("GUS API: No sessionId available - proceeding without sid header");
                return chain.proceed(originalRequest);
            }
        }
    }

    /**
     * Tworzy skonfigurowany OkHttpClient dla API GUS z odpowiednimi interceptorami
     */
    public static OkHttpClient createGusClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new GusAuthInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }
}
