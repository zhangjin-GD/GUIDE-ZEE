package guide.webclient.beans.invuse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import guide.app.common.CommonUtil;
import guide.app.inventory.UDInvUseLine;
import guide.app.inventory.UDInvUseLineSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelMatUseOTDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean tableLine = app.getDataBean("main_invuselinetab_table");
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
		tableLine.reloadTable();
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
//		int countadd = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		MboRemote mbo = this.app.getAppBean().getMbo();
		String status = mbo.getString("status");
		String udcompany = mbo.getString("udcompany");
		if (!"ENTERED".equals(status)) {
			throw new MXApplicationException("guide", "1066");
		}
		UDInvUseLineSet lineSet = (UDInvUseLineSet) mbo.getMboSet("INVUSELINE");
//		if (lineSet != null && !lineSet.isEmpty()) {
//			lineSet.deleteAll(2L);
//		}
		for (int rowCount = 3; rowCount <= lastRowNum; rowCount++) {
			int rownum = rowCount + 1;
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c1 = row.getCell(1);// 物资编码
				Cell c3 = row.getCell(3);// 设备编号
				Cell c5 = row.getCell(5);// 数量
				Cell c6 = row.getCell(6);// 备注

				String value1 = "";
				if (c1 != null) {
					c1.setCellType(CellType.STRING);
					value1 = c1.getStringCellValue().trim();
				}
				String value3 = "";
				if (c3 != null) {
					c3.setCellType(CellType.STRING);
					value3 = c3.getStringCellValue().trim();
				}
				double value5 = 1;
				if (c5 != null) {
					c5.setCellType(CellType.NUMERIC);
					value5 = c5.getNumericCellValue();
				}
				String value6 = "";
				if (c6 != null) {
					c6.setCellType(CellType.STRING);
					value6 = c6.getStringCellValue().trim();
				}

				String itemnum = "";
				if (!value1.isEmpty()) {
					itemnum = CommonUtil.getValue("ITEM", "ITEMNUM='" + value1 + "' AND STATUS ='ACTIVE'", "ITEMNUM");
					if (itemnum == null) {
						Object[] obj = { " B - " + rownum };
						throw new MXApplicationException("guide", "1099", obj);
					}
				}
				String assetnum = "";
				if (!value3.isEmpty()) {
					assetnum = CommonUtil.getValue("ASSET",
							"ASSETNUM ='" + value3 + "' AND UDCOMPANY='" + udcompany + "' AND STATUS='ACTIVE'",
							"ASSETNUM");
					if (assetnum == null) {
						Object[] obj = { " D - " + rownum };
						throw new MXApplicationException("guide", "1100", obj);
					}
				}

				MboSetRemote invbalSet = mbo.getMboSet("INVBALANCESOUTOT");
				invbalSet.setWhere("itemnum='" + value1 + "'");
				invbalSet.setOrderBy("itemnum,physcntdate");
				invbalSet.reset();

				double curbalSum = invbalSet.sum("curbal");

				if ((curbalSum - value5) >= 0) {
					if (!invbalSet.isEmpty() && invbalSet.count() > 0) {
						double curbalNew = 0;
						for (int i = 0; invbalSet.getMbo(i) != null; i++) {
							MboRemote invbal = invbalSet.getMbo(i);
							itemnum = invbal.getString("itemnum");
							String binnum = invbal.getString("binnum");
							String lotnum = invbal.getString("lotnum");
							double curbal = invbal.getDouble("curbal");
							double surplusQty = value5 - curbalNew;// 剩余数量
							double quantity;
							if (curbal >= (surplusQty)) {
								quantity = surplusQty;
							} else {
								quantity = curbal;
							}
							UDInvUseLine line = (UDInvUseLine) lineSet.addAtEnd();
							line.setValue("itemnum", itemnum, 2L);
							line.setValue("frombin", binnum, 2L);
							line.setValue("fromlot", lotnum, 2L);
							line.setValue("quantity", quantity, 2L);
							line.setValue("assetnum", assetnum, 2L);
							line.setValue("remark", value6, 11L);
							curbalNew += quantity;
						}
					}
				} else {
					Object[] obj = { " B - " + rownum };
					throw new MXApplicationException("guide", "1094", obj);
				}
//				countadd++;
			}
		}
//		String params = "总行数：" + (lastRowNum - 2) + "条，已导入" + countadd + "条！";
//		clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示", params, 1);
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
