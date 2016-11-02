package edu.wmi.blindsign;

import java.security.SecureRandom;

/**
 * Created by lupus on 02.11.16.
 */
public class MessageGenerator {

    private SecureRandom secureRandom;

    public MessageGenerator() {
        this.secureRandom = new SecureRandom();
    }

    public byte[] getRandomBytes(int size) {
        byte[] outcome = new byte[size];
        secureRandom.nextBytes(outcome);
        return outcome;
    }
}
