package application;
	
import java.io.IOException;

import db.DatabaseInitializer;
import db.DbException;
import gui.MainViewController;
import gui.utils.Alerts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.services.MeaningService;
import model.services.WordService;


public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			DatabaseInitializer.initializeDatabase();
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			Parent parent = loader.load();
			
			mainScene = new Scene(parent);
			primaryStage.setResizable(false);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("WordMiner");
			primaryStage.show();
			
			MainViewController mainViewController = loader.getController();
			mainViewController.setWordService(new WordService());
			mainViewController.setMeaningService(new MeaningService());
			
			mainScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
				if (mainViewController.getCurrentTab().equals("MineTab") && event.getCode() == KeyCode.ENTER) {
					mainViewController.onBtMineAction();
					event.consume();
				}
			});
		} catch(IOException e) {
			e.printStackTrace();
		} catch(DbException dbException) {
			Alerts.showAlert("Erro Banco de dados", dbException.getMessage(), dbException.getMessage(), AlertType.ERROR);
		}
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
