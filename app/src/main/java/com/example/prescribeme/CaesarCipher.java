package com.example.prescribeme;

public class CaesarCipher {

    protected static int TOTAL_ASCII_CHARS = 256;

    protected static String encrypt(String message) {
        //Encrypt message using Caesar Cipher
        String encryptedMessage = "", shift;
        int randomShift =3+ (int)(Math.random() * TOTAL_ASCII_CHARS-3);
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            c = (char) ((c + randomShift) % TOTAL_ASCII_CHARS);
            encryptedMessage += c;
        }

        if(randomShift<10)
            shift = "00"+randomShift;
        else if(randomShift<100)
            shift = "0"+randomShift;
        else
            shift = ""+randomShift;
        return encryptedMessage+shift;
    }

    protected static String decrypt(String message) {
        //Decrypt message using Caesar Cipher
        String decryptedMessage = "";
        int randomShift;
        try {
            randomShift = Integer.parseInt(message.substring(message.length() - 3));
        }
        catch (Exception e)
        {
            return "Error!";
        }
        for (int i = 0; i < message.length()-3; i++) {
            char c = message.charAt(i);
            c = (char) ((c - randomShift + TOTAL_ASCII_CHARS) % TOTAL_ASCII_CHARS);
            decryptedMessage += c;
        }
        return decryptedMessage;
    }
}
