package guide.webclient.beans.invoice;

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

import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelInvoiceDateBean extends DataBean {
	private List<String> list = new ArrayList<String>();

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
		String login = clientSession.getUserInfo().getPersonId();
		String language = CommonUtil.getValue("PERSON", "status ='ACTIVE' and personid='" + login + "'", "language");
		for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
			int rownum = rowCount + 1;
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c1 = row.getCell(1);// 发票描述
				Cell c2 = row.getCell(2);// 订单号
				Cell c3 = row.getCell(3);// 创建人
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

				String ponum = "";
				if (!value2.isEmpty()) {
					ponum = CommonUtil.getValue("MATRECTRANS", "ponum='" + value2 + "' and  issuetype='RECEIPT'", "ponum");
					if (ponum == null) {
						Object[] obj = { "Warm reminder：" + rownum + "Row C column,	Not in order receiving！" };
						list.add(rownum + "");
						throw new MXApplicationException("udmessage", "error1", obj);
					}
				}

				String value3 = "";
				if (c3 != null) {
					c3.setCellType(CellType.STRING);
					value3 = c3.getStringCellValue().trim();

				}

				String personid = "";
				if (!value3.isEmpty()) {
					personid = CommonUtil.getValue("PERSON", "personid='" + value3 + "'", "personid");
					if (personid == null) {
						Object[] obj = { "Warm reminder：" + rownum + "Row D column,Not in the personnel information！" };
						list.add(rownum + "");
						throw new MXApplicationException("udmessage", "error1", obj);
					}
				}
				
				MboSetRemote invoiceSet = MXServer.getMXServer().getMboSet("INVOICE",
						MXServer.getMXServer().getSystemUserInfo());
				MboRemote invoice = invoiceSet.add();
				MboSetRemote invoicelineSet = invoice.getMboSet("INVOICELINE");
				MboRemote invoiceline = invoicelineSet.add();
				Date date = new Date();
				invoice.setValue("documenttype", "INVOICE", 11L);
				invoice.setValue("glpostdate", date, 11L);
				invoice.setValue("currencycode", "CNY", 11L);
				invoice.setValue("positeid", "CSPL", 11L);
				invoice.setValue("description", value1, 11L);
				invoice.setValue("ponum", value2, 2L);
				MboSetRemote matrectransSet = MXServer.getMXServer().getMboSet("MATRECTRANS",
						MXServer.getMXServer().getSystemUserInfo());
				matrectransSet.setWhere("ponum='" + value2 + "'");
				matrectransSet.reset();
				if (!matrectransSet.isEmpty() && matrectransSet.count() > 0) {
					MboSetRemote poSet = MXServer.getMXServer().getMboSet("PO",
							MXServer.getMXServer().getSystemUserInfo());
					poSet.setWhere("ponum='" + value2 + "'");
					poSet.reset();
					if (!poSet.isEmpty() && poSet.count() > 0) {
						String udvendor = poSet.getMbo(0).getString("vendor");
						invoice.setValue("vendor", udvendor, 2L);
					}
					poSet.close();
					for (int j = 0; j < matrectransSet.count(); j++) {
						MboRemote matrectrans = matrectransSet.getMbo(j);
						invoiceline.setValue("linetype", "ITEM",11L);
						invoiceline.setValue("itemnum", matrectrans.getString("itemnum"),2L);
						invoiceline.setValue("description", matrectrans.getString("description"),11L);
						invoiceline.setValue("receiptreqd", "1",11L);
						invoiceline.setValue("invoiceunit", matrectrans.getString("receivedunit"),11L);
						invoiceline.setValue("conversion","1",11L);
						invoiceline.setValue("unitcost",matrectrans.getString("unitcost"),11L);
						invoiceline.setValue("ponum",matrectrans.getString("ponum"),11L);
						invoiceline.setValue("polinenum",matrectrans.getString("polinenum"),11L);
						invoiceline.setValue("porevisionnum",matrectrans.getString("porevisionnum"),11L);
						invoiceline.setValue("positeid",matrectrans.getString("siteid"),11L);
					}
						
				}
				
				invoice.setValue("enterby",value3, 11L);
				invoiceSet.save();
				invoicelineSet.save();
				invoicelineSet.close();
				invoiceSet.close();
				
				countadd++;
			}
		
		}
		String params = "Total line number：" + (lastRowNum - 1) + "article，imported " + countadd + " article！";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "Warm reminder", params, 1);
	}

	private void deleteXLS(UploadFile uploadfile) {
		uploadfile.delete();
		uploadfile.save();
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
	 * @param fileName
	 *            文件名
	 * @param fileType
	 *            文件类型
	 * @throws MXApplicationException
	 *             maximo异常提示
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
