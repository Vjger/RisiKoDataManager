package it.desimone.gsheetsaccess.gdrive.file;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.googleaccess.GoogleDriveAccess;
import it.desimone.utils.MyException;
import it.desimone.utils.MyLogger;

public class GDriveDownloader {

	
	public static List<ReportDriveData> downloadReport(boolean download){
		List<ReportDriveData> result = new ArrayList<ReportDriveData>();
		
		GoogleDriveAccess googleDriveAccess = new GoogleDriveAccess();

    	String parentFolderId = Configurator.getRCUFolderId();
    	
    	if (parentFolderId != null){
    		try{
    			List<File> availableFolders = googleDriveAccess.getSubFolders(parentFolderId);
    			if (availableFolders != null && !availableFolders.isEmpty()){
    				for (File folder: availableFolders){
    					MyLogger.getLogger().finer("Folder "+folder.getName());
    					PermissionList permissionList = googleDriveAccess.getFolderPermissionList(folder.getId()); 
    					List<Permission> permissions = permissionList.getPermissions();
    					List<String> emailAddresses = null;
    					if (permissions != null){
    						emailAddresses = new ArrayList<String>();
	    					for (Permission permission: permissions){
	    						MyLogger.getLogger().fine("Folder "+folder.getName()+" e-mail: "+permission.getEmailAddress());
	    						emailAddresses.add(permission.getEmailAddress());
	    					}
    					}
    					FileList fileList = googleDriveAccess.filesIntoFolder(folder);
    					List<File> files = fileList.getFiles();
    					if (files != null){
    						MyLogger.getLogger().info("Trovati "+files.size()+" report nel folder "+folder.getName());
    						for (File file: files){
    							MyLogger.getLogger().info("Download del file "+file.getName()+" con id "+file.getId());
    							if (download){
    								googleDriveAccess.downloadFile(file, folder.getName());
    							}
    							ReportDriveData reportDriveData = new ReportDriveData();
    							reportDriveData.setParentFolderId(folder.getId());
    							reportDriveData.setParentFolderName(folder.getName());
    							reportDriveData.setIdGoogleDrive(file.getId());
    							reportDriveData.setFileName(file.getName());
    							reportDriveData.setEmailContacts(emailAddresses);
    							result.add(reportDriveData);
    						}
    					}
    				}
    			}else{
    				MyLogger.getLogger().severe("Non è stato abilitato alcun folder di Google Drive alle credenziali in uso");
    				throw new MyException("Non è stato abilitato alcun folder di Google Drive alle credenziali in uso");
    			}
    		}catch(UnknownHostException uhe){
    			MyLogger.getLogger().severe(uhe.getMessage());
        		throw new MyException("Verificare la connessione Internet: "+uhe.getMessage());
    		}catch(IOException ioe){
    			MyLogger.getLogger().severe(ioe.getMessage());
        		throw new MyException(ioe);
    		}
    	}else{
    		MyLogger.getLogger().severe("Non è stato trovato l'ID del folder genitore di Google Drive");
    		throw new MyException("Non è stato trovato l'ID del folder genitore di Google Drive");
    	}
    	
    	return result;
	}

}
