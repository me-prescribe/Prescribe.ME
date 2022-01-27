package com.example.prescribeme;

public class CaesarCipher {

    protected static int TOTAL_ALPHABETS = 26;

    protected static String encrypt(String message) {
        //Encrypt message using Caesar Cipher
        String encryptedMessage = "";
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isLetter(c)) {
                int offset = Character.isUpperCase(c) ? 'A' : 'a';
                c = (char) (((c - offset) + 3) % TOTAL_ALPHABETS + offset);
            }
            encryptedMessage += c;
        }
        return encryptedMessage;
    }

    protected static String decrypt(String message) {
        //Decrypt message using Caesar Cipher
        String decryptedMessage = "";
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (Character.isLetter(c)) {
                int offset = Character.isUpperCase(c) ? 'A' : 'a';
                c = (char) (((c - offset) - 3 + TOTAL_ALPHABETS) % TOTAL_ALPHABETS + offset);
            }
            decryptedMessage += c;
        }
        return decryptedMessage;
    }
}
