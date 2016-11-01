package edu.wmi.rsa;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class Utils {
    public static BigInteger modInv(BigInteger value, BigInteger n) {

        BigInteger b0 = value;
        BigInteger n0 = n;
        BigInteger t0 = ZERO;
        BigInteger t = ONE;
        BigInteger q = n.divide(b0);
        BigInteger r = n0.subtract(q.multiply(b0));

        while (r.compareTo(ZERO) > 0) {

            BigInteger temp = t0.subtract(q.multiply(t));

            if (temp.compareTo(ZERO) > 0)
                temp = temp.mod(n);
            if (temp.compareTo(ZERO) < 0)
                temp = n.subtract(temp.negate().mod(n));

            t0 = t;
            t = temp;
            n0 = b0;
            b0 = r;
            q = n0.divide(b0);
            r = n0.subtract(q.multiply(b0));
        }

        if (!b0.equals(ONE))
            return null;
        else
            return t.mod(n);
    }


}