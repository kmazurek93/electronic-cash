package edu.wmi.rsa;

import org.junit.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by lupus on 01.11.16.
 */
public class RSAServiceTest {

    public static final String POSSIBLY_MSG = "Possibly Irrational Opened Transaction Ready";
    public static final String WANDERING_MSG = "Wandering, Killing Urgently, Randomly Wiping Intruders Anonymously";
    private RSAService tested = new RSAService();

    private BigInteger k;
    private BigInteger kInv;

    @Test
    public void shouldEncryptAndDecryptProperly() throws Exception {
        byte[] bytes = WANDERING_MSG.getBytes();
        byte[] ciphertext = tested.encrypt(bytes);
        byte[] message = tested.decrypt(ciphertext);
        String decoded = new String(message);
        assertThat(decoded).isEqualTo(WANDERING_MSG);
    }

    @Test
    public void shouldSignAndVerifyPositively() throws Exception {
        byte[] message = POSSIBLY_MSG.getBytes();
        byte[] signature = tested.sign(message);
        boolean actual = tested.verify(signature, message);
        assertThat(actual).isTrue();
    }



}