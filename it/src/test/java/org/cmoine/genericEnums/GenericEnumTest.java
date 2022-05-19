package org.cmoine.genericEnums;

import org.junit.Assert;
import org.junit.Test;

public class GenericEnumTest {
    public static final String ENUM_NAME = "ONE";
    public static final String NON_EXISTENT_ENUM_NAME = "NON_EXISTENT";

    @Test
    public void test() {
        Assert.assertEquals(String.class, Argument.STRING.getClazz());
    }

    @Test
    public void testValues() {
        Assert.assertEquals(SimpleGenericEnum.values().length, SimpleGenericEnumExt.values().length);
    }

    @Test
    public void testValueOfAndEquals() {
        Assert.assertEquals(SimpleGenericEnumExt.ONE, SimpleGenericEnumExt.valueOf(ENUM_NAME));
    }

    @Test
    public void testIllegalArgumentException() {
        IllegalArgumentException e = Assert.assertThrows(IllegalArgumentException.class, () -> SimpleGenericEnumExt.valueOf(NON_EXISTENT_ENUM_NAME));
        Assert.assertEquals("No enum constant org.cmoine.genericEnums.SimpleGenericEnumExt.NON_EXISTENT", e.getMessage());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(ENUM_NAME, SimpleGenericEnumExt.valueOf(ENUM_NAME).toString());
    }

    @Test
    public void testName() {
        Assert.assertEquals(ENUM_NAME, SimpleGenericEnumExt.ONE.name());
    }

    @Test
    public void testOrdinal() {
        Assert.assertEquals(0, SimpleGenericEnumExt.ONE.ordinal());
    }

    @Test
    public void testDefaultValue() {
        Assert.assertEquals(0, PrimitiveExt.INT.getDefaultValue().intValue());
    }

    @Test
    public void testPolymorphicConstructor() {
        Assert.assertEquals(0, PolymorphicConstructorEnumExt.ONE_PARAM.get_default());
        Assert.assertEquals(1, PolymorphicConstructorEnumExt.TWO_PARAM.get_default());
    }

    @Test
    public void testAbstractEnum() {
        Assert.assertEquals(1, AbstractEnumExt.ONE.getValue().intValue());
        Assert.assertEquals(2.0, AbstractEnumExt.TWO.getValue().doubleValue(), 0.01);
    }

    @Test
    public void testPredicate() {
        Assert.assertEquals(true, IsDefaultExt.INT.test(0));
        Assert.assertEquals(false, IsDefaultExt.INT.test(1));
        Assert.assertEquals(true, IsDefaultExt.STRING.test(""));
        Assert.assertEquals(false, IsDefaultExt.STRING.test(null));
    }

    @Test
    public void testDefaultConstructor() {
        Assert.assertEquals(0, DefaultConstructorEnumExt.ZERO_PARAM.get_default());
        Assert.assertEquals(1, DefaultConstructorEnumExt.ONE_PARAM.get_default());
        Assert.assertEquals(2, DefaultConstructorEnumExt.TWO_PARAM.get_default());
    }
}
