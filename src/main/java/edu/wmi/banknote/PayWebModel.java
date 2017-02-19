package edu.wmi.banknote;

import edu.wmi.commitment.model.CommitmentDecision;
import edu.wmi.commitment.model.FullCommitmentDecision;
import edu.wmi.commitment.model.banknote.BanknoteModel;

import java.util.List;

/**
 * Created by lupus on 15.12.16.
 */
public class PayWebModel {

    private BanknoteModel banknoteModel;
    private List<FullCommitmentDecision> fullDecisions;
    private String token;

    public BanknoteModel getBanknoteModel() {
        return banknoteModel;
    }

    public void setBanknoteModel(BanknoteModel banknoteModel) {
        this.banknoteModel = banknoteModel;
    }

    public List<FullCommitmentDecision> getFullDecisions() {
        return fullDecisions;
    }

    public void setFullDecisions(List<FullCommitmentDecision> fullDecisions) {
        this.fullDecisions = fullDecisions;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public FullCommitmentDecision getFullDecisionAt(int i) {
        return fullDecisions.get(i);
    }

    public CommitmentDecision getLeftAt(int i) {
        return banknoteModel.getLeftDecisions().get(i);
    }
    public CommitmentDecision getRightAt(int i) {
        return banknoteModel.getRightDecisions().get(i);
    }
}
