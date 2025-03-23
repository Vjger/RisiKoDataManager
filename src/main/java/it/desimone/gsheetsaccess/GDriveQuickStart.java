package it.desimone.gsheetsaccess;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GDriveQuickStart {
	
	private static java.io.File testExcel = new java.io.File("C:\\Users\\mds\\Downloads\\ModuloMasterVerona.xls");

	
	private static final String PROJECT_LOCATION = "C:\\GIT Repositories\\FirstRepo\\GoogleSheetsRemoteAccess\\";
	//private static final String PROJECT_LOCATION = "C:\\WORK\\WORKSPACES_ECLIPSE\\TEST_GIT\\FirstRepo\\GoogleSheetsRemoteAccess";
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
   
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
    		PROJECT_LOCATION+"\\resources", ".credentials/drive-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES =
        //Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);
    	Arrays.asList(DriveScopes.DRIVE); 

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize()  {
    	// Load client secrets.
    	//InputStream in = GDriveQuickStart.class.getResourceAsStream("/client_secret.json");

    	Credential credential = null;

    	try{
    		InputStream in = new FileInputStream(PROJECT_LOCATION+"/resources//client_secret.json");
    		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    		// Build flow and trigger user authorization request.
    		GoogleAuthorizationCodeFlow flow =
    				new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
    				.setDataStoreFactory(DATA_STORE_FACTORY)
    				.setAccessType("offline")
    				.build();
    		credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    		System.out.println("Credential Access Token: "+credential.getAccessToken());
    		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
    		
    		System.out.println("Credential expires in "+credential.getExpiresInSeconds()+" seconds");
    	}catch(IOException ioe){
    		System.err.println(ioe);
    	}
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService(Credential credential)  {
        //Credential credential = authorize();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }

    public static void main(String[] args) throws IOException {
    	//uploadFileIntoFolder();
//    	extractReportByFolders();
    	Credential credential = authorize();
    	String parentFolderId = "1RCm2oUNm1weBqSErFAFBxsRHfKWDJJ9a";
    	String parentFolderIdStage = "1OremAjhDfjtIXhgWsq7lCAcnSBZKWGsF";
    	List<File> clubFolders = getClubFolders(credential, parentFolderIdStage);
    	System.out.println("Folder del club: "+(clubFolders == null?"null":clubFolders.size()));
    	if (clubFolders != null){;

//			ExcelAccess excelAccess = new ExcelAccess(testExcel);
//			excelAccess.openFileExcel();
//			excelAccess.hideMailColumn();
//			excelAccess.closeFileExcel();
    	
//    		File uploadedFile = uploadClubReport(credential, clubFolders, testExcel);
//    		System.out.println("Uploaded file by Id "+uploadedFile.getId());
    	}
    }
    
    private static void testFileAndFolders() throws IOException{
        Drive service = getDriveService(authorize());

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
        	.setQ("\'0B-WU8eY52U1IdDVqNTVkT2RWd2c\' in parents and mimeType = 'application/vnd.google-apps.folder'")
             .setPageSize(10)
             .setFields("nextPageToken, files(id, name)")
             .execute();
        List<File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
                System.out.println(file.getParents());
            }
        }
    }
    
    private static void extractReportByFolders() throws IOException{
        Credential credential = authorize();
        Drive service = getDriveService(credential);


        // Print the names and IDs for up to 10 files.
        FileList resultFolders = service.files().list()
        	//.setQ("\'0B-WU8eY52U1IdDVqNTVkT2RWd2c\' in parents and mimeType = 'application/vnd.google-apps.folder'")
        	.setQ("\'1RCm2oUNm1weBqSErFAFBxsRHfKWDJJ9a\' in parents and mimeType = 'application/vnd.google-apps.folder' and sharedWithMe=true")
             //.setFields("files(id, name)")
             .execute();
        List<File> folders = resultFolders.getFiles();
        if (folders == null || folders.size() == 0) {
            System.out.println("No folders found.");
        } else {
            System.out.println("Folders:");
            for (File folder : folders) {
                System.out.printf("%s (%s)\n", folder.getName(), folder.getId());
                FileList resultReports = service.files().list()
                    	.setQ("\'"+folder.getId()+"\' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed=false")
                         .setFields("files(id, name, mimeType, webContentLink)")
                         .execute();
                List<File> reports = resultReports.getFiles();
                if (reports == null || reports.size() == 0) {
                    System.out.println("No files found.");
                } else {
                    System.out.println("Files:");
                    for (File report : reports) {
                        System.out.printf("%s (%s)\n", report.getName(), report.getId());
                        //File newMetadata = new File();
                        //newMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
                        //service.files().update(report.getId(), newMetadata).execute();
                        System.out.println(report.getMimeType());
                        System.out.println(report.getWebContentLink());
                        System.out.println(report.getProperties());
                        //if (report.getId().equals("1TDo3k3gB1ZIZoWIhRF9KS7R51shsY8atMLwNJv8vLPg")){
                        	SheetsQuickstart.leggiSheets(credential, report.getId());
                        //}
                    }
                }
            }
        }
    }

    private static void uploadFileIntoFolder() throws IOException{
        Credential credential = authorize();
        Drive service = getDriveService(credential);

        java.io.File filePath = new java.io.File("C:\\Users\\Marco De Simone\\Documents\\OWL\\2016-03-TORNEO-RISIKO-PAIOLO.xls");
        
        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName());
        fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
        fileMetadata.setParents(Collections.singletonList("1oH4UxLXX0tAVa4Rw4svrc4ZmfDOOcAvn"));
        

        FileContent mediaContent = new FileContent("application/vnd.ms-excel", filePath);
        File file = service.files().create(fileMetadata, mediaContent)
            .setFields("id, parents")
            .execute();
        System.out.println("File ID: " + file.getId());

    }
    
    public static List<File> getClubFolders(Credential credential, String parentFolderId) throws IOException{
    	List<File> folders = null;
        Drive service = getDriveService(credential);

        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		Drive.Files.List driveFilesList = service.files().list();
        		if (driveFilesList != null){
        			driveFilesList = driveFilesList.setQ("\'"+parentFolderId+"\' in parents and mimeType = 'application/vnd.google-apps.folder' and sharedWithMe=true");
        			//driveFilesList = driveFilesList.setQ("\'"+parentFolderId+"\' in parents and mimeType = 'application/vnd.google-apps.folder'");
        			try{
	        			FileList fileList = driveFilesList.execute();
	        			if (fileList != null){
	        				folders = fileList.getFiles();
	        			}
        			}catch(Exception e){
        				e.printStackTrace();
        			}
        		}
        	}
        }

        return folders;
    }
    
    public static File uploadClubReport(Credential credential, List<File> clubFolders, java.io.File report) throws IOException{
    	File uploadedFile = null;
        Drive service = getDriveService(credential);
        
        if (clubFolders != null && !clubFolders.isEmpty()){
        	for (File clubFolder: clubFolders){
		        File fileMetadata = new File();
		        fileMetadata.setName(report.getName());
		        fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");   
		        
		        FileContent mediaContent = new FileContent("application/vnd.ms-excel", report);
		        
		        String fileId = fileExistsIntoFolder(credential, clubFolder, report.getName());
		        
	        	Drive.Files driveFiles = service.files();
		        if (fileId != null){
		        	uploadedFile = driveFiles.update(fileId, fileMetadata, mediaContent).setFields("id, parents").execute();		        	
		        }else{	        
			        fileMetadata.setParents(Collections.singletonList(clubFolder.getId()));
		        	uploadedFile = driveFiles.create(fileMetadata, mediaContent).setFields("id, parents").execute();
	        	}
        	}
        }
        return uploadedFile;
    }
    
    public static String fileExistsIntoFolder(Credential credential, File folder, String reportName) throws IOException{
    	String fileId = null;
    	Drive service = getDriveService(credential);
    	
        if (service != null){
        	Drive.Files driveFiles = service.files();
        	if (driveFiles != null){
        		Drive.Files.List driveFilesList = service.files().list();
        		if (driveFilesList != null){
        			driveFilesList = driveFilesList.setQ("\'"+folder.getId()+"\' in parents and name=\'"+reportName+"\' and trashed=false");
        			FileList fileList = driveFilesList.execute();
        			if (fileList != null && fileList.getFiles() != null && !fileList.getFiles().isEmpty()){
        				fileId = fileList.getFiles().get(0).getId();
        			}
        		}
        	}
        }
    	return fileId;
    }
}