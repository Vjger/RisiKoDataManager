package it.desimone.gsheetsaccess.gsheets.dto;

import it.desimone.utils.MyLogger;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClassificheRow extends AbstractSheetRow {
	
	public static final String SHEET_CLASSIFICHE 	= "CLASSIFICHE";
	
	private static final NumberFormat nf = new DecimalFormat("#.0000",DecimalFormatSymbols.getInstance(Locale.ITALY));
	
	public static class ColPosition{
		//zero-based
		public static final Integer ID_TORNEO 				= 0;
		public static final Integer ID_GIOCATORE 			= 1;
		public static final Integer NOMINATIVO_GIOCATORE 	= 2;
		public static final Integer CLUB_GIOCATORE 			= 3;
		public static final Integer POSIZIONE 				= 4;
		public static final Integer PUNTI 					= 5;
		public static final Integer NUMERO_VITTORIE 		= 6;
		public static final Integer PARTITE_GIOCATE 		= 7;
		public static final Integer UPDATE_TIME				= 8;
	}
	
	public Integer getDataSize() {
		return 10;
	}

	private String idTorneo;
	private Integer idGiocatore;
	private String nominativoGiocatore;
	private String clubGiocatore;
	private Integer posizione;
	private Double punti;
	private Integer numeroVittorie;
	private Integer partiteGiocate;
	private String updateTime;

	public String getIdTorneo() {
		return idTorneo;
	}

	public void setIdTorneo(String idTorneo) {
		this.idTorneo = idTorneo;
	}

	public Integer getIdGiocatore() {
		return idGiocatore;
	}

	public void setIdGiocatore(Integer idGiocatore) {
		this.idGiocatore = idGiocatore;
		this.nominativoGiocatore = getGiocatoreCellById(idGiocatore);
		this.clubGiocatore = getClubGiocatoreCellById(idGiocatore);
	}

	public String getNominativoGiocatore() {
		return nominativoGiocatore;
	}

	public void setNominativoGiocatore(String nominativoGiocatore) {
		this.nominativoGiocatore = nominativoGiocatore;
	}

	public String getClubGiocatore() {
		return clubGiocatore;
	}

	public void setClubGiocatore(String clubGiocatore) {
		this.clubGiocatore = clubGiocatore;
	}

	public Integer getPosizione() {
		return posizione;
	}

	public void setPosizione(Integer posizione) {
		this.posizione = posizione;
	}

	public Double getPunti() {
		return punti;
	}

	public void setPunti(Double punti) {
		this.punti = punti;
	}

	public Integer getNumeroVittorie() {
		return numeroVittorie;
	}

	public void setNumeroVittorie(Integer numeroVittorie) {
		this.numeroVittorie = numeroVittorie;
	}

	public Integer getPartiteGiocate() {
		return partiteGiocate;
	}

	public void setPartiteGiocate(Integer partiteGiocate) {
		this.partiteGiocate = partiteGiocate;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	private static String getGiocatoreCellById(Integer id){
		StringBuilder buffer = new StringBuilder();
		buffer.append("=CONCATENATE(");
		buffer.append("CERCA.VERT(");
		buffer.append(id);
		buffer.append(";");
		buffer.append(AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME);
		buffer.append("!A:E; "+(AnagraficaGiocatoreRow.ColPosition.NOME+1)+"; FALSE);");
		buffer.append("\" \";");
		buffer.append("CERCA.VERT(");
		buffer.append(id);
		buffer.append(";");
		buffer.append(AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME);
		buffer.append("!A:E; "+(AnagraficaGiocatoreRow.ColPosition.COGNOME+1)+"; FALSE);");
		buffer.append(")");
		return buffer.toString();
	}
	private static String getClubGiocatoreCellById(Integer id){
		StringBuilder buffer = new StringBuilder();
		buffer.append("=CERCA.VERT(");
		buffer.append(id);
		buffer.append(";");
		buffer.append(AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME);
		buffer.append("!A:E; "+(AnagraficaGiocatoreRow.ColPosition.ULTIMO_CLUB+1)+"; FALSE)");
		return buffer.toString();
	}
	
	public List<Object> getData() {
		super.getData();
		if (idTorneo != null) data.set(ColPosition.ID_TORNEO, idTorneo.trim());
		if (idGiocatore != null) data.set(ColPosition.ID_GIOCATORE, idGiocatore);
		if (nominativoGiocatore != null) data.set(ColPosition.NOMINATIVO_GIOCATORE, nominativoGiocatore);
		if (clubGiocatore != null) data.set(ColPosition.CLUB_GIOCATORE, clubGiocatore.trim());
		if (posizione != null) data.set(ColPosition.POSIZIONE, posizione);
		if (punti != null) data.set(ColPosition.PUNTI, punti);
		if (numeroVittorie != null) data.set(ColPosition.NUMERO_VITTORIE, numeroVittorie);
		if (partiteGiocate != null) data.set(ColPosition.PARTITE_GIOCATE, partiteGiocate);
		if (updateTime != null) data.set(ColPosition.UPDATE_TIME, updateTime);
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		idTorneo 		= (String) data.get(ColPosition.ID_TORNEO);
		idGiocatore 	= Integer.valueOf((String)data.get(ColPosition.ID_GIOCATORE));
		clubGiocatore 	= (String) data.get(ColPosition.CLUB_GIOCATORE);
		posizione 		= Integer.valueOf((String)data.get(ColPosition.POSIZIONE));
		try {
			punti 		= nf.parse((String)data.get(ColPosition.PUNTI)).doubleValue();
		} catch (ParseException e) {
			MyLogger.getLogger().severe("Errore nel parsing di un punteggio: "+data);
			throw new IllegalArgumentException("Errore nel parsing di un punteggio: "+data);
		}
		numeroVittorie 	= Integer.valueOf((String)data.get(ColPosition.NUMERO_VITTORIE));
		partiteGiocate 	= Integer.valueOf((String)data.get(ColPosition.PARTITE_GIOCATE));
		updateTime		= (String) data.get(ColPosition.UPDATE_TIME);
	}

	public List<Integer> keyCols(){
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.ID_TORNEO);
		keyCols.add(ColPosition.ID_GIOCATORE);
		return keyCols;
	}

}
