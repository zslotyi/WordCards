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
    enum ValueInputOption {RAW}
    public void WriteToTable() throws IOException {
        // Build a new authorized API client service.
        
        //WordCards wordcards = new WordCards();
        
        Sheets service = getSheetsService();

        // Prints the names and majors of students in a sample spreadsheet:
        // https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
        String spreadsheetId = "12TpmJ5ULEbwfFUd3ZgNnHDMEQdIiJWZGOlgXwdTcPAc";
        String range = "Data!A2";
        
        ValueRange vr = new ValueRange();
        vr.setMajorDimension("ROWS");
        vr.setRange(range);
        ArrayList actualValue=new ArrayList();
        ArrayList outerList = new ArrayList();
        actualValue.add("asdf");
        outerList.add(actualValue);
        vr.setValues(outerList);
       
        response.setRange(range);
        //String URL = "https://sheets.googleapis.com/v4/spreadsheets/" + spreadsheetId + "/" + actualValue +"/DATA!A2?valueInputOption=USER_ENTERED";
        //HttpPut(URL);
        
        
        
        service.spreadsheets().values()
            .update(spreadsheetId, range, response).execute();
        
        //values = response.getValues();
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

        public void HttpPut(String URL_parameter) throws MalformedURLException, IOException {
        URL url = new URL(URL_parameter);
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setDoOutput(true);
        httpCon.setRequestMethod("PUT");
        OutputStreamWriter out = new OutputStreamWriter(
        httpCon.getOutputStream());
        out.write("Resource content");
        out.close();
        httpCon.getInputStream();
    }
    void WriteExample2() throws IOException {
        Sheets service = getSheetsService();
        List<Request> requests = new ArrayList<>();

            // Change the name of sheet ID '0' (the default first sheet on every
            // spreadsheet)
           /* requests.add(new Request()
                    .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                            .setProperties(new SheetProperties()
                                    .setSheetId(0)
                                    .setTitle("New Sheet Name"))
                            .setFields("title")));*/

            // Insert the values 1, 2, 3 into the first row of the spreadsheet with a
            // different background color in each.
            List<CellData> values = new ArrayList<>();
            /*values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setNumberValue(Double.valueOf(1)))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setRed(Float.valueOf(1)))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setNumberValue(Double.valueOf(2)))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setBlue(Float.valueOf(1)))));
            values.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setNumberValue(Double.valueOf(3)))
                    .setUserEnteredFormat(new CellFormat()
                            .setBackgroundColor(new Color()
                                    .setGreen(Float.valueOf(1)))));*/
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

            // Write "=A1+1" into A2 and fill the formula across A2:C5 (so B2 is
            // "=B1+1", C2 is "=C1+1", A3 is "=A2+1", etc..)
           /* requests.add(new Request()
                    .setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(0)
                                    .setStartRowIndex(1)
                                    .setEndRowIndex(6)
                                    .setStartColumnIndex(0)
                                    .setEndColumnIndex(3))
                            .setCell(new CellData()
                                    .setUserEnteredValue(new ExtendedValue()
                                            .setFormulaValue("=A1 + 1")))
                            .setFields("userEnteredValue")));

            // Copy the format from A1:C1 and paste it into A2:C5, so the data in
            // each column has the same background.
            requests.add(new Request()
                    .setCopyPaste(new CopyPasteRequest()
                            .setSource(new GridRange()
                                    .setSheetId(0)
                                    .setStartRowIndex(0)
                                    .setEndRowIndex(1)
                                    .setStartColumnIndex(0)
                                    .setEndColumnIndex(3))
                            .setDestination(new GridRange()
                                    .setSheetId(0)
                                    .setStartRowIndex(1)
                                    .setEndRowIndex(6)
                                    .setStartColumnIndex(0)
                                    .setEndColumnIndex(3))
                            .setPasteType("PASTE_FORMAT")));*/

            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest)
                    .execute();
    }

}