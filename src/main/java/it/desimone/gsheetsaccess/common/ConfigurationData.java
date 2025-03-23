package it.desimone.gsheetsaccess.common;

import java.util.Map;
import java.util.Set;

public class ConfigurationData {

	private Map<Integer, String> tournamentsSheetId;
	private String anagraficaSheetId;
	private String rcuFolderId;
	private String doneFolderId;
	private String errorFolderId;
	private String backupsFolderId;
	private String templateTorneiId;
	private String playersDataSheetId;
	private String reportElaborazioniSheetId;
	
	public Map<Integer, String> getTournamentsSheetId() {
		return tournamentsSheetId;
	}
	public void setTournamentsSheetId(Map<Integer, String> tournamentsSheetId) {
		this.tournamentsSheetId = tournamentsSheetId;
	}
	public String getAnagraficaSheetId() {
		return anagraficaSheetId;
	}
	public void setAnagraficaSheetId(String anagraficaSheetId) {
		this.anagraficaSheetId = anagraficaSheetId;
	}
	public String getRcuFolderId() {
		return rcuFolderId;
	}
	public void setRcuFolderId(String rcuFolderId) {
		this.rcuFolderId = rcuFolderId;
	}
	public String getDoneFolderId() {
		return doneFolderId;
	}
	public void setDoneFolderId(String doneFolderId) {
		this.doneFolderId = doneFolderId;
	}
	public String getErrorFolderId() {
		return errorFolderId;
	}
	public void setErrorFolderId(String errorFolderId) {
		this.errorFolderId = errorFolderId;
	}
	public String getBackupsFolderId() {
		return backupsFolderId;
	}
	public void setBackupsFolderId(String backupsFolderId) {
		this.backupsFolderId = backupsFolderId;
	}
	public String getTemplateTorneiId() {
		return templateTorneiId;
	}
	public void setTemplateTorneiId(String templateTorneiId) {
		this.templateTorneiId = templateTorneiId;
	}
	public String getPlayersDataSheetId() {
		return playersDataSheetId;
	}
	public void setPlayersDataSheetId(String playersDataSheetId) {
		this.playersDataSheetId = playersDataSheetId;
	}
	public String getReportElaborazioniSheetId() {
		return reportElaborazioniSheetId;
	}
	public void setReportElaborazioniSheetId(String reportElaborazioniSheetId) {
		this.reportElaborazioniSheetId = reportElaborazioniSheetId;
	}
	public void setByConfigurationMap(Map<String, String> configurationMap) {
		Set<String> parameterNames = configurationMap.keySet();
		for (String parameterName: parameterNames) {
			String parameterValue = configurationMap.get(parameterName);
			switch (parameterName) {
			case "PLAYERS_DATA_SHEET_ID":
				setPlayersDataSheetId(parameterValue);
				break;
			case "RCU_FOLDER_ID":
				setRcuFolderId(parameterValue);
				break;
			case "ANAGRAFICA_SHEET_ID":
				setAnagraficaSheetId(parameterValue);
				break;				
			case "DONE_FOLDER_ID":
				setDoneFolderId(parameterValue);
				break;		
			case "ERROR_FOLDER_ID":
				setErrorFolderId(parameterValue);
				break;		
			case "BACKUPS_FOLDER_ID":
				setBackupsFolderId(parameterValue);
				break;
			case "TEMPLATE_TORNEI_ID":
				setTemplateTorneiId(parameterValue);
				break;
			case "REPORT_ELABORAZIONI_SHEET_ID":
				setReportElaborazioniSheetId(parameterValue);
				break;					
			default:
				throw new IllegalArgumentException("Nome parametro non trovato: "+parameterName);
			}
		}
	}
}
