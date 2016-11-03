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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
            byte[] randomBytes = messageGenerator.getRandomBytes(512);
            System.out.println("Message: " + HexUtils.toHexString(randomBytes));
            byte[] hash = Utils.generateSHA1(randomBytes);
            System.out.println("Hash: " + HexUtils.toHexString(hash));
            byte[] maskedHash = rsaMaskingService.bcMaskingOrUnmasking(hash, true);
            System.out.println("Masked hash: " + HexUtils.toHexString(maskedHash));
            RestTemplate restTemplate = new RestTemplate();
            BlindSignature response = restTemplate.postForObject("http://localhost:8081/blindSign", new MaskedMessage(maskedHash), BlindSignature.class);
            byte[] sig = response.getSignature();
            System.out.println("Got sig: " + HexUtils.toHexString(sig));
            try {
                byte[] unmasked = rsaMaskingService.bcMaskingOrUnmasking(sig, false);
                System.out.println("Unmasked sig: " + HexUtils.toHexString(unmasked));
                boolean b = rsaService.verifyBlind(unmasked, hash);
                System.out.println("Verified positively: " + b);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    public MessageGenerator getMessageGenerator() {
        return new MessageGenerator();
    }
}
