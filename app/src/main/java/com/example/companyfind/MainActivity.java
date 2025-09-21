package com.example.companyfind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText nipEditText;
    private Button searchButton;

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
        searchButton = findViewById(R.id.searchButton);
    }

    private void setupClickListeners() {
        searchButton.setOnClickListener(v -> searchCompany());
    }

    private void searchCompany() {
        String nip = nipEditText.getText().toString().trim();

        // Walidacja NIP
        if (!NipValidator.isValidNip(nip)) {
            Toast.makeText(this, getString(R.string.error_invalid_nip), Toast.LENGTH_SHORT).show();
            return;
        }

        // Wyłącz przycisk i pokaż loading
        searchButton.setEnabled(false);
        searchButton.setText(getString(R.string.loading));

        // Wywołaj integrację z GUS
        GusClient.searchCompanyByNip(nip, new GusClient.GusClientCallback() {
            @Override
            public void onSuccess(GusClient.CompanyData companyData) {
                // Przywróć przycisk
                searchButton.setEnabled(true);
                searchButton.setText(getString(R.string.search_button_text));

                // Otwórz nową aktywność z danymi firmy
                openCompanyDetailsActivity(companyData);
            }

            @Override
            public void onError(String errorMessage) {
                // Przywróć przycisk
                searchButton.setEnabled(true);
                searchButton.setText(getString(R.string.search_button_text));

                // Pokaż błąd
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openCompanyDetailsActivity(GusClient.CompanyData companyData) {
        Intent intent = new Intent(this, CompanyDetailsActivity.class);
        intent.putExtra("company_name", companyData.getCompanyName());
        intent.putExtra("nip", companyData.getNip());
        intent.putExtra("regon", companyData.getRegon());
        intent.putExtra("address", companyData.getAddress());
        intent.putExtra("status", companyData.getStatus());
        startActivity(intent);
    }
}