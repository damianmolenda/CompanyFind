package com.example.companyfind.gus;

// SoapBodies.java
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SoapBodies {
    // SOAP 1.2 envelope with WS-Addressing Action + To
    public static RequestBody zalogujEnvelope(String apiKey) {
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "               xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" " +
                        "               xmlns:ns=\"http://CIS/BIR/PUBL/2014/07\">" +
                        "  <soap:Header>" +
                        "    <wsa:Action>http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/Zaloguj</wsa:Action>" +
                        "    <wsa:To>https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc</wsa:To>" +
                        "  </soap:Header>" +
                        "  <soap:Body>" +
                        "    <ns:Zaloguj>" +
                        "      <ns:pKluczUzytkownika>" + apiKey + "</ns:pKluczUzytkownika>" +
                        "    </ns:Zaloguj>" +
                        "  </soap:Body>" +
                        "</soap:Envelope>";

        return RequestBody.create(
                xml, MediaType.parse("application/soap+xml; charset=utf-8")
        );
    }

    public static RequestBody daneSzukajPodmiotyEnvelope(String nip) {
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "               xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" " +
                        "               xmlns:dat=\"http://CIS/BIR/PUBL/2014/07/DataContract\" " +
                        "               xmlns:ns=\"http://CIS/BIR/PUBL/2014/07\">" +
                        "  <soap:Header>" +
                        "    <wsa:Action>http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/DaneSzukajPodmioty</wsa:Action>" +
                        "    <wsa:To>https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc</wsa:To>" +
                        "  </soap:Header>" +
                        "  <soap:Body>" +
                        "    <ns:DaneSzukajPodmioty>" +
                        "      <ns:pParametryWyszukiwania>" +
                        "          <dat:Nip>" + nip + "</dat:Nip>" +
                        "      </ns:pParametryWyszukiwania>" +
                        "    </ns:DaneSzukajPodmioty>" +
                        "  </soap:Body>" +
                        "</soap:Envelope>";

        return RequestBody.create(
                xml, MediaType.parse("application/soap+xml; charset=utf-8")
        );
    }

    // Wyszukiwanie po KRS
    public static RequestBody daneSzukajPodmiotyByKrsEnvelope(String krs) {
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "               xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" " +
                        "               xmlns:ns=\"http://CIS/BIR/PUBL/2014/07\">" +
                        "  <soap:Header>" +
                        "    <wsa:Action>http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/DaneSzukajPodmioty</wsa:Action>" +
                        "    <wsa:To>https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc</wsa:To>" +
                        "  </soap:Header>" +
                        "  <soap:Body>" +
                        "    <ns:DaneSzukajPodmioty>" +
                        "      <ns:pParametryWyszukiwania>" +
                        "        <ParametryWyszukiwania xmlns=\"\">" +
                        "          <Krs>" + krs + "</Krs>" +
                        "        </ParametryWyszukiwania>" +
                        "      </ns:pParametryWyszukiwania>" +
                        "    </ns:DaneSzukajPodmioty>" +
                        "  </soap:Body>" +
                        "</soap:Envelope>";

        return RequestBody.create(
                xml, MediaType.parse("application/soap+xml; charset=utf-8")
        );
    }

    // Wyszukiwanie po REGON
    public static RequestBody daneSzukajPodmiotyByRegonEnvelope(String regon) {
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "               xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" " +
                        "               xmlns:ns=\"http://CIS/BIR/PUBL/2014/07\">" +
                        "  <soap:Header>" +
                        "    <wsa:Action>http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/DaneSzukajPodmioty</wsa:Action>" +
                        "    <wsa:To>https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc</wsa:To>" +
                        "  </soap:Header>" +
                        "  <soap:Body>" +
                        "    <ns:DaneSzukajPodmioty>" +
                        "      <ns:pParametryWyszukiwania>" +
                        "        <ParametryWyszukiwania xmlns=\"\">" +
                        "          <Regon>" + regon + "</Regon>" +
                        "        </ParametryWyszukiwania>" +
                        "      </ns:pParametryWyszukiwania>" +
                        "    </ns:DaneSzukajPodmioty>" +
                        "  </soap:Body>" +
                        "</soap:Envelope>";

        return RequestBody.create(
                xml, MediaType.parse("application/soap+xml; charset=utf-8")
        );
    }

    // Metoda do pobierania pełnego raportu o podmiocie
    public static RequestBody danePobierzPelnyRaportEnvelope(String regon, String nazwaRaportu) {
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "               xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" " +
                        "               xmlns:ns=\"http://CIS/BIR/PUBL/2014/07\">" +
                        "  <soap:Header>" +
                        "    <wsa:Action>http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/DanePobierzPelnyRaport</wsa:Action>" +
                        "    <wsa:To>https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc</wsa:To>" +
                        "  </soap:Header>" +
                        "  <soap:Body>" +
                        "    <ns:DanePobierzPelnyRaport>" +
                        "      <ns:pRegon>" + regon + "</ns:pRegon>" +
                        "      <ns:pNazwaRaportu>" + nazwaRaportu + "</ns:pNazwaRaportu>" +
                        "    </ns:DanePobierzPelnyRaport>" +
                        "  </soap:Body>" +
                        "</soap:Envelope>";

        return RequestBody.create(
                xml, MediaType.parse("application/soap+xml; charset=utf-8")
        );
    }

    // Metoda do pobierania wartości parametrów systemu
    public static RequestBody getValueEnvelope(String parameterName) {
        String xml =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
                        "               xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" " +
                        "               xmlns:ns=\"http://CIS/BIR/PUBL/2014/07\">" +
                        "  <soap:Header>" +
                        "    <wsa:Action>http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/GetValue</wsa:Action>" +
                        "    <wsa:To>https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc</wsa:To>" +
                        "  </soap:Header>" +
                        "  <soap:Body>" +
                        "    <ns:GetValue>" +
                        "      <ns:pNazwaParametru>" + parameterName + "</ns:pNazwaParametru>" +
                        "    </ns:GetValue>" +
                        "  </soap:Body>" +
                        "</soap:Envelope>";

        return RequestBody.create(
                xml, MediaType.parse("application/soap+xml; charset=utf-8")
        );
    }

    // Metoda do sprawdzania statusu sesji
    public static RequestBody getValueStatusSesjiEnvelope() {
        return getValueEnvelope("StatusSesji");
    }

    // Metoda do pobierania daty stanu bazy danych
    public static RequestBody getValueStanDanychEnvelope() {
        return getValueEnvelope("StanDanych");
    }
}
