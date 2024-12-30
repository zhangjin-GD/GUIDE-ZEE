package guide.webclient.beans.gpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelPmDateBean extends DataBean {
	private List<String> list = new ArrayList<String>();	

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		
		MboRemote mbo = this.app.getAppBean().getMbo();
		UploadFile uploadfile = (UploadFile) this.app.get("importfile");
		String fullFileName = uploadfile.getFullFileName();
		checkFileType(fullFileName, "excel");
		String absoluteFileName = uploadfile.getAbsoluteFileName();
		if ("".equalsIgnoreCase(fullFileName) && "".equalsIgnoreCase(absoluteFileName)) {
			throw new MXApplicationException("stockTake", "noimportfile");
		}
		Properties properties = getDocLinkProperties();
		String doclinkProperties = properties.getProperty("mxe.doclink.doctypes.defpath");
		uploadfile.setDirectoryName(doclinkProperties.trim());
		try {
			uploadfile.writeToDisk();
			String sFile = uploadfile.getAbsoluteFileName();
			if (absoluteFileName != null) {
				uploadfile.save();
			}
			// 写入值
			readXls(mbo, sFile);
			// 删除EXCEL
			deleteXLS(uploadfile);
		} catch (IOException e) {
			deleteXLS(uploadfile);
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.app.getAppBean().save();
		return 1;
	}

	private void readXls(MboRemote mbo, String fileName) throws RemoteException, MXException, ParseException {

		InputStream fi = null;
		Workbook workbook;
		try {
			File xlsFile = new File(fileName);
			fi = new FileInputStream(xlsFile);
			String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
			if (".xls".equalsIgnoreCase(suffix)) {
				workbook = new HSSFWorkbook(fi);// 创建excel2003以下的文件文本抽取对象
			} else {
				workbook = new XSSFWorkbook(fi);// 创建excel2006以上的文件文本抽取对象
			}
			readWorkBook(mbo, workbook);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw new MXApplicationException("Warning", "IO Exception");
		} finally {
			if (fi != null) {
				try {
					fi.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readWorkBook(MboRemote mbo, Workbook workbook) throws RemoteException, MXException, ParseException {
		MboSetRemote workorderSet = MXServer.getMXServer().getMboSet("WORKORDER",MXServer.getMXServer().getSystemUserInfo());
		
		Sheet sheet = workbook.getSheetAt(0);// 获取excel第一个sheet标签页
		int lastRowNum = sheet.getLastRowNum();// 行
		int countadd = 0;
		String str = "";
		for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
			int rownum = rowCount + 1;

			Row row = sheet.getRow(rowCount);
			if (row != null) {
	
				//必选字段:顺序、WO描述、设备编码、故障开始时间、故障结束时间
				Cell c1 = row.getCell(1);// 工单描述 B
				Cell c2 = row.getCell(2);// 设备编码 C
				Cell c3 = row.getCell(3);// 故障开始时间 D
				Cell c4 = row.getCell(4);// 故障结束时间 E
				
				Cell c5 = row.getCell(5);// 维修负责人 F
				Cell c6 = row.getCell(6);// 事件分类 G
				Cell c7 = row.getCell(7);// 故障层级 H
				Cell c8 = row.getCell(8);// 故障类别 I
				Cell c9 = row.getCell(9);// 故障现象 J
				Cell c10 = row.getCell(10);// 故障原因 K
				Cell c11 = row.getCell(11);// 故障方案 L
				Cell c12 = row.getCell(12);// 故障分析 M
				Cell c13 = row.getCell(13);// 待处理项目/跟进 N
				Cell c14 = row.getCell(14);// 转变 O

				String value1 = "";
				if (c1 != null) {
					c1.setCellType(CellType.STRING);
					value1 = c1.getStringCellValue().trim();
				}		

				String value2 = "";
				if (c2 != null) {
					c2.setCellType(CellType.STRING);
					value2 = c2.getStringCellValue().trim();
				}

				String value3 = "";
				if (c3 != null) {
					c3.setCellType(CellType.STRING);
					value3 = c3.getStringCellValue().trim();

				}

				String value4 = "";
				if (c4 != null) {
					c4.setCellType(CellType.STRING);
					value4 = c4.getStringCellValue().trim();

				}
				String value5 = "";
				if (c5 != null) {
					c5.setCellType(CellType.STRING);
					value5 = c5.getStringCellValue().trim();

				}
				
				String value6 = "";
				if (c6 != null) {
					c6.setCellType(CellType.STRING);
					value6 = c6.getStringCellValue().trim();

				}
				
				String value7 = "";
				if (c7 != null) {
					c7.setCellType(CellType.STRING);
					value7 = c7.getStringCellValue().trim();

				}
				
				String value8 = "";
				if (c8 != null) {
					c8.setCellType(CellType.STRING);
					value8 = c8.getStringCellValue().trim();

				}
				
				String value9 = "";
				if (c9 != null) {
					c9.setCellType(CellType.STRING);
					value9 = c9.getStringCellValue().trim();

				}
				
				String value10 = "";
				if (c10 != null) {
					c10.setCellType(CellType.STRING);
					value10 = c10.getStringCellValue().trim();

				}
				
				String value11 = "";
				if (c11 != null) {
					c11.setCellType(CellType.STRING);
					value11 = c11.getStringCellValue().trim();

				}
				
				String value12 = "";
				if (c12 != null) {
					c12.setCellType(CellType.STRING);
					value12 = c12.getStringCellValue().trim();

				}
				
				String value13 = "";
				if (c13 != null) {
					c13.setCellType(CellType.STRING);
					value13 = c13.getStringCellValue().trim();

				}
				
				String value14 = "";
				if (c14 != null) {
					c14.setCellType(CellType.STRING);
					value14 = c14.getStringCellValue().trim();

				}

				
				//必填判断 ：顺序、WO描述、设备编码、故障开始时间、故障结束时间
				if (value1.isEmpty()) {
					str+="Warm reminder：Row " + rownum + " Column B, cannot be empty！ \n";
					list.add(rownum + "");
				}
				
				if (value2.isEmpty()) {
					str+="Warm reminder：Row " + rownum + " Column C, cannot be empty！ \n";
					list.add(rownum + "");
				}
				
				if (value3.isEmpty()) {
					str+="Warm reminder：Row " + rownum + " Column D, cannot be empty！ \n";
					list.add(rownum + "");
				}
				
				if (value4.isEmpty()) {
					str+="Warm reminder：Row " + rownum + " Column E, cannot be empty！ \n";
					list.add(rownum + "");
				}
				
				//校验系统是否存在：设备编码、维修负责人
				String assetnum = "";
				
				if (!value2.isEmpty()) {
					assetnum = CommonUtil.getValue("ASSET", " ASSETNUM='" + value2 + "'","assetnum");	
					
					if (assetnum == null) {
						str+="Warm reminder：Row " + rownum + " Column C, Equipment ledger does not exist！ \n";//设备台账不存在
						list.add(rownum + "");
					}
				}
				
				String lead = "";
				if (!value5.isEmpty()) {
					lead = CommonUtil.getValue("PERSON", " PERSONID='" + value5 + "'","personid");
					if (lead == null) {
						str+="Warm reminder：Row " + rownum + " Column F, User does not exist！ \n";//用户不存在
						list.add(rownum + "");
					}
				}
			}
		}
		
		/*1.根据设备编码自动带出设备分类
		  2.故障开始时间=创建日期、故障开始时间=维修开始时间、故障结束时间=维修结束时间*/
		String udassettypecode=""; //设备分类
		if (list.isEmpty() && list.size() == 0) {
			
			for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
				
				Row row = sheet.getRow(rowCount);
				
				String value6 = "";
				if (row.getCell(6) != null) {
					row.getCell(6).setCellType(CellType.STRING);
					value6 = row.getCell(6).getStringCellValue().trim();

				}
				
				String value7 = "";
				if (row.getCell(7) != null) {
					row.getCell(7).setCellType(CellType.STRING);
					value7 = row.getCell(7).getStringCellValue().trim();

				}
				
				String value8 = "";
				if (row.getCell(8) != null) {
					row.getCell(8) .setCellType(CellType.STRING);
					value8 = row.getCell(8).getStringCellValue().trim();

				}
				
				String value9 = "";
				if (row.getCell(9) != null) {
					row.getCell(9).setCellType(CellType.STRING);
					value9 = row.getCell(9).getStringCellValue().trim();

				}
				
				String value10 = "";
				if (row.getCell(10) != null) {
					row.getCell(10).setCellType(CellType.STRING);
					value10 = row.getCell(10).getStringCellValue().trim();

				}
				
				String value11 = "";
				if (row.getCell(11) != null) {
					row.getCell(11).setCellType(CellType.STRING);
					value11 = row.getCell(11).getStringCellValue().trim();

				}
				
				String value12 = "";
				if (row.getCell(12) != null) {
					row.getCell(12).setCellType(CellType.STRING);
					value12 = row.getCell(12).getStringCellValue().trim();

				}
				
				String value13 = "";
				if (row.getCell(13) != null) {
					row.getCell(13).setCellType(CellType.STRING);
					value13 = row.getCell(13).getStringCellValue().trim();

				}
				
				String value14 = "";
				if (row.getCell(14) != null) {
					row.getCell(14).setCellType(CellType.STRING);
					value14 = row.getCell(14).getStringCellValue().trim();

				}
				
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date3 = formatter.parse(getCellString(row.getCell(3), 0));
				Date date4 = formatter.parse(getCellString(row.getCell(4), 0));
				
				udassettypecode = CommonUtil.getValue("ASSET", " ASSETNUM='" + getCellString(row.getCell(2), 0) + "'","udassettypecode");			
				MboRemote workorder = workorderSet.add();
				workorder.setValue("worktype", "EM", 11L);
				workorder.setValue("status", "COMP", 11L);
				workorder.setValue("reportedby", clientSession.getUserInfo().getPersonId(), 2L);
				workorder.setValue("description", getCellString(row.getCell(1), 0), 11L);
				workorder.setValue("assetnum", getCellString(row.getCell(2), 0), 11L);
				workorder.setValue("udassettypecode", udassettypecode, 11L);
				workorder.setValue("targstartdate", date3, 11L);
				workorder.setValue("targcompdate", date4, 2L);
				workorder.setValue("reportdate", date3, 11L);
				workorder.setValue("actstart", date3, 2L);
				workorder.setValue("actfinish", date4, 2L);
				workorder.setValue("lead", getCellString(row.getCell(5), 0), 11L);
				workorder.setValue("udwoanalysis", value6, 11L);
				workorder.setValue("udfailmech", value7, 11L);
				workorder.setValue("udfailtype", value8, 11L);
				workorder.setValue("udfailproblemdesc", value9, 11L);
				workorder.setValue("udfailcausedesc", value10, 11L);
				workorder.setValue("udfailremedydesc", value11, 11L);
				workorder.setValue("udfailanalysis", value12, 11L);
				workorder.setValue("udpditem", value13, 11L);
				workorder.setValue("udshiftpct", value14, 11L);
				workorderSet.save();
				
				countadd++;
			}
			workorderSet.close();
		}else{
			Object[] obj = { str };//用户不存在
			throw new MXApplicationException("udmessage", "error1", obj);
		}
		
		String params = (lastRowNum - 1) + " Emergency Repair Work Order have been imported !";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "Warm reminder", params, 1);
	

	}

	private void deleteXLS(UploadFile uploadfile) {

		uploadfile.delete();
		uploadfile.save();
	}

	private Properties getDocLinkProperties() {
		Properties properties = new Properties();
		try {
			InputStream inputstream = getClass().getResourceAsStream("/doclink.properties");
			properties.load(inputstream);
			inputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}
	
	private String getCellString(Cell cell, int celltype) {
		if (cell != null) {
			celltype = cell.getCellType();
		}
		switch (celltype) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue().trim();
		case Cell.CELL_TYPE_NUMERIC:
			return String.valueOf((int) cell.getNumericCellValue());
		}
		return cell.getStringCellValue().trim();
	}

	/**
	 * 查看上传的文件是否为Excel
	 *
	 * @param fileName 文件名
	 * @param fileType 文件类型
	 * @throws MXApplicationException maximo异常提示
	 */
	private void checkFileType(String fileName, String fileType) throws MXApplicationException {

		if ("Excel".equalsIgnoreCase(fileType)) {

			if (!fileName.toUpperCase().endsWith(".XLS") && !fileName.toUpperCase().endsWith(".XLSX")
					&& !fileName.toUpperCase().endsWith(".XLSM")) {
				throw new MXApplicationException("Warning", "This is not a xls file!");
			}
		}
	}
}
