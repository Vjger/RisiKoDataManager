package it.desimone.gsheetsaccess.batch;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.common.Configurator.Environment;
import it.desimone.gsheetsaccess.dto.TorneoPubblicato;
import it.desimone.gsheetsaccess.utils.TorneiUtils;
import it.desimone.utils.MyLogger;

import java.util.List;
import java.util.logging.Level;

public class TournamentsLoader {
	
	public static void main(String[] args) {
		MyLogger.setConsoleLogLevel(Level.INFO);
		
		Configurator.loadConfiguration(Environment.PRODUCTION);

		List<TorneoPubblicato> torneiPubblicati = TorneiUtils.caricamentoTornei("2019");
		
		MyLogger.getLogger().info("Caricati "+torneiPubblicati.size()+" tornei");
	}

}
