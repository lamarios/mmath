package com.ftpix;

import com.ftpix.mmath.model.MmathModel;
import com.ftpix.mmath.model.stats.Stats;
import com.ftpix.sherdogparser.models.SherdogBaseObject;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gz on 12-Feb-17.
 */
public class ModelTest {


    @Test
    public void testFightStatsIdGenerator() {

        String first = "abcd";
        String second = "efg";

        Assert.assertEquals(first + "&&" + second, Stats.generateFightId(first, second));
        Assert.assertEquals(first + "&&" + second, Stats.generateFightId(second, first));

    }

    @Test
    public void testIdGen() {
        SherdogBaseObject object = new SherdogBaseObject();
        object.setSherdogUrl("http://www.sherdog.com/fighter/Alistair-Overeem-461");
        String expected = "fighter-Alistair-Overeem-461";

        Assert.assertEquals(expected, MmathModel.generateId(object));
    }
}
