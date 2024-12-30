package guide.webclient.beans.pr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import guide.app.pr.PRImpLine;

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

public class ImportExcelPRImpLineDateBean extends DataBean {

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
		int countadd = 0;

		MboSetRemote lineSet = mbo.getMboSet("UDPRIMPLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			lineSet.deleteAll();
		}
		// 从第1行开始取值
		for (int rowCount = 1; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c1 = row.getCell(1);// 物资编码
				Cell c2 = row.getCell(2);// 数量
				Cell c3 = row.getCell(3);// 预计使用时间

				c1.setCellType(Cell.CELL_TYPE_STRING);
				String c1Str = c1.getStringCellValue().trim();

				c2.setCellType(Cell.CELL_TYPE_NUMERIC);
				double c2Num = c2.getNumericCellValue();

				c3.setCellType(Cell.CELL_TYPE_STRING);
				String c3Str = c3.getStringCellValue().trim();

				MboSetRemote itemSet = mbo.getMboSet("$item", "item",
						"status in ('ACTIVE') and itemnum = '" + c1Str + "'");
				if (!itemSet.isEmpty() && itemSet.count() > 0) {

					PRImpLine line = (PRImpLine) lineSet.add();
					line.setValue("itemnum", c1Str, 11L);
					line.setValue("orderqty", c2Num, 11L);
					line.setValue("esttime", c3Str, 11L);
					countadd++;
				}
			}
		}
		String params = "总行数：" + (lastRowNum - 1) + "条，已导入" + countadd + "条！";
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
