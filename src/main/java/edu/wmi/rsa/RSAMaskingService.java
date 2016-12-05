package edu.wmi.rsa;

import edu.wmi.banknote.GeneratedBanknoteWithValues;
import org.bouncycastle.crypto.engines.RSABlindingEngine;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * Created by lupus on 01.11.16.
 */
@Component
public class RSAMaskingService {

    private final RSAService rsaService;
    private RSABlindingParameters rsaBlindingParameters;

    @Autowired
    public RSAMaskingService(RSAService rsaService) {
        this.rsaService = rsaService;
    }


    public byte[] bcMaskingOrUnmasking(byte[] source, boolean isMasking) {
        RSAKeyParameters bcVerifyKey = rsaService.getBCVerifyKey();
        RSABlindingEngine rsaBlindingEngine = new RSABlindingEngine();
        if (isMasking) {
            generateBlindingFactor(bcVerifyKey);
        }
        rsaBlindingEngine.init(isMasking, rsaBlindingParameters);
        return rsaBlindingEngine.processBlock(source, 0, source.length);
    }
    public void maskBanknoteHash(GeneratedBanknoteWithValues banknote) {
        RSAKeyParameters bcVerifyKey = rsaService.getBCVerifyKey();
        RSABlindingEngine rsaBlindingEngine = new RSABlindingEngine();
        RSABlindingFactorGenerator blindingFactorGenerator = new RSABlindingFactorGenerator();
        blindingFactorGenerator.init(bcVerifyKey);
        BigInteger blindingFactor = blindingFactorGenerator.generateBlindingFactor();
        RSABlindingParameters blindingParameters = new RSABlindingParameters(bcVerifyKey, blindingFactor);
        rsaBlindingEngine.init(true, blindingParameters);
        byte[] hash = banknote.getHash();
        banknote.setBlindingFactor(blindingParameters.getBlindingFactor());
        banknote.setMaskedHash(rsaBlindingEngine.processBlock(hash, 0, hash.length));
    }
    public byte[] unmaskHash(BigInteger rsaBlindingFactor, byte[] source) {
        RSAKeyParameters bcVerifyKey = rsaService.getBCVerifyKey();
        RSABlindingEngine rsaBlindingEngine = new RSABlindingEngine();
        RSABlindingParameters blindingParameters = new RSABlindingParameters(bcVerifyKey, rsaBlindingFactor);
        rsaBlindingEngine.init(false, blindingParameters);
        return rsaBlindingEngine.processBlock(source, 0, source.length);
    }
    private void generateBlindingFactor(RSAKeyParameters bcVerifyKey) {
        RSABlindingFactorGenerator blindingFactorGenerator = new RSABlindingFactorGenerator();
        blindingFactorGenerator.init(bcVerifyKey);
        BigInteger blindingFactor = blindingFactorGenerator.generateBlindingFactor();
        this.rsaBlindingParameters = new RSABlindingParameters(bcVerifyKey, blindingFactor);
    }
}
