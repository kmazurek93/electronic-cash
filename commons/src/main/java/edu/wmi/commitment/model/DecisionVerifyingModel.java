package edu.wmi.commitment.model;

/**
 * Created by lupus on 06.10.16.
 */
public class DecisionVerifyingModel {
    private String hexR1;
    private String hexR2;
    private String hexByte;
    private String hexHash;

    public String getHexHash() {
        return hexHash;
    }

    public void setHexHash(String hexHash) {
        this.hexHash = hexHash;
    }

    public String getHexR1() {
        return hexR1;
    }

    public void setHexR1(String hexR1) {
        this.hexR1 = hexR1;
    }

    public String getHexR2() {
        return hexR2;
    }

    public void setHexR2(String hexR2) {
        this.hexR2 = hexR2;
    }

    public String getHexByte() {
        return hexByte;
    }

    public void setHexByte(String hexByte) {
        this.hexByte = hexByte;
    }

}
