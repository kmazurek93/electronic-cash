package edu.wmi.commitment.model;

/**
 * Created by lupus on 02.11.16.
 */
public class BlindSignature {
    private byte[] signature;

    public BlindSignature(byte[] signature) {
        this.signature = signature;
    }

    public BlindSignature() {
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
