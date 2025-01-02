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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.Meaning;
import model.services.MeaningService;
import model.services.WordService;

public class MeaningsViewController implements Initializable {

	private MeaningService meaningService;
	
	@FXML
	private Label labelWord;
	
	@FXML
    private TableView<Meaning> tableViewMeanings;

    @FXML
    private TableColumn<Meaning, String> tableColumnMeaning;

    @FXML
    private TableColumn<Meaning, Integer> tableColumnLearned;
    
    @FXML
    private Button buttonReturn;
    
    @FXML
    private void onButtonReturnAction() {
    	ViewUtils.loadView(MainViewController.class, "/gui/WordsView.fxml", (WordsViewController controller) -> {
			controller.setWordService(new WordService());
			controller.updateTableView();
		});
    }

    
    private ObservableList<Meaning> obsList;

	public void setMeaningService(MeaningService meaningService) {
		this.meaningService = meaningService;
	}
	
	public void setLabelWordText(String word) {
		labelWord.setText(word);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
        tableColumnMeaning.setCellValueFactory(new PropertyValueFactory<>("meaning")); 
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
	
	}
	
	public void updateTableView(Long wordId) {
		try {
	        List<Meaning> meanings = meaningService.findAllMeaningsByWordId(wordId);
	        meanings.forEach(meaning -> meaning.setMeaning(StringUtils.capitalize(meaning.getMeaning())));
	        obsList = FXCollections.observableArrayList(meanings);
	        tableViewMeanings.setItems(obsList);
		} catch (DbException e) {
	    	Alerts.showAlert("Erro Banco de dados", e.getMessage(), e.getMessage(), AlertType.ERROR);
		}
    }
	
	

}
