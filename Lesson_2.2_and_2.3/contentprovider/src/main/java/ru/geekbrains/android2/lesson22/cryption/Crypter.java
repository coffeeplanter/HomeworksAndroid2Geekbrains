package ru.geekbrains.android2.lesson22.cryption;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Crypter {

    private Cipher encipher;
    private Cipher decipher;

    public Crypter(byte[] key) {
        try {
            updateSecretKey(new MySecretKey(key));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSecretKey(SecretKey key) throws Exception {
        encipher = Cipher.getInstance(key.getAlgorithm());
        decipher = Cipher.getInstance(key.getAlgorithm());
        encipher.init(Cipher.ENCRYPT_MODE, key);
        decipher.init(Cipher.DECRYPT_MODE, key);
    }

    private static class MySecretKey implements SecretKey {

        private final byte[] key;

        MySecretKey(byte[] key) {
            this.key = key;
        }

        @Override
        public String getAlgorithm() {
            return "AES";
        }

        @Override
        public String getFormat() {
            return "RAW";
        }

        @Override
        public byte[] getEncoded() {
            return key;
        }
    }

    public String encrypt(String str) {
        try {
            byte[] encrypted = encipher.doFinal(str.getBytes("UTF8"));
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String decrypt(String str) {
        try {
            byte[] decrypted = Base64.decode(str, Base64.DEFAULT);
            return new String(decipher.doFinal(decrypted), "UTF8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

}
