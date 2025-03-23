package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.ArrayList;
import java.util.List;

public class LastUpdateRow extends AbstractSheetRow {
	
	public static final String SHEET_LAST_UPDATE_NAME 	= "LAST_UPDATE";
	
	static class ColPosition{
		//zero-based
		public static final Integer ANNO_RIFERIMENTO 		= 0;
		public static final Integer ULTIMA_ELABORAZIONE 	= 1;

	}
	
	public Integer getDataSize() {
		return 3;
	}
	
	private Integer annoRiferimento;
	private String lastElaboration;

	public Integer getAnnoRiferimento() {
		return annoRiferimento;
	}

	public void setAnnoRiferimento(Integer annoRiferimento) {
		this.annoRiferimento = annoRiferimento;
	}

	public String getLastElaboration() {
		return lastElaboration;
	}

	public void setLastElaboration(String lastElaboration) {
		this.lastElaboration = lastElaboration;
	}

	public List<Object> getData() {
		super.getData();
		if (annoRiferimento != null) data.set(ColPosition.ANNO_RIFERIMENTO, annoRiferimento);
		if (lastElaboration != null) data.set(ColPosition.ULTIMA_ELABORAZIONE, lastElaboration.trim());
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		annoRiferimento 	= Integer.valueOf((String)data.get(ColPosition.ANNO_RIFERIMENTO));
		lastElaboration 	= (String) data.get(ColPosition.ULTIMA_ELABORAZIONE);
	}

	public List<Integer> keyCols(){
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.ANNO_RIFERIMENTO);
		keyCols.add(ColPosition.ULTIMA_ELABORAZIONE);
		return keyCols;
	}

	public boolean equals(Object o){
		LastUpdateRow lastUpdateRow = (LastUpdateRow) o;
		
		return getAnnoRiferimento().equals(lastUpdateRow.getAnnoRiferimento());
	}
	
	public int hashCode(){
		return getAnnoRiferimento().hashCode();
	}
}
