package it.desimone.gsheetsaccess.statistiche;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return getDateSet().stream().map(d -> LocalDate.parse(d, inputFormatter)).sorted().map(d -> d.format(inputFormatter)).collect(Collectors.toList());
	}
	
	public MatchByYearAndClubValue[][] getMatrix() {
		Set<String> organizzatori = getOrganizzatori();
		List<String> dateList = getDateList();
		MatchByYearAndClubValue[][] matrix = new MatchByYearAndClubValue[dateList.size()][organizzatori.size()];
		
		int columnIndex = 0;
		for (MatchByYearAndClubKey matchByYearAndClubKey : statisticsByYear.keySet()) {
			 List<MatchByYearAndClubValue> matches = statisticsByYear.get(matchByYearAndClubKey);
			 for (MatchByYearAndClubValue match: matches) {
				 int rowIndex = dateList.indexOf(match.getDataTurno());
				 matrix[rowIndex][columnIndex] = match;
			 }
			 columnIndex++;
		}
		return matrix;
	}
	
	public MatchByYearAndClubValue[] getMatrixRow(int rowIndex) {
		MatchByYearAndClubValue[] matrixRow = getMatrix()[rowIndex];
		return matrixRow;
	}
	
	private List<MatchByYearAndClubValue> getMatchBySixter(String year, String organizzatore, boolean first){
		MatchByYearAndClubKey matchByYearAndClubKey = new MatchByYearAndClubKey(organizzatore);
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate inizioSecondoSemestre = LocalDate.of(Integer.valueOf(year),7,1);
		if (first) {
			return getStatistics(matchByYearAndClubKey).stream().filter(m -> LocalDate.parse(m.getDataTurno(), inputFormatter).isBefore(inizioSecondoSemestre)).collect(Collectors.toList());
		}else {
			return getStatistics(matchByYearAndClubKey).stream().filter(m -> !LocalDate.parse(m.getDataTurno(), inputFormatter).isBefore(inizioSecondoSemestre)).collect(Collectors.toList());
		}
	}
	
	public int getNumeroDatePerSemestre(String year, String organizzatore, boolean first) {
		return getMatchBySixter(year, organizzatore, first).size();
	}
	
	public int getNumeroTavoliPerSemestre(String year, String organizzatore, boolean first) {
		int result = 0;
		for (MatchByYearAndClubValue matchByYearAndClubValue : getMatchBySixter(year, organizzatore, first)) {
			result += matchByYearAndClubValue.getNumeroTavoli();
		}
		return result;
	}
}
