package it.desimone.gsheetsaccess.ranking;

import it.desimone.gsheetsaccess.ranking.RankingThresholdsNew.ThresholdsNew.ThresholdParameter;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.thoughtworks.xstream.XStream;

public class RankingThresholdsNew {
	private Map<TipoTorneo, ThresholdsNew> sogliePerTipoTorneo = new HashMap<TipoTorneo, ThresholdsNew>();
	static class ThresholdsNew{
		
		public enum ThresholdParameter{
			 MIN_NUM_OF_TABLES_IN_TOURNAMENT
			,MAX_NUM_OF_TOURNAMENTS_BY_MIN_TOURNAMENTS_AND_MAX_PERCENTAGE
			,MAX_NUM_OF_ROUNDS_BY_TOURNAMENT
			,MAX_NUM_OF_ROUNDS_PLAYED_EFFECTIVELY;
		}
		
		private Integer minTables;
		private Integer minTournaments;
		private BigDecimal maxPercentage;
		private Integer maxNumOfRoundsByTournament;
		private Integer maxNumOfRoundsPlayedEffectively;
		//private List<ThresholdParameter> criteria;
		private ThresholdParameter[] criteria;
		public ThresholdsNew(Integer minTables, Integer minTournaments, BigDecimal maxPercentage, Integer maxNumOfRoundsByTournament, Integer maxNumOfRoundsPlayedEffectively, ThresholdParameter[] criteria /*List<ThresholdParameter> criteria*/) {
			super();
			this.minTables = minTables;
			this.minTournaments = minTournaments;
			this.maxPercentage = maxPercentage;
			this.maxNumOfRoundsByTournament = maxNumOfRoundsByTournament;
			this.maxNumOfRoundsPlayedEffectively = maxNumOfRoundsPlayedEffectively;
			this.criteria = criteria;
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
		
		public Integer getMaxNumOfRoundsByTournament() {
			return maxNumOfRoundsByTournament;
		}

		public void setMaxNumOfRoundsByTournament(Integer maxNumOfRoundsByTournament) {
			this.maxNumOfRoundsByTournament = maxNumOfRoundsByTournament;
		}

		public Integer getMaxNumOfRoundsPlayedEffectively() {
			return maxNumOfRoundsPlayedEffectively;
		}

		public void setMaxNumOfRoundsPlayedEffectively(Integer maxNumOfRoundsPlayedEffectively) {
			this.maxNumOfRoundsPlayedEffectively = maxNumOfRoundsPlayedEffectively;
		}

//		public List<ThresholdParameter> getCriteria() {
//			return criteria;
//		}
//
//		public void setCriteria(List<ThresholdParameter> criteria) {
//			this.criteria = criteria;
//		}

		public ThresholdParameter[] getCriteria() {
			return criteria;
		}

		public void setCriteria(ThresholdParameter[] criteria) {
			this.criteria = criteria;
		}
		
		public boolean containsCriterion(ThresholdParameter criterion){
			boolean result = false;
			Arrays.sort(this.criteria);
			result = Arrays.binarySearch(this.criteria, criterion) >= 0;
			return result;
		}
		
		@Override
		public String toString() {
			return "ThresholdsNew [minTables=" + minTables + ", minTournaments=" + minTournaments + ", maxPercentage="
					+ maxPercentage + ", maxNumOfRoundsByTournament=" + maxNumOfRoundsByTournament
					+ ", maxNumOfRoundsPlayedEffectively=" + maxNumOfRoundsPlayedEffectively + ", criteria=" + Arrays.toString(criteria)
					+ "]";
		}

	}
	public void addThresholds(TipoTorneo tipoTorneo, ThresholdsNew thresholds){
		sogliePerTipoTorneo.put(tipoTorneo, thresholds);
	}
	public ThresholdsNew getThresholds(TipoTorneo tipoTorneo){
		return sogliePerTipoTorneo.get(tipoTorneo);
	}
	
	public Set<TipoTorneo> getManagedTournamentsType(){
		return sogliePerTipoTorneo.keySet();
	}
	@Override
	public String toString() {
		return "RankingThresholdNew [sogliePerTipoTorneo=" + sogliePerTipoTorneo
				+ "]";
	}
	
	public static void main (String[] args){
		TreeMap<String, RankingThresholdsNew> mappingScorers = new TreeMap<String, RankingThresholdsNew>();
		
		RankingThresholdsNew rankingThresholds2023 = new RankingThresholdsNew();
		//List<ThresholdParameter> l1 = Arrays.asList(new ThresholdParameter[]{ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT, ThresholdParameter.MAX_NUM_OF_TOURNAMENTS_BY_MIN_TOURNAMENTS_AND_MAX_PERCENTAGE});
		ThresholdParameter[] l1_a = new ThresholdParameter[]{ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT, ThresholdParameter.MAX_NUM_OF_TOURNAMENTS_BY_MIN_TOURNAMENTS_AND_MAX_PERCENTAGE};
		rankingThresholds2023.addThresholds(TipoTorneo.CAMPIONATO, new ThresholdsNew(6,1, new BigDecimal(100), null, null, l1_a));
		ThresholdParameter[] l1_b = new ThresholdParameter[]{ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT, ThresholdParameter.MAX_NUM_OF_TOURNAMENTS_BY_MIN_TOURNAMENTS_AND_MAX_PERCENTAGE};
		rankingThresholds2023.addThresholds(TipoTorneo.INTERCLUB, new ThresholdsNew(6,1, new BigDecimal(33.3), null, null, l1_b));
		ThresholdParameter[] l1_c = new ThresholdParameter[]{ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT, ThresholdParameter.MAX_NUM_OF_TOURNAMENTS_BY_MIN_TOURNAMENTS_AND_MAX_PERCENTAGE};
		rankingThresholds2023.addThresholds(TipoTorneo.OPEN, new ThresholdsNew(6,3, new BigDecimal(13.5), null, null, l1_c));
		ThresholdParameter[] l1_d = new ThresholdParameter[]{ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT, ThresholdParameter.MAX_NUM_OF_TOURNAMENTS_BY_MIN_TOURNAMENTS_AND_MAX_PERCENTAGE};
		rankingThresholds2023.addThresholds(TipoTorneo.MASTER, new ThresholdsNew(6,1, new BigDecimal(30), null, null, l1_d));
		ThresholdParameter[] l1_e = new ThresholdParameter[]{ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT, ThresholdParameter.MAX_NUM_OF_TOURNAMENTS_BY_MIN_TOURNAMENTS_AND_MAX_PERCENTAGE};
		rankingThresholds2023.addThresholds(TipoTorneo.RADUNO_NAZIONALE, new ThresholdsNew(6,1, new BigDecimal(33.3), null, null, l1_e));
		mappingScorers.put("2023", rankingThresholds2023);
		RankingThresholdsNew rankingThresholds2024 = new RankingThresholdsNew();
		//List<ThresholdParameter> l2 = Arrays.asList(new ThresholdParameter[]{ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT, ThresholdParameter.MAX_NUM_OF_ROUNDS_BY_TOURNAMENT});
		ThresholdParameter[] l2 = new ThresholdParameter[]{ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT, ThresholdParameter.MAX_NUM_OF_ROUNDS_BY_TOURNAMENT};
		rankingThresholds2024.addThresholds(TipoTorneo.CAMPIONATO, new ThresholdsNew(6,1, new BigDecimal(100), 45, null, l2));
		rankingThresholds2024.addThresholds(TipoTorneo.INTERCLUB, new ThresholdsNew(6,1, new BigDecimal(33.3), null, null, l1_a));
		rankingThresholds2024.addThresholds(TipoTorneo.OPEN, new ThresholdsNew(6,3, new BigDecimal(13.5), null, null, l1_a));
		rankingThresholds2024.addThresholds(TipoTorneo.MASTER, new ThresholdsNew(15,1, new BigDecimal(30), null, null, l1_a));
		rankingThresholds2024.addThresholds(TipoTorneo.RADUNO_NAZIONALE, new ThresholdsNew(6,1, new BigDecimal(33.3), null, null, l1_a));
		mappingScorers.put("2024", rankingThresholds2024);
		XStream xStream = new XStream();
		String thresholds = xStream.toXML(mappingScorers);
		
		System.out.println(thresholds);
		
		File thresholdsFile = new File("C:\\Users\\mds\\Desktop\\RankingThresholdsNew.xml");
		
//		Map mappa = (Map) xStream.fromXML(thresholdsFile);
		
//		System.out.println(mappa);
	}
}
