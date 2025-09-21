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
                        "               xmlns:ns=\"http://CIS/BIR/PUBL/2014/07\">" +
                        "  <soap:Header>" +
                        "    <wsa:Action>http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/DaneSzukajPodmioty</wsa:Action>" +
                        "    <wsa:To>https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc</wsa:To>" +
                        "  </soap:Header>" +
                        "  <soap:Body>" +
                        "    <ns:DaneSzukajPodmioty>" +
                        "      <ns:pParametryWyszukiwania>" +
                        "        <ns:Nip>" + nip + "</ns:Nip>" +
                        "      </ns:pParametryWyszukiwania>" +
                        "    </ns:DaneSzukajPodmioty>" +
                        "  </soap:Body>" +
                        "</soap:Envelope>";

        return RequestBody.create(
                xml, MediaType.parse("application/soap+xml; charset=utf-8")
        );
    }
}
