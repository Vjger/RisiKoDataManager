package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationMapRow extends AbstractSheetRow {
	
	public static final String SHEET_CONFIGURAZIONI_NAME 	= "CONFIGURAZIONI";
	
	static class ColPosition{
		//zero-based
		public static final Integer CHIAVE	= 0;
		public static final Integer VALORE 	= 1;
	}
	
	public Integer getDataSize() {
		return 3;
	}
	
	private String chiave;
	private String valore;

	public String getChiave() {
		return chiave;
	}

	public void setChiave(String chiave) {
		this.chiave = chiave;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	public List<Object> getData() {
		super.getData();
		if (chiave != null) data.set(ColPosition.CHIAVE, chiave.trim());
		if (valore != null) data.set(ColPosition.VALORE, valore.trim());
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		chiave 		= (String) data.get(ColPosition.CHIAVE);
		valore 		= (String) data.get(ColPosition.VALORE);
	}

	public List<Integer> keyCols(){
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.CHIAVE);
		return keyCols;
	}

	public boolean equals(Object o){
		ConfigurationMapRow configurationMapRow = (ConfigurationMapRow) o;
		
		return getChiave().equals(configurationMapRow.getChiave());
	}
	
	public int hashCode(){
		return getChiave().hashCode();
	}
}
