package it.desimone.gsheetsaccess.ranking;

import it.desimone.gsheetsaccess.common.ResourceWorking;
import it.desimone.gsheetsaccess.ranking.RankingThresholds.Thresholds;
import it.desimone.risiko.torneo.dto.SchedaTorneo.TipoTorneo;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import com.thoughtworks.xstream.XStream;

public class RankingBuilder {

	private static TreeMap<String, RankingThresholds> mappingScorers = new TreeMap<String, RankingThresholds>();
	
	static{
		//Inizializzazione mappa
		XStream xStream = new XStream();
		File thresholdsFile = new File(ResourceWorking.rankingThresholds());
		mappingScorers = (TreeMap) xStream.fromXML(thresholdsFile);
	}
	
	/* Se non si trova quell'anno si prende il più recente successivo ad esso. 
	 * Se non c'è nemmeno così si prende l'ultimo.
	 * */
	private static RankingThresholds scorersFactory(String year){
		RankingThresholds result = mappingScorers.get(year);
		if (result == null){
			Map.Entry<String, RankingThresholds> greaterEntry = mappingScorers.higherEntry(year);
			if (greaterEntry == null){
				greaterEntry = mappingScorers.lastEntry();
			}
			if (greaterEntry != null){
				result = greaterEntry.getValue();
			}
		}
		return result;
	}
	
	public static RankingThresholds getRankingThreshold(String year){
		RankingThresholds result = scorersFactory(year);
		return result;
	}

	public static void main (String[] args){
		System.out.println(getRankingThreshold("2023"));
		System.out.println(getRankingThreshold("2024"));
	}
	
}
