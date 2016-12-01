package edu.wmi.commitment.model;

/**
 * Created by lupus on 06.10.16.
 */
public class CommitmentDecision {

    public byte[] r1;
    public byte[] hash;

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getR1() {
        return r1;
    }

    public void setR1(byte[] r1) {
        this.r1 = r1;
    }

}
