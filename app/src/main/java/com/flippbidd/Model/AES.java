package com.flippbidd.Model;


import com.flippbidd.Others.LogUtils;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private final static String HEX = "0123456789abcdef";
    private static final String TAG = AES.class.getSimpleName();
    private static AES instance;
    private final String AES = "AES";
    private final String CIPHER_TRANSFORM = "AES/CBC/PKCS5Padding";  //ZeroBytePadding PKCS5Padding
    private final Charset CHARSET = Charset.forName("UTF-8");
    private String KEY = "GaIc3qmnw8PNympw";
    private String IV = "TNwzYLgIY8OrBsrL";

    public AES() {
    }

    /**
     * get instance of AES
     *
     * @return
     */
    public static AES getInstance() {
        if (instance == null) {
            instance = new AES();
        }
        return instance;
    }

    /**
     * Encrypt normal text to cipher form
     *
     * @param input
     * @return Normal text from Encrypted text
     */
    public String doEncrypt(String input) {
        String result = "";
        if (input == null || input.equals("")) {
            return result;
        }
        try {
            byte[] keyByte = KEY.getBytes(CHARSET);
            byte[] ivByte = IV.getBytes(CHARSET);
            byte[] inputByte = input.getBytes(CHARSET);

            SecretKeySpec key = new SecretKeySpec(keyByte, AES);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivByte));

            byte[] rawEnc = cipher.doFinal(inputByte);
            result = toHex(rawEnc);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * print byte array value
     *
     * @param data
     */
    private void printByte(byte[] data) {
        StringBuffer strByte = new StringBuffer();
        StringBuffer strNoByte = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            strByte.append((char) data[i] + "|");
            strNoByte.append(data[i] + "|");
        }
        LogUtils.LOGI(TAG, "strByte: " + strByte.toString());
        LogUtils.LOGI(TAG, "strNoByte: " + strNoByte.toString());
    }

    public int doDecrypt(int input) {
        int result = 0;
        try {
            result = Integer.parseInt(doDecrypt(input + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Decrypt Encrypted text
     *
     * @param input
     * @return Normal text from Encrypted text
     */
    public String doDecrypt(String input) {
        String result = "";
        if (input == null || input.equals("")) {
            return result;
        }
        try {
            byte[] keyByte = KEY.getBytes(CHARSET);
            byte[] ivByte = IV.getBytes(CHARSET);
            byte[] inputByte = toByte(input);

            SecretKeySpec key = new SecretKeySpec(keyByte, AES);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivByte));

            byte[] rawDec = cipher.doFinal(inputByte);
            result = new String(rawDec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * from hex string to byte array
     *
     * @param hexString string encoded in hex
     * @return return byte array
     * @throws Exception
     */
    private byte[] toByte(String hexString) throws Exception {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    /**
     * from byte array to hex
     *
     * @param buf byte array buffer
     * @return return hex
     * @throws Exception
     */
    private String toHex(byte[] buf) throws Exception {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    /**
     * append byte in string buffer
     *
     * @param sb String buffer
     * @param b  byte
     * @throws Exception
     */
    private void appendHex(StringBuffer sb, byte b) throws Exception {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

}