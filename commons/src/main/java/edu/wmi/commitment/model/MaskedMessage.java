package edu.wmi.commitment.model;

/**
 * Created by lupus on 02.11.16.
 */
public class MaskedMessage {
    private byte[] maskedMessage;

    public byte[] getMaskedMessage() {
        return maskedMessage;
    }

    public void setMaskedMessage(byte[] maskedMessage) {
        this.maskedMessage = maskedMessage;
    }

    public MaskedMessage() {
    }

    public MaskedMessage(byte[] maskedMessage) {

        this.maskedMessage = maskedMessage;
    }
}
