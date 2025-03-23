package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.ArrayList;
import java.util.List;

public class TorneiRow extends AbstractSheetRow {
	
	public static final String SHEET_TORNEI_NAME 	= "TORNEI";
	
	static class ColPosition{
		//zero-based
		public static final Integer ID_TORNEO 		= 0;
		public static final Integer NOME_TORNEO 	= 1;
		public static final Integer ORGANIZZATORE 	= 2;
		public static final Integer SEDE 			= 3;
		public static final Integer START_DATE 		= 4;
		public static final Integer END_DATE 		= 5;
		public static final Integer TIPO_TORNEO 	= 6;
		public static final Integer NUMERO_TURNI 	= 7;
		public static final Integer NUMERO_PARTECIPANTI 	= 8;
		public static final Integer NUMERO_TAVOLI 	= 9;
		public static final Integer NOTE 			= 10;
		public static final Integer NOME_FILE 		= 11;
		public static final Integer UPDATE_TIME		= 12;
	}
	
	public Integer getDataSize() {
		return 14;
	}
	
	private String idTorneo;
	private String nomeTorneo;
	private String organizzatore;
	private String sede;
	private String startDate;
	private String endDate;
	private String tipoTorneo;
	private Integer numeroTurni;
	private Integer numeroPartecipanti;
	private Integer numeroTavoli;
	private String note;
	private String filename;
	private String updateTime;

	public String getIdTorneo() {
		return idTorneo;
	}

	public void setIdTorneo(String idTorneo) {
		this.idTorneo = idTorneo;
	}

	public String getNomeTorneo() {
		return nomeTorneo;
	}

	public void setNomeTorneo(String nomeTorneo) {
		this.nomeTorneo = nomeTorneo;
	}

	public String getOrganizzatore() {
		return organizzatore;
	}

	public void setOrganizzatore(String organizzatore) {
		this.organizzatore = organizzatore;
	}

	public String getSede() {
		return sede;
	}

	public void setSede(String sede) {
		this.sede = sede;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTipoTorneo() {
		return tipoTorneo;
	}

	public void setTipoTorneo(String tipoTorneo) {
		this.tipoTorneo = tipoTorneo;
	}

	public Integer getNumeroTurni() {
		return numeroTurni;
	}

	public void setNumeroTurni(Integer numeroTurni) {
		this.numeroTurni = numeroTurni;
	}

	public Integer getNumeroPartecipanti() {
		return numeroPartecipanti;
	}

	public void setNumeroPartecipanti(Integer numeroPartecipanti) {
		this.numeroPartecipanti = numeroPartecipanti;
	}

	public Integer getNumeroTavoli() {
		return numeroTavoli;
	}

	public void setNumeroTavoli(Integer numeroTavoli) {
		this.numeroTavoli = numeroTavoli;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public List<Object> getData() {
		super.getData();
		if (idTorneo != null) data.set(ColPosition.ID_TORNEO, idTorneo.trim());
		if (nomeTorneo != null) data.set(ColPosition.NOME_TORNEO, nomeTorneo.trim());
		if (sede != null) data.set(ColPosition.SEDE, sede.trim());
		if (tipoTorneo != null) data.set(ColPosition.TIPO_TORNEO, tipoTorneo.trim());
		if (startDate != null) data.set(ColPosition.START_DATE, startDate.trim());
		if (endDate != null) data.set(ColPosition.END_DATE, endDate.trim());
		if (organizzatore != null) data.set(ColPosition.ORGANIZZATORE, organizzatore.trim());
		if (numeroTurni != null) data.set(ColPosition.NUMERO_TURNI, numeroTurni);
		if (numeroPartecipanti != null) data.set(ColPosition.NUMERO_PARTECIPANTI, numeroPartecipanti);
		if (numeroTavoli != null) data.set(ColPosition.NUMERO_TAVOLI, numeroTavoli);
		if (note != null) data.set(ColPosition.NOTE, note.trim());
		if (filename != null) data.set(ColPosition.NOME_FILE, filename.trim());
		if (updateTime != null) data.set(ColPosition.UPDATE_TIME, updateTime);
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		idTorneo 		= (String) data.get(ColPosition.ID_TORNEO);
		nomeTorneo 		= (String) data.get(ColPosition.NOME_TORNEO);
		sede 			= (String) data.get(ColPosition.SEDE);
		tipoTorneo 		= (String) data.get(ColPosition.TIPO_TORNEO);
		startDate 		= (String) data.get(ColPosition.START_DATE);
		endDate 		= (String) data.get(ColPosition.END_DATE);
		organizzatore 	= (String) data.get(ColPosition.ORGANIZZATORE);
		numeroTurni 	= Integer.valueOf((String)data.get(ColPosition.NUMERO_TURNI));
		numeroPartecipanti 	= Integer.valueOf((String)data.get(ColPosition.NUMERO_PARTECIPANTI));
		numeroTavoli 	= Integer.valueOf((String)data.get(ColPosition.NUMERO_TAVOLI));
		note 			= (String) data.get(ColPosition.NOTE);
		filename		= (String) data.get(ColPosition.NOME_FILE);
		updateTime		= (String) data.get(ColPosition.UPDATE_TIME);
	}

	public List<Integer> keyCols(){
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.ORGANIZZATORE);
		keyCols.add(ColPosition.START_DATE);
		return keyCols;
	}

	public boolean equals(Object o){
		TorneiRow torneo = (TorneiRow) o;
		
		return getIdTorneo().equals(torneo.getIdTorneo());
	}
	
	public int hashCode(){
		return getIdTorneo().hashCode();
	}
}
