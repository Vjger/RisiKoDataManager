package it.desimone;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.googleaccess.GoogleSheetsAccess;
import it.desimone.gsheetsaccess.gsheets.dto.AnagraficaGiocatoreRow;
import it.desimone.gsheetsaccess.gsheets.dto.ClassificheRow;
import it.desimone.gsheetsaccess.gsheets.dto.PartitaRow;
import it.desimone.gsheetsaccess.gsheets.dto.SheetRow;
import it.desimone.gsheetsaccess.gsheets.facade.GSheetsInterface;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class GSheetsInterfaceTest extends TestCase {

	public void testCercaPartiteGiocatore() throws IOException {
		Integer idGiocatore = 1;
		String spreadSheetIdTornei = Configurator.getTorneiSheetId("2018");
		PartitaRow row = new PartitaRow();
		row.setIdGiocatoreVincitore(idGiocatore);
		
		List<SheetRow> rows = GSheetsInterface.findPartiteRowsByIdGiocatore(spreadSheetIdTornei, row);
	}
	
	public void testCercaClassificheGiocatore() throws IOException {
		Integer idGiocatore = 1;
		String spreadSheetIdTornei = Configurator.getTorneiSheetId("2018");
		ClassificheRow row = new ClassificheRow();
		row.setIdGiocatore(idGiocatore);
		
		List<SheetRow> rows = GSheetsInterface.findClassificaRowsByIdGiocatore(spreadSheetIdTornei, row);
	}
	
	
	public void testLeggiSheetStringString() throws IOException {
		GoogleSheetsAccess googleSheetsAccess = new GoogleSheetsAccess();
		String spreadSheetIdTornei = Configurator.getTorneiSheetId("2018");
		String sheetNameAnagrafica = AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME;
		
		Long before = System.currentTimeMillis();
		
		List<List<Object>> sheetRows = googleSheetsAccess.leggiSheet(spreadSheetIdTornei, sheetNameAnagrafica);
		
		Long after = System.currentTimeMillis();
		
		System.out.println("leggiSheet(String spreadsheetId, String sheetName): "+(after-before));
		
//		for (List<Object> row: sheetRows){
//			System.out.println(row);
//		}
	}

	public void testLeggiSheetStringListOfString() throws IOException{
		GoogleSheetsAccess googleSheetsAccess = new GoogleSheetsAccess();
		String spreadSheetIdTornei = Configurator.getTorneiSheetId("2018");
		String sheetNameAnagrafica = AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME;
		List<String> ranges = Collections.singletonList(sheetNameAnagrafica+"!A2:G");
		
		Long before = System.currentTimeMillis();
		
		List<List<Object>> sheetRows = googleSheetsAccess.leggiSheet(spreadSheetIdTornei, ranges);
		
		Long after = System.currentTimeMillis();
		
		System.out.println("leggiSheet(String spreadsheetId, List<String> ranges): "+(after-before));
		
//		for (List<Object> row: sheetRows){
//			System.out.println(row);
//		}
	}

	public void testFindRow() throws IOException {
		GoogleSheetsAccess googleSheetsAccess = new GoogleSheetsAccess();
		String spreadSheetIdTornei = Configurator.getTorneiSheetId("2018");
		String sheetNameAnagrafica = AnagraficaGiocatoreRow.SHEET_GIOCATORI_NAME;
		List<String> ranges = Collections.singletonList(sheetNameAnagrafica+"!A2:G");
		
		Long before = System.currentTimeMillis();
		
		List<List<Object>> sheetRows = googleSheetsAccess.findRow(spreadSheetIdTornei, ranges);
		
		Long after = System.currentTimeMillis();
		
		System.out.println("findRow(String spreadsheetId, List<String> ranges): "+(after-before));
		
		for (List<Object> row: sheetRows){
			System.out.println(row);
		}
	}

}
