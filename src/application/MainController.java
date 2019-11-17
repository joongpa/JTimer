/*
 * To-do list:
 * - Timer start delay
 * - Change ListView to TableView
 * - Style TableView to have no visible column separators
 * - Style the solve number column to be a lighter text color for readability
 * - Add functionality to OK, +2, and DNF buttons
 * - Compute means and averages and display in the TableView
 * - *Scramble generation*
 * - 
*/
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MainController implements Initializable{
	
	@FXML private Parent root;
	@FXML private Label timerLabel;
	@FXML private TableView<Solve> timeList;
	@FXML private TableColumn<Solve, Integer> solveNumber;
	@FXML private TableColumn<Solve, String> displayedTime;
	@FXML private TableColumn<Solve, String> mo3;

	private Timeline timeline;
	private int count;
	
	//hotkeys
	private KeyCombination spaceCombo = new KeyCodeCombination(KeyCode.SPACE);
	private KeyCombination clearCombo = new KeyCodeCombination(KeyCode.D, KeyCodeCombination.ALT_DOWN);
	private KeyCombination deleteCombo = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.ALT_DOWN);
	private KeyCombination okCombo = new KeyCodeCombination(KeyCode.DIGIT1, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination plus2Combo = new KeyCodeCombination(KeyCode.DIGIT2, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination dnfCombo = new KeyCodeCombination(KeyCode.DIGIT3, KeyCodeCombination.CONTROL_DOWN);
	
	Stopwatch st = new Stopwatch();
	Stopwatch delay = new Stopwatch();
	private boolean ready = false;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		solveNumber.setCellValueFactory(new PropertyValueFactory<Solve, Integer>("solveNumber"));
		displayedTime.setCellValueFactory(new PropertyValueFactory<Solve, String>("displayedTime"));
		mo3.setCellValueFactory(new PropertyValueFactory<Solve, String>("mo3"));
		
		count = 0;
		timerLabel.textProperty().set("0.00");
		
		//handles timer stops
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(spaceCombo.match(e))
			{
				count++;
				if(count == 1) delay.start();
				
				if(st.inProgress) //when user presses spacebar to stop the timer
				{
					if(count == 1)
					{
						double finalTime = st.getTime();
						timeline.stop();
						timerLabel.textProperty().set(Stopwatch.formatTime(finalTime));
						
					/*	int size = timeList.getItems().size();
						double mean;
						try
						{
							double sum = 0;
							for(int i = size - 1; i > size - 3; i--)
							{
								sum += timeList.getItems().get(i).getRealTime();
							}
							sum += finalTime;
							mean = sum / 3;
						} catch(IndexOutOfBoundsException error) {
							mean = -1;
						}*/ //basically wtf do I do to get live averages to work
						timeList.getItems().add(new Solve(timeList.getItems().size() + 1, finalTime, mean));
						timeList.getSelectionModel().select(timeList.getItems().size() - 1);
					}
				} else {
					if(delay.getTime() > 0.3)
					{
						timerLabel.textProperty().set("0.00");
						timerLabel.setTextFill(Color.web("#00DD00"));
						ready = true;
					}
				}
				e.consume();
			}
		});
		
		//handles timer starts
		root.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			if(spaceCombo.match(e)) 
			{
				timerLabel.setTextFill(Color.web("#000000"));
				count = 0;
				if(!st.inProgress) //When timer is inactive
				{
					if(ready)
					{
						ready = false;
						st.start();
						timeline = new Timeline(
							new KeyFrame(Duration.millis(10), event -> { 
								timerLabel.textProperty().set(st.getTimeAsString());
						}));
						timeline.setCycleCount(Timeline.INDEFINITE);
						timeline.play();
					}
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
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(okCombo.match(e)) ok();
		});
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(plus2Combo.match(e)) plus2();
		});
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(dnfCombo.match(e)) dnf();
		});
		
	}
	
	public void clear()
	{
		timeList.getItems().clear();
	}
	
	public void delete()
	{
		int selectedIndex = timeList.getSelectionModel().getSelectedIndex();
		int newSelected;
		if(selectedIndex != -1)
		{
			if(selectedIndex == timeList.getItems().size() - 1)
			{
				newSelected = selectedIndex - 1;
			} else newSelected = selectedIndex;
			
			timeList.getItems().remove(selectedIndex);
			timeList.getSelectionModel().select(newSelected);
			
			for(int i = newSelected; i < timeList.getItems().size(); i++)
			{
				int currentNum = timeList.getItems().get(i).getSolveNumber();
				timeList.getItems().get(i).setSolveNumber(currentNum - 1);
			}
		}
	}
	
	public void ok()
	{
		
	}
	
	public void plus2()
	{

	}
	
	public void dnf()
	{

	}
}
