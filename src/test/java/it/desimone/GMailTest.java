package it.desimone;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import it.desimone.gsheetsaccess.googleaccess.GmailAccess;

public class GMailTest {

	public static void main(String[] args) throws MessagingException, IOException {
		GmailAccess gmailAccess = new GmailAccess();
		String subject = "Invio mail di test2";
		String bodyText = "Stai ricevendo una semplice mail di test";
		String from = "pippo@pluto.com";
		String to = "vjger69@gmail.com";
		
		MimeMessage mimeMessage = GmailAccess.createEmail(new String[]{to}, null, null, null, subject, bodyText);
		
		gmailAccess.sendMessage("me", mimeMessage);

	}

}
