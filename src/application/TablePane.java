package application;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TablePane extends ScrollPane {
	class TableImportPane extends FlowPane {
		private Label titleLabel;
		private Button importButton;
		private List<FlowPane> tableDatePanes;
		private List<String> excelPaths;
		
		private void createTableDatePane(String excelPath, Date startDate, Date endDate) {
			String[] dirs = excelPath.split("\\\\");
			String fileName = dirs[dirs.length-1];
			
			LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			FlowPane tableDatePane = new FlowPane();
			DatePicker startDatePicker = new DatePicker(startLocalDate);
			DatePicker endDatePicker = new DatePicker(endLocalDate);
			Label toLabel = new Label("至");
			Label fileNameLabel = new Label(fileName);
			Button removeButton = new Button("删除");
			
			removeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					int idx = tableDatePanes.indexOf(tableDatePane);
					excelPaths.remove(idx);
					tableDatePanes.remove(tableDatePane);
					getChildren().remove(tableDatePane);
				}
			});
			
			tableDatePane.getChildren().addAll(startDatePicker, toLabel, endDatePicker, fileNameLabel, removeButton);
			this.getChildren().add(tableDatePane);
			this.tableDatePanes.add(tableDatePane);
		}
		
		TableImportPane(String title) {
			this.titleLabel = new Label(title);
			this.importButton = new Button("导入");
			this.tableDatePanes = new ArrayList<FlowPane>();
			this.excelPaths = new ArrayList<String>();
			
			this.importButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser excelChooser = new FileChooser();
					excelChooser.setTitle("选择" + titleLabel.getText() + "表格");
					excelChooser.setInitialDirectory(new File("."));
					if(!excelPaths.isEmpty()) {
						excelChooser.setInitialDirectory(new File(excelPaths.get(excelPaths.size()-1)).getParentFile());
					}
					excelChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
					File excelFile = excelChooser.showOpenDialog(primaryStage);
					
					if(excelFile != null) {
						try {
							if(titleLabel.getText() == "常检表") {
								DailyTable.getAndCheckTableTitle(excelFile.getAbsolutePath());
							}else if(titleLabel.getText() == "值班表") {
								DutyTable.getAndCheckTableTitle(excelFile.getAbsolutePath());
							}
							
							if(!excelPaths.contains(excelFile.getPath())) {
								createTableDatePane(excelFile.getPath(), new Date(), new Date());
								excelPaths.add(excelFile.getPath());
							}else {
								throw new Exception("此表格已经导入");
							}
							
						} catch (Exception e) {
							Main.alertError(e);
						}
					}
				}
			});
			
			List<TableInfo> tableInfoList = null;
			try {
				if(titleLabel.getText() == "常检表") {
					tableInfoList = HistoryIO.readDailyTableFileInfo();
				}else if(titleLabel.getText() == "值班表") {
					tableInfoList = HistoryIO.readDutyTableFileInfo();
				}
				
				if(tableInfoList != null) {
					for(TableInfo tableInfo: tableInfoList) {
						if(new File(tableInfo.path).exists()) {
							Date startDate = Main.string2Date(tableInfo.startDate);
							Date endDate = Main.string2Date(tableInfo.endDate);
							createTableDatePane(tableInfo.path, startDate, endDate);
							excelPaths.add(tableInfo.path);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.getChildren().add(0, this.titleLabel);
			this.getChildren().add(1, this.importButton);
//			for(FlowPane tableDatePane: this.tableDatePanes) {
//				this.getChildren().add(tableDatePane);
//			}
		}
	}
	
	private VBox vbox;
	private FlowPane workerListPane;
	private TableImportPane dutyImportPane;
	private TableImportPane dailyImportPane;
	
	private Stage primaryStage;
	private HourListPane hourListPane;
	private List<Worker> workerList;
	
	public static final int WIDTH = 250;
	
	TablePane(Stage primaryStage, HourListPane hourListPane) {
		this.setPrefWidth(WIDTH);
		
		this.vbox = new VBox();
		this.primaryStage = primaryStage;
		this.hourListPane = hourListPane;
		
		this.createWorkerListImportPane();
		this.dutyImportPane = new TableImportPane("值班表");
		this.dailyImportPane = new TableImportPane("常检表");
		
		this.vbox.getChildren().addAll(this.dutyImportPane, this.dailyImportPane);
		this.setContent(this.vbox);
	}
	
	public List<Worker> getWorkerList() {
		return this.workerList;
	}
	
	public List<DutyTable> getDutyTableList() throws Exception{
		List<DutyTable> dutyTableList = new ArrayList<DutyTable>();
		List<TableInfo> tableInfoList = new ArrayList<TableInfo>();
		
		for(int i = 0; i < this.dutyImportPane.excelPaths.size(); i++) {
			String excelPath = this.dutyImportPane.excelPaths.get(i);
			DatePicker startDatePicker = (DatePicker) this.dutyImportPane.tableDatePanes.get(i).getChildren().get(0);
			DatePicker endDatePicker = (DatePicker) this.dutyImportPane.tableDatePanes.get(i).getChildren().get(2);
			
			Date startDate = Main.string2Date(startDatePicker.getValue().toString());
			Date endDate = Main.string2Date(endDatePicker.getValue().toString());
			
			tableInfoList.add(new TableInfo(excelPath, startDatePicker.getValue().toString(), 
					endDatePicker.getValue().toString()));
			dutyTableList.add(new DutyTable(this.workerList, excelPath, startDate, endDate));
		}
		
		HistoryIO.writeDutyTableFileInfo(tableInfoList);
		return dutyTableList;
	}
	
	public List<DailyTable> getDailyTableList() throws Exception{
		List<DailyTable> dailyTableList = new ArrayList<DailyTable>();
		List<TableInfo> tableInfoList = new ArrayList<TableInfo>();
		
		for(int i = 0; i < this.dailyImportPane.excelPaths.size(); i++) {
			String excelPath = this.dailyImportPane.excelPaths.get(i);
			DatePicker startDatePicker = (DatePicker) this.dailyImportPane.tableDatePanes.get(i).getChildren().get(0);
			DatePicker endDatePicker = (DatePicker) this.dailyImportPane.tableDatePanes.get(i).getChildren().get(2);
			
			Date startDate = Main.string2Date(startDatePicker.getValue().toString());
			Date endDate = Main.string2Date(endDatePicker.getValue().toString());
			
			tableInfoList.add(new TableInfo(excelPath, startDatePicker.getValue().toString(), 
					endDatePicker.getValue().toString()));
			dailyTableList.add(new DailyTable(this.workerList, excelPath, startDate, endDate));
		}
		
		HistoryIO.writeDailyTableFileInfo(tableInfoList);
		return dailyTableList;
	}
	
	private void createWorkerListImportPane() {
		this.workerListPane = new FlowPane();
		Label titleLabel = new Label("通讯录");
		Button importButton = new Button("导入");
		Label fileNameLabel = new Label();
		
		importButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser excelChooser = new FileChooser();
				excelChooser.setTitle("选择" + titleLabel.getText() + "表格");
				excelChooser.setInitialDirectory(new File("."));
				try {
					String historyPath = HistoryIO.readWorkerListFileInfo();
					if(new File(historyPath).exists()) {
						excelChooser.setInitialDirectory(new File(historyPath).getParentFile());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				excelChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
				File excelFile = excelChooser.showOpenDialog(primaryStage);
				
				if(excelFile != null) {							
					try {
						workerList = Worker.initWorkerList(excelFile.getPath());
						fileNameLabel.setText(excelFile.getName());
						hourListPane.setWorkerList(workerList);
						
						HistoryIO.writeWorkerListFileInfo(excelFile.getAbsolutePath());
					} catch (Exception e) {
						Main.alertError(e);
					}
				}
			}
		});
		
		String historyPath;
		try {
			historyPath = HistoryIO.readWorkerListFileInfo();
			if(new File(historyPath).exists()) {
				workerList = Worker.initWorkerList(historyPath);
				fileNameLabel.setText(new File(historyPath).getName());
				hourListPane.setWorkerList(workerList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.workerListPane.getChildren().addAll(titleLabel, importButton, fileNameLabel);
		this.vbox.getChildren().add(this.workerListPane);
	}
	
	public static LocalDate date2LocalDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}