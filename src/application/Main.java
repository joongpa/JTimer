package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;


public class Main extends Application {
	
	public static Stage primaryStage;
	public static Stage timeStage;
	
	@Override
	public void start(Stage newStage) {
		try {
			primaryStage = newStage;
			Parent root = FXMLLoader.load(getClass().getResource("/application/Scene.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Jtimer Pre-alpha");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			//popupInit();
			

			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
