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
/**
 * ZEE - 导入年度合同
 */
public class UDImportContractLineDataBean extends DataBean{
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
		//主表信息
		Cell c10 = sheet.getRow(2).getCell(0);//合同描述
		Cell c11 = sheet.getRow(2).getCell(1);//合同供应商
		Cell c12 = sheet.getRow(2).getCell(2);//合同开始日期
		Cell c13 = sheet.getRow(2).getCell(3);//合同结束日期
		//子表信息
			for (int rowCount = 4; rowCount <= lastRowNum; rowCount++) {
				int rownum = rowCount + 1;
				Row row = sheet.getRow(rowCount);
				if (row != null) {
					Cell c1 = row.getCell(1);// 物料编码
					Cell c2 = row.getCell(2);// 物资名称或服务内容
					Cell c3 = row.getCell(3);// 数量
					Cell c4 = row.getCell(4);// 单位
					Cell c5 = row.getCell(5);// 税率
					Cell c6 = row.getCell(6);// 不含税单价
					Cell c7 = row.getCell(7);// 备注信息
					Cell c8 = row.getCell(8);// 折扣
					Cell c9 = row.getCell(9);// 折后价
					
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
					double value8 = 1;
					if (c8 != null) {
						c8.setCellType(CellType.NUMERIC);
						value8 = c8.getNumericCellValue();
					}
					double value9 = 0;
					if (c9 != null) {
						c9.setCellType(CellType.NUMERIC);
						value9 = c9.getNumericCellValue();
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
					line.setValue("unitcost", value6, 2L);
					line.setValue("remarks", value7, 11L);
					line.setValue("uddiscount", value8, 2L);
					line.setValue("uddiscountprice", value9, 2L);
					countadd++;
				}
			}

			String value10 = "";
			if (c10 != null) {
				c10.setCellType(CellType.STRING);
				value10 = c10.getStringCellValue().trim();
			}
			if (value10.equalsIgnoreCase("")) {
				Object[] obj = { "Warm reminder：The contract description should not be empty in the import excel! " };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
			String purchaseagent = mbo.getUserInfo().getPersonId();
			String value11 = "";
			if (c11 != null) {
				c11.setCellType(CellType.STRING);
				value11 = c11.getStringCellValue().trim();
			}
			if (value11.equalsIgnoreCase("")) {
				Object[] obj = { "Warm reminder：The contract vendor should not be empty in the import excel! " };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
			String value12 = "";
			if (c12 != null) {
				c12.setCellType(CellType.STRING);
				value12 = c12.getStringCellValue().trim();
			}
			if (value12.equalsIgnoreCase("")) {
				Object[] obj = { "Warm reminder：The contract startdate should not be empty in the import excel! " };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
			String value13 = "";
			if (c13 != null) {
				c13.setCellType(CellType.STRING);
				value13 = c13.getStringCellValue().trim();
			}
			if (value13.equalsIgnoreCase("")) {
				Object[] obj = { "Warm reminder：The contract enddate should not be empty in the import excel! " };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
			mbo.setValue("description", value10, 11L);
			mbo.setValue("purchaseagent", purchaseagent, 11L);
			mbo.setValue("vendor", value11, 2L);
			mbo.setValue("startdate", value12, 11L);
			mbo.setValue("enddate", value13, 11L);
			
			String params = "Total number of rows：" + (lastRowNum - 3) + " article, imported " + countadd + " article！";
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "Warm reminder:", params, 1);
		
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
