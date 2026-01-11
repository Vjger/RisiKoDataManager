package it.desimone;

import java.util.logging.Level;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.common.Configurator.Environment;
import it.desimone.gsheetsaccess.common.GDriveUtils;
import it.desimone.utils.MyLogger;

public class GDriveUtilsTest {

	public static void main(String[] args) {
		MyLogger.setConsoleLogLevel(Level.ALL);
		Configurator.loadConfiguration(Environment.STAGE);
		testRestore();
	}

	public static void testBackup(){
		try {
			GDriveUtils.backup();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testRestore(){
		try {
			String anagraficaRidottaId = Configurator.getAnagraficaRidottaSheetId();
			String backupAnagraficaRidottaId = "1AhXrPvb32l6Lt04AjaG2nqvaoMoOvyONszhb3H0Xc8o";
			GDriveUtils.restore(anagraficaRidottaId, backupAnagraficaRidottaId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
