package it.desimone.gsheetsaccess.common;

import it.desimone.utils.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfiguratorProperties {
	
public static final String ROOT = new File("").getAbsolutePath();
public static final String PATH_CONFIGURATION = ROOT+File.separator+"configuration"; //ROOT+File.separator+"configuration";
private static final String CONFIG_FILE_STAGE = "configurationStage.properties";	
private static final String CONFIG_FILE_PROD = "configurationProd.properties";	
private volatile static Properties properties = new Properties();

private static Environment environment;

	public enum Environment{
		STAGE, PRODUCTION
	}

	static{
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
	
	public static void loadConfiguration(Environment env){
		String configFileName = null;
		try {
			switch (env) {
			case STAGE:
				configFileName = CONFIG_FILE_STAGE;
				break;
			case PRODUCTION:
				configFileName = CONFIG_FILE_PROD;
				break;
			default:
				break;
			}
			FileInputStream propertiesStream = new FileInputStream(new File(PATH_CONFIGURATION+File.separator+configFileName));
			properties.load(propertiesStream);
			environment = env;
		} catch (IOException e) {
			MyLogger.getLogger().severe("IOException nel caricamento del file di Properties: "+e.getMessage());
		}
	}
	
	
	public static Environment getEnvironment(){
		return environment;
	}
	
	public static void setProperty(String key, String value){
		String configFileName = null;
		switch (environment) {
		case STAGE:
			configFileName = CONFIG_FILE_STAGE;
			break;
		case PRODUCTION:
			configFileName = CONFIG_FILE_PROD;
			break;
		default:
			break;
		}
		FileOutputStream out = null; 
		try{
			out = new FileOutputStream(new File(PATH_CONFIGURATION+File.separator+configFileName));
			properties.setProperty(key, value);
			properties.store(out, null);
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getRCUFolderId(){
		String folderId = ((String)properties.get("rcuFolderId"));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID RCU Folder:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getAnagraficaRidottaSheetId(String year){
		String folderId = ((String)properties.get("spreadSheetIdAnagraficaRidotta"+year));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID Anagrafica Ridotta:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getAnagraficaRidottaSheetId(){
		String folderId = ((String)properties.get("spreadSheetIdAnagraficaRidotta"+2019));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID Anagrafica Ridotta:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getBlackListSheetId(){
		String folderId = ((String)properties.get("spreadSheetIdBlacklist"));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID BlackList:<<"+folderId+">>");
		return folderId;
	}

	public static List<String> getTorneiSheetIds(){
		Integer startingYear = 2019;
		List<String> torneiSheetIds = new ArrayList<String>();
		String torneoSheetId = getTorneiSheetId(startingYear.toString());
		while (torneoSheetId != null){
			torneiSheetIds.add(torneoSheetId);
			startingYear++;
			torneoSheetId = getTorneiSheetId(startingYear.toString());
		}
		return torneiSheetIds;
	}
	
	public static List<Integer> getTorneiYears(){
		Integer startingYear = 2019;
		List<Integer> torneiYears = new ArrayList<Integer>();
		String torneoSheetId = getTorneiSheetId(startingYear.toString());
		while (torneoSheetId != null){
			torneiYears.add(startingYear);
			startingYear++;
			torneoSheetId = getTorneiSheetId(startingYear.toString());
		}
		return torneiYears;
	}
	
	public static String getTorneiSheetId(String year){
		String folderId = ((String)properties.get("spreadSheetIdTornei"+year));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID Tornei per l'anno "+year+":<<"+folderId+">>");
		return folderId;
	}
	
	public static void setTorneiSheetId(String year, String spreadSheetIdTornei){
		setProperty("spreadSheetIdTornei"+year, spreadSheetIdTornei);
	}
		
	public static String getDoneFolderId(){
		String folderId = ((String)properties.get("DONEFolderId"));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("DONE Folder Id:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getErrorFolderId(){
		String folderId = ((String)properties.get("ERRORFolderId"));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ERROR Folder Id:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getReportElaborazioniSheetId(){
		String sheetId = ((String)properties.get("spreadSheetIdReportElaborazioni"));
		if (sheetId != null) sheetId = sheetId.trim();
		MyLogger.getLogger().finest("ID Report Elaborazioni:<<"+sheetId+">>");
		return sheetId;
	}
	
	public static String getRankingSheetId(){
		String sheetId = ((String)properties.get("spreadSheetIdRanking"));
		if (sheetId != null) sheetId = sheetId.trim();
		MyLogger.getLogger().finest("ID Ranking:<<"+sheetId+">>");
		return sheetId;
	}

	public static String getBackupsFolderId(){
		String sheetId = ((String)properties.get("backupsFolder"));
		if (sheetId != null) sheetId = sheetId.trim();
		MyLogger.getLogger().finest("Backups Folder:<<"+sheetId+">>");
		return sheetId;
	}
	
	public static String getTemplateTorneiSheetId(){
		String folderId = ((String)properties.get("spreadSheetIdTemplateTornei"));
		if (folderId != null) folderId = folderId.trim();
		MyLogger.getLogger().finest("ID Template Tornei:<<"+folderId+">>");
		return folderId;
	}
	
	public static String getConfigurationSheetId(){
		String sheetId = ((String)properties.get("configurationSpreadSheetId"));
		if (sheetId != null) sheetId = sheetId.trim();
		MyLogger.getLogger().finest("configurationSpreadSheetId:<<"+sheetId+">>");
		return sheetId;
	}
}
