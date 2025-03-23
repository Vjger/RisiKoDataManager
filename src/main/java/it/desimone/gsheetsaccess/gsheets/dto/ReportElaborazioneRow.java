package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.ArrayList;
import java.util.List;

public class ReportElaborazioneRow extends AbstractSheetRow {
	
	public static final String SHEET_NAME 	= "REPORT";
	
	static class ColPosition{
		//zero-based
		public static final Integer PERFORM_TIME		= 0;
		public static final Integer PUBBLICATORE 		= 1;
		public static final Integer NOME_FILE 			= 2;
		public static final Integer ESITO_ELABORAZIONE	= 3;
		public static final Integer MESSAGGIO_ESITO		= 4;
	}
	
	public Integer getDataSize() {
		return 6;
	}
	
	private String pubblicatore;
	private String filename;
	private String performTime;
	private String esito;
	private String messaggioEsito;


	public ReportElaborazioneRow(){
		super();
	}
	
	public ReportElaborazioneRow(String pubblicatore, String filename,
			String performTime, String esito, String messaggioEsito) {
		super();
		this.pubblicatore = pubblicatore;
		this.filename = filename;
		this.performTime = performTime;
		this.esito = esito;
		this.messaggioEsito = messaggioEsito;
	}

	public List<Object> getData() {
		super.getData();
		if (pubblicatore != null) data.set(ColPosition.PUBBLICATORE, pubblicatore.trim());
		if (filename != null) data.set(ColPosition.NOME_FILE, filename.trim());
		if (performTime != null) data.set(ColPosition.PERFORM_TIME, performTime.trim());
		if (esito != null) data.set(ColPosition.ESITO_ELABORAZIONE, esito.trim());
		if (messaggioEsito != null) data.set(ColPosition.MESSAGGIO_ESITO, messaggioEsito.trim());
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		pubblicatore 	= (String) data.get(ColPosition.PUBBLICATORE);
		performTime 	= (String) data.get(ColPosition.PERFORM_TIME);
		esito 			= (String) data.get(ColPosition.ESITO_ELABORAZIONE);
		messaggioEsito 	= (String) data.get(ColPosition.MESSAGGIO_ESITO);
		filename		= (String) data.get(ColPosition.NOME_FILE);

	}

	public List<Integer> keyCols(){
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.PUBBLICATORE);
		keyCols.add(ColPosition.NOME_FILE);
		keyCols.add(ColPosition.PERFORM_TIME);
		return keyCols;
	}

	public String getPubblicatore() {
		return pubblicatore;
	}

	public void setPubblicatore(String pubblicatore) {
		this.pubblicatore = pubblicatore;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPerformTime() {
		return performTime;
	}

	public void setPerformTime(String performTime) {
		this.performTime = performTime;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}

	public String getMessaggioEsito() {
		return messaggioEsito;
	}

	public void setMessaggioEsito(String messaggioEsito) {
		this.messaggioEsito = messaggioEsito;
	}

	
}
