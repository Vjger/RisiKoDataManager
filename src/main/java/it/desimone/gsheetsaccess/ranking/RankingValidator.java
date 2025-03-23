package it.desimone.gsheetsaccess.ranking;

public class RankingValidator {

	private boolean hasMinimuNumberTablesIfManaged;
	private boolean validMaxNumOfTournamentsByMinTournamentsAndMaxPercentage;
	private boolean validByMaxNumOfRoundsByTournament;
	private boolean validByMaxNumOfRoundsPlayedEffectively;
	
	private static RankingValidator instance;
	
	public static RankingValidator getInstance() {
		if (instance == null) {
			instance = new RankingValidator();
		}
		init();
		return instance;
	}
	
	private static void init() {
		instance.hasMinimuNumberTablesIfManaged = true;
		instance.validMaxNumOfTournamentsByMinTournamentsAndMaxPercentage = true;
		instance.validByMaxNumOfRoundsByTournament = true;
		instance.validByMaxNumOfRoundsPlayedEffectively = true;
	}
	
	public void validMinimuNumberTablesIfManaged(Integer numOfTables, Integer soglia) {
		hasMinimuNumberTablesIfManaged = numOfTables >= soglia;
	}
	
	public void validMaxNumOfTournamentsByMinTournamentsAndMaxPercentage(Integer torneiValevoliPerRanking, Integer soglia) {
		validMaxNumOfTournamentsByMinTournamentsAndMaxPercentage = torneiValevoliPerRanking < soglia;
	}
	
	public void validByMaxNumOfRoundsByTournament(Integer roundCounter, Integer maxNumOfRoundsByTournament) {
		validByMaxNumOfRoundsByTournament = roundCounter < maxNumOfRoundsByTournament;
	}
	
	public void validByMaxNumOfRoundsPlayedEffectively(Integer matchCounter, Integer maxNumOfRoundsPlayedEffectively) {
		validByMaxNumOfRoundsPlayedEffectively = matchCounter < maxNumOfRoundsPlayedEffectively;
	}
	
	public boolean isValid(){
		return hasMinimuNumberTablesIfManaged && validMaxNumOfTournamentsByMinTournamentsAndMaxPercentage && validByMaxNumOfRoundsByTournament && validByMaxNumOfRoundsPlayedEffectively;
	}
}
