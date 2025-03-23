package it.desimone.gsheetsaccess.googleaccess;

import it.desimone.gsheetsaccess.common.ResourceWorking;
import it.desimone.utils.MyLogger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.PermissionList;

public class GoogleDriveAccess extends RisikoDataManagerAccess{
	    
    public PermissionList getFolderPermissionList(String folderId) throws IOException{
    	PermissionList permissionList = null;
        Drive service = getDriveService();

        if (service != null){
        	Drive.Permissions drivePermissions = service.permissions();
        	if (drivePermissions != null){
        		Drive.Permissions.List drivePermissionsList = drivePermissions.list(folderId).setFields("permissions(emailAddress,displayName,domain)");
        		if (drivePermissionsList != null){
        			permissionList = drivePermissionsList.execute();
        		}
        	}
        }

        return permissionList;
    }
    
    public List<File> getSubFolders(String parentFolderId) throws IOException{
    	List<File> folders = null;
        Drive service = getDriveService();

        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		Drive.Files.List driveFilesList = service.files().list();
        		if (driveFilesList != null){
        			driveFilesList = driveFilesList.setQ("\'"+parentFolderId+"\' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed=false");
        			FileList fileList = driveFilesList.execute();
        			if (fileList != null){
        				folders = fileList.getFiles();
        			}
        		}
        	}
        }

        return folders;
    }
    
    public FileList filesIntoFolder(File folder) throws IOException{
    	MyLogger.getLogger().fine("Ricerca dei file nel folder "+folder.getName());
    	FileList fileList = null;
    	Drive service = getDriveService();
    	
        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		Drive.Files.List driveFilesList = service.files().list();
        		if (driveFilesList != null){
        			driveFilesList = driveFilesList.setQ("\'"+folder.getId()+"\' in parents and trashed=false");
        			fileList = driveFilesList.execute();
        		}
        	}
        }
    	return fileList;
    }
    
    public void downloadFile(File file, String folder) throws IOException{
        Drive service = getDriveService();

        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		Drive.Files.Export driveFilesExport = service.files().export(file.getId(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        		if (driveFilesExport != null){
        			java.io.File localFolder = new java.io.File(ResourceWorking.workingAreaPath()+java.io.File.separator+folder);
        			if (!localFolder.exists()){
        				localFolder.mkdir();
        			}
        			java.io.File fileToDownload = new java.io.File(localFolder, file.getName());
        			FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
        			driveFilesExport.executeMediaAndDownloadTo(fileOutputStream);
        		}
        	}
        }
    }

    public File moveFileToNewFolder(String fileId, String newFolderId) throws IOException{
    	File file = null;
        Drive service = getDriveService();

        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		file = driveFiles.get(fileId).setFields("parents").execute();
        		StringBuilder previousParents = new StringBuilder();
	        	for (String parent : file.getParents()) {
	        	  previousParents.append(parent);
	        	  previousParents.append(',');
	        	}
	        	// Move the file to the new folder
	        	file = driveFiles.update(fileId, null)
	        		.setAddParents(newFolderId)
	        	    .setRemoveParents(previousParents.toString())
	        	    .setFields("id, name, parents")
	        	    .execute();
        	}
        }
        return file;
    }
    
    public File copyFileToNewFolder(String fileId, String newFolderId, String suffix) throws IOException{
    	File file = null;
        Drive service = getDriveService();

        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		file = driveFiles.get(fileId).setFields("parents, name").execute();
        		file.setName(file.getName()+suffix);
	        	file = driveFiles.copy(fileId, file).execute();
	        	file = moveFileToNewFolder(file.getId(), newFolderId);
        	}
        }
        return file;
    }
    
    public File copyFile(String fileId, String newName) throws IOException{
    	File file = null;
        Drive service = getDriveService();

        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		file = driveFiles.get(fileId).setFields("parents, name").execute();
        		file.setName(newName);
	        	file = driveFiles.copy(fileId, file).execute();
        	}
        }
        return file;
    }
    
    public File findOrCreateFolderIfNotExists(String parentFolderId, String folderName) throws IOException{
    	File folderFound = null;
    	
    	List<File> subFolders = getSubFolders(parentFolderId);
    	if (subFolders != null && !subFolders.isEmpty()){
	    	for (File file: subFolders){
	    		if(file.getName().equalsIgnoreCase(folderName)){
	    			folderFound = file;
	    			break;
	    		}
	    	}
    	}
    	
    	if (folderFound == null){
    		folderFound = createFolder(parentFolderId, folderName);
    	}
  
        return folderFound;
    }
    
    public File createFolder(String parentFolderId, String folderName) throws IOException{
    	File createdFolder = null;
        Drive service = getDriveService();

        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		File fileMetadata = new File();
        		fileMetadata.setName(folderName);
        		fileMetadata.setMimeType("application/vnd.google-apps.folder");
        		fileMetadata.setParents(Collections.singletonList(parentFolderId));

        		createdFolder = driveFiles.create(fileMetadata).setFields("id").execute();
        	}
        }
        return createdFolder;
    }
    
    public File updateFileWithData(String fileIdToUpdate, String fileIdToRestore) throws IOException{
    	File file = null;
        Drive service = getDriveService();

        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		File restoreFile = driveFiles.get(fileIdToRestore).execute();
        		if (restoreFile != null){
        			file = driveFiles.update(fileIdToUpdate, restoreFile).setFileId(fileIdToUpdate).execute();
        		}
        	}
        }
        return file;
    }
}