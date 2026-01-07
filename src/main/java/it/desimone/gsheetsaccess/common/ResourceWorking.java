package it.desimone.gsheetsaccess.common;

import it.desimone.utils.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ResourceWorking {
	
	public static final String ROOT;
	
	static{
		ROOT = new File("").getAbsolutePath();
		MyLogger.getLogger().finer("ROOT: "+ROOT);	
	}
	
	
	private static final String googleClientSecret = "client_secret_manager.json";
	
	public static String workingAreaPath(){
		return ROOT+File.separator+"working"+File.separator+"tmp";
	}
	
	public static String doneAreaPath(){
		return ROOT+File.separator+"working"+File.separator+"done";
	}
	
	public static String errorAreaPath(){
		return ROOT+File.separator+"working"+File.separator+"error";
	}
	
	public static String tabellinoLoaderInputAreaPath(){
		return ROOT+File.separator+"working"+File.separator+"input"+File.separator+"tabellinoLoader";
	}
	public static String tabellinoLoaderOutputAreaPath(){
		return ROOT+File.separator+"working"+File.separator+"output"+File.separator+"tabellinoLoader";
	}
	
	public static String googleClientSecretPath(){
		return ROOT+File.separator+"resources"+File.separator+"google"+File.separator+googleClientSecret;
	}
	
	public static String velocityTemplatePath(){
		return ROOT+File.separator+"resources"+File.separator+"velocity";
	}
	
	public static String htmlPagesPath(){
		return ROOT+File.separator+"working"+File.separator+"htmlPages";
	}
	
	public static String listTournamentsPath(){
		return htmlPagesPath()+File.separator+"LISTA_TORNEI";
	}

	public static String listRankingsPath(){
		return htmlPagesPath()+File.separator+"RANKING";
	}
	
	public static String statsPath(){
		return htmlPagesPath()+File.separator+"STATISTICHE";
	}
	
	
	public static String rankingThresholds(){
		return ROOT+File.separator+"resources"+File.separator+"RankingThresholdsNew.xml";
	}
	
	public static File googleCredentials(){
		return new java.io.File(ROOT+File.separator+"resources"+File.separator+"google", ".credentials/RisiKo Data Manager");
	}
	
	private static Properties tournamentsDataProperties(){
		Properties props = new Properties();
		FileInputStream propertiesStream = null;
		try {
			propertiesStream = new FileInputStream(new File(ROOT+File.separator+"working"+File.separator+"htmlpublisher"+File.separator+"tournamentsdata.properties"));
			props.load(propertiesStream);
		} catch (IOException e) {
			MyLogger.getLogger().severe("IOException nel caricamento del file di Properties: "+e.getMessage());
		} finally {
			if (propertiesStream != null) {
				try {
					propertiesStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return props;
	}
	
	public static String getLastTournamentDate(String year){
		return tournamentsDataProperties().getProperty("lastupdatedate"+year);
	}
	
	public static void setLastTournamentDate(String year, String value){
		FileOutputStream out = null; 
		Properties props = tournamentsDataProperties();
		try{
			out = new FileOutputStream(new File(ROOT+File.separator+"working"+File.separator+"htmlpublisher"+File.separator+"tournamentsdata.properties"));
			props.setProperty("lastupdatedate"+year, value);
			props.store(out, null);
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		setLastTournamentDate("2019", "pippo");
	}
}
