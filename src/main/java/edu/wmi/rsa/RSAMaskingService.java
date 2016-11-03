package edu.wmi.rsa;

import org.bouncycastle.crypto.engines.RSABlindingEngine;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * Created by lupus on 01.11.16.
 */
@Component
@Profile("alice")
public class RSAMaskingService {

    private final RSAService rsaService;
    private RSABlindingParameters rsaBlindingParameters;

    @Autowired
    public RSAMaskingService(RSAService rsaService) {
        this.rsaService = rsaService;
    }


    public byte[] bcMaskingOrUnmasking(byte[] source, boolean masking) {
        RSAKeyParameters bcVerifyKey = rsaService.createBCVerifyKey();
        RSABlindingEngine rsaBlindingEngine = new RSABlindingEngine();
        if (masking) {
            generateBlindingFactor(bcVerifyKey);
        }
        rsaBlindingEngine.init(masking, rsaBlindingParameters);
        return rsaBlindingEngine.processBlock(source, 0, source.length);
    }

    private void generateBlindingFactor(RSAKeyParameters bcVerifyKey) {
        RSABlindingFactorGenerator blindingFactorGenerator = new RSABlindingFactorGenerator();
        blindingFactorGenerator.init(bcVerifyKey);
        BigInteger blindingFactor = blindingFactorGenerator.generateBlindingFactor();
        this.rsaBlindingParameters = new RSABlindingParameters(bcVerifyKey, blindingFactor);
    }
}
