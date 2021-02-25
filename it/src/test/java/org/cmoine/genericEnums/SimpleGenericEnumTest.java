package org.cmoine.genericEnums;

import org.junit.Assert;
import org.junit.Test;

public class SimpleGenericEnumTest {
    @Test
    public void testValues() {
        Assert.assertEquals(SimpleGenericEnum.values().length, SimpleGenericEnumExt.values().length);
    }
}
