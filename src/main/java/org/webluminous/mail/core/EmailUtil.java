package org.webluminous.mail.core;

import com.webluminous.xsd.email.CtContact;
import com.webluminous.xsd.email.CtEMail;
import com.webluminous.xsd.email.CtMaildata;
import com.webluminous.xsd.email.EMailDocument;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

/**
 * This Class is used for Creating the Email message and triggering of email.
 *
 * @author Manikandan on 11/5/2016.
 * @version 1.0
 * @since 11/2016
 */
public class EmailUtil {

    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class);
    private static Properties mailproperties = new Properties();
    private Session session = null;
    private MimeMessage msg = null;

    public EmailUtil() {
        readproperty();
    }

    /**
     * @param from    From address
     * @param subject Mail subject
     * @param body    Mail body message
     * @param html    indicates whether mail sent in html format
     * @param image   indicates contains image in email
     * @param fileUrl image url
     * @return MimeMessage-Email message
     */
    public MimeMessage getMessage(String from, String subject, String body, boolean html, boolean image, String fileUrl, boolean contenttype) {
        try {
            session = getSession();
            if (msg == null || !contenttype) {
                msg = new MimeMessage(session);
                // -- Set the FROM and TO fields --
                msg.setFrom(new InternetAddress(from));
                // bcc the 'bcc' email address all emails
                if (mailproperties.getProperty(Static.PROP_BCC_EMAIL) != null) {
                    msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(mailproperties.getProperty(Static.PROP_BCC_EMAIL), false));
                }
                // -- Set the subject and body text --
                msg.setSubject(subject, Static.CHARSET);
                msg.setText(body);
                // -- Set some other header information --
                msg.setHeader(Static.X_MAILER, Static.AWE_DIRECT_MAILER);
                msg.setSentDate(new Date());
                // -- Send the message --
                MimeMultipart multipart = new MimeMultipart("related");
                BodyPart messageBodyPart = new MimeBodyPart();
                String htmlText = body;
                //if html message
                if (html) {
                    messageBodyPart.setContent(htmlText, Static.HTML_CONTENT_TYPE);
                    multipart.addBodyPart(messageBodyPart);
                    msg.setDataHandler(new DataHandler(htmlText, Static.HTML_CONTENT_TYPE));
                }
                //send image as attachment
                if (fileUrl != null) {
                    messageBodyPart = new MimeBodyPart();
                    URL url = new URL(fileUrl);
                    URLDataSource ds = new URLDataSource(url);
                    messageBodyPart.setDataHandler(new DataHandler(ds));
                    if (image) {
                        messageBodyPart.setHeader(Static.IMAGE_CONTENT_ID, Static.IMAGE_TAG);
                        multipart.addBodyPart(messageBodyPart);
                    }
                    BodyPart imgPart = new MimeBodyPart();
                    URL imageFileURL = new URL(fileUrl);
                    URLDataSource ds1 = new URLDataSource(imageFileURL);
                    imgPart.setDataHandler(new DataHandler(ds1));
                    if (image) {
                        imgPart.setHeader(Static.IMAGE_CONTENT_ID, Static.IMAGE_TAG);
                    }
                    imgPart.setFileName("ATT.png");
                    multipart.addBodyPart(imgPart);
                    msg.setContent(multipart);
                }
            }
        } catch (MessagingException | MalformedURLException ex) {
            LOGGER.error("send() failed: " + ex.getMessage(), ex);
        }
        return msg;
    }

    /**
     * Reading Email Properties file to get smtp host configuration
     */
    private void readproperty() {
        try {
            mailproperties.load(ClassLoader.getSystemResourceAsStream(Static.SMTP_MAIL_PROPERTY));
        } catch (IOException e) {
            LOGGER.error("Exception on reading the Property file", e);
        }
    }

    /**
     * Sets the mail properties
     *
     * @param key   Key name
     * @param value Value for the key
     */
    public static void setMailproperties(String key, String value) {
        mailproperties.put(key, value);
    }

    /**
     * @return Session- Creates a Email session by reading setting smtp properties
     */
    private Session getSession() {
        if (session == null) {
            session = Session.getInstance(mailproperties, null);
        }
        return session;
    }

    /**
     * @param reader Input Reader Objec which contains Inputs required for trigering email
     */
    public void sendBulkemails(InputReader reader) {
        Session mailsession = null;
        Transport transport = null;
        MimeMessage message;
        try {
            mailsession = getSession();
            transport = mailsession.getTransport(session.getProvider("smtp"));
            transport.connect();
            for (String content : reader.getFilecontent()) {
                EMailDocument document = generateTransformXml(reader.getHeader(), content);
                message = getMessage(reader.getFromaddress(), reader.getSubject(), doTransform(document.xmlText(), InputReader.getMailtemplate()), true, true, null, false);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(document.getEMail().getContact().getEmail(), false));
                message.saveChanges();
                transport.sendMessage(message, message.getAllRecipients());
            }
        } catch (Exception e) {
            LOGGER.error("Exception Occured while sending mail", e);
        }
    }

    /**
     * @param header      header value from input csv
     * @param detailvalue lines from the input csv
     * @return String xml which is used for transformation
     */

    public EMailDocument generateTransformXml(String header, String detailvalue) {
        EMailDocument emailDocument = EMailDocument.Factory.newInstance();
        CtEMail email = emailDocument.addNewEMail();
        CtContact contact = email.addNewContact();
        String[] details = header.split(Static.CSV_FILE_SEPERATOR);
        String[] values = detailvalue.split(Static.CSV_FILE_SEPERATOR);
        for (int i = 0; i < details.length; i++) {
            if (values[i].matches(Static.EMAIL_REGEX)) {
                contact.setEmail(values[i]);
            } else {
                CtMaildata maildata = email.addNewMaildata();
                maildata.setContentname(details[i]);
                maildata.setContentvalue("".equals(values[i]) ? Static.BLANK_VALUE : values[i]);
            }
        }
        return emailDocument;
    }

    /**
     * This method tranforms the xml with xslt and generates the email body message to send
     *
     * @param xml       Transform xml which is generate by generateTransformXml()
     * @param templates Xslt template
     * @return Final Email message as String. if incoming xml is null then it returns null as final message
     * @throws JDOMException        throws on invalid xml while making as document
     * @throws IOException          throws on rading xslt
     * @throws TransformerException throws on any error in xslt
     */
    private String doTransform(String xml, Templates templates) throws JDOMException, TransformerException {
        try {
            if (xml != null) {
                SAXBuilder builder = new SAXBuilder(false);
                Document emaildoc = builder.build(new StringReader(xml));
                Transformer transformer = templates.newTransformer();
                StringWriter sw = new StringWriter();
                transformer.transform(new JDOMSource(emaildoc), new StreamResult(sw));
                return sw.toString();
            }
        } catch (IOException e) {
            LOGGER.error("Exception Occured while transforming", e);
        }
        return null;
    }
}
