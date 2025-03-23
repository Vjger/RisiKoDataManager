package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSheetRow implements SheetRow {

	public static final String SHEET_DATA_ANALYSIS_NAME = "DATA_ANALYSIS";
	
	protected Integer sheetRowNumber;
	protected List<Object> data;
	
	public Integer getSheetRowNumber() {
		return sheetRowNumber;
	}
	public void setSheetRowNumber(Integer sheetRowNumber) {
		this.sheetRowNumber = sheetRowNumber;
	}
	public List<Object> getData() {
		initializeData(getDataSize());
		data.set(getSheetRowNumberColPosition(), "=row()");
		return data;
	}
	public void setData(List<Object> data) {
		this.data = data;
		sheetRowNumber = Integer.valueOf((String)data.get(getSheetRowNumberColPosition()));
	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	protected void initializeData(Integer dimArray){
		data = Arrays.asList(new Object[dimArray]);
		Collections.fill(data, "");
	}
	
	public Integer getSheetRowNumberColPosition(){
		return getDataSize()-1;
	}
}
