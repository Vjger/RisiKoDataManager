package it.desimone.gsheetsaccess.googleaccess;

import it.desimone.gsheetsaccess.common.ResourceWorking;
import it.desimone.utils.MyException;
import it.desimone.utils.MyLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

public class RisikoDataManagerAccess {

	protected Credential credential;

    /** Application name. */
    private static final String APPLICATION_NAME = "RisiKo! Data Manager";

    /** Directory to store user credentials for this application. */
   
    private static final java.io.File DATA_STORE_DIR = ResourceWorking.googleCredentials(); 

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES =
        //Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);
    	Arrays.asList(new String[]{DriveScopes.DRIVE, SheetsScopes.DRIVE, GmailScopes.GMAIL_SEND}); 

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
    		MyLogger.getLogger().severe("Problema con l'accesso a Google: "+t);
        }
    }

    public RisikoDataManagerAccess(){
    	try {
			this.credential = authorize();
		} catch (IOException e) {
			MyLogger.getLogger().severe("Credenziali errate per l'accesso a Google: "+e);
			throw new MyException(e, "Credenziali errate per l'accesso a Google");
		}
    }
    
    
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    protected Credential authorize() throws IOException {
    	// Load client secrets.
    	//InputStream in = GDriveQuickStart.class.getResourceAsStream("/client_secret.json");

    	Credential credential = null;

    		InputStream in = new FileInputStream(ResourceWorking.googleClientSecretPath());
    		
    		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    		// Build flow and trigger user authorization request.
    		GoogleAuthorizationCodeFlow flow =
    				new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
    				.setDataStoreFactory(DATA_STORE_FACTORY)
    				.setAccessType("offline")
    				.build();
    		credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    		
    		MyLogger.getLogger().info("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());

        return credential;
    }
	
    private HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
    	  return new HttpRequestInitializer() {
    	    //@Override
    	    public void initialize(HttpRequest httpRequest) throws IOException {
    	      requestInitializer.initialize(httpRequest);
    	      httpRequest.setConnectTimeout(3 * 60000);  // 3 minutes connect timeout
    	      httpRequest.setReadTimeout(3 * 60000);  // 3 minutes read timeout
    	    }
    	  };
    }
    
    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    protected Drive getDriveService() {
        //Credential credential = authorize();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }
    
    protected Sheets getSheetsService() throws IOException {
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(credential))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
   
    protected Gmail getGmailService() throws IOException {
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
