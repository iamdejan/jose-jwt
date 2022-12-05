package id.train.steam;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class Main {

    private static final byte[] MOD = {
        (byte) 177, (byte) 119, (byte) 33, (byte) 13, (byte) 164, (byte) 30, (byte) 108, (byte) 121,
        (byte) 207, (byte) 136, (byte) 107, (byte) 242, (byte) 12, (byte) 224, (byte) 19, (byte) 226,
        (byte) 198, (byte) 134, (byte) 17, (byte) 71, (byte) 173, (byte) 75, (byte) 42, (byte) 61,
        (byte) 48, (byte) 162, (byte) 206, (byte) 161, (byte) 97, (byte) 108, (byte) 185, (byte) 234,
        (byte) 226, (byte) 219, (byte) 118, (byte) 206, (byte) 118, (byte) 5, (byte) 169, (byte) 224,

        (byte) 60, (byte) 181, (byte) 90, (byte) 85, (byte) 51, (byte) 123, (byte) 6, (byte) 224,
        (byte) 4, (byte) 122, (byte) 29, (byte) 230, (byte) 151, (byte) 12, (byte) 244, (byte) 127,
        (byte) 121, (byte) 25, (byte) 4, (byte) 85, (byte) 220, (byte) 144, (byte) 215, (byte) 110,
        (byte) 130, (byte) 17, (byte) 68, (byte) 228, (byte) 129, (byte) 138, (byte) 7, (byte) 130,
        (byte) 231, (byte) 40, (byte) 212, (byte) 214, (byte) 17, (byte) 179, (byte) 28, (byte) 124,

        (byte) 151, (byte) 178, (byte) 207, (byte) 20, (byte) 14, (byte) 154, (byte) 222, (byte) 113,
        (byte) 176, (byte) 24, (byte) 198, (byte) 73, (byte) 211, (byte) 113, (byte) 9, (byte) 33,
        (byte) 178, (byte) 80, (byte) 13, (byte) 25, (byte) 21, (byte) 25, (byte) 153, (byte) 212,
        (byte) 206, (byte) 67, (byte) 154, (byte) 147, (byte) 70, (byte) 194, (byte) 192, (byte) 183,
        (byte) 160, (byte) 83, (byte) 98, (byte) 236, (byte) 175, (byte) 85, (byte) 23, (byte) 97,

        (byte) 75, (byte) 199, (byte) 177, (byte) 73, (byte) 145, (byte) 50, (byte) 253, (byte) 206,
        (byte) 32, (byte) 179, (byte) 254, (byte) 236, (byte) 190, (byte) 82, (byte) 73, (byte) 67,
        (byte) 129, (byte) 253, (byte) 252, (byte) 220, (byte) 108, (byte) 136, (byte) 138, (byte) 11,
        (byte) 192, (byte) 1, (byte) 36, (byte) 239, (byte) 228, (byte) 55, (byte) 81, (byte) 113,
        (byte) 17, (byte) 25, (byte) 140, (byte) 63, (byte) 239, (byte) 146, (byte) 3, (byte) 172,

        (byte) 96, (byte) 60, (byte) 227, (byte) 233, (byte) 64, (byte) 255, (byte) 224, (byte) 173,
        (byte) 225, (byte) 228, (byte) 229, (byte) 92, (byte) 112, (byte) 72, (byte) 99, (byte) 97,
        (byte) 26, (byte) 87, (byte) 187, (byte) 123, (byte) 46, (byte) 50, (byte) 90, (byte) 202,
        (byte) 117, (byte) 73, (byte) 10, (byte) 153, (byte) 47, (byte) 224, (byte) 178, (byte) 163,
        (byte) 77, (byte) 48, (byte) 46, (byte) 154, (byte) 33, (byte) 148, (byte) 34, (byte) 228,

        (byte) 33, (byte) 172, (byte) 216, (byte) 89, (byte) 46, (byte) 225, (byte) 127, (byte) 68,
        (byte) 146, (byte) 234, (byte) 30, (byte) 147, (byte) 54, (byte) 146, (byte) 5, (byte) 133,
        (byte) 45, (byte) 78, (byte) 254, (byte) 85, (byte) 55, (byte) 75, (byte) 213, (byte) 86,
        (byte) 194, (byte) 218, (byte) 215, (byte) 163, (byte) 189, (byte) 194, (byte) 54, (byte) 6,
        (byte) 83, (byte) 36, (byte) 18, (byte) 153, (byte) 53, (byte) 7, (byte) 48, (byte) 89,

        (byte) 35, (byte) 66, (byte) 144, (byte) 7, (byte) 65, (byte) 154, (byte) 13, (byte) 97,
        (byte) 75, (byte) 55, (byte) 230, (byte) 132, (byte) 3, (byte) 13, (byte) 239, (byte) 71};


    private static final byte[] EXP = {1, 0, 1};

    private static final byte[] MOD_PRIVATE = {
        (byte) 84, (byte) 80, (byte) 150, (byte) 58, (byte) 165, (byte) 235, (byte) 242, (byte) 123,
        (byte) 217, (byte) 55, (byte) 38, (byte) 154, (byte) 36, (byte) 181, (byte) 221, (byte) 156,
        (byte) 211, (byte) 215, (byte) 100, (byte) 164, (byte) 90, (byte) 88, (byte) 40, (byte) 228,
        (byte) 83, (byte) 148, (byte) 54, (byte) 122, (byte) 4, (byte) 16, (byte) 165, (byte) 48,
        (byte) 76, (byte) 194, (byte) 26, (byte) 107, (byte) 51, (byte) 53, (byte) 179, (byte) 165,

        (byte) 31, (byte) 18, (byte) 198, (byte) 173, (byte) 78, (byte) 61, (byte) 56, (byte) 97,
        (byte) 252, (byte) 158, (byte) 140, (byte) 80, (byte) 63, (byte) 25, (byte) 223, (byte) 156,
        (byte) 36, (byte) 203, (byte) 214, (byte) 252, (byte) 120, (byte) 67, (byte) 180, (byte) 167,
        (byte) 3, (byte) 82, (byte) 243, (byte) 25, (byte) 97, (byte) 214, (byte) 83, (byte) 133,
        (byte) 69, (byte) 16, (byte) 104, (byte) 54, (byte) 160, (byte) 200, (byte) 41, (byte) 83,

        (byte) 164, (byte) 187, (byte) 70, (byte) 153, (byte) 111, (byte) 234, (byte) 242, (byte) 158,
        (byte) 175, (byte) 28, (byte) 198, (byte) 48, (byte) 211, (byte) 45, (byte) 148, (byte) 58,
        (byte) 23, (byte) 62, (byte) 227, (byte) 74, (byte) 52, (byte) 117, (byte) 42, (byte) 90,
        (byte) 41, (byte) 249, (byte) 130, (byte) 154, (byte) 80, (byte) 119, (byte) 61, (byte) 26,
        (byte) 193, (byte) 40, (byte) 125, (byte) 10, (byte) 152, (byte) 174, (byte) 227, (byte) 225,

        (byte) 205, (byte) 32, (byte) 62, (byte) 66, (byte) 6, (byte) 163, (byte) 100, (byte) 99,
        (byte) 219, (byte) 19, (byte) 253, (byte) 25, (byte) 105, (byte) 80, (byte) 201, (byte) 29,
        (byte) 252, (byte) 157, (byte) 237, (byte) 69, (byte) 1, (byte) 80, (byte) 171, (byte) 167,
        (byte) 20, (byte) 196, (byte) 156, (byte) 109, (byte) 249, (byte) 88, (byte) 0, (byte) 3,
        (byte) 152, (byte) 38, (byte) 165, (byte) 72, (byte) 87, (byte) 6, (byte) 152, (byte) 71,

        (byte) 156, (byte) 214, (byte) 16, (byte) 71, (byte) 30, (byte) 82, (byte) 51, (byte) 103,
        (byte) 76, (byte) 218, (byte) 63, (byte) 9, (byte) 84, (byte) 163, (byte) 249, (byte) 91,
        (byte) 215, (byte) 44, (byte) 238, (byte) 85, (byte) 101, (byte) 240, (byte) 148, (byte) 1,
        (byte) 82, (byte) 224, (byte) 91, (byte) 135, (byte) 105, (byte) 127, (byte) 84, (byte) 171,
        (byte) 181, (byte) 152, (byte) 210, (byte) 183, (byte) 126, (byte) 24, (byte) 46, (byte) 196,

        (byte) 90, (byte) 173, (byte) 38, (byte) 245, (byte) 219, (byte) 186, (byte) 222, (byte) 27,
        (byte) 240, (byte) 212, (byte) 194, (byte) 15, (byte) 66, (byte) 135, (byte) 226, (byte) 178,
        (byte) 190, (byte) 52, (byte) 245, (byte) 74, (byte) 65, (byte) 224, (byte) 81, (byte) 100,
        (byte) 85, (byte) 25, (byte) 204, (byte) 165, (byte) 203, (byte) 187, (byte) 175, (byte) 84,
        (byte) 100, (byte) 82, (byte) 15, (byte) 11, (byte) 23, (byte) 202, (byte) 151, (byte) 107,

        (byte) 54, (byte) 41, (byte) 207, (byte) 3, (byte) 136, (byte) 229, (byte) 134, (byte) 131,
        (byte) 93, (byte) 139, (byte) 50, (byte) 182, (byte) 204, (byte) 93, (byte) 130, (byte) 89};

    private static RSAPublicKey PUBLIC_KEY;

    private static RSAPrivateKey PRIVATE_KEY;

    static {
        try {
            var keyFactory = KeyFactory.getInstance("RSA");

            var publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, MOD), new BigInteger(1, EXP));
            var privateKeySpec = new RSAPrivateKeySpec(new BigInteger(1, MOD), new BigInteger(1, MOD_PRIVATE));

            PUBLIC_KEY = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            PRIVATE_KEY = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Main() throws JOSEException, ParseException, NoSuchAlgorithmException {
        var serializedJWT = sendJWT();
        System.out.println(serializedJWT);

        validateJWT(serializedJWT);
    }

    private static String sendJWT() throws JOSEException, NoSuchAlgorithmException {
        var now = Instant.now();

        var password = "password";
        var encryptedPassword = hash(password);

        var jwtClaimsSet = new JWTClaimsSet.Builder()
            .issuer("https://stream-train.id")
            .subject("dejan")
            .audience(Collections.emptyList())
            .expirationTime(Date.from(now.plus(10, ChronoUnit.DAYS)))
            .notBeforeTime(Date.from(now))
            .issueTime(Date.from(now))
            .jwtID(UUID.randomUUID().toString())
            .claim("password", encryptedPassword) // in real life, don't send password with JWT claims
            .build();

        var header = new JWEHeader(
            JWEAlgorithm.RSA_OAEP_512,
            EncryptionMethod.A256CBC_HS512
        );
        var jwt = new EncryptedJWT(header, jwtClaimsSet);
        var encryptor = new RSAEncrypter(PUBLIC_KEY);
        jwt.encrypt(encryptor);
        return jwt.serialize();

    }

    /**
     * High-level with Spring Security Crypto can be seen <a href="https://stackoverflow.com/questions/66594009/java-argon2-hashing">here</a>.
     * Low-level implementation can be seen <a href="https://gist.github.com/ensingerphilipp/1b41b0f3650a53172752e5a99c7246be">here</a>.
     *
     * @param password
     * @return
     */
    private static String hash(String password) {
        var encoder = new Argon2PasswordEncoder(16, 128, 2, 65536, 10);
        return encoder.encode(password);
    }

    private static void validateJWT(String serializedJWT) throws ParseException, JOSEException {
        var jwt = EncryptedJWT.parse(serializedJWT);
        var decrypter = new RSADecrypter(PRIVATE_KEY);
        jwt.decrypt(decrypter);
        System.out.println(jwt.getJWTClaimsSet());
    }

    public static void main(String[] args) throws JOSEException, ParseException, NoSuchAlgorithmException {
        new Main();
    }
}
