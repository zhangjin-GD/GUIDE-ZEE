package guide.iface.zeerunning;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.jws.WebMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.emory.mathcs.backport.java.util.Collections;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.AppService;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * @function:ZEE-runningHour&Fuel接口BJZEE
 * @author:zj
 * @date:2023-07-10 08:46:02
 * @modify:2024-02-18 16:54:29
 */
public class ZeeRunhourService extends AppService implements ZeeRunhourServiceRemote {

	public ZeeRunhourService() throws RemoteException {
		super();
	}

	public ZeeRunhourService(MXServer mxServer) throws RemoteException {
		super(mxServer);
	}

	// http://10.18.11.156:9080/meaweb/wsdl/UDRUNHOUR.wsdl -156测试
	
	@WebMethod
	public String UdZeeRunhourService(String json) throws RemoteException, MXException {
		String flag = "";
		try {
			JSONArray ja = new JSONArray(json);
			
			//校验json数据
			JSONObject jsonResult0 = new JSONObject();
			String jsonResultStr0 = "";
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				
				String assetnum0 = getString(jo, "assetnum");
				String meterreading0 = getString(jo, "meterreading");
				String meterdate0 = getString(jo, "meterdate");
				
				if (assetnum0.equalsIgnoreCase("")) {
					jsonResult0.put("status", "1");
					jsonResult0.put("msg", "assetnum is not allowed to be empty");
					jsonResult0.put("json", json);
					jsonResultStr0 = jsonResult0.toString();
					return jsonResultStr0;
				}
				if (meterreading0.equalsIgnoreCase("")) {
					jsonResult0.put("status", "2");
					jsonResult0.put("msg", "meterreading is not allowed to be empty");
					jsonResult0.put("json", json);
					jsonResultStr0 = jsonResult0.toString();
					return jsonResultStr0;
				}
				if (meterdate0.equalsIgnoreCase("")) {
					jsonResult0.put("status", "3");
					jsonResult0.put("msg", "meterdate is not allowed to be empty");
					jsonResult0.put("json", json);
					jsonResultStr0 = jsonResult0.toString();
					return jsonResultStr0;
				}
				
				MboSetRemote assetSet0 = MXServer.getMXServer().getMboSet("ASSET",MXServer.getMXServer().getSystemUserInfo());
				assetSet0.setWhere(" assetnum='"+assetnum0+"' ");
				assetSet0.reset();
				if (assetSet0.isEmpty() || assetSet0.count()==0) {
					jsonResult0.put("status", "4");
					jsonResult0.put("msg", "assetnum"+assetnum0+" does not exist");
					jsonResult0.put("json", json);
					jsonResultStr0 = jsonResult0.toString();
					return jsonResultStr0;
				}
				assetSet0.close();
			}
			
			//记录到JSON中间表
			MboSetRemote udzeerunjsonSet = MXServer.getMXServer().getMboSet("UDZEERUNJSON",MXServer.getMXServer().getSystemUserInfo());
			udzeerunjsonSet.setWhere(" 1=2 ");
			udzeerunjsonSet.reset();
			MboRemote udzeerunjson = udzeerunjsonSet.add(11L);
			udzeerunjson.setValue("description", "ZEE", 11L);
			udzeerunjson.setValue("json", json, 11L);
			udzeerunjson.setValue("enterby", "MAXADMIN", 11L);
			udzeerunjson.setValue("enterdate", MXServer.getMXServer().getDate(), 11L);
			udzeerunjsonSet.save();
			udzeerunjsonSet.close();
			
			
			String assetnum = ""; //设备编码
			String meterreading = ""; //仪表示数
			String meterdate = ""; //抄表时间
			
			JSONObject jsonResult = new JSONObject();
			String jsonResultStr = "";
			
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				
				assetnum = getString(jo, "assetnum");
				meterreading = getString(jo, "meterreading");
				meterdate = getString(jo, "meterdate");
				
				//记录到JSON明细中间表
				MboSetRemote udzeerunrecordSet = MXServer.getMXServer().getMboSet("UDZEERUNRECORD",MXServer.getMXServer().getSystemUserInfo());
				udzeerunrecordSet.setWhere(" 1=2 ");
				udzeerunrecordSet.reset();
				MboRemote udzeerunrecord = udzeerunrecordSet.add(11L);
				udzeerunrecord.setValue("description", "ZEE", 11L);
				udzeerunrecord.setValue("assetnum", assetnum, 11L);
				udzeerunrecord.setValue("meterrecord", Double.parseDouble(meterreading), 11L);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					udzeerunrecord.setValue("meterdate", sdf.parse(meterdate), 11L);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				udzeerunrecord.setValue("enterby", "MAXADMIN", 11L);
				udzeerunrecord.setValue("enterdate", MXServer.getMXServer().getDate(), 11L);
				udzeerunrecordSet.save();
				udzeerunrecordSet.close();
				
//				System.out.println("\n----219-----assetnum---"+assetnum);
//				System.out.println("\n----219-----meterreading---"+meterreading);
//				System.out.println("\n----219-----meterdate---"+meterdate);
				MboSetRemote meterreadingSet = MXServer.getMXServer().getMboSet("METERREADING",MXServer.getMXServer().getSystemUserInfo());
				meterreadingSet.setWhere(" assetnum = '"+assetnum+"' order by reading desc ");
				meterreadingSet.reset();
				if (!meterreadingSet.isEmpty() && meterreadingSet.count() > 0) {
					MboRemote meterread = meterreadingSet.getMbo(0);
					double ss = Double.parseDouble(meterreading);
					meterread.setValue("modifiedreading", ss, 2L);
				}
				meterreadingSet.save();
				meterreadingSet.close();
				
				
//				boolean existAssetmeter = existAssetMeter(assetnum);
//				if (existAssetmeter) {
//					double maxReading = getMaxReading(assetnum);
////					System.out.println("\n---219----MAX---"+maxReading);
//					double meterReading = Double.parseDouble(meterreading);
////					System.out.println("\n---219----CURRENT---"+meterReading);
//					if ( meterReading >= maxReading) {
//						MboSetRemote assetmeterSet = MXServer.getMXServer().getMboSet("ASSETMETER",MXServer.getMXServer().getSystemUserInfo());
//						assetmeterSet.setWhere(" assetnum = '"+assetnum+"' ");
//						assetmeterSet.reset();
//						if (!assetmeterSet.isEmpty() && assetmeterSet.count() > 0) {
//							MboRemote assetmeter = assetmeterSet.getMbo(0);
//							assetmeter.setValue("sincelastrepair", meterreading, 11L);
//							assetmeter.setValue("sincelastoverhaul", meterreading, 11L);
//							assetmeter.setValue("sincelastinspect", meterreading, 11L);
//							assetmeter.setValue("sinceinstall", meterreading, 11L);
//							assetmeter.setValue("lifetodate", meterreading, 11L);
//							assetmeter.setValue("lastreading", meterreading, 11L);
//							assetmeter.setValue("changedate", MXServer.getMXServer().getDate(), 11L);
//							assetmeter.setValue("lastreadingdate", MXServer.getMXServer().getDate(), 11L);
//						}
//						assetmeterSet.save();
//						assetmeterSet.close();
//					}
//				} else {
//					String sql1 = "insert into assetmeter (assetnum, metername, active, measureunitid, avgcalcmethod, rolldownsource, sincelastrepair, sincelastoverhaul, sincelastinspect, sinceinstall, lifetodate, changeby, changedate, siteid, orgid, lastreadingdate, lastreading, readingtype, lastreadinginspctr, assetmeterid, langcode, hasld, linearassetmeterid)"
//							+ " values "
//							+ "('"+assetnum+"','RUNHOURS','1','HOUR','ALL','ASSET','"+meterreading+"','"+meterreading+"','"+meterreading+"','"+meterreading+"','"+meterreading+"','MAXADMIN',sysdate,'CSPL','COSCO',to_date('"+meterdate+"','yyyy-MM-dd HH24:mi:ss'),'"+meterreading+"','ACTUAL','MAXADMIN',ASSETMETERSEQ.nextval,'EN','0','0')";
//					try {
//						exeSQL(sql1);
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
//				
//				int assetid = getAssetid(assetnum);
//				double delta = getDelta(assetnum,Double.parseDouble(meterreading));
//				String sql2 = "insert into meterreading (meterreadingid, assetnum, metername, readingsource, readingtype, delta, reading, rollover, measureunitid, readingdate, inspector, rolldownsource, enterby, enterdate, siteid, orgid, rolldownid, modified, assetid, didrollover)"
//						+ " values "
//						+ "(METERREADINGSEQ.nextval,'"+assetnum+"','RUNHOURS','ENTERED','ACTUAL','"+delta+"','"+meterreading+"','0','HOUR',to_date('"+meterdate+"','yyyy-MM-dd HH24:mi:ss'),'MAXADMIN','NONE','MAXADMIN',sysdate,'CSPL','COSCO',METERREADINGSEQ.nextval,'1','"+assetid+"','0')";
//				try {
//					exeSQL(sql2);
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
				
			}
			jsonResult.put("status", "0");
			jsonResult.put("msg", "Success");
			jsonResult.put("json", json);
			flag = jsonResult.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			JSONObject jsonResult = new JSONObject();
			try {
				jsonResult.put("status", "-1");
				jsonResult.put("msg", "Failure");
				jsonResult.put("json", json);
				flag = jsonResult.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return flag;
		}
		return flag;
	}
	
	public double getMaxReading(String assetnum) throws MXException,RemoteException {
		double maxReading = 0.0D;
		MboSetRemote meterreadingSet = MXServer.getMXServer().getMboSet("METERREADING",MXServer.getMXServer().getSystemUserInfo());
		meterreadingSet.setWhere(" assetnum = '"+assetnum+"' order by reading desc ");
		meterreadingSet.reset();
		if (!meterreadingSet.isEmpty() && meterreadingSet.count() > 0) {
			MboRemote meterread = meterreadingSet.getMbo(0);
			maxReading = meterread.getDouble("reading");
		}
		meterreadingSet.close();
		return maxReading;
	}
	
	public boolean existAssetMeter(String assetnum) throws MXException,RemoteException {
		boolean flag = false;
		MboSetRemote assetmeterSet = MXServer.getMXServer().getMboSet("ASSETMETER",MXServer.getMXServer().getSystemUserInfo());
		assetmeterSet.setWhere(" assetnum = '"+assetnum+"' ");
		assetmeterSet.reset();
		if (!assetmeterSet.isEmpty() && assetmeterSet.count() > 0) {
			flag = true;
		}
		assetmeterSet.close();
		return flag;
	}
	
	public int getAssetid(String assetnum) throws MXException,RemoteException {
		int assetid = 0;
		MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET",MXServer.getMXServer().getSystemUserInfo());
		assetSet.setWhere(" assetnum='"+assetnum+"' ");
		assetSet.reset();
		if (!assetSet.isEmpty() && assetSet.count() > 0) {
			MboRemote asset = assetSet.getMbo(0);
			assetid = asset.getInt("assetid");
		}
		assetSet.close();
		return assetid;
	}
	
	public double getDelta(String assetnum,double meterreading) throws MXException,RemoteException {
		double delta = 0.0D;
		MboSetRemote meterreadingSet = MXServer.getMXServer().getMboSet("METERREADING",MXServer.getMXServer().getSystemUserInfo());
		meterreadingSet.setWhere(" assetnum = '"+assetnum+"' order by reading desc ");
		meterreadingSet.reset();
		if (!meterreadingSet.isEmpty() && meterreadingSet.count() > 0) {
			MboRemote meterread = meterreadingSet.getMbo(0);
			delta = meterreading - meterread.getDouble("reading");
		}
		meterreadingSet.close();
		return delta;
	}

	
	@WebMethod
	public String UdZeeFuelService(String json) throws RemoteException, MXException {
		String flag = "";
		try {
			JSONArray ja = new JSONArray(json);
			
			String rundate = ""; // 工作时间
			String serialnum = ""; // 设备旧编码
			String zeeload = ""; // 燃油量
			String description = ""; // 备注
			String remark = ""; // 物料编码
			
			JSONObject jsonResult = new JSONObject();
			String jsonResultStr = "";
			
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				
				rundate = getString(jo, "rundate");
				serialnum = getString(jo, "serialnum");
				zeeload = getString(jo, "zeeload");
				description = getString(jo, "description");
				remark = getString(jo, "remark");

				if (rundate.equalsIgnoreCase("")) {
					jsonResult.put("status", "1");
					jsonResult.put("msg", "rundate is not allowed to be empty");
					jsonResult.put("json", json);
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (serialnum.equalsIgnoreCase("")) {
					jsonResult.put("status", "2");
					jsonResult.put("msg", "serialnum is not allowed to be empty");
					jsonResult.put("json", json);
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (zeeload.equalsIgnoreCase("")) {
					jsonResult.put("status", "3");
					jsonResult.put("msg", "zeeload is not allowed to be empty");
					jsonResult.put("json", json);
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (description.equalsIgnoreCase("")) {
					jsonResult.put("status", "4");
					jsonResult.put("msg", "description is not allowed to be empty");
					jsonResult.put("json", json);
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}				
				if (remark.equalsIgnoreCase("")) {
					jsonResult.put("status", "5");
					jsonResult.put("msg", "remark is not allowed to be empty");
					jsonResult.put("json", json);
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}

				MboSetRemote udtosworkloadSet = MXServer.getMXServer().getMboSet("UDTOSWORKLOAD",MXServer.getMXServer().getSystemUserInfo());
				udtosworkloadSet.setWhere(" 1=2 ");
				udtosworkloadSet.reset();
				MboRemote udtosworkload = udtosworkloadSet.add(11L);
				udtosworkload.setValue("rundate", rundate, 11L);
				udtosworkload.setValue("serialnum", serialnum, 11L);
				udtosworkload.setValue("zeeload", zeeload, 11L);
				udtosworkload.setValue("description", description, 11L);
				udtosworkload.setValue("remark", remark, 11L);
				udtosworkload.setValue("createdate", MXServer.getMXServer().getDate(), 11L);
				udtosworkload.setValue("udcompany", "ZEE", 11L);
				
				MboSetRemote assetSet = MXServer.getMXServer().getMboSet("ASSET",MXServer.getMXServer().getSystemUserInfo());
				assetSet.setWhere(" serialnum='"+serialnum+"' ");
				assetSet.reset();
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					MboRemote asset = assetSet.getMbo(0);
					udtosworkload.setValue("assetnum", asset.getString("assetnum"), 11L);
				} else {
					udtosworkload.setValue("assetnum", serialnum, 11L);
				}
				assetSet.close();
				
				udtosworkloadSet.save();
				udtosworkloadSet.close();
			}
			jsonResult.put("status", "0");
			jsonResult.put("msg", "Success");
			jsonResult.put("json", json);
			flag = jsonResult.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			JSONObject jsonResult = new JSONObject();
			try {
				jsonResult.put("status", "-1");
				jsonResult.put("msg", "Failure");
				jsonResult.put("json", json);
				flag = jsonResult.toString();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return flag;
		}
		return flag;
	}

	private String getString(JSONObject js, String attr) throws JSONException {
		String lsrtn = "";
		Object object = js.get(attr);
		if (object != null)
			lsrtn = object.toString();
		return lsrtn;
	}

	public void exeSQL(String sql) throws MXException, RemoteException,
			SQLException {
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = MXServer
					.getMXServer()
					.getDBManager()
					.getConnection(
							MXServer.getMXServer().getSystemUserInfo()
									.getConnectionKey());

			if (null != connection) {
				stmt = connection.createStatement();
				try {
					stmt.execute(sql);
					connection.commit();
				} catch (SQLException e) {
					connection.rollback();
					System.out.println(sql + e);
				}
			}

		} catch (RemoteException e) {
		} catch (Exception e) {
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e1) {
				}
			}
			try {
				MXServer.getMXServer()
						.getDBManager()
						.freeConnection(
								MXServer.getMXServer().getSystemUserInfo()
										.getConnectionKey());
				if (connection != null) {
					connection.close();
				}
			} catch (RemoteException e1) {
			} catch (Exception e1) {
			}
		}
	}
	
}
