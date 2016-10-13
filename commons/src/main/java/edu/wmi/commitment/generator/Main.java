package edu.wmi.commitment.generator;

import org.apache.commons.codec.binary.Hex;

import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Created by lupus on 13.10.16.
 */
public class Main {
    private static CommitmentGenerator generator = new CommitmentGenerator();

    public static void main(String[] args) throws NoSuchAlgorithmException {
        int r1Len, r2Len;
        byte[] bits = new byte[1];
        byte[] r1, r2;
        String digest;
        Scanner sc = new Scanner(System.in);
        System.out.println("Length of r1 in bytes: [16-32]");
        r1Len = sc.nextInt();
        System.out.println("Length of r2 in bytes: [16-32]");
        r2Len = sc.nextInt();
        System.out.println("Decision [0-255]; even -> 2k, odd -> 2k+1");
        int dec = sc.nextInt();
        bits[0] = (byte) dec;
        r1 = generator.generateRandomBytes(r1Len);
        r2 = generator.generateRandomBytes(r2Len);
        System.out.println("r1 " + Hex.encodeHexString(r1));
        System.out.println("r2 " + Hex.encodeHexString(r2));
        System.out.println("decision: " + Hex.encodeHexString(bits));
        digest = generator.generateHash(r1, r2, bits);
        System.out.println("Hash: " + digest);
    }
}
