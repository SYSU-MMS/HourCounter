package application;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.Worker.Group;

/**
 * 计算工时的类，集合各类信息，计算出助理的最终工时
 * @author Syderny
 *
 */
public class HourCounter {
	private double maxHours;
	
	private List<Worker> workerList;
	private List<Date> workDates;
	
	private List<DutyTable> dutyTables;
	private List<DailyTable> dailyTables;
	private List<Date> weeklyDates;
	private List<String> hourNames;				// 保存不同工时累积的表头，比如测光、全员大会、值班等
	
	private List<Date> dutyRestDates;
	private List<Date> dailyRestDates;
	
	private Map<Date, Integer> dutyChangeDates;
	private Map<Date, Integer> dailyChangeDates;

	private List<ExtraHourList> extraHourLists;
	
	public static final double DAILY_CHECK_HOURS = 2;
	public static final double WEEKLY_CHECK_HOURS_PER_ROOM = 0.5;
	public static final double GROUP_ADMIN_HOURS = 40;
	public static final double GROUP_SYSTEM_HOURS = 20;
	
	public double getMaxHours() {
		return maxHours;
	}
	
	public void setMaxHours(double maxHours) {
		this.maxHours = maxHours;
	}
	
	public List<Date> getWorkDates() {
		return workDates;
	}
	
	public List<Date> getDutyRestDates() {
		return dutyRestDates;
	}
	
	public void setDutyRestDates(List<Date> dutyRestDates) {
		this.dutyRestDates = dutyRestDates;
	}
	
	public List<Date> getDailyRestDates() {
		return dailyRestDates;
	}
	
	public void setDailyRestDates(List<Date> dailyRestDates) {
		this.dailyRestDates = dailyRestDates;
	}
	
	public Map<Date, Integer> getDutyChangeDates() {
		return dutyChangeDates;
	}
	
	public List<String> getHourNames() {
		return hourNames;
	}
	
	public void setDutyChangeDates(Map<Date, Integer> dutyChangeDates) {
		this.dutyChangeDates = dutyChangeDates;
	}
	
	public Map<Date, Integer> getDailyChangeDates() {
		return dailyChangeDates;
	}
	
	public void setDailyChangeDates(Map<Date, Integer> dailyChangeDates) {
		this.dailyChangeDates = dailyChangeDates;
	}
	
	public List<Worker> getWorkerList() {
		return workerList;
	}
	
	public List<DutyTable> getDutyTable() {
		return dutyTables;
	}
	
	public List<DailyTable> getDailyTable() {
		return dailyTables;
	}
	
	public List<ExtraHourList> getExtraHourLists() {
		return extraHourLists;
	}
	
	public void setExtraHourLists(List<ExtraHourList> extraHourLists) {
		this.extraHourLists = extraHourLists;
	}
	
	public List<Date> getWeeklyDates() {
		return weeklyDates;
	}
	
	/**
	 * 计算工时
	 * @throws Exception
	 */
	public void count() throws Exception {
		// 先清空助理之前的工时
		for(Worker worker: this.workerList) {
			worker.cleanHours();
		}
		
		// 添加工时项目的表头
		this.hourNames.add("常检");
		this.hourNames.add("周检");
		this.hourNames.add("值班");
		this.hourNames.add("系统管理组");
		
		for(Date workDate : workDates) {
			this.countDutyByDate(workDate);
			this.countDailyByDate(workDate);
		}
		this.countWeekly();
		this.countSystemAdminGroup();
		
		// 计算除了基础值班、常检周检外的工时
		for(ExtraHourList extraList: this.extraHourLists) {
			this.hourNames.add(extraList.title);
			this.countExtra(extraList);
		}
		
		List<Worker> removeList = new ArrayList<Worker>();
		for(Worker worker: this.workerList) {
			// 没常检组别的都是C组
			double totalHours = worker.getTotalHours();
			if(worker.group == null && totalHours != 0) {
				worker.group = Group.C;
			}
			if(totalHours == 0) {
				removeList.add(worker);
			}
			worker.finalHours = Math.min(totalHours, this.maxHours);
			worker.restHours = totalHours - worker.finalHours;
		}
		
		// 移除完全没有工时的助理
		this.workerList.removeAll(removeList);
	}
	
	/**
	 * 计算管理组和系统组的工时
	 */
	private void countSystemAdminGroup() {
		List<Worker> groupSystem = this.getWorkersByGroup(Group.SYSTEM);
		List<Worker> groupAdmin = this.getWorkersByGroup(Group.ADMIN);
		this.addWorkersHours(groupSystem, "系统管理组", GROUP_SYSTEM_HOURS);
		this.addWorkersHours(groupAdmin, "系统管理组", GROUP_ADMIN_HOURS);
	}
	
	/**
	 * 计算除常规工时外的工时项目，如测光和旷工等
	 * @param extraList
	 */
	private void countExtra(ExtraHourList extraList) {
		Map<Worker, Double> hourList = extraList.extraHours;
		for(Worker worker: hourList.keySet()) {
			double hours = hourList.get(worker);
			worker.addHoursByName(extraList.title, hours);
		}
	}
	
	/**
	 * 计算周检工时，按常检表的教室数来算
	 * @throws Exception 常检表的有效日期不包含本日，可能漏设置了
	 */
	private void countWeekly() throws Exception {
		for(Date date: this.weeklyDates) {
			boolean contain = false;
			for(DailyTable dailyTable: this.dailyTables) {
				if(dailyTable.containsDate(date)) {
					for(DailyWorker dailyWorker: dailyTable.getTable()) {
						dailyWorker.worker.addHoursByName("周检", dailyWorker.numRooms * WEEKLY_CHECK_HOURS_PER_ROOM);
					}
					contain = true;
					break;
				}
			}
			if(!contain) {
				throw new Exception("未找到日期："+date+"的常检周检信息");
			}
		}
	}
	
	private List<Worker> getWorkersByGroup(Group group){
		List<Worker> groupWorkers = new ArrayList<Worker>();
		for(Worker worker: this.workerList) {
			if(worker.group == group) {
				groupWorkers.add(worker);
			}
		}
		return groupWorkers;
	}
	
	private void addWorkersHours(List<Worker> workers, String hourName, double hours) {
		for(Worker worker: workers) {
			worker.addHoursByName(hourName, hours);
		}
	}
	
	private void countDutyByDate(Date workDate) throws Exception {
		if(this.dutyRestDates.contains(workDate) && !this.dutyChangeDates.containsKey(workDate)) {
			return;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(workDate);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		if(this.dutyChangeDates.containsKey(workDate)) {
			day = this.dutyChangeDates.get(workDate);
		}
		
		boolean contain = false;
		for(DutyTable dutyTable: this.dutyTables) {
			if(dutyTable.containsDate(workDate)) {
				List<DutyPeriod> dayDutyTable = dutyTable.getTableByDay(day);
				for(DutyPeriod period: dayDutyTable) {
					for(Worker workers: period.workers) {
						workers.addHoursByName("值班", period.hours);
					}
				}
				contain = true;
				break;
			}
		}
		if(!contain) {
			throw new Exception("未找到日期："+workDate+"的值班信息");
		}
		
	}
	
	private void countDailyByDate(Date workDate) throws Exception {
		if(dailyRestDates.contains(workDate) && !this.dailyChangeDates.containsKey(workDate)) {
			return;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(workDate);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		if(this.dailyChangeDates.containsKey(workDate)) {
			day = this.dailyChangeDates.get(workDate);
		}
		
		Group group;
		if(day == Calendar.MONDAY || day == Calendar.WEDNESDAY || day == Calendar.FRIDAY) {
			group = Group.A;
		}else if(day == Calendar.TUESDAY || day == Calendar.THURSDAY) {
			group = Group.B;
		}else {
			return;
		}
		
		boolean contain = false;
		for(DailyTable dailyTable: this.dailyTables) {
			if(dailyTable.containsDate(workDate)) {
				List<Worker> groupWorkers = dailyTable.getWorkersByGroup(group);
				this.addWorkersHours(groupWorkers, "常检", DAILY_CHECK_HOURS);
				contain = true;
				break;
			}
		}
		if(!contain) {
			throw new Exception("未找到日期："+workDate+"的常检周检信息");
		}
	}
	
	HourCounter(List<Worker> workerList, List<DutyTable> dutyTables, List<DailyTable> dailyTables,
			List<Date> workDates, List<Date> weeklyDates, int maxHours) {
		this.workerList = workerList;
		this.dutyTables = dutyTables;
		this.dailyTables = dailyTables;
		this.workDates = workDates;
		this.weeklyDates = weeklyDates;
		this.maxHours = maxHours;
		
		this.hourNames = new ArrayList<String>();
		this.dutyRestDates = new ArrayList<Date>();
		this.dailyRestDates = new ArrayList<Date>();
		this.dutyChangeDates = new HashMap<Date, Integer>();
		this.dailyChangeDates = new HashMap<Date, Integer>();
		this.extraHourLists = new ArrayList<ExtraHourList>();
	}

}

