package com.example.companyfind;

public class NipValidator {

    /**
     * Waliduje numer NIP zgodnie z polskim algorytmem
     * @param nip numer NIP do walidacji
     * @return true jeśli NIP jest prawidłowy, false w przeciwnym razie
     */
    public static boolean isValidNip(String nip) {
        if (nip == null || nip.trim().isEmpty()) {
            return false;
        }

        // Usuń wszystkie znaki niebędące cyframi
        nip = nip.replaceAll("[^0-9]", "");

        // NIP musi mieć dokładnie 10 cyfr
        if (nip.length() != 10) {
            return false;
        }

        // Sprawdź czy wszystkie cyfry nie są takie same
        if (nip.matches("(.)\\1{9}")) {
            return false;
        }

        // Oblicz sumę kontrolną
        int[] weights = {6, 5, 7, 2, 3, 4, 5, 6, 7};
        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(nip.charAt(i)) * weights[i];
        }

        int checkDigit = sum % 11;
        if (checkDigit == 10) {
            return false;
        }

        // Porównaj z ostatnią cyfrą NIP
        return checkDigit == Character.getNumericValue(nip.charAt(9));
    }

    /**
     * Formatuje NIP do standardowej postaci XXX-XXX-XX-XX
     * @param nip numer NIP
     * @return sformatowany NIP lub null jeśli nieprawidłowy
     */
    public static String formatNip(String nip) {
        if (!isValidNip(nip)) {
            return null;
        }

        nip = nip.replaceAll("[^0-9]", "");
        return nip.substring(0, 3) + "-" + nip.substring(3, 6) + "-" +
               nip.substring(6, 8) + "-" + nip.substring(8, 10);
    }
}
