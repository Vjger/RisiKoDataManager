package it.desimone.gsheetsaccess.googleaccess;

import it.desimone.utils.MyLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

public class GmailAccess extends RisikoDataManagerAccess{

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */

    public static MimeMessage createEmail(String[] to, String[] cc, String[] bcc, String from, String subject, String bodyText) throws MessagingException {
    	Properties props = new Properties();
    	Session session = Session.getDefaultInstance(props, null);

    	MimeMessage email = new MimeMessage(session);

    	if (from != null){
    		email.setFrom(new InternetAddress(from));
    	}
    	if (to != null){
    		for (String a: to){
    			email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(a));
    		}
    	}
    	if (cc != null){
    		for (String c: cc){
    			email.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(c));
    		}
    	}
    	if (bcc != null){
    		for (String b: bcc){
    			email.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(b));
    		}
    	}
    	email.setSubject(subject);
    	email.setText(bodyText);
    	return email;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public Message sendMessage(String userId, MimeMessage emailContent) throws MessagingException, IOException {
    	Gmail service = getGmailService();
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        MyLogger.getLogger().info("Message id: " + message.getId());
        //MyLogger.getLogger().info(message.toPrettyString());
        return message;
    }

}