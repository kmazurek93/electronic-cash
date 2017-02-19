package edu.wmi.banknote;

import edu.wmi.commitment.model.CommitmentDecision;
import edu.wmi.commitment.model.FullCommitmentDecision;
import edu.wmi.rsa.RSAMaskingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static edu.wmi.banknote.BanknoteGenerator.xor;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.tomcat.util.buf.HexUtils.fromHexString;
import static org.apache.tomcat.util.buf.HexUtils.toHexString;

/**
 * Created by lupus on 01.12.16.
 */
@Component
public class BanknoteVerifyingService {

    private MessageDigest md5 = MessageDigest.getInstance("MD5");
    @Autowired
    private RSAMaskingService rsaMaskingService;

    public BanknoteVerifyingService() throws NoSuchAlgorithmException {
    }

    public boolean verifyValues(List<GeneratedBanknoteWithValues> list) {
        BigInteger value = list.get(0).getBanknoteModel().getValue();
        for (GeneratedBanknoteWithValues b : list) {
            if (!value.equals(b.getBanknoteModel().getValue())) {
                return false;
            }
        }
        return true;
    }

    public boolean verify(GeneratedBanknoteWithValues b, byte[] userId) {
        return verifyHash(b) && verifyDecisions(b) && verifyXor(b, userId) && verifyMasking(b);
    }


    private boolean verifyXor(GeneratedBanknoteWithValues b, byte[] userId) {
        String uid = toHexString(userId);
        for (int i = 0; i < 100; i++) {
            byte[] left = b.getLeftValues().get(i).value;
            byte[] right = b.getRightValues().get(i).value;
            String uid2 = toHexString(xor(left, right));
            if (!uid2.equals(uid)) {
                return false;
            }
        }
        return true;

    }

    private boolean verifyDecisions(GeneratedBanknoteWithValues b) {
        for (int i = 0; i < 100; i++) {
            FullCommitmentDecision leftFull = b.getLeftDecisions().get(i);
            FullCommitmentDecision rightFull = b.getRightDecisions().get(i);

            CommitmentDecision left = b.getBanknoteModel().getLeftDecisions().get(i);
            CommitmentDecision right = b.getBanknoteModel().getRightDecisions().get(i);
            if (!verifyFullDecision(leftFull, left)) return false;
            if (!verifyFullDecision(rightFull, right)) return false;
        }
        return true;
    }

    public boolean verifyFullDecision(FullCommitmentDecision full, CommitmentDecision partial) {
        if (!Arrays.equals(partial.r1, full.r1)) {
            return false;
        }
        if (!Arrays.equals(partial.hash, full.hash)) {
            return false;
        }
        String allBytes = join(toHexString(full.r1), toHexString(full.r2), toHexString(full.decision));
        byte[] digest = md5.digest(fromHexString(allBytes));
        return Arrays.equals(digest, full.hash);
    }

    private boolean verifyHash(GeneratedBanknoteWithValues b) {
        String hash = toHexString(b.getHash());
        byte[] bytes = b.getBanknoteModel().concatenateBytes();
        String madeHash = toHexString(md5.digest(bytes));
        return hash.equals(madeHash);
    }

    private boolean verifyMasking(GeneratedBanknoteWithValues b) {
        BigInteger blindingFactor = b.getBlindingFactor();
        byte[] maskedHash = b.getMaskedHash();
        byte[] hash = b.getHash();
        byte[] maskedByBankUsingBF = rsaMaskingService.maskWith(hash, blindingFactor);
        return Arrays.equals(maskedByBankUsingBF, maskedHash);
    }


}
