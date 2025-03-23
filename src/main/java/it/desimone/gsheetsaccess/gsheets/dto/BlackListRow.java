package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.ArrayList;
import java.util.List;

public class BlackListRow extends AbstractSheetRow {
	
	public static final String SHEET_BLACKLIST_NAME 	= "BLACKLIST";
	
	static class ColPosition{
		//zero-based
		public static final Integer ID_ANAGRAFICA 				= 0;
		public static final Integer ANNI_ESCLUSI	 			= 1;
		public static final Integer DATA_INIZIO_ESCLUSIONE	 	= 2;
		public static final Integer DATA_FINE_ESCLUSIONE	 	= 3;
		public static final Integer NOMINATIVO	 				= 4;
		public static final Integer MOTIVAZIONE	 				= 5;
	}
	
	public Integer getDataSize() {
		return 7;
	}
	
	private String idAnagrafica;
	private String anniEsclusi;
	private String dataInizioEsclusione;
	private String dataFineEsclusione;
	private String motivazione;

	public String getIdAnagrafica() {
		return idAnagrafica;
	}

	public void setIdAnagrafica(String idAnagrafica) {
		this.idAnagrafica = idAnagrafica;
	}

	public String getAnniEsclusi() {
		return anniEsclusi;
	}

	public void setAnniEsclusi(String anniEsclusi) {
		this.anniEsclusi = anniEsclusi;
	}

	public String getDataInizioEsclusione() {
		return dataInizioEsclusione;
	}

	public void setDataInizioEsclusione(String dataInizioEsclusione) {
		this.dataInizioEsclusione = dataInizioEsclusione;
	}

	public String getDataFineEsclusione() {
		return dataFineEsclusione;
	}

	public void setDataFineEsclusione(String dataFineEsclusione) {
		this.dataFineEsclusione = dataFineEsclusione;
	}

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

	public List<Object> getData() {
		super.getData();
		if (idAnagrafica != null) data.set(ColPosition.ID_ANAGRAFICA, idAnagrafica.trim());
		if (anniEsclusi != null) data.set(ColPosition.ANNI_ESCLUSI, anniEsclusi.trim());
		if (dataInizioEsclusione != null) data.set(ColPosition.DATA_INIZIO_ESCLUSIONE, dataInizioEsclusione.trim());
		if (dataFineEsclusione != null) data.set(ColPosition.DATA_FINE_ESCLUSIONE, dataFineEsclusione.trim());
		if (motivazione != null) data.set(ColPosition.MOTIVAZIONE, motivazione.trim());
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		idAnagrafica 		= (String) data.get(ColPosition.ID_ANAGRAFICA);
		anniEsclusi 		= (String) data.get(ColPosition.ANNI_ESCLUSI);
		dataInizioEsclusione = (String) data.get(ColPosition.DATA_INIZIO_ESCLUSIONE);
		dataFineEsclusione 	= (String) data.get(ColPosition.DATA_FINE_ESCLUSIONE);
		motivazione 		= (String) data.get(ColPosition.MOTIVAZIONE);
	}

	public List<Integer> keyCols(){
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.ID_ANAGRAFICA);
		return keyCols;
	}

	public boolean equals(Object o){
		BlackListRow blackListRow = (BlackListRow) o;
		
		return getIdAnagrafica().equals(blackListRow.getIdAnagrafica());
	}
	
	public int hashCode(){
		return getIdAnagrafica().hashCode();
	}
}
