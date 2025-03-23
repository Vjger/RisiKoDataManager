package it.desimone;

import it.desimone.gsheetsaccess.dto.TorneoPubblicato;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiRow;
import it.desimone.gsheetsaccess.htmlpublisher.HtmlPublisher;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;
import it.desimone.utils.MyLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class HtmlPublisherTest {

	public static void main(String[] args) {
		MyLogger.setConsoleLogLevel(Level.ALL);
		testPubblicazioneTornei();

	}

	private static void testPubblicazioneTornei(){
		List<TorneoPubblicato> tornei = new ArrayList<TorneoPubblicato>();
		
		TorneiRow torneoRow = new TorneiRow();
		torneoRow.setIdTorneo("20181105 - SABAUDIA [RCU]");
		torneoRow.setTipoTorneo(TipoTorneo.MASTER.name());
		
		List<PartitaRow> partite = new ArrayList<PartitaRow>();
		PartitaRow partita1 = new PartitaRow();
		partita1.setNumeroTurno(1);
		partita1.setNumeroTavolo(1);
		partita1.setDataTurno("05/01/2019");
		partita1.setIdGiocatoreVincitore(101);
		partita1.setNominativoVincitore("Marco De Simone");
		partita1.setIdGiocatore1(101);
		partita1.setIdGiocatore2(102);
		partita1.setIdGiocatore3(103);
//		partita1.setIdGiocatore4(104);
//		partita1.setIdGiocatore5(105);
		partita1.setNominativoGiocatore1("Marco De Simone");
		partita1.setPunteggioGiocatore1(40.0);
		partita1.setNominativoGiocatore2("Mario Rossi");
		partita1.setPunteggioGiocatore2(38.0);
		partita1.setNominativoGiocatore3("Aristo Gitone");
		partita1.setPunteggioGiocatore3(11.0);
//		partita1.setNominativoGiocatore4("");
//		partita1.setPunteggioGiocatore4(0.0);
//		partita1.setNominativoGiocatore5("");
//		partita1.setPunteggioGiocatore5(0.0);
		
		PartitaRow partita2 = new PartitaRow();
		partita2.setNumeroTurno(1);
		partita2.setNumeroTavolo(2);
		partita2.setDataTurno("05/01/2019");
		partita2.setNominativoVincitore("Marco De Simone");
		partita2.setIdGiocatore1(101);
		partita2.setIdGiocatore2(102);
		partita2.setIdGiocatore3(103);
		partita2.setIdGiocatore4(104);
//		partita2.setIdGiocatore5(105);
		partita2.setIdGiocatoreVincitore(102);
		partita2.setNominativoGiocatore1("Marco De Simone");
		partita2.setPunteggioGiocatore1(40.1);
		partita2.setNominativoGiocatore2("Mario Rossi");
		partita2.setPunteggioGiocatore2(40.0);
		partita2.setNominativoGiocatore3("Aristo Gitone");
		partita2.setPunteggioGiocatore3(11.0);
		partita2.setNominativoGiocatore4("Luisa Bianchi");
		partita2.setPunteggioGiocatore4(10.0);
//		partita2.setNominativoGiocatore5("");
//		partita2.setPunteggioGiocatore5(0.0);
		
		PartitaRow partita3 = new PartitaRow();
		partita3.setNumeroTurno(1);
		partita3.setNumeroTavolo(3);
		partita3.setDataTurno("05/01/2019");
		partita3.setNominativoVincitore("Marco De Simone");
		partita3.setIdGiocatore1(101);
		partita3.setIdGiocatore2(102);
		partita3.setIdGiocatore3(103);
		partita3.setIdGiocatore4(104);
		partita3.setIdGiocatore5(105);
		partita3.setIdGiocatoreVincitore(103);
		partita3.setNominativoGiocatore1("Marco De Simone");
		partita3.setPunteggioGiocatore1(40.1);
		partita3.setNominativoGiocatore2("Mario Rossi");
		partita3.setPunteggioGiocatore2(40.0);
		partita3.setNominativoGiocatore3("Aristo Gitone");
		partita3.setPunteggioGiocatore3(11.0);
		partita3.setNominativoGiocatore4("Luisa Bianchi");
		partita3.setPunteggioGiocatore4(10.0);
		partita3.setNominativoGiocatore5("Filippo De Magistris");
		partita3.setPunteggioGiocatore5(8.0);
		
		partite.add(partita1);
		partite.add(partita2);
		partite.add(partita3);
		TorneoPubblicato torneo = new TorneoPubblicato(torneoRow);
		torneo.setPartite(partite);
		
		tornei.add(torneo);
		//HtmlPublisher.torneiPublisher(tornei);
	}
	
}
