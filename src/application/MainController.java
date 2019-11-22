/*
 * To-do list:
 * - Make time generated time list selectable
 * - Show best statistics in lower left corner
 * - *Scramble generation*
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
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
	@FXML private TableColumn<Solve, Time> displayedTime;
	@FXML private TableColumn<Solve, Time> mo3;
	@FXML private TableColumn<Solve, Time> ao5;
	@FXML private TableColumn<Solve, Time> ao12;
	

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
	
	ObservableList<Solve> dummyList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		setScramble(scrambleText);
		dummyList = FXCollections.observableArrayList();
		//for(int i = 0; i < 30; i++) dummyList.add(new Solve(i+1,Math.random() * 10 + 120));
		popup = newPopup();
		
		solveNumber.setCellValueFactory(new PropertyValueFactory<Solve, Integer>("solveNumber"));
		displayedTime.setCellValueFactory(new PropertyValueFactory<Solve, Time>("this"));
		mo3.setCellValueFactory(new PropertyValueFactory<Solve, Time>("this"));
		ao5.setCellValueFactory(new PropertyValueFactory<Solve, Time>("this"));
		ao12.setCellValueFactory(new PropertyValueFactory<Solve, Time>("this"));
		
		solveNumber.setSortable(false);
		displayedTime.setSortable(false);
		mo3.setSortable(false);
		ao5.setSortable(false);
		ao12.setSortable(false);
		//timeList.setItems(dummyList);
		
		displayedTime.setCellFactory(col -> {
			return new TableCell<Solve, Time>() {

				@Override
				protected void updateItem(Time item, boolean empty) {
					super.updateItem(item, empty);
					avgUpdate(this, item, empty);
				}
			};
		});
		
		mo3.setCellFactory(col -> {
			final TableCell<Solve, Time> cell = new TableCell<Solve, Time>() {
				
				@Override
				protected void updateItem(Time item, boolean empty) {
					Average average = new Average((Solve)item, timeList, 3, false);
					super.updateItem(item, empty);
					avgUpdate(this, average, empty);
				}
			};
			return cell;
		});
		
		ao5.setCellFactory(col -> {
			return new TableCell<Solve, Time>() {
				
				@Override
				protected void updateItem(Time item, boolean empty) {
					Average average = new Average((Solve)item, timeList, 5, true);
					super.updateItem(item, empty);
					avgUpdate(this, average, empty);
				}
			};
		});
		
		ao12.setCellFactory(col -> {
			return new TableCell<Solve, Time>() {
				
				@Override
				protected void updateItem(Time item, boolean empty) {
					Average average = new Average((Solve)item, timeList, 12, true);
					super.updateItem(item, empty);
					avgUpdate(this, average, empty);
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
						timeList.getItems().add(new Solve(largestIndex + 1, st.getTime(), scrambleText.getText()));
						timeList.getSelectionModel().select(largestIndex, solveNumber);
						timeList.scrollTo(largestIndex);
						
				        setScramble(scrambleText);
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
		Label timeInfoLabel = new Label("Generated by JTimer:\n" + string);
		timeInfoLabel.setMinSize(700, 400);
		return timeInfoLabel;
	}
	
	private void avgUpdate(TableCell<Solve, Time> cell, Time item, boolean empty)
	{
		if(item == null || empty || cell.getItem() == null) {
			cell.setText(null);
			cell.setGraphic(null);
		}
		else {
			String text = item.getDisplayedTime();
			if(!text.equals("-")) {
				Button button = new Button(text);
				cell.setGraphic(button);
				if(text.contains("+")) button.setTextFill(Color.DARKORANGE);
				else if(text.contains("DNF")) button.setTextFill(Color.RED);
				else button.setTextFill(Color.BLACK);
				
				button.setOnAction(e -> {
					popup.getContent().removeAll(popup.getContent());
					if(item instanceof Average)
						popup.getContent().add(newTimeInfoLabel(((Object)item).toString()));
					else popup.getContent().add(newTimeInfoLabel(((Object)item).toString() + "   " + ((Solve)item).getScramble()));
					popup.show(Main.primaryStage);
				});
			} else {
				cell.setGraphic(null);
			}
		}
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
	
}
