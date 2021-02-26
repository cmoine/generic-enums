package org.cmoine.genericEnums;

import org.junit.Assert;
import org.junit.Test;

public class GenericEnumTest {
    @Test
    public void testValues() {
        Assert.assertEquals(SimpleGenericEnum.values().length, SimpleGenericEnumExt.values().length);
    }

    @Test
    public void testDefaultValue() {
        Assert.assertEquals(0, PrimitiveExt.INT.getDefaultValue().intValue());
    }
}
