package edu.wmi.rsa;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by lupus on 01.11.16.
 */
public class RSAMaskTest {
    private RSAMask tested;
    private RSAService rsaService;
    public static final String WANDERING_MSG = "Wandering, Killing Urgently";

    @Before
    public void setUp() throws Exception {
        rsaService = new RSAService();
        this.tested = new RSAMask(rsaService);
    }

    @Test
    public void shouldMaskMessageAndSignAndVerify() throws Exception {
        byte[] masked = tested.mask(WANDERING_MSG.getBytes());
        byte[] signedMasked = rsaService.signBlindly(masked);
        byte[] unmaskedSignature = tested.unMask(signedMasked);
        boolean actual = rsaService.verifyBlind(unmaskedSignature, WANDERING_MSG.getBytes());
        assertThat(actual).isTrue();


    }



}