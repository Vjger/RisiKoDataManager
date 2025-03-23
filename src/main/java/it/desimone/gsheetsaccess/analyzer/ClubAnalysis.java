package it.desimone.gsheetsaccess.analyzer;

import it.desimone.gsheetsaccess.dto.ScorePlayer;
import it.desimone.gsheetsaccess.dto.TorneoPubblicato;
import it.desimone.gsheetsaccess.dto.ScorePlayer.TabellinoPlayer;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRow;
import it.desimone.gsheetsaccess.gsheets.dto.ClassificheRow;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClubAnalysis {

	private Map<String, List<ClubPlayerData>> clubData = new HashMap<String, List<ClubPlayerData>>();
	
	public class ClubPlayerData{
		private AnagraficaGiocatoreRow anagraficaGiocatoreRow;
		private Set<TorneoPubblicato> torneiDisputati = new HashSet<TorneoPubblicato>();
		private Set<TorneiDisputati> torneiDisputati2 = new HashSet<TorneiDisputati>();
		
		public ClubPlayerData(AnagraficaGiocatoreRow anagraficaGiocatoreRow) {
			super();
			this.anagraficaGiocatoreRow = anagraficaGiocatoreRow;
		}
		public AnagraficaGiocatoreRow getAnagraficaGiocatoreRow() {
			return anagraficaGiocatoreRow;
		}
		public void setAnagraficaGiocatoreRow(
				AnagraficaGiocatoreRow anagraficaGiocatoreRow) {
			this.anagraficaGiocatoreRow = anagraficaGiocatoreRow;
		}
		public Set<TorneoPubblicato> getTorneiDisputati() {
			return torneiDisputati;
		}
		public Set<TorneiDisputati> getTorneiDisputati2() {
			return torneiDisputati2;
		}
		public void setTorneiDisputati(Set<TorneoPubblicato> torneiDisputati) {
			this.torneiDisputati = torneiDisputati;
		}
		public void addTorneoDisputato(TorneoPubblicato torneoDisputato) {
			this.torneiDisputati.add(torneoDisputato);
		}
		public void addTorneoDisputato(TorneoPubblicato torneoDisputato, BigDecimal rankingScore) {
			this.torneiDisputati.add(torneoDisputato);
			TorneiDisputati torneiDisputati = new TorneiDisputati(torneoDisputato, rankingScore);
			this.torneiDisputati2.add(torneiDisputati);
		}
		public Integer getPosizione(TorneoPubblicato torneoPubblicato){
			Integer posizione = null;
			if (torneoPubblicato.isConcluso()){
				if (torneoPubblicato.getClassifica() != null && !torneoPubblicato.getClassifica().isEmpty()){
					for (ClassificheRow classificheRow: torneoPubblicato.getClassifica()){
						if (classificheRow.getIdGiocatore().equals(anagraficaGiocatoreRow.getId())){
							posizione = classificheRow.getPosizione();
							break;
						}
					}
				}
			}
			return posizione;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime
					* result
					+ ((anagraficaGiocatoreRow == null) ? 0
							: anagraficaGiocatoreRow.hashCode());
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
			ClubPlayerData other = (ClubPlayerData) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (anagraficaGiocatoreRow == null) {
				if (other.anagraficaGiocatoreRow != null)
					return false;
			} else if (!anagraficaGiocatoreRow
					.equals(other.anagraficaGiocatoreRow))
				return false;
			return true;
		}
		@Override
		public String toString() {
			
			StringBuilder buffer = new StringBuilder();
			buffer.append(anagraficaGiocatoreRow);
			buffer.append("\t");
			buffer.append(torneiDisputati.size());
			
			return buffer.toString();
		}
		private ClubAnalysis getOuterType() {
			return ClubAnalysis.this;
		}
		
		public BigDecimal getOverallRanking() {
			BigDecimal ranking = BigDecimal.ZERO;
			for (TorneiDisputati torneoDisputato: torneiDisputati2) {
				if (TipoTorneo.parseTipoTorneo(torneoDisputato.getTorneoPubblicato().getTorneoRow().getTipoTorneo()) == TipoTorneo.CAMPIONATO) {
					ranking = ranking.add(torneoDisputato.getRankingScore());
				}
			}	
			return ranking;
		}
	}
	
	public void populateData(TorneoPubblicato torneoPubblicato, List<AnagraficaGiocatoreRow> anagraficheGiocatoriRow){
		String organizzatore = torneoPubblicato.getTorneoRow().getOrganizzatore();
		List<ClubPlayerData> listClubPlayerData = clubData.get(organizzatore);
		if (listClubPlayerData == null){
			listClubPlayerData = new ArrayList<ClubPlayerData>();
		}
		Set<Integer> idPartecipanti = torneoPubblicato.getIdPartecipanti();
		for (Integer idPartecipante: idPartecipanti){
			if (idPartecipante <=0){ //Si tolgono anonimi e ghost
				continue;
			}
			AnagraficaGiocatoreRow anagraficaSonda = new AnagraficaGiocatoreRow(idPartecipante);
			ClubPlayerData clubPlayerDataSonda = new ClubPlayerData(anagraficaSonda);
			Integer indexOfAnagrafica = listClubPlayerData.indexOf(clubPlayerDataSonda);
			ClubPlayerData clubPlayerData = null;
			if (indexOfAnagrafica >=0){
				clubPlayerData = listClubPlayerData.get(indexOfAnagrafica);
			}else{
				int indexAnagrafica = anagraficheGiocatoriRow.indexOf(anagraficaSonda);
				clubPlayerData = new ClubPlayerData(anagraficheGiocatoriRow.get(indexAnagrafica));
				listClubPlayerData.add(clubPlayerData);
			}
			
			clubPlayerData.addTorneoDisputato(torneoPubblicato);
		}
		clubData.put(organizzatore, listClubPlayerData);
	}
	
	public void populateData(TorneoPubblicato torneoPubblicato, List<AnagraficaGiocatoreRow> anagraficheGiocatoriRow, List<ScorePlayer> tabellini){
		String organizzatore = torneoPubblicato.getTorneoRow().getOrganizzatore();
		List<ClubPlayerData> listClubPlayerData = clubData.get(organizzatore);
		if (listClubPlayerData == null){
			listClubPlayerData = new ArrayList<ClubPlayerData>();
		}
		Set<Integer> idPartecipanti = torneoPubblicato.getIdPartecipanti();
		for (Integer idPartecipante: idPartecipanti){
			if (idPartecipante <=0){ //Si tolgono anonimi e ghost
				continue;
			}
			AnagraficaGiocatoreRow anagraficaSonda = new AnagraficaGiocatoreRow(idPartecipante);
			ClubPlayerData clubPlayerDataSonda = new ClubPlayerData(anagraficaSonda);
			Integer indexOfAnagrafica = listClubPlayerData.indexOf(clubPlayerDataSonda);
			ClubPlayerData clubPlayerData = null;
			if (indexOfAnagrafica >=0){
				clubPlayerData = listClubPlayerData.get(indexOfAnagrafica);
			}else{
				int indexAnagrafica = anagraficheGiocatoriRow.indexOf(anagraficaSonda);
				clubPlayerData = new ClubPlayerData(anagraficheGiocatoriRow.get(indexAnagrafica));
				listClubPlayerData.add(clubPlayerData);
			}
			
			clubPlayerData.addTorneoDisputato(torneoPubblicato);
			
			ScorePlayer scorePlayerSonda = new ScorePlayer(anagraficaSonda);
			Integer indexOfScorePlayer = tabellini.indexOf(scorePlayerSonda);
			if (indexOfScorePlayer >=0){
				scorePlayerSonda = tabellini.get(indexOfScorePlayer);
				Set<TabellinoPlayer> tabelliniPlayer = scorePlayerSonda.getTabelliniPlayer();
				TabellinoPlayer tabellinoTorneoPubblicato = null;
				for (TabellinoPlayer tabellino: tabelliniPlayer) {
					if (tabellino.getTorneo().equals(torneoPubblicato)) {
						tabellinoTorneoPubblicato = tabellino;
						break;
					}
				}
				if (tabellinoTorneoPubblicato != null) {
					clubPlayerData.addTorneoDisputato(torneoPubblicato, tabellinoTorneoPubblicato.getScoreRanking());
				}
			}
		}
		clubData.put(organizzatore, listClubPlayerData);
	}

	
	private static Comparator<ClubPlayerData> getComparatorClubPlayerData(){
		Comparator<ClubPlayerData> comp = new Comparator<ClubAnalysis.ClubPlayerData>() {

			@Override
			public int compare(ClubPlayerData o1, ClubPlayerData o2) {
				return o2.getTorneiDisputati().size() - o1.getTorneiDisputati().size();
			}
		};
		return comp;
	}
	
	private static Comparator<ClubPlayerData> getComparatorClubPlayerDataByRanking(){
		Comparator<ClubPlayerData> comp = new Comparator<ClubAnalysis.ClubPlayerData>() {

			@Override
			public int compare(ClubPlayerData o1, ClubPlayerData o2) {
				return o2.getOverallRanking().compareTo(o1.getOverallRanking());
			}
		};
		return comp;
	}
	
	public List<ClubPlayerData> getPlayerDataByClub(String organizzatore){
		List<ClubPlayerData> clubPlayerData = clubData.get(organizzatore);
		clubPlayerData.sort(getComparatorClubPlayerDataByRanking());
		
		return clubPlayerData;
	}

	public void selectByClub(Set<String> clubsToSave){
		Set<String> clubs = new HashSet<String>(clubData.keySet());
		clubs.removeAll(clubsToSave);
		for (String club: clubs){
			clubData.remove(club);
		}
	}
	
	public Set<String> getClubs(){
		return clubData.keySet();
	}
	
}
