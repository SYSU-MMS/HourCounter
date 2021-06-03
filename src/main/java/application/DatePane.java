package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * 设定日期的面板，包括结算日期和休息日等
 * @author Syderny
 *
 */
public class DatePane extends GridPane {
	/**
	 * 设定当前结算工时的月份的面板
	 * @author Syderny
	 *
	 */
	class TitleDatePane extends FlowPane {
		private Label titleLabel;
		private Spinner<Integer> yearSpinner;
		private Label yearLabel;
		private Spinner<Integer> monthSpinner;
		private Label monthLabel;
		
		TitleDatePane() {
			Calendar cal = Calendar.getInstance();
			this.titleLabel = new Label("月份：");
			this.yearSpinner = new Spinner<Integer>(2000, 9999, cal.get(Calendar.YEAR));
			this.yearLabel = new Label("年");
			this.monthSpinner = new Spinner<Integer>(1, 12, cal.get(Calendar.MONTH)+1);
			this.monthLabel = new Label("月");
			
			this.yearSpinner.getEditor().getStyleClass().add("spinner-editor");
			this.yearSpinner.getStyleClass().add("year-spinner");
			this.monthSpinner.getEditor().getStyleClass().add("spinner-editor");
			this.monthSpinner.getStyleClass().add("setup-spinner");
			
			this.titleLabel.getStyleClass().add("date-set-label");
			this.yearLabel.getStyleClass().add("date-set-label");
			this.monthLabel.getStyleClass().add("date-set-label");
			
			FlowPane rightPane = new FlowPane();
			rightPane.getChildren().addAll(this.yearSpinner, this.yearLabel, this.monthSpinner, this.monthLabel);
			this.getChildren().addAll(this.titleLabel, rightPane);
		}
	}
	
	/**
	 * 设定工时上限的面板
	 * @author Syderny
	 *
	 */
	class LimitPane extends FlowPane {
		private Label limitLabel;
		private Spinner<Integer> limitSpinner;
		
		LimitPane() {
			this.limitLabel = new Label("本月工时上限：");
			this.limitSpinner = new Spinner<Integer>(1, 200, 40);
			
			this.limitSpinner.getEditor().getStyleClass().add("spinner-editor");
			this.limitSpinner.getStyleClass().add("setup-spinner");
			
			this.limitLabel.getStyleClass().add("date-set-label");
			
			FlowPane rightPane = new FlowPane();
			rightPane.getChildren().addAll(this.limitSpinner);
			this.getChildren().addAll(this.limitLabel, rightPane);
		}
	}
	
	/**
	 * 设定结算起止日期的面板
	 * @author Syderny
	 *
	 */
	class WorkDatePane extends FlowPane {
		private Label titleLabel;
		private DatePicker startDatePicker;
		private Label toLabel;
		private DatePicker endDatePicker;
		
		WorkDatePane() {
			this.titleLabel = new Label("结算起止日期：");
			this.toLabel = new Label("-");
			this.startDatePicker = new DatePicker(LocalDate.now());
			this.endDatePicker = new DatePicker(LocalDate.now());
			
			this.startDatePicker.getStyleClass().add("work-date-picker");
			this.endDatePicker.getStyleClass().add("work-date-picker");
			this.startDatePicker.getEditor().getStyleClass().add("date-picker-editor");
			this.endDatePicker.getEditor().getStyleClass().add("date-picker-editor");
			this.startDatePicker.getEditor().getStyleClass().add("work-date-picker-editor");
			this.endDatePicker.getEditor().getStyleClass().add("work-date-picker-editor");
			
			this.titleLabel.getStyleClass().add("date-set-label");
			this.toLabel.getStyleClass().add("date-set-label");
			
			FlowPane rightPane = new FlowPane();
			rightPane.getChildren().addAll(this.startDatePicker, this.toLabel, this.endDatePicker);
			this.getChildren().addAll(this.titleLabel, rightPane);
		}
	}
	
	/**
	 * 设定周检日期的面板
	 * @author Syderny
	 *
	 */
	class WeeklyDatePane extends ScrollPane {
		private Button addButton;
		private List<Button> removeButtons;
		private List<DatePicker> weeklyDatePickers;
		
		WeeklyDatePane() {
			this.addButton = new Button("+");
			this.weeklyDatePickers = new ArrayList<DatePicker>();
			this.removeButtons = new ArrayList<Button>();
			this.addButton.getStyleClass().add("add-button");
			
			FlowPane flowPane = new FlowPane(this.addButton);
			flowPane.getStyleClass().add("scroll-pane-flow-pane");
			flowPane.setVgap(5);
			
			this.setContent(flowPane);
			this.getChildren().addAll(flowPane);
			
			this.addButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					DatePicker weeklyDatePicker = new DatePicker(LocalDate.now());
					Button removeButton = new Button("X");
					FlowPane unitPane = new FlowPane();
					
					weeklyDatePicker.getStyleClass().add("work-date-picker");
					weeklyDatePicker.getEditor().getStyleClass().add("date-picker-editor");
					weeklyDatePicker.getEditor().getStyleClass().add("work-date-picker-editor");
					removeButton.getStyleClass().add("small-close-button");
					unitPane.getStyleClass().add("date-unit-pane");
					
					removeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							weeklyDatePickers.remove(weeklyDatePicker);
							removeButtons.remove(removeButton);
							unitPane.getChildren().removeAll(weeklyDatePicker, removeButton);
							flowPane.getChildren().remove(unitPane);
						}
					});
					
					weeklyDatePickers.add(weeklyDatePicker);
					removeButtons.add(removeButton);
					unitPane.getChildren().addAll(weeklyDatePicker, removeButton);
					flowPane.getChildren().add(flowPane.getChildren().size()-1, unitPane);
				}
			});
			
		}
	}
	
	/**
	 * 设定值班或常检休息日的面板
	 * @author Syderny
	 *
	 */
	class RestDatePane extends ScrollPane {
		private Button addSegmentButton;
		private List<Button> removeButtons;
		private List<Label> toLabels;
		private List<DatePicker> startDatePickers;
		private List<DatePicker> endDatePickers;
		
		RestDatePane() {
			this.addSegmentButton = new Button("+");
			this.toLabels = new ArrayList<Label>();
			this.startDatePickers = new ArrayList<DatePicker>();
			this.endDatePickers = new ArrayList<DatePicker>();
			this.removeButtons = new ArrayList<Button>();
			this.addSegmentButton.getStyleClass().add("add-button");
			
			FlowPane flowPane = new FlowPane(this.addSegmentButton);
			flowPane.getStyleClass().add("scroll-pane-flow-pane");
			flowPane.setVgap(5);
			
			this.setContent(flowPane);
			this.getChildren().addAll(flowPane);
			
			this.addSegmentButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Label toLabel = new Label("-");
					DatePicker startDatePicker = new DatePicker(LocalDate.now());
					DatePicker endDatePicker = new DatePicker(LocalDate.now());
					Button removeButton = new Button("X");
					FlowPane unitPane = new FlowPane();
					
					toLabel.setStyle("-fx-font-weight: bold");
					startDatePicker.getStyleClass().add("work-date-picker");
					endDatePicker.getStyleClass().add("work-date-picker");
					startDatePicker.getEditor().getStyleClass().add("date-picker-editor");
					endDatePicker.getEditor().getStyleClass().add("date-picker-editor");
					startDatePicker.getEditor().getStyleClass().add("work-date-picker-editor");
					endDatePicker.getEditor().getStyleClass().add("work-date-picker-editor");
					removeButton.getStyleClass().add("small-close-button");
					unitPane.getStyleClass().add("date-segment-unit-pane");
					
					removeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							toLabels.remove(toLabel);
							startDatePickers.remove(startDatePicker);
							endDatePickers.remove(endDatePicker);
							removeButtons.remove(removeButton);
							unitPane.getChildren().removeAll(startDatePicker, toLabel, endDatePicker, removeButton);
							flowPane.getChildren().remove(unitPane);
						}
					});
					
					toLabels.add(toLabel);
					startDatePickers.add(startDatePicker);
					endDatePickers.add(endDatePicker);
					removeButtons.add(removeButton);
					unitPane.getChildren().addAll(startDatePicker, toLabel, endDatePicker, removeButton);
					flowPane.getChildren().add(flowPane.getChildren().size()-1, unitPane);
				}
			});
			
		}
	}
	
	/**
	 * 设定常检或值班转换日的面板，就是调休时，某些日期需要更换成周几的值班或常检
	 * @author Syderny
	 *
	 */
	class ChangeDatePane extends ScrollPane {
		private Button addChangeButton;
		private List<Button> removeButtons;
		private List<Label> toLabels;
		private List<DatePicker> changeDatePickers;
		private List<Spinner<Integer>> changeDaySpinners;
		
		ChangeDatePane() {
			this.addChangeButton = new Button("+");
			this.toLabels = new ArrayList<Label>();
			this.changeDatePickers = new ArrayList<DatePicker>();
			this.changeDaySpinners = new ArrayList<Spinner<Integer>>();
			this.removeButtons = new ArrayList<Button>();
			this.addChangeButton.getStyleClass().add("add-button");
			
			FlowPane flowPane = new FlowPane(this.addChangeButton);
			flowPane.getStyleClass().add("scroll-pane-flow-pane");
			flowPane.setVgap(5);
			
			this.setContent(flowPane);
			this.getChildren().addAll(flowPane);
			
			this.addChangeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Label toLabel = new Label("-");
					DatePicker changeDatePicker = new DatePicker(LocalDate.now());
					
					int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
					day = day == Calendar.SUNDAY ? 6 : day-Calendar.MONDAY;
					Spinner<Integer> changeDaySpinner = new Spinner<Integer>(1, 7, day);
					
					Button removeButton = new Button("X");
					
					FlowPane unitPane = new FlowPane();
					toLabel.setStyle("-fx-font-weight: bold;");
					changeDatePicker.getStyleClass().add("work-date-picker");
					changeDatePicker.getEditor().getStyleClass().add("date-picker-editor");
					changeDatePicker.getEditor().getStyleClass().add("work-date-picker-editor");
					changeDaySpinner.getStyleClass().add("setup-spinner");
					changeDaySpinner.getEditor().getStyleClass().add("spinner-editor");
					removeButton.getStyleClass().add("small-close-button");
					unitPane.getStyleClass().add("date-change-unit-pane");
					
					removeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							toLabels.remove(toLabel);
							changeDatePickers.remove(changeDatePicker);
							changeDaySpinners.remove(changeDaySpinner);
							removeButtons.remove(removeButton);
							unitPane.getChildren().removeAll(changeDatePicker, toLabel, changeDaySpinner, removeButton);
							flowPane.getChildren().remove(unitPane);
						}
					});
					
					
					toLabels.add(toLabel);
					changeDatePickers.add(changeDatePicker);
					changeDaySpinners.add(changeDaySpinner);
					removeButtons.add(removeButton);
					unitPane.getChildren().addAll(changeDatePicker, toLabel, changeDaySpinner, removeButton);
					flowPane.getChildren().add(flowPane.getChildren().size()-1, unitPane);
				}
			});
			
		}
	}
	
	private VBox vbox;
	private Accordion accordion;
	private TitleDatePane titleDatePane;
	private LimitPane limitPane;
	private WorkDatePane workDatePane;
	private WeeklyDatePane weeklyDatePane;
	private RestDatePane dailyRestPane;
	private RestDatePane dutyRestPane;
	private ChangeDatePane dailyChangePane;
	private ChangeDatePane dutyChangePane;
	
	public DatePane() {
		this.vbox = new VBox(20);
		this.accordion = new Accordion();
		
		this.vbox.setAlignment(Pos.CENTER);
		
		this.add(this.vbox, 0, 0);
		this.add(this.accordion, 1, 0);
		
		
		this.titleDatePane = new TitleDatePane();
		this.vbox.getChildren().add(this.titleDatePane);
		
		this.limitPane = new LimitPane();
		this.vbox.getChildren().add(this.limitPane);
		
		this.workDatePane = new WorkDatePane();
		this.vbox.getChildren().add(this.workDatePane);
		
		
		this.vbox.getStyleClass().add("date-left-vbox");
		
		
		this.weeklyDatePane = new WeeklyDatePane();
		TitledPane weeklyDateTitledPane = new TitledPane("周检日期", weeklyDatePane);
		accordion.getPanes().add(weeklyDateTitledPane);
		
		this.dailyRestPane = new RestDatePane();
		TitledPane dailyRestTitledPane = new TitledPane("常检休息日", dailyRestPane);
		accordion.getPanes().add(dailyRestTitledPane);
		
		this.dutyRestPane = new RestDatePane();
		TitledPane dutyRestTitledPane = new TitledPane("值班休息日", dutyRestPane);
		accordion.getPanes().add(dutyRestTitledPane);
		
		this.dailyChangePane = new ChangeDatePane();
		TitledPane dailyChangeTitledPane = new TitledPane("常检调换日", dailyChangePane);
		accordion.getPanes().add(dailyChangeTitledPane);
		
		this.dutyChangePane = new ChangeDatePane();
		TitledPane dutyChangeTitledPane = new TitledPane("值班调换日", dutyChangePane);
		accordion.getPanes().add(dutyChangeTitledPane);
	}
	
	// 以下的方法用于获取本面板设定的各类信息
	
	public List<Date> getWorkDates() {
		Date startDate = Main.string2Date(this.workDatePane.startDatePicker.getValue().toString());
		Date endDate = Main.string2Date(this.workDatePane.endDatePicker.getValue().toString());
		return getDates(startDate, endDate);
	}
	
	public List<Date> getWeeklyDates() {
		List<Date> weeklyDates = new ArrayList<Date>();
		
		for(DatePicker weeklyDatePicker: this.weeklyDatePane.weeklyDatePickers) {
			weeklyDates.add(Main.string2Date(weeklyDatePicker.getValue().toString()));
		}
		
		return weeklyDates;
	}
	
	public int getHoursLimit() {
		return this.limitPane.limitSpinner.getValue();
	}
	
	public List<Date> getDailyRestDates() {
		List<Date> dailyRestDates = new ArrayList<Date>();
		
		for(int i = 0; i < this.dailyRestPane.startDatePickers.size(); i++) {
			Date startDate = Main.string2Date(this.dailyRestPane.startDatePickers.get(i).getValue().toString());
			Date endDate = Main.string2Date(this.dailyRestPane.endDatePickers.get(i).getValue().toString());
			dailyRestDates.addAll(getDates(startDate, endDate));
		}
		
		return dailyRestDates;
	}
	
	public List<Date> getDutyRestDates() {
		List<Date> dutyRestDates = new ArrayList<Date>();
		
		for(int i = 0; i < this.dutyRestPane.startDatePickers.size(); i++) {
			Date startDate = Main.string2Date(this.dutyRestPane.startDatePickers.get(i).getValue().toString());
			Date endDate = Main.string2Date(this.dutyRestPane.endDatePickers.get(i).getValue().toString());
			dutyRestDates.addAll(getDates(startDate, endDate));
		}
		
		return dutyRestDates;
	}
	
	public String getTitleDate() {
		int year = this.titleDatePane.yearSpinner.getValue();
		int month = this.titleDatePane.monthSpinner.getValue();
		return String.format("%d年%d月", year, month);
	}
	
	public Map<Date, Integer> getDailyChangeDates() {
		Map<Date, Integer> dailyChangeDates = new HashMap<Date, Integer>();
		
		for(int i = 0; i < this.dailyChangePane.changeDatePickers.size(); i++) {
			Date dailyDate = Main.string2Date(this.dailyChangePane.changeDatePickers.get(i).getValue().toString());
			int changeToDay = this.dailyChangePane.changeDaySpinners.get(i).getValue();
			changeToDay = changeToDay == 7 ? Calendar.SUNDAY : changeToDay + Calendar.MONDAY-1;
			dailyChangeDates.put(dailyDate, changeToDay);
		}
		
		return dailyChangeDates;
	}
	
	public Map<Date, Integer> getDutyChangeDates() {
		Map<Date, Integer> dutyChangeDates = new HashMap<Date, Integer>();
		
		for(int i = 0; i < this.dutyChangePane.changeDatePickers.size(); i++) {
			Date dutyDate = Main.string2Date(this.dutyChangePane.changeDatePickers.get(i).getValue().toString());
			int changeToDay = this.dutyChangePane.changeDaySpinners.get(i).getValue();
			changeToDay = changeToDay == 7 ? Calendar.SUNDAY : changeToDay + Calendar.MONDAY-1;
			dutyChangeDates.put(dutyDate, changeToDay);
		}
		
		return dutyChangeDates;
	}
	
	public static List<Date> getDates(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        List<Date> dateList = new ArrayList<Date>();
        dateList.add(startDate);
        while (endDate.after(startCal.getTime())) {
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(startCal.getTime());
        }
        return dateList;
    }
}