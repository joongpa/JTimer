/*
 * To-do list:
 * - Separate settings page
 * - Add other buffer support
 * - Add floating support
 * - Letter pair tracing (until a cycle break)
 * - Separate page for categorizing solves
 * - Options to categorize solve by "Corner Memory Related", "Edge Memory Related", and "Execution Related"
 * - Page to enter letter scheme
 * - Style stuff to not be default javafx look
 * - Switch to Maven so everything can be packaged neatly
 * - <LAST PRIORITY> Stackmat and Typing input
 * 
 * 
 * Bugs:
 * - Timer occasionally does not stop (Cause unclear)
*/
package application;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import net.gnehzr.tnoodle.puzzle.NoInspectionThreeByThreeCubePuzzle;
import net.gnehzr.tnoodle.puzzle.ThreeByThreeCubePuzzle;
import net.gnehzr.tnoodle.scrambles.Puzzle;

public class MainController implements Initializable{
	
	private Popup popup;
	
	HashMap<String, int[]> bufferMap;
	
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
	@FXML private ChoiceBox<String> cornerBufferChoiceBox;
	@FXML private ChoiceBox<String> edgeBufferChoiceBox;
	@FXML private ChoiceBox<String> parity1;
	@FXML private ChoiceBox<String> parity2;
	
	String[] options = {"Default", "6", "7", "8", "9", "10", "11", "12", "13"};
	String[] cornerBufferOptions = {"UBL", "UFR", "UBR", "UFL", "DFR", "DFL", "DBR", "DBL"};
	String[] edgeBufferOptions = {"UF", "UB", "UR", "UL", "DF", "DR", "DL", "DB", "FR", "FL", "BR", "BL"};
	
	String[] intervalOptions = {"0", "5", "10", "15"};
	String[] delayOptions = {"0.00", "0.30", "0.55"};
	
	@FXML private Label accuracy;
	@FXML private Label bestTime;
	@FXML private Label bestMean;
	@FXML private Label bestAverage5;
	@FXML private Label bestAverage12;
	@FXML private Label mean;
	@FXML private Label average;
	@FXML private Label intervalTimer;
	private int keyPressCount = 0;
	
	@FXML private ChoiceBox<String> timerDelayTextField;
	@FXML private ChoiceBox<String> timerIntervalTextField;
	
	//hotkeys
	private KeyCombination spaceCombo = new KeyCodeCombination(KeyCode.SPACE);
	private KeyCombination clearCombo = new KeyCodeCombination(KeyCode.D, KeyCodeCombination.ALT_DOWN);
	private KeyCombination deleteCombo = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.ALT_DOWN);
	private KeyCombination okCombo = new KeyCodeCombination(KeyCode.DIGIT1, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination plus2Combo = new KeyCodeCombination(KeyCode.DIGIT2, KeyCodeCombination.CONTROL_DOWN);
	private KeyCombination dnfCombo = new KeyCodeCombination(KeyCode.DIGIT3, KeyCodeCombination.CONTROL_DOWN);
	
	double delayTime = -5;
	int intervalTime = 0;
	
	Stopwatch st = new Stopwatch();
	SimpleTimer delay = new SimpleTimer();
	CountDownTimer interval = new CountDownTimer(intervalTime);
	
	BooleanProperty ready = new SimpleBooleanProperty(false);
	BooleanProperty lockout = new SimpleBooleanProperty(false);

	int currentAlgCount;
	
	Puzzle puzzle = new NoInspectionThreeByThreeCubePuzzle();
	
	int[] cornerBuffer = {0,2,2};
	int[] edgeBuffer = {0,2,1};
	int[] thing = {0,1,2};
	Scrambler scrambler = new Scrambler(cornerBuffer, edgeBuffer, edgeBuffer, thing);

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
		bufferMap = new HashMap<>();
		bufferMap.put("UBL", new int[] {0,0,0});
		bufferMap.put("UBR", new int[] {0,0,2});
		bufferMap.put("UFR", new int[] {0,2,2});
		bufferMap.put("UFL", new int[] {0,2,0});
		bufferMap.put("DFR", new int[] {2,2,2});
		bufferMap.put("DFL", new int[] {2,2,0});
		bufferMap.put("DBR", new int[] {2,0,2});
		bufferMap.put("DBL", new int[] {2,0,0});
		bufferMap.put("UB", new int[] {0,0,1});
		bufferMap.put("UR", new int[] {0,1,2});
		bufferMap.put("UF", new int[] {0,2,1});
		bufferMap.put("UL", new int[] {0,1,0});
		bufferMap.put("DF", new int[] {2,2,1});
		bufferMap.put("DR", new int[] {2,1,2});
		bufferMap.put("DB", new int[] {2,0,1});
		bufferMap.put("DL", new int[] {2,1,0});
		bufferMap.put("FR", new int[] {1,2,2});
		bufferMap.put("FL", new int[] {1,2,0});
		bufferMap.put("BR", new int[] {1,0,2});
		bufferMap.put("BL", new int[] {1,0,0});
		
		series = new XYChart.Series<>();
		xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList("8", "9", "10", "11", "12")));
		
		algCount.getItems().addAll(FXCollections.observableArrayList(options));
		cornerBufferChoiceBox.getItems().addAll(FXCollections.observableArrayList(cornerBufferOptions));
		edgeBufferChoiceBox.getItems().addAll(FXCollections.observableArrayList(edgeBufferOptions));
		parity1.getItems().addAll(FXCollections.observableArrayList(edgeBufferOptions));
		parity2.getItems().addAll(FXCollections.observableArrayList(edgeBufferOptions));
		
		cornerBufferChoiceBox.getSelectionModel().select(1);
		edgeBufferChoiceBox.getSelectionModel().select(0);
		parity1.getSelectionModel().select(0);
		parity2.getSelectionModel().select(2);
		
		timerDelayTextField.getItems().addAll(FXCollections.observableArrayList(delayOptions));
		timerDelayTextField.getSelectionModel().clearAndSelect(0);
		timerIntervalTextField.getItems().addAll(FXCollections.observableArrayList(intervalOptions));
		timerIntervalTextField.getSelectionModel().clearAndSelect(0);
		algCount.getSelectionModel().clearAndSelect(0);
		
		setScramble();
		popup = newPopup();

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
		
		interval.start();
		root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

			if(st.inProgress.get())
			{
				keyPressCount++;
				if(keyPressCount == 1) {
					st.pause();
					int largestIndex = timeList.getItems().size();
					timeList.getItems().add(new Solve(largestIndex + 1, st.getTime(), scrambleText.getText(), currentAlgCount));
					timeList.getSelectionModel().select(largestIndex, solveNumber);
					timeList.scrollTo(largestIndex);
			        setScramble();
			        setSessionStats();
			        st.stop();
			        lockout.set(true);
				}
			} else if(spaceCombo.match(e)){
				keyPressCount++;
				if(keyPressCount == 1)
					delay.start();
				if(delay.getTime() > delayTime && !lockout.get() && interval.getTime() <= 0){
					st.reset();
					timerLabel.setTextFill(Color.web("#00DD00"));
					ready.set(true);
				}
			}
			e.consume();
		});
		
		root.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
			keyPressCount = 0;
			lockout.set(false);
			if(spaceCombo.match(e)) 
			{
				timerLabel.setTextFill(Color.web("#000000"));

				if(!st.inProgress.get())
				{
					if(ready.get())
					{
						st.start();
						ready.set(false);
					}
				} else {
					st.stop();
				}
					
				e.consume();
			}
		});
		
		st.inProgress.addListener((o, oldvalue, newValue) -> {
			if(oldvalue) {
				interval.reset();
				interval.start();
				if(intervalTime != 0) scrambleText.setVisible(false);
			}
		});
		
		interval.formattedTimeProperty.addListener((o, oldvalue, newValue) -> {
			if(Integer.valueOf(newValue) <= 0) {
				intervalTimer.textProperty().unbind();
				intervalTimer.setText("Ready");
				scrambleText.setVisible(true);
			}
			
			if(Integer.valueOf(newValue) > 0) {
				intervalTimer.textProperty().bind(interval.formattedTimeProperty);
			}
		});
		
		intervalTimer.textProperty().bind(interval.formattedTimeProperty);
		
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
		
		algCount.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			setScramble();
		});
		
		timerDelayTextField.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			delayTime = Double.valueOf(newValue);
			if(delayTime == 0) delayTime = -5;
		});
		
		timerIntervalTextField.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			intervalTime = Integer.valueOf(newValue);
			interval.setStartTime(intervalTime);
		});
	
		timeList.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			if(newValue != null) algDisplay.setText(String.valueOf(newValue.numAlgs));
		});

		cornerBufferChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			scrambler.cornerBuffer = bufferMap.get(newValue);
			setScramble();
		});
		
		edgeBufferChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			scrambler.edgeBuffer = bufferMap.get(newValue);
			setScramble();
		});
		
		parity1.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			scrambler.parityEdge1 = bufferMap.get(newValue);
			setScramble();
		});
		
		parity2.getSelectionModel().selectedItemProperty().addListener((o, oldvalue, newValue) -> {
			scrambler.parityEdge2 = bufferMap.get(newValue);
			setScramble();
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
	
	private void setScramble()
	{
		int numAlgs = 0;
		String choice = algCount.getSelectionModel().getSelectedItem();
		if(choice.equals("Default")) {
			String scramble = puzzle.generateScramble();
			scrambleText.setText(scramble);
	        numAlgs = scrambler.getAlgCount(scramble);
	            
		} else {
			scrambleText.setText(scrambler.genScramble(Integer.valueOf(choice)));
			numAlgs = Integer.valueOf(choice);
		}
		currentAlgCount = numAlgs;
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
		barChart.getData().clear();
		series.getData().clear();
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
