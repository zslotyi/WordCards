/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
private Label numberOfWords;
    
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
        
        setUpBoard();
        
        primaryStage.show();
        
          
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
    private void setUpBoard(){
        numberOfWords = new Label(" " + dictionary.countTheRows(0));
        scoreNode.add(numberOfWords, 0, 0);
        
    }
    private void importCSS(){
        String css = this.getClass().getResource("wordcards.css").toExternalForm();
        primaryScene.getStylesheets().add(css);
    }
    
    private void setGameOver(){
        
    }
}
