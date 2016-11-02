package edu.wmi.rest;

import edu.wmi.blindsign.BlindSignService;
import edu.wmi.commitment.model.BlindSignature;
import edu.wmi.commitment.model.MaskedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lupus on 01.11.16.
 */
@RestController
@CrossOrigin
@Profile("bob")
public class BlindSignRestService {

    private final BlindSignService blindSignService;

    @Autowired
    public BlindSignRestService(BlindSignService blindSignService) {
        this.blindSignService = blindSignService;
    }


    @RequestMapping(path = "/blindSign", method = RequestMethod.POST)
    public BlindSignature signBlindly(@RequestBody MaskedMessage maskedMessage) {
        return blindSignService.signBlindly(maskedMessage);
    }

}
