package it.desimone.gsheetsaccess;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.common.ExcelValidationException;
import it.desimone.gsheetsaccess.common.GDriveUtils;
import it.desimone.gsheetsaccess.gdrive.file.GDriveDownloader;
import it.desimone.gsheetsaccess.gdrive.file.ReportAnalyzer;
import it.desimone.gsheetsaccess.gdrive.file.ReportDriveData;
import it.desimone.gsheetsaccess.googleaccess.GmailAccess;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRidottaRow;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRow;
import it.desimone.gsheetsaccess.gsheets.dto.ClassificheRow;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.gsheets.dto.ReportElaborazioneRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRow;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiRow;
import it.desimone.gsheetsaccess.gsheets.facade.ExcelGSheetsBridge;
import it.desimone.gsheetsaccess.gsheets.facade.GSheetsInterface;
import it.desimone.risiko.torneo.batch.ExcelValidator.ExcelValidatorMessages;
import it.desimone.risiko.torneo.batch.ExcelValidator.ExcelValidatorMessages.Scheda;
import it.desimone.risiko.torneo.dto.GiocatoreDTO;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;
import it.desimone.risiko.torneo.dto.Torneo;
import it.desimone.utils.MyException;
import it.desimone.utils.MyLogger;
import it.desimone.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class ReportPublisher {

	public static void main(String[] args) {
		publish();
	}
	
	public static void publish() {
		//MyLogger.setConsoleLogLevel(Level.INFO);
		MyLogger.getLogger().info("INIZIO Elaborazione");
		try{
			List<ReportDriveData> publishedReport = GDriveDownloader.downloadReport(true);
			if (publishedReport != null && !publishedReport.isEmpty()){
				GDriveUtils.backup();
				List<SheetRow> reportElaborazioni = new ArrayList<SheetRow>();
				MyLogger.getLogger().info("Inizio elaborazione di "+publishedReport.size()+" report");
				for (ReportDriveData reportDriveData: publishedReport){
					MyLogger.getLogger().info("Inizio elaborazione di "+reportDriveData);
					try{
						Torneo torneo = ReportAnalyzer.analyzeExcelReport(reportDriveData);
						MyLogger.getLogger().info("Validato report "+reportDriveData);
						pubblicaTorneo(torneo);
						MyLogger.getLogger().info("Pubblicato report "+reportDriveData);
						ReportElaborazioneRow reportElaborazioneRow = PublisherActions.successingPublishing(reportDriveData);
						reportElaborazioni.add(reportElaborazioneRow);
					}catch(ExcelValidationException eve){
						MyLogger.getLogger().severe("Errore di validazione del report "+reportDriveData+"\n"+eve.getMessages().toString());
						ReportElaborazioneRow reportElaborazioneRow = PublisherActions.validationErrorPublishing(reportDriveData, eve);
						reportElaborazioni.add(reportElaborazioneRow);
					}catch(MyException me){
						MyLogger.getLogger().severe("Errore di validazione del report "+reportDriveData+"\n"+me.getMessage());
						ReportElaborazioneRow reportElaborazioneRow = PublisherActions.errorPublishing(reportDriveData, me);
						reportElaborazioni.add(reportElaborazioneRow);
					}catch(Exception e){
						MyLogger.getLogger().severe("Errore di pubblicazione del report "+reportDriveData+"\n"+e.getMessage());
						sendErrorMail(reportDriveData, e.getMessage());
					}
				}
				if (!reportElaborazioni.isEmpty()){
					String reportElaborazioniId = Configurator.getReportElaborazioniSheetId();
					GSheetsInterface.appendRows(reportElaborazioniId, ReportElaborazioneRow.SHEET_NAME, reportElaborazioni);
				}
			}else{
				MyLogger.getLogger().info("Nessun report da elaborare");
			}
			MyLogger.getLogger().info("FINE Elaborazione");
		}catch(Exception e){
			MyLogger.getLogger().severe("Errore di accesso a google drive "+e.getMessage());
			sendErrorMail(null, e.getMessage());
		}
	}

	
	private static void sendErrorMail(ReportDriveData reportDriveData, String errorMessage){
		MyLogger.getLogger().entering("ReportPublisher", "sendErrorMail");
		
		GmailAccess gmailAccess = new GmailAccess();
		String subject = "ERRORE NELL'ELABORAZIONE DEI REPORT";
		String[] to = {"risiko.it@gmail.com"};

		if (reportDriveData != null){
			errorMessage = "Report "+reportDriveData.toString()+"\n"+errorMessage;
		}
		
		MimeMessage mimeMessage;
		try {
			MyLogger.getLogger().severe("Sending mail to "+to+" with subject "+subject);
			mimeMessage = GmailAccess.createEmail(to, null, null, null, subject, errorMessage);
			gmailAccess.sendMessage("me", mimeMessage);
		} catch (MessagingException e) {
			MyLogger.getLogger().severe("Error sending mail to "+to+": "+e.getMessage());
		} catch (IOException e) {
			MyLogger.getLogger().severe("Error sending mail to "+to+": "+e.getMessage());
		}
		MyLogger.getLogger().exiting("ReportPublisher", "sendErrorMail");
	}
	
	public static void pubblicaTorneo(Torneo torneo) throws Exception{
		MyLogger.getLogger().entering("ReportPublisher", "pubblicaTorneo");
		
		insertOrUpdateTorneo(torneo);
		Map<Integer, Integer> mappaIdExcelVsIdGSheets = insertOrUpdateGiocatori(torneo);
		deleteAndInsertPartita(torneo, mappaIdExcelVsIdGSheets);
		deleteAndInsertClassifica(torneo, mappaIdExcelVsIdGSheets);
		
		MyLogger.getLogger().exiting("ReportPublisher", "pubblicaTorneo");
	}
	
	
	private static void insertOrUpdateTorneo(Torneo torneo) throws Exception{
		MyLogger.getLogger().entering("ReportPublisher", "insertOrUpdateTorneo");
		
		TorneiRow torneoRow = ExcelGSheetsBridge.getTorneoRowByTorneo(torneo);
		
		String year = ExcelGSheetsBridge.obtainYearTorneo(torneo);
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		if (spreadSheetIdTornei == null){
			GDriveUtils.cloneTornei(year);
			spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		}
		String sheetNameTornei = TorneiRow.SHEET_TORNEI_NAME;
		Integer torneoRowFound = GSheetsInterface.findNumTorneoRowByIdTorneo(spreadSheetIdTornei, sheetNameTornei, torneoRow);
		
		if (torneoRowFound != null){
			torneoRow.setSheetRowNumber(torneoRowFound);
			List<SheetRow> rows = new ArrayList<SheetRow>();
			rows.add(torneoRow);
			GSheetsInterface.updateRows(spreadSheetIdTornei, sheetNameTornei, rows, true);
		}else{
			GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNameTornei, Collections.singletonList((SheetRow)torneoRow));
		}
		MyLogger.getLogger().exiting("ReportPublisher", "insertOrUpdateTorneo");
	}
	
	
	private static Map<Integer, Integer> insertOrUpdateGiocatori(Torneo torneo) throws IOException, ExcelValidationException{
		MyLogger.getLogger().entering("ReportPublisher", "insertOrUpdateGiocatori");
		
		Map<Integer, Integer> mappaIdExcelVsIdGSheets = null;
		SheetRow[][] anagrafiche = ExcelGSheetsBridge.getAnagraficheRowByTorneo(torneo);
		
		String year = ExcelGSheetsBridge.obtainYearTorneo(torneo);

		String spreadSheetIdAnagraficaRidotta 	= Configurator.getAnagraficaRidottaSheetId();
		String spreadSheetIdTornei 				= Configurator.getTorneiSheetId(year);
		String sheetNameAnagraficaRidotta 		= AnagraficaGiocatoreRidottaRow.SHEET_ANAGRAFICA_NAME;
		String sheetNameGiocatori 				= AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME;
		
		if (anagrafiche != null && anagrafiche.length > 0){
			mappaIdExcelVsIdGSheets = new HashMap<Integer, Integer>();
			
			List<SheetRow> anagraficheRidotteDaAggiungere = new ArrayList<SheetRow>();
			List<SheetRow> anagraficheDaAggiungere = new ArrayList<SheetRow>();
			List<SheetRow> anagraficheDaAggiornare = new ArrayList<SheetRow>();
			
			List<GiocatoreDTO> partecipanti = torneo.getPartecipanti();
			
			List<AnagraficaGiocatoreRidottaRow> anagraficheDaVerificare = new ArrayList<AnagraficaGiocatoreRidottaRow>();
			for (SheetRow[] sheetRow: anagrafiche){
				AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidotta = (AnagraficaGiocatoreRidottaRow)sheetRow[0];
				anagraficheDaVerificare.add(anagraficaGiocatoreRidotta);
			}
	
			//List<AnagraficaGiocatoreRidottaRow> anagraficaRowFound = GSheetsInterface.findAnagraficheRidotteByKey(spreadSheetIdAnagraficaRidotta, anagraficheDaVerificare);
			//Aggiungere la ricerca dei giocatori utilizzando un FILTER o un altro a seconda del fatto che ci sia o no indicato l'ID Nazionale nel report
			List<AnagraficaGiocatoreRidottaRow> anagraficaRowFound = GSheetsInterface.findAnagraficheRidotteByKeyOrIdNazionale(spreadSheetIdAnagraficaRidotta, anagraficheDaVerificare);
			
			//TODO Con la nuova gestione dell'anagrafica sul report vanno fatti due controlli che prima non servivano:
			//		1) Controllare che l'ID indicato effettivamente esista. Cioè, se io mi trovo impostato l'ID Nazionale e poi la query non lo trova, questo dovrebbe essere un errore.
			//		2) Controllare che ci sia corrispondenza tra la coppia nome+cognome associata all'ID Nazionale e quella che risulta dal report: anche in questo caso se non c'è corrispondenza dovrebbe esserci un errore
			if (anagraficaRowFound != null){
				validateAnagrafiche(anagraficheDaVerificare, anagraficaRowFound);
				Integer maxId = GSheetsInterface.findMaxIdAnagrafica(spreadSheetIdAnagraficaRidotta);
				int index = 0;
				for (AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidottaRow: anagraficaRowFound){
					AnagraficaGiocatoreRow anagraficaGiocatoreRow = (AnagraficaGiocatoreRow) anagrafiche[index][1];
					if (anagraficaGiocatoreRidottaRow.getId() == null){
						anagraficaGiocatoreRidottaRow.setId(++maxId);
						anagraficheRidotteDaAggiungere.add(anagraficaGiocatoreRidottaRow);
						anagraficheDaAggiungere.add(anagraficaGiocatoreRow);
					}else{
						anagraficheDaAggiornare.add(anagraficaGiocatoreRow);
					}
					anagraficaGiocatoreRow.setId(anagraficaGiocatoreRidottaRow.getId());			
					//E' un po' una zozzata: si dà per scontato che la lista dei giocatori e l'array contengano stesso giocatore per stesso indice.
					mappaIdExcelVsIdGSheets.put(partecipanti.get(index).getId(), anagraficaGiocatoreRidottaRow.getId());
					index++;
				}
			}
			
			if (!anagraficheRidotteDaAggiungere.isEmpty()){
				GSheetsInterface.appendRows(spreadSheetIdAnagraficaRidotta, sheetNameAnagraficaRidotta, anagraficheRidotteDaAggiungere);
				MyLogger.getLogger().info("Aggiunte "+anagraficheRidotteDaAggiungere.size()+" anagrafiche ridotte");
			}
			if (!anagraficheDaAggiornare.isEmpty()){
				List<SheetRow> anagraficheDaAggiornareRowFound = GSheetsInterface.findAnagraficheByKey(spreadSheetIdTornei, anagraficheDaAggiornare);
				Iterator<SheetRow> iterator = anagraficheDaAggiornareRowFound.iterator();
				while (iterator.hasNext()){
					SheetRow anagraficaIpoteticamenteDaAggiornare = iterator.next();
					if (anagraficaIpoteticamenteDaAggiornare.getSheetRowNumber() == null){
						anagraficheDaAggiungere.add(anagraficaIpoteticamenteDaAggiornare);
						iterator.remove();
					}
				}
				GSheetsInterface.updateRows(spreadSheetIdTornei, sheetNameGiocatori, anagraficheDaAggiornareRowFound, true);
				MyLogger.getLogger().info("Aggiornate "+anagraficheDaAggiornare.size()+" anagrafiche");
			}
			if (!anagraficheDaAggiungere.isEmpty()){
				GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNameGiocatori, anagraficheDaAggiungere);
				MyLogger.getLogger().info("Aggiunte "+anagraficheDaAggiungere.size()+" anagrafiche");
			}
		}
		MyLogger.getLogger().exiting("ReportPublisher", "insertOrUpdateGiocatori");
		return mappaIdExcelVsIdGSheets;	
	}

	
	private static void validateAnagrafiche(List<AnagraficaGiocatoreRidottaRow> anagraficheDaVerificare, List<AnagraficaGiocatoreRidottaRow> anagraficaRowFound) throws ExcelValidationException{
		List<ExcelValidatorMessages> validationErrors = new ArrayList<ExcelValidatorMessages>();
		for (int index = 0; index < anagraficheDaVerificare.size(); index++){
			AnagraficaGiocatoreRidottaRow anagraficaDaVerificare = anagraficheDaVerificare.get(index);
			AnagraficaGiocatoreRidottaRow anagraficaVerificata   = anagraficaRowFound.get(index);
			if (anagraficaDaVerificare.getId() != null && StringUtils.isNullOrEmpty(anagraficaDaVerificare.getDataDiNascita())){
				if (anagraficaVerificata.getId() == null){
					validationErrors.add(new ExcelValidatorMessages(Scheda.ISCRITTI, "L'idNazionale ["+anagraficaDaVerificare.getId()+"] del giocatore "+anagraficaDaVerificare.getNome()+" "+anagraficaDaVerificare.getCognome()+" non esiste nell'Anagrafica Nazionale"));
				}else if  (StringUtils.isNullOrEmpty(anagraficaVerificata.getNome()) 
						|| StringUtils.isNullOrEmpty(anagraficaVerificata.getCognome())){
						validationErrors.add(new ExcelValidatorMessages(Scheda.ISCRITTI, "Il nominativo "+anagraficaDaVerificare.getNome()+" "+anagraficaDaVerificare.getCognome()+"  ha un idNazionale ["+anagraficaDaVerificare.getId()+"] che non esiste nell'Anagrafica Nazionale"));
				}else if (!anagraficaDaVerificare.getNome().trim().equalsIgnoreCase(anagraficaVerificata.getNome().trim()) 
					   || !anagraficaDaVerificare.getCognome().trim().equalsIgnoreCase(anagraficaVerificata.getCognome().trim())){
					validationErrors.add(new ExcelValidatorMessages(Scheda.ISCRITTI, "Il nominativo "+anagraficaDaVerificare.getNome()+" "+anagraficaDaVerificare.getCognome()+" del giocatore associato all'idNazionale ["+anagraficaDaVerificare.getId()+"] non corrisponde a quello sull'Anagrafica Nazionale: "+anagraficaVerificata.getNome()+" "+anagraficaVerificata.getCognome()));
				}
			}
			anagraficaDaVerificare.setId(anagraficaVerificata.getId());
			anagraficaRowFound.set(index, anagraficaDaVerificare); //ripristino i dati precedenti aggiungendo l'eventuale id trovato da query
		}
		if (!validationErrors.isEmpty()){
			throw new ExcelValidationException(validationErrors);
		}
	}
	
	private static void deleteAndInsertPartita(Torneo torneo, Map<Integer, Integer> mappaIdExcelVsIdGSheets) throws IOException{
		MyLogger.getLogger().entering("ReportPublisher", "deleteAndInsertPartita");
		
		List<SheetRow> partiteRow = ExcelGSheetsBridge.getPartiteRowByTorneo(torneo, mappaIdExcelVsIdGSheets);
		
		String year = ExcelGSheetsBridge.obtainYearTorneo(torneo);
		
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		String sheetNamePartite = PartitaRow.SHEET_PARTITE_NAME;
		
		//Basta un oggetto: tanto l'id del torneo è sempre lo stesso.
		PartitaRow partitaRowDiRicerca = new PartitaRow();
		partitaRowDiRicerca.setIdTorneo(ExcelGSheetsBridge.obtainIdTorneo(torneo));
		List<Integer> partiteRowFound = GSheetsInterface.findNumPartiteRowsByIdTorneo(spreadSheetIdTornei, partitaRowDiRicerca);

		if (partiteRowFound != null && !partiteRowFound.isEmpty()){
			MyLogger.getLogger().info("Cancellazione di "+partiteRowFound.size()+" partite del torneo "+torneo);
			GSheetsInterface.deleteRowsByNumRow(spreadSheetIdTornei, sheetNamePartite, partiteRowFound);
		}
		
		if (partiteRow != null && !partiteRow.isEmpty()){
			MyLogger.getLogger().info("Inserimento di "+partiteRow.size()+" partite del torneo "+torneo);
			GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNamePartite, partiteRow);
		}
		
		MyLogger.getLogger().exiting("ReportPublisher", "deleteAndInsertPartita");
	}

	private static void deleteAndInsertClassifica(Torneo torneo, Map<Integer, Integer> mappaIdExcelVsIdGSheets) throws IOException{
		MyLogger.getLogger().entering("ReportPublisher", "deleteAndInsertClassifica");
		
		List<SheetRow> partiteRow = ExcelGSheetsBridge.getPartiteRowByTorneo(torneo, mappaIdExcelVsIdGSheets);
		List<SheetRow> classificaRows = ExcelGSheetsBridge.getClassificaRowsByTorneo(torneo, mappaIdExcelVsIdGSheets, partiteRow);
		
		String year = ExcelGSheetsBridge.obtainYearTorneo(torneo);
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		String sheetNameClassifiche = ClassificheRow.SHEET_CLASSIFICHE;
		
		ClassificheRow classificheRowDiRicerca = new ClassificheRow();
		classificheRowDiRicerca.setIdTorneo(ExcelGSheetsBridge.obtainIdTorneo(torneo));
		List<Integer> classificheRowFound = GSheetsInterface.findClassificaRowsByIdTorneo(spreadSheetIdTornei, classificheRowDiRicerca);

		if (classificheRowFound != null && !classificheRowFound.isEmpty()){
			MyLogger.getLogger().info("Cancellazione di "+classificheRowFound.size()+" giocatori in classifica del torneo "+torneo);
			GSheetsInterface.deleteRowsByNumRow(spreadSheetIdTornei, sheetNameClassifiche, classificheRowFound);
		}

		if (classificaRows != null && !classificaRows.isEmpty()){
			if (torneo.isConcluso() && TipoTorneo.prevedeClassifica(torneo.getSchedaTorneo().getTipoTorneo())){
				MyLogger.getLogger().info("Inserimento di "+classificaRows.size()+" giocatori in classifica del torneo "+torneo);
				GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNameClassifiche, classificaRows);
			}
		}
		MyLogger.getLogger().exiting("ReportPublisher", "deleteAndInsertClassifica");
	}
	
}
