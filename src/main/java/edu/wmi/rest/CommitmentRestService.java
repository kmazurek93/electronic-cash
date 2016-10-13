package edu.wmi.rest;

import edu.wmi.commitment.CommitmentService;
import edu.wmi.commitment.model.DecisionMakingModel;
import edu.wmi.commitment.model.DecisionVerifyingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lupus on 06.10.16.
 */
@RestController
@CrossOrigin
public class CommitmentRestService {

    public static final String VERIFY_DECISION = "/verifyDecision";
    public static final String MAKE_DECISION = "/makeDecision";
    @Autowired
    private CommitmentService commitmentService;

    @RequestMapping(path = MAKE_DECISION, method = RequestMethod.POST)
    public void makeDecision(@RequestBody DecisionMakingModel model) {
        commitmentService.makeDecision(model.getHexR1(), model.getHexHash());
    }

    @RequestMapping(path = VERIFY_DECISION, method = RequestMethod.POST)
    public boolean verifyDecision(@RequestBody DecisionVerifyingModel model) throws IOException, NoSuchAlgorithmException {
        return commitmentService.verifyDecision(model.getHexR1(), model.getHexR2(), model.getHexByte(), model.getHexHash());
    }

}
