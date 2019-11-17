package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

public class MainController implements Initializable{
	
	@FXML
	public Parent root;
	
	@FXML
	public ListView<String> list;
	public Label timerLabel;

	private Timeline timeline;
	private StringProperty time = new SimpleStringProperty();
	private int count;
	
	//hotkeys
	private KeyCombination spaceCombo = new KeyCodeCombination(KeyCode.SPACE);
	private KeyCombination clearCombo = new KeyCodeCombination(KeyCode.D, KeyCodeCombination.ALT_DOWN);
	private KeyCombination deleteCombo = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.ALT_DOWN);
	private KeyCombination okCombo = new KeyCodeCombination(KeyCode.DIGIT1, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination plus2Combo = new KeyCodeCombination(KeyCode.DIGIT2, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination dnfCombo = new KeyCodeCombination(KeyCode.DIGIT3, KeyCodeCombination.CONTROL_DOWN);
	
	Stopwatch st = new Stopwatch();

	@Override
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		count = 0;
		time.setValue("0.00");
		timerLabel.textProperty().bind(time);
		
		//handles timer stops
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(spaceCombo.match(e))
			{
				count++;
				if(st.inProgress) //when user presses spacebar to stop the timer
				{
					if(count == 1)
					{
						timeline.stop();
						list.getItems().add((list.getItems().size() + 1) + " " + timerLabel.getText());
						list.getSelectionModel().select(list.getItems().size() - 1);
					}
				} else {
					time.setValue("0.00");
				}
				e.consume();
			}
		});
		
		//handles timer starts
		root.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			if(spaceCombo.match(e)) 
			{
				count = 0;
				if(!st.inProgress) //When timer is inactive
				{
					st.start();
					timeline = new Timeline(
						new KeyFrame(Duration.millis(50), event -> { 
							time.setValue(st.getTimeAsString());
					}));
					timeline.setCycleCount(Timeline.INDEFINITE);
					timeline.play();
				} else {
					st.inProgress = false;
				}
				e.consume();
			}
		});
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(clearCombo.match(e)) clear();
		});
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(deleteCombo.match(e)) delete();
		});
		
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
	
	public void ok()
	{
		
	}
	
	public void plus2()
	{
		list.getItems().set(list.getSelectionModel().getSelectedIndex(), list.getItems().get(list.getSelectionModel().getSelectedIndex()) + " +2");
	}
}
