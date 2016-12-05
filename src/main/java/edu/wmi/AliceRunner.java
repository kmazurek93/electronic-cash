package edu.wmi;

import edu.wmi.banknote.BanknoteFullRequest;
import edu.wmi.banknote.BanknoteGenerator;
import edu.wmi.banknote.BanknoteRequest;
import edu.wmi.banknote.GeneratedBanknoteWithValues;
import edu.wmi.blindsign.MessageGenerator;
import edu.wmi.commitment.model.banknote.ByteArray;
import edu.wmi.rsa.RSAMaskingService;
import edu.wmi.rsa.RSAService;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.out;
import static org.apache.tomcat.util.buf.HexUtils.toHexString;

/**
 * Created by lupus on 02.11.16.
 */
@SpringBootApplication
@EnableAutoConfiguration
@Profile("alice")
public class AliceRunner {

    public static final String BLINDSIGN = "http://localhost:8081/blindSign";
    public static final String BR = "http://localhost:8081/banknoteRequest";
    public static final String BR_2 = "http://localhost:8081/banknoteRequestTwo";

    public static final Long BANKNOTE_VALUE = 1000L;
    public static final String ALICE_ID = "428ef77c36437ecec7eb1f0a53998c36d1c2c3aa349bc25072b5ae61d657e0" +
            "28071225c431db7277bad31822e36023c10566dd22dcceaf35349e5c0e792e984b";

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(AliceRunner.class, args);
        RSAMaskingService rsaMaskingService = ctx.getBean(RSAMaskingService.class);
        MessageGenerator messageGenerator = ctx.getBean(MessageGenerator.class);
        RSAService rsaService = ctx.getBean(RSAService.class);
        BanknoteGenerator banknoteGenerator = ctx.getBean(BanknoteGenerator.class);
        byte[] aliceId = HexUtils.fromHexString(ALICE_ID);
        List<GeneratedBanknoteWithValues> banknotes = newArrayList();
        for (int i = 0; i < 100; i++) {
            banknotes.add(banknoteGenerator.generateBanknote(BANKNOTE_VALUE, aliceId));
        }
        banknotes.forEach(rsaMaskingService::maskBanknoteHash);
        List<ByteArray> hashes = banknotes.stream().map(o -> new ByteArray(o.getMaskedHash())).collect(Collectors.toList());
        BanknoteRequest banknoteRequest = new BanknoteRequest();
        banknoteRequest.setUserId(aliceId);
        banknoteRequest.setMaskedHashes(hashes);
        RestTemplate restTemplate = new RestTemplate();
        Integer toRemove = restTemplate.postForObject(BR, banknoteRequest, Integer.class);
        out.println("Bank agreed to sign banknote no: " + toRemove + "if all ok");
        GeneratedBanknoteWithValues toSign = banknotes.remove(toRemove.intValue());
        BanknoteFullRequest banknoteFullRequest = new BanknoteFullRequest();
        banknoteFullRequest.setUserId(aliceId);
        banknoteFullRequest.setNinetyNineBanknotes(banknotes);
        ByteArray byteArray = restTemplate.postForObject(BR_2, banknoteFullRequest, ByteArray.class);

        if (byteArray == null) {
            out.println("ERROR");
        } else {
            byte[] signature = rsaMaskingService.unmaskHash(toSign.getBlindingFactor(), byteArray.value);
            out.println("Signature: " + toHexString(signature));
            toSign.getBanknoteModel().setSignature(signature);
        }
        Boolean value = restTemplate.postForObject("http://localhost:8081/verifySignature",
                of(
                        "SIGNATURE", new ByteArray(toSign.getBanknoteModel().getSignature()),
                        "HASH", new ByteArray(toSign.getHash())),
                Boolean.class);
        out.println("Signature valid: " + value);
    }

    @Bean
    public MessageGenerator getMessageGenerator() {
        return new MessageGenerator();
    }
}
