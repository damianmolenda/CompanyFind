package com.example.companyfind.gus;

import com.example.companyfind.model.Company;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CompanyParser {

    public static List<Company> parseSearchResponse(String xml) {
        List<Company> companies = new ArrayList<>();

        try {
            System.out.println("=== RAW XML INPUT ===");
            System.out.println("Raw XML length: " + xml.length());
            System.out.println("Raw XML preview: " + xml.substring(0, Math.min(500, xml.length())));

            // Usuń MIME multipart headers jeśli są obecne
            xml = cleanMimeMultipart(xml);

            // Usuń namespace prefiksy dla łatwiejszego parsowania
            xml = xml.replaceAll("xmlns[^=]*=\"[^\"]*\"", "")
                    .replaceAll("[a-zA-Z]+:", "");

            System.out.println("=== CLEANED XML ===");
            System.out.println("Cleaned XML: " + xml.substring(0, Math.min(500, xml.length())));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            // Sprawdź czy są wyniki w DaneSzukajPodmiotyResult
            NodeList resultNodes = doc.getElementsByTagName("DaneSzukajPodmiotyResult");
            System.out.println("Found DaneSzukajPodmiotyResult nodes: " + resultNodes.getLength());

            if (resultNodes.getLength() == 0) {
                return companies; // Pusta lista jeśli brak wyników
            }

            String resultContent = resultNodes.item(0).getTextContent();
            System.out.println("Raw result content: " + resultContent);

            // Parsuj escaped XML content
            if (resultContent != null && resultContent.contains("<")) {
                // Unescape HTML entities
                resultContent = resultContent.replace("&lt;", "<")
                                           .replace("&gt;", ">")
                                           .replace("&#xD;", "\r")
                                           .replace("&#xA;", "\n")
                                           .replace("&amp;", "&")
                                           .replace("&quot;", "\"")
                                           .replace("&apos;", "'");

                System.out.println("Unescaped XML: " + resultContent);

                // Parse the unescaped XML
                Document dataDoc = builder.parse(new ByteArrayInputStream(resultContent.getBytes(StandardCharsets.UTF_8)));

                // Szukaj elementów dane (każdy element to jedna firma)
                NodeList companyNodes = dataDoc.getElementsByTagName("dane");
                System.out.println("Found " + companyNodes.getLength() + " company nodes");

                for (int i = 0; i < companyNodes.getLength(); i++) {
                    Element companyElement = (Element) companyNodes.item(i);
                    Company company = parseCompanyElement(companyElement);
                    if (company != null) {
                        companies.add(company);
                        System.out.println("Parsed company: " + company.getName() + " (NIP: " + company.getNip() + ")");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error parsing XML: " + e.getMessage());
            e.printStackTrace();
        }

        return companies;
    }

    private static String cleanMimeMultipart(String xml) {
        try {
            // Usuń MIME multipart headers na początku
            if (xml.contains("--uuid:") && xml.contains("Content-Type:")) {
                int startIndex = xml.indexOf("<s:Envelope");
                if (startIndex == -1) {
                    startIndex = xml.indexOf("<Envelope");
                }
                if (startIndex != -1) {
                    xml = xml.substring(startIndex);
                }
            }

            // Usuń MIME multipart footers na końcu
            if (xml.contains("--uuid:") && xml.lastIndexOf("--uuid:") > xml.indexOf("</s:Envelope>")) {
                int endIndex = xml.lastIndexOf("</s:Envelope>") + "</s:Envelope>".length();
                if (endIndex != -1) {
                    xml = xml.substring(0, endIndex);
                }
            }

            return xml.trim();
        } catch (Exception e) {
            System.out.println("Error cleaning MIME multipart: " + e.getMessage());
            return xml;
        }
    }

    private static Company parseCompanyElement(Element element) {
        try {
            Company company = new Company();

            // Podstawowe dane
            company.setNip(getElementText(element, "Nip"));
            company.setRegon(getElementText(element, "Regon"));
            company.setName(getElementText(element, "Nazwa"));
            company.setType(getElementText(element, "Typ"));

            // Status firmy - SilosID 6 oznacza aktywną
            String silosId = getElementText(element, "SilosID");
            company.setStatus(silosId != null && silosId.equals("6") ? "Aktywna" : "Nieaktywna");

            // Adres
            company.setProvince(getElementText(element, "Wojewodztwo"));
            company.setDistrict(getElementText(element, "Powiat"));
            company.setMunicipality(getElementText(element, "Gmina"));
            company.setCity(getElementText(element, "Miejscowosc"));
            company.setPostalCode(getElementText(element, "KodPocztowy"));
            company.setStreet(getElementText(element, "Ulica"));
            company.setHouseNumber(getElementText(element, "NrNieruchomosci"));

            // Utwórz sformatowany adres
            company.setAddress(buildFormattedAddress(company));

            System.out.println("=== PARSED COMPANY DETAILS ===");
            System.out.println("Name: " + company.getName());
            System.out.println("NIP: " + company.getNip());
            System.out.println("REGON: " + company.getRegon());
            System.out.println("Type: " + company.getType());
            System.out.println("Status: " + company.getStatus());
            System.out.println("Street: " + company.getStreet());
            System.out.println("House Number: " + company.getHouseNumber());
            System.out.println("City: " + company.getCity());
            System.out.println("Postal Code: " + company.getPostalCode());
            System.out.println("Province: " + company.getProvince());
            System.out.println("Formatted Address: " + company.getAddress());

            return company;

        } catch (Exception e) {
            System.out.println("Error parsing company element: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String buildFormattedAddress(Company company) {
        StringBuilder sb = new StringBuilder();

        if (company.getStreet() != null && !company.getStreet().isEmpty()) {
            sb.append(company.getStreet());
            if (company.getHouseNumber() != null && !company.getHouseNumber().isEmpty()) {
                sb.append(" ").append(company.getHouseNumber());
            }
            sb.append("\n");
        }

        if (company.getPostalCode() != null && !company.getPostalCode().isEmpty()) {
            sb.append(company.getPostalCode()).append(" ");
        }

        if (company.getCity() != null && !company.getCity().isEmpty()) {
            sb.append(company.getCity());
        }

        if (company.getProvince() != null && !company.getProvince().isEmpty()) {
            sb.append("\n").append("woj. ").append(company.getProvince().toLowerCase());
        }

        return sb.toString().trim();
    }

    private static String getElementText(Element parent, String tagName) {
        try {
            NodeList nodeList = parent.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                String text = nodeList.item(0).getTextContent();
                return text != null && !text.trim().isEmpty() ? text.trim() : null;
            }
        } catch (Exception e) {
            System.out.println("Error getting element text for tag: " + tagName + " - " + e.getMessage());
        }
        return null;
    }

    // Alternatywna metoda parsowania dla przypadków gdy XML ma inną strukturę
    public static Company parseFirstCompany(String xml) {
        List<Company> companies = parseSearchResponse(xml);
        return companies.isEmpty() ? null : companies.get(0);
    }
}
