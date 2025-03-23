package it.desimone.gsheetsaccess.htmlpublisher;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.googleaccess.GoogleSheetsAccess;
import it.desimone.gsheetsaccess.gsheets.dto.LastUpdateRow;
import it.desimone.gsheetsaccess.gsheets.facade.ExcelGSheetsBridge;
import it.desimone.gsheetsaccess.utils.TorneiUtils;
import it.desimone.utils.MyLogger;

public class LastUpdateData {

	public static final String LAST_UPDATE_SHEET = "LAST_UPDATE";
	private Map<Integer, Date> lastUpdateMap;
	private static LastUpdateData instance;
	private List<LastUpdateRow> lastUpdateRows;
	
	//private LastUpdateData () {}
	
	public static LastUpdateData getInstance() {
		if (instance == null) {	
			instance = new LastUpdateData();
			GoogleSheetsAccess googleSheetsAccess = new GoogleSheetsAccess();
			try {
				List<List<Object>> rows = googleSheetsAccess.leggiSheet(Configurator.getBlackListSheetId(), LAST_UPDATE_SHEET+"!A2:B");
				if (CollectionUtils.isNotEmpty(rows)) {
					Map<Integer, Date> lum = new HashMap<Integer, Date>();
					for (List<Object> row: rows) {
						Integer anno =  Integer.parseInt((String)row.get(0));
						String lastUpdateDateString = (String)row.get(1);
						Date lastUpdateDate =  ExcelGSheetsBridge.dfUpdateTime.parse(lastUpdateDateString);
		
						lum.put(anno, lastUpdateDate);
					}
					instance.setLastUpdateMap(lum);
				}
				MyLogger.getLogger().info("LAST_UPDATE_SHEET: "+instance);
			} catch (IOException e) {
				MyLogger.getLogger().severe("Errore nell'acquisizione della last update list: "+e.getMessage());
			} catch (Exception e) {
				MyLogger.getLogger().severe("Errore nell'elaborazione della last update list: "+e.getMessage());
			}
		}
		return instance;
	}

	public LastUpdateData() {
		readLastUpdateRows();
	}
	public Map<Integer, Date> getLastUpdateMap() {
		return lastUpdateMap;
	}

	public void setLastUpdateMap(Map<Integer, Date> lastUpdateMap) {
		this.lastUpdateMap = lastUpdateMap;
	}

	public Date getLastTournamentDate(Integer year){
		return lastUpdateMap.get(year);
	}
	
	public void readLastUpdateRows() {
		lastUpdateRows = TorneiUtils.getAllLastUpdateRow();
	}
	
	public LastUpdateRow getLastUpdateRow(Integer year){
		LastUpdateRow result = null;
		Stream<LastUpdateRow> stream = lastUpdateRows.stream();
		Optional<LastUpdateRow> lastUpdateRow = stream.filter(lur -> lur.getAnnoRiferimento().equals(year)).findFirst();
		if (lastUpdateRow.isPresent()) {
			result = lastUpdateRow.get();
		}
		return result;
	}
	
	public Date getLastTournamentDateByUpdateRow(Integer year){
		Date result = null;
		LastUpdateRow lastUpdateRow = getLastUpdateRow(year);
		if (lastUpdateRow != null) {
			try {
				result = ExcelGSheetsBridge.dfUpdateTime.parse(lastUpdateRow.getLastElaboration());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
