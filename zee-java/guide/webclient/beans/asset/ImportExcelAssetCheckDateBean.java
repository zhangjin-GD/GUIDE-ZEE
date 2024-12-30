package guide.webclient.beans.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelAssetCheckDateBean extends DataBean {

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
		Row row0 = sheet.getRow(0);
		String cell_0_0 = row0.getCell(0).toString();
		if ("待检查设备清单".equals(cell_0_0)) {
			getSheet1(mbo, workbook);
		}

	}

	// 主要指标信息
	private void getSheet1(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		int count = 0;
		for (int rowCount = 3; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c1 = row.getCell(1);// 设备设备编号
				Cell c3 = row.getCell(3);// 检验日期
				Cell c4 = row.getCell(4);// 下次检验日期
				String value0 = "";
				if (c1 != null) {
					c1.setCellType(CellType.STRING);
					value0 = c1.getStringCellValue().trim();
				}

				Date value3 = null;
				if (c3 != null) {
					boolean isDate = HSSFDateUtil.isCellDateFormatted(c3);
					if (isDate) {
						c3.setCellType(CellType.NUMERIC);
						value3 = c3.getDateCellValue();
					}
				}

				Date value4 = null;
				if (c4 != null) {
					boolean isDate = HSSFDateUtil.isCellDateFormatted(c4);
					if (isDate) {
						c4.setCellType(CellType.NUMERIC);
						value4 = c4.getDateCellValue();
					}
				}
				MboSetRemote assetSet = mbo.getMboSet("$asset" + count, "asset",
						"status in ('ACTIVE','OPERATING') and assetnum = '" + value0 + "'");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					MboRemote asset = assetSet.getMbo(0);
					if (value3 != null) {
						asset.setValue("checkdate", value3, 11L);
					}
					if (value4 != null) {
						asset.setValue("nextcheckdate", value4, 11L);
					}
					count++;
				}
			}
		}
		String params = "总行数：" + (lastRowNum - 2) + "条，已导入" + count + "条！";
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
