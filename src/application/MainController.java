/*
 * To-do list:
 * - Make time list selectable and display averages
 * - Show best statistics in lower left corner
 * - *Scramble generation*
 * - 
*/
package application;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class MainController implements Initializable{
	
	@FXML private Parent root;
	@FXML private Label timerLabel;
	@FXML private TableView<Solve> timeList;
	@FXML private TableColumn<Solve, Integer> solveNumber;
	@FXML private TableColumn<Solve, String> displayedTime;
	@FXML private TableColumn<Solve, Solve> mo3;
	@FXML private TableColumn<Solve, Solve> ao5;
	@FXML private TableColumn<Solve, Solve> ao12;

	//private Timeline timeline;
	private int keyPressCount = 0;
	
	//hotkeys
	private KeyCombination spaceCombo = new KeyCodeCombination(KeyCode.SPACE);
	private KeyCombination clearCombo = new KeyCodeCombination(KeyCode.D, KeyCodeCombination.ALT_DOWN);
	private KeyCombination deleteCombo = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.ALT_DOWN);
	private KeyCombination okCombo = new KeyCodeCombination(KeyCode.DIGIT1, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination plus2Combo = new KeyCodeCombination(KeyCode.DIGIT2, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination dnfCombo = new KeyCodeCombination(KeyCode.DIGIT3, KeyCodeCombination.CONTROL_DOWN);
	
	Stopwatch st = new Stopwatch();
	SimpleTimer delay = new SimpleTimer();
	private boolean ready = false;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		solveNumber.setCellValueFactory(new PropertyValueFactory<Solve, Integer>("solveNumber"));
		displayedTime.setCellValueFactory(new PropertyValueFactory<Solve, String>("displayedTime"));
		mo3.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		ao5.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		ao12.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		
		solveNumber.setSortable(false);
		displayedTime.setSortable(false);
		mo3.setSortable(false);
		ao5.setSortable(false);
		ao12.setSortable(false);
		
		displayedTime.setCellFactory(col -> {
			return new TableCell<Solve, String>() {
				
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					
					if(item == null || empty) {
						setText(null);
					}
					else {
						setText(item);
						if(item.equals("DNF")) setTextFill(Color.RED);
						else if(item.contains("+")) setTextFill(Color.ORANGE);
						else setTextFill(Color.BLACK);
					}
				}
			};
		});
		
		mo3.setCellFactory(col -> {
			return new TableCell<Solve, Solve>() {
				
				@Override
				protected void updateItem(Solve item, boolean empty) {
					super.updateItem(item, empty);
					if(item == null || empty) {
						setText(null);
					}
					else {
						String text = getFormattedAverage(item, 3, false);
						setText(text);
						setTextFill(text.equals("DNF") ? Color.RED : Color.BLACK);
					}
				}
			};
		});
		
		ao5.setCellFactory(col -> {
			return new TableCell<Solve, Solve>() {
				
				@Override
				protected void updateItem(Solve item, boolean empty) {
					super.updateItem(item, empty);
					if(item == null || empty) {
						setText(null);
					}
					else {
						String text = getFormattedAverage(item, 5, true);
						setText(text);
						setTextFill(text.equals("DNF") ? Color.RED : Color.BLACK);
					}
				}
			};
		});
		
		ao12.setCellFactory(col -> {
			return new TableCell<Solve, Solve>() {
				
				@Override
				protected void updateItem(Solve item, boolean empty) {
					super.updateItem(item, empty);
					if(item == null || empty) {
						setText(null);
					}
					else {
						String text = getFormattedAverage(item, 12, true);
						setText(text);
						setTextFill(text.equals("DNF") ? Color.RED : Color.BLACK);
					}
				}
			};
		});
		
		timerLabel.textProperty().bind(st.formattedTimeProperty);
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(spaceCombo.match(e))
			{
				keyPressCount++;
				if(keyPressCount == 1)
				{
					if(st.inSolvingPhase())
					{
						st.pause();
						int largestIndex = timeList.getItems().size();
						timeList.getItems().add(new Solve(largestIndex + 1, st.getTime()));
						timeList.getSelectionModel().select(largestIndex);
						timeList.scrollTo(largestIndex);
					} else {
						delay.start();
					}
				} else if(delay.getTime() > 0.3 && !st.inSolvingPhase()){
					st.reset();
					timerLabel.setTextFill(Color.web("#00DD00"));
					ready = true;
				}
			}
			e.consume();
		});
		
		root.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			if(spaceCombo.match(e)) 
			{
				timerLabel.setTextFill(Color.web("#000000"));
				keyPressCount = 0;
				
				if(!st.inSolvingPhase())
				{
					if(ready)
					{
						st.start();
						ready = false;
					}
				} else {
					st.stop();
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
			
			for(int i = selectedIndex; i < timeList.getItems().size(); i++)
			{
				int currentNum = timeList.getItems().get(i).getSolveNumber();
				timeList.getItems().get(i).setSolveNumber(currentNum - 1);
			}
		}
	}
	
	public void ok()
	{
		try {
			timeList.getSelectionModel().getSelectedItem().solveStateProperty.set(SolveState.OK);
			timeList.refresh();
		} catch(NullPointerException e) {}
	}
	
	public void plus2()
	{
		try {
			timeList.getSelectionModel().getSelectedItem().solveStateProperty.set(SolveState.PLUS2);
			timeList.refresh();
		} catch(NullPointerException e) {}
	}
	
	public void dnf()
	{
		try {
			timeList.getSelectionModel().getSelectedItem().solveStateProperty.set(SolveState.DNF);
			timeList.refresh();
		} catch(NullPointerException e) {}
	}
	
	private double getAverage(Solve item, int numSolves, boolean isAverage)
	{
		try {
			int startIndex = IntStream.range(0, timeList.getItems().size())
								.filter(i -> item.equals(timeList.getItems().get(i)))
								.findFirst()
								.orElse(-1)
								- numSolves + 1;
			double sum = 0;
			Solve[] solves = new Solve[numSolves];
			for(int i = 0; i < solves.length; i++) solves[i] = timeList.getItems().get(i + startIndex);
			Arrays.sort(solves);
			int uncountedSolves = 0;
			if(isAverage) uncountedSolves = (numSolves <= 10) ? 1 : (int)Math.round(numSolves * 0.05);
			
			for(int i = uncountedSolves; i < solves.length - uncountedSolves; i++)
			{
				
				if(solves[i].solveStateProperty.get() == SolveState.OK)
					sum += solves[i].getRealTime();
				else if(solves[i].solveStateProperty.get() == SolveState.PLUS2)
					sum += solves[i].getRealTime() + 2;
				else return -2;
			}
			double mean = sum / (numSolves - (2 * uncountedSolves));
			return(mean);
		} catch(IndexOutOfBoundsException e) {
			return -1;
		}
	}
	
	private String getFormattedAverage(Solve item, int numSolves, boolean isAverage)
	{
		double value = getAverage(item, numSolves, isAverage);
		if(value == -1) return "-";
		else if(value == -2) return "DNF";
		else return Stopwatch.formatTime(value);
	}
}
