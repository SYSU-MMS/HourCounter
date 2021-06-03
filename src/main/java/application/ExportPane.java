package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.Worker.Group;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 导出面板，导出工资明细表、发放表等
 * @author Syderny
 *
 */
public class ExportPane extends GridPane {
	/**
	 * 单项设置面板
	 * @author Syderny
	 *
	 */
	class SetUpPane extends BorderPane {
		private Label titleLabel;
		private Spinner<Double> spinner;
		
		SetUpPane(String title, double defaultHours, double stepHours) {
			this.titleLabel = new Label(title);
			this.spinner = new Spinner<Double>(0, 999, defaultHours, stepHours);
			
			this.titleLabel.getStyleClass().add("setup-title-label");
			this.spinner.getStyleClass().add("setup-spinner");
			this.spinner.getEditor().getStyleClass().add("spinner-editor");
			
			this.setRight(this.spinner);
			this.setLeft(this.titleLabel);
		}
		
		public double getValue() {
			return this.spinner.getValue();
		}
	}
	
	private Button exportDetailTableButton;
	private Button exportGrantTableButton;
	private TablePane tablePane;
	private DatePane datePane;
	private HourListPane hourListPane;
	private Stage primaryStage;
	
	private SetUpPane dailyHoursPane;
	private SetUpPane weeklyHoursPane;
	private SetUpPane adminHoursPane;
	private SetUpPane systemHoursPane;
	private SetUpPane paymentPane;
	
	public static final double DAILY_CHECK_HOURS_PER_DAY = 2;
	public static final double WEEKLY_CHECK_HOURS_PER_WEEK = 0.5;
	public static final double GROUP_ADMIN_HOURS_PER_MONTH = 40;
	public static final double GROUP_SYSTEM_HOURS_PER_MONTH = 20;
	public static final double PAYMENT_PER_HOURS = 25;
	
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
		
		this.dailyHoursPane = new SetUpPane("常检每日工时", DAILY_CHECK_HOURS_PER_DAY, 0.5);
		this.weeklyHoursPane = new SetUpPane("周检每间工时", WEEKLY_CHECK_HOURS_PER_WEEK, 0.5);
		this.adminHoursPane = new SetUpPane("管理组本月工时", GROUP_ADMIN_HOURS_PER_MONTH, 1);
		this.systemHoursPane = new SetUpPane("系统组本月工时", GROUP_SYSTEM_HOURS_PER_MONTH, 1);
		this.paymentPane = new SetUpPane("每小时薪酬", PAYMENT_PER_HOURS, 1);
		
		this.exportDetailTableButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				HourCounter hourCounter = initHourCounter();
				if(hourCounter != null) {
					String dirPath = getSaveDirectory("工资明细表");
					if(dirPath != null) {
						exportDetailTable(hourCounter, dirPath);					
					}
				}
			}
		});
		
		this.exportGrantTableButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser excelChooser = new FileChooser();
				excelChooser.setTitle("选择工资明细表格");
				excelChooser.setInitialDirectory(new File("."));
				try {
					String historyPath = HistoryIO.readExportFileInfo();
					if(new File(historyPath).getParentFile().isDirectory()) {
						excelChooser.setInitialDirectory(new File(historyPath).getParentFile());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				excelChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
				File excelFile = excelChooser.showOpenDialog(primaryStage);
				
				if(excelFile != null) {
					try {
						getAndCheckDetailTableTitle(excelFile.getAbsolutePath());
						String exportPath =  getSaveDirectory("多媒体勤工助学劳务发放表");
						if(exportPath != null) {
							exportGrantTable(excelFile.getAbsolutePath(), exportPath);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Main.alertError(e);
					}
				}
			}
		});
		
		this.setHgap(20);
		this.setVgap(20);
		GridPane.setHalignment(this.paymentPane, HPos.CENTER);
		GridPane.setHalignment(this.dailyHoursPane, HPos.CENTER);
		GridPane.setHalignment(this.weeklyHoursPane, HPos.CENTER);
		GridPane.setHalignment(this.adminHoursPane, HPos.CENTER);
		GridPane.setHalignment(this.systemHoursPane, HPos.CENTER);
		GridPane.setHalignment(this.exportDetailTableButton, HPos.CENTER);
		GridPane.setHalignment(this.exportGrantTableButton, HPos.CENTER);
		
		this.getStyleClass().add("export-pane");
		this.exportDetailTableButton.getStyleClass().add("export-table-button");
		this.exportGrantTableButton.getStyleClass().add("export-table-button");
		
		this.add(this.paymentPane, 0, 0);
		this.add(this.dailyHoursPane, 0, 1);
		this.add(this.weeklyHoursPane, 0, 2);
		this.add(this.adminHoursPane, 1, 1);
		this.add(this.systemHoursPane, 1, 2);
		this.add(this.exportDetailTableButton, 0, 3);
		this.add(this.exportGrantTableButton, 1, 3);
	}
	
	/**
	 * 检查明细表格式是否正确，返回表头信息
	 * @param excelPath
	 * @return 表头对应列数
	 * @throws Exception 格式不对，这不是明细表
	 */
	public static Map<String, Integer> getAndCheckDetailTableTitle(String excelPath) throws Exception {
		Map<String, Integer> title2Col = new HashMap<String, Integer>();
		Exception formatException = new Exception("工资明细表格式错误！");
		
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(excelPath));
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		
		XSSFRow titleRow = sheet.getRow(0);
		for(int i = 0; i < titleRow.getLastCellNum(); i++) {
			XSSFCell cell = titleRow.getCell(i);
			if(cell == null) {
				throw formatException;
			}
			title2Col.put(cell.getStringCellValue().replaceAll(" ", ""), i);
		}
		
		if(!title2Col.containsKey("本月总计") || !title2Col.containsKey("劳务发放表工时")) {
			throw formatException;
		}
		
		return title2Col;
	}
	
	/**
	 * 根据工资明细表生成劳务发放表
	 * @param detailTablePath
	 * @param exportPath
	 * @throws Exception
	 */
	private void exportGrantTable(String detailTablePath, String exportPath) throws Exception {
		List<Worker> grantWorkerList = new ArrayList<Worker>();
		Map<String, Integer> title2Col = getAndCheckDetailTableTitle(detailTablePath);
		
		// 先导入明细表
		XSSFWorkbook detailWorkbook = new XSSFWorkbook(new FileInputStream(detailTablePath));
		XSSFSheet detailSheet = detailWorkbook.getSheetAt(0);
		XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(detailWorkbook);
		
		double totalHours = 0;
		for(int i = 1; i <= detailSheet.getLastRowNum(); i++) {
			XSSFRow row = detailSheet.getRow(i);
			String name = row.getCell(title2Col.get("姓名")).getStringCellValue();
			boolean hasWorker = false;
			for(Worker worker: this.tablePane.getWorkerList()) {
				if(worker.name.equals(name)) {
					worker.finalHours = evaluator.evaluate(row.getCell(title2Col.get("劳务发放表工时"))).getNumberValue();
					grantWorkerList.add(worker);
					hasWorker = true;
					totalHours += worker.finalHours;
					break;
				}
			}
			if(!hasWorker) {
				throw new Exception(String.format("助理：%s在通讯录中不存在", name));
			}
		}
		
		// 按学号排序
		Collections.sort(grantWorkerList, new Comparator<Worker>() {
			@Override
			public int compare(Worker worker1, Worker worker2) {
				return worker1.studentID.compareTo(worker2.studentID);
			}
		});
		
		// 按照模板生成发放表
		XSSFWorkbook grantWorkbook = new XSSFWorkbook(new FileInputStream("grantTableModel.xlsx"));
		XSSFSheet grantSheet = grantWorkbook.getSheetAt(0);
		
		XSSFCellStyle cellStyle = grantWorkbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		Font font = grantWorkbook.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setFontName("宋体");
		cellStyle.setFont(font);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		
		XSSFRow grantTitleRow = grantSheet.getRow(0);
		XSSFCell grantTitleCell = grantTitleRow.getCell(0);
		grantTitleCell.setCellValue(this.getTitleDate() + "勤工助学劳务报酬发放表");
		
		XSSFRow sumRow = grantSheet.getRow(4);
		XSSFCell sumCell = sumRow.getCell(0);
		sumCell.setCellValue(String.format(
				"总人数：%d   总工时：%.1f    总金额：%.1f     复核人签名：       学生处负责人签名盖章：      财务处负责人签名盖章：", 
				grantWorkerList.size(), totalHours, totalHours*this.paymentPane.getValue()));
		
		for(int i = 0; i < grantWorkerList.size(); i++) {
			Worker worker = grantWorkerList.get(i);
			grantSheet.shiftRows(4+i, grantSheet.getLastRowNum(), 1, true, false);
			XSSFRow newRow = grantSheet.createRow(4+i);
			
			XSSFCell indexCell = newRow.createCell(0, CellType.NUMERIC);
			indexCell.setCellStyle(cellStyle);
			indexCell.setCellValue(i+1);
			
			XSSFCell campusCell = newRow.createCell(1, CellType.STRING);
			campusCell.setCellStyle(cellStyle);
			campusCell.setCellValue("东校区");
			
			XSSFCell workUnitCell = newRow.createCell(2, CellType.STRING);
			workUnitCell.setCellStyle(cellStyle);
			workUnitCell.setCellValue("教务部");
			
			XSSFCell workDepartmentCell = newRow.createCell(3, CellType.STRING);
			workDepartmentCell.setCellStyle(cellStyle);
			workDepartmentCell.setCellValue("多媒体室");
			
			XSSFCell studentIDCell = newRow.createCell(4, CellType.STRING);
			studentIDCell.setCellStyle(cellStyle);
			studentIDCell.setCellValue(worker.studentID);
			
			XSSFCell nameCell = newRow.createCell(5, CellType.STRING);
			nameCell.setCellStyle(cellStyle);
			nameCell.setCellValue(worker.name);
			
			XSSFCell hoursCell = newRow.createCell(6, CellType.NUMERIC);
			hoursCell.setCellStyle(cellStyle);
			hoursCell.setCellValue(worker.finalHours);
			
			XSSFCell paymentCell = newRow.createCell(7, CellType.FORMULA);
			paymentCell.setCellStyle(cellStyle);
			paymentCell.setCellFormula(String.format("G%d*%f", 5+i, this.paymentPane.getValue()));
			
			XSSFCell bankAccountCell = newRow.createCell(8, CellType.STRING);
			bankAccountCell.setCellStyle(cellStyle);
			bankAccountCell.setCellValue(worker.bankcard);
			
			XSSFCell phoneCell = newRow.createCell(9, CellType.STRING);
			phoneCell.setCellStyle(cellStyle);
			phoneCell.setCellValue(worker.phone);
			
			XSSFCell signatureCell = newRow.createCell(10, CellType.STRING);
			signatureCell.setCellStyle(cellStyle);
			
			XSSFCell remarkCell = newRow.createCell(11, CellType.STRING);
			remarkCell.setCellStyle(cellStyle);
			remarkCell.setCellValue("固定岗");
		}
		
		// 导出文件
		grantWorkbook.setForceFormulaRecalculation(true);
		File file = new File(exportPath);
		try {
			file.createNewFile();
			FileOutputStream stream =new FileOutputStream(file);
			grantWorkbook.write(stream);
			stream.close();
			grantWorkbook.close();
		} catch (IOException e) {
			e.printStackTrace();
			Main.alertError(e);
		}
	}
	
	private String getTitleDate() {
		return this.datePane.getTitleDate();
	}
	
	/**
	 * 弹出文件选择器获取导出的目录
	 * @param tableTitle
	 * @return
	 */
	private String getSaveDirectory(String tableTitle) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("保存表格");
		fileChooser.setInitialDirectory(new File("."));
		try {
			String historyPath = HistoryIO.readExportFileInfo();
			if(new File(historyPath).getParentFile().isDirectory()) {
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
			
			hourCounter.setDailyCheckHoursPerday(this.dailyHoursPane.getValue());
			hourCounter.setWeeklyCheckHoursPerWeek(this.weeklyHoursPane.getValue());
			hourCounter.setGroupAdminHours(this.adminHoursPane.getValue());
			hourCounter.setGroupSystemHours(this.systemHoursPane.getValue());
			
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
			totalCell.setCellFormula(String.format("SUM(%c%d:%c%d)", 'E', i+2, 'A'+4+numHourNames-1, i+2));
			
			XSSFCell preRestCell = row.createCell(5 + numHourNames);
			preRestCell.setCellValue(worker.preRestHours);
			
			XSSFCell finalCell = row.createCell(6 + numHourNames);
			finalCell.setCellFormula(String.format("MIN(%c%d,%f)", 'A'+4+numHourNames, i+2, counter.getMaxHours()));
			
			XSSFCell restCell = row.createCell(7 + numHourNames);
			restCell.setCellFormula(String.format("%c%d-%c%d", 'A'+4+numHourNames, i+2, 'A'+6+numHourNames, i+2));
		}
		
		// 导出文件
		workbook.setForceFormulaRecalculation(true);
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