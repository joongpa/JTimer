/*
 * To-do list:
 * - Time per alg graph
 * - Add other buffer support
 * - Letter pair tracing (until a cycle break)
 * - Separate page for categorizing solves
 * - Options to categorize solve by "Corner Memory Related", "Edge Memory Related", and "Execution Related"
 * - Page to enter letter scheme
 * - Progress bar that shows how long spacebar must be held for
 * - Style stuff to not be default javafx look
 * - <LAST PRIORITY> Stackmat and Typing input
 * 
 * 
 * Bugs:
 * - Timer occasionally does not stop (Cause unclear)
*/
package application;

import java.net.URL;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import net.gnehzr.tnoodle.puzzle.ThreeByThreeCubePuzzle;
import net.gnehzr.tnoodle.scrambles.Puzzle;

public class MainController implements Initializable{
	
	private Popup popup;
	
	
	@FXML private BarChart<String, Integer> barChart;
	@FXML private CategoryAxis xAxis;
	@FXML private NumberAxis yAxis;
	private XYChart.Series<String, Integer> series;
	
	@FXML private Label algDisplay;
	@FXML private Label dnfAlgs;
	@FXML private Label succAlgs;
	
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
	
	String[] options = {"Default", "6", "7", "8", "9", "10", "11", "12", "13"};
	//String[] cornerBufferOptions = {"UBL", "UFR", "UBR", "UFL", "DFR", "DFL", "DBR", "DBL"};
	//String[] edgeBufferOptions = {"UF", "UB", "UR", "UL", "DF", "DR", "DL", "DB", "FR", "FL", "BR", "BL"};
	
	
	@FXML private Label accuracy;
	@FXML private Label bestTime;
	@FXML private Label bestMean;
	@FXML private Label bestAverage5;
	@FXML private Label bestAverage12;
	@FXML private Label mean;
	@FXML private Label average;
	
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

	int currentAlgCount;
	
	Puzzle puzzle = new ThreeByThreeCubePuzzle();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
		series = new XYChart.Series<>();
		xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(
				   "8", "9", "10", "11", "12")));
		
		algCount.getItems().addAll(FXCollections.observableArrayList(options));
		algCount.getSelectionModel().clearAndSelect(0);
		currentAlgCount = setScramble();
		popup = newPopup();
		
		//Solve[] tester = new Solve[30];
		//for(int i = 0; i < 30; i++) tester[i] = new Solve(i+1, 20.0, "", 10);
		//ObservableList<Solve> dummyList = FXCollections.observableArrayList(tester);
		//timeList.setItems(dummyList);
		
		solveNumber.setCellValueFactory(new PropertyValueFactory<Solve, Integer>("solveNumber"));
		displayedTime.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		mo3.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		ao5.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));
		ao12.setCellValueFactory(new PropertyValueFactory<Solve, Solve>("this"));

		mo3.setSortable(false);
		ao5.setSortable(false);
		ao12.setSortable(false);
		
		solveNumber.setResizable(false);
		displayedTime.setResizable(false);
		mo3.setResizable(false);
		ao5.setResizable(false);
		ao12.setResizable(false);
		
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
					timeList.getItems().add(new Solve(largestIndex + 1, st.getTime(), scrambleText.getText(), currentAlgCount));
					timeList.getSelectionModel().select(largestIndex, solveNumber);
					timeList.scrollTo(largestIndex);
			        currentAlgCount = setScramble();
			        setSessionStats();
			        st.stop();
			        lockout = true;
				}
			} else if(spaceCombo.match(e)){
				keyPressCount++;
				if(keyPressCount == 1)
					delay.start();
				if(delay.getTime() > 0 && !lockout){
					st.reset();
					timerLabel.setTextFill(Color.web("#00DD00"));
					ready = true;
				}
			}
			e.consume();
		});
		
		root.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			keyPressCount = 0;
			lockout = false;
			if(spaceCombo.match(e)) 
			{
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
		
		algCount.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			currentAlgCount = setScramble();
		});
		
		timeList.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			algDisplay.setText(String.valueOf(newValue.numAlgs));
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
	
	public TextArea newTimeInfoLabel(String string)
	{
		TextArea ta = new TextArea();
		ta.setEditable(false);
		ta.setText(string);
		ta.setPrefSize(700, 400);
		ta.selectAll();
		ta.setCache(false);
		return ta;
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
			popup.setX(30);
			popup.setY(100);
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
			popup.setX(30);
			popup.setY(100);
			popup.getContent().add(newTimeInfoLabel(item.toString()));
			popup.show(Main.primaryStage);
		});
		return button;
	}
	
	private int setScramble()
	{
		int numAlgs = 0;
		String choice = algCount.getSelectionModel().getSelectedItem();
		if(choice.equals("Default")) {
			String scramble = puzzle.generateScramble();
			scrambleText.setText(scramble);
	        numAlgs = Scrambler.getAlgCount(scramble);
	            
		} else {
			scrambleText.setText(Scrambler.genScramble(Integer.valueOf(choice)));
			numAlgs = Integer.valueOf(choice);
		}
		return numAlgs;
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
		
		IntSummaryStatistics dnfStats = Arrays.stream(solveList)
										  .filter(s -> s.solveStateProperty.get() == SolveState.DNF)
										  .mapToInt(s -> s.numAlgs)
										  .summaryStatistics();
		
		if(dnfStats.getAverage() == 0.0)
			dnfAlgs.setText("-");
		else 
			dnfAlgs.setText(String.format("%.2f", dnfStats.getAverage()));
		
		IntSummaryStatistics succStats = Arrays.stream(solveList)
				  .filter(s -> s.solveStateProperty.get() != SolveState.DNF)
				  .mapToInt(s -> s.numAlgs)
				  .summaryStatistics();

		if(succStats.getAverage() == 0.0) 
			succAlgs.setText("-");
		else
			succAlgs.setText(String.format("%.2f", succStats.getAverage()));
		
		
		
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
		setAlgAccuracy(solveList);
	}
	
	public void setAlgAccuracy(Solve[] solveList) {
		
		double[] algAverages = new double[xAxis.getCategories().size()]; 
		for(int i = 0; i < algAverages.length; i++) {
			algAverages[i] = getAlgAverage(i + 8, solveList);
			series.getData().add(new XYChart.Data<String, Integer>(String.valueOf(i + 8), (int)algAverages[i]));
		}
		
		barChart.getData().addAll(series);
	}
	
	public double getAlgAverage(int alg, Solve[] solveList) {
		double successes = (double)Arrays.stream(solveList).filter(s -> s.solveStateProperty.get() != SolveState.DNF)
												  .filter(s -> s.numAlgs == alg)
												  .count();
		double total = (double)Arrays.stream(solveList).filter(s -> s.numAlgs == alg).count();
	
		return 100 * (successes/total);
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
		algDisplay.setText(null);
		dnfAlgs.setText(null);
		succAlgs.setText(null);
	}
	
}
