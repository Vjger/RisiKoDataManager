package it.desimone.gsheetsaccess.htmlpublisher;

import java.util.Objects;

public class FalsePositivePlayers {

	private int idAnagrafica1;
	private int idAnagrafica2;
	
	public FalsePositivePlayers(int idAnagrafica1, int idAnagrafica2) {
		super();
		this.idAnagrafica1 = idAnagrafica1;
		this.idAnagrafica2 = idAnagrafica2;
	}
	public int getIdAnagrafica1() {
		return idAnagrafica1;
	}
	public void setIdAnagrafica1(int idAnagrafica1) {
		this.idAnagrafica1 = idAnagrafica1;
	}
	public int getIdAnagrafica2() {
		return idAnagrafica2;
	}
	public void setIdAnagrafica2(int idAnagrafica2) {
		this.idAnagrafica2 = idAnagrafica2;
	}
	@Override
	public int hashCode() {
		return Objects.hash(idAnagrafica1, idAnagrafica2);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FalsePositivePlayers other = (FalsePositivePlayers) obj;
		return (idAnagrafica1 == other.idAnagrafica1 && idAnagrafica2 == other.idAnagrafica2) || (idAnagrafica2 == other.idAnagrafica1 && idAnagrafica1 == other.idAnagrafica2);
	}
	@Override
	public String toString() {
		return "FalsePositivePlayers [idAnagrafica1=" + idAnagrafica1 + ", idAnagrafica2=" + idAnagrafica2 + "]";
	}

	
	
}
