package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 表格的历史调用信息
 * @author Syderny
 *
 */
class TableInfo {
	String path, startDate, endDate;
	TableInfo(){
		this.path = "";
		this.startDate = "";
		this.endDate = "";
	}
	TableInfo(String path, String startDate, String endDate){
		this.path = path;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}

/**
 * 用于操作历史文件读取信息的类，下一次选择文件时自动定位历史位置，或者直接读取
 * @author Syderny
 * 使用了Java DOM来操作一个同目录下的history.xml文件
 */
public class HistoryIO {
	
	public static final String HISTORY_XML_PATH = "history.xml";
	
	private static Document readDocument() throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(HISTORY_XML_PATH));
		document.normalize();
		return document;
	}
	
	private static void writeDocument(Document document) throws Exception {
		document.normalize();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(document.getFirstChild());
		transformer.transform(source, new StreamResult(new File(HISTORY_XML_PATH)));
	}
	
	public static String readWorkerListFileInfo() throws Exception {
		Document document = readDocument();
		Element workerListEle = (Element) document.getElementsByTagName("workerList").item(0);
		String path = workerListEle.getElementsByTagName("path").item(0).getTextContent();
		return path;
	}
	
	public static List<TableInfo> readDutyTableFileInfo() throws Exception {
		List<TableInfo> infoList = new ArrayList<TableInfo>();
		
		Document document = readDocument();
		for(int i = 0; i < document.getElementsByTagName("dutyTable").getLength(); i++) {
			TableInfo info = new TableInfo();
			Element dutyTableEle = (Element) document.getElementsByTagName("dutyTable").item(i);
			info.path = dutyTableEle.getElementsByTagName("path").item(0).getTextContent();
			info.startDate = dutyTableEle.getElementsByTagName("startDate").item(0).getTextContent();
			info.endDate = dutyTableEle.getElementsByTagName("endDate").item(0).getTextContent();
			infoList.add(info);
		}
		
		return infoList;
	}
		
	public static List<TableInfo> readDailyTableFileInfo() throws Exception {
		List<TableInfo> infoList = new ArrayList<TableInfo>();
		
		Document document = readDocument();
		for(int i = 0; i < document.getElementsByTagName("dailyTable").getLength(); i++) {
			TableInfo info = new TableInfo();
			Element dailyTableEle = (Element) document.getElementsByTagName("dailyTable").item(i);
			info.path = dailyTableEle.getElementsByTagName("path").item(0).getTextContent();
			info.startDate = dailyTableEle.getElementsByTagName("startDate").item(0).getTextContent();
			info.endDate = dailyTableEle.getElementsByTagName("endDate").item(0).getTextContent();
			infoList.add(info);
		}
		
		return infoList;
	}
	
	public static String readExportFileInfo() throws Exception {
		Document document = readDocument();
		Element workerListEle = (Element) document.getElementsByTagName("exportFile").item(0);
		String path = workerListEle.getElementsByTagName("path").item(0).getTextContent();
		return path;
	}
	
	public static void writeWorkerListFileInfo(String path) throws Exception {
		Document document = readDocument();
		Element workerListEle = (Element) document.getElementsByTagName("workerList").item(0);
		workerListEle.getElementsByTagName("path").item(0).setTextContent(path);
		
		writeDocument(document);
	}
	
	public static void writeDutyTableFileInfo(List<TableInfo> infoList) throws Exception {
		Document document = readDocument();
		Element root = document.getDocumentElement();
		
		int length = document.getElementsByTagName("dutyTable").getLength();
		for(int i = 0; i < length; i++) {
			Element oldDutyTableEle = (Element) document.getElementsByTagName("dutyTable").item(0);
			root.removeChild(oldDutyTableEle);
		}
		
		for(TableInfo info: infoList) {
			Element newDutyTableEle = (Element) document.createElement("dutyTable");
			
			Element pathEle = document.createElement("path");
			pathEle.setTextContent(info.path);
			newDutyTableEle.appendChild(pathEle);
			
			Element startDateEle = document.createElement("startDate");
			startDateEle.setTextContent(info.startDate);
			newDutyTableEle.appendChild(startDateEle);
			
			Element endDateEle = document.createElement("endDate");
			endDateEle.setTextContent(info.endDate);
			newDutyTableEle.appendChild(endDateEle);
			
			root.appendChild(newDutyTableEle);
		}
		
		writeDocument(document);
	}
	
	public static void writeDailyTableFileInfo(List<TableInfo> infoList) throws Exception {
		Document document = readDocument();
		Element root = document.getDocumentElement();		
		
		int length = document.getElementsByTagName("dailyTable").getLength();
		for(int i = 0; i < length; i++) {
			Element oldDailyTableEle = (Element) document.getElementsByTagName("dailyTable").item(0);
			root.removeChild(oldDailyTableEle);
		}
		
		for(TableInfo info: infoList) {
			Element newDailyTableEle = (Element) document.createElement("dailyTable");
			
			Element pathEle = document.createElement("path");
			pathEle.setTextContent(info.path);
			newDailyTableEle.appendChild(pathEle);
			
			Element startDateEle = document.createElement("startDate");
			startDateEle.setTextContent(info.startDate);
			newDailyTableEle.appendChild(startDateEle);
			
			Element endDateEle = document.createElement("endDate");
			endDateEle.setTextContent(info.endDate);
			newDailyTableEle.appendChild(endDateEle);
			
			root.appendChild(newDailyTableEle);
		}
		
		writeDocument(document);
	}
	
	public static void writeExportFileInfo(String path) throws Exception {
		Document document = readDocument();
		Element workerListEle = (Element) document.getElementsByTagName("exportFile").item(0);
		workerListEle.getElementsByTagName("path").item(0).setTextContent(path);
		
		writeDocument(document);
	}
	
//	public static void main(String[] args) {
//		List<TableInfo> infoList = new ArrayList<TableInfo>();
//		TableInfo info1 = new TableInfo();
//		info1.path = "path";
//		info1.startDate = "s";
//		info1.endDate = "e";
//		TableInfo info2 = new TableInfo();
//		info2.path = "path";
//		info2.startDate = "s";
//		info2.endDate = "e";
//		infoList.add(info2);
//		infoList.add(info1);
//		try {
//			writeDutyTableFileInfo(infoList);
//			System.out.println(readDutyTableFileInfo());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
}