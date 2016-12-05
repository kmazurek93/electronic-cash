package edu.wmi.banknote;

import edu.wmi.commitment.model.FullCommitmentDecision;
import edu.wmi.commitment.model.banknote.BanknoteModel;
import edu.wmi.commitment.model.banknote.ByteArray;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by lupus on 10.11.16.
 */
public class GeneratedBanknoteWithValues {
    private BanknoteModel banknoteModel;
    private List<ByteArray> leftValues;
    private List<ByteArray> rightValues;
    private List<FullCommitmentDecision> leftDecisions;
    private List<FullCommitmentDecision> rightDecisions;
    private byte[] hash;
    private byte[] maskedHash;
    private BigInteger blindingFactor;

    public BigInteger getBlindingFactor() {
        return blindingFactor;
    }

    public void setBlindingFactor(BigInteger blindingFactor) {
        this.blindingFactor = blindingFactor;
    }

    public BanknoteModel getBanknoteModel() {
        return banknoteModel;
    }

    public void setBanknoteModel(BanknoteModel banknoteModel) {
        this.banknoteModel = banknoteModel;
    }

    public List<ByteArray> getLeftValues() {
        return leftValues;
    }

    public void setLeftValues(List<ByteArray> leftValues) {
        this.leftValues = leftValues;
    }

    public List<ByteArray> getRightValues() {
        return rightValues;
    }

    public void setRightValues(List<ByteArray> rightValues) {
        this.rightValues = rightValues;
    }

    public List<FullCommitmentDecision> getLeftDecisions() {
        return leftDecisions;
    }

    public void setLeftDecisions(List<FullCommitmentDecision> leftDecisions) {
        this.leftDecisions = leftDecisions;
    }

    public List<FullCommitmentDecision> getRightDecisions() {
        return rightDecisions;
    }

    public void setRightDecisions(List<FullCommitmentDecision> rightDecisions) {
        this.rightDecisions = rightDecisions;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getMaskedHash() {
        return maskedHash;
    }

    public void setMaskedHash(byte[] maskedHash) {
        this.maskedHash = maskedHash;
    }
}
