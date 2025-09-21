package com.example.companyfind;

import android.util.Log;

import com.example.companyfind.gus.GusBirClient;
import com.example.companyfind.gus.GusCompanyData;

import java.util.List;

public class GusClient {

    private static final String TAG = "GusClient";

    public interface GusClientCallback {
        void onSuccess(CompanyData companyData);
        void onError(String errorMessage);
    }

    public static class CompanyData {
        private final String companyName;
        private final String nip;
        private final String regon;
        private final String address;
        private final String status;

        public CompanyData(String companyName, String nip, String regon, String address, String status) {
            this.companyName = companyName;
            this.nip = nip;
            this.regon = regon;
            this.address = address;
            this.status = status;
        }

        // Gettery
        public String getCompanyName() { return companyName; }
        public String getNip() { return nip; }
        public String getRegon() { return regon; }
        public String getAddress() { return address; }
        public String getStatus() { return status; }
    }

    /**
     * Wyszukuje dane firmy na podstawie NIP używając rzeczywistej integracji z GUS
     */
    public static void searchCompanyByNip(String nip, GusClientCallback callback) {
        Log.d(TAG, "Rozpoczynam wyszukiwanie firmy dla NIP: " + nip);

        GusBirClient gusBirClient = new GusBirClient();

        gusBirClient.searchByNip(nip, new GusBirClient.GusBirCallback() {
            @Override
            public void onSuccess(List<GusCompanyData> companies) {
                if (companies != null && !companies.isEmpty()) {
                    // Bierz pierwszą znalezioną firmę
                    GusCompanyData gusData = companies.get(0);

                    // Konwertuj dane z GUS na format używany przez aplikację
                    CompanyData companyData = convertGusDataToCompanyData(gusData);

                    Log.d(TAG, "Znaleziono firmę: " + companyData.getCompanyName());
                    callback.onSuccess(companyData);
                } else {
                    Log.w(TAG, "Nie znaleziono firm dla NIP: " + nip);
                    callback.onError("Nie znaleziono firmy o podanym numerze NIP");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Błąd wyszukiwania: " + errorMessage);
                callback.onError("Błąd podczas wyszukiwania w systemie GUS: " + errorMessage);
            }
        });
    }

    /**
     * Konwertuje dane z systemu GUS na format używany przez aplikację
     */
    private static CompanyData convertGusDataToCompanyData(GusCompanyData gusData) {
        String companyName = gusData.getNazwa();
        if (companyName == null || companyName.trim().isEmpty()) {
            companyName = "Brak nazwy";
        }

        String nip = gusData.getNip();
        if (nip == null || nip.trim().isEmpty()) {
            nip = "Brak danych";
        }

        String regon = gusData.getRegon();
        if (regon == null || regon.trim().isEmpty()) {
            regon = "Brak danych";
        }

        String address = gusData.getPelnyAdres();
        if (address == null || address.trim().isEmpty()) {
            address = "Brak adresu";
        }

        String status = gusData.getStatus();
        if (status == null || status.trim().isEmpty()) {
            // Jeśli brak daty zakończenia działalności, uznaj za aktywną
            String dataZakonczenia = gusData.getDataZakonczeniaDzialalnosci();
            if (dataZakonczenia == null || dataZakonczenia.trim().isEmpty()) {
                status = "Aktywna";
            } else {
                status = "Zakończona działalność";
            }
        }

        return new CompanyData(companyName, nip, regon, address, status);
    }
}
