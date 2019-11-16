package application;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;

public class MainController implements Initializable{
	
	@FXML
	public ListView<String> list;
	@FXML
	public Button clear;
	@FXML
	public Button delete;
	@FXML
	public Button plus2;
	@FXML
	public Button dnf;
	@FXML
	public Button ok;

	ObservableList<String> data = FXCollections.observableArrayList("17.83", "19.54", "20.55");

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		list.setItems(data);
		
	}
	
	public void clear()
	{
		list.getItems().clear();
	}
	
	public void delete()
	{
		int selectedIndex = list.getSelectionModel().getSelectedIndex();
		int newSelected;
		if(selectedIndex != -1)
		{
			if(selectedIndex == list.getItems().size() - 1)
			{
				newSelected = selectedIndex - 1;
			} else newSelected = selectedIndex;
			
			list.getItems().remove(selectedIndex);
			list.getSelectionModel().select(newSelected);
		}
	}
}
