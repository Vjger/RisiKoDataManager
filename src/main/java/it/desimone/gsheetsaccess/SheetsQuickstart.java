package it.desimone.gsheetsaccess;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;




public class SheetsQuickstart {
	private static final String PROJECT_LOCATION = "C:\\WORK\\WORKSPACES_ECLIPSE\\TEST_GIT\\FirstRepo\\GoogleSheetsRemoteAccess";
	//private static final String PROJECT_LOCATION = "C:\\GIT Repositories\\FirstRepo\\GoogleSheetsRemoteAccess\\";
	
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Sheets API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
    		PROJECT_LOCATION+"\\resources", ".credentials/sheets.googleapis.com-java-quickstart");

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
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
    public static ValueRange response;
    public static UpdateValuesResponse request;

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(PROJECT_LOCATION+"/resources//client_secret.json");
            //SheetsQuickstart.class.getResourceAsStream("C://WorkSpaces Eclipse//RisikoWorkSpace//GoogleSheetsRemoteAccess//resources//client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public static Sheets getSheetsService(Credential credential) throws IOException {

        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    
    public static void main(String[] args) throws IOException {
        Credential credential = authorize();
    	leggiGiocatori(credential, "1pJcq1B3pKaAV2-IIKTgqYwv80wIZszWqpeF0Ml2npTU");
    	leggiGiocatori(credential, "1zElBXO79cam7BR6w1uQRCB42W5tld5qHZtkCdql7BLs");
    }

    
    public static void leggiGiocatori(Credential credential, String spreadsheetId)throws IOException{
    	//https://drive.google.com/file/d/1viGXLybtQRTeu4rOFII7hD7xsubMM15Z/view?usp=sharing
    	
        Sheets service = getSheetsService(credential);
        //String range = "Iscritti!B4:E56";
        String range = "Iscritti!B:E";
        
        //spreadsheetId = "1C02AuY2mNAABS4OhL9EytZYCBSH6cArNa5HrXRxb37k";
        //range = "Ranking!C5:C5";
               
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = response.getValues();
	      for (List row : values) {
	        // Print columns A and E, which correspond to indices 0 and 4.
	        System.out.println(row);
	      }
    }
    
    public static void leggiSheets(Credential credential, String spreadsheetId)throws IOException{
    
        Sheets service = getSheetsService(credential);
	    Spreadsheet response1= service.spreadsheets().get(spreadsheetId).setIncludeGridData (false).execute ();
	
	    List<Sheet> workSheetList = response1.getSheets();
	
	    for (Sheet sheet : workSheetList) {
	        System.out.println(sheet.getProperties().getTitle());
	        ValueRange response = service.spreadsheets().values().get(spreadsheetId, sheet.getProperties().getTitle()).execute();
	        List<List<Object>> values = response.getValues();
		      for (List row : values) {
		        // Print columns A and E, which correspond to indices 0 and 4.
		        System.out.println(row);
		      }
	    }
    }
    public static void testBase() throws IOException {
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        // Prints the names and majors of students in a sample spreadsheet:
        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
        //https://drive.google.com/file/d/0B-WU8eY52U1IWlplVnZNNzRkVDg/view?usp=sharing
        String spreadsheetId = "1C02AuY2mNAABS4OhL9EytZYCBSH6cArNa5HrXRxb37k";
        String range = "Ranking!C5:C5";
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
          System.out.println("Name, Major");
          for (List row : values) {
            // Print columns A and E, which correspond to indices 0 and 4.
            System.out.println(row);
          }
        }
    }
    
    public static ValueRange getResponse(String SheetName,String RowStart, String RowEnd) throws IOException{
        // Build a new authorized API client service.
        Sheets service = getSheetsService();


        // Prints the names and majors of students in a sample spreadsheet:
        String spreadsheetId = "1234";
        String range = SheetName+"!"+RowStart+":"+RowEnd;
        response = service.spreadsheets().values()
            .get(spreadsheetId, range).execute ();

        return response;

    }


    public static void setValue(String SheetName,String RowStart, String RowEnd) throws IOException{
      // Build a new authorized API client service.
      Sheets service = getSheetsService();
      // Prints the names and majors of students in a sample spreadsheet:
      String spreadsheetId = "1234";
      String range = RowStart+":"+RowEnd;

      List<List<Object>> arrData = getData();

      ValueRange oRange = new ValueRange();
      oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
      oRange.setValues(arrData);

      List<ValueRange> oList = new ArrayList<ValueRange>();
      oList.add(oRange);

      BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
      oRequest.setValueInputOption("RAW");
      oRequest.setData(oList);

      BatchUpdateValuesResponse oResp1 = service.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();

     // service.spreadsheets().values().update (spreadsheetId, range,) ;     
      //return request;

      }

    public static List<List<Object>> getData ()  {

      List<Object> data1 = new ArrayList<Object>();
      data1.add ("Ashwin");

      List<List<Object>> data = new ArrayList<List<Object>>();
      data.add (data1);

      return data;
    }

}
