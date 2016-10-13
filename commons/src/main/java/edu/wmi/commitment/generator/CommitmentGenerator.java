package edu.wmi.commitment.generator;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by lupus on 13.10.16.
 */
public class CommitmentGenerator {

    public String generateHash(byte[] r1, byte[] r2, byte[] bits) throws NoSuchAlgorithmException {
        byte[] addedTwo = ArrayUtils.addAll(r1, r2);
        byte[] addedAll = ArrayUtils.addAll(addedTwo, bits);
        MessageDigest md5 = MessageDigest.getInstance("md5");
        byte[] digest = md5.digest(addedAll);
        return Hex.encodeHexString(digest);
    }

    public byte[] generateRandomBytes(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

}
