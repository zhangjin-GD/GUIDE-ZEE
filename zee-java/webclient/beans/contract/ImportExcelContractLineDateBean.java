package guide.webclient.beans.contract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import guide.app.common.CommonUtil;
import guide.app.contract.ContractLine;
import guide.app.contract.ContractLineSet;
import psdi.mbo.MboRemote;
import psdi.security.UserInfo;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelContractLineDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
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
			readXls(sFile);
			// 删除EXCEL
			deleteXLS(uploadfile);
		} catch (IOException e) {
			deleteXLS(uploadfile);
			e.printStackTrace();
		}
		this.app.getAppBean().save();
		return 1;
	}

	private void readXls(String fileName) throws RemoteException, MXException {

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
			readWorkBook(workbook);

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

	private void readWorkBook(Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		int countadd = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		MboRemote mbo = this.app.getAppBean().getMbo();
		UserInfo userInfo = mbo.getUserInfo();
		String status = mbo.getString("status");
		if (!"WAPPR".equals(status)) {
			throw new MXApplicationException("guide", "1066");
		}
		ContractLineSet lineSet = (ContractLineSet) mbo.getMboSet("UDCONTRACTLINE");
		if (lineSet != null && !lineSet.isEmpty()) {
			lineSet.deleteAll(11L);
		}
		if(userInfo.getLangCode().equalsIgnoreCase("EN")){
			for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
				int rownum = rowCount + 1;
				Row row = sheet.getRow(rowCount);
				if (row != null) {
					Cell c1 = row.getCell(1);// 物料编码
					Cell c2 = row.getCell(2);// 物资名称或服务内容
					Cell c3 = row.getCell(3);// 数量
					Cell c4 = row.getCell(4);// 单位
					Cell c5 = row.getCell(5);// 税率
					Cell c6 = row.getCell(6);// 含税单价
					Cell c7 = row.getCell(7);// 备注信息
					
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
					double value3 = 1;
					if (c3 != null) {
						c3.setCellType(CellType.NUMERIC);
						value3 = c3.getNumericCellValue();
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
					double value6 = 0;
					if (c6 != null) {
						c6.setCellType(CellType.NUMERIC);
						value6 = c6.getNumericCellValue();
					}
					String value7 = "";
					if (c7 != null) {
						c7.setCellType(CellType.STRING);
						value7 = c7.getStringCellValue().trim();
					}

					String itemnum = "";
					if (!value1.isEmpty()) {
						itemnum = CommonUtil.getValue("ITEM", "ITEMNUM='" + value1 + "' AND STATUS ='ACTIVE'", "ITEMNUM");
						if (itemnum == null) {
							Object[] obj = { "Warm reminder：" + rownum + "Row B column,Not in the material unified construction ledger!" };
							throw new MXApplicationException("udmessage", "error1", obj);
						}
					}

					String orderunit = CommonUtil.getValue("MEASUREUNIT", "MEASUREUNITID='" + value4 + "'",
							"MEASUREUNITID");
					if (orderunit == null) {
						Object[] obj = { "Warm reminder：" + rownum + "Row E column,Unit does not exist!" };
						throw new MXApplicationException("udmessage", "error1", obj);
					}

					String taxcode = CommonUtil.getValue("TAX", "TAXCODE='" + value5 + "'", "TAXCODE");
					if (taxcode == null) {
						Object[] obj = { "Warm reminder：" + rownum + "Row F column,Tax rate does not exist!" };
						throw new MXApplicationException("udmessage", "error1", obj);
					}
					ContractLine line = (ContractLine) lineSet.add();
					if (!itemnum.isEmpty()) {
						line.setValue("LINETYPE", "ITEM", 11L);
					} else {
						line.setValue("LINETYPE", "SERVICE", 11L);
					}
					line.setValue("description", value2, 11L);
					line.setValue("itemnum", itemnum, 2L);
					line.setValue("orderqty", value3, 2L);
					line.setValue("orderunit", orderunit, 11L);
					line.setValue("tax1code", taxcode, 2L);
					line.setValue("totalunitcost", value6, 2L);
					line.setValue("remarks", value7, 11L);
					countadd++;
				}
			}
			String params = "Total number of rows：" + (lastRowNum - 1) + " article, imported " + countadd + " article！";
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "Warm reminder:", params, 1);
		}else{
			for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
				int rownum = rowCount + 1;
				Row row = sheet.getRow(rowCount);
				if (row != null) {
					Cell c1 = row.getCell(1);// 物料编码
					Cell c2 = row.getCell(2);// 物资名称或服务内容
					Cell c3 = row.getCell(3);// 数量
					Cell c4 = row.getCell(4);// 单位
					Cell c5 = row.getCell(5);// 税率
					Cell c6 = row.getCell(6);// 含税单价
					Cell c7 = row.getCell(7);// 备注信息
					
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
					double value3 = 1;
					if (c3 != null) {
						c3.setCellType(CellType.NUMERIC);
						value3 = c3.getNumericCellValue();
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
					double value6 = 0;
					if (c6 != null) {
						c6.setCellType(CellType.NUMERIC);
						value6 = c6.getNumericCellValue();
					}
					String value7 = "";
					if (c7 != null) {
						c7.setCellType(CellType.STRING);
						value7 = c7.getStringCellValue().trim();
					}

					String itemnum = "";
					if (!value1.isEmpty()) {
						itemnum = CommonUtil.getValue("ITEM", "ITEMNUM='" + value1 + "' AND STATUS ='ACTIVE'", "ITEMNUM");
						if (itemnum == null) {
							Object[] obj = { "温馨提示：" + rownum + "行 B列,不在物资统建台账中！" };
							throw new MXApplicationException("udmessage", "error1", obj);
						}
					}

					String orderunit = CommonUtil.getValue("MEASUREUNIT", "MEASUREUNITID='" + value4 + "'",
							"MEASUREUNITID");
					if (orderunit == null) {
						Object[] obj = { "温馨提示：" + rownum + "行 E列,单位不存在！" };
						throw new MXApplicationException("udmessage", "error1", obj);
					}

					String taxcode = CommonUtil.getValue("TAX", "TAXCODE='" + value5 + "'", "TAXCODE");
					if (taxcode == null) {
						Object[] obj = { "温馨提示：" + rownum + "行 F列,税率不存在！" };
						throw new MXApplicationException("udmessage", "error1", obj);
					}
					ContractLine line = (ContractLine) lineSet.add();
					if (!itemnum.isEmpty()) {
						line.setValue("LINETYPE", "ITEM", 11L);
					} else {
						line.setValue("LINETYPE", "SERVICE", 11L);
					}
					line.setValue("description", value2, 11L);
					line.setValue("itemnum", itemnum, 2L);
					line.setValue("orderqty", value3, 2L);
					line.setValue("orderunit", orderunit, 11L);
					line.setValue("tax1code", taxcode, 2L);
					line.setValue("totalunitcost", value6, 2L);
					line.setValue("remarks", value7, 11L);
					countadd++;
				}
			}
			String params = "总行数：" + (lastRowNum - 1) + "条，已导入" + countadd + "条！";
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示", params, 1);
		}
		
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
