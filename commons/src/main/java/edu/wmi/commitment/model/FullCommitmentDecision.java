package edu.wmi.commitment.model;

/**
 * Created by lupus on 10.11.16.
 */
public class FullCommitmentDecision {

    public byte[] r1;
    public byte[] r2;
    public byte[] decision;
    public byte[] hash;

    public byte[] getR1() {
        return r1;
    }

    public void setR1(byte[] r1) {
        this.r1 = r1;
    }

    public byte[] getR2() {
        return r2;
    }

    public void setR2(byte[] r2) {
        this.r2 = r2;
    }

    public byte[] getDecision() {
        return decision;
    }

    public void setDecision(byte[] decision) {
        this.decision = decision;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public void initialize(int len) {
        r1 = new byte[len];
        r2 = new byte[len];
        decision = new byte[len];
    }

    public CommitmentDecision createCommitmentDecision() {
        CommitmentDecision cd = new CommitmentDecision();
        cd.r1 = this.r1;
        cd.hash = this.hash;
        return cd;
    }
}
