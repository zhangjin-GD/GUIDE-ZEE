package guide.webclient.beans.gpm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
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

public class ImportExcelUDworeDateBean extends DataBean {
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
		DataBean udworemainSet = app.getDataBean("udworemain");
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		MboRemote mbo = this.app.getAppBean().getMbo();
		String personId = mbo.getUserInfo().getPersonId();
		String language = CommonUtil.getValue("PERSON", "status ='ACTIVE' and personid='" + personId + "'", "language");
		if (language.equalsIgnoreCase("EN")) {//
			for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
				int rownum = rowCount + 1;
				Row row = sheet.getRow(rowCount);
				if (row != null) {
					Cell c1 = row.getCell(1);// 工单描述
					Cell c2 = row.getCell(2);// 设备分类
					Cell c3 = row.getCell(3);// 设备编码
					Cell c4 = row.getCell(4);// 故障开始时间

					String value1 = "";
					if (c1 != null) {
						c1.setCellType(CellType.STRING);
						value1 = c1.getStringCellValue().trim();
						System.out.println("--------------------" + value1);
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

					String cldc = "";
					if (!value1.isEmpty()) {
						cldc = CommonUtil.getValue("ALNDOMAIN", "domainid='UDWOREMRANK' and value='" + value1 + "'",
								"description");
						if (cldc == null) {
							Object[] obj = { "Warm reminder：" + rownum + "Row B, column B, is not in the game！" };
							list.add(rownum + "");
							throw new MXApplicationException("udmessage", "error1", obj);
						}
					}

					if (value2.isEmpty()) {
						Object[] obj = { "Warm reminder：" + rownum + "Row C column, cannot be empty！" };
						list.add(rownum + "");
						throw new MXApplicationException("udmessage", "error1", obj);
					}

					String yxj = "";
					if (!value3.isEmpty()) {
						yxj = CommonUtil.getValue("ALNDOMAIN", "domainid='UDLEVEL' and value='" + value3 + "'",
								"description");
						if (yxj == null) {
							Object[] obj = { "Warm reminder：" + rownum + "Row D column, not in priority！" };
							list.add(rownum + "");
							throw new MXApplicationException("udmessage", "error1", obj);
						}
					}
				}
			}
		} else {
			for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
				int rownum = rowCount + 1;
				Row row = sheet.getRow(rowCount);
				if (row != null) {
					Cell c1 = row.getCell(1);// 工单描述
					Cell c2 = row.getCell(2);// 设备分类
					Cell c3 = row.getCell(3);// 设备编码
					Cell c4 = row.getCell(4);// 故障开始时间

					String value1 = "";
					if (c1 != null) {
						c1.setCellType(CellType.STRING);
						value1 = c1.getStringCellValue().trim();
						System.out.println("--------------------" + value1);
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

					String cldc = "";
					if (!value1.isEmpty()) {
						cldc = CommonUtil.getValue("ALNDOMAIN", "domainid='UDWOREMRANK' and value='" + value1 + "'",
								"description");
						if (cldc == null) {
							Object[] obj = { "温馨提示：" + rownum + "行 B列,不在处理对策中！" };
							list.add(rownum + "");
							throw new MXApplicationException("udmessage", "error1", obj);
						}
					}

					if (value2.isEmpty()) {
						Object[] obj = { "温馨提示：" + rownum + "行 C列,不能为空！" };
						list.add(rownum + "");
						throw new MXApplicationException("udmessage", "error1", obj);
					}

					String yxj = "";
					if (!value3.isEmpty()) {
						yxj = CommonUtil.getValue("ALNDOMAIN", "domainid='UDLEVEL' and value='" + value3 + "'",
								"description");
						if (yxj == null) {
							Object[] obj = { "温馨提示：" + rownum + "行 D列,不在优先级中！" };
							list.add(rownum + "");
							throw new MXApplicationException("udmessage", "error1", obj);
						}
					}
				}
			}

			if (list.isEmpty() && list.size() == 0) {
				for (int i = 2; i <= lastRowNum; i++) {
					Row row = sheet.getRow(i);
					udworemainSet.addrow();
					udworemainSet.setValue("rank", getCellString(row.getCell(1), 0), 11L);
					udworemainSet.setValue("description", getCellString(row.getCell(2), 0), 11L);
					udworemainSet.setValue("worklevel", getCellString(row.getCell(3), 0), 11L);
					udworemainSet.setValue("remark", getCellString(row.getCell(4), 0), 11L);
					countadd++;
				}
			}
			String params = "Total line number：" + (lastRowNum - 1) + "article，imported " + countadd + " article！";
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "Warm reminder", params, 1);
		}
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
