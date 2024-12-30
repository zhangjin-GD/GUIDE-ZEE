package guide.webclient.beans.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Properties;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import guide.app.asset.ShorePowerLine;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelSPLineDateBean extends DataBean {

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
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		MboSetRemote lineSet = mbo.getMboSet("UDSHOREPOWERLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			lineSet.deleteAll();
		}

		for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c0 = row.getCell(0);// 日 期
				Cell c1 = row.getCell(1);// 船 名
				Cell c2 = row.getCell(2);// 岸电箱号
				Cell c3 = row.getCell(3);// 开始时间
				Cell c4 = row.getCell(4);// 结束时间
				Cell c5 = row.getCell(5);// 接电时长(H)
				Cell c6 = row.getCell(6);// 用电量（KWH)
				Cell c7 = row.getCell(7);// 是否干线船

				c0.setCellType(Cell.CELL_TYPE_NUMERIC);
				Date c0date = c0.getDateCellValue();

				c1.setCellType(Cell.CELL_TYPE_STRING);
				String c1Str = c1.getStringCellValue().trim();

				c2.setCellType(Cell.CELL_TYPE_STRING);
				String c2Str = c2.getStringCellValue().trim();

				c3.setCellType(Cell.CELL_TYPE_NUMERIC);
				Date c3date = c3.getDateCellValue();

				c4.setCellType(Cell.CELL_TYPE_NUMERIC);
				Date c4date = c4.getDateCellValue();

				c5.setCellType(Cell.CELL_TYPE_NUMERIC);
				double c5Num = c5.getNumericCellValue();

				c6.setCellType(Cell.CELL_TYPE_NUMERIC);
				double c6Num = c6.getNumericCellValue();

				c7.setCellType(Cell.CELL_TYPE_STRING);
				String c7Str = c7.getStringCellValue().trim();

				ShorePowerLine line = (ShorePowerLine) lineSet.add();
				if (c0date != null) {
					line.setValue("createdate", c0date, 11L);
				} else {
					line.setValue("createdate", MXServer.getMXServer().getDate(), 11L);
				}

				line.setValue("shipname", c1Str, 11L);
				line.setValue("boxno", c2Str, 11L);

				if (c3date != null) {
					line.setValue("starttime", c3date, 11L);
				}
				if (c4date != null) {
					line.setValue("endtime", c4date, 11L);
				}

				line.setValue("duration", c5Num, 11L);
				line.setValue("electric", c6Num, 11L);
				if ("Y".equals(c7Str)) {
					line.setValue("istrunkship", true, 11L);
				} else {
					line.setValue("istrunkship", false, 11L);
				}
			}
		}
		String params = "已导入完成，请核对数据！";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示", params, 1);

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
