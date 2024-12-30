package guide.webclient.beans.rfq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
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

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelQuotationLineDateBean extends DataBean {

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
		}
		this.app.getAppBean().save();
		return 1;
	}

	private void readXls(MboRemote mbo, String fileName) throws RemoteException, MXException {

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

	private void readWorkBook(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {

		Sheet sheet = workbook.getSheetAt(0);
		Row row0 = sheet.getRow(0);
		String cell_0_0 = row0.getCell(0).toString();
		if ("询报价信息".equals(cell_0_0)) {
			getSheet1(mbo, workbook);
		} else {
			getSheet2(mbo, workbook);
		}
	}

	private void getSheet1(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();
		for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c2 = row.getCell(2);// 报价时间
				Cell c4 = row.getCell(4);// 行
				Cell c9 = row.getCell(9);// 税代码

				String c0Str = row.getCell(0).toString().trim();

				Date c2Date = MXServer.getMXServer().getDate();
				if (c2.getCellType() == 1) {
					c2Date = c2.getDateCellValue();
				}
				c4.setCellType(Cell.CELL_TYPE_STRING);
				String c4Str = c4.getStringCellValue().trim();

				c9.setCellType(Cell.CELL_TYPE_STRING);
				String c9Str = c9.getStringCellValue().trim();

				double c13Num = 0;
				String c13Str = row.getCell(13).toString();
				if (c13Str != null && !"".equals(c13Str)) {
					c13Num = Double.valueOf(c13Str);
				}

				String c14Str = row.getCell(14).toString().trim();

				int c15Num = 0;
				String c15Str = row.getCell(15).toString();
				if (c15Str != null && !"".equals(c15Str)) {
					c15Num = Double.valueOf(c15Str).intValue();
				}

				String c16Str = row.getCell(16).toString().trim();

				MboSetRemote rfqVendorSet = mbo.getMboSet("RFQVENDOR");
				if (!rfqVendorSet.isEmpty() && rfqVendorSet.count() > 0) {
					for (int i = 0; rfqVendorSet.getMbo(i) != null; i++) {
						MboRemote rfqVendor = rfqVendorSet.getMbo(i);
						MboSetRemote quolineSet = rfqVendor.getMboSet("QUOTATIONLINEVENDOR");
						if (!quolineSet.isEmpty() && quolineSet.count() > 0) {
							for (int j = 0; quolineSet.getMbo(j) != null; j++) {
								MboRemote quoline = quolineSet.getMbo(j);
								String vendor = quoline.getString("vendor");
								String rfqlinenum = quoline.getString("rfqlinenum");
								if (vendor.equalsIgnoreCase(c0Str) && rfqlinenum.equalsIgnoreCase(c4Str)) {
									quoline.setValue("quotestartdate", c2Date, 11L);
									quoline.setValue("tax1code", c9Str, 2L);
									if (c13Num > 0) {
										quoline.setValue("udtotalcost", c13Num, 2L);
									}
									quoline.setValue("memo", c14Str, 11L);
									quoline.setValue("deliverytime", c15Num, 11L);
									quoline.setValue("udbidinfo", c16Str, 11L);
								}
							}
						}
					}
				}
			}
		}
	}

	private void getSheet2(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		// 从第1行开始取值
		List<List<String>> objList = new ArrayList<List<String>>();
		boolean issign = false;
		for (int rowCount = 6; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell cell0 = row.getCell(0);
				Cell cell8 = row.getCell(8);// 税率 I
				Cell cell11 = row.getCell(11);// 含税单价 L
				Cell cell12 = row.getCell(12);// 含税总金额 M
				Cell cell13 = row.getCell(13);// 供货期（天） N
				Cell cell14 = row.getCell(14);// 供应商备注(报价信息) 0
				String value0 = "";// 第一行信息
				if (cell0 != null) {
					cell0.setCellType(CellType.STRING);
					value0 = cell0.getStringCellValue().trim();
				}
				String value8 = "";
				if (cell8 != null) {
					cell8.setCellType(CellType.STRING);
					value8 = cell8.getStringCellValue().trim();
				}
				String value11 = "";
				if (cell11 != null) {
					cell11.setCellType(CellType.STRING);
					value11 = cell11.getStringCellValue().trim();
				}
				String value12 = "";
				if (cell12 != null) {
					cell12.setCellType(CellType.STRING);
					value12 = cell12.getStringCellValue().trim();
				}
				String value13 = "";
				if (cell13 != null) {
					cell13.setCellType(CellType.STRING);
					value13 = cell13.getStringCellValue().trim();
				}
				String value14 = "";
				if (cell14 != null) {
					cell14.setCellType(CellType.STRING);
					value14 = cell14.getStringCellValue().trim();
				}
				List<String> objArr = new ArrayList<String>();
				if (!value0.contains("total") && !value0.contains("Service")) {
					objArr.add(value0);// 序号
					objArr.add(value8);// 税率 I
					objArr.add(value11);// 含税单价
					objArr.add(value12);// 含税总金额
					objArr.add(value13);// 供货期（天）
					objArr.add(value14);// 供应商备注(报价信息)
				}
				if (value0.contains("Service")) {
					String vendor = value0.substring(value0.indexOf("<") + 1, value0.indexOf(">"));
					objArr.add(vendor);
					issign = true;
				}
				if (!objArr.isEmpty() && objArr.size() > 0) {
					objList.add(objArr);
				}
				if (issign) {
					break;
				}
			}
		}

		int maxnum = objList.size() - 1;
		for (int i = 0; i < maxnum; i++) {
			List<String> objMaxList = objList.get(maxnum);
			String vendor = objMaxList.get(0);
			if (i != maxnum) {
				List<String> objArr = objList.get(i);
				String linenum = objArr.get(0);// 序号
				String tax1code = objArr.get(1);// 含税单价 L
				String udtotalprice = objArr.get(2);// 含税单价 L
				String udtotalcost = objArr.get(3);// 含税总金额 M
				String deliverytime = objArr.get(4);// 供货期（天） N
				String udbidinfo = objArr.get(5);// 供应商备注(报价信息) O

				MboSetRemote rfqVendorSet = mbo.getMboSet("RFQVENDOR");
				if (!rfqVendorSet.isEmpty() && rfqVendorSet.count() > 0) {
					for (int j = 0; rfqVendorSet.getMbo(j) != null; j++) {
						MboRemote rfqVendor = rfqVendorSet.getMbo(j);
						String rfqvendor = rfqVendor.getString("vendor");
						if (rfqvendor.equalsIgnoreCase(vendor)) {
							MboSetRemote quolineSet = rfqVendor.getMboSet("QUOTATIONLINEVENDOR");
							if (!quolineSet.isEmpty() && quolineSet.count() > 0) {
								for (int k = 0; quolineSet.getMbo(k) != null; k++) {
									MboRemote quoline = quolineSet.getMbo(k);
									String quovendor = quoline.getString("vendor");
									String rfqlinenum = quoline.getString("rfqlinenum");
									if (quovendor.equalsIgnoreCase(vendor) && rfqlinenum.equalsIgnoreCase(linenum)) {
										quoline.setValue("quotestartdate", MXServer.getMXServer().getDate(), 11L);
										quoline.setValue("tax1code", tax1code, 2L);
										if (udtotalprice != null && !"".equals(udtotalprice)) {
											Double udtotalpriceD = Double.valueOf(udtotalprice);
											quoline.setValue("udtotalprice", udtotalpriceD, 2L);
										}

										if (udtotalcost != null && !"".equals(udtotalcost)) {
											Double udtotalcostD = Double.valueOf(udtotalcost);
											quoline.setValue("udtotalcost", udtotalcostD, 2L);
										}

										if (deliverytime != null && !"".equals(deliverytime)) {
											Double deliverytimeD = Double.valueOf(deliverytime);
											quoline.setValue("deliverytime", deliverytimeD, 11L);
										}
										quoline.setValue("udbidinfo", udbidinfo, 11L);
									}
								}
							}
						}
					}
				}
			}
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
