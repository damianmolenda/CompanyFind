package com.example.companyfind.gus;

/**
 * Klasa reprezentująca dane firmy z systemu GUS
 */
public class GusCompanyData {
    private String nazwa;
    private String nip;
    private String regon;
    private String adres;
    private String status;
    private String wojewodztwo;
    private String powiat;
    private String gmina;
    private String kodPocztowy;
    private String miejscowosc;
    private String ulica;
    private String nrNieruchomosci;
    private String nrLokalu;
    private String typ;
    private String silosID;
    private String dataZakonczeniaDzialalnosci;

    // Konstruktor domyślny
    public GusCompanyData() {}

    // Gettery i settery
    public String getNazwa() { return nazwa; }
    public void setNazwa(String nazwa) { this.nazwa = nazwa; }

    public String getNip() { return nip; }
    public void setNip(String nip) { this.nip = nip; }

    public String getRegon() { return regon; }
    public void setRegon(String regon) { this.regon = regon; }

    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getWojewodztwo() { return wojewodztwo; }
    public void setWojewodztwo(String wojewodztwo) { this.wojewodztwo = wojewodztwo; }

    public String getPowiat() { return powiat; }
    public void setPowiat(String powiat) { this.powiat = powiat; }

    public String getGmina() { return gmina; }
    public void setGmina(String gmina) { this.gmina = gmina; }

    public String getKodPocztowy() { return kodPocztowy; }
    public void setKodPocztowy(String kodPocztowy) { this.kodPocztowy = kodPocztowy; }

    public String getMiejscowosc() { return miejscowosc; }
    public void setMiejscowosc(String miejscowosc) { this.miejscowosc = miejscowosc; }

    public String getUlica() { return ulica; }
    public void setUlica(String ulica) { this.ulica = ulica; }

    public String getNrNieruchomosci() { return nrNieruchomosci; }
    public void setNrNieruchomosci(String nrNieruchomosci) { this.nrNieruchomosci = nrNieruchomosci; }

    public String getNrLokalu() { return nrLokalu; }
    public void setNrLokalu(String nrLokalu) { this.nrLokalu = nrLokalu; }

    public String getTyp() { return typ; }
    public void setTyp(String typ) { this.typ = typ; }

    public String getSilosID() { return silosID; }
    public void setSilosID(String silosID) { this.silosID = silosID; }

    public String getDataZakonczeniaDzialalnosci() { return dataZakonczeniaDzialalnosci; }
    public void setDataZakonczeniaDzialalnosci(String dataZakonczeniaDzialalnosci) {
        this.dataZakonczeniaDzialalnosci = dataZakonczeniaDzialalnosci;
    }

    /**
     * Zwraca pełny adres jako jeden string
     */
    public String getPelnyAdres() {
        StringBuilder sb = new StringBuilder();

        if (ulica != null && !ulica.trim().isEmpty()) {
            sb.append(ulica);
            if (nrNieruchomosci != null && !nrNieruchomosci.trim().isEmpty()) {
                sb.append(" ").append(nrNieruchomosci);
            }
            if (nrLokalu != null && !nrLokalu.trim().isEmpty()) {
                sb.append("/").append(nrLokalu);
            }
            sb.append(", ");
        }

        if (kodPocztowy != null && !kodPocztowy.trim().isEmpty()) {
            sb.append(kodPocztowy).append(" ");
        }

        if (miejscowosc != null && !miejscowosc.trim().isEmpty()) {
            sb.append(miejscowosc);
        }

        return sb.toString().trim();
    }
}
