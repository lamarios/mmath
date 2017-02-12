package com.ftpix;

import com.ftpix.mmath.model.stats.Stats;

import org.junit.Assert;
import org.junit.Test;
/**
 * Created by gz on 12-Feb-17.
 */
public class FightStatsTest {


    @Test
    public void testFightStatsIdGenerator(){

        String first = "abcd";
        String second = "efg";

        Assert.assertEquals(first+second, Stats.generateFightId(first, second));
        Assert.assertEquals(first+second, Stats.generateFightId(second, first));

    }
}
