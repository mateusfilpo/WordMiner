package gui.utils;

import java.io.IOException;
import java.util.function.Consumer;

import application.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

public class ViewUtils {
	
	public static <T> void loadView(Class<?> controllerClass, String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(controllerClass.getResource(absoluteName));
			AnchorPane newAnchorPane = loader.load();
			
			Scene mainScene = Main.getMainScene();
			AnchorPane mainAnchorPane = (AnchorPane) mainScene.getRoot();
			
			Node mainMenu = mainAnchorPane.getChildren().get(0);
			mainAnchorPane.getChildren().clear();
			
			mainAnchorPane.getChildren().add(mainMenu);
			mainAnchorPane.getChildren().addAll(newAnchorPane.getChildren());
			
			T controller = loader.getController();
			initializingAction.accept(controller);
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
		
	}

}
