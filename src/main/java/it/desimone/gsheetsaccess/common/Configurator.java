package it.desimone.gsheetsaccess.common;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import it.desimone.gsheetsaccess.gsheets.dto.ConfigurationMapRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRowFactory.SheetRowType;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiSheetIdRow;
import it.desimone.gsheetsaccess.gsheets.facade.GSheetsInterface;
import it.desimone.utils.MyLogger;

public class Configurator {

	private static ConfigurationData configurationData = new ConfigurationData();
	private Configurator() {}
	
	private static Environment environment;
	
	public enum Environment{
		STAGE, PRODUCTION
	}
	
	static {
		loadConfiguration(Environment.PRODUCTION);
	}
	
	public static void switchEnvironment(){
		switch (environment) {
		case STAGE:
			loadConfiguration(Environment.PRODUCTION);
			break;
		case PRODUCTION:
			loadConfiguration(Environment.STAGE);
			break;
		default:
			break;
		}
	}
	
	public static Environment getEnvironment(){
		return environment;
	}
	
	private static void loadPropertiesConfiguration(Environment env) {
		switch (env) {
		case STAGE:
			ConfiguratorProperties.loadConfiguration(ConfiguratorProperties.Environment.STAGE);
			break;
		case PRODUCTION:
			ConfiguratorProperties.loadConfiguration(ConfiguratorProperties.Environment.PRODUCTION);
			break;
		default:
			break;
		}
	}
	
	public static void loadConfiguration(Environment env) {
		loadPropertiesConfiguration(env);
		try {

			List<ConfigurationMapRow> configurazioni = GSheetsInterface.getAllRows(ConfiguratorProperties.getConfigurationSheetId(), SheetRowType.ConfigurationMap);
			if (CollectionUtils.isNotEmpty(configurazioni)) {
				Map<String, String> mappaConfigurazioni = new HashMap<String, String>();
				for (ConfigurationMapRow row: configurazioni) {
					mappaConfigurazioni.put(row.getChiave(), row.getValore());
				}
				configurationData.setByConfigurationMap(mappaConfigurazioni);
			}

			List<TorneiSheetIdRow> torneiSheetId = GSheetsInterface.getAllRows(ConfiguratorProperties.getConfigurationSheetId(), SheetRowType.TorneiSheetId);
			if (CollectionUtils.isNotEmpty(torneiSheetId)) {
				Map<Integer, String> mappaTornei = new HashMap<Integer, String>();
				for (TorneiSheetIdRow row: torneiSheetId) {
					mappaTornei.put(Integer.valueOf(row.getAnnoTorneo()), row.getSheetIdTorneo());
				}
				configurationData.setTournamentsSheetId(mappaTornei);
			}
			environment = env;
		} catch (IOException e) {
			MyLogger.getLogger().severe("Errore nell'acquisizione della configurazione: "+e.getMessage());
		} 
	}
	
	public static String getRCUFolderId(){
		String folderId = configurationData.getRcuFolderId();
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID RCU Folder:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getAnagraficaRidottaSheetId(){
		String folderId = configurationData.getAnagraficaSheetId();
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID Anagrafica Ridotta:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getBlackListSheetId(){
		String folderId = configurationData.getPlayersDataSheetId();
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID BlackList:<<"+folderId+">>");
		return folderId;
	}

	public static List<String> getTorneiSheetIds(){
		List<String> torneiSheetIds = configurationData.getTournamentsSheetId().values().stream().collect(Collectors.toList());
		return torneiSheetIds;
	}
	
	public static List<Integer> getTorneiYears(){
		List<Integer> torneiYears = configurationData.getTournamentsSheetId().keySet().stream().collect(Collectors.toList());
		return torneiYears;
	}
	
	public static String getTorneiSheetId(String year){
		String folderId = configurationData.getTournamentsSheetId().get(Integer.valueOf(year));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID Tornei per l'anno "+year+":<<"+folderId+">>");
		return folderId;
	}
	
	public static void setTorneiSheetId(String year, String spreadSheetIdTornei) throws IOException{
		TorneiSheetIdRow sheetIdTorneiRow = new TorneiSheetIdRow();
		sheetIdTorneiRow.setAnnoTorneo(year);
		sheetIdTorneiRow.setSheetIdTorneo(spreadSheetIdTornei);
		GSheetsInterface.appendRows(ConfiguratorProperties.getConfigurationSheetId(), TorneiSheetIdRow.SHEET_TORNEI_NAME, Collections.singletonList((SheetRow)sheetIdTorneiRow));
		loadConfiguration(getEnvironment());
	}
		
	public static String getDoneFolderId(){
		String folderId = configurationData.getDoneFolderId();
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("DONE Folder Id:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getErrorFolderId(){
		String folderId = configurationData.getErrorFolderId();
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ERROR Folder Id:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getReportElaborazioniSheetId(){
		String sheetId = configurationData.getReportElaborazioniSheetId();
		if (sheetId != null) sheetId = sheetId.trim();
		MyLogger.getLogger().finest("ID Report Elaborazioni:<<"+sheetId+">>");
		return sheetId;
	}

	public static String getBackupsFolderId(){
		String sheetId = configurationData.getBackupsFolderId();
		if (sheetId != null) sheetId = sheetId.trim();
		MyLogger.getLogger().finest("Backups Folder:<<"+sheetId+">>");
		return sheetId;
	}
	
	public static String getTemplateTorneiSheetId(){
		String folderId = configurationData.getTemplateTorneiId();
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID Template Tornei:<<"+folderId+">>");
		return folderId;
	}
	
	
}
