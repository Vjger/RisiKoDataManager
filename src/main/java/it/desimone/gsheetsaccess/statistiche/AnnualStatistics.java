package it.desimone.gsheetsaccess.statistiche;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnualStatistics {

	private int numberOfClubs;
	private int numberOfEvents;
	private int numberOfMatches;
	private int numberOfPlayers;
	private int numberOfDates;
	
	private Map<String, Set<String>> annualDates = new HashMap<String, Set<String>>();
	private Set<String> annualOrganizers = new HashSet<String>();
	private Set<Integer> annualPlayers = new HashSet<Integer>();
	private Map<Integer, Set<String>> annualPlayerAndTournaments = new HashMap<Integer, Set<String>>();
	
	public int getNumberOfClubs() {
		return this.annualOrganizers.size();
	}
	public void setNumberOfClubs(int numberOfClubs) {
		this.numberOfClubs = numberOfClubs;
	}
	public int getNumberOfEvents() {
		return numberOfEvents;
	}
	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}
	public int getNumberOfMatches() {
		return numberOfMatches;
	}
	public void setNumberOfMatches(int numberOfMatches) {
		this.numberOfMatches = numberOfMatches;
	}
	public int getNumberOfPlayers() {
		return this.annualPlayers.size();
	}
	public int getNumberOfPlayersOfAnnualTournaments() {
		return this.annualPlayerAndTournaments.size();
	}
	public Set<Integer> getAnnualPlayers(){
		return annualPlayers;
	}
	public Map<Integer, Set<String>> getAnnualPlayersAndTournaments(){
		return annualPlayerAndTournaments;
	}
	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}
	public int getNumberOfDates() {
		return this.annualDates.values()
                .stream()
                .mapToInt(Set::size)
                .sum();
	}
	public void setNumberOfDates(int numberOfDates) {
		this.numberOfDates = numberOfDates;
	}
	
	public void addNumberOfClubs(int adder) {
		this.numberOfClubs += adder;
	}
	public void addClub(String club) {
		this.annualOrganizers.add(club);
	}
	
	public void addNumberOfEvents(int adder) {
		this.numberOfEvents += adder;
	}
	public void addNumberOfMatches(int adder) {
		this.numberOfMatches += adder;
	}
	public void addNumberOfPlayers(int adder) {
		this.numberOfPlayers += adder;
	}
	
	public void addPlayer(Integer playerId) {
		this.annualPlayers.add(playerId);
	}
	
	public void addPlayer(Set<Integer> playerIds, String tournamentId) {
		for (Integer playerId: playerIds) {
			this.annualPlayerAndTournaments.computeIfAbsent(playerId, k -> new HashSet<>()).add(tournamentId);
		}
	}
	
	public void addNumberOfDates(int adder) {
		this.numberOfDates += adder;
	}
	
	public void addDate(String organizzatore, String date) {
		this.annualDates.computeIfAbsent(organizzatore, k -> new HashSet<>()).add(date);
	}
	
	public Collection<Set<String>> getAnnualPlayersAndTournamentsDistribution(){
		return annualPlayerAndTournaments.values();
	}
	
}
