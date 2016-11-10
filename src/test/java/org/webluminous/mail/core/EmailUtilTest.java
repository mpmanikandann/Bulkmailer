package org.webluminous.mail.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * @author Manikandan on 11/10/2016.
 */
public class EmailUtilTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testEmail() throws Exception {
        InputReader reader = new InputReader(ClassLoader.getSystemResource("inputcsvfile.csv").getPath(), ClassLoader.getSystemResource("inputtemplate.xsl").getPath());
        reader.setFromaddress("test@test.com");
        reader.setSubject("test mail");
        EmailUtil emailUtil = new EmailUtil();
        emailUtil.sendBulkemails(reader);
    }


}