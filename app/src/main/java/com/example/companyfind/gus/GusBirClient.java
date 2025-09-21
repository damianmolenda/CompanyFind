package com.example.companyfind.gus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Klient do komunikacji z serwisem GUS BIR1
 * Implementuje rzeczywistą integrację z WSDL używając OkHttp
 */
public class GusBirClient {

    private static final String TAG = "GusBirClient";

    // URLs dla serwisu testowego GUS
    private static final String SOAP_URL = "https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc";
    private static final String NAMESPACE = "http://CIS/BIR/PUBL/2014/07";
    private static final String SOAP_ACTION_BASE = "http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/";

    // Klucz użytkownika testowego
    private static final String USER_KEY = "abcde12345abcde1234";

    private String sessionId = null;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final OkHttpClient httpClient = new OkHttpClient();

    public interface GusBirCallback {
        void onSuccess(List<GusCompanyData> companies);
        void onError(String errorMessage);
    }

    /**
     * Wyszukuje firmy po NIP
     */
    public void searchByNip(String nip, GusBirCallback callback) {
        executor.execute(() -> {
            try {
                // 1. Zaloguj się do serwisu
                if (!login()) {
                    mainHandler.post(() -> callback.onError("Błąd logowania do serwisu GUS"));
                    return;
                }

                // 2. Wyszukaj firmy
                List<GusCompanyData> companies = searchCompanies("nip", nip);

                // 3. Pobierz szczegółowe dane dla każdej firmy
                if (companies != null && !companies.isEmpty()) {
                    for (GusCompanyData company : companies) {
                        enrichCompanyData(company);
                    }
                }

                // 4. Wyloguj się
                logout();

                // 5. Zwróć wynik
                final List<GusCompanyData> finalCompanies = companies;
                mainHandler.post(() -> {
                    if (finalCompanies != null && !finalCompanies.isEmpty()) {
                        callback.onSuccess(finalCompanies);
                    } else {
                        callback.onError("Nie znaleziono firm");
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Błąd podczas wyszukiwania", e);
                mainHandler.post(() -> callback.onError("Błąd podczas wyszukiwania: " + e.getMessage()));
            }
        });
    }

    /**
     * Logowanie do serwisu GUS
     */
    private boolean login() {
        try {
            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                    "xmlns:ns=\"" + NAMESPACE + "\">" +
                    "<soap:Header/>" +
                    "<soap:Body>" +
                    "<ns:Zaloguj>" +
                    "<ns:pKluczUzytkownika>" + USER_KEY + "</ns:pKluczUzytkownika>" +
                    "</ns:Zaloguj>" +
                    "</soap:Body>" +
                    "</soap:Envelope>";

            String response = sendSoapRequest(soapBody, "Zaloguj");
            if (response != null) {
                sessionId = extractValueFromSoapResponse(response, "ZalogujResult");
                Log.d(TAG, "Zalogowano, SessionID: " + sessionId);
                return sessionId != null && !sessionId.trim().isEmpty();
            }
            return false;

        } catch (Exception e) {
            Log.e(TAG, "Błąd logowania", e);
            return false;
        }
    }

    /**
     * Wylogowanie z serwisu GUS
     */
    private void logout() {
        try {
            if (sessionId == null) return;

            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                    "xmlns:ns=\"" + NAMESPACE + "\">" +
                    "<soap:Header/>" +
                    "<soap:Body>" +
                    "<ns:Wyloguj>" +
                    "<ns:pIdentyfikatorSesji>" + sessionId + "</ns:pIdentyfikatorSesji>" +
                    "</ns:Wyloguj>" +
                    "</soap:Body>" +
                    "</soap:Envelope>";

            sendSoapRequest(soapBody, "Wyloguj");
            Log.d(TAG, "Wylogowano z sesji: " + sessionId);
            sessionId = null;

        } catch (Exception e) {
            Log.e(TAG, "Błąd wylogowania", e);
        }
    }

    /**
     * Wyszukuje firmy w systemie GUS
     */
    private List<GusCompanyData> searchCompanies(String searchType, String searchValue) {
        try {
            // Przygotuj parametry wyszukiwania
            String searchParams = "";
            if ("nip".equals(searchType)) {
                searchParams = "<root><dane><Nip>" + searchValue + "</Nip></dane></root>";
            } else if ("regon".equals(searchType)) {
                searchParams = "<root><dane><Regon>" + searchValue + "</Regon></dane></root>";
            }

            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                    "xmlns:ns=\"" + NAMESPACE + "\">" +
                    "<soap:Header/>" +
                    "<soap:Body>" +
                    "<ns:DaneSzukaj>" +
                    "<ns:pParametryWyszukiwania><![CDATA[" + searchParams + "]]></ns:pParametryWyszukiwania>" +
                    "<ns:pIdentyfikatorSesji>" + sessionId + "</ns:pIdentyfikatorSesji>" +
                    "</ns:DaneSzukaj>" +
                    "</soap:Body>" +
                    "</soap:Envelope>";

            String response = sendSoapRequest(soapBody, "DaneSzukaj");
            if (response != null) {
                String xmlResponse = extractValueFromSoapResponse(response, "DaneSzukajResult");
                Log.d(TAG, "Odpowiedź wyszukiwania: " + xmlResponse);
                return parseSearchResponse(xmlResponse);
            }
            return null;

        } catch (Exception e) {
            Log.e(TAG, "Błąd wyszukiwania", e);
            return null;
        }
    }

    /**
     * Pobiera szczegółowe dane firmy
     */
    private void enrichCompanyData(GusCompanyData company) {
        try {
            String regon = company.getRegon();
            if (regon == null || regon.trim().isEmpty()) return;

            // Określ nazwę raportu na podstawie długości REGON
            String reportName;
            if (regon.length() == 9) {
                reportName = "PublDaneRaportPrawna";
            } else {
                reportName = "PublDaneRaportDzialalnoscFizycznejCeidg";
            }

            String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                    "xmlns:ns=\"" + NAMESPACE + "\">" +
                    "<soap:Header/>" +
                    "<soap:Body>" +
                    "<ns:DanePobierzPelnyRaport>" +
                    "<ns:pRegon>" + regon + "</ns:pRegon>" +
                    "<ns:pNazwaRaportu>" + reportName + "</ns:pNazwaRaportu>" +
                    "<ns:pIdentyfikatorSesji>" + sessionId + "</ns:pIdentyfikatorSesji>" +
                    "</ns:DanePobierzPelnyRaport>" +
                    "</soap:Body>" +
                    "</soap:Envelope>";

            String response = sendSoapRequest(soapBody, "DanePobierzPelnyRaport");
            if (response != null) {
                String xmlResponse = extractValueFromSoapResponse(response, "DanePobierzPelnyRaportResult");
                parseDetailedResponse(xmlResponse, company);
            }

        } catch (Exception e) {
            Log.e(TAG, "Błąd pobierania szczegółów dla REGON: " + company.getRegon(), e);
        }
    }

    /**
     * Wysyła żądanie SOAP do serwisu
     */
    private String sendSoapRequest(String soapBody, String action) throws IOException {
        // Serwis GUS wymaga application/soap+xml zamiast text/xml
        MediaType mediaType = MediaType.parse("application/soap+xml; charset=utf-8");
        RequestBody requestBody = RequestBody.create(soapBody, mediaType);

        Request request = new Request.Builder()
                .url(SOAP_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/soap+xml; charset=utf-8")
                .addHeader("SOAPAction", "\"" + SOAP_ACTION_BASE + action + "\"")
                .addHeader("Accept", "application/soap+xml, text/xml")
                .addHeader("User-Agent", "CompanyFind/1.0")
                .build();

        Log.d(TAG, "Wysyłanie żądania SOAP: " + action);
        Log.d(TAG, "URL: " + SOAP_URL);
        Log.d(TAG, "SOAPAction: " + SOAP_ACTION_BASE + action);
        Log.d(TAG, "Content-Type: application/soap+xml; charset=utf-8");

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = null;
            if (response.body() != null) {
                responseBody = response.body().string();
            }

            Log.d(TAG, "Kod odpowiedzi: " + response.code());
            Log.d(TAG, "Odpowiedź: " + (responseBody != null ? responseBody.substring(0, Math.min(500, responseBody.length())) : "null"));

            if (response.isSuccessful() && responseBody != null) {
                return responseBody;
            } else {
                Log.e(TAG, "Błąd HTTP: " + response.code() + " - " + response.message());
                if (responseBody != null) {
                    Log.e(TAG, "Treść błędu: " + responseBody);
                }
                return null;
            }
        }
    }

    /**
     * Wyciąga wartość z odpowiedzi SOAP
     */
    private String extractValueFromSoapResponse(String soapResponse, String elementName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(soapResponse)));

            NodeList nodes = doc.getElementsByTagName(elementName);
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
        } catch (Exception e) {
            Log.e(TAG, "Błąd parsowania odpowiedzi SOAP", e);
        }
        return null;
    }

    /**
     * Parsuje odpowiedź z wyszukiwania firm
     */
    private List<GusCompanyData> parseSearchResponse(String xmlResponse) {
        List<GusCompanyData> companies = new ArrayList<>();

        try {
            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                return companies;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            NodeList daneNodes = doc.getElementsByTagName("dane");

            for (int i = 0; i < daneNodes.getLength(); i++) {
                Element daneElement = (Element) daneNodes.item(i);
                GusCompanyData company = new GusCompanyData();

                company.setRegon(getElementValue(daneElement, "Regon"));
                company.setNip(getElementValue(daneElement, "Nip"));
                company.setNazwa(getElementValue(daneElement, "Nazwa"));
                company.setWojewodztwo(getElementValue(daneElement, "Wojewodztwo"));
                company.setPowiat(getElementValue(daneElement, "Powiat"));
                company.setGmina(getElementValue(daneElement, "Gmina"));
                company.setMiejscowosc(getElementValue(daneElement, "Miejscowosc"));
                company.setKodPocztowy(getElementValue(daneElement, "KodPocztowy"));
                company.setUlica(getElementValue(daneElement, "Ulica"));
                company.setNrNieruchomosci(getElementValue(daneElement, "NrNieruchomosci"));
                company.setNrLokalu(getElementValue(daneElement, "NrLokalu"));
                company.setTyp(getElementValue(daneElement, "Typ"));
                company.setSilosID(getElementValue(daneElement, "SilosID"));
                company.setDataZakonczeniaDzialalnosci(getElementValue(daneElement, "DataZakonczeniaDzialalnosci"));

                companies.add(company);
            }

        } catch (Exception e) {
            Log.e(TAG, "Błąd parsowania odpowiedzi wyszukiwania", e);
        }

        return companies;
    }

    /**
     * Parsuje szczegółową odpowiedź o firmie
     */
    private void parseDetailedResponse(String xmlResponse, GusCompanyData company) {
        try {
            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            NodeList daneNodes = doc.getElementsByTagName("dane");

            if (daneNodes.getLength() > 0) {
                Element daneElement = (Element) daneNodes.item(0);

                // Aktualizuj dane firmy o dodatkowe informacje
                String status = getElementValue(daneElement, "StatusNip");
                if (status == null || status.trim().isEmpty()) {
                    status = getElementValue(daneElement, "SilosEtykieta");
                }
                company.setStatus(status);
            }

        } catch (Exception e) {
            Log.e(TAG, "Błąd parsowania szczegółowej odpowiedzi", e);
        }
    }

    /**
     * Pomocnicza metoda do pobierania wartości elementu XML
     */
    private String getElementValue(Element parent, String tagName) {
        try {
            NodeList nodeList = parent.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                String value = nodeList.item(0).getTextContent();
                return value != null ? value.trim() : "";
            }
        } catch (Exception e) {
            Log.w(TAG, "Nie można pobrać wartości dla: " + tagName);
        }
        return "";
    }
}
