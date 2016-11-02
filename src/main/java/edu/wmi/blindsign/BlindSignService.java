package edu.wmi.blindsign;

import edu.wmi.commitment.model.BlindSignature;
import edu.wmi.commitment.model.MaskedMessage;
import edu.wmi.rsa.RSAService;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;

/**
 * Created by lupus on 02.11.16.
 */
@Service
public class BlindSignService {

    private final RSAService rsaService;

    @Autowired
    public BlindSignService(RSAService rsaService) {
        this.rsaService = rsaService;
    }

    public BlindSignature signBlindly(MaskedMessage maskedMessage) {
        byte[] hexBytes = maskedMessage.getMaskedMessage();
        System.out.println("Got message: " + HexUtils.toHexString(hexBytes));
        try {
            byte[] signature = rsaService.signBlindly(hexBytes);
            BlindSignature blindSignature = new BlindSignature();
            blindSignature.setSignature(signature);
            System.out.println("Sending signature: " + HexUtils.toHexString(signature));
            return blindSignature;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Cannot sign blindly lol");
        }
    }
}
