package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import db.DbException;
import gui.utils.Alerts;
import gui.utils.ViewUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.Word;
import model.services.MeaningService;
import model.services.WordService;

public class WordsViewController implements Initializable {

    private WordService wordService;
    
    @FXML
    private TextField textFieldSearch;

    @FXML
    private TableView<Word> tableViewWords;

    @FXML
    private TableColumn<Word, String> tableColumnWord;

    @FXML
    private TableColumn<Word, Integer> tableColumnLearned;

    @FXML
    private TableColumn<Word, String> tableColumnMeanings;
    
    private ObservableList<Word> obsList;
    private FilteredList<Word> filteredData;

    public void setWordService(WordService wordService) {
        this.wordService = wordService;
    }

    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnWord.setCellValueFactory(new PropertyValueFactory<>("word")); 
        tableColumnLearned.setCellValueFactory(new PropertyValueFactory<>("learned")); 

        tableColumnLearned.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item == 0 ? "Not Learned" : "Learned");
                }
            }
        });
        
        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(word -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (word.getWord().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } 

                return false;
            });
        });
        
        tableColumnMeanings.setCellFactory(column -> new TableCell<>() {

            private final Button showButton = new Button("Show");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Word word = getTableView().getItems().get(getIndex());
                    setGraphic(showButton);

                    showButton.setOnAction(event -> {            
                        onShowMeanings(word); 
                    });
                }
            }
        });
    }

    public void updateTableView() {
    	try {
	        List<Word> words = wordService.findAllWords();
	        words.forEach(word -> word.setWord(StringUtils.capitalize(word.getWord())));
	        obsList = FXCollections.observableArrayList(words);
	        
	        filteredData = new FilteredList<>(obsList, p -> true);
	        
	        tableViewWords.setItems(filteredData);
    	} catch (DbException e) {
	    	Alerts.showAlert("Erro Banco de dados", e.getMessage(), e.getMessage(), AlertType.ERROR);
		}
    }

    private void onShowMeanings(Word word) {
    	ViewUtils.loadView(MainViewController.class, "/gui/MeaningsView.fxml", (MeaningsViewController controller) -> {
			controller.setMeaningService(new MeaningService());
			controller.setLabelWordText(word.getWord());
			controller.updateTableView(word.getId());
		});
    }
}