package com.example.companyfind;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.companyfind.model.Company;

public class CompanyDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_COMPANY = "extra_company";

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
        Company company = getIntent().getParcelableExtra(EXTRA_COMPANY);

        if (company != null) {
            companyNameValue.setText(company.getName() != null ? company.getName() : "Brak danych");
            nipValue.setText(company.getNip() != null ? company.getNip() : "Brak danych");
            regonValue.setText(company.getRegon() != null ? company.getRegon() : "Brak danych");
            addressValue.setText(company.getFormattedAddress() != null && !company.getFormattedAddress().trim().isEmpty()
                    ? company.getFormattedAddress() : "Brak danych");

            String status = "Aktywna";
            if (company.getStatus() != null) {
                status = company.getStatus().equals("1") ? "Aktywna" : "Nieaktywna";
            }
            statusValue.setText(status);

            // Ustaw tytuł aktywności
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Szczegóły: " +
                    (company.getName() != null ? company.getName() : "Firma"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            // Jeśli nie ma danych firmy, wyświetl błąd
            companyNameValue.setText("Błąd: Brak danych firmy");
            nipValue.setText("-");
            regonValue.setText("-");
            addressValue.setText("-");
            statusValue.setText("-");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
