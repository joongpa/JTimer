/*
 * To-do list:
 * - Make time generated time list selectable
 * - Show best statistics in lower left corner
 * - Progress bar that shows how long spacebar must be held for
 * - <LAST PRIORITY> Stackmat input
*/
package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
import javafx.scene.text.Text;
import javafx.stage.Popup;
import java.io.File;
import java.io.FileNotFoundException;
//import net.gnehzr.tnoodle.puzzle.ThreeByThreeCubePuzzle;
//import net.gnehzr.tnoodle.scrambles.Puzzle;

public class MainController implements Initializable{
	
	//private Puzzle puzzle = new ThreeByThreeCubePuzzle();
	private Popup popup;
	
	@FXML private Text scrambleText;
	@FXML private Parent root;
	@FXML private Label timerLabel;
	@FXML private TableView<Solve> timeList;
	@FXML private TableColumn<Solve, Integer> solveNumber;
	@FXML private TableColumn<Solve, Solve> displayedTime;
	@FXML private TableColumn<Solve, Solve> mo3;
	@FXML private TableColumn<Solve, Solve> ao5;
	@FXML private TableColumn<Solve, Solve> ao12;
	@FXML private ChoiceBox<String> algCount;
	
	String[] options = {"Default", "7", "8", "9", "10", "11", "12", "13"};
	
	@FXML private Label accuracy;
	@FXML private Label bestTime;
	@FXML private Label bestMean;
	@FXML private Label bestAverage5;
	@FXML private Label bestAverage12;
	@FXML private Label mean;
	@FXML private Label average;
	

	//private Timeline timeline;
	private int keyPressCount = 0;
	
	//hotkeys
	private KeyCombination spaceCombo = new KeyCodeCombination(KeyCode.SPACE);
	private KeyCombination clearCombo = new KeyCodeCombination(KeyCode.D, KeyCodeCombination.ALT_DOWN);
	private KeyCombination deleteCombo = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.ALT_DOWN);
	private KeyCombination okCombo = new KeyCodeCombination(KeyCode.DIGIT1, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination plus2Combo = new KeyCodeCombination(KeyCode.DIGIT2, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination dnfCombo = new KeyCodeCombination(KeyCode.DIGIT3, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination upCombo = new KeyCodeCombination(KeyCode.UP);
	private KeyCombination downCombo = new KeyCodeCombination(KeyCode.DOWN);
	
	Stopwatch st = new Stopwatch();
	SimpleTimer delay = new SimpleTimer();
	private boolean ready = false;
	boolean lockout = false;


	ObservableList<Solve> dummyList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		algCount.getItems().addAll(FXCollections.observableArrayList(options));
		algCount.getSelectionModel().clearAndSelect(0);
		setScramble(scrambleText);
		dummyList = FXCollections.observableArrayList();
		//dummyList.add(new Solve(1, 0.05, ""));
		//dummyList.add(new Solve(2, 0.04, ""));
		//dummyList.add(new Solve(3, 0.04, ""));
		//for(int i = 0; i < 30000; i++) dummyList.add(new Solve(i+1,Math.random() * 10 + 120, ""));
		popup = newPopup();
		
		solveNumber.setCellValueFactory(new PropertyValueFactory<Solve, Integer>("solveNumber"));
		displayedTime.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		mo3.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		ao5.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		ao12.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		
		solveNumber.setSortable(false);
		displayedTime.setSortable(false);
		mo3.setSortable(false);
		ao5.setSortable(false);
		ao12.setSortable(false);
		
		solveNumber.setResizable(false);
		displayedTime.setResizable(false);
		mo3.setResizable(false);
		ao5.setResizable(false);
		ao12.setResizable(false);
		//timeList.setItems(dummyList);
		
		displayedTime.setCellFactory(col -> {
			final TableCell<Solve, Solve> cell = new ButtonCell() {

				@Override
				protected void updateItem(Solve item, boolean empty) {
					super.updateItem(item, empty);
					solveUpdate(this, item, empty);
				}
			};
			return cell;
		});
		
		mo3.setCellFactory(col -> {
			final TableCell<Solve, Solve> cell = new ButtonCell() {
				
				@Override
				protected void updateItem(Solve item, boolean empty) {
					Average average = new Average(item, timeList, 3, false, true);
					if(item != null && !empty) {
						item.mo3 = average;
					}
					super.updateItem(item, empty);
					avgUpdate(this, average, empty);
				}
			};
			return cell;
		});
		
		ao5.setCellFactory(col -> {
			final TableCell<Solve, Solve> cell = new ButtonCell() {
				
				@Override
				protected void updateItem(Solve item, boolean empty) {
					Average average = new Average(item, timeList, 5, true, true);
					if(item != null && !empty) item.ao5 = average;
					super.updateItem(item, empty);
					avgUpdate(this, average, empty);
				}
			};
			return cell;
		});
		
		ao12.setCellFactory(col -> {
			final TableCell<Solve, Solve> cell = new ButtonCell() {
				
				@Override
				protected void updateItem(Solve item, boolean empty) {
					Average average = new Average(item, timeList, 12, true, true);
					if(item != null && !empty) item.ao12 = average;
					super.updateItem(item, empty);
					avgUpdate(this, average, empty);
				}
			};
			return cell;
		});
		
		timerLabel.textProperty().bind(st.formattedTimeProperty);
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			
			if(st.inSolvingPhase())
			{
				keyPressCount++;
				if(keyPressCount == 1) {
					st.pause();
					int largestIndex = timeList.getItems().size();
					timeList.getItems().add(new Solve(largestIndex + 1, st.getTime(), scrambleText.getText()));
					timeList.getSelectionModel().select(largestIndex, solveNumber);
					timeList.scrollTo(largestIndex);
			        setScramble(scrambleText);
			        setSessionStats();
			        st.stop();
			        lockout = true;
				}
			} else if(spaceCombo.match(e)){
				keyPressCount++;
				if(keyPressCount == 1)
					delay.start();
				if(delay.getTime() > -10.0 && !lockout){
					st.reset();
					timerLabel.setTextFill(Color.web("#00DD00"));
					ready = true;
				}
			}
			/*if(spaceCombo.match(e))
			{
				keyPressCount++;
				if(keyPressCount == 1)
				{
					if(st.inSolvingPhase())
					{
						st.pause();
						int largestIndex = timeList.getItems().size();
						timeList.getItems().add(new Solve(largestIndex + 1, st.getTime(), scrambleText.getText()));
						timeList.getSelectionModel().select(largestIndex, solveNumber);
						timeList.scrollTo(largestIndex);
				        setScramble(scrambleText);
				        setSessionStats();
					} else {
						delay.start();
					}
				} else if(delay.getTime() > 0.3 && !st.inSolvingPhase()){
					st.reset();
					timerLabel.setTextFill(Color.web("#00DD00"));
					ready = true;
				}
			}*/
			e.consume();
		});
		
		root.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			keyPressCount = 0;
			lockout = false;
			if(spaceCombo.match(e)) 
			{
				//keyPressCount = 0;
				timerLabel.setTextFill(Color.web("#000000"));
				
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
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(upCombo.match(e))
			{
				try
				{
					timeList.getSelectionModel().select(timeList.getSelectionModel().getSelectedIndex() - 1);
					
				} catch (IndexOutOfBoundsException error) {}
			}
		});
		
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(downCombo.match(e))
			{
				try
				{
					timeList.getSelectionModel().select(timeList.getSelectionModel().getSelectedIndex() + 1);
				} catch (IndexOutOfBoundsException error) {}
			}
		});
	}
	
	public void clear()
	{
		timeList.getItems().clear();
		clearStats();
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
		setSessionStats();
	}
	
	public void ok()
	{
		try {
			timeList.getSelectionModel().getSelectedItem().solveStateProperty.set(SolveState.OK);
			timeList.refresh();
		} catch(NullPointerException e) {}
		setSessionStats();
	}
	
	public void plus2()
	{
		try {
			timeList.getSelectionModel().getSelectedItem().solveStateProperty.set(SolveState.PLUS2);
			timeList.refresh();
		} catch(NullPointerException e) {}
		setSessionStats();
	}
	
	public void dnf()
	{
		try {
			timeList.getSelectionModel().getSelectedItem().solveStateProperty.set(SolveState.DNF);
			timeList.refresh();
		} catch(NullPointerException e) {}
		setSessionStats();
	}
	
	public Popup newPopup()
	{
		Popup popup = new Popup();
		popup.setX(30);
		popup.setY(100);
		popup.setAutoHide(true);
		return popup;
	}
	
	public Label newTimeInfoLabel(String string)
	{
		Label timeInfoLabel = new Label(string);
		timeInfoLabel.setMinSize(700, 400);
		return timeInfoLabel;
	}
	
	private void avgUpdate(ButtonCell cell, Average item, boolean empty)
	{
		if(item == null || empty || cell.getItem() == null) {
			cell.setText(null);
			cell.setGraphic(null);
		}
		else {
			String text = item.getDisplayedTime();
			if(!text.equals("-")) {
				Button button = getAvgTimeButton(item);
				cell.button = button;
				cell.setGraphic(button);
			} else {
				cell.setGraphic(null);
			}
		}
	}
	
	public void solveUpdate(ButtonCell cell, Solve item, boolean empty) {
		if(item == null || empty || cell.getItem() == null) {
			cell.setText(null);
			cell.setGraphic(null);
		}
		else {
			String text = item.getDisplayedTime();
			if(!text.equals("-")) {
				Button button = getSolveTimeButton(item);
				cell.button = button;
				cell.setGraphic(button);
			} else {
				cell.setGraphic(null);
			}
		}
	}
	
	public Button getSolveTimeButton(Solve item) {
		if(item == null) return null;
		String text = item.getDisplayedTime();
		Button button = new Button(text);
		if(text.contains("+")) button.setTextFill(Color.DARKORANGE);
		else if(text.contains("DNF")) button.setTextFill(Color.RED);
		else button.setTextFill(Color.BLACK);
		button.setOnAction(e -> {
			popup.getContent().removeAll(popup.getContent());
			popup.getContent().add(newTimeInfoLabel(item.toString() + "   " + item.getScramble()));
			popup.show(Main.primaryStage);
		});
		return button;
	}
	
	public Button getAvgTimeButton(Average item) {
		if(item == null) return null;
		String text = item.getDisplayedTime();
		Button button = new Button(text);
		if(text.contains("+")) button.setTextFill(Color.DARKORANGE);
		else if(text.contains("DNF")) button.setTextFill(Color.RED);
		else button.setTextFill(Color.BLACK);
		button.setOnAction(e -> {
			popup.getContent().removeAll(popup.getContent());
			popup.getContent().add(newTimeInfoLabel(item.toString()));
			popup.show(Main.primaryStage);
		});
		return button;
	}
	
	private void setScramble(Text text)
	{
		try {
            
            URL url = new URL("http://localhost:2014/scramble/.txt?=333");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
             
            text.setText(in.readLine());
            in.close();
             
        }
        catch (MalformedURLException error) {}
        catch (IOException error) {}
	}
	
	public void setSessionStats()
	{
		Solve[] solveList = Arrays.copyOf(timeList.getItems().toArray(), timeList.getItems().size(), Solve[].class);
		
		int successes = 0;
		for(Solve solve : timeList.getItems())
		{
			if(solve.solveStateProperty.get() != SolveState.DNF) successes++;
		}
		int rate = (int)(100 * ((double)successes/timeList.getItems().size()));
		accuracy.setText(rate + "%   " + successes + "/" + timeList.getItems().size());
		
		if(solveList.length > 0) {
			TimeComparator tc = new TimeComparator();
			Solve minSingle = Arrays.stream(solveList).min(tc).orElseThrow();
			Button minSingleButton = getSolveTimeButton(minSingle);
			bestTime.setGraphic(minSingleButton);
		}
		
		if(solveList.length >= 3) {
			AverageComparator ac = new AverageComparator();
			Average minMean = Arrays.stream(solveList).map(s -> s.mo3).min(ac).orElseThrow();
			Button minMeanButton = getAvgTimeButton(minMean);
			bestMean.setGraphic(minMeanButton);
		}
		
		if(solveList.length >= 5) {
			AverageComparator ac = new AverageComparator();
			Average minMean = Arrays.stream(solveList).map(s -> s.ao5).min(ac).orElseThrow();
			Button minMeanButton = getAvgTimeButton(minMean);
			bestAverage5.setGraphic(minMeanButton);
		}
		
		if(solveList.length >= 12) {
			AverageComparator ac = new AverageComparator();
			Average minMean = Arrays.stream(solveList).map(s -> s.ao12).min(ac).orElseThrow();
			Button minMeanButton = getAvgTimeButton(minMean);
			bestAverage12.setGraphic(minMeanButton);
		}
		
		if(solveList.length > 0) {
			Average mean1 = new Average(solveList, false, false);
			Button meanButton = getAvgTimeButton(mean1);
			mean.setGraphic(meanButton);
		}
			
		if(solveList.length > 2) {
			Average avg1 = new Average(solveList, true, true);
			Button avgButton = getAvgTimeButton(avg1);
			average.setGraphic(avgButton);
		}
	}
	
	class ButtonCell extends TableCell<Solve, Solve> {
		public Button button;
	}
	
	public void clearStats()
	{
		accuracy.setText(null);
		mean.setGraphic(null);
		average.setGraphic(null);
		bestTime.setGraphic(null);
		bestMean.setGraphic(null);
		bestAverage5.setGraphic(null);
		bestAverage12.setGraphic(null);
	}
	
}
