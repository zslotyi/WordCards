import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SheetsQuickstart {
    
    List<List<Object>> values;
    private HashMap<Integer, List> validWords;
    private ArrayList<Integer> validKeys;
    ValueRange response;
    String spreadsheetId;
    
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Sheets API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart.json");

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
     * at ~/.credentials/sheets.googleapis.com-java-quickstart.json
     */
    private static final List<String> SCOPES =
        Arrays.asList(SheetsScopes.SPREADSHEETS);

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
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            SheetsQuickstart.class.getResourceAsStream("/client_secret.json");
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

    public SheetsQuickstart() throws IOException {
        this.spreadsheetId = "12TpmJ5ULEbwfFUd3ZgNnHDMEQdIiJWZGOlgXwdTcPAc";
        // Build a new authorized API client service.
        
        //WordCards wordcards = new WordCards();
        
        Sheets service = getSheetsService();

        // Prints the names and majors of students in a sample spreadsheet:
        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
        String spreadsheetId = "12TpmJ5ULEbwfFUd3ZgNnHDMEQdIiJWZGOlgXwdTcPAc";
        String range = "Sheet1!A2:D";
        response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        values = response.getValues();
        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
          System.out.println("Actual, Target");
         /* for (List row : values) {
            // Print columns A and E, which correspond to indices 0 and 4.
            try {
            System.out.printf("%s, %s\n", row.get(0), row.get(1));
            }
            catch (IndexOutOfBoundsException e) {
                System.out.println("Itt most valami üres");
            }
          }*/
         
         System.out.println("A 202.sor: ");
                 List row202 = values.get(200);
                 System.out.printf("%s, %s\n", row202.get(0), row202.get(1));
        }
    }
    List printRow (int n) {
        List row = values.get(n-2);
        return row;
    }
    /**
     * 
     * @param k
     * @return number of rows of the get(k) column
     * 
     * this method will go through the k column, and check how many values are not null
     * it will return the number of valid values it finds
     */
    int countTheRows (int k) {
        validWords = new HashMap<>();
        validKeys = new ArrayList<>();
        int e=0;
        for (List row : values) {
            if (row.get(k) != "") {
                storeValidWords(e,row);
                e++;
            }
        }
        return e;
    }
    private void storeValidWords(int e, List row) {
        validWords.put(e, row);
        validKeys.add(e);
    }
    List getRandomWord() {
        if (validWords.isEmpty() || validKeys.isEmpty())
        {
            throw new AssertionError("The valid words and valid keys must not be empty when calling a random word!");
        }
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(validKeys.size());
        Integer key = validKeys.get(index);
            if (!(key instanceof Integer))
            {
                throw new AssertionError ("The retrieved key must be of Integer type!");
            }
            return validWords.get(key);
   }
    
    /**
     * This is for writing only
     */
    void WriteExample2() throws IOException {
        Sheets service = getSheetsService();
        List<Request> requests = new ArrayList<>();

               List<CellData> values = new ArrayList<>();
               values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue("Hello World!")));
            requests.add(new Request()
                    .setUpdateCells(new UpdateCellsRequest()
                            .setStart(new GridCoordinate()
                                    .setSheetId(895100899)
                                    .setRowIndex(0)
                                    .setColumnIndex(0))
                            .setRows(Arrays.asList(
                                    new RowData().setValues(values)))
                            .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

         BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
                    .execute();
    }
      void WriteToCell(int row, int column, String value) throws IOException {
        Sheets service = getSheetsService();
        List<Request> requests = new ArrayList<>();

               List<CellData> values = new ArrayList<>();
               values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(value)));
            requests.add(new Request()
                    .setUpdateCells(new UpdateCellsRequest()
                            .setStart(new GridCoordinate()
                                    .setSheetId(895100899)
                                    .setRowIndex(row)
                                    .setColumnIndex(column))
                            .setRows(Arrays.asList(
                                    new RowData().setValues(values)))
                            .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

         BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
                    .execute();
    }
    void WriteToCell(int row, int column, int value) throws IOException {
        Sheets service = getSheetsService();
        List<Request> requests = new ArrayList<>();

               List<CellData> values = new ArrayList<>();
               values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setNumberValue((double)value)));
            requests.add(new Request()
                    .setUpdateCells(new UpdateCellsRequest()
                            .setStart(new GridCoordinate()
                                    .setSheetId(895100899)
                                    .setRowIndex(row)
                                    .setColumnIndex(column))
                            .setRows(Arrays.asList(
                                    new RowData().setValues(values)))
                            .setFields("userEnteredValue,userEnteredFormat.backgroundColor")));

         BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
                    .execute();
    }
    
    String getCellValue(String range) throws IOException {
        Sheets service = getSheetsService();
        response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        values = response.getValues();
        List valuesList = values.get(0);
        return valuesList.get(0).toString();
    }

}