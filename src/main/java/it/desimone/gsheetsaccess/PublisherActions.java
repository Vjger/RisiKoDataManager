package it.desimone.gsheetsaccess;

import it.desimone.gsheetsaccess.common.ExcelValidationException;
import it.desimone.gsheetsaccess.common.FileUtils;
import it.desimone.gsheetsaccess.common.GDriveUtils;
import it.desimone.gsheetsaccess.gdrive.file.ReportDriveData;
import it.desimone.gsheetsaccess.googleaccess.GmailAccess;
import it.desimone.gsheetsaccess.gsheets.dto.ReportElaborazioneRow;
import it.desimone.gsheetsaccess.htmlpublisher.SitePages;
import it.desimone.risiko.torneo.batch.ExcelValidator.ExcelValidatorMessages;
import it.desimone.utils.MyException;
import it.desimone.utils.MyLogger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class PublisherActions {

	private static final String SUBJECT_PREFIX_KO = "[NON RISPONDERE A QUESTA MAIL] ERRORE nell'elaborazione del report di RisiKo! ";
	private static final String SUBJECT_PREFIX_OK = "[NON RISPONDERE A QUESTA MAIL] Esito positivo elaborazione report di RisiKo! ";
	
	public static ReportElaborazioneRow successingPublishing(ReportDriveData reportDriveData){
		FileUtils.moveToDone(reportDriveData);
		GDriveUtils.moveToDone(reportDriveData);
		sendMail(reportDriveData, SUBJECT_PREFIX_OK, messaggioOK(reportDriveData));
		ReportElaborazioneRow reportElaborazioneRow = new ReportElaborazioneRow(reportDriveData.getParentFolderName(), reportDriveData.getFileName(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), "OK", null);
		return reportElaborazioneRow;
	}
	
	public static ReportElaborazioneRow validationErrorPublishing(ReportDriveData reportDriveData, ExcelValidationException eve){
		FileUtils.moveToError(reportDriveData);
		GDriveUtils.moveToError(reportDriveData);
		sendMail(reportDriveData, SUBJECT_PREFIX_KO, messaggioKOValidazione(reportDriveData, eve));
		ReportElaborazioneRow reportElaborazioneRow = new ReportElaborazioneRow(reportDriveData.getParentFolderName(), reportDriveData.getFileName(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), "KO", eve.getMessages().toString());
		return reportElaborazioneRow;
	}
	
	public static ReportElaborazioneRow errorPublishing(ReportDriveData reportDriveData, MyException me){
		FileUtils.moveToError(reportDriveData);
		GDriveUtils.moveToError(reportDriveData);
		sendMail(reportDriveData, SUBJECT_PREFIX_KO, messaggioKOErrore(reportDriveData, me));
		ReportElaborazioneRow reportElaborazioneRow = new ReportElaborazioneRow(reportDriveData.getParentFolderName(), reportDriveData.getFileName(), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), "KO", me.getMessage());
		return reportElaborazioneRow;
	}
	
	
	private static void sendMail(ReportDriveData reportDriveData, String subject, String message){
		GmailAccess gmailAccess = new GmailAccess();
		//subject = subject + reportDriveData.getFileName();
		String[] to = reportDriveData.getEmailContacts().toArray(new String[0]);
		String[] bcc = {"risiko.it@gmail.com"};

		MimeMessage mimeMessage;
		try {
			MyLogger.getLogger().info("Invio mail a "+Arrays.toString(to)+" con subject "+subject+" per il report "+reportDriveData.getFileName());
			mimeMessage = GmailAccess.createEmail(to, null, bcc, null, subject, message);
			gmailAccess.sendMessage("me", mimeMessage);
		} catch (MessagingException e) {
			MyLogger.getLogger().severe("Error sending mail to "+Arrays.toString(to)+": "+e.getMessage());
		} catch (IOException e) {
			MyLogger.getLogger().severe("Error sending mail to "+Arrays.toString(to)+": "+e.getMessage());
		}
	}
	
	private static String messaggioOK(ReportDriveData reportDriveData){
		StringBuilder result = new StringBuilder();
		result.append("L'elaborazione del report [");
		result.append(reportDriveData.getFileName());
		result.append("] è avvenuta regolarmente ed ora i dati del report sono disponibili nel database centrale");
		result.append("\n");
		result.append("Si raccomanda di verificare la corrispondenza del report sulle pagine web e l'eventuale generazione di doppioni anagrafici");
		result.append("al seguente indirizzo: "+SitePages.DOPPIONI_URL);
		return result.toString();
	}
	
	private static String messaggioKOValidazione(ReportDriveData reportDriveData, ExcelValidationException eve){
		StringBuilder result = new StringBuilder();
		result.append("L'elaborazione del report [");
		result.append(reportDriveData.getFileName());
		result.append("] ha riscontrato degli errori di validazione e quindi è stato scartato.");
		result.append("\n");
		result.append("Correggere gli errori indicati e ripubblicare il report");
		result.append("\n");
		result.append("ERRORI");
		result.append("\n");
		List<ExcelValidatorMessages> messages = eve.getMessages();
		if (messages != null && !messages.isEmpty()){
			for (ExcelValidatorMessages message: messages){
				result.append(message.getMessage());
				result.append("\n");
			}
		}
		return result.toString();
	}
	
	private static String messaggioKOErrore(ReportDriveData reportDriveData, MyException me){
		StringBuilder result = new StringBuilder();
		result.append("L'elaborazione del report [");
		result.append(reportDriveData.getFileName());
		result.append("] ha riscontrato degli errori e quindi è stato scartato.");
		result.append("\n");
		result.append("Correggere gli errori indicati e ripubblicare il report");
		result.append("\n");
		result.append("ERRORI");
		result.append("\n");
		result.append(me.getMessage());
		return result.toString();
	}
}
