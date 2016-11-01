package edu.wmi.rsa;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by lupus on 27.10.16.
 */
public class RSAGen {

    private KeyPairGenerator keyPairGenerator;

    public RSAGen() {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR");
        }
    }

    public KeyPair getNextKeyPair(int bits) {
        keyPairGenerator.initialize(bits, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

}
