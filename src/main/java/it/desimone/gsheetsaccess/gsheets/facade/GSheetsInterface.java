package it.desimone.gsheetsaccess.gsheets.facade;

import it.desimone.gsheetsaccess.googleaccess.GoogleSheetsAccess;
import it.desimone.gsheetsaccess.gsheets.dto.AbstractSheetRow;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRidottaRow;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRow;
import it.desimone.gsheetsaccess.gsheets.dto.ClassificheRow;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRowFactory;
import it.desimone.gsheetsaccess.gsheets.dto.TorneiRow;
import it.desimone.gsheetsaccess.htmlpublisher.LastUpdateData;
import it.desimone.utils.MyLogger;
import it.desimone.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

public class GSheetsInterface {

	private static final String NOT_AVAILABLE = "#N/A";
	private static GoogleSheetsAccess googleSheetAccess;
	
	public static GoogleSheetsAccess getGoogleSheetsInstance(){
		if (googleSheetAccess == null){
			googleSheetAccess = new GoogleSheetsAccess();
		}
		return googleSheetAccess;
	}
	
	public static void main(String[]s){
		for (int i = 1; i <= 30; i++){
			System.out.println(i+": "+toAlphabetic(i));
		}
	}
	
	private static String toAlphabetic(int i) {
	    if( i<0 ) {
	        return "-"+toAlphabetic(-i-1);
	    }

	    int quot = i/26;
	    int rem = i%26;
	    char letter = (char)((int)'A' + rem);
	    if( quot == 0 ) {
	        return ""+letter;
	    } else {
	        return toAlphabetic(quot-1) + letter;
	    }
	}
	
	private static List<String> byKeyColumnsToRanges(String sheetName, List<Integer> keyColumns){
		List<String> ranges = null;
		if (sheetName != null){
			ranges = new ArrayList<String>();
			if (keyColumns!= null && !keyColumns.isEmpty()){
				for (Integer keyColumn: keyColumns){
					String columnLetter = toAlphabetic(keyColumn);
					ranges.add(sheetName+"!"+columnLetter+":"+columnLetter);
				}
			}else{
				ranges.add(sheetName);	
			}
		}
		return ranges;
	}
	
	
	public static List<SheetRow> findSheetRowsByCols(String spreadSheetId, String sheetName, SheetRow sheetRow, Integer... searchCols) throws IOException{
		List<SheetRow> result = null;
			
		List<Integer> rangeCols = Arrays.asList(searchCols);
		rangeCols.add(sheetRow.getSheetRowNumberColPosition());
		List<String> ranges = byKeyColumnsToRanges(sheetName, rangeCols);
		
		List<List<Object>> data = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		List<Object> dataSheetRow = sheetRow.getData();
		int indexRow = 0;
		for (List<Object> row: data){
			indexRow++;
			boolean rowFound = true;
			for (int i=0; i < searchCols.length && rowFound; i++){
				Object elementoRigaRemota = row.get(i);
				Object elementoRigaInCanna = dataSheetRow.get(searchCols[i]);
				rowFound = rowFound && elementoRigaInCanna.toString().trim().equalsIgnoreCase(elementoRigaRemota.toString().trim());
			}
			if (rowFound){
				if (result == null){
					result = new ArrayList<SheetRow>();
				}
				SheetRow sRow;
				try {
					sRow = (SheetRow) sheetRow.clone();
					//sRow.setSheetRowNumber(indexRow);
					sRow.setSheetRowNumber((Integer)row.get(row.size() -1));
					result.add(sRow);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	
	public static Integer findNumTorneoRowByIdTorneo(String spreadSheetId, String sheetName, SheetRow sheetRow) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "findNumTorneoRowByIdTorneo");
		Integer result = null;
		String query = getQueryTorneo(((TorneiRow) sheetRow).getIdTorneo());
		List<Integer> numRows = findNumRowsByIdTorneo(spreadSheetId, sheetRow, query);
		if (numRows != null){
			result = numRows.get(0);
		}
		MyLogger.getLogger().exiting("GSheetsInterface", "findNumTorneoRowByIdTorneo");
		return result;
	}
	
	public static Integer findNumLastUpdateByYear(String spreadSheetId, String sheetName, String year) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "findNumLastUpdateByYear");
		Integer result = null;
		String query = getQueryLastUpdate(year);
		List<Integer> numRows = findNumRowsByYear(spreadSheetId, query);
		if (numRows != null){
			result = numRows.get(0);
		}
		MyLogger.getLogger().exiting("GSheetsInterface", "findNumLastUpdateByYear");
		return result;
	}
	
	public static SheetRow findTorneoRowByIdTorneo(String spreadSheetId, String sheetName, SheetRow sheetRow) throws IOException{
		String query = getQueryTorneo(((TorneiRow) sheetRow).getIdTorneo());
		SheetRow result = findTorneoByIdTorneo(spreadSheetId, sheetRow, query);
		return result;
	}
	
	public static List<Integer> findNumPartiteRowsByIdTorneo(String spreadSheetId, SheetRow sheetRow) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "findNumPartiteRowsByIdTorneo");
		String query = getQueryPartiteTorneo(((PartitaRow) sheetRow).getIdTorneo());
		List<Integer> numRows = findNumRowsByIdTorneo(spreadSheetId, sheetRow, query);
		MyLogger.getLogger().exiting("GSheetsInterface", "findNumPartiteRowsByIdTorneo");
		return numRows;
	}
	
	public static List<Integer> findClassificaRowsByIdTorneo(String spreadSheetId, SheetRow sheetRow) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "findClassificaRowsByIdTorneo");
		String query = getQueryClassificaTorneo(((ClassificheRow) sheetRow).getIdTorneo());
		List<Integer> numRows = findNumRowsByIdTorneo(spreadSheetId, sheetRow, query);
		MyLogger.getLogger().exiting("GSheetsInterface", "findClassificaRowsByIdTorneo");
		return numRows;
	}
	
	
	private static List<Integer> findNumRowsByIdTorneo(String spreadSheetId, SheetRow sheetRow, String query) throws IOException{
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();

		String rangeRicerca = sheetNameDataAnalysis+"!A1:A1";

		List<Object> rigaFormula = Arrays.asList(new Object[]{query});
		values.add(rigaFormula);
		
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		String columnLetterNumRows = toAlphabetic(sheetRow.getSheetRowNumberColPosition());
		List<String> ranges = Collections.singletonList(AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME+"!"+columnLetterNumRows+":"+columnLetterNumRows);
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		List<Integer> numRows = null;
		if (queryResponses != null && !queryResponses.isEmpty()){
			numRows = new ArrayList<Integer>();
			for (List<Object> queryResponse: queryResponses){
				String valueQuery = (String)queryResponse.get(0);
				try{
					Integer numRow = Integer.valueOf(valueQuery);
					numRows.add(numRow);
				}catch(NumberFormatException ne){
					MyLogger.getLogger().info("Not found: "+valueQuery);
				}
			}
		}

		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		return numRows;
	}
	
	private static List<Integer> findNumRowsByYear(String spreadSheetId, String query) throws IOException{
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();

		String rangeRicerca = sheetNameDataAnalysis+"!A1:A1";

		List<Object> rigaFormula = Arrays.asList(new Object[]{query});
		values.add(rigaFormula);
		
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		List<String> ranges = Collections.singletonList(AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME+"!C:C");
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		List<Integer> numRows = null;
		if (queryResponses != null && !queryResponses.isEmpty()){
			numRows = new ArrayList<Integer>();
			for (List<Object> queryResponse: queryResponses){
				String valueQuery = (String)queryResponse.get(0);
				try{
					Integer numRow = Integer.valueOf(valueQuery);
					numRows.add(numRow);
				}catch(NumberFormatException ne){
					MyLogger.getLogger().info("Not found: "+valueQuery);
				}
			}
		}

		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		return numRows;
	}
	
	private static SheetRow findTorneoByIdTorneo(String spreadSheetId, SheetRow sheetRow, String query) throws IOException{
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();

		String rangeRicerca = sheetNameDataAnalysis+"!A1:A1";

		List<Object> rigaFormula = Arrays.asList(new Object[]{query});
		values.add(rigaFormula);
		
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		String columnLetterNumRows = toAlphabetic(sheetRow.getSheetRowNumberColPosition());
		List<String> ranges = Collections.singletonList(AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME+"!A"+":"+columnLetterNumRows);
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		if (queryResponses != null && !queryResponses.isEmpty()){
			sheetRow.setData(queryResponses.get(0));
		}

		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		return sheetRow;
	}
	
	public static List<SheetRow> findClassificaRowsByIdGiocatore(String spreadSheetId, SheetRow sheetRow) throws IOException{
		String query = getQueryClassificaTorneo(((ClassificheRow) sheetRow).getIdGiocatore());
		List<SheetRow> numRows = findClassificheRowsByRow(spreadSheetId, sheetRow, query);
		return numRows;
	}
	
	public static List<SheetRow> findPartiteRowsByIdGiocatore(String spreadSheetId, SheetRow sheetRow) throws IOException{
		String query = getQueryPartiteTorneo(((PartitaRow) sheetRow).getIdGiocatoreVincitore());
		List<SheetRow> numRows = findPartiteRowsByRow(spreadSheetId, sheetRow, query);
		return numRows;
	}
	
	private static List<SheetRow> findClassificheRowsByRow(String spreadSheetId, SheetRow sheetRow, String query) throws IOException{
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();

		String rangeRicerca = sheetNameDataAnalysis+"!A1:A1";

		List<Object> rigaFormula = Arrays.asList(new Object[]{query});
		values.add(rigaFormula);
		
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		String columnLetterNumRows = toAlphabetic(sheetRow.getSheetRowNumberColPosition());
		List<String> ranges = Collections.singletonList(AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME+"!A:"+columnLetterNumRows);
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		List<SheetRow> numRows = null;
		if (queryResponses != null && !queryResponses.isEmpty()){
			numRows = new ArrayList<SheetRow>();
			for (List<Object> queryResponse: queryResponses){
				if (queryResponse != null && !queryResponse.isEmpty() && !queryResponse.get(0).equals(NOT_AVAILABLE)){
					ClassificheRow classificheRow = new ClassificheRow();
					classificheRow.setData(queryResponse);
					numRows.add(classificheRow);
				}
			}
		}

		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		return numRows;
	}
	
	private static List<SheetRow> findPartiteRowsByRow(String spreadSheetId, SheetRow sheetRow, String query) throws IOException{
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();

		String rangeRicerca = sheetNameDataAnalysis+"!A1:A1";

		List<Object> rigaFormula = Arrays.asList(new Object[]{query});
		values.add(rigaFormula);
		
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		String columnLetterNumRows = toAlphabetic(sheetRow.getSheetRowNumberColPosition());
		List<String> ranges = Collections.singletonList(AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME+"!A:"+columnLetterNumRows);
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		List<SheetRow> numRows = null;
		if (queryResponses != null && !queryResponses.isEmpty()){
			numRows = new ArrayList<SheetRow>();
			for (List<Object> queryResponse: queryResponses){
				if (queryResponse != null && !queryResponse.isEmpty() && !queryResponse.get(0).equals(NOT_AVAILABLE)){
					PartitaRow partitaRow = new PartitaRow();
					partitaRow.setData(queryResponse);
					numRows.add(partitaRow);
				}
			}
		}

		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		return numRows;
	}
	
	public static void clearSheet(String spreadSheetId, String sheetName) throws IOException{
		List<ValueRange> data = Collections.singletonList(new ValueRange().setRange(sheetName));
		//getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		
		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(sheetName));
	}
	
	public static SheetRow findSheetRowByKey(String spreadSheetId, String sheetName, SheetRow sheetRow) throws IOException{
		SheetRow result = null;
		
		List<Integer> keyCols = sheetRow.keyCols();
		
		List<Integer> rangeCols = new ArrayList<Integer>(keyCols);
		rangeCols.add(sheetRow.getSheetRowNumberColPosition());
		List<String> ranges = byKeyColumnsToRanges(sheetName, rangeCols);
		
		List<List<Object>> data = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		List<Object> dataSheetRow = sheetRow.getData();
		int indexRow = 0;
		for (List<Object> row: data){
			indexRow++;
			boolean rowFound = true;
			for (int i=0; i < keyCols.size() && rowFound; i++){
				Object elementoRigaRemota = row.get(i);
				Object elementoRigaInCanna = dataSheetRow.get(keyCols.get(i));
				rowFound = rowFound && elementoRigaInCanna.toString().trim().equalsIgnoreCase(elementoRigaRemota.toString().trim());
			}
			if (rowFound){
				//sheetRow.setSheetRowNumber(indexRow);
				sheetRow.setSheetRowNumber(Integer.valueOf((String)row.get(row.size() -1)));
				result = sheetRow;
				break;
			}
		}

		return result;
	}
	
	public static List<AnagraficaGiocatoreRidottaRow> findAnagraficheRidotteByKey(String spreadSheetId, List<AnagraficaGiocatoreRidottaRow> sheetRows) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "findAnagraficheRidotteByKey");
		
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME;
    	
//		List<List<Object>> values = new ArrayList<List<Object>>();
    	int indexStartingRow = 8;
//    	int numeroRiga = indexStartingRow;
		String rangeRicerca = sheetNameDataAnalysis+"!A"+indexStartingRow+":D"+(indexStartingRow+sheetRows.size()-1);
//    	for (AnagraficaGiocatoreRidottaRow sheetRow: sheetRows){
//			//List<Object> rigaRicerca = Arrays.asList(new Object[]{sheetRow.getNome().trim(), sheetRow.getCognome().trim(), sheetRow.getEmail().trim(), getQueryAnagraficaRidotta(numeroRiga)});
//			List<Object> rigaRicerca = Arrays.asList(new Object[]{sheetRow.getNome().trim(), sheetRow.getCognome().trim(), sheetRow.getDataDiNascita().trim(), getQueryAnagraficaRidotta(numeroRiga)});
//			values.add(rigaRicerca);
//			numeroRiga++;
//    	}
//		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
//    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		
    	//Parte anagrafica
		List<List<Object>> valuesA = new ArrayList<List<Object>>();
		String rangeRicercaA = sheetNameDataAnalysis+"!A"+indexStartingRow+":C"+(indexStartingRow+sheetRows.size()-1);
    	for (AnagraficaGiocatoreRidottaRow sheetRow: sheetRows){
			//List<Object> rigaRicerca = Arrays.asList(new Object[]{sheetRow.getNome().trim(), sheetRow.getCognome().trim(), sheetRow.getEmail().trim(), getQueryAnagraficaRidotta(numeroRiga)});
			List<Object> rigaRicerca = Arrays.asList(new Object[]{sheetRow.getNome().trim(), sheetRow.getCognome().trim(), sheetRow.getDataDiNascita().trim()});
			valuesA.add(rigaRicerca);
    	}
		data.add(new ValueRange().setRange(rangeRicercaA).setValues(valuesA));
    	Integer updatedRowsA = getGoogleSheetsInstance().updateRows(spreadSheetId, data, false);
    	
    	//Parte formule
		List<List<Object>> valuesF = new ArrayList<List<Object>>();
    	int numeroRiga = indexStartingRow;
		String rangeRicercaF = sheetNameDataAnalysis+"!D"+indexStartingRow+":D"+(indexStartingRow+sheetRows.size()-1);
    	for (AnagraficaGiocatoreRidottaRow sheetRow: sheetRows){
			//List<Object> rigaRicerca = Arrays.asList(new Object[]{sheetRow.getNome().trim(), sheetRow.getCognome().trim(), sheetRow.getEmail().trim(), getQueryAnagraficaRidotta(numeroRiga)});
			List<Object> rigaRicerca = Arrays.asList(new Object[]{getQueryAnagraficaRidotta(numeroRiga)});
			valuesF.add(rigaRicerca);
			numeroRiga++;
    	}
    	data.clear();
		data.add(new ValueRange().setRange(rangeRicercaF).setValues(valuesF));
    	Integer updatedRowsF = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
    	
    	String range = AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME+"!"+"D"+indexStartingRow+":D"+(indexStartingRow+sheetRows.size()-1);
		List<String> ranges = Collections.singletonList(range);
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		if (queryResponses != null && !queryResponses.isEmpty()){
			int index = 0;
			for (List<Object> queryResponse: queryResponses){
				String valueQuery = (String)queryResponse.get(0);
				try{
					Integer idAnagrafica = Integer.valueOf(valueQuery);
					sheetRows.get(index).setId(idAnagrafica);
				}catch(NumberFormatException ne){
					MyLogger.getLogger().fine("Not found: "+valueQuery);
				}
				index++;
			}
		}
		
		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		MyLogger.getLogger().exiting("GSheetsInterface", "findAnagraficheRidotteByKey");
		return sheetRows;
	}
	
	public static List<AnagraficaGiocatoreRidottaRow> findAnagraficheRidotteByKeyOrIdNazionale(String spreadSheetId, List<AnagraficaGiocatoreRidottaRow> sheetRows) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "findAnagraficheRidotteByKeyOrIdNazionale");
		
		List<AnagraficaGiocatoreRidottaRow> result = new ArrayList<AnagraficaGiocatoreRidottaRow>(sheetRows.size());
		
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME;
    	
    	int indexStartingRow = 8;
		String rangeRicerca = sheetNameDataAnalysis+"!A"+indexStartingRow+":D"+(indexStartingRow+sheetRows.size()-1);
		
    	//Parte anagrafica
		List<List<Object>> valuesA = new ArrayList<List<Object>>();
		String rangeRicercaA = sheetNameDataAnalysis+"!A"+indexStartingRow+":C"+(indexStartingRow+sheetRows.size()-1);
    	for (AnagraficaGiocatoreRidottaRow sheetRow: sheetRows){
    		List<Object> rigaRicerca = null;
    		if (sheetRow.getId() != null && StringUtils.isNullOrEmpty(sheetRow.getDataDiNascita())){ //Se viene comunque impostata la data di nascita essa prevale sull'indicazione dell'ID
    			rigaRicerca = Arrays.asList(new Object[]{sheetRow.getId()!=0?sheetRow.getId():""+sheetRow.getId()}); //La FILTER funziona sullo zero solo se Stringa
    		}else{
    			rigaRicerca = Arrays.asList(new Object[]{sheetRow.getNome().trim(), sheetRow.getCognome().trim(), sheetRow.getDataDiNascita().trim()});
    		}
			valuesA.add(rigaRicerca);
		}
		data.add(new ValueRange().setRange(rangeRicercaA).setValues(valuesA));
    	Integer updatedRowsA = getGoogleSheetsInstance().updateRows(spreadSheetId, data, false);
    	
    	//Parte formule
		List<List<Object>> valuesF = new ArrayList<List<Object>>();
    	int numeroRiga = indexStartingRow;
		String rangeRicercaF = sheetNameDataAnalysis+"!D"+indexStartingRow+":D"+(indexStartingRow+sheetRows.size()-1);
    	for (AnagraficaGiocatoreRidottaRow sheetRow: sheetRows){
    		List<Object> rigaRicerca = null;
    		if (sheetRow.getId() != null && StringUtils.isNullOrEmpty(sheetRow.getDataDiNascita())){ //Se viene comunque impostata la data di nascita essa prevale sull'indicazione dell'ID
    			rigaRicerca = Arrays.asList(new Object[]{getQueryAnagraficaRidottaByID(numeroRiga)});
    		}else{
    			rigaRicerca = Arrays.asList(new Object[]{getQueryAnagraficaRidotta(numeroRiga)});
    		}
			valuesF.add(rigaRicerca);
			numeroRiga++;
    	}
    	data.clear();
		data.add(new ValueRange().setRange(rangeRicercaF).setValues(valuesF));
    	Integer updatedRowsF = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
    	
    	String range = AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME+"!"+"D"+indexStartingRow+":F"+(indexStartingRow+sheetRows.size()-1);
		List<String> ranges = Collections.singletonList(range);
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		if (queryResponses != null && !queryResponses.isEmpty()){
			int index = 0;
			for (List<Object> queryResponse: queryResponses){
				AnagraficaGiocatoreRidottaRow anagraficaGiocatoreRidottaRowResult = new AnagraficaGiocatoreRidottaRow();
				if (queryResponse != null){
					if (queryResponse.size() >=1){
						String idQuery = (String)queryResponse.get(0);
						try{
							Integer idAnagrafica = Integer.valueOf(idQuery);
							anagraficaGiocatoreRidottaRowResult.setId(idAnagrafica);
						}catch(NumberFormatException ne){
							MyLogger.getLogger().fine("Not found: "+idQuery);
						}
					}
					if (queryResponse.size() >=3){
						String nomeResponse = (String)queryResponse.get(1);
						String cognomeResponse = (String)queryResponse.get(2);
						try{
							anagraficaGiocatoreRidottaRowResult.setNome(nomeResponse);
							anagraficaGiocatoreRidottaRowResult.setCognome(cognomeResponse);
						}catch(Exception ne){
							MyLogger.getLogger().fine("Not found: "+ne.getMessage());
						}
					}
				}
				result.add(anagraficaGiocatoreRidottaRowResult);
				index++;
			}
		}
		
		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		MyLogger.getLogger().exiting("GSheetsInterface", "findAnagraficheRidotteByKeyOrIdNazionale");
		return result;
	}
	
	public static List<AnagraficaGiocatoreRidottaRow> findAnagraficheRidotteById(String spreadSheetId, List<AnagraficaGiocatoreRidottaRow> sheetRows) throws IOException{
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();
    	int indexStartingRow = 8;
    	int numeroRiga = indexStartingRow;
		String rangeRicerca = sheetNameDataAnalysis+"!A"+indexStartingRow+":B"+(indexStartingRow+sheetRows.size()-1);
    	for (AnagraficaGiocatoreRidottaRow sheetRow: sheetRows){
			List<Object> rigaRicerca = Arrays.asList(new Object[]{sheetRow.getId(), getQueryAnagraficaRidotta(numeroRiga)});
			values.add(rigaRicerca);
			numeroRiga++;
    	}
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		
    	String range = AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME+"!"+"G"+indexStartingRow+":G"+(indexStartingRow+sheetRows.size()-1);
		List<String> ranges = Collections.singletonList(range);
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		if (queryResponses != null && !queryResponses.isEmpty()){
			int index = 0;
			for (List<Object> queryResponse: queryResponses){
				String valueQuery = (String)queryResponse.get(0);
				try{
					Integer numRow = Integer.valueOf(valueQuery);
					sheetRows.get(index).setSheetRowNumber(numRow);
				}catch(NumberFormatException ne){
					MyLogger.getLogger().info("Not found: "+valueQuery);
				}
				index++;
			}
		}
		
		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		
		return sheetRows;
	}
	
	
	public static List<AnagraficaGiocatoreRidottaRow> findAnagraficheRidotteById2(String spreadSheetId, List<AnagraficaGiocatoreRidottaRow> sheetRows) throws IOException{
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();
    	int indexStartingRow = 8;
    	int numeroRiga = indexStartingRow;
		String rangeRicerca = sheetNameDataAnalysis+"!A"+indexStartingRow+":B"+(indexStartingRow+sheetRows.size()-1);
    	for (AnagraficaGiocatoreRidottaRow sheetRow: sheetRows){
			List<Object> rigaRicerca = Arrays.asList(new Object[]{sheetRow.getId(), getQueryAnagraficaRidotta2(numeroRiga)});
			values.add(rigaRicerca);
			numeroRiga++;
    	}
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		
    	String range = AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME+"!"+"B"+indexStartingRow+":G"+(indexStartingRow+sheetRows.size()-1);
		List<String> ranges = Collections.singletonList(range);
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		if (queryResponses != null && !queryResponses.isEmpty()){
			int index = 0;
			for (List<Object> queryResponse: queryResponses){
				
//				String valueQuery = (String)queryResponse.get(0);
//				try{
//					Integer id = Integer.valueOf(valueQuery);
//					sheetRows.get(index).setId(id);
//				}catch(NumberFormatException ne){
//					MyLogger.getLogger().info("Not found: "+valueQuery);
//				}
//				valueQuery = (String)queryResponse.get(1);
//				sheetRows.get(index).setNome(valueQuery);
//
//				valueQuery = (String)queryResponse.get(2);
//				sheetRows.get(index).setCognome(valueQuery);
//				
//				valueQuery = (String)queryResponse.get(3);
//				sheetRows.get(index).setDataDiNascita(valueQuery);
//				
//				valueQuery = (String)queryResponse.get(4);
//				sheetRows.get(index).setUpdateTime(valueQuery);
//				
//				valueQuery = (String)queryResponse.get(5);
//				try{
//					Integer numRow = Integer.valueOf(valueQuery);
//					sheetRows.get(index).setSheetRowNumber(numRow);
//				}catch(NumberFormatException ne){
//					MyLogger.getLogger().info("Not found: "+valueQuery);
//				}
				if (queryResponse != null && !queryResponse.isEmpty() && !queryResponse.get(0).equals(NOT_AVAILABLE)){
					SheetRow row = sheetRows.get(index);
					row.setData(queryResponse);
				}
				index++;
			}
		}
		
		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		
		return sheetRows;
	}
	
	public static List<SheetRow> findAnagraficheByKey(String spreadSheetId, List<SheetRow> sheetRows) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "findAnagraficheByKey");
		
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();
    	int indexStartingRow = 1;
    	int numeroRiga = indexStartingRow;
		String rangeRicerca = sheetNameDataAnalysis+"!A"+indexStartingRow+":B"+(indexStartingRow+sheetRows.size()-1);
    	for (SheetRow sheetRow: sheetRows){
			List<Object> rigaRicerca = Arrays.asList(new Object[]{((AnagraficaGiocatoreRow) sheetRow).getId(), getQueryAnagrafica(numeroRiga)});
			values.add(rigaRicerca);
			numeroRiga++;
    	}
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		String columnLetterNumRows = toAlphabetic(sheetRows.get(0).getSheetRowNumberColPosition()+1);
		List<String> ranges = Collections.singletonList(AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME+"!"+columnLetterNumRows+indexStartingRow+":"+columnLetterNumRows+(indexStartingRow+sheetRows.size()-1));
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		if (queryResponses != null && !queryResponses.isEmpty()){
			int index = 0;
			for (List<Object> queryResponse: queryResponses){
				if (queryResponse != null && !queryResponse.isEmpty()){
					Object o = queryResponse.get(0);
					if (o != null){
						String valueQuery = (String)o;
						try{
							Integer numRow = Integer.valueOf(valueQuery);
							sheetRows.get(index).setSheetRowNumber(numRow);
						}catch(NumberFormatException ne){
							MyLogger.getLogger().info("Not found: "+valueQuery);
						}
					}
				}
				index++;
			}
		}

		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		MyLogger.getLogger().exiting("GSheetsInterface", "findAnagraficheByKey");
		return sheetRows;
	}
	
	public static List<SheetRow> leggiAnagraficheByKey(String spreadSheetId, List<SheetRow> sheetRows) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "leggiAnagraficheByKey");
		
    	List<ValueRange> data = new ArrayList<ValueRange>();

    	String sheetNameDataAnalysis = AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME;
    	
		List<List<Object>> values = new ArrayList<List<Object>>();
    	int indexStartingRow = 1;
    	int numeroRiga = indexStartingRow;
		String rangeRicerca = sheetNameDataAnalysis+"!A"+indexStartingRow+":B"+(indexStartingRow+sheetRows.size()-1);
    	for (SheetRow sheetRow: sheetRows){
			List<Object> rigaRicerca = Arrays.asList(new Object[]{((AnagraficaGiocatoreRow) sheetRow).getId(), getQueryAnagrafica(numeroRiga)});
			values.add(rigaRicerca);
			numeroRiga++;
    	}
		data.add(new ValueRange().setRange(rangeRicerca).setValues(values));
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadSheetId, data, true);
		String columnLetterNumRows = toAlphabetic(sheetRows.get(0).getSheetRowNumberColPosition()+1);
		List<String> ranges = Collections.singletonList(AbstractSheetRow.SHEET_DATA_ANALYSIS_NAME+"!"+"B"+indexStartingRow+":"+columnLetterNumRows+(indexStartingRow+sheetRows.size()-1));
		
		List<List<Object>> queryResponses = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		if (queryResponses != null && !queryResponses.isEmpty()){
			int index = 0;
			for (List<Object> queryResponse: queryResponses){
				if (queryResponse != null && !queryResponse.isEmpty() && !queryResponse.get(0).equals(NOT_AVAILABLE)){
					Object o1 = queryResponse.get(1);
					if (o1 != null){
						String valueQuery = (String)o1;
						((AnagraficaGiocatoreRow) sheetRows.get(index)).setNome(valueQuery);
					}
					Object o2 = queryResponse.get(2);
					if (o2 != null){
						String valueQuery = (String)o2;
						((AnagraficaGiocatoreRow) sheetRows.get(index)).setCognome(valueQuery);
					}
					Object o3 = queryResponse.get(3);
					if (o3 != null){
						String valueQuery = (String)o3;
						((AnagraficaGiocatoreRow) sheetRows.get(index)).setUltimoClub(valueQuery);
					}
					Object o4 = queryResponse.get(4);
					if (o4 != null){
						String valueQuery = (String)o4;
						((AnagraficaGiocatoreRow) sheetRows.get(index)).setIdUltimoTorneo(valueQuery);
					}
					Object o5 = queryResponse.get(5);
					if (o5 != null){
						String valueQuery = (String)o5;
						((AnagraficaGiocatoreRow) sheetRows.get(index)).setUpdateTime(valueQuery);
					}
					Object o6 = queryResponse.get(6);
					if (o6 != null){
						String valueQuery = (String)o6;
						try{
							Integer numRow = Integer.valueOf(valueQuery);
							sheetRows.get(index).setSheetRowNumber(numRow);
						}catch(NumberFormatException ne){
							MyLogger.getLogger().info("Not found: "+valueQuery);
						}
					}
				}
				index++;
			}
		}

		getGoogleSheetsInstance().clearRows(spreadSheetId, Collections.singletonList(rangeRicerca));
		MyLogger.getLogger().exiting("GSheetsInterface", "leggiAnagraficheByKey");
		return sheetRows;
	}
	
	
	private static String getQueryTorneo(String idTorneo){
		StringBuilder buffer = new StringBuilder();
		buffer.append("=FILTER(");
		buffer.append(TorneiRow.SHEET_TORNEI_NAME + "!A2:");
		buffer.append(toAlphabetic(new TorneiRow().getDataSize() -1)+";");
		buffer.append(TorneiRow.SHEET_TORNEI_NAME + "!A2:A = ");
		buffer.append("\""+idTorneo+"\"");
		buffer.append(")");
		return buffer.toString();
	}
	
	private static String getQueryClassificaTorneo(String idTorneo){
		StringBuilder buffer = new StringBuilder();
		buffer.append("=FILTER(");
		buffer.append(ClassificheRow.SHEET_CLASSIFICHE + "!A2:");
		buffer.append(toAlphabetic(new ClassificheRow().getDataSize() -1)+";");
		buffer.append(ClassificheRow.SHEET_CLASSIFICHE + "!A2:A = ");
		buffer.append("\""+idTorneo+"\"");
		buffer.append(")");
		return buffer.toString();
	}
	
	private static String getQueryClassificaTorneo(Integer idGiocatore){
		StringBuilder buffer = new StringBuilder();
		buffer.append("=FILTER(");
		buffer.append(ClassificheRow.SHEET_CLASSIFICHE + "!A2:");
		buffer.append(toAlphabetic(new ClassificheRow().getDataSize() -1)+";");
		buffer.append(ClassificheRow.SHEET_CLASSIFICHE + "!B2:B = ");
		buffer.append(idGiocatore);
		buffer.append(")");
		return buffer.toString();
	}
	
	private static String getQueryPartiteTorneo(Integer idGiocatore){
		StringBuilder buffer = new StringBuilder();
		buffer.append("=FILTER(");
		buffer.append(PartitaRow.SHEET_PARTITE_NAME + "!A2:");
		buffer.append(toAlphabetic(new PartitaRow().getDataSize() -1)+";");
		buffer.append("(");
		buffer.append(PartitaRow.SHEET_PARTITE_NAME + "!G2:G = ");
		buffer.append(idGiocatore);
		buffer.append(")+(");
		buffer.append(PartitaRow.SHEET_PARTITE_NAME + "!J2:J = ");
		buffer.append(idGiocatore);
		buffer.append(")+(");
		buffer.append(PartitaRow.SHEET_PARTITE_NAME + "!M2:M = ");
		buffer.append(idGiocatore);
		buffer.append(")+(");
		buffer.append(PartitaRow.SHEET_PARTITE_NAME + "!P2:P = ");
		buffer.append(idGiocatore);
		buffer.append(")+(");
		buffer.append(PartitaRow.SHEET_PARTITE_NAME + "!S2:S = ");
		buffer.append(idGiocatore);
		buffer.append(")");
		buffer.append(")");
		return buffer.toString();
	}
	
	private static String getQueryPartiteTorneo(String idTorneo){
		//String query = "=QUERY("+AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME+"!A2:G;ʺSELECT G WHERE A = 'ʺ&A"+numeroRiga+"&ʺ'ʺ; -1)";
		String query = "=FILTER("+PartitaRow.SHEET_PARTITE_NAME+"!A2:V; "+PartitaRow.SHEET_PARTITE_NAME+"!A2:A = \""+idTorneo+"\")";
		return query;
	}
		
	private static String getQueryAnagrafica(Integer numeroRiga){
		//String query = "=QUERY("+AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME+"!A2:G;ʺSELECT G WHERE A = 'ʺ&A"+numeroRiga+"&ʺ'ʺ; -1)";
		String query = "=FILTER("+AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME+"!A2:G; "+AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME+"!A2:A = A"+numeroRiga+")";
		return query;
	}
	
	private static String getQueryAnagraficaRidotta(Integer numeroRiga){
		//String query = "=QUERY(ANAGRAFICA!A2:E;ʺSELECT A WHERE upper(B) = upper('ʺ&A"+numeroRiga+"&ʺ') AND upper(C) = upper('ʺ&B"+numeroRiga+"&ʺ') AND upper(D) = upper('ʺ&C"+numeroRiga+"&ʺ')ʺ; -1)";
		return "=FILTER(ANAGRAFICA!A2:F; upper(ANAGRAFICA!B2:B) = upper(A"+numeroRiga+"); upper(ANAGRAFICA!C2:C) = upper(B"+numeroRiga+"); upper(ANAGRAFICA!D2:D) = upper(C"+numeroRiga+"))";
	}
	
	private static String getQueryAnagraficaRidottaByID(Integer numeroRiga){
		String query = "=FILTER("+AnagraficaGiocatoreRidottaRow.SHEET_ANAGRAFICA_NAME+"!A2:F; "+AnagraficaGiocatoreRidottaRow.SHEET_ANAGRAFICA_NAME+"!A2:A = A"+numeroRiga+")";
		return query;
	}

	private static String getQueryAnagraficaRidotta2(Integer numeroRiga){
		//String query = "=QUERY(ANAGRAFICA!A2:E;ʺSELECT A WHERE upper(B) = upper('ʺ&A"+numeroRiga+"&ʺ') AND upper(C) = upper('ʺ&B"+numeroRiga+"&ʺ') AND upper(D) = upper('ʺ&C"+numeroRiga+"&ʺ')ʺ; -1)";
		return "=FILTER(ANAGRAFICA!A2:F; upper(ANAGRAFICA!A2:A) = upper(A"+numeroRiga+"))";
	}
	
	private static String getQueryLastUpdate(String year){
		StringBuilder buffer = new StringBuilder();
		buffer.append("=FILTER(");
		buffer.append(LastUpdateData.LAST_UPDATE_SHEET + "!A2:B");
		buffer.append(LastUpdateData.LAST_UPDATE_SHEET + "!A2:A = ");
		buffer.append("\""+year+"\"");
		buffer.append(")");
		return buffer.toString();
	}
	
	public static SheetRow findSheetRowByLineNumber(String spreadSheetId, String sheetName, SheetRow sheetRow) throws IOException{
		SheetRow result = null;
	
		List<String> ranges = Collections.singletonList(sheetName+"!"+sheetRow.getSheetRowNumber()+":"+sheetRow.getSheetRowNumber());
		
		List<List<Object>> data = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);
		
		if (data != null && !data.isEmpty()){
			List<Object> sheetRowData = data.get(0);
			sheetRow.setData(sheetRowData);
			result = sheetRow;
		}

		return result;
	}
	
	public static void deleteRow(String spreadSheetId, String sheetName, SheetRow sheetRow) throws IOException{
		getGoogleSheetsInstance().deleteRow(spreadSheetId, sheetName, sheetRow.getSheetRowNumber());
	}
	
	public static void deleteRows(String spreadSheetId, String sheetName, List<SheetRow> sheetRows) throws IOException{
		List<Integer> numRows = new ArrayList<Integer>();
		for (SheetRow row: sheetRows){
			numRows.add(row.getSheetRowNumber());
		}
		getGoogleSheetsInstance().deleteRows(spreadSheetId, sheetName, numRows);
	}
	
	public static void deleteRowsByNumRow(String spreadSheetId, String sheetName, List<Integer> sheetRows) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "deleteRowsByNumRow");
		getGoogleSheetsInstance().deleteRows(spreadSheetId, sheetName, sheetRows);
		MyLogger.getLogger().exiting("GSheetsInterface", "deleteRowsByNumRow");
	}
	
	public static void appendRows(String spreadSheetId, String sheetName, List<SheetRow> sheetRows) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "appendRows");
		if (sheetRows != null && !sheetRows.isEmpty()){
			List<List<Object>> rowData = new ArrayList<List<Object>>();
			int index = 0;
			for (SheetRow row: sheetRows){
				try{
					rowData.add(row.getData());
					index++;
				}catch(ArrayIndexOutOfBoundsException aioe){
					MyLogger.getLogger().severe("indice: "+index);
					throw aioe;
				}
			}
			getGoogleSheetsInstance().appendDataToSheet(spreadSheetId, sheetName, rowData);
		}
		MyLogger.getLogger().exiting("GSheetsInterface", "appendRows");
	}
	
	public static Integer findMaxIdAnagrafica(String spreadSheetId) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "findMaxIdAnagrafica");
		List<String> ranges = Collections.singletonList(AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME+"!"+"B2");
		
		List<List<Object>> data = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);

		MyLogger.getLogger().exiting("GSheetsInterface", "findMaxIdAnagrafica");
		return (Integer) Integer.valueOf((String)data.get(0).get(0));
	}
	
	public static Integer findIdAnagraficaVerificato(String spreadSheetId) throws IOException{
		Integer result = null;
		List<String> ranges = Collections.singletonList(AnagraficaGiocatoreRidottaRow.SHEET_DATA_ANALYSIS_NAME+"!"+"B5");
		
		List<List<Object>> data = getGoogleSheetsInstance().leggiSheet(spreadSheetId, ranges);

		if (data != null && !data.isEmpty() && data.get(0) != null && !data.get(0).isEmpty()){
			String value = (String)data.get(0).get(0);
			try{
				result = Integer.valueOf((String)data.get(0).get(0));
			}catch(NumberFormatException ne){
				MyLogger.getLogger().info("Not found: "+value);
			}
		}
		return result;
	}
	
    public static Integer updateRows(String spreadsheetId, String sheetName, List<SheetRow> rows, boolean userEntered) throws IOException{
		MyLogger.getLogger().entering("GSheetsInterface", "updateRows");
    	if (rows == null || rows.isEmpty()) return null;
    	
    	List<ValueRange> data = new ArrayList<ValueRange>();
    	for (SheetRow row: rows){
    		String range = sheetName+"!"+row.getSheetRowNumber()+":"+row.getSheetRowNumber();
    		List<List<Object>> values = new ArrayList<List<Object>>();
    		values.add(row.getData());
    		data.add(new ValueRange().setRange(range).setValues(values));
    	}
    	
    	Integer updatedRows = getGoogleSheetsInstance().updateRows(spreadsheetId, data, userEntered);
		MyLogger.getLogger().exiting("GSheetsInterface", "updateRows");
    	return updatedRows;
    }
	
    
	public static <T> List<T> getAllRows(String spreadSheetId, SheetRowFactory.SheetRowType sheetRowType) throws IOException{
		List<T> result = null;
		GoogleSheetsAccess googleSheetsAccess = getGoogleSheetsInstance();

		String sheetName = SheetRowFactory.getSheetName(sheetRowType);
		List<String> ranges = Collections.singletonList(sheetName+"!A2:"+toAlphabetic(SheetRowFactory.create(sheetRowType).getDataSize()));

		List<List<Object>> sheetRows = googleSheetsAccess.leggiSheet(spreadSheetId, ranges);

		if (sheetRows != null && !sheetRows.isEmpty()){
			result = new ArrayList<T>();
			for (List<Object> sheetRow: sheetRows){
				SheetRow row = SheetRowFactory.create(sheetRowType);
				row.setData(sheetRow);
				
				result.add((T) row);
			}
		}
		return result;
	}

}
