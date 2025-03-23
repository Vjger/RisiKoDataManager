package it.desimone.gsheetsaccess.gsheets.dto;

public class SheetRowFactory {

	public enum SheetRowType{
		 AnagraficaGiocatoreRidotta
		,AnagraficaGiocatore
		,Torneo
		,Partita
		,Classifica
		,ReportElaborazione
		,Ranking
		,LastUpdate
		,TorneiSheetId
		,ConfigurationMap
		,BlackList
	}
	
	public static String getSheetName(SheetRowType sheetRowType){
		String result = null;
		switch (sheetRowType) {
		case AnagraficaGiocatoreRidotta:
			result = AnagraficaGiocatoreRidottaRow.SHEET_ANAGRAFICA_NAME;
			break;
		case AnagraficaGiocatore:
			result = AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME;
			break;
		case Torneo:
			result = TorneiRow.SHEET_TORNEI_NAME;
			break;
		case Partita:
			result = PartitaRow.SHEET_PARTITE_NAME;
			break;
		case Classifica:
			result = ClassificheRow.SHEET_CLASSIFICHE;
			break;	
		case ReportElaborazione:
			result = ReportElaborazioneRow.SHEET_NAME;
			break;		
		case Ranking:
			result = RankingRow.SHEET_NAME;
			break;
		case LastUpdate:
			result = LastUpdateRow.SHEET_LAST_UPDATE_NAME;
			break;
		case TorneiSheetId:
			result = TorneiSheetIdRow.SHEET_TORNEI_NAME;
			break;
		case ConfigurationMap:
			result = ConfigurationMapRow.SHEET_CONFIGURAZIONI_NAME;
			break;		
		case BlackList:
			result = BlackListRow.SHEET_BLACKLIST_NAME;
			break;			
		default:
			throw new IllegalArgumentException("Valore non previsto: "+sheetRowType);
		}
		return result;
	}
	
	public static SheetRow create(SheetRowType sheetRowType){
		SheetRow result = null;
		
		switch (sheetRowType) {
		case AnagraficaGiocatoreRidotta:
			result = new AnagraficaGiocatoreRidottaRow();
			break;
		case AnagraficaGiocatore:
			result = new AnagraficaGiocatoreRow();
			break;
		case Torneo:
			result = new TorneiRow();
			break;
		case Partita:
			result = new PartitaRow();
			break;
		case Classifica:
			result = new ClassificheRow();
			break;	
		case ReportElaborazione:
			result = new ReportElaborazioneRow();
			break;		
		case Ranking:
			result = new RankingRow();
			break;
		case LastUpdate:
			result = new LastUpdateRow();
			break;
		case TorneiSheetId:
			result = new TorneiSheetIdRow();
			break;
		case ConfigurationMap:
			result = new ConfigurationMapRow();
			break;	
		case BlackList:
			result = new BlackListRow();
			break;			
		default:
			throw new IllegalArgumentException("Valore non previsto: "+sheetRowType);
		}
		
		return result;
	}
	
}
