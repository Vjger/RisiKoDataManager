package it.desimone.gsheetsaccess.statistiche;

import java.util.Objects;

public class MatchByYearAndClubValue {

	private String dataTurno;
	private Integer numeroTavoli;

	private String tipoTorneo;

	public MatchByYearAndClubValue(String dataTurno) {
		super();
		this.dataTurno = dataTurno;
		this.numeroTavoli = 0;
	}


	public String getDataTurno() {
		return dataTurno;
	}

	public void setDataTurno(String dataTurno) {
		this.dataTurno = dataTurno;
	}

	public Integer getNumeroTavoli() {
		return numeroTavoli;
	}

	public void setNumeroTavoli(Integer numeroTavoli) {
		this.numeroTavoli = numeroTavoli;
	}

	public String getTipoTorneo() {
		return tipoTorneo;
	}

	public void setTipoTorneo(String tipoTorneo) {
		this.tipoTorneo = tipoTorneo;
	}

	public void addNumeroTavoli(Integer numeroTavoli) {
		this.numeroTavoli += numeroTavoli;
	}


	@Override
	public int hashCode() {
		return Objects.hash(dataTurno);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchByYearAndClubValue other = (MatchByYearAndClubValue) obj;
		return Objects.equals(dataTurno, other.dataTurno);
	}


	@Override
	public String toString() {
		return "MatchByYearAndClubValue [dataTurno=" + dataTurno + ", numeroTavoli=" + numeroTavoli + ", tipoTorneo="
				+ tipoTorneo + "]";
	}
}
