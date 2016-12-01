package edu.wmi.commitment.model.banknote;

import edu.wmi.commitment.model.CommitmentDecision;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by lupus on 03.11.16.
 */
public class BanknoteModel {

    private byte[] serialNo;
    private BigInteger value;
    private List<CommitmentDecision> leftDecisions;
    private List<CommitmentDecision> rightDecisions;

    public byte[] getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(byte[] serialNo) {
        this.serialNo = serialNo;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public List<CommitmentDecision> getLeftDecisions() {
        return leftDecisions;
    }

    public void setLeftDecisions(List<CommitmentDecision> leftDecisions) {
        this.leftDecisions = leftDecisions;
    }

    public List<CommitmentDecision> getRightDecisions() {
        return rightDecisions;
    }

    public void setRightDecisions(List<CommitmentDecision> rightDecisions) {
        this.rightDecisions = rightDecisions;
    }

    public byte[] concatenateBytes() {
        int length = serialNo.length + value.toByteArray().length;
        for (CommitmentDecision commitmentDecision : leftDecisions) {
            length += commitmentDecision.getR1().length;
            length += commitmentDecision.getHash().length;
        }
        for (CommitmentDecision commitmentDecision : rightDecisions) {
            length += commitmentDecision.getR1().length;
            length += commitmentDecision.getHash().length;
        }
        byte[] output = new byte[length];
        int offset = 0;
        System.arraycopy(serialNo, 0, output, offset, serialNo.length);
        offset += serialNo.length;
        System.arraycopy(value.toByteArray(), 0, output, offset, value.toByteArray().length);
        offset += value.toByteArray().length;
        offset = fillBytesFromDecisionsAndGetOffset(leftDecisions, output, offset);
        fillBytesFromDecisionsAndGetOffset(rightDecisions, output, offset);
        return output;

    }

    private int fillBytesFromDecisionsAndGetOffset(List<CommitmentDecision> list, byte[] output, int offset) {
        for (CommitmentDecision commitmentDecision : list) {
            int r1len = commitmentDecision.getR1().length;
            System.arraycopy(commitmentDecision.getR1(), 0, output, offset, r1len);
            offset += r1len;
            int hashLen = commitmentDecision.getHash().length;
            System.arraycopy(commitmentDecision.getHash(), 0, output, offset, hashLen);
            offset += hashLen;
        }
        return offset;
    }
}