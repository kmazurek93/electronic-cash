package edu.wmi.commitment;


import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lupus on 06.10.16.
 */
@Service
public class CommitmentService {

    private CommitmentMap commitmentMap = CommitmentMap.getInstance();

    public void makeDecision(String hexR1, String hexHash) {
        String s = commitmentMap.getR1ToHashMap().get(hexR1);
        if (s == null) {
            commitmentMap.getR1ToHashMap().put(hexR1, hexHash);
        } else {
            throw new RuntimeException("decision already made!!!");
        }
    }

    public boolean verifyDecision(String hexR1, String hexR2, String hexByte, String hexHash) throws IOException, NoSuchAlgorithmException {
        String hash = commitmentMap.getR1ToHashMap().get(hexR1);
        if (hash == null) {
            return false;
        }
        if(!hash.equals(hexHash)) {
            return false;
        }
        String allBytesString = StringUtils.join(hexR1, hexR2, hexByte);
        byte[] allBytes = HexUtils.fromHexString(allBytesString);
        MessageDigest md5encoder = MessageDigest.getInstance("md5");
        byte[] md5 = md5encoder.digest(allBytes);
        byte[] committedHash = HexUtils.fromHexString(hash);
        if (committedHash.length != md5.length) {
            return false;
        }
        for (int i = 0; i < md5.length; i++) {
            if (md5[i] != committedHash[i]) {
                return false;
            }
        }
        return true;
    }
}
