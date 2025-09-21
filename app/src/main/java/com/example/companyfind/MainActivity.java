package com.example.companyfind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.companyfind.gus.CompanyParser;
import com.example.companyfind.gus.RetrofitClient;
import com.example.companyfind.gus.SessionStore;
import com.example.companyfind.gus.SidParser;
import com.example.companyfind.gus.SoapApi;
import com.example.companyfind.gus.SoapBodies;
import com.example.companyfind.gus.SoapRequest;
import com.example.companyfind.gus.SoapResponse;
import com.example.companyfind.model.Company;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText nipEditText;
    private TextView resultTextView;
    private Button searchButton;
    private SoapApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        nipEditText = findViewById(R.id.nipEditText);
        resultTextView = findViewById(R.id.resultTextView);
        searchButton = findViewById(R.id.searchButton);
    }

    private void setupClickListeners() {
        searchButton.setOnClickListener(v -> searchCompany());
    }

    private void searchCompany() {
        String nip = nipEditText.getText().toString().trim();

        if (nip.isEmpty()) {
            resultTextView.setText("Proszę wprowadzić NIP");
            return;
        }

        if (nip.length() != 10) {
            resultTextView.setText("NIP musi mieć 10 cyfr");
            return;
        }

        searchButton.setEnabled(false);
        resultTextView.setText("Logowanie do systemu GUS...");

        // Wykonaj operację w tle
        new Thread(() -> {
            try {
                String apiKey = "abcde12345abcde12345"; // Usuń spację na początku i użyj prawdziwego klucza
                api = RetrofitClient.get().create(SoapApi.class);

                // Najpierw zaloguj się
                ResponseBody loginBody = api.zaloguj(SoapBodies.zalogujEnvelope(apiKey))
                        .execute()
                        .body();

                String loginXml = loginBody != null ? loginBody.string() : "";
                String sid = SidParser.extract(loginXml);

                if (sid == null || sid.isEmpty()) {
                    runOnUiThread(() -> {
                        resultTextView.setText("Błąd logowania. Sprawdź klucz API.\n\nOdpowiedź: " + loginXml);
                        searchButton.setEnabled(true);
                    });
                    return;
                }

                SessionStore.setSid(sid);

                runOnUiThread(() -> {
                    resultTextView.setText("Zalogowano pomyślnie. SID: " + sid + "\nWyszukiwanie firmy...");
                });

                // Dodatkowe sprawdzenie - czy SID jest prawidłowo zapisany
                String storedSid = SessionStore.getSid();
                System.out.println("Stored SID after login: " + storedSid);
                System.out.println("Original SID: " + sid);
                System.out.println("SIDs match: " + sid.equals(storedSid));

                // Teraz wyszukaj firmę po NIP - przekaż SID jako nagłówek
                System.out.println("Sending search request with SID: " + sid);
                System.out.println("SessionStore SID: " + SessionStore.getSid());

                // Utwórz SOAP envelope i wyloguj jego zawartość
                RequestBody soapEnvelope = SoapBodies.daneSzukajPodmiotyEnvelope(nip);
                System.out.println("SOAP Envelope created successfully");

                // Dodaj szczegółowe logowanie żądania
                System.out.println("=== SENDING SOAP REQUEST ===");
                System.out.println("NIP: " + nip);
                System.out.println("SID Header: " + sid);

                // Wyślij żądanie HTTP z prawidłowymi nagłówkami
                okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
                okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                        .url("https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc")
                        .post(soapEnvelope)
                        .addHeader("Content-Type", "application/soap+xml; charset=utf-8")
                        .addHeader("SOAPAction", "\"http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/DaneSzukajPodmioty\"")
                        .addHeader("sid", sid)
                        .addHeader("User-Agent", "CompanyFind/1.0")
                        .addHeader("Accept", "*/*")
                        .build();

                System.out.println("HTTP Headers:");
                System.out.println("Content-Type: application/soap+xml; charset=utf-8");
                System.out.println("SOAPAction: \"http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/DaneSzukajPodmioty\"");
                System.out.println("sid: " + sid);

                okhttp3.Response httpResponse = httpClient.newCall(httpRequest).execute();

                System.out.println("=== RECEIVED RESPONSE ===");

                String searchXml = "";
                String errorDetails = "";

                if (httpResponse.isSuccessful() && httpResponse.body() != null) {
                    searchXml = httpResponse.body().string();
                } else {
                    errorDetails = "HTTP Error: " + httpResponse.code();
                    System.out.println("HTTP Error: " + httpResponse.code());
                    if (httpResponse.body() != null) {
                        String errorBody = httpResponse.body().string();
                        errorDetails += "\nError body: " + errorBody;
                        System.out.println("Error body: " + errorBody);
                    }
                }

                // Logowanie diagnostyczne
                System.out.println("HTTP Response code: " + httpResponse.code());
                System.out.println("Response Headers: " + httpResponse.headers());
                System.out.println("SID used in request: " + sid);
                System.out.println("Search response length: " + searchXml.length());
                System.out.println("=== FULL SEARCH RESPONSE ===");
                System.out.println(searchXml);
                System.out.println("=== END SEARCH RESPONSE ===");

                httpResponse.close();

                // Utwórz final zmienną dla lambda expression
                final String finalSearchXml = searchXml;
                final String finalErrorDetails = errorDetails;
                final int finalResponseCode = httpResponse.code();

                runOnUiThread(() -> {
                    try {
                        if (!finalSearchXml.isEmpty()) {
                            System.out.println("=== PARSING COMPANY DATA ===");
                            Company company = CompanyParser.parseFirstCompany(finalSearchXml);

                            if (company != null && company.getNip() != null) {
                                System.out.println("Successfully parsed company:");
                                System.out.println("Name: " + company.getName());
                                System.out.println("NIP: " + company.getNip());
                                System.out.println("REGON: " + company.getRegon());
                                System.out.println("Address: " + company.getFormattedAddress());
                                System.out.println("Status: " + company.getStatus());

                                // Znaleziono firmę - przejdź do szczegółów
                                Intent intent = new Intent(MainActivity.this, CompanyDetailsActivity.class);
                                intent.putExtra(CompanyDetailsActivity.EXTRA_COMPANY, company);
                                startActivity(intent);

                                resultTextView.setText("Znaleziono firmę: " + company.getName());
                            } else {
                                System.out.println("No company data parsed or missing NIP");
                                resultTextView.setText("Nie znaleziono firmy o podanym NIP: " + nip +
                                        "\n\nSprawdź czy NIP jest poprawny lub czy firma jest zarejestrowana w systemie GUS." +
                                        "\n\nKod HTTP: " + finalResponseCode +
                                        "\n\nDługość odpowiedzi: " + finalSearchXml.length());
                            }
                        } else {
                            resultTextView.setText("Błąd wyszukiwania firmy o NIP: " + nip +
                                    "\n\n" + finalErrorDetails +
                                    "\n\nKod HTTP: " + finalResponseCode +
                                    "\n\nUpewnij się, że:\n- NIP jest poprawny\n- Masz prawidłowy klucz API\n- SID jest prawidłowo ustawiony");
                        }
                    } catch (Exception parseEx) {
                        System.out.println("Exception during parsing: " + parseEx.getMessage());
                        parseEx.printStackTrace();
                        resultTextView.setText("Błąd parsowania odpowiedzi: " + parseEx.getMessage() +
                                "\n\nSurowa odpowiedź:\n" + finalSearchXml.substring(0, Math.min(500, finalSearchXml.length())));
                    }

                    searchButton.setEnabled(true);
                });

            } catch (Exception ex) {
                runOnUiThread(() -> {
                    resultTextView.setText("Błąd połączenia: " + ex.getMessage());
                    searchButton.setEnabled(true);
                });
            }
        }).start();
    }


}