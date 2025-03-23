package it.desimone.gsheetsaccess.common;

import it.desimone.gsheetsaccess.gdrive.file.ReportDriveData;
import it.desimone.utils.MyLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

	private static final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static void moveToDone(ReportDriveData reportDriveData){
		String fileName = reportDriveData.getFileName();
		Path pathWorking = FileSystems.getDefault().getPath(ResourceWorking.workingAreaPath()+File.separator+reportDriveData.getParentFolderName(), fileName);
		File folderClub = new File(ResourceWorking.doneAreaPath()+File.separator+reportDriveData.getParentFolderName());
		if (!folderClub.exists()){
			folderClub.mkdir();
		}
		Path pathDone = FileSystems.getDefault().getPath(folderClub.getAbsolutePath(), df.format(new Date())+"_"+fileName);
		try {
			Files.move(pathWorking, pathDone, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			MyLogger.getLogger().severe("error copying "+reportDriveData+" to done: "+e.getMessage());
		}catch(InvalidPathException ipe){
			MyLogger.getLogger().severe("error moving "+reportDriveData+" by "+pathWorking+" to "+pathDone+": "+ipe.getMessage());
		}
	}
	
	public static void moveToError(ReportDriveData reportDriveData){
		String fileName = reportDriveData.getFileName();
		Path pathWorking = FileSystems.getDefault().getPath(ResourceWorking.workingAreaPath()+File.separator+reportDriveData.getParentFolderName(), fileName);
		File folderClub = new File(ResourceWorking.errorAreaPath()+File.separator+reportDriveData.getParentFolderName());
		if (!folderClub.exists()){
			folderClub.mkdir();
		}
		Path pathDone = FileSystems.getDefault().getPath(folderClub.getAbsolutePath(), df.format(new Date())+"_"+fileName);
		try {
			Files.move(pathWorking, pathDone, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			MyLogger.getLogger().severe("error copying "+reportDriveData+" to error: "+e.getMessage());
		} catch(InvalidPathException ipe){
			MyLogger.getLogger().severe("error moving "+reportDriveData+" by "+pathWorking+" to "+pathDone+": "+ipe.getMessage());
		}
	}
	
}
