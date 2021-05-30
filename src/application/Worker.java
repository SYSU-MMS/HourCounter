package application;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

/**
 * 助理类
 * @author Syderny
 *
 */
public class Worker {
	public enum Group {
		A, B, AB, C, SYSTEM, ADMIN
	}
	
	String name;
	Group group;
	String phone, bankcard, studentID;
	Map<String, Double> hourList;
	double preHours, finalHours, restHours;
	
	public Worker() {
		this.hourList = new HashMap<String, Double>();
	}
	
	@Override
	public String toString() {
		return name + "(" + group + ") " + this.hourList;
	}
	
	public void cleanHours() {
		this.hourList = new HashMap<String, Double>();
		this.finalHours = 0;
		this.restHours = 0;
	}
	
	/**
	 * 返回对应项目的工时，如周检或值班的工时。只有HourCounter计算之后才有工时信息
	 * @param name
	 * @return
	 */
	public double getHoursByName(String name) {
		if(hourList.containsKey(name)) {
			return hourList.get(name);
		}
		return -1;
	}
	
	/**
	 * 添加对应项目的工时，如常检或周检等
	 * @param name
	 * @param hours
	 */
	public void addHoursByName(String name, double hours) {
		if(hourList.containsKey(name)) {
			hourList.put(name, hourList.get(name) + hours);
		}else {
			hourList.put(name, hours);
		}
	}
	
	/**
	 * 获取累计的总工时，包括上月积余的
	 * @return
	 */
	public double getTotalHours() {
		double tot = this.preHours;
		for(String name: hourList.keySet()) {
			tot += hourList.get(name);
		}
		return tot;
	}
	
	/**
	 * 读取通讯录信息，初始化助理信息列表
	 * @param excelPath
	 * @return 助理信息列表，有时候也会被说成通讯录
	 * @throws Exception 格式不对
	 */
	public static List<Worker> initWorkerList(String excelPath) throws Exception {
		List<Worker> workerList = new ArrayList<Worker>();
		Map<String, Integer> title2Col = new HashMap<String, Integer>();
		Exception formatException = new Exception("通讯录格式错误！");
		
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(excelPath));
		XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
		
		XSSFRow titleRow = sheet.getRow(0);
		for(int i = 0; i <= 5; i++) {
			XSSFCell cell = titleRow.getCell(i);
			if(cell == null) {
				throw formatException;
			}
			title2Col.put(cell.getStringCellValue(), i);
		}
		if(!title2Col.containsKey("姓名") || !title2Col.containsKey("学号") || !title2Col.containsKey("银行账号") || 
				!title2Col.containsKey("组别") || !title2Col.containsKey("电话") || !title2Col.containsKey("上月积余")) {
			throw formatException;
		}
		
		for(int i = 1; i <= sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			Worker worker = new Worker();
			
			for(int j = 0; j < 4; j++) {
				row.getCell(j).setCellType(CellType.STRING);
			}
			
			if(row.getCell(title2Col.get("学号")) != null) {				
				worker.studentID = row.getCell(title2Col.get("学号")).getStringCellValue();
			}
			if(row.getCell(title2Col.get("姓名")) != null) {				
				worker.name = row.getCell(title2Col.get("姓名")).getStringCellValue();
			}else {
				throw new Exception("通讯录中助理信息必须包含姓名");
			}
			if(row.getCell(title2Col.get("银行账号")) != null) {				
				worker.bankcard = row.getCell(title2Col.get("银行账号")).getStringCellValue();
			}
			if(row.getCell(title2Col.get("电话")) != null) {				
				worker.phone = row.getCell(title2Col.get("电话")).getStringCellValue();
			}
			if(row.getCell(title2Col.get("组别")) != null) {
				if(row.getCell(title2Col.get("组别")).getStringCellValue().equals("系统组")) {
					worker.group = Group.SYSTEM;
				}else if(row.getCell(title2Col.get("组别")).getStringCellValue().equals("管理组")) {
					worker.group = Group.ADMIN;
				}
			} 
			if(row.getCell(title2Col.get("上月积余")) != null) {
				row.getCell(title2Col.get("上月积余")).setCellType(CellType.NUMERIC);
				worker.preHours = row.getCell(title2Col.get("上月积余")).getNumericCellValue();
			}
			
			workerList.add(worker);
		}
		
		return workerList;
	}
	
//	public static void main(String[] args) {
//		List<Worker> workers = initWorkerList("C:\\Users\\18196\\Desktop\\3rd\\工时\\5月\\通讯录.xlsx");
//		System.out.println(workers.size());
//		System.out.println(workers.get(0).name);
//		System.out.println(workers.get(0).studentID);
//		System.out.println(workers.get(0).bankcard);
//		System.out.println(workers.get(0).phone);
//	}
}
