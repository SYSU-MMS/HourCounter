package application;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.Worker.Group;

public class DailyTable {
	private Date startDate;
	private Date endDate;
	private List<DailyWorker> table;
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public List<DailyWorker> getTable(){
		return table;
	}
	
	public boolean containsDate(Date date) {
		return (date.after(this.startDate) && date.before(this.endDate)) ||
				date.equals(this.startDate) || date.equals(this.endDate);
	}
	public List<Worker> getWorkersByGroup(Group group) {
		List<Worker> workers = new ArrayList<Worker>();
		
		for(DailyWorker dailyWorker: table) {
			if(dailyWorker.group == group || dailyWorker.group == Group.AB) {
				workers.add(dailyWorker.worker);
			}
		}
		
		return workers;
	}
	
	public static Map<String, Integer> getAndCheckTableTitle(String excelPath) throws Exception {
		Map<String, Integer> title2Col = new HashMap<String, Integer>();
		Exception formatException = new Exception("常检表格式错误！");
		
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(excelPath));
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		
		XSSFRow titleRow = sheet.getRow(0);
		for(int i = 0; i <= 3; i++) {
			XSSFCell cell = titleRow.getCell(i);
			if(cell == null) {
				throw formatException;
			}
			title2Col.put(cell.getStringCellValue().replaceAll(" ", ""), i);
		}
		
		if(!title2Col.containsKey("A姓名") || !title2Col.containsKey("B姓名") || !title2Col.containsKey("A教室") ||
				!title2Col.containsKey("B教室")) {
			throw formatException;
		}
		
		return title2Col;
	}
	
	public DailyTable(List<Worker> workerList, String excelPath, Date startDate, Date endDate) throws Exception {
		this.table = new ArrayList<DailyWorker>();
		this.startDate = startDate;
		this.endDate = endDate;
		
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(excelPath));
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		
		Map<String, Integer> title2Col = getAndCheckTableTitle(excelPath);
		
		for(int i = 1; i <= sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			
			for(int j = 0; j <= 3; j++) {
				row.getCell(j).setCellType(CellType.STRING);
			}
			
			String nameA = row.getCell(title2Col.get("A姓名")).getStringCellValue();
			String nameB = row.getCell(title2Col.get("B姓名")).getStringCellValue();
			int numRoomsA = row.getCell(title2Col.get("A教室")).getStringCellValue().split(" ").length;
			int numRoomsB = row.getCell(title2Col.get("B教室")).getStringCellValue().split(" ").length;
			
			boolean hasNameA = false;
			boolean hasNameB = false;
			for(Worker worker: workerList) {
				if(nameA.equals(nameB)) {
					DailyWorker dailyWorker = new DailyWorker();
					dailyWorker.worker = worker;
					dailyWorker.group = Group.AB;
					worker.group = dailyWorker.group;
					dailyWorker.numRooms = numRoomsA + numRoomsB;
					this.table.add(dailyWorker);
					
					hasNameA = true;
					hasNameB = true;
				}else {
					if(worker.name.equals(nameA)) {
						DailyWorker dailyWorker = new DailyWorker();
						dailyWorker.worker = worker;
						dailyWorker.group = Group.A;
						worker.group = dailyWorker.group;
						dailyWorker.numRooms = numRoomsA;
						this.table.add(dailyWorker);
						
						hasNameA = true;
					}
					if(worker.name.equals(nameB)) {
						DailyWorker dailyWorker = new DailyWorker();
						dailyWorker.worker = worker;
						dailyWorker.group = Group.B;
						worker.group = dailyWorker.group;
						dailyWorker.numRooms = numRoomsB;
						this.table.add(dailyWorker);
						
						hasNameB = true;
					}
				}
			}
			if(!hasNameA) {
				throw new Exception("通讯录中未找到助理：" + nameA + "的信息_"+excelPath);
			}
			if(!hasNameB) {
				throw new Exception("通讯录中未找到助理：" + nameB + "的信息_"+excelPath);
			}
		}
	}
	
//	public static void main(String[] args) {
//		List<Worker> workers = Worker.initWorkerList("C:\\Users\\18196\\Desktop\\3rd\\工时\\5月\\通讯录.xlsx");
//		DailyTable dailyTable = new DailyTable(workers,
//				"C:\\Users\\18196\\Desktop\\3rd\\工时\\5月\\2021-2022春季学期11-20周常检周检表.xlsx");
//		System.out.println(dailyTable.getTable().size());
//		for(DailyWorker dailyWorker: dailyTable.getTable()) {
//			System.out.println(dailyWorker.worker.name+ " " +dailyWorker.numRooms);
//		}
//	}
}

class DailyWorker {
	Worker worker;
	Group group;
	int numRooms;
}