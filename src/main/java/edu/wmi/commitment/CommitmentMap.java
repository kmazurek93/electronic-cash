package edu.wmi.commitment;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by lupus on 06.10.16.
 */
public class CommitmentMap {

    private static CommitmentMap instance;
    private volatile Map<String, String> r1ToHashMap;

    private CommitmentMap() {
        r1ToHashMap = newHashMap();
    }

    public synchronized static CommitmentMap getInstance() {
        if (instance == null) {
            instance = new CommitmentMap();
        }
        return instance;
    }

    public Map<String, String> getR1ToHashMap() {
        return r1ToHashMap;
    }

}
