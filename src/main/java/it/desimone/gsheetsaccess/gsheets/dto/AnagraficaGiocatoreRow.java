package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.Collections;
import java.util.List;

public class AnagraficaGiocatoreRow extends AbstractSheetRow{
	
	public static final String SHEET_GIOCATORI_NAME 	= "GIOCATORI";

	private Integer id;
	private String nome;
	private String cognome;
	private String ultimoClub;
	private String idTorneoProvenienza;
	private String updateTime;
	
	static class ColPosition{
		//zero-based
		public static final Integer ID 					= 0;
		public static final Integer NOME 				= 1;
		public static final Integer COGNOME 			= 2;
		public static final Integer ULTIMO_CLUB 		= 3;
		public static final Integer ID_TORNEO_PROVENIENZA 	= 4;
		public static final Integer UPDATE_TIME 		= 5;
	}
	
	public AnagraficaGiocatoreRow(){}
	
	public AnagraficaGiocatoreRow(AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidottaRow){
		this.id = anagraficaGiocatoreRidottaRow.getId();
		this.nome = anagraficaGiocatoreRidottaRow.getNome();
		this.cognome = anagraficaGiocatoreRidottaRow.getCognome();
	}
	
	public AnagraficaGiocatoreRow(Integer id){
		this.id = id;
	}
	
	public Integer getDataSize() {
		return 7;
	}
	
	public List<Integer> keyCols() {
		return Collections.singletonList(ColPosition.ID);
	}

	public List<Object> getData() {
		super.getData();
		if (id != null) data.set(ColPosition.ID, id);
		if (nome != null) data.set(ColPosition.NOME, nome.trim());
		if (cognome != null) data.set(ColPosition.COGNOME, cognome.trim());
		if (ultimoClub != null) data.set(ColPosition.ULTIMO_CLUB, ultimoClub.trim());
		if (idTorneoProvenienza != null) data.set(ColPosition.ID_TORNEO_PROVENIENZA, idTorneoProvenienza.trim());
		if (updateTime != null) data.set(ColPosition.UPDATE_TIME, updateTime);
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		id 				= Integer.valueOf((String)data.get(ColPosition.ID));
		nome 			= (String) data.get(ColPosition.NOME);
		cognome 		= (String) data.get(ColPosition.COGNOME);
		ultimoClub 		= (String) data.get(ColPosition.ULTIMO_CLUB);
		idTorneoProvenienza 	= (String) data.get(ColPosition.ID_TORNEO_PROVENIENZA);
		updateTime		= (String) data.get(ColPosition.UPDATE_TIME);
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getCognome() {
		return cognome;
	}


	public void setCognome(String cognome) {
		this.cognome = cognome;
	}


	public String getUltimoClub() {
		return ultimoClub;
	}


	public void setUltimoClub(String ultimoClub) {
		this.ultimoClub = ultimoClub;
	}


	public String getIdUltimoTorneo() {
		return idTorneoProvenienza;
	}


	public void setIdUltimoTorneo(String idUltimoTorneo) {
		this.idTorneoProvenienza = idUltimoTorneo;
	}


	public String getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AnagraficaGiocatoreRow other = (AnagraficaGiocatoreRow) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AnagraficaGiocatoreRow [id=" + id + ", nome=" + nome
				+ ", cognome=" + cognome + "]";
	}

	
	
}
