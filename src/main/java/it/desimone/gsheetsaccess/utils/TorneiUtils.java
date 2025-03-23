package it.desimone.gsheetsaccess.utils;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.dto.ScorePlayer;
import it.desimone.gsheetsaccess.dto.ScorePlayer.TabellinoPlayer;
import it.desimone.gsheetsaccess.dto.TorneoPubblicato;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRidottaRow;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRow;
import it.desimone.gsheetsaccess.gsheets.dto.ClassificheRow;
import it.desimone.gsheetsaccess.gsheets.dto.LastUpdateRow;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRowFactory.SheetRowType;
import it.desimone.gsheetsaccess.gsheets.dto.TabellinoGiocatore;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiRow;
import it.desimone.gsheetsaccess.gsheets.facade.GSheetsInterface;
import it.desimone.gsheetsaccess.htmlpublisher.FalsePositiveData;
import it.desimone.gsheetsaccess.ranking.BlackListData;
import it.desimone.gsheetsaccess.ranking.RankingCalculator;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;
import it.desimone.utils.Capitalize;
import it.desimone.utils.MyLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.impl.common.Levenshtein;

public class TorneiUtils {

	private static final Integer LEVENSHTEIN_LIMIT = 4;
	
	static class FalsiPositivi{
		private Integer primo;
		private Integer secondo;
		public FalsiPositivi(Integer primo, Integer secondo) {
			super();
			this.primo = primo;
			this.secondo = secondo;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((primo == null) ? 0 : primo.hashCode());
			result = prime * result
					+ ((secondo == null) ? 0 : secondo.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FalsiPositivi other = (FalsiPositivi) obj;
			if (primo == null) {
				if (other.primo != null)
					return false;
			} else if (!primo.equals(other.primo))
				return false;
			if (secondo == null) {
				if (other.secondo != null)
					return false;
			} else if (!secondo.equals(other.secondo))
				return false;
			return true;
		}


	}
	
	private static List<FalsiPositivi> falsiPositivi = new ArrayList<TorneiUtils.FalsiPositivi>();
	static{
		falsiPositivi.add(new TorneiUtils.FalsiPositivi(233, 1211)); //Alessio Boni - Alessio Bini
		falsiPositivi.add(new TorneiUtils.FalsiPositivi(330, 648)); //Stefano Bracci - Stefano Bianchi
		falsiPositivi.add(new TorneiUtils.FalsiPositivi(719, 2010)); //Gianluca Maconi - Gianluca Marconi
		falsiPositivi.add(new TorneiUtils.FalsiPositivi(1413, 1674)); //Riccardo Vada - Riccardo Duca
		falsiPositivi.add(new TorneiUtils.FalsiPositivi(497, 2362)); //Antonio Condorelli - Antonio Condorelli
	}
	
	public static AnagraficaGiocatoreRow findAnagraficaById(List<AnagraficaGiocatoreRow> anagrafiche, Integer id){
		if (anagrafiche == null || id == null) return null;
		AnagraficaGiocatoreRow result = null;
		AnagraficaGiocatoreRow anagraficaSonda = new AnagraficaGiocatoreRow();
		anagraficaSonda.setId(id);
		int index = anagrafiche.indexOf(anagraficaSonda);
		if (index >=0){
			result = anagrafiche.get(index);
		}
		return result;
	}
	
	public static List<TorneoPubblicato> caricamentoTornei(String year){
		MyLogger.getLogger().info("START");
		
		List<TorneiRow> torneiRow = getAllTornei(year);
		MyLogger.getLogger().info("Caricati "+torneiRow.size()+" tornei");
		
		List<PartitaRow> partiteRow = getAllPartite(year);
		MyLogger.getLogger().info("Caricate "+partiteRow.size()+" partite");
		
		List<ClassificheRow> classificheRow = getAllClassifiche(year);
		MyLogger.getLogger().info("Caricate "+(classificheRow==null?0:classificheRow.size())+" righe classifica");
		
		List<TorneoPubblicato> result = null;
		if (torneiRow != null){
			result = new ArrayList<TorneoPubblicato>();
			for (TorneiRow torneoRow: torneiRow){
				TorneoPubblicato torneoPubblicato = new TorneoPubblicato(torneoRow);
				result.add(torneoPubblicato);
			}
			
			MyLogger.getLogger().info("Inizio associazione partite a tornei");
			if (partiteRow != null){
				for (PartitaRow partitaRow: partiteRow){
					TorneiRow torneoRicerca = new TorneiRow();
					torneoRicerca.setIdTorneo(partitaRow.getIdTorneo());
					int index = result.indexOf(new TorneoPubblicato(torneoRicerca));
					if (index >= 0){
						TorneoPubblicato torneoPubblicato = result.get(index);
						torneoPubblicato.add(partitaRow);
					}
				}
			}	
			MyLogger.getLogger().info("Inizio associazione classifiche a tornei");
			if (classificheRow != null){
				for (ClassificheRow classificaRow: classificheRow){
					TorneiRow torneoRicerca = new TorneiRow();
					torneoRicerca.setIdTorneo(classificaRow.getIdTorneo());
					int index = result.indexOf(new TorneoPubblicato(torneoRicerca));
					if (index >= 0){
						TorneoPubblicato torneoPubblicato = result.get(index);
						torneoPubblicato.add(classificaRow);
					}
				}
			}
		}

		MyLogger.getLogger().info("END");
		
		return result;
	}
	
	public static List<TorneiRow> getAllTornei(String year){
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		List<TorneiRow> result = null;
		try{
			result = GSheetsInterface.getAllRows(spreadSheetIdTornei, SheetRowType.Torneo);
		}catch(IOException ioe){
			MyLogger.getLogger().severe("Eccezione: "+ioe.getMessage());
		}
		return result;
	}
	
	public static List<PartitaRow> getAllPartite(String year){
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		List<PartitaRow> result = null;
		try{
			result = GSheetsInterface.getAllRows(spreadSheetIdTornei, SheetRowType.Partita);
		}catch(IOException ioe){
			MyLogger.getLogger().severe("Eccezione: "+ioe.getMessage());
		}
		return result;
	}
	
	public static List<ClassificheRow> getAllClassifiche(String year){
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		List<ClassificheRow> result = null;
		try{
			result = GSheetsInterface.getAllRows(spreadSheetIdTornei, SheetRowType.Classifica);
		}catch(IOException ioe){
			MyLogger.getLogger().severe("Eccezione: "+ioe.getMessage());
		}	
		return result;
	}
	
	public static List<AnagraficaGiocatoreRow> getAllAnagraficheGiocatori(String year){
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		List<AnagraficaGiocatoreRow> result = null;
		try{
			result = GSheetsInterface.getAllRows(spreadSheetIdTornei, SheetRowType.AnagraficaGiocatore);
		}catch(IOException ioe){
			MyLogger.getLogger().severe("Eccezione: "+ioe.getMessage());
		}	
		return result;
	}
	
	public static List<AnagraficaGiocatoreRidottaRow> getAllAnagraficheGiocatoriRidotte(){
		String spreadSheetIdAnagrafiche = Configurator.getAnagraficaRidottaSheetId();
		List<AnagraficaGiocatoreRidottaRow> result = null;
		try{
			result = GSheetsInterface.getAllRows(spreadSheetIdAnagrafiche, SheetRowType.AnagraficaGiocatoreRidotta);
		}catch(IOException ioe){
			MyLogger.getLogger().severe("Eccezione: "+ioe.getMessage());
		}	
		return result;
	}
	
	public static List<LastUpdateRow> getAllLastUpdateRow(){
		String spreadSheetLastUpdate = Configurator.getBlackListSheetId();
		List<LastUpdateRow> result = null;
		try{
			result = GSheetsInterface.getAllRows(spreadSheetLastUpdate, SheetRowType.LastUpdate);
		}catch(IOException ioe){
			MyLogger.getLogger().severe("Eccezione: "+ioe.getMessage());
		}	
		return result;
	}
	
	public static void insertOrUpdateLastUpdateRow(LastUpdateRow lastUpdateRow){
		String spreadSheetLastUpdate = Configurator.getBlackListSheetId();
		try{
			if (lastUpdateRow.getSheetRowNumber() == null) {
				GSheetsInterface.appendRows(spreadSheetLastUpdate, LastUpdateRow.SHEET_LAST_UPDATE_NAME, Collections.singletonList(lastUpdateRow));
			}else {
				Integer updated = GSheetsInterface.updateRows(spreadSheetLastUpdate, LastUpdateRow.SHEET_LAST_UPDATE_NAME, Collections.singletonList(lastUpdateRow), true);
			}
		}catch(IOException ioe){
			MyLogger.getLogger().severe("Eccezione: "+ioe.getMessage());
		}	
	}
	
	public static boolean haPartecipato(PartitaRow partitaRow, Integer idPlayer){
		boolean result = 
				( partitaRow.getIdGiocatore1()!= null && partitaRow.getIdGiocatore1().equals(idPlayer) )
			||  ( partitaRow.getIdGiocatore2()!= null && partitaRow.getIdGiocatore2().equals(idPlayer) )
			||  ( partitaRow.getIdGiocatore3()!= null && partitaRow.getIdGiocatore3().equals(idPlayer) )
			||  ( partitaRow.getIdGiocatore4()!= null && partitaRow.getIdGiocatore4().equals(idPlayer) )
			||  ( partitaRow.getIdGiocatore5()!= null && partitaRow.getIdGiocatore5().equals(idPlayer) );
		return result;
	}
	
	public static boolean isVincitore(PartitaRow partitaRow, Integer idPlayer){
		boolean result = 
				partitaRow.getIdGiocatoreVincitore()!= null && partitaRow.getIdGiocatoreVincitore().equals(idPlayer);
		return result;
	}
	
	
	public static TabellinoGiocatore[] getTabelliniPlayer(Integer idPlayerFrom, Integer idPlayerTo, String year){
		MyLogger.getLogger().info("Inizio estrazione tabellini giocatore con id ["+idPlayerFrom+"] e id ["+idPlayerTo+"] per l'anno "+year);
		TabellinoGiocatore tabellinoGiocatoreFrom = getTabellinoPlayer(idPlayerFrom, year);
		TabellinoGiocatore tabellinoGiocatoreTo = getTabellinoPlayer(idPlayerTo, year);
		
		return new TabellinoGiocatore[]{tabellinoGiocatoreFrom, tabellinoGiocatoreTo};
	}
	
	public static TabellinoGiocatore getTabellinoPlayer(Integer idPlayer, String year){
		MyLogger.getLogger().info("Inizio estrazione tabellino giocatore con id ["+idPlayer+"] per l'anno "+year);
		TabellinoGiocatore tabellinoGiocatore = null;
		
		try {
			String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
			String spreadSheetAnagraficaRidotta = Configurator.getAnagraficaRidottaSheetId();

			PartitaRow partitaRow = new PartitaRow();
			partitaRow.setIdGiocatoreVincitore(idPlayer);
			List<SheetRow> righePartiteGiocatore = GSheetsInterface.findPartiteRowsByIdGiocatore(spreadSheetIdTornei, partitaRow);
			MyLogger.getLogger().info("Estratte "+(righePartiteGiocatore==null?0:righePartiteGiocatore.size())+" righe Partita per il giocatore con ID ["+idPlayer+"]");

			Set<TorneiRow> torneiGiocati = new HashSet<TorneiRow>();
			if (righePartiteGiocatore != null && !righePartiteGiocatore.isEmpty()){
				String sheetNameTornei = TorneiRow.SHEET_TORNEI_NAME;
				for (SheetRow matchRow: righePartiteGiocatore){
					String idTorneo = ((PartitaRow) matchRow).getIdTorneo();
					
					TorneiRow torneoRow = new TorneiRow();
					torneoRow.setIdTorneo(idTorneo);

					if (!torneiGiocati.contains(torneoRow)){
						torneoRow = (TorneiRow) GSheetsInterface.findTorneoRowByIdTorneo(spreadSheetIdTornei, sheetNameTornei, torneoRow);
						if (torneoRow != null){
							torneiGiocati.add(torneoRow);
						}
					}
				}
			}
			
			AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRow = new AnagraficaGiocatoreRidottaRow();
			anagraficaGiocatoreRow.setId(idPlayer);
			List<AnagraficaGiocatoreRidottaRow> anagraficheRidotteRowFound = GSheetsInterface.findAnagraficheRidotteById2(spreadSheetAnagraficaRidotta, Collections.singletonList(anagraficaGiocatoreRow));
			
			tabellinoGiocatore = new TabellinoGiocatore((AnagraficaGiocatoreRidottaRow) ((anagraficheRidotteRowFound != null && !anagraficheRidotteRowFound.isEmpty())?anagraficheRidotteRowFound.get(0):null), righePartiteGiocatore, torneiGiocati);
			
		}catch(Exception e){
			MyLogger.getLogger().severe("Errore accedendo ai dati "+e.getMessage());
		}
		
		return tabellinoGiocatore;
	}
	
	public static void mergePlayer(Integer idPlayerFrom, Integer idPlayerTo, String year){
		MyLogger.getLogger().info("Inizio merge dati da giocatore con id ["+idPlayerFrom+"] a giocatore con id ["+idPlayerTo+"] per l'anno ["+year+"]");
		
		try {
			String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
			String spreadSheetAnagraficaRidotta = Configurator.getAnagraficaRidottaSheetId();
			ClassificheRow classificheRow = new ClassificheRow();
			classificheRow.setIdGiocatore(idPlayerFrom);
			List<SheetRow> righeClassificaGiocatore = GSheetsInterface.findClassificaRowsByIdGiocatore(spreadSheetIdTornei, classificheRow);
			
			MyLogger.getLogger().info("Estratte "+(righeClassificaGiocatore==null?0:righeClassificaGiocatore.size())+" righe Classifica per il giocatore con ID ["+idPlayerFrom+"]");
			if (righeClassificaGiocatore != null) MyLogger.getLogger().info(righeClassificaGiocatore.toString());
			
			PartitaRow partitaRow = new PartitaRow();
			partitaRow.setIdGiocatoreVincitore(idPlayerFrom);
			List<SheetRow> righePartiteGiocatore = GSheetsInterface.findPartiteRowsByIdGiocatore(spreadSheetIdTornei, partitaRow);
			
			MyLogger.getLogger().info("Estratte "+(righePartiteGiocatore==null?0:righePartiteGiocatore.size())+" righe Partita per il giocatore con ID ["+idPlayerFrom+"]");
			if (righePartiteGiocatore != null) MyLogger.getLogger().info(righePartiteGiocatore.toString());
			
			for (SheetRow rigaClassifica: righeClassificaGiocatore){
				ClassificheRow riga = (ClassificheRow) rigaClassifica;
				riga.setIdGiocatore(idPlayerTo);
			}
			
			for (SheetRow rigaPartite: righePartiteGiocatore){
				PartitaRow riga = (PartitaRow) rigaPartite;
				if (riga.getIdGiocatore1() != null && riga.getIdGiocatore1().equals(idPlayerFrom)){
					riga.setIdGiocatore1(idPlayerTo);
				}
				if (riga.getIdGiocatore2() != null && riga.getIdGiocatore2().equals(idPlayerFrom)){
					riga.setIdGiocatore2(idPlayerTo);
				}
				if (riga.getIdGiocatore3() != null && riga.getIdGiocatore3().equals(idPlayerFrom)){
					riga.setIdGiocatore3(idPlayerTo);
				}
				if (riga.getIdGiocatore4() != null && riga.getIdGiocatore4().equals(idPlayerFrom)){
					riga.setIdGiocatore4(idPlayerTo);
				}
				if (riga.getIdGiocatore5() != null && riga.getIdGiocatore5().equals(idPlayerFrom)){
					riga.setIdGiocatore5(idPlayerTo);
				}
				if (riga.getIdGiocatoreVincitore() != null && riga.getIdGiocatoreVincitore().equals(idPlayerFrom)){
					riga.setIdGiocatoreVincitore(idPlayerTo);
				}
			}
			
			GSheetsInterface.updateRows(spreadSheetIdTornei, ClassificheRow.SHEET_CLASSIFICHE, righeClassificaGiocatore, true);
			MyLogger.getLogger().info("Sostituito nelle righe classifica il giocatore con ID ["+idPlayerFrom+"] con quello con ID ["+idPlayerTo+"]");
			GSheetsInterface.updateRows(spreadSheetIdTornei, PartitaRow.SHEET_PARTITE_NAME, righePartiteGiocatore, true);
			MyLogger.getLogger().info("Sostituito nelle righe partita il giocatore con ID ["+idPlayerFrom+"] con quello con ID ["+idPlayerTo+"]");
			
			//Il giocatore va cancellato solo se nello stesso anno esiste anche il suo clone. Altrimenti va sostituito con il clone "buono"
			//INizio cancellazione/update giocatore
			SheetRow anagraficaGiocatoreRowFrom = new AnagraficaGiocatoreRow();
			((AnagraficaGiocatoreRow)anagraficaGiocatoreRowFrom).setId(idPlayerFrom);
//			List<SheetRow> anagraficheDaCancellareRowFound = GSheetsInterface.findAnagraficheByKey(spreadSheetIdTornei, Collections.singletonList(anagraficaGiocatoreRowFrom));
			List<SheetRow> anagraficheDaCancellareRowFound = GSheetsInterface.leggiAnagraficheByKey(spreadSheetIdTornei, Collections.singletonList(anagraficaGiocatoreRowFrom));
			
			if (anagraficheDaCancellareRowFound != null && !anagraficheDaCancellareRowFound.isEmpty() && anagraficheDaCancellareRowFound.get(0).getSheetRowNumber() != null){
				SheetRow anagraficaGiocatoreRowTo = new AnagraficaGiocatoreRow();
				((AnagraficaGiocatoreRow)anagraficaGiocatoreRowTo).setId(idPlayerTo);
				List<SheetRow> anagraficheDaVerificareRowFound = GSheetsInterface.findAnagraficheByKey(spreadSheetIdTornei, Collections.singletonList(anagraficaGiocatoreRowTo));
				if (anagraficheDaVerificareRowFound != null && !anagraficheDaVerificareRowFound.isEmpty() && anagraficheDaVerificareRowFound.get(0).getSheetRowNumber() != null){
					List<Integer> rowNumberGiocatoreFrom = new ArrayList<Integer>();
					AnagraficaGiocatoreRow anagraficaDaCancellare = (AnagraficaGiocatoreRow) anagraficheDaCancellareRowFound.get(0);
					rowNumberGiocatoreFrom.add(anagraficaDaCancellare.getSheetRowNumber());
					GSheetsInterface.deleteRowsByNumRow(spreadSheetIdTornei, AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME, rowNumberGiocatoreFrom);
					MyLogger.getLogger().info("Cancellato il giocatore con ID ["+idPlayerFrom+"] dal foglio Tornei "+year);
				}else{
					for (SheetRow anagraficaRow: anagraficheDaCancellareRowFound){
						AnagraficaGiocatoreRow anagraficaDaAggiornare = (AnagraficaGiocatoreRow) anagraficaRow;
						anagraficaDaAggiornare.setId(idPlayerTo);
						//Devo trovare la ridotta per recuperare nome e cognome
						AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidottaRow = new AnagraficaGiocatoreRidottaRow();
						anagraficaGiocatoreRidottaRow.setId(idPlayerTo);
						List<AnagraficaGiocatoreRidottaRow> anagraficheRidotteDaCopiareRowFound = GSheetsInterface.findAnagraficheRidotteById2(spreadSheetAnagraficaRidotta, Collections.singletonList(anagraficaGiocatoreRidottaRow));
						
						if (anagraficheRidotteDaCopiareRowFound != null && !anagraficheRidotteDaCopiareRowFound.isEmpty()){
							anagraficaDaAggiornare.setNome(anagraficheRidotteDaCopiareRowFound.get(0).getNome());
							anagraficaDaAggiornare.setCognome(anagraficheRidotteDaCopiareRowFound.get(0).getCognome());
						}
						
						GSheetsInterface.updateRows(spreadSheetIdTornei, AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME, anagraficheDaCancellareRowFound, true);
					}
					
					MyLogger.getLogger().info("Aggiornato il giocatore con ID ["+idPlayerFrom+"] verso l'ID ["+idPlayerTo+"] nel foglio Tornei "+year);
				}
			}
			
//			SheetRow anagraficaRidottaGiocatoreRowFrom = new AnagraficaGiocatoreRidottaRow();
//			((AnagraficaGiocatoreRidottaRow)anagraficaRidottaGiocatoreRowFrom).setId(idPlayerFrom);
//			List<SheetRow> anagraficheRidotteDaCancellareRowFound = GSheetsInterface.findAnagraficheByKey(spreadSheetAnagraficaRidotta, Collections.singletonList(anagraficaGiocatoreRowFrom));
//			
//			if (anagraficheRidotteDaCancellareRowFound != null && !anagraficheRidotteDaCancellareRowFound.isEmpty()){
//				List<Integer> rowNumberGiocatoreFrom = new ArrayList<Integer>();
//				rowNumberGiocatoreFrom.add(anagraficheRidotteDaCancellareRowFound.get(0).getSheetRowNumber());
//				GSheetsInterface.deleteRowsByNumRow(spreadSheetAnagraficaRidotta, AnagraficaGiocatoreRidottaRow.SHEET_ANAGRAFICA_NAME, rowNumberGiocatoreFrom);
//				MyLogger.getLogger().info("Cancellato il giocatore con ID ["+idPlayerFrom+"] dal foglio Anagrafica Ridotta");
//			}
			
		}catch(Exception e){
			MyLogger.getLogger().severe("Errore accedendo ai dati "+e.getMessage());
		}
	}
	
	public static void deletePlayer(Integer idPlayerFrom){
		try{
			String spreadSheetAnagraficaRidotta = Configurator.getAnagraficaRidottaSheetId();
			AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidottaRow = new AnagraficaGiocatoreRidottaRow();
			anagraficaGiocatoreRidottaRow.setId(idPlayerFrom);
			List<AnagraficaGiocatoreRidottaRow> anagraficheRidotteDaCancellareRowFound = GSheetsInterface.findAnagraficheRidotteById2(spreadSheetAnagraficaRidotta, Collections.singletonList(anagraficaGiocatoreRidottaRow));
			
			if (anagraficheRidotteDaCancellareRowFound != null && !anagraficheRidotteDaCancellareRowFound.isEmpty()){
				List<Integer> rowNumberGiocatoreFrom = new ArrayList<Integer>();
				rowNumberGiocatoreFrom.add(anagraficheRidotteDaCancellareRowFound.get(0).getSheetRowNumber());
				GSheetsInterface.deleteRowsByNumRow(spreadSheetAnagraficaRidotta, AnagraficaGiocatoreRidottaRow.SHEET_ANAGRAFICA_NAME, rowNumberGiocatoreFrom);
				MyLogger.getLogger().info("Cancellato il giocatore con ID ["+idPlayerFrom+"] dal foglio Anagrafica Ridotta");
			}
		}catch(Exception e){
			MyLogger.getLogger().severe("Errore accedendo ai dati "+e.getMessage());
		}
	}
		
	public static void deleteTorneo(String idTorneo, String year){
		MyLogger.getLogger().info("INIZIO Cancellazione del torneo ["+idTorneo+"]");
		
		try {
			deletePartiteTorneoRows(idTorneo, year);
			deleteClassificaTorneoRows(idTorneo, year);
			deleteTorneoRow(idTorneo, year);
		} catch (IOException e) {
			MyLogger.getLogger().severe("Errore accedendo ai dati del torneo ["+idTorneo+"] - "+e.getMessage());
		}
		
		MyLogger.getLogger().info("FINE Cancellazione del torneo ["+idTorneo+"]");
	}
	
	private static void deletePartiteTorneoRows(String idTorneo, String year) throws IOException{
		MyLogger.getLogger().info("Inizio cancellazione righe partita del torneo ["+idTorneo+"]");
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		String sheetNamePartite = PartitaRow.SHEET_PARTITE_NAME;
		
		//Basta un oggetto: tanto l'id del torneo è sempre lo stesso.
		PartitaRow partitaRowDiRicerca = new PartitaRow();
		partitaRowDiRicerca.setIdTorneo(idTorneo);
		List<Integer> partiteRowFound = GSheetsInterface.findNumPartiteRowsByIdTorneo(spreadSheetIdTornei, partitaRowDiRicerca);

		if (partiteRowFound != null && !partiteRowFound.isEmpty()){
			MyLogger.getLogger().info("Cancellazione di "+partiteRowFound.size()+" partite del torneo "+idTorneo);
			GSheetsInterface.deleteRowsByNumRow(spreadSheetIdTornei, sheetNamePartite, partiteRowFound);
		}
	}
	
	private static void deleteClassificaTorneoRows(String idTorneo, String year) throws IOException{
		MyLogger.getLogger().info("Inizio cancellazione righe classifica del torneo ["+idTorneo+"]");
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		String sheetNameClassifiche = ClassificheRow.SHEET_CLASSIFICHE;
		
		ClassificheRow classificheRowDiRicerca = new ClassificheRow();
		classificheRowDiRicerca.setIdTorneo(idTorneo);
		List<Integer> classificheRowFound = GSheetsInterface.findClassificaRowsByIdTorneo(spreadSheetIdTornei, classificheRowDiRicerca);

		if (classificheRowFound != null && !classificheRowFound.isEmpty()){
			MyLogger.getLogger().info("Cancellazione di "+classificheRowFound.size()+" giocatori in classifica del torneo "+idTorneo);
			GSheetsInterface.deleteRowsByNumRow(spreadSheetIdTornei, sheetNameClassifiche, classificheRowFound);
		}
	}
	
	private static void deleteTorneoRow(String idTorneo, String year) throws IOException{
		MyLogger.getLogger().info("Inizio cancellazione riga del Torneo ["+idTorneo+"]");
		TorneiRow torneoRow = new TorneiRow();
		torneoRow.setIdTorneo(idTorneo);
		
		String spreadSheetIdTornei = Configurator.getTorneiSheetId(year);
		String sheetNameTornei = TorneiRow.SHEET_TORNEI_NAME;
		Integer torneoRowFound = GSheetsInterface.findNumTorneoRowByIdTorneo(spreadSheetIdTornei, sheetNameTornei, torneoRow);
		
		if (torneoRowFound != null){
			List<Integer> partitaList = new ArrayList<Integer>();
			partitaList.add(torneoRowFound);
			GSheetsInterface.deleteRowsByNumRow(spreadSheetIdTornei, sheetNameTornei, partitaList);
		}else{
			MyLogger.getLogger().severe("Non trovato il torneo ["+idTorneo+"]");
		}
	}
			
	public static List<AnagraficaGiocatoreRidottaRow> findOrphansSlow(){
		List<AnagraficaGiocatoreRidottaRow> result = new ArrayList<AnagraficaGiocatoreRidottaRow>();
		
		List<AnagraficaGiocatoreRidottaRow> allPlayers = getAllAnagraficheGiocatoriRidotte();
		
		MyLogger.getLogger().info("In esame "+allPlayers.size()+" giocatori");
		
		int counter = 0;
		for (AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidottaRow: allPlayers){
			counter++;
			boolean isOrphan = true;
			for (String spreadSheetIdTornei: Configurator.getTorneiSheetIds()){
				Integer idGiocatore = anagraficaGiocatoreRidottaRow.getId();
				ClassificheRow classificheRow = new ClassificheRow();
				classificheRow.setIdGiocatore(idGiocatore);
				PartitaRow partitaRow = new PartitaRow();
				partitaRow.setIdGiocatoreVincitore(idGiocatore);
				try{
					List<SheetRow> righeClassificaGiocatore = GSheetsInterface.findClassificaRowsByIdGiocatore(spreadSheetIdTornei, classificheRow);
					List<SheetRow> righePartiteGiocatore = GSheetsInterface.findPartiteRowsByIdGiocatore(spreadSheetIdTornei, partitaRow);
					
					if ( (righeClassificaGiocatore != null && !righeClassificaGiocatore.isEmpty())
					  || (righePartiteGiocatore != null    && !righePartiteGiocatore.isEmpty())
					  ){
						isOrphan = false;
						break;
					}
				}catch(Exception e){
					MyLogger.getLogger().severe(e.getMessage());
				}
			}
			if (isOrphan){
				result.add(anagraficaGiocatoreRidottaRow);
			}
			if (counter%10 == 0){
				MyLogger.getLogger().info("In esame il "+counter+"° giocatore; per ora "+result.size()+" orfani");
			}
		}
		
		return result;
	}
	
	
	public static List<AnagraficaGiocatoreRidottaRow> findOrphansInMemory(List<AnagraficaGiocatoreRidottaRow> allPlayers){
		List<AnagraficaGiocatoreRidottaRow> result = new ArrayList<AnagraficaGiocatoreRidottaRow>();
			
		List<PartitaRow> partiteRow = new ArrayList<PartitaRow>();
		
		List<ClassificheRow> classificheRow = new ArrayList<ClassificheRow>();
		
		for (Integer year: Configurator.getTorneiYears()){
			partiteRow.addAll(getAllPartite(year.toString()));
			List<ClassificheRow> classificheYear = getAllClassifiche(year.toString());
			if (classificheYear != null){//A inizio anno potrebbero essere vuote
				classificheRow.addAll(classificheYear);
			}
		}
		MyLogger.getLogger().info("In esame "+allPlayers.size()+" giocatori");
		MyLogger.getLogger().info("Caricate "+partiteRow.size()+" partite");
		MyLogger.getLogger().info("Caricate "+classificheRow.size()+" righe classifica");
		
		int counter = 0;
		for (AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidottaRow: allPlayers){
			Integer idGiocatore = anagraficaGiocatoreRidottaRow.getId();
			counter++;
			boolean isOrphan = true;
			Iterator<PartitaRow> itPartitaRow = partiteRow.iterator();
			while(itPartitaRow.hasNext() && isOrphan){
				PartitaRow partitaRow = itPartitaRow.next();
				
				isOrphan = (partitaRow.getIdGiocatore1() == null || !partitaRow.getIdGiocatore1().equals(idGiocatore))
						&& (partitaRow.getIdGiocatore2() == null || !partitaRow.getIdGiocatore2().equals(idGiocatore))
						&& (partitaRow.getIdGiocatore3() == null || !partitaRow.getIdGiocatore3().equals(idGiocatore))
						&& (partitaRow.getIdGiocatore4() == null || !partitaRow.getIdGiocatore4().equals(idGiocatore))
						&& (partitaRow.getIdGiocatore5() == null || !partitaRow.getIdGiocatore5().equals(idGiocatore));
			}
			if (isOrphan){
				Iterator<ClassificheRow> itClassificaRow = classificheRow.iterator();
				while(itClassificaRow.hasNext() && isOrphan){
					ClassificheRow classificaRow = itClassificaRow.next();
					
					isOrphan = classificaRow.getIdGiocatore() == null || !classificaRow.getIdGiocatore().equals(idGiocatore);
				}	
			}
			
			if (isOrphan){
				result.add(anagraficaGiocatoreRidottaRow);
			}
//			if (counter%100 == 0){
//				MyLogger.getLogger().info("In esame il "+counter+"° giocatore; per ora "+result.size()+" orfani");
//			}
		}
		MyLogger.getLogger().info("Trovati "+result.size()+" orfani");
		return result;
	}
	
	public static Set<AnagraficaGiocatoreRidottaRow> findClone(){
	
		List<AnagraficaGiocatoreRidottaRow> allPlayers = getAllAnagraficheGiocatoriRidotte();
		
		List<AnagraficaGiocatoreRidottaRow> orfani = findOrphansInMemory(allPlayers);
		
		allPlayers.removeAll(orfani);
		
		Object[][] cloni = new Object[500][3];
		
		int indexCloni = 0;
		for (int i = 0; i < allPlayers.size(); i++){
			for (int j = i+1; j < allPlayers.size(); j++){
					AnagraficaGiocatoreRidottaRow anagI = allPlayers.get(i);
					AnagraficaGiocatoreRidottaRow anagJ = allPlayers.get(j);
					String stringI = (anagI.getNome()+anagI.getCognome()).toLowerCase()+anagI.getDataDiNascita();
					String stringJ = (anagJ.getNome()+anagJ.getCognome()).toLowerCase()+anagJ.getDataDiNascita();
					int indexL = Levenshtein.distance(stringI, stringJ);
					//int indexLV = it.desimone.gsheetsaccess.utils.Levenshtein.calculateFast(stringI, stringJ);
					if (indexL <= LEVENSHTEIN_LIMIT && isNotFalsePositive(anagI, anagJ)){
						cloni[indexCloni][0] = anagI;
						cloni[indexCloni][1] = anagJ;
						cloni[indexCloni][2] = indexL;
						
						indexCloni++;
					}
			}
		}
		MyLogger.getLogger().info("Trovati "+indexCloni+" cloni");
		Set<AnagraficaGiocatoreRidottaRow> cloniSingoli = new HashSet<AnagraficaGiocatoreRidottaRow>();
		for (int i=0; i < indexCloni; i++){
			Object[] clone = cloni[i];
			cloniSingoli.add((AnagraficaGiocatoreRidottaRow)clone[0]);
			cloniSingoli.add((AnagraficaGiocatoreRidottaRow)clone[1]);
			MyLogger.getLogger().finest(clone[0]+" - "+clone[1]+" - "+clone[2]);
		}
		
		MyLogger.getLogger().info("Trovati "+cloniSingoli.size()+" cloni singoli");
		
		return cloniSingoli;
	}
	
	private static boolean isNotFalsePositive(AnagraficaGiocatoreRidottaRow anagI, AnagraficaGiocatoreRidottaRow anagJ) {
//		FalsiPositivi check = new FalsiPositivi(anagI.getId(), anagJ.getId());
//		return !falsiPositivi.contains(check);
		FalsePositiveData falsePositiveData = FalsePositiveData.getInstance();
		return !falsePositiveData.areFalsePositive(anagI.getId(), anagJ.getId());
	}

	public static void printScorePlayers(/*String year*/){
//		List<TorneoPubblicato> torneiPubblicati = TorneiUtils.caricamentoTornei(year);
//		List<ScorePlayer> tabellini = RankingCalculator.elaboraTabellini(year, torneiPubblicati, null);
//	
		List<ScorePlayer> allTabellini = getOverallTabellini();
		allTabellini.sort(new Comparator<ScorePlayer>() {

			@Override
			public int compare(ScorePlayer o1, ScorePlayer o2) {
				String nominativo1 = o1.getAnagraficaGiocatore().getCognome().toLowerCase()+o1.getAnagraficaGiocatore().getNome().toLowerCase();
				String nominativo2 = o2.getAnagraficaGiocatore().getCognome().toLowerCase()+o2.getAnagraficaGiocatore().getNome().toLowerCase();
				return nominativo1.compareTo(nominativo2);
			}
			
		});
		
		PrintWriter out = null;
		
		try{
			out = new PrintWriter("C:\\Users\\marco.desimone\\Documents\\PRIVATE\\ScorePlayer.csv","ISO-8859-1");
			for (ScorePlayer scorePlayer: allTabellini){
				String line = buildLine(scorePlayer);
				out.write(line);
			}
		}catch(IOException ioe){
			MyLogger.getLogger().severe(ioe.getMessage());
		}finally{
			out.close();
		}

	}
	
	public static List<ScorePlayer> getOverallTabellini(){
		List<ScorePlayer> allTabellini = new ArrayList<ScorePlayer>();
		List<Integer> years = Configurator.getTorneiYears();
		
		for (Integer year: years){
			List<TorneoPubblicato> torneiPubblicati = TorneiUtils.caricamentoTornei(year.toString());
			List<ScorePlayer> tabelliniAnnuali = RankingCalculator.elaboraTabellini(year.toString(), torneiPubblicati, null, BlackListData.getInstance());
			for (ScorePlayer scorePlayer: tabelliniAnnuali){
				if (allTabellini.contains(scorePlayer)){
					Integer index = allTabellini.indexOf(scorePlayer);
					ScorePlayer scorePlayerFound = allTabellini.get(index);
					scorePlayerFound.addTabelliniPlayer(scorePlayer.getTabelliniPlayer());
				}else{
					allTabellini.add(scorePlayer);
				}
			}
		}
		return allTabellini;
	}
	
	private static String buildLine(ScorePlayer scorePlayer){
		Set<TabellinoPlayer> tab1 = scorePlayer.getTabelliniPlayer();
		List<TabellinoPlayer> lTab1 = new ArrayList<ScorePlayer.TabellinoPlayer>(tab1);
		StringBuilder buffer = new StringBuilder();
		buffer.append(Capitalize.capitalizeSingleString(scorePlayer.getAnagraficaGiocatore().getNome().trim()));
		buffer.append(" ");
		buffer.append(Capitalize.capitalizeSingleString(scorePlayer.getAnagraficaGiocatore().getCognome().trim()));
		buffer.append(";");
		buffer.append(scorePlayer.getAnagraficaGiocatore().getId());
		buffer.append(";");
		if (lTab1.size() >=1){
			TabellinoPlayer tabellino = lTab1.get(lTab1.size() -1);
			TorneiRow torneoRow = tabellino.getTorneo().getTorneoRow();
			buffer.append(torneoRow.getOrganizzatore());
			buffer.append(" - ");
			buffer.append(torneoRow.getNomeTorneo());
//			buffer.append(" - ");
//			buffer.append(TipoTorneo.parseTipoTorneo(torneoRow.getTipoTorneo()).getAcronimo());
		}
		buffer.append(";");
		if (lTab1.size() >=2){
			TabellinoPlayer tabellino = lTab1.get(lTab1.size() -2);
			TorneiRow torneoRow = tabellino.getTorneo().getTorneoRow();
			buffer.append(torneoRow.getOrganizzatore());
			buffer.append(" - ");
			buffer.append(torneoRow.getNomeTorneo());
//			buffer.append(" - ");
//			buffer.append(TipoTorneo.parseTipoTorneo(torneoRow.getTipoTorneo()).getAcronimo());
		}
		buffer.append(";");
		if (lTab1.size() >=3){
			TabellinoPlayer tabellino = lTab1.get(lTab1.size() -3);
			TorneiRow torneoRow = tabellino.getTorneo().getTorneoRow();
			buffer.append(torneoRow.getOrganizzatore());
			buffer.append(" - ");
			buffer.append(torneoRow.getNomeTorneo());
//			buffer.append(" - ");
//			buffer.append(TipoTorneo.parseTipoTorneo(torneoRow.getTipoTorneo()).getAcronimo());
		}
		buffer.append("\n");
		return buffer.toString();
	}
}
