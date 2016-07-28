/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @date: 2016. 07. 15. 10:19
 * @author Zsolt Balla
 * 
 */
public class WordCards extends Application {
private GridPane rootNode, wordNode, scoreNode;
private Scene primaryScene;
private SheetsQuickstart dictionary;
private Label feedback, numberOfWords, questionWord, numberOfAttempts, numberOfAttemptsNow, numberOfSuccess, numberOfSuccessNow, numberOfFailure, numberOfFailureNow, successRate, successRateNow;
private TextField answerWord;
private Button submitButton, helpButton;
private double allAttempts, successAttempts, failureAttempts, allAttemptsToday, successAttemptsToday, failureAttemptsToday;
private double allSuccessRate, successRateToday;
private String correctAnswer;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
   
        primaryStage.setTitle("Flashcard application from Google Spreadsheet");
        rootNode = new GridPane();
        wordNode = new GridPane();
        scoreNode = new GridPane();
        
        rootNode.add(wordNode,1,1);
        rootNode.add(scoreNode,1,2);
        
        primaryScene = new Scene(rootNode, 600, 700);
        importCSS();
        primaryStage.setScene(primaryScene);
        
        dictionary = new SheetsQuickstart();
        
        setUpWordBoard();
        setUpScoreBoard();
        
        
        
        primaryStage.show();
        askAWord();
        
          
         primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                try {
                    setGameOver();
                    Platform.exit();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        
    }
    public static void main (String[] args) {
        Application.launch(args);
    }
    /**
     * This method will draw up the word field of the game board:
     * a Label for the question word, a text input field for the answer, a Help
     * and a Submit Button
     */
    private void setUpWordBoard() {
        questionWord = new Label("Placeholder");
        answerWord = new TextField("Your answer here");
        
     answerWord.setOnKeyPressed((event) -> { 
         if(event.getCode() == KeyCode.ENTER) { checkAnswer(); } });
        
        submitButton = new Button("Submit");
            submitButton.setOnAction((al)->checkAnswer());
        helpButton = new Button("Help");
            helpButton.setOnAction((al)->helpWithAnswer());
        
        feedback = new Label ("");
        wordNode.add(questionWord, 0,0);
        wordNode.add(answerWord, 1,0);
        wordNode.add(submitButton,0,1);
        wordNode.add(helpButton,1,1);
        wordNode.add(feedback,0,2);
        
    }
    private void setUpScoreBoard() throws IOException {
        numberOfWords = new Label(" " + dictionary.countTheRows(0));
        scoreNode.add(numberOfWords, 0, 0);
        
        allAttempts = Integer.parseInt(dictionary.getCellValue("Data!H2"));
        numberOfAttempts = new Label("Number of Attempts: " + (int)allAttempts);
        scoreNode.add(numberOfAttempts, 0, 1);
        
        successAttempts = Integer.parseInt(dictionary.getCellValue("Data!I2"));
        numberOfSuccess = new Label("Successful: " + (int)successAttempts);
        scoreNode.add(numberOfSuccess, 1, 1);
        
        failureAttempts = Integer.parseInt(dictionary.getCellValue("Data!J2"));
        numberOfFailure = new Label("Failed: " + (int)failureAttempts);
        scoreNode.add(numberOfFailure, 2, 1);
        
        allAttemptsToday = 0;
        successAttemptsToday=0;
        failureAttemptsToday=0;
        
        numberOfAttemptsNow = new Label("Today: " + (int)allAttemptsToday);
        scoreNode.add(numberOfAttemptsNow, 0, 2);
        
        numberOfSuccessNow = new Label(" " + (int)successAttemptsToday);
        scoreNode.add(numberOfSuccessNow, 1, 2);
        
        numberOfFailureNow = new Label(" " + (int)failureAttemptsToday);
        scoreNode.add(numberOfFailureNow, 2, 2);
        
        calculateSuccessRate();
        
        successRate = new Label("Success Rate: " + (int)allSuccessRate);
        scoreNode.add(successRate, 1, 0);
        
        successRateNow = new Label("Today " + (int)successRateToday);
        scoreNode.add(successRateNow, 2, 0);
                
    }
    private void updateScoreBoard() {
        calculateSuccessRate();
        numberOfAttemptsNow.setText("" +(int)allAttemptsToday);
        numberOfSuccessNow.setText("" +(int)successAttemptsToday);
        numberOfFailureNow.setText("" +(int)failureAttemptsToday);
        successRateNow.setText("" +(int)successRateToday);        
    }
    private void calculateSuccessRate() {
        if (allAttemptsToday!=0) {
        successRateToday = successAttemptsToday / allAttemptsToday *100;
        }
        else
        {
        successRateToday=0;
        }
        if (allAttempts!=0)
        {
        allSuccessRate = successAttempts/allAttempts*100;
        }
        else {
        allSuccessRate=0;
        }
    }
    private void askAWord(){
        List values = dictionary.getRandomWord();
        if (setDirection() == true)
        {
        questionWord.setText(values.get(0).toString());
        correctAnswer = values.get(3).toString();
        }
        else
        {
        questionWord.setText(values.get(3).toString());
        correctAnswer = values.get(0).toString();   
        }
    }
    private boolean setDirection() {
        Random r = new Random();
        return r.nextBoolean();
    }
    private void importCSS(){
        String css = this.getClass().getResource("wordcards.css").toExternalForm();
        primaryScene.getStylesheets().add(css);
    }
    private void checkAnswer(){
        
        //TODO: we have to nest it in an if with a checkbox to make accent removals optional
        
        String checkCorrect = flattenToAscii(correctAnswer);
        String checkYours = flattenToAscii(answerWord.getText());
        
        //feedback.setText("Checkyours: \"" + checkYours + "\" CheckCorrect: \"" + checkCorrect + "\"");
        
        if (checkYours.equals(checkCorrect)) //helyes megoldás
        {
            feedback.setText("Great Answer!");
            successAttemptsToday++;
        }
        else //hibás megoldás
        {
            feedback.setText("No! Your answer was \"" + checkYours + "\" and the correct answer is \"" + checkCorrect + "\"");
            failureAttemptsToday++;
        }
        
        answerWord.clear();
        allAttemptsToday++;
        updateScoreBoard();

        askAWord();
    }
    private String flattenToAscii(String string) {
    char[] out = new char[string.length()];
    String norm = Normalizer.normalize(string, Normalizer.Form.NFD);

    int j = 0;
    for (int i = 0, n = norm.length(); i < n; ++i) {
        char c = norm.charAt(i);
        int type = Character.getType(c);

        //Log.d(TAG,""+c);
        //by Ricardo, modified the character check for accents, ref: http://stackoverflow.com/a/5697575/689223
        if (type != Character.NON_SPACING_MARK){
            out[j] = c;
            j++;
        }
    }
    //Log.d(TAG,"normalized string:"+norm+"/"+new String(out));
    return new String(out);
}
    
    private String removeAccents(String strget)
    {
        
        String str = strget.replace("à", "a");
        str = str.replace("â", "a");
        str = str.replace("û", "u");
        str = str.replace("ù", "u");
        str = str.replace("è", "e");
        str = str.replace("ê", "e");
        str = str.replace("ë", "e");
        str = str.replace("ê", "e");
        str = str.replace("ï", "i");
        str = str.replace("î", "i");
        str = str.replace("ô", "ô");
        str = str.replace("œ", "oe");
        str = str.replace("æ", "ae");
        
        
        return str;
    }
    private void helpWithAnswer(){
        
    }
    private void setGameOver() throws IOException{
        dictionary.WriteToCell(1,7,(int)allAttemptsToday + (int)allAttempts); //overall attempts
        dictionary.WriteToCell(1,8, (int)successAttemptsToday + (int)successAttempts); //overall success
        dictionary.WriteToCell(1,9, (int)failureAttemptsToday + (int)failureAttempts); //overall failure
    }
}
