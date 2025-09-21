package com.example.companyfind;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CompanyDetailsActivity extends AppCompatActivity {

    private TextView companyNameValue;
    private TextView nipValue;
    private TextView regonValue;
    private TextView addressValue;
    private TextView statusValue;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        initViews();
        setupClickListeners();
        displayCompanyData();
    }

    private void initViews() {
        companyNameValue = findViewById(R.id.companyNameValue);
        nipValue = findViewById(R.id.nipValue);
        regonValue = findViewById(R.id.regonValue);
        addressValue = findViewById(R.id.addressValue);
        statusValue = findViewById(R.id.statusValue);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    private void displayCompanyData() {
        // Pobierz dane z Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String companyName = extras.getString("company_name", "Brak danych");
            String nip = extras.getString("nip", "Brak danych");
            String regon = extras.getString("regon", "Brak danych");
            String address = extras.getString("address", "Brak danych");
            String status = extras.getString("status", "Brak danych");

            companyNameValue.setText(companyName);
            nipValue.setText(nip);
            regonValue.setText(regon);
            addressValue.setText(address);
            statusValue.setText(status);
        }
    }
}
