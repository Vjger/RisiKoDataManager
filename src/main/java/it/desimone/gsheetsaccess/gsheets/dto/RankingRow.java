package it.desimone.gsheetsaccess.gsheets.dto;

import it.desimone.gsheetsaccess.common.Configurator;

import java.util.ArrayList;
import java.util.List;

public class RankingRow extends AbstractSheetRow {
	
	public static final String SHEET_NAME 	= "RANKING";
	
	public static class ColPosition{
		//zero-based
		public static final Integer POSIZIONE_RANKING		= 0;
		public static final Integer ID_GIOCATORE 			= 1;
		public static final Integer NOMINATIVO_GIOCATORE 	= 2;
		public static final Integer PUNTI_RANKING 			= 3;
	}
	
	public static final String[] intestazione = new String[]{"POS.", "ID_GIOCATORE", "NOMINATIVO", "RANK"};
	
	public Integer getDataSize() {
		return 1000; //metto un numero alto perchè in realtà la dimensione sarà variabile
	}

	public class ContributoRanking{
		private String idTorneo;
		private Double puntiRanking;
		public String getIdTorneo() {
			return idTorneo;
		}
		public void setIdTorneo(String idTorneo) {
			this.idTorneo = idTorneo;
		}
		public Double getPuntiRanking() {
			return puntiRanking;
		}
		public void setPuntiRanking(Double puntiRanking) {
			this.puntiRanking = puntiRanking;
		}
	}
	
	private Integer posizioneRanking;
	private Integer idGiocatore;
	private String nominativoGiocatore;
	private Double puntiRanking;
	private List<ContributoRanking> contributiRanking;

	public Integer getPosizioneRanking() {
		return posizioneRanking;
	}
	public void setPosizioneRanking(Integer posizioneRanking) {
		this.posizioneRanking = posizioneRanking;
	}
	public Integer getIdGiocatore() {
		return idGiocatore;
	}
	public void setIdGiocatore(Integer idGiocatore) {
		//this.nominativoGiocatore = getGiocatoreCellById(idGiocatore);
		this.idGiocatore = idGiocatore;
	}
	public String getNominativoGiocatore() {
		return nominativoGiocatore;
	}
	public void setNominativoGiocatore(String nominativoGiocatore) {
		this.nominativoGiocatore = nominativoGiocatore;
	}
	public Double getPuntiRanking() {
		return puntiRanking;
	}
	public void setPuntiRanking(Double puntiRanking) {
		this.puntiRanking = puntiRanking;
	}
	public List<ContributoRanking> getContributiRanking() {
		return contributiRanking;
	}
	public void setContributiRanking(List<ContributoRanking> contributiRanking) {
		this.contributiRanking = contributiRanking;
	}
	
	private static String getGiocatoreCellById(Integer id){
		StringBuilder buffer = new StringBuilder();
		buffer.append("=CONCATENATE(");
		buffer.append("CERCA.VERT(");
		buffer.append(id);
		buffer.append(";");
		buffer.append("IMPORTRANGE(\"https://docs.google.com/spreadsheets/d/");
		buffer.append(Configurator.getTorneiSheetId("2019")+"\";");
		buffer.append("\""+AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME);
		buffer.append("!A:E\"); "+(AnagraficaGiocatoreRow.ColPosition.NOME+1)+"; FALSE);");
		buffer.append("\" \";");
		buffer.append("CERCA.VERT(");
		buffer.append(id);
		buffer.append(";");
		buffer.append("IMPORTRANGE(\"https://docs.google.com/spreadsheets/d/");
		buffer.append(Configurator.getTorneiSheetId("2019")+"\";");
		buffer.append("\""+AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME);
		buffer.append("!A:E\"); "+(AnagraficaGiocatoreRow.ColPosition.COGNOME+1)+"; FALSE);");
		buffer.append(")");
		return buffer.toString();
	}
	
	public List<Object> getData() {
		super.getData();
		if (posizioneRanking != null) data.set(ColPosition.POSIZIONE_RANKING, posizioneRanking);
		if (idGiocatore != null) data.set(ColPosition.ID_GIOCATORE, idGiocatore);
		if (nominativoGiocatore != null) data.set(ColPosition.NOMINATIVO_GIOCATORE, nominativoGiocatore);
		if (puntiRanking != null) data.set(ColPosition.PUNTI_RANKING, puntiRanking);

		if (contributiRanking != null && !contributiRanking.isEmpty()){
			Integer startIndex = ColPosition.PUNTI_RANKING;
			for (ContributoRanking contributoRanking: contributiRanking){
				data.set(++startIndex,contributoRanking.getIdTorneo());
				data.set(++startIndex,contributoRanking.getPuntiRanking());
			}
		}
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		posizioneRanking 		= Integer.valueOf((String) data.get(ColPosition.POSIZIONE_RANKING));
		idGiocatore 			= Integer.valueOf((String)data.get(ColPosition.ID_GIOCATORE));
		puntiRanking 			= (Double) data.get(ColPosition.PUNTI_RANKING);
		if (data.size() > (ColPosition.PUNTI_RANKING +1)){
			contributiRanking = new ArrayList<RankingRow.ContributoRanking>();
			int counter = 0;
			ContributoRanking contributoRanking = null;
			for (int index = ColPosition.PUNTI_RANKING +1; index < data.size() -1; index++){ //data.size() -1 perchè l'ultima occorrenza è sempre il numero di riga
				counter++;
				if (counter%2 != 0){ //Questo "2" deriva dal fatto che ogni oggetto ContributoRanking è composto da due attributi
					contributoRanking = new ContributoRanking();
					contributoRanking.setIdTorneo((String)data.get(index));
				}else{
					contributoRanking.setPuntiRanking((Double)data.get(index));
					contributiRanking.add(contributoRanking);
				}
			}
		}
	}

	public List<Integer> keyCols(){
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.ID_GIOCATORE);
		return keyCols;
	}

}
