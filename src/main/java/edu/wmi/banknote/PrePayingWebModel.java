package edu.wmi.banknote;

import edu.wmi.commitment.model.banknote.BanknoteModel;

/**
 * Created by lupus on 15.12.16.
 */
public class PrePayingWebModel {

    private BanknoteModel banknoteModel;
    private byte[] maskedSignature;


    public BanknoteModel getBanknoteModel() {
        return banknoteModel;
    }

    public void setBanknoteModel(BanknoteModel banknoteModel) {
        this.banknoteModel = banknoteModel;
    }

    public byte[] getMaskedSignature() {
        return maskedSignature;
    }

    public void setMaskedSignature(byte[] maskedSignature) {
        this.maskedSignature = maskedSignature;
    }
}
