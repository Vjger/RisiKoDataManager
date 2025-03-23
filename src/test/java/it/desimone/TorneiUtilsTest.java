package it.desimone;

import java.util.List;
import java.util.logging.Level;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.common.Configurator.Environment;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRidottaRow;
import it.desimone.gsheetsaccess.utils.TorneiUtils;
import it.desimone.utils.MyLogger;

public class TorneiUtilsTest {

	public static void main(String[] args) {
		Configurator.loadConfiguration(Environment.PRODUCTION);
		MyLogger.setConsoleLogLevel(Level.INFO);

//		List<AnagraficaGiocatoreRidottaRow> orfani = TorneiUtils.findOrphansInMemory();
//		
//		MyLogger.getLogger().info("Trovati "+orfani.size()+" orfani");
//		for (AnagraficaGiocatoreRidottaRow orfano: orfani){
//			MyLogger.getLogger().info(orfano.toString());
//		}
		TorneiUtils.printScorePlayers();
	
		System.out.println("END");
	}

}
