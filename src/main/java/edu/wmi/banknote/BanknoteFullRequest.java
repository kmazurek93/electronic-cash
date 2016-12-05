package edu.wmi.banknote;

import java.util.List;

/**
 * Created by lupus on 01.12.16.
 */
public class BanknoteFullRequest {
    private List<GeneratedBanknoteWithValues> ninetyNineBanknotes;
    byte[] userId;

    public List<GeneratedBanknoteWithValues> getNinetyNineBanknotes() {
        return ninetyNineBanknotes;
    }

    public void setNinetyNineBanknotes(List<GeneratedBanknoteWithValues> ninetyNineBanknotes) {
        this.ninetyNineBanknotes = ninetyNineBanknotes;
    }

    public byte[] getUserId() {
        return userId;
    }

    public void setUserId(byte[] userId) {
        this.userId = userId;
    }
}
