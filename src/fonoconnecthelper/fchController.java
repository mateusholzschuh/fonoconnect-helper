/*
 * @FonoConnectHelper :: UIController
 * version: b11.4.18
 */
package fonoconnecthelper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author Mateus
 */
public class fchController implements Initializable {
    
    /*       UI - Elements      */ 
    
    //Words list - ListView
    @FXML
    ListView<String> wordsListView;
    
    //Console Log
    @FXML
    TextArea txtAreaConsole;
  
    //Word - Text
    @FXML
    Text word;
    
    //Phonems - Text
    @FXML
    Text phonemes;
    
    //Phonems - Combobox
    @FXML
    ComboBox<String> phonemesComboBox;
    
    //MenuControls
    @FXML
    HBox menuControls;
    
    //Save button
    @FXML
    Button saveButton;
    
    //Notification
    @FXML
    StackPane notification;
    
    @FXML
    Text notificationText;
    
    
    /*      Core      */
    List<Word>    wordsList;
    List<Phoneme> phonemesList;
    List<Phoneme> wordPhonemes;
    Boolean       isSaved;
    FonoConnectDBHelper dbh;
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        //Initialize the core
        wordsList    = new ArrayList<>();
        phonemesList = new ArrayList<>();
        wordPhonemes = new ArrayList<>();
        isSaved      = true;
        
        //Connect to the database
        dbh = new FonoConnectDBHelper("jdbc:sqlite:database.db");

        //Load the words from database
        wordsList = dbh.getWords();
        for(Word w : wordsList) {
            updateConsole("Palavra carregada: " + w.getName());
            wordsListView.getItems().add(w.getName());
            //Event handler
            wordsListView.setOnMouseClicked((MouseEvent event) -> {
                //Activate the menu
                if(!menuControls.isVisible() && !saveButton.isVisible()) {
                    menuControls.setVisible(true);
                    saveButton.setVisible(true);
                }
                //Before load the new word in screen, check if the last was saved
                if(!isSaved) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Deseja descartar as alterações?", ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Atenção");
                    alert.setHeaderText("Atenção!");
                    alert.showAndWait();
                    if(alert.getResult().equals(ButtonType.NO)) {
                        return;
                    }
                    else {
                        isSaved = true;
                    }
                }
                word.setText(wordsListView.getSelectionModel().getSelectedItem());
                wordPhonemes.clear();
                
                Word wf = null;
                for(Word ww : wordsList) {
                    if(ww.getName().equals(wordsListView.getSelectionModel().getSelectedItem())) {
                        wf = ww;
                        break;
                    }
                }
                if(wf != null)
                    wordPhonemes = dbh.getPhonemesInWord(wf.getId());
                
                updateWordPhonemes();
            });
        }

        //Load the phonemes from database to the list and combobox
        phonemesList = dbh.getPhonemes();
        for(Phoneme p : phonemesList) {
            updateConsole("Fonema carregado: " + p.getName());
            phonemesComboBox.getItems().add("/ " + p.getName() + " / " + " - " + p.getExample().replace("_", " "));
        }
        
        
    }    
    
    /*      UI - Functions      */
    
    //Add phoneme to word's list of phonemes
    @FXML
    public void addPhoneme() {
        //Get phoneme id from combobox
        Integer pID   = phonemesComboBox.getItems().indexOf(phonemesComboBox.getValue());
        
        //If the combobox is empty, do nothing
        if(pID == -1) {            
            return;
        }
        
        //Else, add the phoneme
        Phoneme phoneme = phonemesList.get( pID );
        wordPhonemes.add(phoneme);
        
        //Update the UI
        updateWordPhonemes();
        isSaved = false;
    }
    
    //Remove the last phoneme added
    @FXML
    public void removeLastPhoneme() {
        if(wordPhonemes.size() > 0)
            wordPhonemes.remove(wordPhonemes.size()-1);
        updateWordPhonemes();
        isSaved = false;
    }
    
    //Reset phonemes from list
    @FXML
    public void resetPhonemes() {
        wordPhonemes.clear();
        updateWordPhonemes();
        isSaved = false;
    }
    
    //Save current modifications
    @FXML
    public void saveModifications() {
        Integer wid = null;
        Integer pid;
        Boolean ok = false;
        
        for(Word w : wordsList) {
            if(w.getName().equals(word.getText())) {
                wid = w.getId();
                break;
            }
        }
        if(wid != null) {
            dbh.deletePhonemesInWord(wid);
            
            if(wordPhonemes.isEmpty()) {
                ok = true;
            }
            for(Phoneme p : wordPhonemes) {
                pid = p.getId();
                ok = dbh.saveWordPhoneme(wid, pid);
            }
        }
        if(ok) {
            handleNotification("Salvo com sucesso", !ok);
        }
        else {
            handleNotification("Erro ao salvar", !ok);
        }
        isSaved = ok;
    }

    
    /*      Other functions     */
    
    private void updateConsole(String log) {
        txtAreaConsole.setText(
                txtAreaConsole.getText() + log + "\n"
        );
    }
    
    private void updateWordPhonemes() {
        String str = "/ ";
        for (Phoneme p : wordPhonemes) {
            str = str.concat(p.getName() + " ");
        }
        str = str.concat("/");        
        phonemes.setText(str);
    }
    
    private void handleNotification(String str, boolean error) {
        notificationText.setText(str);
        
        if(error){
            notification.setStyle("-fx-background-color: #e44534;-fx-background-radius:25px;-fx-border-radius:25px;");
            notificationText.setFill(Color.DARKRED);
        }
        else {
            notification.setStyle("-fx-background-color: #90ee90;-fx-background-radius:25px;-fx-border-radius:25px;");
            notificationText.setFill(Color.DARKGREEN);
        }
        notification.setVisible(true);
        FadeTransition ft = new FadeTransition(new Duration(1000), notification);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
    
}
