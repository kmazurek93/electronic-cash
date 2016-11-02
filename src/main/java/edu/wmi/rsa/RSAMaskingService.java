package edu.wmi.rsa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Random;

import static edu.wmi.rsa.Utils.modInv;

/**
 * Created by lupus on 01.11.16.
 */
@Component
@Profile("alice")
public class RSAMaskingService {

    private final RSAService rsaService;
    private BigInteger k;
    private BigInteger kInv;
    private RSAPublicKey publicKey;
    private int blockSize;

    @Autowired
    public RSAMaskingService(RSAService rsaService) {
        this.rsaService = rsaService;
        this.publicKey = (RSAPublicKey) rsaService.getSignPublicKey();
        this.blockSize = (getModulus().bitLength() / 8);

    }

    public byte[] mask(byte[] toMask) {
        genMaskMultiplier();
        BigInteger kPow = k.modPow(publicKey.getPublicExponent(), getModulus());

        return doMaskOrUnmask(kPow, toMask);
    }

    public byte[] unMask(byte[] toUnmask) {
        if (kInv == null) {
            throw new IllegalArgumentException("How you mask the message?");
        } else {
            return doMaskOrUnmask(kInv, toUnmask);
        }
    }

    private void genMaskMultiplier() {
        Random random = new SecureRandom();
        int bitLength = getModulus().bitLength() / 2;
        k = new BigInteger(bitLength, 10, random);
        while (!k.gcd(getModulus()).equals(BigInteger.ONE)) {
            k = new BigInteger(bitLength, 10, random);
        }
        kInv = modInv(k, getModulus());
    }

    private BigInteger getModulus() {
        return publicKey.getModulus();
    }

    private int countResultLength(byte[] toSign) {
        int length = toSign.length;
        return length / blockSize == 0 ? blockSize : length % blockSize == 0 ? length : (length / blockSize) * blockSize + blockSize;
    }

    private byte[] doMaskOrUnmask(BigInteger exp, byte source[]) {
        byte[] result = new byte[countResultLength(source)];
        byte[] block = new byte[blockSize];
        int blocksDone = 0;
        for (int i = 0; i < source.length; i++) {
            if ((i != 0 && i % blockSize == 0) || i == source.length - 1) {
                if (i == source.length - 1) block[i % blockSize] = source[i];

                BigInteger a = new BigInteger(1, block);
                BigInteger maskedA = a.multiply(exp).mod(getModulus());
                byte[] src = maskedA.toByteArray();
                if (src.length > blockSize) {
                    int diff = src.length - blockSize;
                    System.arraycopy(src, diff, result, blocksDone * blockSize, src.length - diff);

                } else {
                    System.arraycopy(src, 0, result, blocksDone * blockSize + (blockSize - src.length), src.length);
                }
                Arrays.fill(block, (byte) 0);

                blocksDone++;
            }
            block[i % blockSize] = source[i];
        }
        return result;
    }

}
