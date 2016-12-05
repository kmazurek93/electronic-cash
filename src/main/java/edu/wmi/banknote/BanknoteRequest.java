package edu.wmi.banknote;

import edu.wmi.commitment.model.banknote.ByteArray;

import java.util.List;

/**
 * Created by lupus on 01.12.16.
 */

public class BanknoteRequest {

    private byte[] userId;
    private List<ByteArray> maskedHashes;
    private Integer chosenOne;

    public Integer getChosenOne() {
        return chosenOne;
    }

    public void setChosenOne(Integer chosenOne) {
        this.chosenOne = chosenOne;
    }

    public byte[] getUserId() {
        return userId;
    }

    public void setUserId(byte[] userId) {
        this.userId = userId;
    }

    public List<ByteArray> getMaskedHashes() {
        return maskedHashes;
    }

    public void setMaskedHashes(List<ByteArray> maskedHashes) {
        this.maskedHashes = maskedHashes;
    }
}
