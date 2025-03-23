package it.desimone.gsheetsaccess;

import it.desimone.gsheetsaccess.ranking.ElementoRanking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ScoreGiocatore implements Comparable<ScoreGiocatore>{

	private Integer idGiocatore;
	private BigDecimal scoreRanking;
	private List<ElementoRanking> elementiRanking;
	
	public ScoreGiocatore(){
		elementiRanking = new ArrayList<ElementoRanking>();
	}
	
	public ScoreGiocatore(Integer idGiocatore){
		this();
		this.idGiocatore = idGiocatore;
	}
	
	public void addElementoRanking(ElementoRanking elementoRanking){
		elementiRanking.add(elementoRanking);
	}

	public Integer getIdGiocatore() {
		return idGiocatore;
	}

	public void setIdGiocatore(Integer idGiocatore) {
		this.idGiocatore = idGiocatore;
	}

	public List<ElementoRanking> getElementiRanking() {
		return elementiRanking;
	}

	public void setElementiRanking(List<ElementoRanking> elementiRanking) {
		this.elementiRanking = elementiRanking;
	}

	public BigDecimal getScoreRanking() {
		return scoreRanking;
	}

	public void setScoreRanking(BigDecimal scoreRanking) {
		this.scoreRanking = scoreRanking;
	}

	public int compareTo(ScoreGiocatore o) {
		int compare = 0;
		if (scoreRanking != null){
			compare = scoreRanking.compareTo(o.getScoreRanking());
		}
		return compare;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idGiocatore == null) ? 0 : idGiocatore.hashCode());
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
		ScoreGiocatore other = (ScoreGiocatore) obj;
		if (idGiocatore == null) {
			if (other.idGiocatore != null)
				return false;
		} else if (!idGiocatore.equals(other.idGiocatore))
			return false;
		return true;
	}
	
}
