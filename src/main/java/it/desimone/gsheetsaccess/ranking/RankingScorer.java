package it.desimone.gsheetsaccess.ranking;

import it.desimone.gsheetsaccess.ranking.RankingThresholds.Thresholds;
import it.desimone.gsheetsaccess.ranking.RankingThresholdsNew.ThresholdsNew;
import it.desimone.gsheetsaccess.ranking.RankingThresholdsNew.ThresholdsNew.ThresholdParameter;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;

import java.math.BigDecimal;

public class RankingScorer {
	
	private static final BigDecimal unoPUNTOdue = new BigDecimal(1.2);

	public static BigDecimal calcolaScore(String year, int posizioneNelTorneo, TipoTorneo tipoTorneo, int numeroTavoli, int numeroPartecipanti,int numeroTurni){
		BigDecimal score = BigDecimal.ZERO;
		BigDecimal b = BigDecimal.ONE;
		
		//if (hasMinimuNumberTables(year, tipoTorneo, numeroTavoli)){
		if (hasMinimuNumberTablesIfManaged(year, tipoTorneo, numeroTavoli)){			
			BigDecimal VT = a(posizioneNelTorneo).multiply(new BigDecimal(100).add(b.multiply(new BigDecimal(numeroTavoli)))).multiply(classe(tipoTorneo, numeroTurni));
	
			if (posizioneNelTorneo == 1){
				score = VT;
			}else{
				score = VT.multiply(new BigDecimal(Math.exp(-posizioneNelTorneo/20.0)));
			}
		}		
		return score;
	}
	
	public static boolean hasMinimuNumberTables(String year, TipoTorneo tipoTorneo, int numeroTavoli){
		boolean result = false;
		RankingThresholds rankingThresholds = RankingBuilder.getRankingThreshold(year);
		Thresholds thresholds = rankingThresholds.getThresholds(tipoTorneo);
		if (thresholds != null){
			result = numeroTavoli >= thresholds.getMinTables();
		}
		return result;
	}
	
	public static boolean hasMinimuNumberTablesIfManaged(String year, TipoTorneo tipoTorneo, int numeroTavoli){
		boolean result = true;
		RankingThresholdsNew rankingThresholds = RankingBuilderNew.getRankingThreshold(year);
		ThresholdsNew thresholds = rankingThresholds.getThresholds(tipoTorneo);
		if (thresholds != null && thresholds.containsCriterion(ThresholdParameter.MIN_NUM_OF_TABLES_IN_TOURNAMENT)){
			result = numeroTavoli >= thresholds.getMinTables();
		}
		return result;
	}
	
	public static boolean hasMinimuNumberTables(String year, int numeroTavoli, int numeroMinimoTavoli){
		boolean	result = numeroTavoli >= numeroMinimoTavoli;
		return result;
	}
	
	private static BigDecimal a(int posizione){
		return posizione == 1?unoPUNTOdue:BigDecimal.ONE;
	}
	
	private static BigDecimal classe(TipoTorneo tipoTorneo, int numeroTurni){
		BigDecimal result = BigDecimal.ZERO;
		switch (tipoTorneo) {
		case RADUNO_NAZIONALE:
			result = new BigDecimal(5);
			break;
		case MASTER:
			result = new BigDecimal(4);
			break;
		case OPEN:
			result = new BigDecimal(2);
			break;
		case INTERCLUB:
			result = new BigDecimal(1.5);
			break;
		case CAMPIONATO:
			if (numeroTurni < 3){
				result = new BigDecimal(0.2);
			}else if (numeroTurni == 3){
				result = new BigDecimal(0.5);
			}else if (numeroTurni == 4){
				result = new BigDecimal(0.7);
			}else if (numeroTurni == 5){
				result = new BigDecimal(0.8);
			}else {
				result = BigDecimal.ONE;
			}
			break;
		default:
			break;
		}
		return result;
	}
	
	
}
