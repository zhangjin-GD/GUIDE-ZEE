package guide.app.common;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.tivoli.maximo.report.birt.admin.ReportAdminService;
import com.ibm.tivoli.maximo.report.birt.runtime.ReportParameterData;

import psdi.app.doclink.Docinfo;
import psdi.app.doclink.Doclinks;
import psdi.common.role.MaxRole;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.SqlFormat;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXCipher;
import psdi.util.MXException;

public class CommonUtil {

	/**
	 * 
	 * @param tableName  表名(大写)
	 * @param keyName    编号字段（大写）
	 * @param keyWord    前缀 like '%PO%':PO就是关键字
	 * @param dateFormat 日期格式 YYYYMMDD或YYYY-MM
	 * @param length     长度
	 * @return
	 * @throws MXException
	 * @throws RemoteException
	 */
	public static String autoKeyNum(String tableName, String keyName, String keyWord, String dateFormat, int length)
			throws RemoteException, MXException {

		String dateStr = "";
		if (dateFormat != null && !dateFormat.isEmpty()) {
			dateStr = getCurrentDateFormat(dateFormat);
		}

		String keyNum = getKeyNum(tableName, keyName, keyWord, dateStr, length); //MATRECTRANS UDRECNUM PONUM的值  系统时间字符串格式    KEYLEN

		return keyNum;
	}

	private static String getKeyNum(String tableName, String keyName, String keyWord, String dateStr, int length)
			throws RemoteException, MXException {
		String keyValue = keyWord + dateStr;// 前缀+日期格式     PONUM+日期
		String keyNum = checkKeyNum(tableName, keyName, keyValue);
		if (keyNum != null && !keyNum.isEmpty()) {
			int end = keyNum.length();
			keyNum = keyValue + getKeyDigit(keyNum, end - length, end);
		} else {
			keyNum = keyValue + suppleMentZero(1, length);
		}
		return keyNum;

	}

	/**
	 * 截取字符串
	 * 
	 * @param keyNum
	 * @param st     开始
	 * @param end    结束
	 * @return
	 */
	public static String getKeyDigit(String keyNum, int st, int end) {

		String digit = keyNum.substring(st, end);

		int length = end - st;
		int num = Integer.parseInt(digit);

		num = num + 1;
		keyNum = suppleMentZero(num, length);
		return keyNum;
	}

	/**
	 * 获取最近一条数据
	 * 
	 * @param tableName
	 * @param keyName
	 * @param keyWord
	 * @return
	 * @throws RemoteException
	 * @throws MXException
	 */
	private static String checkKeyNum(String tableName, String keyName, String keyWord)
			throws RemoteException, MXException {

		if (tableName != null && !tableName.isEmpty()) {
			MXServer mxServer = MXServer.getMXServer();
			MboSetRemote tableSet = mxServer.getMboSet(tableName, mxServer.getSystemUserInfo());
			if (tableSet != null && !tableSet.isEmpty()) {
				tableSet.setQbe(keyName, keyWord + "%");
				tableSet.setOrderBy(keyName + " desc");
				tableSet.reset();
				// System.out.println("sql-最近一条->" + tableSet.getCompleteWhere());
				if (tableSet != null && !tableSet.isEmpty()) {
					return tableSet.getMbo(0).getString(keyName);
				}
			}
		}
		return "";
	}

	/**
	 * 补充零
	 * 
	 * @param num    数字
	 * @param length 需要返回字符串位数
	 * @return digit
	 */
	private static String suppleMentZero(long num, int length) {

		StringBuilder digit = new StringBuilder(Long.toString(num));
		/*
		 * 如果获取的后缀长度大于定义的长度，则取后缀长度
		 */
		if (digit.length() > length) {
			length = digit.length();
		}
		for (int i = digit.length(); i < length; i++) {

			digit.insert(0, "0");
		}
		return digit.toString();
	}

	public static Date getFormatDate(Date date, String dateFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String dateStr = sdf.format(date);
		return sdf.parse(dateStr);
	}
	
	//yyyy-MM-dd HH:mm:ss
	public static Date getDateFormat(String dateStr, String dateFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.parse(dateStr);
	}
	
	public static String getCurrentDateFormat(String dateFormat) throws RemoteException {
		Date date = MXServer.getMXServer().getDate();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}

	public static String getDateFormat(Date date, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}
	
	public static Date getCalDate(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	public static int getMonthMaxDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public static String getBudget(MboRemote mbo, String buditemnum) throws RemoteException, MXException {
		String company = mbo.getString("udcompany");
		String department = mbo.getString("uddept");
		String office = mbo.getString("udofs");
		if(office == null || office.equalsIgnoreCase("")){
			office = "null";
		}
		String budgetNum = "";
		MboSetRemote budgetSet = MXServer.getMXServer().getMboSet("UDBUDGET", MXServer.getMXServer().getSystemUserInfo());
		budgetSet.setWhere("buditemnum='" + buditemnum + "' and status = 'APPR' and year='"+CommonUtil.getCurrentDateFormat("yyyy")+"'"
				+ " and udcompany='"+company+"' and nvl(uddept,'"+department+"')='"+department+"' and nvl(udofs,'"+office+"')='"+office+"'");
		if (!budgetSet.isEmpty() && budgetSet.count() > 0) {
			budgetNum = budgetSet.getMbo(0).getString("budgetnum");
		}
		budgetSet.close();
		return budgetNum;
	}

	public static String getLocCP(String location) throws RemoteException, MXException {
		String company = "";
		MboSetRemote locationSet = MXServer.getMXServer().getMboSet("LOCATIONS", MXServer.getMXServer().getSystemUserInfo());
		locationSet.setWhere("location='" + location + "'");
		if (!locationSet.isEmpty() && locationSet.count() > 0) {
			company = locationSet.getMbo(0).getString("udcompany");
		}
		locationSet.close();
		return company;
	}
	
	public static boolean getLocSap(String location) throws RemoteException, MXException {
		boolean isSap = false;
		MboSetRemote locationSet = MXServer.getMXServer().getMboSet("LOCATIONS", MXServer.getMXServer().getSystemUserInfo());
		locationSet.setWhere("location='" + location + "'");
		if (!locationSet.isEmpty() && locationSet.count() > 0) {
			isSap = locationSet.getMbo(0).getBoolean("udissap");
		}
		locationSet.close();
		return isSap;
	}
	
	public static boolean getLocCS(String location) throws RemoteException, MXException {
		boolean isCS = false;
		MboSetRemote locationSet = MXServer.getMXServer().getMboSet("LOCATIONS", MXServer.getMXServer().getSystemUserInfo());
		locationSet.setWhere("location='" + location + "'");
		if (!locationSet.isEmpty() && locationSet.count() > 0) {
			isCS = locationSet.getMbo(0).getBoolean("udisconsignment");
		}
		locationSet.close();
		return isCS;
	}

	public static boolean isAssign(MboRemote mbo, String userId) throws RemoteException, MXException {
		MboSetRemote wfassignmentSet = mbo.getMboSet("WFASSIGNMENT");
		if (!wfassignmentSet.isEmpty() && wfassignmentSet.count() > 0) {
			MboRemote wfassignment = null;
			for (int i = 0; (wfassignment = wfassignmentSet.getMbo(i)) != null; i++) {
				if (wfassignment.getString("assigncode").equalsIgnoreCase(userId))
					return true;
			}
		}
		return false;
	}

	public static int getWfassignId(MboRemote mbo, String userId) throws RemoteException, MXException {
		int assignId = 0;
		MboSetRemote wfassignmentSet = mbo.getMboSet("WFASSIGNMENT");
		if (!wfassignmentSet.isEmpty() && wfassignmentSet.count() > 0) {
			MboRemote wfassignment = null;
			for (int i = 0; (wfassignment = wfassignmentSet.getMbo(i)) != null; i++) {
				if (wfassignment.getString("assigncode").equalsIgnoreCase(userId))
					assignId = wfassignment.getInt("assignid");
			}
		}
		return assignId;
	}

	public static String getMaxUser(String userId) throws RemoteException, MXException {
		String userid = null;
		MboSetRemote maxuserSet = MXServer.getMXServer().getMboSet("MAXUSER",
				MXServer.getMXServer().getSystemUserInfo());
		maxuserSet.setWhere("userid='" + userId + "'");
		if (!maxuserSet.isEmpty() && maxuserSet.count() > 0) {
			userid = maxuserSet.getMbo(0).getString("userid");
		}
		maxuserSet.close();
		return userid;
	}

	public static String getAttrs(String keyNum) throws RemoteException, MXException {
		String attrs = null;
		MboSetRemote mobileSerSet = MXServer.getMXServer().getMboSet("UDWEBSERVQUERY",
				MXServer.getMXServer().getSystemUserInfo());
		mobileSerSet.setWhere("keynum='" + keyNum + "'");
		if (!mobileSerSet.isEmpty() && mobileSerSet.count() > 0) {
			attrs = mobileSerSet.getMbo(0).getString("attrs");
		}
		mobileSerSet.close();
		return attrs;
	}
	
	public static String getAbbrCompany(String udcompany) throws RemoteException, MXException {
		String abbrCompany = null;
		MboSetRemote persongroupSet = MXServer.getMXServer().getMboSet("PERSONGROUP", MXServer.getMXServer().getSystemUserInfo());
		persongroupSet.setWhere("uddept='" + udcompany + "'");
		if (!persongroupSet.isEmpty() && persongroupSet.count() > 0) {
			abbrCompany = persongroupSet.getMbo(0).getString("persongroup");
		}
		persongroupSet.close();
		return abbrCompany;
	}

	public static String getValue(MboRemote mbo, String relationship, String attr) throws RemoteException, MXException {
		String value = "";
		MboSetRemote objectSet = mbo.getMboSet(relationship);
		if (!objectSet.isEmpty() && objectSet.count() > 0) {
			value = objectSet.getMbo(0).getString(attr);
		}
		return value;
	}

	public static String getValue(String tableName, String whereSql, String attr) throws RemoteException, MXException {
		String value = null;
		MboSetRemote objectSet = MXServer.getMXServer().getMboSet(tableName, MXServer.getMXServer().getSystemUserInfo());
		objectSet.setWhere(whereSql);
		if (!objectSet.isEmpty() && objectSet.count() > 0) {
			value = objectSet.getMbo(0).getString(attr);
		}
		objectSet.close();
		return value;
	}
	
	public static double getSumValue(String tableName, String whereSql, String attr) throws RemoteException, MXException {
		double value = 0.0;
		MboSetRemote objectSet = MXServer.getMXServer().getMboSet(tableName, MXServer.getMXServer().getSystemUserInfo());
		objectSet.setWhere(whereSql);
		if (!objectSet.isEmpty() && objectSet.count() > 0) {
			value = objectSet.sum(attr);
		}
		objectSet.close();
		return value;
	}

	public static void insertAutoWf(String ownerTable, String ownerId, String personId) throws RemoteException, MXException {
		try {
			MXServer mxServer = MXServer.getMXServer();
			MboSetRemote autoWfHistorySet = MXServer.getMXServer().getMboSet("UDAUTOWFHISTORY", MXServer.getMXServer().getSystemUserInfo());
			MboRemote autoWfHistory = autoWfHistorySet.add();
			autoWfHistory.setValue("createby", personId, 11L);
			autoWfHistory.setValue("createtime", mxServer.getDate(), 11L);
			autoWfHistory.setValue("ownertable", ownerTable, 11L);
			autoWfHistory.setValue("ownerid", ownerId, 11L);
			autoWfHistorySet.save();
			autoWfHistorySet.close();
		} catch (Exception e) {
			System.out.println("\n提示，流程发送记录写入失败！");
			e.printStackTrace();
		}
	}
	
	public static void ifaceLog(String description, String personId, String tableName, String keyNum, String ifaceNum,
			String message) throws RemoteException, MXException {
		try {
			MXServer mxServer = MXServer.getMXServer();
			MboSetRemote ifaceLogSet = MXServer.getMXServer().getMboSet("UDIFACELOG",
					MXServer.getMXServer().getSystemUserInfo());
			MboRemote ifaceLog = ifaceLogSet.add();
			if (description.length() > 250) {
				ifaceLog.setValue("description_longdescription", description, 11L);
			} else {
				ifaceLog.setValue("description", description, 11L);
			}
			if (message.length() > 200) {
				message = message.substring(0, 200);
			}
			ifaceLog.setValue("personid", personId, 11L);
			ifaceLog.setValue("transtime", mxServer.getDate(), 11L);
			ifaceLog.setValue("tablename", tableName, 11L);
			ifaceLog.setValue("keynum", keyNum, 11L);
			ifaceLog.setValue("ifacenum", ifaceNum, 11L);
			ifaceLog.setValue("message", message, 11L);
			ifaceLogSet.save();
			ifaceLogSet.close();
		} catch (Exception e) {
			System.out.println("\n提示，接口日志写入失败！");
			e.printStackTrace();
		}
	}

	public static void createDoc(MboRemote mbo, String docFile, String docName, String docType)
			throws RemoteException, MXException {
		String url = docFile + "\\" + docName;
		long docinfoid;
		String document;
		System.out.println("url-->"+url);
		MboSetRemote docinfoSet = mbo.getMboSet("$DOCINFO", "DOCINFO", "urlname='" + url + "'");
		if (docinfoSet.isEmpty()) {
			Docinfo docinfo = (Docinfo) docinfoSet.add();
			docinfo.getMboValue("document").autoKey();
			docinfo.setValue("urltype", "FILE", 2L);
			docinfo.setValue("newurlname", url, 11L);
			docinfo.setValue("urlname", url, 11L);
			docinfo.setValue("doctype", docType, 2L);
			docinfo.setValue("description", docName, 11L);
			docinfoid = docinfo.getLong("docinfoid");
			document = docinfo.getString("document");
		} else {
			MboRemote docinfo = docinfoSet.getMbo(0);
			docinfoid = docinfo.getLong("docinfoid");
			document = docinfo.getString("document");
		}
		MboSetRemote doclinksSet = mbo.getMboSet("$DOCLINKS", "DOCLINKS", "docinfoid='" + docinfoid + "'");
		if (doclinksSet.isEmpty()) {
			Doclinks doclinks = (Doclinks) doclinksSet.add();
			doclinks.setValue("docinfoid", docinfoid, 11L);
			doclinks.setValue("document", document, 11L);
			doclinks.setValue("ownertable", mbo.getName(), 2L);
			doclinks.setValue("ownerid", mbo.getUniqueIDValue(), 2L);
			doclinks.setValue("doctype", docType, 2L);
		}
	}

	/**
	 * @创建人: pr
	 * @创建时间:2022年4月26日
	 * @修改时间:
	 */
	public static boolean isInWF(MboRemote mbo) {
		boolean rs = false;
		try {
			String ownertable = mbo.getName();
			long ownerid = mbo.getUniqueIDValue();
			UserInfo userinfo = mbo.getUserInfo();
			MboSetRemote msr_wfinstance = MXServer.getMXServer().getMboSet("wfinstance", userinfo);
			msr_wfinstance.setWhere("ACTIVE=1 and OWNERTABLE='" + ownertable + "' and OWNERID=" + ownerid);
			if ((msr_wfinstance != null) && (msr_wfinstance.count() > 0)) {
				rs = true;
			}
			msr_wfinstance.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static  MboRemote getAdmin(MaxRole roleMbo) throws RemoteException, MXException {
		String adminUserId = MXServer.getMXServer().getProperty("mxe.adminuserid");
        SqlFormat sqf = new SqlFormat(roleMbo, "userid=:1");
        sqf.setObject(1, "MAXUSER", "USERID", adminUserId);
        MboSetRemote users = roleMbo.getMboSet("$adminuser", "maxuser", sqf.format());
        MboRemote user = users.getMbo(0);
        if(user == null)
            throw new MXApplicationException("signature", "AdminUserMissing", new String[] { adminUserId });
        else
            return user.getMboSet("PERSON").getMbo(0);
	}
	
	public static  MboRemote getWFAdmin(MaxRole roleMbo) throws RemoteException, MXException {
		String adminUserId = MXServer.getMXServer().getProperty("guide.wf.admin");
        SqlFormat sqf = new SqlFormat(roleMbo, "userid=:1");
        sqf.setObject(1, "MAXUSER", "USERID", adminUserId);
        MboSetRemote users = roleMbo.getMboSet("$adminuser", "maxuser", sqf.format());
        MboRemote user = users.getMbo(0);
        if(user == null)
            throw new MXApplicationException("signature", "AdminUserMissing", new String[] { adminUserId });
        else
            return user.getMboSet("PERSON").getMbo(0);
	}

	public static String getEmail(String personId) throws RemoteException, MXException {
		String email = null;
		MboSetRemote emailSet = MXServer.getMXServer().getMboSet("EMAIL", MXServer.getMXServer().getSystemUserInfo());
		emailSet.setWhere("personid='"+personId+"'");
		if (!emailSet.isEmpty() && emailSet.count() > 0) {
			email = emailSet.getMbo(0).getString("emailaddress");
		}
		emailSet.close();
		return email;
	}
	
	public static File getReport(UserInfo reportUserInfo, JSONObject paramRpt, JSONObject paramData) throws MXException, RemoteException, JSONException {
		MXServer mxServer = MXServer.getMXServer();
		String tempFolder = mxServer.getProperty("mxe.doclink.doctypes.defpath") + File.separator + "reports\\"+getString(paramRpt, "description");// 临时文件夹
		try {
			// 文件名称
			Date currentDate = mxServer.getDate();
			String reportOutputFileName = getString(paramRpt, "description") + getString(paramRpt, "keyNum") + String.valueOf(currentDate.getTime()) + ".pdf";
			String tempfile = tempFolder + File.separator + reportOutputFileName;
			// 创建文件
			File srcfile = new File(tempfile);
			srcfile.getParentFile().mkdirs();

			// 报表参数
			ReportParameterData parameterData = new ReportParameterData();
			Iterator keys = paramData.keys();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				String value = paramData.getString(key);
				parameterData.addParameter(key, value);
			}
			// 报表内容
			ReportAdminService reportAdminService = (ReportAdminService) mxServer.lookup("BIRTREPORT");
			byte reportOutput[] = reportAdminService.runReport(reportUserInfo, getString(paramRpt, "reportName"), getString(paramRpt, "appName"), parameterData, reportOutputFileName, "pdf", null);
			ByteArrayInputStream bis = new ByteArrayInputStream(reportOutput);

			// 报表写入文件
			try {
				createFileFromStream(srcfile, bis);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return srcfile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void createFileFromStream(File file, InputStream inputStream) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buf = new byte[1024];
		while (true) {
			int bytesRead = inputStream.read(buf);
			if (bytesRead <= 0) {
				break;
			}
			fos.write(buf, 0, bytesRead);
		}
		fos.flush();
		fos.close();
	}
	
	public static String getPassWord(byte[] bytes) throws MXException, RemoteException, SQLException, ParseException, JSONException{
		String decPassWord = null;
		String algTest = "DESede";
		String modeTest = "CBC";
		String paddingTest = "PKCS5Padding";
		// 6
//		String keyTest = "j3*9vk0e8rjvc9fj(*KFikd#";
//		String specTest = "kE*(RKc%";
		// 7
		String keyTest = "Sa#qk5usfmMI-@2dbZP9`jL3";
		String specTest = "beLd7$lB";
		String modTest = "";
		String providerTest = "";
		MXCipher mxc = new MXCipher(algTest, modeTest, paddingTest, keyTest, specTest, modTest, providerTest);
//		byte[] bytes = StrToBytes(str); 
		decPassWord = mxc.decData(bytes);
		return decPassWord;
	}
	
	public static byte[] StrToBytes(String str) {
		int len = str.length();
		if (len == 0 || len % 2 == 1) {
			return null;
		}
		byte[] b = new byte[len / 2];
		for (int i = 0; i < str.length(); i += 2) {
			b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
		}
		return b;
	}
	
	public static String getDefSapOrderType(int sapmappingid, String company, String attr) throws RemoteException, MXException {
		String value = null;
		MboSetRemote defSapOrderType = MXServer.getMXServer().getMboSet("UDSAPORDERTYPE", MXServer.getMXServer().getSystemUserInfo());
		defSapOrderType.setWhere("parent=" + sapmappingid + " and udcompany='"+company+"' and isdefault=1");
		if (!defSapOrderType.isEmpty() && defSapOrderType.count() > 0) {
			return defSapOrderType.getMbo(0).getString(attr);
		}
		defSapOrderType.close();
		return value;
	}
	
	public static String getSapOrderType(int sapmappingid, String company, String attr) throws RemoteException, MXException {
		String value = null;
		MboSetRemote sapOrderTypeSet = MXServer.getMXServer().getMboSet("UDSAPORDERTYPE", MXServer.getMXServer().getSystemUserInfo());
		sapOrderTypeSet.setWhere("parent=" + sapmappingid + " and udcompany='"+company+"'");
		if (!sapOrderTypeSet.isEmpty() && sapOrderTypeSet.count() > 0) {
			return sapOrderTypeSet.getMbo(0).getString(attr);
		}
		sapOrderTypeSet.close();
		return value;
	}
	
	public static JSONObject getMatHeader(MboRemote mbo, String num, String zTran, Date transDate, String vendor) throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		Header.put("ZSOURCE", getValue(mbo, "UDCOMPANY", "sapzsource"));// 原系统，固定值
		Header.put("BUKRS", getValue(mbo, "UDCOMPANY", "costcenter"));// 公司代码
		Header.put("ZSTOCKNO", num);// 物资单号
		Header.put("BUDAT", getDateFormat(transDate, "yyyyMMdd"));// 凭证日期;
		if(mbo.getString("udbudat") != null && !mbo.getString("udbudat").equalsIgnoreCase("")){
			Header.put("BUDAT", getDateFormat(mbo.getDate("udbudat"), "yyyyMMdd"));// 凭证日期;
		}
		Header.put("ZDATE1", getDateFormat(transDate, "yyyyMMdd"));// 传输日期
		Header.put("ZTRAN", zTran);// 移动类型
		if(vendor != null && !vendor.equalsIgnoreCase("")){
			Header.put("LIFNR", getValue("COMPANIES", "company='"+vendor+"'", "udmdmnum"));// 供应商或债权人的帐号0
		}
		Header.put("ZEAMHEADFIELD1", "");
		Header.put("ZEAMHEADFIELD2", "");
		Header.put("ZEAMHEADFIELD3", "");
		Header.put("ZEAMHEADFIELD4", "");
		Header.put("ZEAMHEADFIELD5", "");
		return Header;
	}
	
	public static JSONObject getMatUseHeader(MboRemote invuse) throws JSONException, RemoteException, MXException {
		String issueType = invuse.getString("udapptype");
		JSONObject Header = new JSONObject();
		Header.put("ZSOURCE", getValue(invuse, "UDCOMPANY", "sapzsource"));// 原系统，固定值
		Header.put("BUKRS", getValue(invuse, "UDCOMPANY", "costcenter"));// 公司代码
		Header.put("ZSTOCKNO", "USE" + invuse.getString("invusenum"));// 物资单号
		Header.put("BUDAT", getCurrentDateFormat("yyyyMMdd"));// 凭证日期;
		if(invuse.getString("udbudat") != null && !invuse.getString("udbudat").equalsIgnoreCase("")){
			Header.put("BUDAT", getDateFormat(invuse.getDate("udbudat"), "yyyyMMdd"));// 凭证日期;
		}
		Header.put("ZDATE1", getCurrentDateFormat("yyyyMMdd"));// 传输日期
//		Header.put("LIFNR", getString("vendor"));//供应商或债权人的帐号0
		if (issueType != null && (issueType.equalsIgnoreCase("MATUSEWO") || issueType.equalsIgnoreCase("MATUSEOT"))) {
			Header.put("ZTRAN", invuse.getString("udmovementtype"));// 移动类型
		} else {
			Header.put("ZTRAN", invuse.getString("udmovementtype"));// 移动类型
		}
		Header.put("ZEAMHEADFIELD1", "");
		Header.put("ZEAMHEADFIELD2", "");
		Header.put("ZEAMHEADFIELD3", "");
		Header.put("ZEAMHEADFIELD4", "");
		Header.put("ZEAMHEADFIELD5", "");
		return Header;
	}

	public static JSONArray getMatUseItem(MboRemote invuse) throws RemoteException, JSONException, MXException {
		JSONArray ItemSet = new JSONArray();
		MboRemote invuseline = null;
		MboSetRemote invuselineSet = invuse.getMboSet("INVUSELINE");
		MboRemote item = null;
		MboSetRemote itemSet = null;
		String assetnum = null;
		String KOSTL = null;
		String issueType = invuse.getString("udapptype");
		if (!invuselineSet.isEmpty() && invuselineSet.count() > 0) {
			for (int i = 0; (invuseline = invuselineSet.getMbo(i)) != null; i++) {
				assetnum = invuseline.getString("assetnum");
				JSONObject Item = new JSONObject();
				Item.put("ZSTOCKNO", "USE" + invuse.getString("invusenum"));// 物资单号
				Item.put("ZSTOCKITEMNO", invuseline.getInt("invuselinenum"));// 物资单项目号
//				item.put("WRBTR1", invuseline.getString("itemnum"));//预留字段0
				Item.put("ZQUANTITY", invuseline.getDouble("quantity"));// 数量
				Item.put("DMBTR3", String.format("%.2f", invuseline.getDouble("linecost")));// 成本
				if(issueType.equalsIgnoreCase("MATUSECS") || issueType.equalsIgnoreCase("MATRETCS")){
					Item.put("DMBTR3", String.format("%.2f", invuseline.getDouble("udlinecost")));// 成本
				}
				Item.put("WAERS", getValue(invuse, "UDCOMPANY", "currency"));// 货币码0

				itemSet = invuseline.getMboSet("ITEM");
				if (!itemSet.isEmpty() && itemSet.count() > 0) {
					item = itemSet.getMbo(0);
					Item.put("MTART", item.getString("udmaterialtype"));// 物料类型代码
					Item.put("MAKTX", item.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
					if(item.getString("description") == null || item.getString("description").equalsIgnoreCase("")){
						Item.put("MAKTX", invuseline.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
					}
					Item.put("ZUNIT", item.getString("orderunit"));// 单位
					Item.put("ZMATERIALCODE", invuseline.getString("itemnum"));// 物料编码
					Item.put("ZMATERIALL1", invuseline.getString("itemnum").substring(0, 2));// 物料大类
					Item.put("ZMATERIALL2", invuseline.getString("itemnum").substring(0, 4));// 物料中类
					Item.put("ZMATERIALL3", invuseline.getString("itemnum").substring(0, 6));// 物料小类
					Item.put("ZMATERIALDESCL1", getValue(item, "CLASS1", "description"));// 物料大类
					Item.put("ZMATERIALDESCL2", getValue(item, "CLASS2", "description"));// 物料中类
					Item.put("ZMATERIALDESCL3", getValue(item, "CLASS3", "description"));// 物料小类
				}
				if (assetnum != null && !assetnum.equalsIgnoreCase("")) {
					MboSetRemote assetSet = invuseline.getMboSet("ASSET");
					if (!assetSet.isEmpty() && assetSet.count() > 0) {
						MboRemote asset = assetSet.getMbo(0);
						Item.put("ZEQUIPCODE", assetnum);// 设备编号
						Item.put("ZEQUIPNAME", asset.getString("description").replaceAll("[<>&]", ""));// 设备描述
						Item.put("ZEQUIPCLASS", asset.getString("udassettypecode"));// 设备分类
						Item.put("ZEQUIPCLASSNAME", asset.getString("udassettypecode.name"));// 设备分类描述
						KOSTL = asset.getString("udcostcenter");
						if (KOSTL != null && KOSTL.equalsIgnoreCase("VIRTUAL")) {
							KOSTL = getValue(invuse, "UDDEPT", "COSTCENTER");
						}
					}
				} else {
					KOSTL = getValue(invuse, "UDDEPT", "COSTCENTER");
				}
				Item.put("KOSTL", KOSTL);// 成本中心0
				Item.put("AUFNR", invuseline.getString("udordertype"));// 内部订单号
				Item.put("ZREPAIRTYPE", invuseline.getString("WORKORDER.WORKTYPE.wtypedesc"));// 维修类型
				Item.put("ZEQUIPCLASSNAME", invuseline.getString("wonum"));// 维修工单编号
				Item.put("ZEAMITEMFIELD1", "");
				Item.put("ZEAMITEMFIELD2", "");
				Item.put("ZEAMITEMFIELD3", "");
				Item.put("ZEAMITEMFIELD4", "");
				Item.put("ZEAMITEMFIELD5", "");
				ItemSet.put(Item);
			}
		}
		return ItemSet;
	}
	
	public static JSONObject getMatRecHeader(MboRemote po, String recNum, String issueType, String zType) throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		Header.put("ZSOURCE", getValue(po, "UDCOMPANY", "sapzsource"));// 原系统，固定值
		Header.put("BUKRS", getValue(po, "UDCOMPANY", "costcenter"));// 公司代码
		Header.put("ZSTOCKNO", "REC" + recNum);// 物资单号
		Header.put("BUDAT", getCurrentDateFormat("yyyyMMdd"));// 凭证日期;
		if(po.getString("udbudat") != null && !po.getString("udbudat").equalsIgnoreCase("")){
			Header.put("BUDAT", getDateFormat(po.getDate("udbudat"), "yyyyMMdd"));// 凭证日期;
		}
		Header.put("ZDATE1", getCurrentDateFormat("yyyyMMdd"));// 传输日期
		Header.put("LIFNR", getValue("COMPANIES", "company='"+po.getString("vendor")+"'", "udmdmnum"));// 供应商或债权人的帐号0
		if (issueType != null && issueType.equalsIgnoreCase("RECEIPT")) {
			Header.put("ZTRAN", "101");// 接收
		} else if (issueType != null && issueType.equalsIgnoreCase("RETURN")) {
			if(zType != null && zType.equalsIgnoreCase("Y")){
				Header.put("ZTRAN", "101X");// 冲销
			}else if(zType != null && zType.equalsIgnoreCase("N")){
				Header.put("ZTRAN", "102");// 退货
			}
		}
		Header.put("ZEAMHEADFIELD1", "");
		Header.put("ZEAMHEADFIELD2", "");
		Header.put("ZEAMHEADFIELD3", "");
		Header.put("ZEAMHEADFIELD4", "");
		Header.put("ZEAMHEADFIELD5", "");
		return Header;
	}
	
	public static JSONArray getMatRecItem(MboRemote po, MboSetRemote parentmatrecSet, String recNum) throws RemoteException, JSONException, MXException {
		JSONArray ItemSet = new JSONArray();
		MboRemote matrectrans = null;
		MboRemote item = null;
		MboSetRemote itemSet = null;
//		String assetnum = null;
		boolean isSap = false;
		boolean isCS = false;
		int flag = 1;
		double totalcost = 0.00;
		String costCenter = "";
		String LIFNR1 = "";
		String udcosttype = "";
		for (int i = 0; (matrectrans = parentmatrecSet.getMbo(i)) != null; i++) {
			isSap = getLocSap(matrectrans.getString("tostoreloc"));
			isCS = getLocCS(matrectrans.getString("tostoreloc"));
			String udcompany = po.getString("udcompany");
			/**
			 * ZEE-即收即发时,库房为空,但需传SAP
			 */
			if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
				isSap = true;
			}
			
			if ((matrectrans.getString("udsapnum") == null || matrectrans.getString("udsapnum").equalsIgnoreCase(""))
					&& isSap && !isCS && matrectrans.toBeAdded()) {
				JSONObject Item = new JSONObject();
				if (matrectrans.getDouble("quantity") < 0)
					flag = -1;
				Item.put("ZSTOCKNO", "REC" + recNum);// 物资单号
				Item.put("ZSTOCKITEMNO", matrectrans.getString("udzitemno"));// 物资单项目号
				if(matrectrans.getString("udztype").equalsIgnoreCase("Y")){//退货
					Item.put("ZSTOCKITEMNO", getValue(matrectrans, "ORIGINALRECEIPT", "udzitemno"));// 物资单项目号
				}
//				item.put("WRBTR1", matrectrans.getString("itemnum"));//预留字段0
				Item.put("ZQUANTITY", String.format("%.2f", flag * matrectrans.getDouble("quantity")));// 数量
				Item.put("DMBTR3", String.format("%.2f", flag * matrectrans.getDouble("linecost")));// 成本
				Item.put("WAERS", getValue(po, "UDCOMPANY", "currency"));// 货币码0
				
				//订单需传字段
				totalcost = matrectrans.getDouble("linecost") + matrectrans.getDouble("tax1");
				Item.put("DMBTR1", String.format("%.2f", flag * totalcost));// 含税金额
				Item.put("DMBTR4", String.format("%.2f", flag * matrectrans.getDouble("tax1")));// 税额
				Item.put("MWSKZ", getValue(matrectrans, "TAX1CODE", "description"));// 税代码
				Item.put("EBELN", matrectrans.getString("ponum"));// 采购凭证号
				Item.put("EBELP", matrectrans.getString("polinenum"));// 采购凭证号采购凭证项目编号
				Item.put("ZAUXFIELD", "T001-" + po.getString("udconnum"));// 付款条款(海外专用)+合同号
				
				itemSet = matrectrans.getMboSet("ITEM");
				String issue1 = matrectrans.getString("issue");
				if (!itemSet.isEmpty() && itemSet.count() > 0) {
					item = itemSet.getMbo(0);
					//如果是即收即发，则物料类型按照ZEE的costtype来传SAP；
					//如果非即收即发，则物料类型按照总部SAP的itemtype来传SAP
					//只传1条记录（接收时）
					MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
					uditemcpSet.setWhere(" udcompany = 'ZEE' and itemnum = '"+ item.getString("itemnum") + "' ");
					uditemcpSet.reset();
					if(!uditemcpSet.isEmpty() && uditemcpSet.count() > 0){
						MboRemote uditemcp = uditemcpSet.getMbo(0);
			            udcosttype = uditemcp.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格	
			            if (isNumeric(udcosttype) && udcosttype.length() >= 4) {
			                udcosttype = udcosttype.substring(0, 4);
			            } else {
			                udcosttype = udcosttype; 
			            }
					}
					if (issue1 != null && issue1.equalsIgnoreCase("Y")) {
						if(udcosttype!=null && !udcosttype.equalsIgnoreCase("")){
							Item.put("MTART", udcosttype);
						}
					}
					if(issue1 != null && issue1.equalsIgnoreCase("N")){
						Item.put("MTART", item.getString("udmaterialtype"));	
					}
					Item.put("MAKTX", item.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
					if(item.getString("description") == null || item.getString("description").equalsIgnoreCase("")){
						Item.put("MAKTX", matrectrans.getString("description").replaceAll("[<>&]", ""));// 物料描述(短文本)
					}
					Item.put("ZUNIT", matrectrans.getString("receivedunit"));// 单位
					Item.put("ZMATERIALCODE", matrectrans.getString("itemnum"));// 物料编码
					Item.put("ZMATERIALL1", matrectrans.getString("itemnum").substring(0, 2));// 物料大类
					Item.put("ZMATERIALL2", matrectrans.getString("itemnum").substring(0, 4));// 物料中类
					Item.put("ZMATERIALL3", matrectrans.getString("itemnum").substring(0, 6));// 物料小类
					Item.put("ZMATERIALDESCL1", getValue(item, "CLASS1", "description"));// 物料大类
					Item.put("ZMATERIALDESCL2", getValue(item, "CLASS2", "description"));// 物料中类
					Item.put("ZMATERIALDESCL3", getValue(item, "CLASS3", "description"));// 物料小类
				}
				/*
				assetnum = matrectrans.getString("assetnum");
				if (assetnum != null && !assetnum.equalsIgnoreCase("")) {
					MboSetRemote assetSet = matrectrans.getMboSet("MEARCVASSET");
					if (!assetSet.isEmpty() && assetSet.count() > 0) {
						MboRemote asset = assetSet.getMbo(0);
						Item.put("ZEQUIPCODE", assetnum);// 设备编号
						Item.put("ZEQUIPNAME", asset.getString("description"));// 设备描述
						Item.put("ZEQUIPCLASS", asset.getString("udassettypecode"));// 设备分类
						Item.put("ZEQUIPCLASSNAME", asset.getString("udassettypecode.description"));// 设备分类描述
					}
				}
				Item.put("KOSTL", "");// 成本中心0
				Item.put("AUFNR", "");// 内部订单号
				Item.put("ZREPAIRTYPE", matrectrans.getString("WORKORDER.WORKTYPE.wtypedesc"));// 维修类型
				Item.put("ZEQUIPCLASSNAME", matrectrans.getString("refwo"));// 维修工单编号
				*/
				costCenter = getValue(matrectrans, "POLINE", "udcostcenter");
				if(costCenter != null && !costCenter.equalsIgnoreCase("")){
					Item.put("KOSTL", costCenter);// 成本中心0
				}
				
				
				/**
				 * ZEE-即收即发:1、取POLINE设备的成本中心 2、取POLINE部门成本中心 3、取PO部门成本中心
				 * 2024-06-24 09:33:24
				**/
				String issue = matrectrans.getString("POLINE.issue");
				if (issue != null && issue.equalsIgnoreCase("Y")) {
					String udcostcenterasset = matrectrans.getString("POLINE.udcostcenterasset");
					String udcostcenterzee = matrectrans.getString("POLINE.udcostcenterzee");
					if (udcostcenterasset!=null && !udcostcenterasset.equalsIgnoreCase("")) {
						Item.put("KOSTL", udcostcenterasset);
					} else if (udcostcenterzee!=null && !udcostcenterzee.equalsIgnoreCase("")) {
						Item.put("KOSTL", udcostcenterzee);
					} else {
						Item.put("KOSTL", po.getString("UDDEPT.costcenter"));
					}
				}
				
				LIFNR1 = getValue(matrectrans, "POLINE", "udvendor");
				if(LIFNR1 != null && !LIFNR1.equalsIgnoreCase("")){
					Item.put("LIFNR1", LIFNR1);//运费供应商账号
				}
				Item.put("ZEAMITEMFIELD1", "");
				Item.put("ZEAMITEMFIELD2", "");
				Item.put("ZEAMITEMFIELD3", "");
				Item.put("ZEAMITEMFIELD4", "");
				Item.put("ZEAMITEMFIELD5", "");
				ItemSet.put(Item);
			}
		}
		return ItemSet;
	}
	
	public static JSONObject getSerRecHeader(MboRemote po, String recNum, String issueType, String zType) throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		MboSetRemote companySet = po.getMboSet("UDCOMPANY");
		if (!companySet.isEmpty() && companySet.count() > 0) {
			MboRemote company = companySet.getMbo(0);
			Header.put("ZSOURCE", company.getString("sapzsource"));// 原系统，固定值
			Header.put("BUKRS", company.getString("costcenter"));// 公司代码
			Header.put("ZTYPE", "D1");// 接口类型
			Header.put("UNIQUEID", "SER" + recNum);// 单据编号
			Header.put("SHA1", "SER" + recNum + getCurrentDateFormat("yyyyMMdd"));// 电子签名编号
			Header.put("PATH", "");// 附件网址链接
			Header.put("VENDORNAME", getValue(po, "VENDOR", "name"));// 供应商名称
			Header.put("VATNUMBER", "A965169921");// 供应商税号
			Header.put("LIFNR", getValue("COMPANIES", "company='"+po.getString("vendor")+"'", "udmdmnum"));// 供应商或债权人的帐号0
			Header.put("COMPANYNAME", company.getString("description"));// 公司名称
			Header.put("INVOICE_STATUS", "1");// 发票状态1：新增 / 2：更新 / 3：删除
			Header.put("ORIGINALINV", "SER" + recNum);// 物资单号
			Header.put("EXPEDITIONDATE", getCurrentDateFormat("yyyyMMdd"));// 期望付款日期 
			Header.put("DUEDATE", "");// 到期日期
			Header.put("DMBTR1", po.getDouble("totalcost"));// 含税金额
			Header.put("DMBTR2", po.getDouble("pretaxtotal"));// 不含税金额
			Header.put("WAERS", getValue(po, "UDCOMPANY", "currency"));// 货币
			Header.put("TEXT", "");// 备注文本
			Header.put("PROCESS", "3");// 货币1.交货单（与物资系统入库单号关联）3.费用报销
			Header.put("ZHEADER", "");// 抬头
			
			Header.put("ZZ01", "");
			Header.put("ZZ02", "");
			Header.put("ZZ03", "");
			Header.put("ZZ04", "");
			Header.put("ZZ05", "");
			Header.put("ZZ06", "");
			Header.put("ZZ07", "");
			Header.put("ZZ08", "");
			Header.put("ZZ09", "");
			Header.put("ZZ010", "");
			Header.put("EPIGRAPH", "");//EPIGRAPH
			//SAP不存在冲销了吗？
			if (issueType != null && issueType.equalsIgnoreCase("RECEIPT")) {
				Header.put("ZTRAN", "101");// 接收
			} else if (issueType != null && issueType.equalsIgnoreCase("RETURN")) {
				if(zType != null && zType.equalsIgnoreCase("Y")){
					Header.put("ZTRAN", "101X");// 冲销
				}else if(zType != null && zType.equalsIgnoreCase("N")){
					Header.put("ZTRAN", "102");// 退货
				}
			}
		}
		return Header;
	}
	
	public static JSONArray getSerRecItem(MboRemote po, MboSetRemote nocostServrectransSet, String recNum) throws RemoteException, JSONException, MXException {
//		private String LINECONCEPT;
//		private String BOOKINGLINE;
//		private String ANALYTICALACC;
//		private String ZSERVICE;
//		private String ZKOSTL ;
//		private String DETAIL ;
//		private String WORKERTYPE;
//		private String PERCENTDISTRIBUTION;
//		private String DMBTR2 ;
//		private String ZZMWSKZ1;
//		private String ZZWITHHOLD;
//		private String WORKINGDAY;
//		private String ACC_DESCRIPTION ;
//		private String EXPENSETYPE;
		JSONArray ItemSet = new JSONArray();
		MboRemote nocostServrectrans = null;
		MboSetRemote companySet = po.getMboSet("UDCOMPANY");
		if (!companySet.isEmpty() && companySet.count() > 0) {
			MboRemote company = companySet.getMbo(0);
			for (int i = 0; (nocostServrectrans = nocostServrectransSet.getMbo(i)) != null; i++) {
				if ((nocostServrectrans.getString("udsapnum") == null || nocostServrectrans.getString("udsapnum").equalsIgnoreCase(""))
						&& nocostServrectrans.toBeAdded()) {
					JSONObject Item = new JSONObject();
					Item.put("ZSOURCE", company.getString("sapzsource"));// 数据源
					Item.put("BUKRS", company.getString("costcenter"));// 公司代码
					Item.put("ZTYPE", "D1");// 接口类型
					Item.put("APPROVEDSTATUS", "1");// 审核状态0：未审核；1：已审核
					Item.put("UNIQUEID", "SER" + recNum);// 单据编号
					Item.put("CONCEPTLINE", nocostServrectrans.getString("udzitemno"));// 物资单号
					Item.put("ZSTOCKNO", getValue(nocostServrectrans, "UDBUDGET", "buditemnum"));// 费用编码 或者 入库单号
					String KOSTL = getValue(nocostServrectrans, "PRDEPT", "COSTCENTER");
					String assetnum = nocostServrectrans.getString("assetnum");
					if (assetnum != null && !assetnum.equalsIgnoreCase("")) {
					   KOSTL = getValue(nocostServrectrans, "ASSET", "UDCOSTCENTER");
					}
					Item.put("ZKOSTL", KOSTL);// 税代码
					Item.put("DMBTR2", nocostServrectrans.getDouble("linecost"));
					Item.put("ZZMWSKZ1", getValue(nocostServrectrans, "TAX1CODE", "description"));// 税代码
					Item.put("ZZ01", "");
					Item.put("ZZ02", "");
					Item.put("ZZ03", "");
					Item.put("ZZ04", "");
					Item.put("ZZ05", "");
					Item.put("ZZ06", "");
					Item.put("ZZ07", "");
					Item.put("ZZ08", "");
					Item.put("ZZ09", "");
					Item.put("ZZ010", "");
					ItemSet.put(Item);
				}
			}
		}
		return ItemSet;
	}
	
	public static JSONObject getIvoHeader(MboRemote invoice) throws JSONException, RemoteException, MXException {
		JSONObject Header = new JSONObject();
		MboSetRemote companySet = invoice.getMboSet("UDCOMPANY");
		if (!companySet.isEmpty() && companySet.count() > 0) {
			MboRemote company = companySet.getMbo(0);
			Header.put("ZSOURCE", company.getString("sapzsource"));// 原系统，固定值
			Header.put("BUKRS", company.getString("costcenter"));// 公司代码
			Header.put("ZTYPE", "D1");// 接口类型
			Header.put("UNIQUEID", "IVO" + invoice.getString("invoicenum"));// 单据编号
			Header.put("SHA1", "IVO" + invoice.getString("invoicenum") + getCurrentDateFormat("yyyyMMdd"));// 电子签名编号
			Header.put("PATH", "");// 附件网址链接
			Header.put("VENDORNAME", getValue(invoice, "COMPANIES", "name"));// 供应商名称
			Header.put("VATNUMBER", "A965169921");// 供应商税号
			Header.put("LIFNR", getValue("COMPANIES", "company='"+invoice.getString("vendor")+"'", "udmdmnum"));// 供应商或债权人的帐号0
			Header.put("COMPANYNAME", company.getString("description"));// 公司名称
			Header.put("INVOICE_STATUS", "1");// 发票状态1：新增 / 2：更新 / 3：删除
			Header.put("ORIGINALINV", "IVO" + invoice.getString("invoicenum"));// 物资单号
			Header.put("EXPEDITIONDATE", getCurrentDateFormat("yyyyMMdd"));// 期望付款日期 
			Header.put("DUEDATE", "");// 到期日期
			Header.put("DMBTR1", invoice.getDouble("totalcost"));// 含税金额
			Header.put("DMBTR2", invoice.getDouble("pretaxtotal"));// 不含税金额
			Header.put("WAERS", getValue(invoice, "UDCOMPANY", "currency"));// 货币
			Header.put("TEXT", "");// 备注文本
			Header.put("PROCESS", "1");// 货币1.交货单（与物资系统入库单号关联）3.费用报销
			Header.put("ZHEADER", "");// 抬头
			
			Header.put("ZZ01", "");
			Header.put("ZZ02", "");
			Header.put("ZZ03", "");
			Header.put("ZZ04", "");
			Header.put("ZZ05", "");
			Header.put("ZZ06", "");
			Header.put("ZZ07", "");
			Header.put("ZZ08", "");
			Header.put("ZZ09", "");
			Header.put("ZZ010", "");
			Header.put("EPIGRAPH", "");//EPIGRAPH
		}
		return Header;
	}
	
	public static JSONArray getIvoItem(MboRemote invoice) throws RemoteException, JSONException, MXException {
//		private String LINECONCEPT;
//		private String BOOKINGLINE;
//		private String ANALYTICALACC;
//		private String ZSERVICE;
//		private String ZKOSTL ;
//		private String DETAIL ;
//		private String WORKERTYPE;
//		private String PERCENTDISTRIBUTION;
//		private String DMBTR2 ;
//		private String ZZMWSKZ1;
//		private String ZZWITHHOLD;
//		private String WORKINGDAY;
//		private String ACC_DESCRIPTION ;
//		private String EXPENSETYPE;
		JSONArray ItemSet = new JSONArray();
		MboSetRemote companySet = invoice.getMboSet("UDCOMPANY");
		if (!companySet.isEmpty() && companySet.count() > 0) {
			MboRemote company = companySet.getMbo(0);
			MboSetRemote invoicelineSet = invoice.getMboSet("INVOICELINE");
			if (!invoicelineSet.isEmpty() && invoicelineSet.count() > 0) {
				MboRemote invoiceline = null;
				for (int i = 0; (invoiceline = invoicelineSet.getMbo(i)) != null; i++) {
					JSONObject Item = new JSONObject();
					Item.put("ZSOURCE", company.getString("sapzsource"));// 数据源
					Item.put("BUKRS", company.getString("costcenter"));// 公司代码
					Item.put("ZTYPE", "D1");// 接口类型
					Item.put("APPROVEDSTATUS", "1");// 审核状态0：未审核；1：已审核
					Item.put("UNIQUEID", "IVO" + invoice.getString("invoicenum"));// 单据编号
					Item.put("CONCEPTLINE", invoiceline.getString("invoicelinenum"));// 物资单号
					Item.put("ZSTOCKNO", "REC" + getValue(invoiceline, "MATRECTRANS", "UDRECNUM"));// 费用编码 或者 入库单号
					Item.put("ZKOSTL", "");
					Item.put("DMBTR2", invoiceline.getDouble("linecost"));
					Item.put("ZZMWSKZ1", getValue(invoiceline, "TAX1CODE", "description"));// 税代码
					Item.put("ZZ01", "");
					Item.put("ZZ02", "");
					Item.put("ZZ03", "");
					Item.put("ZZ04", "");
					Item.put("ZZ05", "");
					Item.put("ZZ06", "");
					Item.put("ZZ07", "");
					Item.put("ZZ08", "");
					Item.put("ZZ09", "");
					Item.put("ZZ010", "");
					ItemSet.put(Item);
				}
			}
		}
		return ItemSet;
	}
	
	public static void setSapStatus(String obejctName, String sql, String sapNum, String sapStatus) throws RemoteException, MXException {
		MboSetRemote transSet = MXServer.getMXServer().getMboSet(obejctName, MXServer.getMXServer().getSystemUserInfo());
		transSet.setWhere(sql+" and udsapstatus is null and udsapnum is null");
		if(!transSet.isEmpty() && transSet.count() > 0){
			transSet.getMbo(0).setValue("udsapnum", sapNum, 11L);
			transSet.getMbo(0).setValue("udsapstatus", sapStatus, 11L);
			transSet.save();
		}
		transSet.close();
	}
	
	public static String getString(JSONObject js, String attr) throws JSONException {
		String lsrtn = "";
		Object object = js.get(attr);
		if (object != null)
			lsrtn = object.toString();
		return lsrtn;
	}
	
	public static String getStrDecode(String deCodeStr) {
		byte[] deCodeStrBytes = Base64.getDecoder().decode(deCodeStr);
		String returnStr = "";
		try {
			returnStr = new String(deCodeStrBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	public static String sendGDEam(String url, JSONObject jsonData) throws RemoteException{
		System.out.println("\n--------------sendGDEam："+url);
		System.out.println("\n--------------sendGDEam："+jsonData);
		if(url == null || url.equalsIgnoreCase("")){
			return "invalid url...";
		}
		String result = "500";
        try {
        	HttpPost post = new HttpPost(url);
            //创建参数集合
            List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            //添加参数
            Iterator keys = jsonData.keys();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				String value = jsonData.getString(key);
				list.add(new BasicNameValuePair(key, value));
			}
            //把参数放入请求对象，，post发送的参数list，指定格式
            post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            // 添加请求头参数
            String authorization = MXServer.getMXServer().getProperty("guide.header.authorization");
            if (authorization != null && !authorization.isEmpty()) {
                    post.addHeader("Authorization", authorization);
            }
            //添加请求头参数
            //post.addHeader("timestamp","1594695607545");
            CloseableHttpClient client = HttpClients.createDefault();
            //启动执行请求，并获得返回值
            CloseableHttpResponse response = client.execute(post);
            //得到返回的entity对象
            HttpEntity entity = response.getEntity();
            //把实体对象转换为string
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
	public static JSONObject getFuelGoct(String sqlNum, Date startTime, Date endTime) throws MXException, RemoteException, JSONException {
		JSONObject returnData = new JSONObject();
		JSONArray fuelDataSet = new JSONArray();
		String sql = CommonUtil.getAttrs(sqlNum);
		sql = sql.replace(":starttime", "'"+CommonUtil.getDateFormat(startTime, "yyyy-MM-dd HH:mm:ss")+"'");
		sql = sql.replace(":endtime", "'"+CommonUtil.getDateFormat(endTime, "yyyy-MM-dd HH:mm:ss")+"'");
		System.out.println("\n-------------------------"+sql);
		// 连接类，得到和目标数据库连接的对象
		Connection connection = null;
		PreparedStatement prepar = null;
		ResultSet set = null;
		try {
			String gddbUrl = getPropertyValue("guide.gddb.url", "jdbc:mysql://10.18.11.22:3306/tos?serverTimezone=GMT");
			String gddbUser = getPropertyValue("guide.gddb.user", "root");
			String gddbPassWord = getPropertyValue("guide.gddb.password", "1qaz@wsx");
			Class.forName("com.mysql.cj.jdbc.Driver");// 加载驱动类
			connection = DriverManager.getConnection(gddbUrl, gddbUser, gddbPassWord);// 获取与目标数据库的连接
			prepar = connection.prepareStatement(sql);
			set = prepar.executeQuery();//将得到的数据库响应的查询结果存放在ResultSet对象中
			while(set.next())
			{
				JSONObject fuelData = new JSONObject();
				fuelData.put("CarNum", set.getString("CarNum"));
				fuelData.put("OnceLiter", set.getDouble("OnceLiter"));
				fuelData.put("CompanyName", set.getString("CompanyName"));
				fuelDataSet.put(fuelData);
			}
			returnData.put("data", fuelDataSet);
			returnData.put("result", "Y");
		} catch (SQLException e) {
			returnData.put("result", e.toString());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			returnData.put("result", e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			returnData.put("result", e.toString());
			e.printStackTrace();
		}
		finally {
			try {
				if (set != null){
					set.close();
				}
				if (prepar != null){
					prepar.close();
				}
				if (connection != null){
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnData;
	}

	public static String sendQpctTos(String param) throws RemoteException {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder("");
		String qpctTosUrl = getPropertyValue("guide.tos.qpct.url", "http://192.168.6.121:8020/index/postCAREPLAN");
		try {
			URL realUrl = new URL(qpctTosUrl);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(param);
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			result = result.append(e.toString());
		}finally{
			if(out!=null){ 
				try { 
					out.close(); 
				}catch(Exception ex){} 
			}
			if(in!=null){ 
				try { 
					in.close(); 
				}catch(Exception ex){} 
			}
		}
		return result.toString();
	}
	
	public static String getPropertyValue(String propName, String defaultValue) throws RemoteException {
		String value = MXServer.getMXServer().getProperty(propName);
		if(value == null || value.equalsIgnoreCase("")){
			value = defaultValue;
		}
		return value;
	}

	public static boolean isAdmin(String curUserId) throws RemoteException, MXException {
		boolean isAdmin = false;
		MboSetRemote groupUserSet = MXServer.getMXServer().getMboSet("GROUPUSER", MXServer.getMXServer().getSystemUserInfo());
		groupUserSet.setWhere("groupname='MAXADMIN' and userid='"+curUserId+"'");
		if (!groupUserSet.isEmpty() && groupUserSet.count() > 0) {
			isAdmin = true;
		}
		groupUserSet.close();
		return isAdmin;
	}
	
    /**
     * 检查字符串是否为数字
     */
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
