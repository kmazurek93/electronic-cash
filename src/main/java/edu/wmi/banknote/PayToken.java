package edu.wmi.banknote;

import java.util.List;

/**
 * Created by lupus on 15.12.16.
 */
public class PayToken {

    private List<Integer> integers;
    private String token;

    public List<Integer> getIntegers() {
        return integers;
    }

    public void setIntegers(List<Integer> integers) {
        this.integers = integers;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
