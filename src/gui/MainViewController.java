package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.utils.Alerts;
import gui.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.entities.Meaning;
import model.entities.Word;
import model.services.MeaningService;
import model.services.WordService;

public class MainViewController implements Initializable {
	
	private String currentTab = "MineTab";
	
	private WordService wordService;
	private MeaningService meaningService;

	@FXML
	private MenuItem menuItemMine;
	
	@FXML
	private MenuItem menuItemSaveWord;
	
	@FXML
	private MenuItem menuItemWords;
	
	@FXML
	private MenuItem menuItemHelp;
	
	@FXML 
	private TextField textFieldMinedWord;
	
	@FXML 
	private TextField textFieldMinedMeaning;
	
	@FXML
	private Button btMine;
	
	@FXML
	public void onMenuItemMineAction() {
		ViewUtils.loadView(MainViewController.class, "/gui/MainView.fxml", (MainViewController controller) -> {
			currentTab = "MineTab";
			controller.setWordService(new WordService());
			controller.setMeaningService(new MeaningService());
			Main.getMainScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
				if (event.getCode() == KeyCode.ENTER) {
					controller.onBtMineAction();
					event.consume();
				}
			});
		});
	}
	
	@FXML
	public void onMenuItemSaveWordAction() {
		ViewUtils.loadView(MainViewController.class, "/gui/SaveWordView.fxml", (SaveWordViewController controller) -> {
			currentTab = "SaveWordTab";
			controller.setWordService(new WordService());
			controller.setMeaningService(new MeaningService());
		});
	}
	
	@FXML
	public void onMenuItemWordsAction() {
		ViewUtils.loadView(MainViewController.class, "/gui/WordsView.fxml", (WordsViewController controller) -> {
			currentTab = "WordsTab";
			controller.setWordService(new WordService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemHelpAction() {
		ViewUtils.loadView(MainViewController.class, "/gui/HelpView.fxml", x -> {
			currentTab = "HelpTab";
		});
	}
	
	@FXML
	public void onBtMineAction() {
		String wordText = textFieldMinedWord.getText();
	    String meaningText = textFieldMinedMeaning.getText();
	    
	    try {
		    if (!wordText.isEmpty() && !meaningText.isEmpty()) {
		    	Long wordId = wordService.findWordIdByWord(wordText);
		    	
		    	wordService.markAsLearned(wordText, meaningText);
		        meaningService.markAsLearned(meaningText, wordId);
			}
			
			Word word = wordService.fetchWord();
			Meaning meaning = meaningService.fetchMeaning(word.getId());
		
			textFieldMinedWord.setText(word.getWord());
			textFieldMinedMeaning.setText(meaning.getMeaning());
			
			String textToCopy = word.getWord() + " (" + meaning.getMeaning() + ")";
			copyToClipboard(textToCopy);
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
		textFieldMinedWord.setEditable(false);
		textFieldMinedWord.setFocusTraversable(false);
		
		textFieldMinedMeaning.setEditable(false);
		textFieldMinedMeaning.setFocusTraversable(false);
	}
	
	
	private void copyToClipboard(String text) {	
		if (text.contains("No meanings")) {
			return;
		}
		
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
	
	public String getCurrentTab() {
		return currentTab;
	}
	
	public void SetCurrentTab(String currentTab) {
		this.currentTab = currentTab;
	}

}
