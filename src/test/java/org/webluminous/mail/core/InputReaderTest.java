package org.webluminous.mail.core;

import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.xml.transform.TransformerConfigurationException;
import java.io.FileNotFoundException;

/**
 * @author Manikandan on 11/9/2016.
 */
@RunWith(JMockit.class)
public class InputReaderTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void presetup() {
        System.setProperty(Static.SYSYEM_PROPERTY_FILE, ClassLoader.getSystemResource("inputcsvfile.csv").getPath());
        System.setProperty(Static.SYSTEM_PROPERTY_TEMPLATE_FILE, ClassLoader.getSystemResource("inputtemplate.xsl").getPath());
    }

    @Test
    public void testwithSystemProperty() throws Exception {
        InputReader reader = new InputReader();
        reader.setFromaddress("test@test.com");
        reader.setSubject("test mail");
        Assert.assertTrue("CSV File contents", reader.getFilecontent().size() > 0);
        Assert.assertTrue("Template Contents", InputReader.getMailtemplate() != null);
        Assert.assertTrue("Header Content", reader.getHeader() != null);
        Assert.assertEquals("test@test.com", reader.getFromaddress());
        Assert.assertEquals("test mail", reader.getSubject());
    }

    @Test
    public void testWithoutSystemproperty() throws Exception {
        InputReader reader = new InputReader(ClassLoader.getSystemResource("inputcsvfile.csv").getPath(), ClassLoader.getSystemResource("inputtemplate.xsl").getPath());
        reader.setFromaddress("test@test.com");
        reader.setSubject("test mail");
        Assert.assertTrue("CSV File contents", reader.getFilecontent().size() > 0);
        Assert.assertTrue("Template Contents", InputReader.getMailtemplate() != null);
        Assert.assertTrue("Header Content", reader.getHeader() != null);
        Assert.assertEquals("test@test.com", reader.getFromaddress());
        Assert.assertEquals("test mail", reader.getSubject());
    }

    @Test
    public void testcsvfilenotfoundexception() throws Exception {
        exception.expect(FileNotFoundException.class);
        InputReader reader = new InputReader("../inputcsvfile.csv", "../inputtemplate.xsl");
    }

    @Test
    public void testxslfilenotfoundexception() throws Exception {
        exception.expect(FileNotFoundException.class);
        InputReader reader = new InputReader(ClassLoader.getSystemResource("inputcsvfile.csv").getPath(), "../inputtemplate.xsl");
    }

    @Test
    public void testcsvfilewithoutvalue() throws Exception {
        InputReader reader = new InputReader(ClassLoader.getSystemResource("inputcsvwithoutvalues.csv").getPath(), ClassLoader.getSystemResource("inputtemplate.xsl").getPath());
        reader.setFromaddress("test@test.com");
        reader.setSubject("test mail");
        Assert.assertTrue("CSV File contents", reader.getFilecontent().size() == 0);
    }

    @Test
    public void testwithoutpropertemplatevalue() throws Exception {
        exception.expect(TransformerConfigurationException.class);
        InputReader reader = new InputReader(ClassLoader.getSystemResource("inputcsvfile.csv").getPath(), ClassLoader.getSystemResource("inputcsvwithoutvalues.csv").getPath());

    }

}