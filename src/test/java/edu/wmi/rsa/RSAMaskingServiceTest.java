package edu.wmi.rsa;

import org.junit.Before;
import org.junit.Test;

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
        byte[] masked = tested.mask(hash);
        byte[] signedMasked = rsaService.signBlindly(masked);
        byte[] unmaskedSignature = tested.unMask(signedMasked);
        boolean actual = rsaService.verifyBlind(unmaskedSignature, hash);
        assertThat(actual).isTrue();


    }


}