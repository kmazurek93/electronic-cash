package edu.wmi.rsa;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

/**
 * Created by lupus on 01.11.16.
 */
@Component
public class RSAService {

    private static final String KEYS_SIGN_ID_RSA_PUB = "keys/sign/id_rsa.pub";
    private static final String KEYS_SIGN_ID_RSA = "keys/sign/id_rsa";
    private static final String KEYS_ENC_ID_RSA_PUB = "keys/enc/id_rsa.pub";
    private static final String KEYS_ENC_ID_RSA = "keys/enc/id_rsa";
    public static final String RSA = "RSA/ECB/NoPadding";

    private KeyPair signingKeyPair;
    private KeyPair encryptKeyPair;
    private Cipher rsa;
    private Signature signature;

    public KeyPair getSigningKeyPair() {
        return signingKeyPair;
    }

    public void setSigningKeyPair(KeyPair signingKeyPair) {
        this.signingKeyPair = signingKeyPair;
    }

    public KeyPair getEncryptKeyPair() {
        return encryptKeyPair;
    }

    public void setEncryptKeyPair(KeyPair encryptKeyPair) {
        this.encryptKeyPair = encryptKeyPair;
    }

    public RSAService() {
        Security.addProvider(new BouncyCastleProvider());
        try {
            this.encryptKeyPair = readKeys(KEYS_ENC_ID_RSA_PUB, KEYS_ENC_ID_RSA);
            this.signingKeyPair = readKeys(KEYS_SIGN_ID_RSA_PUB, KEYS_SIGN_ID_RSA);
            this.rsa = Cipher.getInstance(RSA);
            this.signature = Signature.getInstance("SHA1withRSA");
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize RSAService", e);
        }

    }

    private KeyPair readKeys(String publicKeyRes, String privateKeyRes) throws Exception {
        InputStream rsapub = RSAService.class.getResourceAsStream(publicKeyRes);
        InputStream rsapriv = RSAService.class.getResourceAsStream(privateKeyRes);
        StringWriter writer = new StringWriter();
        IOUtils.copy(rsapub, writer);
        String readPub = writer.toString();
        StringWriter writer1 = new StringWriter();
        IOUtils.copy(rsapriv, writer1);
        String readPriv = writer1.toString();

        RSAPrivateKey privateKey = RSAPrivateCrtKeyImpl.newKey(HexUtils.fromHexString(readPriv));
        RSAPublicKey publicKey = new RSAPublicKeyImpl(HexUtils.fromHexString(readPub));
        return new KeyPair(publicKey, privateKey);
    }

    public byte[] encrypt(byte[] message) throws java.security.GeneralSecurityException {
        rsa.init(Cipher.ENCRYPT_MODE, this.encryptKeyPair.getPublic());
        return rsa.doFinal(message);
    }

    public byte[] decrypt(byte[] cipher) throws GeneralSecurityException {
        rsa.init(Cipher.DECRYPT_MODE, this.encryptKeyPair.getPrivate());
        return rsa.doFinal(cipher);
    }

    public byte[] sign(byte[] message) throws GeneralSecurityException {
        this.signature.initSign(signingKeyPair.getPrivate(), new SecureRandom());
        this.signature.update(message);
        return this.signature.sign();
    }

    public boolean verify(byte[] rsaSignature, byte[] message) throws GeneralSecurityException {
        this.signature.initVerify(signingKeyPair.getPublic());
        this.signature.update(message);
        return this.signature.verify(rsaSignature);
    }

    public PublicKey getSignPublicKey() {
        return this.signingKeyPair.getPublic();
    }

    public PublicKey getEncryptPublicKey() {
        return this.encryptKeyPair.getPublic();
    }

    public byte[] signBlindly(byte[] masked) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, this.signingKeyPair.getPrivate());
        return cipher.doFinal(masked);
    }

    public boolean verifyBlind(byte[] signed, byte[] msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, this.signingKeyPair.getPublic());
        byte[] bytes = cipher.doFinal(signed);
        msg = padMessageArray(msg, bytes.length);
        return Arrays.equals(msg, bytes);
    }

    private byte[] padMessageArray(byte[] msg, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < msg.length; i++) {
            result[i] = msg[i];
        }
        for (int i = msg.length; i < length; i++) {
            result[i] = 0;
        }
        return result;
    }

    public void createBCKeys() {
        RSAPrivateCrtKeyImpl aPrivate = (RSAPrivateCrtKeyImpl) this.signingKeyPair.getPrivate();
        BigInteger modulus = aPrivate.getModulus();
        RSAPrivateCrtKeyParameters privateKey = new RSAPrivateCrtKeyParameters(modulus,
                aPrivate.getPublicExponent(), aPrivate.getPrivateExponent(), aPrivate.getPrimeP(), aPrivate.getPrimeQ(),
                aPrivate.getPrimeExponentP(), aPrivate.getPrimeExponentQ(), aPrivate.getCrtCoefficient());
        
    }
}
