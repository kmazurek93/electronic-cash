package edu.wmi.banknote;

import edu.wmi.commitment.model.banknote.BanknoteModel;
import edu.wmi.commitment.model.banknote.ByteArray;
import edu.wmi.rsa.RSAService;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.System.out;
import static org.apache.tomcat.util.buf.HexUtils.toHexString;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by lupus on 01.12.16.
 */
@RestController
@CrossOrigin
@Profile("bob")
public class BankApi {

    public static final String INVALID_BANKNOTE_DETECTED = "INVALID BANKNOTE DETECTED.";
    public static final String NO_BANKNOTES = "NO BANKNOTES";
    private Map<String, BanknoteRequest> map = newHashMap();
    private List<String> alreadyUsedBanknoteIds = newArrayList();
    @Autowired
    private BanknoteVerifyingService banknoteVerifyingService;
    @Autowired
    private RSAService rsaService;

    @RequestMapping(value = "/banknoteRequest", method = POST)
    public Integer requestBanknote(@RequestBody BanknoteRequest banknoteRequest) {
        out.println("Got request");
        String id = toHexString(banknoteRequest.getUserId());
        int i = new SecureRandom().nextInt() % 100;
        if (i < 0) i = i * (-1);
        banknoteRequest.setChosenOne(i);
        map.put(id, banknoteRequest);
        return i;
    }


    @RequestMapping(value = "/banknoteRequestTwo", method = POST)
    public ByteArray checkBanknotesAndReturnSignedOne(@RequestBody BanknoteFullRequest request) {
        out.println("checking 99 banknotes");
        String uid = toHexString(request.getUserId());
        BanknoteRequest banknoteRequest = map.get(uid);
        if (banknoteRequest == null) {
            out.println(NO_BANKNOTES);
            return null;
        }

        List<Boolean> booleans = request
                .getNinetyNineBanknotes().stream()
                .map(b ->
                        banknoteVerifyingService.verify(b, request.getUserId()))
                .collect(Collectors.toList());
        for (int i = 0; i < 99; i++) {
            out.println("Banknote " + i + " valid:" + booleans.get(i));
        }
        if (booleans.contains(false)) {
            out.println(INVALID_BANKNOTE_DETECTED);
            return null;
        }
        if (!banknoteVerifyingService.verifyValues(request.getNinetyNineBanknotes())) {
            out.println(INVALID_BANKNOTE_DETECTED);
            return null;
        }
        Integer chosenOne = banknoteRequest.getChosenOne();
        try {
            map.remove(uid);
            return new ByteArray(rsaService.signBlindly(banknoteRequest.getMaskedHashes().get(chosenOne).value));
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    @RequestMapping(value = "/verifySignature", method = POST)
    public Boolean isSignatureValid(@RequestBody Map<String, ByteArray> hashAndSig) {
        return rsaService.verifyBlind(hashAndSig.get("SIGNATURE").value, hashAndSig.get("HASH").value);
    }

    @RequestMapping(value = "/giveMeCash", method = POST)
    public Integer getCash(@RequestBody CommittedBanknote committedBanknote) throws NoSuchAlgorithmException {
        String id = HexUtils.toHexString(committedBanknote.getBanknoteModel().getSerialNo());
        if(alreadyUsedBanknoteIds.contains(id)) {
            out.println("Banknote USED!");
            return 0;
        }
        if(isValid(committedBanknote)) {
            alreadyUsedBanknoteIds.add(id);
            out.println("PAYING OUT CASH!");
            return committedBanknote.getBanknoteModel().getValue().intValue();
        }
        out.println("banknote invalid");
        return 0;
    }

    private boolean isValid(CommittedBanknote banknote) throws NoSuchAlgorithmException {
        BanknoteModel banknoteModel = banknote.getBanknoteModel();
        ByteArray signature = new ByteArray(banknoteModel.getSignature());
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        ByteArray hash = new ByteArray(md5.digest(banknoteModel.concatenateBytes()));
        RestTemplate restTemplate = new RestTemplate();
        Boolean isSigvalid = restTemplate.postForObject("http://localhost:8081/verifySignature",
                of("SIGNATURE", signature, "HASH", hash),
                Boolean.class);

        if (!isSigvalid) {
            out.println("Signature invalid");
            return false;
        }

        for (int i = 0; i < 100; i++) {
            if (banknote.getIntegers().get(i).equals(0)) {
                if (!banknoteVerifyingService.verifyFullDecision(banknote.getFullCommitmentDecisions().get(i), banknote.getBanknoteModel().getLeftDecisions().get(i))) {
                    out.println("Decision " + i + " is invalid");
                    return false;
                }
            } else {
                if (!banknoteVerifyingService.verifyFullDecision(banknote.getFullCommitmentDecisions().get(i), banknote.getBanknoteModel().getRightDecisions().get(i))) {
                    out.println("Decision " + i + " is invalid");
                    return false;
                }
            }
        }
        out.println("Banknote with id " + getBanknoteId(banknoteModel) + " is valid");
        return true;
    }

    private String getBanknoteId(BanknoteModel banknoteModel) {

        return HexUtils.toHexString(banknoteModel.getSerialNo());
    }
}

