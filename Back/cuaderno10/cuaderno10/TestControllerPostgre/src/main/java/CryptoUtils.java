import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;            // bytes
    private static final int TAG_LENGTH_BIT = 128;      // bits

    // Lee la clave AES desde variable de entorno (BASE64)
    private static SecretKey getKeyFromEnv() {
        String b64 = System.getenv("MY_APP_AES_KEY");
        byte[] decoded = Base64.getDecoder().decode(b64);
        return new SecretKeySpec(decoded, 0, decoded.length, "AES");
    }

    public static String encrypt(String plaintext) throws Exception {
        SecretKey key = getKeyFromEnv();
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String cipherTextB64) throws Exception {
        SecretKey key = getKeyFromEnv();
        byte[] combined = Base64.getDecoder().decode(cipherTextB64);

        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plain = cipher.doFinal(combined, iv.length, combined.length - iv.length);
        return new String(plain, "UTF-8");
    }
}
