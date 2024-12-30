package guide.webclient.beans.gjobplan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelGjobTaskDateBean extends DataBean {

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
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		MboRemote gjobPlan = this.app.getAppBean().getMbo();
		MboRemote gjobTask = null;
		MboSetRemote gjobTaskSet = gjobPlan.getMboSet("UDGJOBTASK");
		if (gjobTaskSet != null && !gjobTaskSet.isEmpty()) {
			gjobTaskSet.deleteAll(11L);
		}
		for (int rowCount = 1; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c0 = row.getCell(0);// linenum
				Cell c1 = row.getCell(1);// mechname
				Cell c2 = row.getCell(2);// content
				Cell c3 = row.getCell(3);// inspection
				Cell c4 = row.getCell(4);// jpduration
				
				if(c0 == null){
					Object[] params1 = { "prompt：row " + rowCount + " cell 1 is empty..." };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params1);
				}
				if(c1 == null){
					Object[] params2 = { "prompt：row " + rowCount + " cell 2 is empty..." };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params2);
				}
				if(c2 == null){
					Object[] params3 = { "prompt：row " + rowCount + " cell 3 is empty..." };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params3);
				}
				if(c3 == null){
					Object[] params4 = { "prompt：row " + rowCount + " cell 4 is empty..." };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params4);
				}
				if(c4 == null){
					Object[] params5 = { "prompt：row " + rowCount + " cell 5 is empty..." };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params5);
				}
				
				c0.setCellType(Cell.CELL_TYPE_STRING);
				c1.setCellType(Cell.CELL_TYPE_STRING);
				c2.setCellType(Cell.CELL_TYPE_STRING);
				c3.setCellType(Cell.CELL_TYPE_STRING);
				c4.setCellType(Cell.CELL_TYPE_NUMERIC);
				
				String value0 = c0.getStringCellValue().trim();
				String value1 = c1.getStringCellValue().trim();
				String value2 = c2.getStringCellValue().trim();
				String value3 = c3.getStringCellValue().trim();
				double value4 = c4.getNumericCellValue();

				gjobTask = gjobTaskSet.add();
				gjobTask.setValue("linenum", value0, 11L);
				gjobTask.setValue("mechname", value1, 11L);
				gjobTask.setValue("content", value2, 11L);
				gjobTask.setValue("inspection", value3, 11L);
				gjobTask.setValue("jpduration", value4, 11L);
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
