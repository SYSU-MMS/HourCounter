package application;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DutyTable {
	private Date startDate;
	private Date endDate;
	private List<List<DutyPeriod>> table;
	
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
	public List<List<DutyPeriod>> getTable(){
		return table;
	}
	
	public boolean containsDate(Date date) {
		return (date.after(this.startDate) && date.before(this.endDate)) ||
				date.equals(this.startDate) || date.equals(this.endDate);
	}
	public List<DutyPeriod> getTableByDay(int day) {
		day = day == Calendar.SUNDAY ? 6 : day-Calendar.MONDAY;
		return table.get(day);
	}
	
	public static Map<String, Integer> getAndCheckTableTitle(String excelPath) throws Exception {
		Map<String, Integer> title2Col = new HashMap<String, Integer>();
		Exception formatException = new Exception("值班表格式错误！");
		
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(excelPath));
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		
		XSSFRow titleRow = sheet.getRow(0);
		for(int i = 0; i <= 7; i++) {
			XSSFCell cell = titleRow.getCell(i);
			if(cell == null) {
				throw formatException;
			}
			title2Col.put(cell.getStringCellValue().replaceAll(" ", ""), i);
		}
		
		if(!title2Col.containsKey("时长") || !title2Col.containsKey("周一") || !title2Col.containsKey("周二") ||
				!title2Col.containsKey("周三") || !title2Col.containsKey("周五") || !title2Col.containsKey("周六") ||
				!title2Col.containsKey("周日")) {
			throw formatException;
		}
		
		return title2Col;
	}
	
	public DutyTable(List<Worker> workerList, String excelPath, Date startDate, Date endDate) throws Exception {
		this.table = new ArrayList<List<DutyPeriod>>();
		for(int i = 0; i < 7; i++) {
			this.table.add(new ArrayList<DutyPeriod>());
		}
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(excelPath));
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		
		for(int i = 1; i <= sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			
			for(int j = 0; j <= 7; j++) {
				row.getCell(j).setCellType(CellType.STRING);
			}
			
			String[] strHours = row.getCell(0).getStringCellValue().split("/");
			if(strHours.length == 1) {
				for(int j = 1; j <= 7; j++) {
					DutyPeriod dutyPeriod = new DutyPeriod();
					dutyPeriod.hours = Double.parseDouble(strHours[0]);
					
					String[] names = row.getCell(j).getStringCellValue().split(" ");
					for(String name: names) {
						boolean hasName = false;
						for(Worker worker: workerList) {
							if(worker.name.equals(name)) {
								dutyPeriod.workers.add(worker);
								hasName = true;
								break;
							}
						}
						if(!hasName) {
							throw new Exception("通讯录中未找到助理：" + name + "的信息_"+excelPath);
						}
					}
					
					this.table.get(j-1).add(dutyPeriod);
				}
			}else if(strHours.length == 2) {
				for(int j = 1; j <= 5; j++) {
					DutyPeriod dutyPeriod = new DutyPeriod();
					dutyPeriod.hours = Double.parseDouble(strHours[0]);
					
					String[] names = row.getCell(j).getStringCellValue().split(" ");
					for(String name: names) {
						boolean hasName = false;
						for(Worker worker: workerList) {
							if(worker.name.equals(name)) {
								dutyPeriod.workers.add(worker);
								hasName = true;
								break;
							}
						}
						if(!hasName) {
							throw new Exception("通讯录中未找到助理：" + name + "的信息_"+excelPath);
						}
					}
					
					this.table.get(j-1).add(dutyPeriod);
				}
				
				for(int j = 6; j <= 7; j++) {
					DutyPeriod dutyPeriod = new DutyPeriod();
					dutyPeriod.hours = Double.parseDouble(strHours[1]);
					
					String[] names = row.getCell(j).getStringCellValue().split(" ");
					for(String name: names) {
						boolean hasName = false;
						for(Worker worker: workerList) {
							if(worker.name.equals(name)) {
								dutyPeriod.workers.add(worker);
								hasName = true;
								break;
							}
						}
						if(!hasName) {
							throw new Exception("通讯录中未找到助理：" + name + "的信息_"+excelPath);
						}
					}
					
					this.table.get(j-1).add(dutyPeriod);
				}
			}
		}
	}
	
//	public static void main(String[] args) {
//		System.out.println("start");
//		List<Worker> workers = Worker.initWorkerList("C:\\Users\\18196\\Desktop\\3rd\\工时\\5月\\通讯录.xlsx");
//		DutyTable dutyTable = new DutyTable(workers, 
//				"C:\\Users\\18196\\Desktop\\3rd\\工时\\5月\\2021-2022春季学期4-10周值班表.xlsx");
//		
//		int count = 0;
//		for(int i = 1; i <= 7; i++) {
//			List<DutyPeriod> table = dutyTable.getTableByDay(i);
//			for(DutyPeriod period: table) {
//				System.out.println(period.workers.size());
//				count++;
//			}
//		}
//		System.out.println(count);
//	}
}

class DutyPeriod {
	List<Worker> workers;
	double hours;
	
	DutyPeriod(){
		workers = new ArrayList<Worker>();
	}
}