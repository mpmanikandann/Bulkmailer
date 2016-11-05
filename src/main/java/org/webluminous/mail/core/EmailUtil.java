package org.webluminous.mail.core;

import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Manikandan on 11/5/2016.
 */
public class EmailUtil {

  private static final String MAIL_PROPERTY = "Email.properties";
  private static final String PROP_BCC_EMAIL = "BCCEMAIL";
  private Logger OUT = Logger.getLogger(this.getClass());
  private static Properties handlerProps = null;
  private Session session = null;
  private MimeMessage msg = null;

  public EmailUtil() {
    readproperty();
  }

  /**
   * @param from From address
   * @param subject Mail subject
   * @param body Mail body message
   * @param html indicates whether mail sent in html format
   * @param image indicates contains image in email
   * @param fileUrl image url
   *
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
        if (handlerProps.getProperty(PROP_BCC_EMAIL) != null) {
          msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(handlerProps.getProperty(PROP_BCC_EMAIL), false));
        }
        // -- Set the subject and body text --
        msg.setSubject(subject, "iso-8859-1");
        msg.setText(body);
        // -- Set some other header information --
        msg.setHeader("X-Mailer", "AWE-Direct-Mailer");
        msg.setSentDate(new Date());
        // -- Send the message --
        MimeMultipart multipart = new MimeMultipart("related");
        BodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = body;
        //if html message
        if (html) {
          messageBodyPart.setContent(htmlText, "text/html");
          multipart.addBodyPart(messageBodyPart);
          msg.setDataHandler(new DataHandler(htmlText, "text/html"));
        }
        //send image as attachment
        if (fileUrl != null) {
          messageBodyPart = new MimeBodyPart();
          URL url = new URL(fileUrl);
          URLDataSource ds = new URLDataSource(url);
          messageBodyPart.setDataHandler(new DataHandler(ds));
          if (image) {
            messageBodyPart.setHeader("Content-ID", "<image>");
            multipart.addBodyPart(messageBodyPart);
          }
          BodyPart imgPart = new MimeBodyPart();
          URL imageFileURL = new URL(fileUrl);
          URLDataSource ds1 = new URLDataSource(imageFileURL);
          imgPart.setDataHandler(new DataHandler(ds1));
          if (image) {
            imgPart.setHeader("Content-ID", "<image>");
          }
          imgPart.setFileName("ATT.png");
          multipart.addBodyPart(imgPart);
          msg.setContent(multipart);
        }
      }
    } catch (Exception ex) {
      OUT.error("send() failed: " + ex.getMessage(), ex);
    }
    return msg;
  }

  /**
   * Reading Email Properties file to get smtp host configuration
   */
  private void readproperty() {
    try {
      if (handlerProps == null) {
        handlerProps = new Properties();
        handlerProps.load(ClassLoader.getSystemResourceAsStream(MAIL_PROPERTY));
      }
    } catch (IOException e) {
      OUT.error("Exception on reading the Property file");
    }
  }

  /**
   * @return Session- Creates a Email session by reading setting smtp properties
   */
  private Session getSession() {
    if (session == null) {
      session = Session.getDefaultInstance(handlerProps, null);
    }
    return session;
  }
}
