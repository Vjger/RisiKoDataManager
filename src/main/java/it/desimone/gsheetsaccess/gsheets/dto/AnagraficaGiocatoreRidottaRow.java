package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.ArrayList;
import java.util.List;

public class AnagraficaGiocatoreRidottaRow extends AbstractSheetRow{
	
	public static final String SHEET_ANAGRAFICA_NAME 	= "ANAGRAFICA";
	public static final String SHEET_DATA_ANALYSIS_NAME = "DATA_ANALYSIS";
	
	public static final String NOME_ANONIMO = "ANONIMO";
	public static final String COGNOME_ANONIMO = "ANONIMO";
	public static final String DATA_NASCITA_ANONIMO = "01/01/2019";

	private Integer id;
	private String nome;
	private String cognome;
	//private String email;
	private String dataDiNascita;
	private String updateTime;
	
	static class ColPosition{
		//zero-based
		public static final Integer ID 					= 0;
		public static final Integer NOME 				= 1;
		public static final Integer COGNOME 			= 2;
		//public static final Integer E_MAIL		 		= 3;
		public static final Integer DATA_DI_NASCITA		= 3;
		public static final Integer UPDATE_TIME 		= 4;
	}
	
	public Integer getDataSize() {
		return 6;
	}
	
	public List<Integer> keyCols() {
		List<Integer> keyCols = new ArrayList<Integer>();
		keyCols.add(ColPosition.NOME);
		keyCols.add(ColPosition.COGNOME);
		//keyCols.add(ColPosition.E_MAIL);
		keyCols.add(ColPosition.DATA_DI_NASCITA);
		return keyCols;
	}

	public List<Object> getData() {
		super.getData();
		if (id != null) data.set(ColPosition.ID, id);
		if (nome != null) data.set(ColPosition.NOME, nome.trim());
		if (cognome != null) data.set(ColPosition.COGNOME, cognome.trim());
		//if (email != null) data.set(ColPosition.E_MAIL, email.trim());
		if (dataDiNascita != null) data.set(ColPosition.DATA_DI_NASCITA, dataDiNascita.trim());
		if (updateTime != null) data.set(ColPosition.UPDATE_TIME, updateTime);
		return data;
	}

	public void setData(List<Object> data) {
		if (data == null || data.isEmpty()) return;
		super.setData(data);
		id 				= Integer.valueOf((String)data.get(ColPosition.ID));
		nome 			= (String) data.get(ColPosition.NOME);
		cognome 		= (String) data.get(ColPosition.COGNOME);
		//email	 		= (String) data.get(ColPosition.E_MAIL);
		dataDiNascita	= (String) data.get(ColPosition.DATA_DI_NASCITA);
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


	public String getDataDiNascita() {
		return dataDiNascita;
	}

	public void setDataDiNascita(String dataDiNascita) {
		this.dataDiNascita = dataDiNascita;
	}

//	public String getEmail() {
//		return email;
//	}
//
//
//	public void setEmail(String email) {
//		this.email = email;
//	}

	public String getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "[id=" + id + ", nome=" + nome
				+ ", cognome=" + cognome + ", dataDiNascita=" + dataDiNascita
				+ "]";
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
		AnagraficaGiocatoreRidottaRow other = (AnagraficaGiocatoreRidottaRow) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
