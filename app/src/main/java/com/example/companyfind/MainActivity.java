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

import com.example.companyfind.gus.RetrofitClient;
import com.example.companyfind.gus.SessionStore;
import com.example.companyfind.gus.SidParser;
import com.example.companyfind.gus.SoapApi;
import com.example.companyfind.gus.SoapBodies;
import com.example.companyfind.gus.SoapRequest;
import com.example.companyfind.gus.SoapResponse;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
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
                    resultTextView.setText("Zalogowano pomyślnie. Wyszukiwanie firmy...");
                });

                // Teraz wyszukaj firmę po NIP
                ResponseBody searchBody = api.daneSzukajPodmioty(SoapBodies.daneSzukajPodmiotyEnvelope(nip))
                        .execute()
                        .body();

                String searchXml = searchBody != null ? searchBody.string() : "";

                runOnUiThread(() -> {
                    if (searchXml.contains("DaneSzukajPodmiotyResult")) {
                        // Parsuj wyniki i wyświetl dane firmy
                        displayCompanyInfo(searchXml, nip);
                    } else {
                        resultTextView.setText("Nie znaleziono firmy o podanym NIP: " + nip +
                                "\n\nOdpowiedź serwera:\n" + searchXml);
                    }
                    searchButton.setEnabled(true);
                });

            } catch (Exception ex) {
                runOnUiThread(() -> {
                    resultTextView.setText("Błąd: " + ex.getMessage());
                    searchButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void displayCompanyInfo(String xml, String nip) {
        // Prosta ekstrakcja danych z XML - można to ulepszyć używając parsera XML
        String result = "Wyniki wyszukiwania dla NIP: " + nip + "\n\n";

        if (xml.contains("<")) {
            // Sprawdź czy są wyniki
            if (xml.contains("DaneSzukajPodmiotyResult") && xml.length() > 200) {
                result += "Znaleziono dane firmy!\n";
                result += "Szczegóły w odpowiedzi XML:\n\n";
                result += xml.substring(0, Math.min(xml.length(), 500)) + "...";
            } else {
                result += "Brak danych dla podanego NIP.";
            }
        } else {
            result += "Otrzymano pustą odpowiedź z serwera.";
        }

        resultTextView.setText(result);
    }

}