package it.desimone.gsheetsaccess.googleaccess;

import it.desimone.utils.MyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchClearValuesRequest;
import com.google.api.services.sheets.v4.model.BatchClearValuesResponse;
import com.google.api.services.sheets.v4.model.BatchGetValuesByDataFilterRequest;
import com.google.api.services.sheets.v4.model.BatchGetValuesByDataFilterResponse;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.DataFilter;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DeleteRangeRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.MatchedValueRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GoogleSheetsAccess extends RisikoDataManagerAccess{

	private static final String USER_ENTERED = "USER_ENTERED";
	private static final String RAW = "RAW";
    
    public List<Sheet> elencoSheets(String spreadsheetId) throws IOException{
        
        Sheets service = getSheetsService();
        
        Sheets.Spreadsheets.Get spreadSheetsGet = service.spreadsheets().get(spreadsheetId);
        
        spreadSheetsGet = spreadSheetsGet.setIncludeGridData(false);
        
	    Spreadsheet response1= spreadSheetsGet.execute();
	    
	    List<Sheet> sheets = response1.getSheets();
	
	    for (Sheet sheet: sheets){
	    	MyLogger.getLogger().info(sheet.getProperties().toPrettyString());
	    }
	    
	    return sheets;
    }
    
    
    public List<List<Object>> leggiSheet(String spreadsheetId, String sheetName) throws IOException{
        
        Sheets service = getSheetsService();

        Sheets.Spreadsheets.Values.Get spreadSheetsValuesGet = service.spreadsheets().values().get(spreadsheetId, sheetName);
        
        ValueRange response = spreadSheetsValuesGet.execute();
        return response.getValues();
    }
    
    public List<List<Object>> leggiSheet(String spreadsheetId, List<String> ranges) throws IOException{
        
    	MyLogger.getLogger().fine(ranges.toString());
    	
        Sheets service = getSheetsService();

        Sheets.Spreadsheets.Values.BatchGet spreadSheetsValuesBatchGet = service.spreadsheets().values().batchGet(spreadsheetId).setRanges(ranges);
        
        BatchGetValuesResponse response = spreadSheetsValuesBatchGet.execute();
        List<ValueRange> valueRanges = response.getValueRanges();
        
    	MyLogger.getLogger().fine(valueRanges.toString());
        
        List<List<Object>> result = null;
        if (valueRanges != null && !valueRanges.isEmpty()){
	        result = new ArrayList<List<Object>>();
	        
	        if (valueRanges.get(0).getValues() != null){
		        for (int i = 0; i < valueRanges.get(0).getValues().size(); i++){
		        	
		        	List<Object> row = new ArrayList<Object>();
			        for (ValueRange range: valueRanges){	        	
			        	row.addAll(range.getValues().get(i));
			        }
			        result.add(row);
		        }
	        }
        }
        return result;
    }
    
    public List<List<Object>> findRow(String spreadsheetId, List<String> ranges) throws IOException{
        
        Sheets service = getSheetsService();

        Sheets.Spreadsheets.Values spreadSheetValues = service.spreadsheets().values();
        
        BatchGetValuesByDataFilterRequest batchGetValuesByDataFilterRequest = new BatchGetValuesByDataFilterRequest();

        List<DataFilter> dataFilters = new ArrayList<DataFilter>();
        for (String range: ranges){
            DataFilter dataFilter = new DataFilter();
            dataFilter.setA1Range(range);
        	dataFilters.add(dataFilter);
        }
        batchGetValuesByDataFilterRequest.setDataFilters(dataFilters);
        Sheets.Spreadsheets.Values.BatchGetByDataFilter batchGetByDataFilter = spreadSheetValues.batchGetByDataFilter(spreadsheetId, batchGetValuesByDataFilterRequest);
               
        BatchGetValuesByDataFilterResponse response = batchGetByDataFilter.execute();
        List<MatchedValueRange> matchedValueRanges = response.getValueRanges();
        
        List<List<Object>> result = null;
        if (matchedValueRanges != null && !matchedValueRanges.isEmpty()){
	        result = new ArrayList<List<Object>>();
        	for (MatchedValueRange matchedValueRange: matchedValueRanges){

			        	List<Object> row = new ArrayList<Object>();
       	
				        	row.addAll(matchedValueRange.getValueRange().getValues());

				        result.add(row);
			        }
        	}
        return result;
    }
    
    public List<List<Object>> findData(String spreadsheetId, Integer sheetId, Integer firstColumn, Integer lastColumn, Integer firstRow, Integer lastRow) throws IOException{
        
        Sheets service = getSheetsService();

        Sheets.Spreadsheets.Values spreadSheetValues = service.spreadsheets().values();
        
        BatchGetValuesByDataFilterRequest batchGetValuesByDataFilterRequest = new BatchGetValuesByDataFilterRequest();
        DataFilter dataFilter = new DataFilter();
        GridRange gridRange = new GridRange();
        gridRange.setSheetId(sheetId);
        if (firstColumn != null)
        	gridRange.setStartColumnIndex(firstColumn-1);
        if (lastColumn != null)
        	gridRange.setEndColumnIndex(lastColumn);
        if (firstRow != null)
        	gridRange.setStartRowIndex(firstRow-1);
        if (lastRow != null)
        	gridRange.setEndRowIndex(lastRow);
        dataFilter.setGridRange(gridRange);
        List<DataFilter> dataFilters = new ArrayList<DataFilter>();
        dataFilters.add(dataFilter);
        batchGetValuesByDataFilterRequest.setDataFilters(dataFilters);
        Sheets.Spreadsheets.Values.BatchGetByDataFilter batchGetByDataFilter = spreadSheetValues.batchGetByDataFilter(spreadsheetId, batchGetValuesByDataFilterRequest);
               
        BatchGetValuesByDataFilterResponse response = batchGetByDataFilter.execute();
        List<MatchedValueRange> matchedValueRanges = response.getValueRanges();
        
        List<List<Object>> result = null;
        if (matchedValueRanges != null && !matchedValueRanges.isEmpty()){
	        result = new ArrayList<List<Object>>();
        	for (MatchedValueRange matchedValueRange: matchedValueRanges){

			        	List<Object> row = new ArrayList<Object>();
       	
				        	row.addAll(matchedValueRange.getValueRange().getValues());

				        result.add(row);
			        }
        	}
        return result;
    }
    
    public void appendDataToSheet(String spreadsheetId, String sheetName, List<List<Object>> data) throws IOException{
    	
    	MyLogger.getLogger().fine(data.toString());
        
        Sheets service = getSheetsService();

        ValueRange valueRange = new ValueRange();
        valueRange.setRange(sheetName);
        valueRange.setValues(data);
        
        Sheets.Spreadsheets.Values.Append spreadSheetsValuesAppend = service.spreadsheets().values().append(spreadsheetId, sheetName, valueRange);
        
        spreadSheetsValuesAppend = spreadSheetsValuesAppend.setValueInputOption(USER_ENTERED);

        AppendValuesResponse response = spreadSheetsValuesAppend.execute();
        
        MyLogger.getLogger().fine(response.getUpdates().toPrettyString());

    }
    
    
    public void deleteRow(String spreadsheetId, String sheetName, Integer numRow) throws IOException{
    	Integer sheetId = getSheetIdBySheetName(spreadsheetId, sheetName);
    	
    	if (sheetId != null){
    		deleteRow(spreadsheetId, sheetId, numRow);
    	}
    }
    
    public void deleteRows(String spreadsheetId, String sheetName, List<Integer> numRows) throws IOException{
    	Integer sheetId = getSheetIdBySheetName(spreadsheetId, sheetName);
    	
    	if (sheetId != null){
    		deleteRows(spreadsheetId, sheetId, numRows);
    	}
    }
    
    private Integer getSheetIdBySheetName(String spreadsheetId, String sheetName)  throws IOException{
    	Integer sheetId = null;
    	
        Sheets service = getSheetsService();
        
        Sheets.Spreadsheets.Get spreadSheetsGet = service.spreadsheets().get(spreadsheetId);
        
        spreadSheetsGet = spreadSheetsGet.setRanges(Collections.singletonList(sheetName));
        
	    Spreadsheet response = spreadSheetsGet.execute();
	    
	    List<Sheet> sheets = response.getSheets();
	    
	    if (sheets != null && !sheets.isEmpty()){
	    	sheetId = sheets.get(0).getProperties().getSheetId();
	    }
	    
	    return sheetId;
	}


	public void deleteRow(String spreadsheetId, Integer sheetId, Integer numRow) throws IOException{
        
        Sheets service = getSheetsService();

        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
        DeleteDimensionRequest deleteDimensionRequest = new DeleteDimensionRequest();
        DimensionRange dimensionRange = new DimensionRange();
        dimensionRange.setDimension("ROWS");
        dimensionRange.setSheetId(sheetId);
        dimensionRange.setStartIndex(numRow -1);
        dimensionRange.setEndIndex(numRow);
        deleteDimensionRequest.setRange(dimensionRange);
        Request deleteRequest = new Request();
        deleteRequest.setDeleteDimension(deleteDimensionRequest);
        batchUpdateSpreadsheetRequest.setRequests(Collections.singletonList(deleteRequest));
        
        Sheets.Spreadsheets.BatchUpdate batchUpdate = service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateSpreadsheetRequest);
        
        BatchUpdateSpreadsheetResponse response = batchUpdate.execute();
        
        MyLogger.getLogger().fine(response.getReplies().toString());

    }
	
	public void deleteRows(String spreadsheetId, Integer sheetId, List<Integer> numRows) throws IOException{
        if (numRows == null || numRows.isEmpty()){return;}
		
        Collections.sort(numRows, Collections.reverseOrder());
        
        Sheets service = getSheetsService();

        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();

        List<Request> deleteRequests = new ArrayList<Request>();
        for (Integer numRow: numRows){
	        DeleteDimensionRequest deleteDimensionRequest = new DeleteDimensionRequest();
	        DimensionRange dimensionRange = new DimensionRange();
	        dimensionRange.setDimension("ROWS");
	        dimensionRange.setSheetId(sheetId);
	        dimensionRange.setStartIndex(numRow-1);
	        dimensionRange.setEndIndex(numRow);
	        deleteDimensionRequest.setRange(dimensionRange);
	        
	        DeleteRangeRequest deleteRangeRequest = new DeleteRangeRequest();
	        deleteRangeRequest.setShiftDimension("ROWS");
	        GridRange gridRange = new GridRange();
	        gridRange.setSheetId(sheetId);
	        gridRange.setStartRowIndex(numRow-1);
	        gridRange.setEndRowIndex(numRow);
	        deleteRangeRequest.setRange(gridRange);
	        
	        Request deleteRequest = new Request();
	        deleteRequest.setDeleteDimension(deleteDimensionRequest);
	        //deleteRequest.setDeleteRange(deleteRangeRequest);
	        deleteRequests.add(deleteRequest);
        }
        batchUpdateSpreadsheetRequest.setRequests(deleteRequests);
        batchUpdateSpreadsheetRequest.setIncludeSpreadsheetInResponse(true);
        
        Sheets.Spreadsheets.BatchUpdate batchUpdate = service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateSpreadsheetRequest);
        
        BatchUpdateSpreadsheetResponse response = batchUpdate.execute();
        
        MyLogger.getLogger().fine(response.getReplies().toString());

    }
    
    public Integer updateRows(String spreadsheetId, List<ValueRange> data, boolean userEntered) throws IOException{
   	
    	MyLogger.getLogger().fine(data.toString());
    	
        Sheets service = getSheetsService();
        String valueInputOption = userEntered?USER_ENTERED:RAW;
    	BatchUpdateValuesRequest body = new BatchUpdateValuesRequest().setValueInputOption(valueInputOption).setData(data);
    	BatchUpdateValuesResponse result = service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();

    	MyLogger.getLogger().fine(result.toPrettyString());
    	
    	return result.getTotalUpdatedRows();
    }
    
    public void clearRows(String spreadsheetId, List<String> range) throws IOException{
       	
        Sheets service = getSheetsService();
		
		BatchClearValuesRequest batchClearValuesRequest = new BatchClearValuesRequest();
		batchClearValuesRequest.setRanges(range);
		Sheets.Spreadsheets.Values.BatchClear batchClear = service.spreadsheets().values().batchClear(spreadsheetId, batchClearValuesRequest);
		BatchClearValuesResponse response = batchClear.execute();

    	MyLogger.getLogger().fine(response.toPrettyString());
    }
}
