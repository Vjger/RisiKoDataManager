package it.desimone.gsheetsaccess.statistiche;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ReportTournaments {

	private Map<MatchByYearAndClubKey, List<MatchByYearAndClubValue>> statisticsByYear = new TreeMap<MatchByYearAndClubKey, List<MatchByYearAndClubValue>>();

	
	public void putStatistics(MatchByYearAndClubKey matchByYearAndClubKey, List<MatchByYearAndClubValue> matchByYearAndClubValues) {
		statisticsByYear.put(matchByYearAndClubKey, matchByYearAndClubValues);
	}
	
	public List<MatchByYearAndClubValue> getStatistics(MatchByYearAndClubKey matchByYearAndClubKey) {
		return statisticsByYear.get(matchByYearAndClubKey);
	}
	
	public boolean containsStatistics(MatchByYearAndClubKey matchByYearAndClubKey) {
		return statisticsByYear.containsKey(matchByYearAndClubKey);
	}
	
	public Set<String> getOrganizzatori(){
		return statisticsByYear.keySet().stream().map(MatchByYearAndClubKey::getOrganizzatore).collect(Collectors.toCollection(TreeSet<String>::new));
	}
	
	public Set<String> getDateSet(){
		return statisticsByYear.values().stream().flatMap(List::stream).map(MatchByYearAndClubValue::getDataTurno).collect(Collectors.toCollection(TreeSet<String>::new));
	}

	public List<String> getDateList(){
		return getDateSet().stream().sorted().collect(Collectors.toList());
	}
	
	public int[][] getMatrix() {
		Set<String> organizzatori = getOrganizzatori();
		List<String> dateList = getDateList();
		int[][] matrix = new int[organizzatori.size()][dateList.size()];
		
		int columnIndex = 0;
		for (MatchByYearAndClubKey matchByYearAndClubKey : statisticsByYear.keySet()) {
			 List<MatchByYearAndClubValue> matches = statisticsByYear.get(matchByYearAndClubKey);
			 for (MatchByYearAndClubValue match: matches) {
				 int rowIndex = dateList.indexOf(match.getDataTurno());
				 matrix[rowIndex][columnIndex] = match.getNumeroTavoli();
			 }
			 columnIndex++;
		}
		return matrix;
	}
	
	public int[] getMatrixRow(int rowIndex) {
		return getMatrix()[rowIndex];
	}
}
