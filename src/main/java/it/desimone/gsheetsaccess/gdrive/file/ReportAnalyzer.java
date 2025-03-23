package it.desimone.gsheetsaccess.gdrive.file;

import it.desimone.gsheetsaccess.common.ExcelValidationException;
import it.desimone.gsheetsaccess.common.ResourceWorking;
import it.desimone.risiko.torneo.batch.ExcelAccess;
import it.desimone.risiko.torneo.batch.ExcelValidator;
import it.desimone.risiko.torneo.batch.ExcelValidator.ExcelValidatorData;
import it.desimone.risiko.torneo.dto.Torneo;

import java.io.File;

public class ReportAnalyzer {

	public static Torneo analyzeExcelReport(ReportDriveData reportDriveData) throws ExcelValidationException {
		Torneo torneo = null;
		File excelFile = new File(ResourceWorking.workingAreaPath()+java.io.File.separator+reportDriveData.getParentFolderName(),reportDriveData.getFileName());
		ExcelValidator excelValidator = new ExcelValidator(excelFile);
		ExcelValidatorData excelValidatorData = excelValidator.validaFoglioExcel();
		if (excelValidatorData != null && excelValidatorData.containsErrors()){
			throw new ExcelValidationException(excelValidatorData.getErrors());
		}else{
			ExcelAccess excelAccess = new ExcelAccess(excelFile);
			excelAccess.openFileExcel();
			torneo = excelAccess.elaboraTorneo();
			excelAccess.closeFileExcel();
			torneo.setFilename(reportDriveData.getFileName());
		}
		
		return torneo;
	}
	
}
