package it.desimone.gsheetsaccess.statistiche;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.itextpdf.text.log.SysoLogger;

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

	public Set<String> getDateSet(String year){
		return statisticsByYear.values().stream().flatMap(List::stream).filter(m -> m.isOfYear(year)).map(MatchByYearAndClubValue::getDataTurno).collect(Collectors.toCollection(TreeSet<String>::new));
	}
	
	public List<String> getDateList(String year){
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return getDateSet(year).stream().map(d -> LocalDate.parse(d, inputFormatter)).sorted().map(d -> d.format(inputFormatter)).collect(Collectors.toList());
	}
	
	public MatchByYearAndClubValue[][] getMatrix(String year) {
		Set<String> organizzatori = selectOrganizzatoriByYear(year);
		List<String> dateList = getDateList(year);
		MatchByYearAndClubValue[][] matrix = new MatchByYearAndClubValue[dateList.size()][organizzatori.size()];
		
		int columnIndex = 0;
		for (String organizzatore : organizzatori) {
			 List<MatchByYearAndClubValue> matches = statisticsByYear.get(new MatchByYearAndClubKey(organizzatore));
			 for (MatchByYearAndClubValue match: matches) {
				 int rowIndex = dateList.indexOf(match.getDataTurno());
				 if (rowIndex != -1) {
					 matrix[rowIndex][columnIndex] = match;
				 }
			 }
			 columnIndex++;
		}
//		for (MatchByYearAndClubKey matchByYearAndClubKey : statisticsByYear.keySet()) {
//			 List<MatchByYearAndClubValue> matches = statisticsByYear.get(matchByYearAndClubKey);
//			 for (MatchByYearAndClubValue match: matches) {
//				 int rowIndex = dateList.indexOf(match.getDataTurno());
//				 if (rowIndex != -1) {
//					 matrix[rowIndex][columnIndex] = match;
//				 }
//			 }
//			 columnIndex++;
//		}
		return matrix;
	}
	
	public Set<String> selectOrganizzatoriByYear(String year){
		Set<String> organizzatori = getOrganizzatori();
		Iterator<String> iterator = organizzatori.iterator();
		
		String organizzatore = iterator.next();
		while (iterator.hasNext()) {
			
			boolean haUnEVentoNellAnno = false;
			for (MatchByYearAndClubValue matchByYearAndClubValue: getStatistics(new MatchByYearAndClubKey(organizzatore))) {
				if (matchByYearAndClubValue.isOfYear(year)) {
					haUnEVentoNellAnno = true;
					break;
				}
			}
			
			if (!haUnEVentoNellAnno) {
				iterator.remove();
			}
			organizzatore = iterator.next();
		}
		return organizzatori;
	}
	
	public MatchByYearAndClubValue[] getMatrixRow(String year, int rowIndex) {
		MatchByYearAndClubValue[] matrixRow = getMatrix(year)[rowIndex];
		return matrixRow;
	}
	
	private List<MatchByYearAndClubValue> getMatchBySixter(String year, String organizzatore, boolean first){
		MatchByYearAndClubKey matchByYearAndClubKey = new MatchByYearAndClubKey(organizzatore);
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate inizioAnno = LocalDate.of(Integer.valueOf(year),1,1);
		LocalDate inizioSecondoSemestre = LocalDate.of(Integer.valueOf(year),7,1);
		if (first) {
			return getStatistics(matchByYearAndClubKey).stream().filter(m -> !LocalDate.parse(m.getDataTurno(), inputFormatter).isBefore(inizioAnno) && LocalDate.parse(m.getDataTurno(), inputFormatter).isBefore(inizioSecondoSemestre)).collect(Collectors.toList());
		}else {
			return getStatistics(matchByYearAndClubKey).stream().filter(m -> !LocalDate.parse(m.getDataTurno(), inputFormatter).isBefore(inizioAnno) && !LocalDate.parse(m.getDataTurno(), inputFormatter).isBefore(inizioSecondoSemestre)).collect(Collectors.toList());
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
