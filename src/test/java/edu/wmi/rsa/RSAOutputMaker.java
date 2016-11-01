package edu.wmi.rsa;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.Test;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import sun.security.rsa.RSAPublicKeyImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by lupus on 27.10.16.
 */
public class RSAOutputMaker {

    private RSAGen rsaGen;

    @Test
    public void generate() {
        rsaGen = new RSAGen();
        KeyPair keyPair = rsaGen.getNextKeyPair(2048);
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();

        String stringedPrivate = HexUtils.toHexString(privateKey);
        String stringedPublic = HexUtils.toHexString(publicKey);
        System.out.println("priv");
        System.out.println(stringedPrivate);
        System.out.println("public");
        System.out.println(stringedPublic);

    }

    @Test
    public void generateTwo() {
        System.out.println("-------");
        generate();
        System.out.println("-------");
        generate();

    }

    @Test
    public void importKeys() throws IOException, InvalidKeyException {
        InputStream rsapub = RSAOutputMaker.class.getResourceAsStream("keys/enc/id_rsa.pub");
        InputStream rsapriv = RSAOutputMaker.class.getResourceAsStream("keys/enc/id_rsa");
        StringWriter writer = new StringWriter();
        IOUtils.copy(rsapub, writer);
        String readPub = writer.toString();
        StringWriter writer1 = new StringWriter();
        IOUtils.copy(rsapriv, writer1);
        String readPriv = writer1.toString();

        RSAPrivateKey privateKey = RSAPrivateCrtKeyImpl.newKey(HexUtils.fromHexString(readPriv));
        RSAPublicKey publicKey = new RSAPublicKeyImpl(HexUtils.fromHexString(readPub));
        KeyPair keyPair = new KeyPair(publicKey, privateKey);
        assertThat(privateKey.getModulus()).isEqualByComparingTo(publicKey.getModulus());
        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getPrivate()).isEqualTo(privateKey);
        assertThat(keyPair.getPublic()).isEqualTo(publicKey);
    }

}
