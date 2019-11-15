package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class MainController implements Initializable{
	
	@FXML
	public ListView<String> list;
	
	ObservableList<String> data = FXCollections.observableArrayList("17.83", "19.54", "20.55");

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		list.setItems(data);
	}
	
	

}
