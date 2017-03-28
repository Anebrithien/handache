package com.tsingye.util;

import org.junit.Assert;
import org.junit.Test;

import static com.tsingye.util.MathUtils.calcDistance;
import static com.tsingye.util.MathUtils.roundDown;
import static com.tsingye.util.MathUtils.roundHalfUp;
import static com.tsingye.util.MathUtils.roundUp;

/**
 * test cases for {@link MathUtils}
 * Created by tsingye on 17-3-6.
 */
public class MathUtilsTest {

    @Test
    public void testRoundDown() {
        Assert.assertEquals(5.0D, roundDown(5.011D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.0D, roundDown(5.055D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.0D, roundDown(5.099D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.01D, roundDown(5.011D, 2), Double.MIN_NORMAL);
        Assert.assertEquals(5.05D, roundDown(5.055D, 2), Double.MIN_NORMAL);
        Assert.assertEquals(5.09D, roundDown(5.099D, 2), Double.MIN_NORMAL);
    }

    @Test
    public void testRoundUp() {
        Assert.assertEquals(5.1D, roundUp(5.011D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.1D, roundUp(5.055D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.1D, roundUp(5.099D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.02D, roundUp(5.011D, 2), Double.MIN_NORMAL);
        Assert.assertEquals(5.06D, roundUp(5.055D, 2), Double.MIN_NORMAL);
        Assert.assertEquals(5.10D, roundUp(5.099D, 2), Double.MIN_NORMAL);
    }

    @Test
    public void testRoundHalfUp() {
        Assert.assertEquals(5.0D, roundHalfUp(5.011D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.1D, roundHalfUp(5.055D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.1D, roundHalfUp(5.099D, 1), Double.MIN_NORMAL);
        Assert.assertEquals(5.01D, roundHalfUp(5.011D, 2), Double.MIN_NORMAL);
        Assert.assertEquals(5.06D, roundHalfUp(5.055D, 2), Double.MIN_NORMAL);
        Assert.assertEquals(5.10D, roundHalfUp(5.099D, 2), Double.MIN_NORMAL);
    }

    @Test
    public void calcDistanceTest() {
        double[] tianAnMen = {39.907333, 116.391083}; //lat, lng
        double[] luGouQiao = {39.85025, 116.219066};
        double actual = calcDistance(tianAnMen[1], tianAnMen[0], luGouQiao[1], luGouQiao[0]);
        Assert.assertEquals(16e3, actual, 1e3);
        System.out.println("actual = " + actual);
    }
}
