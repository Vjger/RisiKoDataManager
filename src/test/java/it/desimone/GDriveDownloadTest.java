package it.desimone;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import it.desimone.gsheetsaccess.common.Configurator;
import it.desimone.gsheetsaccess.googleaccess.GoogleDriveAccess;
import it.desimone.utils.MyException;
import it.desimone.utils.MyLogger;

public class GDriveDownloadTest {

	public static void main(String[] args) {
		MyLogger.setConsoleLogLevel(Level.ALL);
		
		GoogleDriveAccess googleDriveAccess = new GoogleDriveAccess();

    	String parentFolderId = Configurator.getRCUFolderId();
    	
    	if (parentFolderId != null){
    		try{
    			List<File> availableFolders = googleDriveAccess.getSubFolders(parentFolderId);
    			if (availableFolders != null && !availableFolders.isEmpty()){
    				for (File folder: availableFolders){
    					MyLogger.getLogger().info("Folder "+folder.getName());
    					PermissionList permissionList = googleDriveAccess.getFolderPermissionList(folder.getId()); 
    					List<Permission> permissions = permissionList.getPermissions();
    					for (Permission permission: permissions){
    						MyLogger.getLogger().info("Folder "+folder.getName()+" permission: "+permission.getRole()+" - "+permission.getEmailAddress());
    					}
    					FileList fileList = googleDriveAccess.filesIntoFolder(folder);
    					List<File> files = fileList.getFiles();
    					if (files != null){
    						MyLogger.getLogger().info("Trovati "+files.size()+" nel folder "+folder.getName());
    						for (File file: files){
    							MyLogger.getLogger().info("Download del file ["+file.getName()+"] con id "+file.getId());
    							//googleDriveAccess.downloadFile(file, folder.getName());
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
		
	}

}
