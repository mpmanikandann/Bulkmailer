package org.webluminous.mail;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Created by Manikandan on 11/10/2016.
 */
public class StringUtilsTest {
    @Test
    public void testwithnullstring() throws Exception {
        Assert.assertFalse("String is Null", StringUtils.isNotNull(null));
    }

    @Test
    public void testwitemptyvalue() throws Exception {
        Assert.assertFalse("String is Not Null but Empty", StringUtils.isNotNull(""));
    }

    @Test
    public void testwithproperstring() {
        Assert.assertTrue("String with value", StringUtils.isNotNull("test"));
    }

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<StringUtils> constructor = StringUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}