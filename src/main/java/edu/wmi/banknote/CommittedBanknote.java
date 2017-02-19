package edu.wmi.banknote;

import edu.wmi.commitment.model.FullCommitmentDecision;
import edu.wmi.commitment.model.banknote.BanknoteModel;

import java.util.List;

/**
 * Created by lupus on 15.12.16.
 */
public class CommittedBanknote {

    BanknoteModel banknoteModel;
    List<Integer> integers;
    List<FullCommitmentDecision> fullCommitmentDecisions;

    public BanknoteModel getBanknoteModel() {
        return banknoteModel;
    }

    public void setBanknoteModel(BanknoteModel banknoteModel) {
        this.banknoteModel = banknoteModel;
    }

    public List<Integer> getIntegers() {
        return integers;
    }

    public void setIntegers(List<Integer> integers) {
        this.integers = integers;
    }

    public List<FullCommitmentDecision> getFullCommitmentDecisions() {
        return fullCommitmentDecisions;
    }

    public void setFullCommitmentDecisions(List<FullCommitmentDecision> fullCommitmentDecisions) {
        this.fullCommitmentDecisions = fullCommitmentDecisions;
    }
}
