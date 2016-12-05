package edu.wmi.rsa;

import edu.wmi.banknote.BanknoteGenerator;
import edu.wmi.banknote.GeneratedBanknoteWithValues;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by lupus on 05.12.16.
 */
public class BanknoteGeneratorTest {
    private RSAMaskingService rsaMaskingService;
    private RSAService rsaService;
    private BanknoteGenerator tested;
    public static final String WANDERING_MSG = "Wandering, Killing Urgently";

    @Before
    public void setUp() throws Exception {
        rsaService = new RSAService();
        this.rsaMaskingService = new RSAMaskingService(rsaService);
        tested = new BanknoteGenerator();
    }

    @Test
    public void shittyTest() {
        List<GeneratedBanknoteWithValues> banknoteWithValuesList = newArrayList();
        Set<BigInteger> blindingFactors;
        SecureRandom secureRandom = new SecureRandom();
        byte[] aliceId = new byte[64];
        secureRandom.nextBytes(aliceId);
        for(int i = 0 ; i<100; i++) {
            banknoteWithValuesList.add(tested.generateBanknote(1000L, aliceId));
        }
        banknoteWithValuesList.forEach(rsaMaskingService::maskBanknoteHash);
        blindingFactors = banknoteWithValuesList.stream().map(GeneratedBanknoteWithValues::getBlindingFactor).collect(Collectors.toSet());
        assertThat(blindingFactors).hasSize(100);
        blindingFactors.forEach(System.out::println);

        banknoteWithValuesList.forEach(b -> {
            byte[] hash = b.getHash();
            byte[] maskedHash = b.getMaskedHash();
            byte[] bytes = rsaMaskingService.unmaskHash(b.getBlindingFactor(), maskedHash);
            assertThat(bytes).isEqualTo(hash);
        });
    }

}
