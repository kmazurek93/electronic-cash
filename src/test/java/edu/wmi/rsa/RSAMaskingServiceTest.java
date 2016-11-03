package edu.wmi.rsa;

import org.junit.Before;
import org.junit.Test;

import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by lupus on 01.11.16.
 */
public class RSAMaskingServiceTest {
    private RSAMaskingService tested;
    private RSAService rsaService;
    public static final String WANDERING_MSG = "Wandering, Killing Urgently";

    @Before
    public void setUp() throws Exception {
        rsaService = new RSAService();
        this.tested = new RSAMaskingService(rsaService);
    }

    @Test
    public void shouldMaskMessageAndSignAndVerify() throws Exception {
        byte[] hash = Utils.generateSHA1(WANDERING_MSG.getBytes());
        byte[] masked = tested.bcMaskingOrUnmasking(hash, true);
        byte[] signedMasked = rsaService.signBlindly(masked);
        byte[] unmaskedSignature = tested.bcMaskingOrUnmasking(signedMasked, false);
        boolean actual = rsaService.verifyBlind(unmaskedSignature, hash);
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldMaskMessageAndSignAndVerifyRandomly() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[512];
        for (int i = 0; i < 100; i++) {
            secureRandom.nextBytes(randomBytes);
            byte[] hash = Utils.generateSHA1(randomBytes);
            byte[] masked = tested.bcMaskingOrUnmasking(hash, true);
            byte[] signedMasked = rsaService.signBlindly(masked);
            byte[] unmaskedSignature = tested.bcMaskingOrUnmasking(signedMasked, false);
            boolean actual = rsaService.verifyBlind(unmaskedSignature, hash);
            assertThat(actual).isTrue();
        }
    }


}