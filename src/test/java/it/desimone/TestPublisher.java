package it.desimone;

import it.desimone.gsheetsaccess.ReportPublisher;
import it.desimone.gsheetsaccess.gdrive.file.ReportAnalyzer;
import it.desimone.gsheetsaccess.gdrive.file.ReportDriveData;
import it.desimone.risiko.torneo.dto.Torneo;
import it.desimone.utils.MyLogger;

public class TestPublisher {

	public static void main(String[] args) {
		pubblicaTorneoByFile();
	}

	private static void pubblicaTorneoByFile(){
		String fileName = "28aCoppa8.xls";
		ReportDriveData reportDriveData = new ReportDriveData();
		reportDriveData.setFileName(fileName);
		reportDriveData.setParentFolderName("RCU MODENA");
		
		MyLogger.getLogger().info("Inizio elaborazione di "+reportDriveData);
		try{
			Torneo torneo = ReportAnalyzer.analyzeExcelReport(reportDriveData);
			MyLogger.getLogger().info("Validato report "+reportDriveData);
			ReportPublisher.pubblicaTorneo(torneo);
			MyLogger.getLogger().info("Pubblicato report "+reportDriveData);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
