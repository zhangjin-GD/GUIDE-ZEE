package guide.webclient.beans.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import guide.app.asset.EqRunLogLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.UploadFile;

public class ImportExcelEqRunLogDateBean extends DataBean {

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
		if ("主要指标信息".equals(cell_0_0)) {
			getSheet1(mbo, workbook);
		} else if ("所有指标信息".equals(cell_0_0)) {
			getSheet2(mbo, workbook);
		} else if ("用水量指标信息".equals(cell_0_0)) {
			getSheet3(mbo, workbook);
		} else if ("用电量指标信息".equals(cell_0_0)) {
			getSheet4(mbo, workbook);
		} else if ("柴油用量指标信息".equals(cell_0_0)) {
			getSheet5(mbo, workbook);
		} else if ("汽油用量指标信息".equals(cell_0_0)) {
			getSheet6(mbo, workbook);
		}

	}

	// 主要指标信息
	private void getSheet1(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		int countadd = 0;
		Date enddate = mbo.getDate("enddate");
		MboSetRemote runLineSet = mbo.getMboSet("UDEQRUNLOGLINE");
		for (int rowCount = 3; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c0 = row.getCell(0);// 设备编码
				Cell c1 = row.getCell(1);// 工作时间Hour
				Cell c2 = row.getCell(2);// 作业箱量Unit
				Cell c3 = row.getCell(3);// 作业箱量Teu
				Cell c4 = row.getCell(4);// 作业箱量Move
				Cell c5 = row.getCell(5);// 柴油油量
				Cell c6 = row.getCell(6);// 汽油油量
				Cell c7 = row.getCell(7);// 用电量
				Cell c8 = row.getCell(8);// 用水量

				String value0 = "";
				if (c0 != null) {
					c0.setCellType(CellType.STRING);
					value0 = c0.getStringCellValue().trim();
				}

				double value1 = 0;
				boolean isNull1 = true;
				if (c1 != null) {
					CellType c1type = c1.getCellTypeEnum();
					if (CellType.BLANK.equals(c1type)) {
						isNull1 = false;
					} else {
						c1.setCellType(CellType.NUMERIC);
						value1 = c1.getNumericCellValue();
					}
				}

				double value2 = 0;
				boolean isNull2 = true;
				if (c2 != null) {
					CellType c2type = c2.getCellTypeEnum();
					if (CellType.BLANK.equals(c2type)) {
						isNull2 = false;
					} else {
						c2.setCellType(CellType.NUMERIC);
						value2 = c2.getNumericCellValue();
					}
				}

				double value3 = 0;
				boolean isNull3 = true;
				if (c3 != null) {
					CellType c3type = c3.getCellTypeEnum();
					if (CellType.BLANK.equals(c3type)) {
						isNull3 = false;
					} else {
						c3.setCellType(CellType.NUMERIC);
						value3 = c3.getNumericCellValue();
					}

				}

				double value4 = 0;
				boolean isNull4 = true;
				if (c4 != null) {
					CellType c4type = c4.getCellTypeEnum();
					if (CellType.BLANK.equals(c4type)) {
						isNull4 = false;
					} else {
						c4.setCellType(CellType.NUMERIC);
						value4 = c4.getNumericCellValue();
					}
				}

				double value5 = 0;
				boolean isNull5 = true;
				if (c5 != null) {
					CellType c5type = c5.getCellTypeEnum();
					if (CellType.BLANK.equals(c5type)) {
						isNull5 = false;
					} else {
						c5.setCellType(CellType.NUMERIC);
						value5 = c5.getNumericCellValue();
					}
				}

				double value6 = 0;
				boolean isNull6 = true;
				if (c6 != null) {
					CellType c6type = c6.getCellTypeEnum();
					if (CellType.BLANK.equals(c6type)) {
						isNull6 = false;
					} else {
						c6.setCellType(CellType.NUMERIC);
						value6 = c6.getNumericCellValue();
					}
				}

				double value7 = 0;
				boolean isNull7 = true;
				if (c7 != null) {
					CellType c7type = c7.getCellTypeEnum();
					if (CellType.BLANK.equals(c7type)) {
						isNull7 = false;
					} else {
						c7.setCellType(CellType.NUMERIC);
						value7 = c7.getNumericCellValue();
					}

				}

				double value8 = 0;
				boolean isNull8 = true;
				if (c8 != null) {
					CellType c8type = c8.getCellTypeEnum();
					if (CellType.BLANK.equals(c8type)) {
						isNull8 = false;
					} else {
						c8.setCellType(CellType.NUMERIC);
						value8 = c8.getNumericCellValue();
					}
				}

				boolean flag = false;
				MboSetRemote assetSet = mbo.getMboSet("$asset", "asset",
						"status in ('ACTIVE','OPERATING') and assetnum = '" + value0 + "'");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
						for (int j = 0; runLineSet.getMbo(j) != null; j++) {
							MboRemote runLine = runLineSet.getMbo(j);
							String assetnum = runLine.getString("assetnum");
							if (value0.equalsIgnoreCase(assetnum)) {
								flag = true;
								if (isNull1) {
									runLine.setValue("workhour", value1, 11L);
								}
								if (isNull2) {
									runLine.setValue("boxunit", value2, 11L);
								}
								if (isNull3) {
									runLine.setValue("boxteu", value3, 11L);
								}
								if (isNull4) {
									runLine.setValue("boxmove", value4, 11L);
								}
								if (isNull5) {
									runLine.setValue("oill", value5, 11L);
								}
								if (isNull6) {
									runLine.setValue("oill2", value6, 11L);
								}
								if (isNull7) {
									runLine.setValue("electrickwh", value7, 11L);
								}
								if (isNull8) {
									runLine.setValue("water", value8, 11L);
								}
								runLine.setValue("createdate", enddate, 11L);
								break;
							}
						}
						if (!flag) {
							EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
							runLine.setValue("assetnum", value0, 11L);
							if (isNull1) {
								runLine.setValue("workhour", value1, 11L);
							}
							if (isNull2) {
								runLine.setValue("boxunit", value2, 11L);
							}
							if (isNull3) {
								runLine.setValue("boxteu", value3, 11L);
							}
							if (isNull4) {
								runLine.setValue("boxmove", value4, 11L);
							}
							if (isNull5) {
								runLine.setValue("oill", value5, 11L);
							}
							if (isNull6) {
								runLine.setValue("oill2", value6, 11L);
							}
							if (isNull7) {
								runLine.setValue("electrickwh", value7, 11L);
							}
							if (isNull8) {
								runLine.setValue("water", value8, 11L);
							}
							runLine.setValue("createdate", enddate, 11L);
						}
					} else {
						EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
						runLine.setValue("assetnum", value0, 11L);
						if (isNull1) {
							runLine.setValue("workhour", value1, 11L);
						}
						if (isNull2) {
							runLine.setValue("boxunit", value2, 11L);
						}
						if (isNull3) {
							runLine.setValue("boxteu", value3, 11L);
						}
						if (isNull4) {
							runLine.setValue("boxmove", value4, 11L);
						}
						if (isNull5) {
							runLine.setValue("oill", value5, 11L);
						}
						if (isNull6) {
							runLine.setValue("oill2", value6, 11L);
						}
						if (isNull7) {
							runLine.setValue("electrickwh", value7, 11L);
						}
						if (isNull8) {
							runLine.setValue("water", value8, 11L);
						}
						runLine.setValue("createdate", enddate, 11L);
					}
					countadd++;
				}
			}
		}
		String params = "总行数：" + (lastRowNum - 2) + "条，已导入" + countadd + "条！";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示", params, 1);

	}

	// 所有指标信息
	private void getSheet2(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		int countadd = 0;
		Date enddate = mbo.getDate("enddate");
		MboSetRemote runLineSet = mbo.getMboSet("UDEQRUNLOGLINE");
		for (int rowCount = 3; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c0 = row.getCell(0);// 设备编码
				Cell c1 = row.getCell(1);// 工作时间Hour
				Cell c2 = row.getCell(2);// 作业箱量Unit
				Cell c3 = row.getCell(3);// 作业箱量Teu
				Cell c4 = row.getCell(4);// 作业箱量Move
				Cell c5 = row.getCell(5);// 柴油
				Cell c6 = row.getCell(6);// 汽油
				Cell c7 = row.getCell(7);// 用电量
				Cell c8 = row.getCell(8);// 用水量
				Cell c9 = row.getCell(9);// 公里数
				Cell c10 = row.getCell(10);// 起升运行小时
				Cell c11 = row.getCell(11);// 大车运行小时
				Cell c12 = row.getCell(12);// 小车运行小时
				Cell c13 = row.getCell(13);// 俯仰运行小时
				Cell c14 = row.getCell(14);// 起升＋小车运行小时
				Cell c15 = row.getCell(15);// 控制电源小时数
				Cell c16 = row.getCell(16);// 旋转小车小时数
				Cell c17 = row.getCell(17);// 吊具开闭锁次数
				Cell c18 = row.getCell(18);// 发动机运行时间
				String value0 = "";
				if (c0 != null) {
					c0.setCellType(CellType.STRING);
					value0 = c0.getStringCellValue().trim();
				}

				double value1 = 0;
				boolean isNull1 = true;
				if (c1 != null) {
					CellType c1type = c1.getCellTypeEnum();
					if (CellType.BLANK.equals(c1type)) {
						isNull1 = false;
					} else {
						c1.setCellType(CellType.NUMERIC);
						value1 = c1.getNumericCellValue();
					}
				}

				double value2 = 0;
				boolean isNull2 = true;
				if (c2 != null) {
					CellType c2type = c2.getCellTypeEnum();
					if (CellType.BLANK.equals(c2type)) {
						isNull2 = false;
					} else {
						c2.setCellType(CellType.NUMERIC);
						value2 = c2.getNumericCellValue();
					}
				}

				double value3 = 0;
				boolean isNull3 = true;
				if (c3 != null) {
					CellType c3type = c3.getCellTypeEnum();
					if (CellType.BLANK.equals(c3type)) {
						isNull3 = false;
					} else {
						c3.setCellType(CellType.NUMERIC);
						value3 = c3.getNumericCellValue();
					}

				}

				double value4 = 0;
				boolean isNull4 = true;
				if (c4 != null) {
					CellType c4type = c4.getCellTypeEnum();
					if (CellType.BLANK.equals(c4type)) {
						isNull4 = false;
					} else {
						c4.setCellType(CellType.NUMERIC);
						value4 = c4.getNumericCellValue();
					}
				}

				double value5 = 0;
				boolean isNull5 = true;
				if (c5 != null) {
					CellType c5type = c5.getCellTypeEnum();
					if (CellType.BLANK.equals(c5type)) {
						isNull5 = false;
					} else {
						c5.setCellType(CellType.NUMERIC);
						value5 = c5.getNumericCellValue();
					}
				}

				double value6 = 0;
				boolean isNull6 = true;
				if (c6 != null) {
					CellType c6type = c6.getCellTypeEnum();
					if (CellType.BLANK.equals(c6type)) {
						isNull6 = false;
					} else {
						c6.setCellType(CellType.NUMERIC);
						value6 = c6.getNumericCellValue();
					}
				}

				double value7 = 0;
				boolean isNull7 = true;
				if (c7 != null) {
					CellType c7type = c7.getCellTypeEnum();
					if (CellType.BLANK.equals(c7type)) {
						isNull7 = false;
					} else {
						c7.setCellType(CellType.NUMERIC);
						value7 = c7.getNumericCellValue();
					}

				}

				double value8 = 0;
				boolean isNull8 = true;
				if (c8 != null) {
					CellType c8type = c8.getCellTypeEnum();
					if (CellType.BLANK.equals(c8type)) {
						isNull8 = false;
					} else {
						c8.setCellType(CellType.NUMERIC);
						value8 = c8.getNumericCellValue();
					}
				}

				double value9 = 0;
				boolean isNull9 = true;
				if (c9 != null) {
					CellType c9type = c9.getCellTypeEnum();
					if (CellType.BLANK.equals(c9type)) {
						isNull9 = false;
					} else {
						c9.setCellType(CellType.NUMERIC);
						value9 = c9.getNumericCellValue();
					}
				}

				double value10 = 0;
				boolean isNull10 = true;
				if (c10 != null) {
					CellType c10type = c10.getCellTypeEnum();
					if (CellType.BLANK.equals(c10type)) {
						isNull10 = false;
					} else {
						c10.setCellType(CellType.NUMERIC);
						value10 = c10.getNumericCellValue();
					}
				}

				double value11 = 0;
				boolean isNull11 = true;
				if (c11 != null) {
					CellType c11type = c11.getCellTypeEnum();
					if (CellType.BLANK.equals(c11type)) {
						isNull11 = false;
					} else {
						c11.setCellType(CellType.NUMERIC);
						value11 = c11.getNumericCellValue();
					}
				}

				double value12 = 0;
				boolean isNull12 = true;
				if (c12 != null) {
					CellType c12type = c12.getCellTypeEnum();
					if (CellType.BLANK.equals(c12type)) {
						isNull12 = false;
					} else {
						c12.setCellType(CellType.NUMERIC);
						value12 = c12.getNumericCellValue();
					}
				}

				double value13 = 0;
				boolean isNull13 = true;
				if (c13 != null) {
					CellType c13type = c13.getCellTypeEnum();
					if (CellType.BLANK.equals(c13type)) {
						isNull13 = false;
					} else {
						c13.setCellType(CellType.NUMERIC);
						value13 = c13.getNumericCellValue();
					}
				}

				double value14 = 0;
				boolean isNull14 = true;
				if (c14 != null) {
					CellType c14type = c14.getCellTypeEnum();
					if (CellType.BLANK.equals(c14type)) {
						isNull14 = false;
					} else {
						c14.setCellType(CellType.NUMERIC);
						value14 = c14.getNumericCellValue();
					}
				}

				double value15 = 0;
				boolean isNull15 = true;
				if (c15 != null) {
					CellType c15type = c15.getCellTypeEnum();
					if (CellType.BLANK.equals(c15type)) {
						isNull15 = false;
					} else {
						c15.setCellType(CellType.NUMERIC);
						value15 = c15.getNumericCellValue();
					}
				}

				double value16 = 0;
				boolean isNull16 = true;
				if (c16 != null) {
					CellType c16type = c16.getCellTypeEnum();
					if (CellType.BLANK.equals(c16type)) {
						isNull16 = false;
					} else {
						c16.setCellType(CellType.NUMERIC);
						value16 = c16.getNumericCellValue();
					}
				}

				double value17 = 0;
				boolean isNull17 = true;
				if (c17 != null) {
					CellType c17type = c17.getCellTypeEnum();
					if (CellType.BLANK.equals(c17type)) {
						isNull17 = false;
					} else {
						c17.setCellType(CellType.NUMERIC);
						value17 = c17.getNumericCellValue();
					}
				}

				double value18 = 0;
				boolean isNull18 = true;
				if (c18 != null) {
					CellType c18type = c18.getCellTypeEnum();
					if (CellType.BLANK.equals(c18type)) {
						isNull18 = false;
					} else {
						c18.setCellType(CellType.NUMERIC);
						value18 = c18.getNumericCellValue();
					}
				}

				boolean flag = false;
				MboSetRemote assetSet = mbo.getMboSet("$asset", "asset",
						"status in ('ACTIVE','OPERATING') and assetnum = '" + value0 + "'");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
						for (int j = 0; runLineSet.getMbo(j) != null; j++) {
							MboRemote runLine = runLineSet.getMbo(j);
							String assetnum = runLine.getString("assetnum");
							if (value0.equalsIgnoreCase(assetnum)) {
								flag = true;
								if (isNull1) {
									runLine.setValue("workhour", value1, 11L);
								}
								if (isNull2) {
									runLine.setValue("boxunit", value2, 11L);
								}
								if (isNull3) {
									runLine.setValue("boxteu", value3, 11L);
								}
								if (isNull4) {
									runLine.setValue("boxmove", value4, 11L);
								}
								if (isNull5) {
									runLine.setValue("oill", value5, 11L);
								}
								if (isNull6) {
									runLine.setValue("oill2", value6, 11L);
								}
								if (isNull7) {
									runLine.setValue("electrickwh", value7, 11L);
								}
								if (isNull8) {
									runLine.setValue("water", value8, 11L);
								}
								if (isNull9) {
									runLine.setValue("mileage", value9, 11L);
								}
								if (isNull10) {
									runLine.setValue("parta", value10, 11L);
								}
								if (isNull11) {
									runLine.setValue("partb", value11, 11L);
								}
								if (isNull12) {
									runLine.setValue("partc", value12, 11L);
								}
								if (isNull13) {
									runLine.setValue("partd", value13, 11L);
								}
								if (isNull14) {
									runLine.setValue("parte", value14, 11L);
								}
								if (isNull15) {
									runLine.setValue("partf", value15, 11L);
								}
								if (isNull16) {
									runLine.setValue("partg", value16, 11L);
								}
								if (isNull17) {
									runLine.setValue("switchlock", value17, 11L);
								}
								if (isNull18) {
									runLine.setValue("parth", value18, 11L);
								}
								runLine.setValue("createdate", enddate, 11L);
								break;
							}
						}
						if (!flag) {
							EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
							runLine.setValue("assetnum", value0, 11L);
							if (isNull1) {
								runLine.setValue("workhour", value1, 11L);
							}
							if (isNull2) {
								runLine.setValue("boxunit", value2, 11L);
							}
							if (isNull3) {
								runLine.setValue("boxteu", value3, 11L);
							}
							if (isNull4) {
								runLine.setValue("boxmove", value4, 11L);
							}
							if (isNull5) {
								runLine.setValue("oill", value5, 11L);
							}
							if (isNull6) {
								runLine.setValue("oill2", value6, 11L);
							}
							if (isNull7) {
								runLine.setValue("electrickwh", value7, 11L);
							}
							if (isNull8) {
								runLine.setValue("water", value8, 11L);
							}
							if (isNull9) {
								runLine.setValue("mileage", value9, 11L);
							}
							if (isNull10) {
								runLine.setValue("parta", value10, 11L);
							}
							if (isNull11) {
								runLine.setValue("partb", value11, 11L);
							}
							if (isNull12) {
								runLine.setValue("partc", value12, 11L);
							}
							if (isNull13) {
								runLine.setValue("partd", value13, 11L);
							}
							if (isNull14) {
								runLine.setValue("parte", value14, 11L);
							}
							if (isNull15) {
								runLine.setValue("partf", value15, 11L);
							}
							if (isNull16) {
								runLine.setValue("partg", value16, 11L);
							}
							if (isNull17) {
								runLine.setValue("switchlock", value17, 11L);
							}
							if (isNull18) {
								runLine.setValue("parth", value18, 11L);
							}
							runLine.setValue("createdate", enddate, 11L);
						}
					} else {
						EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
						runLine.setValue("assetnum", value0, 11L);
						if (isNull1) {
							runLine.setValue("workhour", value1, 11L);
						}
						if (isNull2) {
							runLine.setValue("boxunit", value2, 11L);
						}
						if (isNull3) {
							runLine.setValue("boxteu", value3, 11L);
						}
						if (isNull4) {
							runLine.setValue("boxmove", value4, 11L);
						}
						if (isNull5) {
							runLine.setValue("oill", value5, 11L);
						}
						if (isNull6) {
							runLine.setValue("oill2", value6, 11L);
						}
						if (isNull7) {
							runLine.setValue("electrickwh", value7, 11L);
						}
						if (isNull8) {
							runLine.setValue("water", value8, 11L);
						}
						if (isNull9) {
							runLine.setValue("mileage", value9, 11L);
						}
						if (isNull10) {
							runLine.setValue("parta", value10, 11L);
						}
						if (isNull11) {
							runLine.setValue("partb", value11, 11L);
						}
						if (isNull12) {
							runLine.setValue("partc", value12, 11L);
						}
						if (isNull13) {
							runLine.setValue("partd", value13, 11L);
						}
						if (isNull14) {
							runLine.setValue("parte", value14, 11L);
						}
						if (isNull15) {
							runLine.setValue("partf", value15, 11L);
						}
						if (isNull16) {
							runLine.setValue("partg", value16, 11L);
						}
						if (isNull17) {
							runLine.setValue("switchlock", value17, 11L);
						}
						if (isNull18) {
							runLine.setValue("parth", value18, 11L);
						}
						runLine.setValue("createdate", enddate, 11L);
					}
					countadd++;
				}
			}
		}
		String params = "总行数：" + (lastRowNum - 2) + "条，已导入" + countadd + "条！";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示", params, 1);

	}

	// 用水量指标信息
	private void getSheet3(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		int countadd = 0;
		Date enddate = mbo.getDate("enddate");
		MboSetRemote runLineSet = mbo.getMboSet("UDEQRUNLOGLINE");
		for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c0 = row.getCell(0);// 设备编码
				Cell c1 = row.getCell(1);// 上月抄表数
				Cell c2 = row.getCell(2);// 本月抄表数
				Cell c3 = row.getCell(3);// 用水量

				String value0 = "";
				if (c0 != null) {
					c0.setCellType(CellType.STRING);
					value0 = c0.getStringCellValue().trim();
				}

				double value1 = 0;
				boolean isNull1 = true;
				if (c1 != null) {
					CellType c1type = c1.getCellTypeEnum();
					if (CellType.BLANK.equals(c1type)) {
						isNull1 = false;
					} else {
						c1.setCellType(CellType.NUMERIC);
						value1 = c1.getNumericCellValue();
					}
				}

				double value2 = 0;
				boolean isNull2 = true;
				if (c2 != null) {
					CellType c2type = c2.getCellTypeEnum();
					if (CellType.BLANK.equals(c2type)) {
						isNull2 = false;
					} else {
						c2.setCellType(CellType.NUMERIC);
						value2 = c2.getNumericCellValue();
					}
				}

				double value3 = 0;
				boolean isNull3 = true;
				if (c3 != null) {
					CellType c3type = c3.getCellTypeEnum();
					if (CellType.BLANK.equals(c3type)) {
						isNull3 = false;
					} else {
						c3.setCellType(CellType.NUMERIC);
						value3 = c3.getNumericCellValue();
					}
				}

				boolean flag = false;
				MboSetRemote assetSet = mbo.getMboSet("$asset", "asset",
						"status in ('ACTIVE','OPERATING') and assetnum = '" + value0 + "'");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
						for (int j = 0; runLineSet.getMbo(j) != null; j++) {
							MboRemote runLine = runLineSet.getMbo(j);
							String assetnum = runLine.getString("assetnum");
							if (value0.equalsIgnoreCase(assetnum)) {
								flag = true;
								if (isNull1) {
									runLine.setValue("waterpre", value1, 2L);
								}
								if (isNull2) {
									runLine.setValue("watercur", value2, 2L);
								}
								if (isNull3) {
									runLine.setValue("water", value3, 11L);
								}
								runLine.setValue("createdate", enddate, 11L);
								break;
							}
						}
						if (!flag) {
							EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
							runLine.setValue("assetnum", value0, 11L);
							if (isNull1) {
								runLine.setValue("waterpre", value1, 2L);
							}
							if (isNull2) {
								runLine.setValue("watercur", value2, 2L);
							}
							if (isNull3) {
								runLine.setValue("water", value3, 11L);
							}
							runLine.setValue("createdate", enddate, 11L);
						}
					} else {
						EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
						runLine.setValue("assetnum", value0, 11L);
						if (isNull1) {
							runLine.setValue("waterpre", value1, 2L);
						}
						if (isNull2) {
							runLine.setValue("watercur", value2, 2L);
						}
						if (isNull3) {
							runLine.setValue("water", value3, 11L);
						}
						runLine.setValue("createdate", enddate, 11L);
					}
					countadd++;
				}
			}
		}
		String params = "总行数：" + (lastRowNum - 1) + "条，已导入" + countadd + "条！";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示", params, 1);

	}

	// 用电量指标信息
	private void getSheet4(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		int countadd = 0;
		Date enddate = mbo.getDate("enddate");
		MboSetRemote runLineSet = mbo.getMboSet("UDEQRUNLOGLINE");
		for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c0 = row.getCell(0);// 设备编码
				Cell c1 = row.getCell(1);// 上月抄表数
				Cell c2 = row.getCell(2);// 本月抄表数
				Cell c3 = row.getCell(3);// 用电量

				String value0 = "";
				if (c0 != null) {
					c0.setCellType(CellType.STRING);
					value0 = c0.getStringCellValue().trim();
				}

				double value1 = 0;
				boolean isNull1 = true;
				if (c1 != null) {
					CellType c1type = c1.getCellTypeEnum();
					if (CellType.BLANK.equals(c1type)) {
						isNull1 = false;
					} else {
						c1.setCellType(CellType.NUMERIC);
						value1 = c1.getNumericCellValue();
					}
				}

				double value2 = 0;
				boolean isNull2 = true;
				if (c2 != null) {
					CellType c2type = c2.getCellTypeEnum();
					if (CellType.BLANK.equals(c2type)) {
						isNull2 = false;
					} else {
						c2.setCellType(CellType.NUMERIC);
						value2 = c2.getNumericCellValue();
					}
				}

				double value3 = 0;
				boolean isNull3 = true;
				if (c3 != null) {
					CellType c3type = c3.getCellTypeEnum();
					if (CellType.BLANK.equals(c3type)) {
						isNull3 = false;
					} else {
						c3.setCellType(CellType.NUMERIC);
						value3 = c3.getNumericCellValue();
					}
				}

				boolean flag = false;
				MboSetRemote assetSet = mbo.getMboSet("$asset", "asset",
						"status in ('ACTIVE','OPERATING') and assetnum = '" + value0 + "'");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
						for (int j = 0; runLineSet.getMbo(j) != null; j++) {
							MboRemote runLine = runLineSet.getMbo(j);
							String assetnum = runLine.getString("assetnum");
							if (value0.equalsIgnoreCase(assetnum)) {
								flag = true;
								if (isNull1) {
									runLine.setValue("electrickwhpre", value1, 2L);
								}
								if (isNull2) {
									runLine.setValue("electrickwhcur", value2, 2L);
								}
								if (isNull3) {
									runLine.setValue("electrickwh", value3, 11L);
								}
								runLine.setValue("createdate", enddate, 11L);
								break;
							}
						}
						if (!flag) {
							EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
							runLine.setValue("assetnum", value0, 11L);
							if (isNull1) {
								runLine.setValue("electrickwhpre", value1, 2L);
							}
							if (isNull2) {
								runLine.setValue("electrickwhcur", value2, 2L);
							}
							if (isNull3) {
								runLine.setValue("electrickwh", value3, 11L);
							}
							runLine.setValue("createdate", enddate, 11L);
						}
					} else {
						EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
						runLine.setValue("assetnum", value0, 11L);
						if (isNull1) {
							runLine.setValue("electrickwhpre", value1, 2L);
						}
						if (isNull2) {
							runLine.setValue("electrickwhcur", value2, 2L);
						}
						if (isNull3) {
							runLine.setValue("electrickwh", value3, 11L);
						}
						runLine.setValue("createdate", enddate, 11L);
					}
					countadd++;
				}
			}
		}
		String params = "总行数：" + (lastRowNum - 1) + "条，已导入" + countadd + "条！";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示", params, 1);

	}

	// 柴油指标信息
	private void getSheet5(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		int countadd = 0;
		Date enddate = mbo.getDate("enddate");
		MboSetRemote runLineSet = mbo.getMboSet("UDEQRUNLOGLINE");
		for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c0 = row.getCell(0);// 设备编码
				Cell c1 = row.getCell(1);// 柴油

				String value0 = "";
				if (c0 != null) {
					c0.setCellType(CellType.STRING);
					value0 = c0.getStringCellValue().trim();
				}

				double value1 = 0;
				boolean isNull1 = true;
				if (c1 != null) {
					CellType c1type = c1.getCellTypeEnum();
					if (CellType.BLANK.equals(c1type)) {
						isNull1 = false;
					} else {
						c1.setCellType(CellType.NUMERIC);
						value1 = c1.getNumericCellValue();
					}
				}

				boolean flag = false;
				MboSetRemote assetSet = mbo.getMboSet("$asset", "asset",
						"status in ('ACTIVE','OPERATING') and assetnum = '" + value0 + "'");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
						for (int j = 0; runLineSet.getMbo(j) != null; j++) {
							MboRemote runLine = runLineSet.getMbo(j);
							String assetnum = runLine.getString("assetnum");
							if (value0.equalsIgnoreCase(assetnum)) {
								flag = true;
								if (isNull1) {
									runLine.setValue("oill", value1, 11L);
								}
								runLine.setValue("createdate", enddate, 11L);
								break;
							}
						}
						if (!flag) {
							EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
							runLine.setValue("assetnum", value0, 11L);
							if (isNull1) {
								runLine.setValue("oill", value1, 11L);
							}
							runLine.setValue("createdate", enddate, 11L);
						}
					} else {
						EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
						runLine.setValue("assetnum", value0, 11L);
						if (isNull1) {
							runLine.setValue("oill", value1, 11L);
						}
						runLine.setValue("createdate", enddate, 11L);
					}
					countadd++;
				}
			}
		}
		String params = "总行数：" + (lastRowNum - 1) + "条，已导入" + countadd + "条！";
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示", params, 1);

	}

	// 柴油指标信息
	private void getSheet6(MboRemote mbo, Workbook workbook) throws RemoteException, MXException {
		int sheetNum = 0;
		Sheet sheet = workbook.getSheetAt(sheetNum);
		int lastRowNum = sheet.getLastRowNum();// 行
		int countadd = 0;
		Date enddate = mbo.getDate("enddate");
		MboSetRemote runLineSet = mbo.getMboSet("UDEQRUNLOGLINE");
		for (int rowCount = 2; rowCount <= lastRowNum; rowCount++) {
			Row row = sheet.getRow(rowCount);
			if (row != null) {
				Cell c0 = row.getCell(0);// 设备编码
				Cell c1 = row.getCell(1);// 汽油

				String value0 = "";
				if (c0 != null) {
					c0.setCellType(CellType.STRING);
					value0 = c0.getStringCellValue().trim();
				}

				double value1 = 0;
				boolean isNull1 = true;
				if (c1 != null) {
					CellType c1type = c1.getCellTypeEnum();
					if (CellType.BLANK.equals(c1type)) {
						isNull1 = false;
					} else {
						c1.setCellType(CellType.NUMERIC);
						value1 = c1.getNumericCellValue();
					}
				}

				boolean flag = false;
				MboSetRemote assetSet = mbo.getMboSet("$asset", "asset",
						"status in ('ACTIVE','OPERATING') and assetnum = '" + value0 + "'");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					if (!runLineSet.isEmpty() && runLineSet.count() > 0) {
						for (int j = 0; runLineSet.getMbo(j) != null; j++) {
							MboRemote runLine = runLineSet.getMbo(j);
							String assetnum = runLine.getString("assetnum");
							if (value0.equalsIgnoreCase(assetnum)) {
								flag = true;
								if (isNull1) {
									runLine.setValue("oill2", value1, 11L);
								}
								runLine.setValue("createdate", enddate, 11L);
								break;
							}
						}
						if (!flag) {
							EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
							runLine.setValue("assetnum", value0, 11L);
							if (isNull1) {
								runLine.setValue("oill2", value1, 11L);
							}
							runLine.setValue("createdate", enddate, 11L);
						}
					} else {
						EqRunLogLine runLine = (EqRunLogLine) runLineSet.add();
						runLine.setValue("assetnum", value0, 11L);
						if (isNull1) {
							runLine.setValue("oill2", value1, 11L);
						}
						runLine.setValue("createdate", enddate, 11L);
					}
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

			if (fileName != null && !fileName.toUpperCase().endsWith(".XLS")
					&& !fileName.toUpperCase().endsWith(".XLSX") && !fileName.toUpperCase().endsWith(".XLSM")) {
				throw new MXApplicationException("Warning", "This is not a xls file!");
			}
		}
	}
}
