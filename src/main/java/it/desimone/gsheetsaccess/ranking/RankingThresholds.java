package it.desimone.gsheetsaccess.ranking;

import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RankingThresholds {
	private Map<TipoTorneo, Thresholds> sogliePerTipoTorneo = new HashMap<TipoTorneo, Thresholds>();
	static class Thresholds{
		private Integer minTables;
		private Integer minTournaments;
		private BigDecimal maxPercentage;
		public Thresholds(Integer minTables, Integer minTournaments, BigDecimal maxPercentage) {
			super();
			this.minTables = minTables;
			this.minTournaments = minTournaments;
			this.maxPercentage = maxPercentage;
		}
		
		public Integer getMinTables() {
			return minTables;
		}
		public void setMinTables(Integer minTables) {
			this.minTables = minTables;
		}
		public Integer getMinTournaments() {
			return minTournaments;
		}
		public void setMinTournaments(Integer minTournaments) {
			this.minTournaments = minTournaments;
		}
		public BigDecimal getMaxPercentage() {
			return maxPercentage;
		}
		public void setMaxPercentage(BigDecimal maxPercentage) {
			this.maxPercentage = maxPercentage;
		}

		@Override
		public String toString() {
			return "Thresholds [minTables=" + minTables + ", minTournaments="
					+ minTournaments + ", maxPercentage=" + maxPercentage + "]";
		}
	}
	public void addThresholds(TipoTorneo tipoTorneo, Thresholds thresholds){
		sogliePerTipoTorneo.put(tipoTorneo, thresholds);
	}
	public Thresholds getThresholds(TipoTorneo tipoTorneo){
		return sogliePerTipoTorneo.get(tipoTorneo);
	}
	
	public Set<TipoTorneo> getManagedTournamentsType(){
		return sogliePerTipoTorneo.keySet();
	}
	@Override
	public String toString() {
		return "RankingThreshold [sogliePerTipoTorneo=" + sogliePerTipoTorneo
				+ "]";
	}
	
	
}
