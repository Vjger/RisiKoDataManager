package it.desimone.gsheetsaccess.analyzer;

import java.math.BigDecimal;
import java.util.Objects;

import it.desimone.gsheetsaccess.dto.TorneoPubblicato;

public class TorneiDisputati{
	private TorneoPubblicato torneoPubblicato;
	private BigDecimal rankingScore;
	public TorneiDisputati(TorneoPubblicato torneoPubblicato, BigDecimal rankingScore) {
		super();
		this.torneoPubblicato = torneoPubblicato;
		this.rankingScore = rankingScore;
	}
	public TorneoPubblicato getTorneoPubblicato() {
		return torneoPubblicato;
	}
	public void setTorneoPubblicato(TorneoPubblicato torneoPubblicato) {
		this.torneoPubblicato = torneoPubblicato;
	}
	public BigDecimal getRankingScore() {
		return rankingScore;
	}
	public void setRankingScore(BigDecimal rankingScore) {
		this.rankingScore = rankingScore;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rankingScore, torneoPubblicato);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TorneiDisputati other = (TorneiDisputati) obj;
		return Objects.equals(rankingScore, other.rankingScore)
				&& Objects.equals(torneoPubblicato, other.torneoPubblicato);
	}
	@Override
	public String toString() {
		return "TorneiDisputati [torneoPubblicato=" + torneoPubblicato + ", rankingScore=" + rankingScore + "]";
	}
	
}
