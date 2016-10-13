package edu.wmi.commitment.model;

/**
 * Created by lupus on 06.10.16.
 */
public class CommitmentDecision {

    private byte[] r1bytes;
    private byte[] hash;

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getR1bytes() {
        return r1bytes;
    }

    public void setR1bytes(byte[] r1bytes) {
        this.r1bytes = r1bytes;
    }

}
