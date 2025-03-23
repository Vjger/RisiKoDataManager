package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.ArrayList;
import java.util.List;

public class TorneiSheetIdRow extends AbstractSheetRow {
	
	public static final String SHEET_TORNEI_NAME 	= "TORNEI";
	
	static class ColPosition{
		//zero-based
		public static final Integer ANNO_TORNEO 		= 0;
		public static final Integer SHEET_ID_TORNEO 	= 1;
	}
	
	public Integer getDataSize() {
		return 3;
	}
	
	private String annoTorneo;
	private String sheetIdTorneo;

	public String getAnnoTorneo() {
		return annoTorneo;
	}

	public void setAnnoTorneo(String annoTorneo) {
		this.annoTorneo = annoTorneo;
	}

	public String getSheetIdTorneo() {
		return sheetIdTorneo;
	}

	public void setSheetIdTorneo(String sheetIdTorneo) {
		this.sheetIdTorneo = sheetIdTorneo;
	}

	public List<Object> getData() {
		super.getData();
		if (annoTorneo != null) data.set(ColPosition.ANNO_TORNEO, annoTorneo.trim());
		if (sheetIdTorneo != null) data.set(ColPosition.SHEET_ID_TORNEO, sheetIdTorneo.trim());
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		annoTorneo 		= (String) data.get(ColPosition.ANNO_TORNEO);
		sheetIdTorneo 		= (String) data.get(ColPosition.SHEET_ID_TORNEO);
	}

	public List<Integer> keyCols(){
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.ANNO_TORNEO);
		return keyCols;
	}

	public boolean equals(Object o){
		TorneiSheetIdRow torneo = (TorneiSheetIdRow) o;
		
		return getAnnoTorneo().equals(torneo.getAnnoTorneo());
	}
	
	public int hashCode(){
		return getAnnoTorneo().hashCode();
	}
}
