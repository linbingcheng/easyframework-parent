package top.bingchenglin.commons.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class CipherUtil {
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    // 对称加密（sym）：DES、3DES（TripleDES）、AES、RC2、RC4、RC5和Blowfish
    public static final String ALGORITHM_DES = "DES";
    public static final String ALGORITHM_DES_ECB_PKCS5Padding = "DES/ECB/PKCS5Padding";
    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_RC2 = "RC2";
    public static final String ALGORITHM_RC4 = "RC4";
    public static final String ALGORITHM_BLOWFISH = "Blowfish";

    // 非对称加密（asym）：RSA、Elgamal
    public static final String ALGORITHM_RSA = "RSA";
    public static final String ALGORITHM_RSA_ECB_PKCS1Padding = "RSA/ECB/PKCS1Padding";
    public static final String ALGORITHM_RSA_ECB_NoPadding = "RSA/ECB/NoPadding";

    // 哈希（hash）：MD5、SHA1、SHA256
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA1 = "SHA1withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA256 = "SHA256withRSA";

    public static final String PUBLIC_KEY = "PublicKey";

    public static final String PRIVATE_KEY = "PrivateKey";

    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();

    private CipherUtil() {
        // ignore
    }

    public static String encryptPassword(String username, String password) {
        String cipherPassword = DigestUtils.sha256Hex("jcoc2#" + username + "#" + password);
        String typeCipherPassword = "default:" + cipherPassword;
        String wrapCipherPassword = Base64.encodeBase64String(typeCipherPassword.getBytes(CHARSET_UTF8));
        return wrapCipherPassword;
    }

    public static boolean verifyPassword(String username, String password, String inputPassword) {
        String inputCipherPassword = encryptPassword(username, inputPassword);
        if (password.equals(inputCipherPassword)) {
            return true;
        }
        return false;
    }

    public static String getSecretKey(String algorithm) throws CipherException {
        try {
            algorithm = fixAlgorithm(algorithm);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm, DEFAULT_PROVIDER);
            if (ALGORITHM_DES.equalsIgnoreCase(algorithm)) {
                keyGenerator.init(64);
            } else {
                keyGenerator.init(128);
            }
            SecretKey secretKey = keyGenerator.generateKey();
            String secretKeyString = Base64.encodeBase64String(secretKey.getEncoded());
            return secretKeyString;
        } catch (NoSuchAlgorithmException e) {
            throw new CipherException("生成密钥失败", e);
        }
    }

    public static String encryptWithSecretKey(String text, String secretKeyString, String algorithm) throws CipherException {
        try {
            byte[] secretKeyBytes = Base64.decodeBase64(secretKeyString);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] dataBytes = cipher.doFinal(text.getBytes(CHARSET_UTF8));
            return Base64.encodeBase64String(dataBytes);
        } catch (Exception e) {
            throw new CipherException("加密失败", e);
        }
    }

    public static String decryptWithSecretKey(String data, String secretKeyString, String algorithm) throws CipherException {
        try {
            byte[] secretKeyBytes = Base64.decodeBase64(secretKeyString);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] textBytes = cipher.doFinal(Base64.decodeBase64(data));
            return new String(textBytes, CHARSET_UTF8);
        } catch (Exception e) {
            throw new CipherException("解密失败", e);
        }
    }

    public static Map<String, String> getSecretKeyPair(String algorithm) throws CipherException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(fixAlgorithm(algorithm), DEFAULT_PROVIDER);
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            String publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
            String privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
            Map<String, String> keyPairMap = new HashMap<String, String>();
            keyPairMap.put(PUBLIC_KEY, publicKeyString);
            keyPairMap.put(PRIVATE_KEY, privateKeyString);
            return keyPairMap;
        } catch (NoSuchAlgorithmException e) {
            throw new CipherException("生成密钥对失败", e);
        }
    }

    public static String encryptWithPrivateKey(String text, String privateKeyString, String algorithm) throws CipherException {
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyString, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] dataBytes = splitFinal(text.getBytes(CHARSET_UTF8), MAX_ENCRYPT_BLOCK, cipher);
            return Base64.encodeBase64String(dataBytes);
        } catch (Exception e) {
            throw new CipherException("加密失败", e);
        }
    }

    public static String encryptWithPublicKey(String text, String publicKeyString, String algorithm) throws CipherException {
        try {
            PublicKey publicKey = getPublicKey(publicKeyString, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] dataBytes = splitFinal(text.getBytes(CHARSET_UTF8), MAX_ENCRYPT_BLOCK, cipher);
            return Base64.encodeBase64String(dataBytes);
        } catch (Exception e) {
            throw new CipherException("加密失败", e);
        }
    }

    public static String decryptWithPrivateKey(String data, String privateKeyString, String algorithm) throws CipherException {
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyString, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] textBytes = splitFinal(Base64.decodeBase64(data), MAX_DECRYPT_BLOCK, cipher);
            return new String(textBytes, CHARSET_UTF8);
        } catch (Exception e) {
            throw new CipherException("解密失败", e);
        }
    }

    public static String decryptWithPublicKey(String data, String publicKeyString, String algorithm) throws CipherException {
        try {
            PublicKey publicKey = getPublicKey(publicKeyString, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm, DEFAULT_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] textBytes = splitFinal(Base64.decodeBase64(data), MAX_DECRYPT_BLOCK, cipher);
            return new String(textBytes, CHARSET_UTF8);
        } catch (Exception e) {
            throw new CipherException("加密失败", e);
        }
    }

    public static String signWithPrivateKey(String data, String privateKeyString, String algorithm, String signatureAlgorithm) throws CipherException {
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyString, algorithm);
            Signature signature = Signature.getInstance(signatureAlgorithm, DEFAULT_PROVIDER);
            signature.initSign(privateKey);
            signature.update(Base64.decodeBase64(data));
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            throw new CipherException("数字签名失败", e);
        }
    }

    public static boolean verifyWithPublicKey(String data, String sign, String publicKeyString, String algorithm, String signatureAlgorithm) throws CipherException {
        try {
            PublicKey publicKey = getPublicKey(publicKeyString, algorithm);
            Signature signature = Signature.getInstance(signatureAlgorithm, DEFAULT_PROVIDER);
            signature.initVerify(publicKey);
            signature.update(Base64.decodeBase64(data));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            throw new CipherException("校验签名失败", e);
        }
    }

    private static PrivateKey getPrivateKey(String privateKeyString, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Base64.decodeBase64(privateKeyString);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(fixAlgorithm(algorithm), DEFAULT_PROVIDER);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    private static PublicKey getPublicKey(String publicKeyString, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.decodeBase64(publicKeyString);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(fixAlgorithm(algorithm), DEFAULT_PROVIDER);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    private static String fixAlgorithm(String algorithm) {
        int index = algorithm.indexOf('/');
        if (index >= 0) {
            algorithm = algorithm.substring(0, index);
        }
        return algorithm;
    }

    /**
     * 对数据分段加密或解密
     *
     * @param data
     * @param maxSize
     * @param cipher
     * @return
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static byte[] splitFinal(byte[] data, int maxSize, Cipher cipher) throws IOException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            byte[] bytes;
            int index = 0;
            int offset = 0;
            int length = data.length;
            while (length - offset > 0) {
                if (length - offset > maxSize) {
                    bytes = cipher.doFinal(data, offset, maxSize);
                } else {
                    bytes = cipher.doFinal(data, offset, length - offset);
                }
                stream.write(bytes, 0, bytes.length);
                offset = (++index) * maxSize;
            }
            stream.flush();
            return stream.toByteArray();
        } finally {
            stream.close();
        }
    }

    public static void main(String[] args) {
        String username = args.length > 0 ? args[0] : "admin";
        String password = args.length > 1 ? args[1] : "12345";

        String cipherPassword = CipherUtil.encryptPassword(username, password);
        boolean isValid = verifyPassword(username, cipherPassword, password);
        System.out.format("username: %s\npassword: %s\ncipherPassword: %s\nisValid: %b\n",
                username, password, cipherPassword, isValid);
    }
}
