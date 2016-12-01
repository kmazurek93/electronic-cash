package edu.wmi.banknote;

import edu.wmi.commitment.model.CommitmentDecision;
import edu.wmi.commitment.model.FullCommitmentDecision;
import edu.wmi.commitment.model.banknote.BanknoteModel;
import edu.wmi.commitment.model.banknote.ByteArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.buf.HexUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by lupus on 10.11.16.
 */
public class BanknoteGenerator {
    public static final String MD_5 = "MD5";
    private SecureRandom secureRandom;
    private MessageDigest md5 = MessageDigest.getInstance(MD_5);

    public BanknoteGenerator() throws NoSuchAlgorithmException {
    }

    public GeneratedBanknoteWithValues generateBanknote(Long value, byte[] userId) {
        secureRandom = new SecureRandom();

        return generateSingleBanknote(value, userId);
    }

    private GeneratedBanknoteWithValues generateSingleBanknote(Long value, byte[] userId) {
        BigInteger banknoteValue = new BigInteger(value.toString());
        List<FullCommitmentDecision> leftDec = newArrayList();
        List<FullCommitmentDecision> rightDec = newArrayList();
        for (int i = 0; i < 100; i++) {
            List<FullCommitmentDecision> temp = generateLRCommitmentDecision(userId);
            leftDec.add(temp.get(0));
            rightDec.add(temp.get(1));
        }
        return createBanknote(banknoteValue, leftDec, rightDec);
    }

    private GeneratedBanknoteWithValues createBanknote(BigInteger banknoteValue,
                                                       List<FullCommitmentDecision> leftDec, List<FullCommitmentDecision> rightDec) {
        byte[] banknoteSerialNo = new byte[64];
        secureRandom.nextBytes(banknoteSerialNo);
        GeneratedBanknoteWithValues outcome = new GeneratedBanknoteWithValues();
        outcome.setLeftDecisions(leftDec);
        outcome.setRightDecisions(rightDec);
        outcome.setLeftValues(leftDec.stream().map(ld -> new ByteArray(ld.decision)).collect(Collectors.toList()));
        outcome.setRightValues(rightDec.stream().map(rd -> new ByteArray(rd.decision)).collect(Collectors.toList()));
        outcome.setBanknoteModel(createBanknoteModel(banknoteValue, leftDec, rightDec, banknoteSerialNo));
        return outcome;
    }

    private BanknoteModel createBanknoteModel(BigInteger banknoteValue, List<FullCommitmentDecision> leftDec, List<FullCommitmentDecision> rightDec, byte[] banknoteSerialNo) {
        BanknoteModel banknoteModel = new BanknoteModel();
        banknoteModel.setValue(banknoteValue);
        banknoteModel.setSerialNo(banknoteSerialNo);
        banknoteModel.setLeftDecisions(leftDec.stream().map(fcd -> {
            CommitmentDecision cd = new CommitmentDecision();
            cd.r1 = fcd.r1;
            cd.hash = fcd.hash;
            return cd;
        }).collect(Collectors.toList()));
        banknoteModel.setRightDecisions(rightDec.stream().map(fcd -> {
            CommitmentDecision cd = new CommitmentDecision();
            cd.r1 = fcd.r1;
            cd.hash = fcd.hash;
            return cd;
        }).collect(Collectors.toList()));
        return banknoteModel;
    }

    private byte[] xor(byte[] arr1, byte[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Not equal length");
        }
        byte[] output = new byte[arr1.length];
        int i = 0;
        for (byte b : arr1) {
            output[i] = (byte) (b ^ arr2[i++]);
        }
        return output;
    }

    private List<FullCommitmentDecision> generateLRCommitmentDecision(byte[] userId) {
        int len = userId.length;
        byte[] leftDecision = new byte[len];
        secureRandom.nextBytes(leftDecision);
        byte[] rightDecision = xor(leftDecision, userId);
        FullCommitmentDecision lcd = new FullCommitmentDecision();
        FullCommitmentDecision rcd = new FullCommitmentDecision();
        initializeRandomsAndHash(leftDecision, lcd, len);
        initializeRandomsAndHash(rightDecision, rcd, len);
        return newArrayList(lcd, rcd);
    }

    private void initializeRandomsAndHash(byte[] decision, FullCommitmentDecision model, int length) {
        model.initialize(length);
        secureRandom.nextBytes(model.r1);
        secureRandom.nextBytes(model.r2);
        model.decision = decision;
        byte[] toHash = getBytesToHash(model);
        model.hash = md5.digest(toHash);
    }

    private byte[] getBytesToHash(FullCommitmentDecision model) {
        return HexUtils.fromHexString(
                StringUtils.join(HexUtils.toHexString(model.r1),
                        HexUtils.toHexString(model.r2),
                        HexUtils.toHexString(model.decision)));
    }
}
