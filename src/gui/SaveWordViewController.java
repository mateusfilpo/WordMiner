package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.utils.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.services.MeaningService;
import model.services.WordService;

public class SaveWordViewController implements Initializable {

	private WordService wordService;
	private MeaningService meaningService;

	@FXML
	private TextField textFieldWord;

	@FXML
	private TextField textFieldMeaning;

	@FXML
	private Button btSaveWord;

	@FXML
	private Label savedWord;

	@FXML
	private void onBtSaveWordAction() {
		String currentWord = textFieldWord.getText();
		try {
			Long wordId = wordService.saveWord(currentWord);
			meaningService.saveMeaning(textFieldMeaning.getText(), wordId);
	
			textFieldWord.setText("");
			textFieldMeaning.setText("");
	
			if (wordId != null) {
				savedWord.setText("\"" + currentWord.trim() + "\" Was Saved.");
			}
		} catch (DbException e) {
	    	Alerts.showAlert("Erro Banco de dados", e.getMessage(), e.getMessage(), AlertType.ERROR);
		}
	}

	public void setWordService(WordService wordService) {
		this.wordService = wordService;
	}

	public void setMeaningService(MeaningService meaningService) {
		this.meaningService = meaningService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Platform.runLater(() -> btSaveWord.requestFocus());

		Main.getMainScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				onBtSaveWordAction();
				event.consume();
			}
		});
	}

}
