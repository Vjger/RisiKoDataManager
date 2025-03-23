package it.desimone;

import it.desimone.gsheetsaccess.googleaccess.GoogleDriveAccess;
import it.desimone.utils.MyLogger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Delete;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.PermissionList;

public class GoogleDriveAccessTest extends GoogleDriveAccess{

	public static void main(String[] args) throws IOException {
		MyLogger.setConsoleLogLevel(Level.ALL);
		//java.io.File report = new java.io.File("‪C:\\Utenti\\mds\\Desktop\\2018-03-TORNEO-RISIKO-PAIOLO.xls");
		java.io.File report = new java.io.File("C:\\Users\\mds\\Documents\\GUFO\\REPORT GUFO\\2018\\2018-06-CAMPIONATO-RISIKO-VICTORIAN-MONKEY.xls");
		//testUploadClubReport(report);
		testGetFiles();
	}
	
	public static void testGetFiles() throws IOException {

		//String fileId = "1ThWk8Z8n8Tr9JztAtlzw3C2mthbyPy8G";
		String fileId = "1cxnawgeqgKZR7cDrTgDQNx_4Ui_TeklA";
		String fileName = "2018-06-CAMPIONATO-RISIKO-VICTORIAN-MONKEY";
		
		GoogleDriveAccessTest gt = new GoogleDriveAccessTest();
		File folder = gt.getDriveService().files().get(fileId).setFields("id, name, parents").execute();
		FileList fileList = gt.filesIntoFolder(folder);
		List<File> files = fileList.getFiles();
		if (files != null){
			MyLogger.getLogger().info("Trovati "+files.size()+" nel folder "+folder.getName());
			StringBuilder buffer = new StringBuilder();
			for (File file: files){
				MyLogger.getLogger().info("Name ["+file.getName()+"] id ["+file.getId()+"] Original fileName ["+file.getOriginalFilename()+"] file extension ["+file.getFileExtension()+"] full file extension ["+file.getFullFileExtension()+"]");
				MyLogger.getLogger().info(file.toString());
				buffer.append(file.getExplicitlyTrashed()+"\n");
				buffer.append(file.getIsAppAuthorized()+"\n");
				buffer.append(file.getAppProperties()+"\n");
				buffer.append(file.getLastModifyingUser()+"\n");
				buffer.append(file.getModifiedByMe()+"\n");
				buffer.append(file.getOwnedByMe()+"\n");
				buffer.append(file.getOwners()+"\n");
				buffer.append(file.getProperties()+"\n");
				buffer.append(file.getShared()+"\n");
				buffer.append(file.getSharingUser()+"\n");
				buffer.append(file.getTrashed()+"\n");
				buffer.append(file.getTrashingUser()+"\n");
				MyLogger.getLogger().info(buffer.toString());
				
				PermissionList pl = gt.getFolderPermissionList(file.getId());
				MyLogger.getLogger().info(pl.toPrettyString());
				
				/*
				 * Delete delete = gt.getDriveService().files().delete(file.getId());
				 * delete.execute();
				 */
			}
		}
		
		Drive.Files.List driveFilesList = gt.getDriveService().files().list();
		driveFilesList = driveFilesList.setQ("\'"+folder.getId()+"\' in parents and name contains \'"+fileName+"\' and trashed=false");
		FileList fileList2 = driveFilesList.execute();
		List<File> reports = fileList2.getFiles();
		for (File report: reports){
			MyLogger.getLogger().info("Report ["+report.getName()+"] con id ["+report.getId()+"]");
		}
	}

    public static void testUploadClubReport(java.io.File report) throws IOException{
    	MyLogger.getLogger().fine("Elaborazione del file "+report.getName());
    	
		String clubFolderId = "1TCMe2yZchgnBMeLiVbvD_AwDhSL-AOKP";
		
		GoogleDriveAccessTest gt = new GoogleDriveAccessTest();
		File clubFolder = gt.getDriveService().files().get(clubFolderId).setFields("id, name, parents").execute();
        Drive service = gt.getDriveService();
        
        File fileMetadata = new File();
        String reportName = report.getName();
        MyLogger.getLogger().fine("ReportName: ["+reportName+"]");
        fileMetadata.setName(reportName);
        fileMetadata.setOriginalFilename(report.getName());
        fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");   

        FileContent mediaContent = new FileContent("application/vnd.ms-excel", report);

        Drive.Files driveFiles = service.files();

        fileMetadata.setParents(Collections.singletonList(clubFolder.getId()));
        File uploadedFile = driveFiles.create(fileMetadata, mediaContent).setFields("id, parents, name, originalFilename").execute();
        MyLogger.getLogger().fine("Inserito file "+report.getName()+" con id "+uploadedFile.getId()+" nel folder "+clubFolder.getName()+"-"+clubFolder.getId());
    }
       
}
