package edu.wmi.banknote;

import edu.wmi.commitment.model.banknote.BanknoteModel;
import edu.wmi.commitment.model.banknote.ByteArray;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.System.out;

/**
 * Created by lupus on 15.12.16.
 */
@RestController
@CrossOrigin
@Profile("merlin")
public class ShopApi {

    private SecureRandom secureRandom = new SecureRandom();
    private MessageDigest md5 = MessageDigest.getInstance("MD5");
    private Map<String, List<Integer>> map = newHashMap();
    private Map<String, CommittedBanknote> banknoteMap = newHashMap();

    @Autowired
    private BanknoteVerifyingService service;

    public ShopApi() throws NoSuchAlgorithmException {
    }

    @RequestMapping(method = RequestMethod.GET, value = "/prePay")
    public PayToken prePayWithBanknote() {
        List<Integer> integers = newArrayList();
        PayToken payToken = new PayToken();
        byte[] token = new byte[64];
        secureRandom.nextBytes(token);
        for (int i = 0; i < 100; i++) {
            integers.add(secureRandom.nextInt() % 2);
        }
        payToken.setToken(HexUtils.toHexString(token));
        payToken.setIntegers(integers);
        map.put(payToken.getToken(), integers);
        return payToken;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/pay")
    public Boolean payWithBanknote(@RequestBody PayWebModel payWebModel) {
        String token = payWebModel.getToken();
        List<Integer> integers = map.get(token);
        if (integers == null) {
            out.println("No entry in token map!");
            return false;
        }

        BanknoteModel banknoteModel = payWebModel.getBanknoteModel();
        if(banknoteMap.get(getBanknoteId(banknoteModel)) != null) {
            out.println("Banknote exists!");
            return false;
        }
        if (isInvalid(payWebModel, integers, banknoteModel)) return false;
        CommittedBanknote committedBanknote = new CommittedBanknote();
        committedBanknote.setIntegers(integers);
        committedBanknote.setFullCommitmentDecisions(payWebModel.getFullDecisions());
        committedBanknote.setBanknoteModel(banknoteModel);
        banknoteMap.put(getBanknoteId(banknoteModel), committedBanknote);
        RestTemplate restTemplate = new RestTemplate();
        Integer integer = restTemplate.postForObject("http://localhost:8081/giveMeCash", committedBanknote, Integer.class);
        System.out.println("GOT CASH: " + integer);
        integer = restTemplate.postForObject("http://localhost:8081/giveMeCash", committedBanknote, Integer.class);
        System.out.println("GOT CASH: " + integer);

        return true;
    }

    public boolean isInvalid(PayWebModel payWebModel, List<Integer> integers, BanknoteModel banknoteModel) {
        ByteArray signature = new ByteArray(banknoteModel.getSignature());
        ByteArray hash = new ByteArray(md5.digest(banknoteModel.concatenateBytes()));
        RestTemplate restTemplate = new RestTemplate();
        Boolean isSigvalid = restTemplate.postForObject("http://localhost:8081/verifySignature",
                of("SIGNATURE", signature, "HASH", hash),
                Boolean.class);

        if (!isSigvalid) {
            out.println("Signature invalid");
            return true;
        }

        for (int i = 0; i < 100; i++) {
            if (integers.get(i).equals(0)) {
                if (!service.verifyFullDecision(payWebModel.getFullDecisionAt(i), payWebModel.getLeftAt(i))) {
                    out.println("Decision " + i + " is invalid");
                    return true;
                }
            } else {
                if (!service.verifyFullDecision(payWebModel.getFullDecisionAt(i), payWebModel.getRightAt(i))) {
                    out.println("Decision " + i + " is invalid");
                    return true;
                }
            }
        }
        out.println("Banknote with id " + getBanknoteId(banknoteModel) + " is valid");
        return false;
    }

    private String getBanknoteId(BanknoteModel banknoteModel) {
        return HexUtils.toHexString(banknoteModel.getSerialNo());
    }
}
