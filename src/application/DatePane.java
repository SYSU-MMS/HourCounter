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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DatePane extends ScrollPane {
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
			this.monthSpinner = new Spinner<Integer>(1, 12, cal.get(Calendar.MONTH));
			this.monthLabel = new Label("月");
			
			this.getChildren().addAll(this.titleLabel, this.yearSpinner, this.yearLabel, this.monthSpinner, this.monthLabel);
		}
	}
	
	class LimitPane extends FlowPane {
		private Label limitLabel;
		private Spinner<Integer> limitSpinner;
		
		LimitPane() {
			this.limitLabel = new Label("本月工资上限：");
			this.limitSpinner = new Spinner<Integer>(1, 200, 40);
			
			this.getChildren().addAll(this.limitLabel, this.limitSpinner);
		}
	}
	
	class WorkDatePane extends FlowPane {
		private Label titleLabel;
		private DatePicker startDatePicker;
		private Label toLabel;
		private DatePicker endDatePicker;
		
		WorkDatePane() {
			this.titleLabel = new Label("结算起止日期");
			this.toLabel = new Label("至");
			this.startDatePicker = new DatePicker(LocalDate.now());
			this.endDatePicker = new DatePicker(LocalDate.now());
			
			this.getChildren().addAll(this.titleLabel, this.startDatePicker, this.toLabel, this.endDatePicker);
		}
	}
	
	class WeeklyDatePane extends FlowPane {
		private Label titleLabel;
		private Button addButton;
		private List<Button> removeButtons;
		private List<DatePicker> weeklyDatePickers;
		
		WeeklyDatePane() {
			this.titleLabel = new Label("周检日期：");
			this.addButton = new Button("添加日期");
			this.weeklyDatePickers = new ArrayList<DatePicker>();
			this.removeButtons = new ArrayList<Button>();
			
			this.addButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					DatePicker weeklyDatePicker = new DatePicker(LocalDate.now());
					Button removeButton = new Button("删除");
					
					removeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							weeklyDatePickers.remove(weeklyDatePicker);
							removeButtons.remove(removeButton);
							getChildren().removeAll(weeklyDatePicker, removeButton);
						}
					});
					
					weeklyDatePickers.add(weeklyDatePicker);
					removeButtons.add(removeButton);
					
					getChildren().addAll(weeklyDatePicker, removeButton);
				}
			});
			
			this.getChildren().addAll(this.titleLabel, this.addButton);
		}
	}
	
	class RestDatePane extends FlowPane {
		private Label titleLabel;
		private Button addSegmentButton;
		private List<Button> removeButtons;
		private List<Label> toLabels;
		private List<DatePicker> startDatePickers;
		private List<DatePicker> endDatePickers;
		
		RestDatePane(String title) {
			this.titleLabel = new Label(title);
			this.addSegmentButton = new Button("添加时段");
			this.toLabels = new ArrayList<Label>();
			this.startDatePickers = new ArrayList<DatePicker>();
			this.endDatePickers = new ArrayList<DatePicker>();
			this.removeButtons = new ArrayList<Button>();
			
			this.addSegmentButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Label toLabel = new Label("to");
					DatePicker startDatePicker = new DatePicker(LocalDate.now());
					DatePicker endDatePicker = new DatePicker(LocalDate.now());
					Button removeButton = new Button("删除");
					
					removeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							toLabels.remove(toLabel);
							startDatePickers.remove(startDatePicker);
							endDatePickers.remove(endDatePicker);
							removeButtons.remove(removeButton);
							getChildren().removeAll(toLabel, startDatePicker, endDatePicker, removeButton);
						}
					});
					
					toLabels.add(toLabel);
					startDatePickers.add(startDatePicker);
					endDatePickers.add(endDatePicker);
					removeButtons.add(removeButton);
					getChildren().addAll(startDatePicker, toLabel, endDatePicker, removeButton);
				}
			});
			
			this.getChildren().addAll(this.titleLabel, this.addSegmentButton);
		}
	}
	
	class ChangeDatePane extends FlowPane {
		private Label titleLabel;
		private Button addChangeButton;
		private List<Button> removeButtons;
		private List<Label> toLabels;
		private List<DatePicker> changeDatePickers;
		private List<Spinner<Integer>> changeDaySpinners;
		
		ChangeDatePane(String title) {
			this.titleLabel = new Label(title);
			this.addChangeButton = new Button("新增日期转换");
			this.toLabels = new ArrayList<Label>();
			this.changeDatePickers = new ArrayList<DatePicker>();
			this.changeDaySpinners = new ArrayList<Spinner<Integer>>();
			this.removeButtons = new ArrayList<Button>();
			
			this.addChangeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Label toLabel = new Label("转换为星期");
					DatePicker changeDatePicker = new DatePicker(LocalDate.now());
					
					int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
					day = day == Calendar.SUNDAY ? 6 : day-Calendar.MONDAY;
					Spinner<Integer> changeDaySpinner = new Spinner<Integer>(1, 7, day);
					
					Button removeButton = new Button("删除");
					
					removeButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							toLabels.remove(toLabel);
							changeDatePickers.remove(changeDatePicker);
							changeDaySpinners.remove(changeDaySpinner);
							removeButtons.remove(removeButton);
							getChildren().removeAll(changeDatePicker, toLabel, changeDaySpinner, removeButton);
						}
					});
					
					toLabels.add(toLabel);
					changeDatePickers.add(changeDatePicker);
					changeDaySpinners.add(changeDaySpinner);
					removeButtons.add(removeButton);
					getChildren().addAll(changeDatePicker, toLabel, changeDaySpinner, removeButton);
				}
			});
			
			this.getChildren().addAll(this.titleLabel, this.addChangeButton);
		}
	}
	
	private VBox vbox;
	private TitleDatePane titleDatePane;
	private LimitPane limitPane;
	private WorkDatePane workDatePane;
	private WeeklyDatePane weeklyDatePane;
	private RestDatePane dailyRestPane;
	private RestDatePane dutyRestPane;
	private ChangeDatePane dailyChangePane;
	private ChangeDatePane dutyChangePane;
	
	public static final int WIDTH = 250;
	
	public DatePane() {
		this.setPrefWidth(WIDTH);
		
		this.vbox = new VBox();
		this.getChildren().add(this.vbox);
		
		this.titleDatePane = new TitleDatePane();
		this.titleDatePane.setPrefWidth(WIDTH);
		this.vbox.getChildren().add(this.titleDatePane);
		
		this.limitPane = new LimitPane();
		this.limitPane.setPrefWidth(WIDTH);
		this.vbox.getChildren().add(this.limitPane);
		
		this.workDatePane = new WorkDatePane();
		this.workDatePane.setPrefWidth(WIDTH);
		this.vbox.getChildren().add(this.workDatePane);
		
		this.weeklyDatePane = new WeeklyDatePane();
		this.weeklyDatePane.setPrefWidth(WIDTH);
		this.vbox.getChildren().add(this.weeklyDatePane);
		
		this.dailyRestPane = new RestDatePane("常检休息日");
		this.dailyRestPane.setPrefWidth(WIDTH);
		this.vbox.getChildren().add(this.dailyRestPane);
		
		this.dutyRestPane = new RestDatePane("值班休息日");
		this.dutyRestPane.setPrefWidth(WIDTH);
		this.vbox.getChildren().add(this.dutyRestPane);
		
		this.dailyChangePane = new ChangeDatePane("常检日期转换");
		this.dailyChangePane.setPrefWidth(WIDTH);
		this.vbox.getChildren().add(this.dailyChangePane);
		
		this.dutyChangePane = new ChangeDatePane("值班日期转换");
		this.dutyChangePane.setPrefWidth(WIDTH);
		this.vbox.getChildren().add(this.dutyChangePane);
		
		this.setContent(this.vbox);
	}
	
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