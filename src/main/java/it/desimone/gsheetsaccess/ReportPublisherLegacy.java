package it.desimone.gsheetsaccess;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.common.ExcelValidationException;
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
import it.desimone.risiko.torneo.dto.GiocatoreDTO;
import it.desimone.risiko.torneo.dto.Torneo;
import it.desimone.utils.MyException;
import it.desimone.utils.MyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class ReportPublisherLegacy {

	public static void main(String[] args) {
		MyLogger.setConsoleLogLevel(Level.INFO);

		try{
			List<ReportDriveData> publishedReport = GDriveDownloader.downloadReport(true);
			if (publishedReport != null && !publishedReport.isEmpty()){
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
			}
		}catch(Exception e){
			MyLogger.getLogger().severe("Errore di accesso a google drive "+e.getMessage());
			sendErrorMail(null, e.getMessage());
		}
	}

	
	private static void sendErrorMail(ReportDriveData reportDriveData, String errorMessage){
		GmailAccess gmailAccess = new GmailAccess();
		String subject = "ERRORE NELL'ELABORAZIONE DEI REPORT";
		String[] to = {"risiko.it@gmail.com"};

		if (reportDriveData != null){
			errorMessage = "Report "+reportDriveData.toString()+"\n"+errorMessage;
		}
		
		MimeMessage mimeMessage;
		try {
			MyLogger.getLogger().fine("Sending mail to "+to+" with subject "+subject);
			mimeMessage = GmailAccess.createEmail(to, null, null, null, subject, errorMessage);
			gmailAccess.sendMessage("me", mimeMessage);
		} catch (MessagingException e) {
			MyLogger.getLogger().severe("Error sending mail to "+to+": "+e.getMessage());
		} catch (IOException e) {
			MyLogger.getLogger().severe("Error sending mail to "+to+": "+e.getMessage());
		}
	}

	private static void pubblicaTorneo(Torneo torneo) throws IOException{
		insertOrUpdateTorneo(torneo);
		Map<Integer, Integer> mappaIdExcelVsIdGSheets = insertOrUpdateGiocatori(torneo);
		deleteAndInsertPartita(torneo, mappaIdExcelVsIdGSheets);
		deleteAndInsertClassifica(torneo, mappaIdExcelVsIdGSheets);
	}
	
	
	private static void insertOrUpdateTorneo(Torneo torneo) throws IOException{
		TorneiRow torneoRow = ExcelGSheetsBridge.getTorneoRowByTorneo(torneo);
		
		String year = ExcelGSheetsBridge.obtainYearTorneo(torneo);
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		String sheetNameTornei = TorneiRow.SHEET_TORNEI_NAME;
		SheetRow torneoRowFound = GSheetsInterface.findSheetRowByKey(spreadSheetIdTornei, sheetNameTornei, torneoRow);
		
		if (torneoRowFound != null){
			torneoRow.setSheetRowNumber(torneoRowFound.getSheetRowNumber());
			List<SheetRow> rows = new ArrayList<SheetRow>();
			rows.add(torneoRow);
			GSheetsInterface.updateRows(spreadSheetIdTornei, sheetNameTornei, rows, true);
		}else{
			GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNameTornei, Collections.singletonList((SheetRow)torneoRow));
		}
	}
	
	
	private static Map<Integer, Integer> insertOrUpdateGiocatori(Torneo torneo) throws IOException{
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
			
			Integer maxId = GSheetsInterface.findMaxIdAnagrafica(spreadSheetIdAnagraficaRidotta);
			int index = 0;
			List<GiocatoreDTO> partecipanti = torneo.getPartecipanti();
			for (SheetRow[] anagrafica: anagrafiche){
				AnagraficaGiocatoreRidottaRow 	anagraficaGiocatoreRidottaRow 	= (AnagraficaGiocatoreRidottaRow) anagrafica[0];
				AnagraficaGiocatoreRow 			anagraficaGiocatoreRow 			= (AnagraficaGiocatoreRow) anagrafica[1];
				
				SheetRow anagraficaRowFound = GSheetsInterface.findSheetRowByKey(spreadSheetIdAnagraficaRidotta, sheetNameAnagraficaRidotta, anagraficaGiocatoreRidottaRow);
				if (anagraficaRowFound == null){
					anagraficaGiocatoreRidottaRow.setId(++maxId);
					anagraficheRidotteDaAggiungere.add(anagraficaGiocatoreRidottaRow);
//					GSheetsInterface.appendRows(spreadSheetIdAnagraficaRidotta, AnagraficaGiocatoreRidottaRow.SHEET_ANAGRAFICA_NAME, Collections.singletonList((SheetRow)anagraficaGiocatoreRidottaRow));
				}else{
					anagraficaGiocatoreRidottaRow = (AnagraficaGiocatoreRidottaRow) GSheetsInterface.findSheetRowByLineNumber(spreadSheetIdAnagraficaRidotta, sheetNameAnagraficaRidotta, anagraficaGiocatoreRidottaRow);
				}
				
				anagraficaGiocatoreRow.setId(anagraficaGiocatoreRidottaRow.getId());
				SheetRow giocatoriRowFound = GSheetsInterface.findSheetRowByKey(spreadSheetIdTornei, sheetNameGiocatori, anagraficaGiocatoreRow);
				
				if (giocatoriRowFound != null){
					anagraficaGiocatoreRow.setSheetRowNumber(giocatoriRowFound.getSheetRowNumber());
					anagraficheDaAggiornare.add(anagraficaGiocatoreRow);
//					List<SheetRow> rows = new ArrayList<SheetRow>();
//					rows.add(anagraficaGiocatoreRow);
//					GSheetsInterface.updateRows(spreadSheetIdTornei, sheetNameGiocatori, rows, true);
				}else{
					anagraficheDaAggiungere.add(anagraficaGiocatoreRow);
//					GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNameGiocatori, Collections.singletonList((SheetRow)anagraficaGiocatoreRow));
				}
				
				//E' un po' una zozzata: si dà per scontato che la lista dei giocatori e l'array contengano stesso giocatore per stesso indice.
				mappaIdExcelVsIdGSheets.put(partecipanti.get(index).getId(), anagraficaGiocatoreRidottaRow.getId());
				
				index++;
			}
			if (!anagraficheRidotteDaAggiungere.isEmpty()){
				GSheetsInterface.appendRows(spreadSheetIdAnagraficaRidotta, sheetNameAnagraficaRidotta, anagraficheRidotteDaAggiungere);
				MyLogger.getLogger().info("Aggiunte "+anagraficheRidotteDaAggiungere.size()+" anagrafiche ridotte");
			}
			if (!anagraficheDaAggiornare.isEmpty()){
				GSheetsInterface.updateRows(spreadSheetIdTornei, sheetNameGiocatori, anagraficheDaAggiornare, true);
				MyLogger.getLogger().info("Aggiornate "+anagraficheDaAggiornare.size()+" anagrafiche");
			}
			if (!anagraficheDaAggiungere.isEmpty()){
				GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNameGiocatori, anagraficheDaAggiungere);
				MyLogger.getLogger().info("Aggiunte "+anagraficheDaAggiungere.size()+" anagrafiche");
			}
		}
		
		return mappaIdExcelVsIdGSheets;	
	}

	
	private static void deleteAndInsertPartita(Torneo torneo, Map<Integer, Integer> mappaIdExcelVsIdGSheets) throws IOException{
		List<SheetRow> partiteRow = ExcelGSheetsBridge.getPartiteRowByTorneo(torneo, mappaIdExcelVsIdGSheets);
		
		String year = ExcelGSheetsBridge.obtainYearTorneo(torneo);
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		String sheetNamePartite = PartitaRow.SHEET_PARTITE_NAME;
		
		//Basta un oggetto: tanto l'id del torneo è sempre lo stesso.
		PartitaRow partitaRowDiRicerca = new PartitaRow();
		partitaRowDiRicerca.setIdTorneo(ExcelGSheetsBridge.obtainIdTorneo(torneo));
		List<SheetRow> partiteRowFound = GSheetsInterface.findSheetRowsByCols(spreadSheetIdTornei, sheetNamePartite, partitaRowDiRicerca, PartitaRow.ColPosition.ID_TORNEO);

		if (partiteRowFound != null && !partiteRowFound.isEmpty()){
			MyLogger.getLogger().info("Cancellazione di "+partiteRowFound.size()+" partite del torneo "+torneo);
			GSheetsInterface.deleteRows(spreadSheetIdTornei, sheetNamePartite, partiteRowFound);
		}
		
		if (partiteRow != null && !partiteRow.isEmpty()){
			MyLogger.getLogger().info("Inserimento di "+partiteRow.size()+" partite del torneo "+torneo);
			GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNamePartite, partiteRow);
		}
	}

	private static void deleteAndInsertClassifica(Torneo torneo, Map<Integer, Integer> mappaIdExcelVsIdGSheets) throws IOException{
		List<SheetRow> partiteRow = ExcelGSheetsBridge.getPartiteRowByTorneo(torneo, mappaIdExcelVsIdGSheets);
		List<SheetRow> classificaRows = ExcelGSheetsBridge.getClassificaRowsByTorneo(torneo, mappaIdExcelVsIdGSheets, partiteRow);
		
		String year = ExcelGSheetsBridge.obtainYearTorneo(torneo);
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		String sheetNameClassifiche = ClassificheRow.SHEET_CLASSIFICHE;
		
		ClassificheRow classificheRowDiRicerca = new ClassificheRow();
		classificheRowDiRicerca.setIdTorneo(ExcelGSheetsBridge.obtainIdTorneo(torneo));
		List<SheetRow> classificheRowFound = GSheetsInterface.findSheetRowsByCols(spreadSheetIdTornei, sheetNameClassifiche, classificheRowDiRicerca, ClassificheRow.ColPosition.ID_TORNEO);
		
		if (classificheRowFound != null && !classificheRowFound.isEmpty()){
			MyLogger.getLogger().info("Cancellazione di "+classificheRowFound.size()+" giocatori in classifica del torneo "+torneo);
			GSheetsInterface.deleteRows(spreadSheetIdTornei, sheetNameClassifiche, classificheRowFound);
		}
		
		if (classificaRows != null && !classificaRows.isEmpty()){
			if (torneo.isConcluso()){
				MyLogger.getLogger().info("Inserimento di "+classificaRows.size()+" giocatori in classifica del torneo "+torneo);
				GSheetsInterface.appendRows(spreadSheetIdTornei, sheetNameClassifiche, classificaRows);
			}
		}
	}
	
}
