package it.desimone.gsheetsaccess.htmlpublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.googleaccess.GoogleSheetsAccess;
import it.desimone.utils.MyLogger;

public class FalsePositiveData {

	public static final String FALSE_POSITIVE_SHEET = "FALSE_POSITIVE";
	private List<FalsePositivePlayers> falsePositivePlayersList;
	private static FalsePositiveData instance;
	
	private FalsePositiveData () {}
	
	public static FalsePositiveData getInstance() {
		if (instance == null) {	
			instance = new FalsePositiveData();
			GoogleSheetsAccess googleSheetsAccess = new GoogleSheetsAccess();
			try {
				List<List<Object>> rows = googleSheetsAccess.leggiSheet(Configurator.getBlackListSheetId(), FALSE_POSITIVE_SHEET+"!A2:B");
				if (CollectionUtils.isNotEmpty(rows)) {
					 List<FalsePositivePlayers> fppl = new ArrayList<FalsePositivePlayers>();
					for (List<Object> row: rows) {
						Integer idAnagrafica1 =  Integer.parseInt((String)row.get(0));
						Integer idAnagrafica2 =  Integer.parseInt((String)row.get(1));
	
						FalsePositivePlayers falsePositivePlayers = new FalsePositivePlayers(idAnagrafica1, idAnagrafica2);
	
						fppl.add(falsePositivePlayers);
					}
					instance.setFalsePositivePlayersList(fppl);
				}
				MyLogger.getLogger().info("FALSE_POSITIVE_SHEET: "+instance);
			} catch (IOException e) {
				MyLogger.getLogger().severe("Errore nell'acquisizione della false positive list: "+e.getMessage());
			} catch (Exception e) {
				MyLogger.getLogger().severe("Errore nell'elaborazione della false positive list: "+e.getMessage());
			}
		}
		return instance;
	}

	public List<FalsePositivePlayers> getFalsePositivePlayersList() {
		return falsePositivePlayersList;
	}

	public void setFalsePositivePlayersList(List<FalsePositivePlayers> falsePositivePlayersList) {
		this.falsePositivePlayersList = falsePositivePlayersList;
	}

	@Override
	public String toString() {
		return "FalsePositiveData [falsePositivePlayers=" + falsePositivePlayersList + "]";
	}
	
	public boolean areFalsePositive(Integer anagraficaId1, Integer anagraficaId2) {
		FalsePositivePlayers falsePositivePlayers = new FalsePositivePlayers(anagraficaId1, anagraficaId2);
		return falsePositivePlayersList != null && falsePositivePlayersList.contains(falsePositivePlayers);
	}
}
