package edu.wmi;

import edu.wmi.blindsign.MessageGenerator;
import edu.wmi.commitment.model.BlindSignature;
import edu.wmi.commitment.model.MaskedMessage;
import edu.wmi.rsa.RSAMaskingService;
import edu.wmi.rsa.RSAService;
import edu.wmi.rsa.Utils;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import java.security.GeneralSecurityException;

/**
 * Created by lupus on 02.11.16.
 */
@SpringBootApplication
@EnableAutoConfiguration
@Profile("alice")
public class AliceRunner {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(AliceRunner.class, args);
        RSAMaskingService rsaMaskingService = ctx.getBean(RSAMaskingService.class);
        MessageGenerator messageGenerator = ctx.getBean(MessageGenerator.class);
        RSAService rsaService = ctx.getBean(RSAService.class);
        for (int i = 0; i < 10; i++) {
            byte[] randomBytes = messageGenerator.getRandomBytes(72);
            System.out.println("Message: " + HexUtils.toHexString(randomBytes));
            byte[] hash = Utils.generateSHA1(randomBytes);
            byte[] maskedHash = rsaMaskingService.mask(hash);
            System.out.println("Hash: " + HexUtils.toHexString(hash));
            String maskedHashString = HexUtils.toHexString(maskedHash);
            System.out.println("Masked hash: " + maskedHashString);
            MaskedMessage model = new MaskedMessage(hash);

            RestTemplate restTemplate = new RestTemplate();
            BlindSignature blindSignature = restTemplate.postForEntity("http://localhost:8081/blindSign", model, BlindSignature.class).getBody();

            byte[] signature = blindSignature.getSignature();
            System.out.println("Signature: " + HexUtils.toHexString(signature));
            byte[] unmaskedSignature = rsaMaskingService.unMask(signature);
            System.out.println("Unmasked signature: " + HexUtils.toHexString(unmaskedSignature));
            try {
                boolean valid = rsaService.verifyBlind(unmaskedSignature, hash);
                System.out.println("Verified: " + valid);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    public MessageGenerator getMessageGenerator() {
        return new MessageGenerator();
    }
}
