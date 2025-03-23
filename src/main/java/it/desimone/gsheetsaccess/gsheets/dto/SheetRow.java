package it.desimone.gsheetsaccess.gsheets.dto;

import java.util.List;

public interface SheetRow extends Cloneable{

	public Integer getSheetRowNumberColPosition();
	
	public Integer getSheetRowNumber();
	public void setSheetRowNumber(Integer sheetRowNumber);
	
	public List<Object> getData();
	public void setData(List<Object> data);
	
	public List<Integer> keyCols();
	
	public Integer getDataSize();
	
	public Object clone() throws CloneNotSupportedException;
}
