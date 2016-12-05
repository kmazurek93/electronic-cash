package edu.wmi.banknote;

import edu.wmi.commitment.model.banknote.ByteArray;
import edu.wmi.rsa.RSAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private Map<String, BanknoteRequest> map = newHashMap();
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
            return null;
        }
        List<Boolean> booleans = request
                .getNinetyNineBanknotes().stream()
                .map(b ->
                        banknoteVerifyingService.verify(b, request.getUserId()))
                .collect(Collectors.toList());
        for (int i = 0; i < 99; i++) {
            out.println("Banknote " + i + " is " + booleans.get(i));
        }
        if (booleans.contains(false)) {
            return null;
        }
        if (!banknoteVerifyingService.verifyValues(request.getNinetyNineBanknotes())) {
            return null;
        }
        Integer chosenOne = banknoteRequest.getChosenOne();
        try {
            return new ByteArray(rsaService.signBlindly(banknoteRequest.getMaskedHashes().get(chosenOne).value));
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    @RequestMapping(value = "/verifySignature", method = POST)
    public Boolean isSignatureValid(@RequestBody Map<String, ByteArray> hashAndSig) {
        return rsaService.verifyBlind(hashAndSig.get("SIGNATURE").value, hashAndSig.get("MESSAGE").value);
    }
}

