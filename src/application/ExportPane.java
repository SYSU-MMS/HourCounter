package application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.Worker.Group;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 导出面板，导出工资明细表、发放表等
 * @author Syderny
 *
 */
public class ExportPane extends FlowPane {
	private Button exportDetailTableButton;
	private Button exportGrantTableButton;
	private TablePane tablePane;
	private DatePane datePane;
	private HourListPane hourListPane;
	private Stage primaryStage;
	
	/**
	 * 初始化导出面板，由于导出表格需要所有的信息，所以将其他面板传参进来
	 * @param primaryStage
	 * @param tablePane
	 * @param datePane
	 * @param hourListPane
	 */
	public ExportPane(Stage primaryStage, TablePane tablePane, DatePane datePane, HourListPane hourListPane) {
		this.exportDetailTableButton = new Button("导出工资明细表");
		this.exportGrantTableButton = new Button("导出劳务发放表");
		this.tablePane = tablePane;
		this.datePane = datePane;
		this.hourListPane = hourListPane;
		this.primaryStage = primaryStage;
		
		this.exportDetailTableButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				HourCounter hourCounter = initHourCounter();
				if(hourCounter != null) {
					String dirPath = getDirectory("工资明细表");
					exportDetailTable(hourCounter, dirPath);					
				}
			}
		});
		
		this.getChildren().addAll(this.exportDetailTableButton, this.exportGrantTableButton);
	}
	
	private String getTitleDate() {
		return this.datePane.getTitleDate();
	}
	
	/**
	 * 弹出文件选择器获取导出的目录
	 * @param tableTitle
	 * @return
	 */
	private String getDirectory(String tableTitle) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("保存表格");
		fileChooser.setInitialDirectory(new File("."));
		try {
			String historyPath = HistoryIO.readExportFileInfo();
			if(new File(historyPath).exists()) {
				fileChooser.setInitialDirectory(new File(historyPath).getParentFile());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		fileChooser.setInitialFileName(this.getTitleDate() + tableTitle + ".xlsx");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
		File file = fileChooser.showSaveDialog(this.primaryStage);
		if(file != null) {
//			if(file.exists() && !this.getCoverConfirm(file.getName())) {
//				return null;
//			}
			try {
				HistoryIO.writeExportFileInfo(file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return file.getAbsolutePath();
		}
		return null;
	}
	
//	private boolean getCoverConfirm(String fileName) {
//		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//		alert.setTitle("确认保存");
//		alert.setHeaderText(fileName + " " + "已存在");
//		alert.setContentText("确认覆盖它吗？");
//		Optional<ButtonType> result = alert.showAndWait();
//		return result.get() == ButtonType.OK;
//	}
	
	/**
	 * 初始化一个HourCounter用于计算工时
	 * @return
	 */
	private HourCounter initHourCounter() {
		try {
			HourCounter hourCounter = new HourCounter(this.tablePane.getWorkerList(), this.tablePane.getDutyTableList(), 
					this.tablePane.getDailyTableList(), this.datePane.getWorkDates(),
					this.datePane.getWeeklyDates(), this.datePane.getHoursLimit());
			hourCounter.setDutyRestDates(this.datePane.getDutyRestDates());
			hourCounter.setDailyRestDates(this.datePane.getDailyRestDates());
			hourCounter.setDailyChangeDates(this.datePane.getDailyChangeDates());
			hourCounter.setDutyChangeDates(this.datePane.getDutyChangeDates());
			hourCounter.setExtraHourLists(this.hourListPane.getExtraLists());
			hourCounter.count();
			return hourCounter;
		} catch (Exception e) {
			e.printStackTrace();
			Main.alertError(e);
			
			return null;
		}
	}
	
	/**
	 * 导出工资明细表
	 * @param counter
	 * @param excelPath
	 */
	private void exportDetailTable(HourCounter counter, String excelPath) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		XSSFRow titleRow = sheet.createRow(0);
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		
		
		// ============创建表头===============
		String[] infoTitle = {"序号", "组别", "学号", "姓名"};
		for(int i = 0; i < infoTitle.length; i++) {
			XSSFCell cell = titleRow.createCell(i);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(infoTitle[i]);
		}
		
		int numHourNames = counter.getHourNames().size();
		for(int i = 0; i < numHourNames; i++) {
			XSSFCell cell = titleRow.createCell(i + infoTitle.length);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(counter.getHourNames().get(i));
		}
		
		String[] totalTitle = {"本月总计", "上月积余", "劳务发放表工时", "本月结余", "备注"};
		for(int i = 0; i < totalTitle.length; i++) {
			XSSFCell cell = titleRow.createCell(i + infoTitle.length + numHourNames);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(totalTitle[i]);
		}
		// ================================
		
		// 按照组别排序助理，ABC组在前，系统管理在后
		List<Worker> workerList = counter.getWorkerList();
		Collections.sort(workerList, new Comparator<Worker>() {
			@Override
			public int compare(Worker worker1, Worker worker2) {
				if(worker1.group.equals(worker2.group)) {
					return worker1.studentID.compareTo(worker2.studentID);
				}
				return worker1.group.compareTo(worker2.group);
			}
		});
		
		// 遍历助理，一行行添加助理信息
		for(int i = 0; i < workerList.size(); i++) {
			XSSFRow row = sheet.createRow(i+1);
			Worker worker = workerList.get(i);
			
			// 以下是设置助理的基本信息在对应的单元格
			
			XSSFCell indexCell = row.createCell(0);
			indexCell.setCellStyle(cellStyle);
			indexCell.setCellValue(i+1);
			
			XSSFCell groupCell = row.createCell(1);
			groupCell.setCellStyle(cellStyle);
			String group;
			if(worker.group.equals(Group.SYSTEM)) {
				group = "系统组";
			}else if(worker.group.equals(Group.ADMIN)) {
				group = "管理组";
			}else {
				group = worker.group.toString();
			}
			groupCell.setCellValue(group);
			
			XSSFCell studentIDCell = row.createCell(2);
			studentIDCell.setCellStyle(cellStyle);
			studentIDCell.setCellValue(worker.studentID);
			
			XSSFCell nameCell = row.createCell(3);
			nameCell.setCellStyle(cellStyle);
			nameCell.setCellValue(worker.name);
			
			// 以下是设置此助理在各个工时项目的工时在对应的单元格
			
			for(int j = 0; j < numHourNames; j++) {
				XSSFCell cell = row.createCell(j+4);
				if(worker.hourList.containsKey(counter.getHourNames().get(j))) {
					cell.setCellValue(worker.hourList.get(counter.getHourNames().get(j)));					
				}else {
					cell.setCellValue("");
				}
			}
			
			// 以下是"本月总计", "上月积余", "劳务发放表工时", "本月结余", ["备注"]的信息
			
			XSSFCell totalCell = row.createCell(4 + numHourNames);
			double totalHours = worker.getTotalHours();
			totalCell.setCellValue(totalHours);
			
			XSSFCell preRestCell = row.createCell(5 + numHourNames);
			preRestCell.setCellValue(worker.preHours);
			
			XSSFCell finalCell = row.createCell(6 + numHourNames);
			finalCell.setCellValue(worker.finalHours);
			
			XSSFCell RestCell = row.createCell(7 + numHourNames);
			RestCell.setCellValue(worker.restHours);
		}
		
		// 导出文件
		File file = new File(excelPath);
		try {
			file.createNewFile();
			FileOutputStream stream =new FileOutputStream(file);
			workbook.write(stream);
			stream.close();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
			Main.alertError(e);
		}
	}
}