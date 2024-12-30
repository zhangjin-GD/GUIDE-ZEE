package guide.iface.tma;

import java.rmi.RemoteException;

import javax.jws.WebMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.AppService;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * @function:TMA>-EAM
 * @author:zj
 * @date:22023-02-08 10:23:52
 * @modify:
 */
public class TmaToEamService extends AppService implements TmaToEamServiceRemote {

	public TmaToEamService() throws RemoteException {
		super();
	}

	public TmaToEamService(MXServer mxServer) throws RemoteException {
		super(mxServer);
	}

	// http://localhost:7001/meaweb/wsdl/UDTMA.wsdl -本机
	// http://10.18.11.156:9080/meaweb/wsdl/UDTMA.wsdl -156测试
	@WebMethod
	public String UdTmaService(String json) throws RemoteException, MXException {
		String flag = "";
		try {
			JSONArray ja = new JSONArray(json);
			String trnid = "";// 主键
			String insdate = "";// 创建日期
			String trntype = "";// 事务类型
			String measuredate = "";// 测量日期
			String equipcode = "";// 设备代码 3 assetnum
			String measuretype = "";// 测量类型 1 READINGTYPE
			String measureunit = "";// 测量单位 2 PMTYPE
			String measurevalue = "";// 测量值 5 VALUE

			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				trnid = getString(jo, "trnid");
				insdate = getString(jo, "insdate");
				trntype = getString(jo, "trntype");
				measuredate = getString(jo, "measuredate");
				equipcode = getString(jo, "equipcode");
				measuretype = getString(jo, "measuretype");
				measureunit = getString(jo, "measureunit");
				measurevalue = getString(jo, "measurevalue");

				if (trnid.equalsIgnoreCase("")) {
					return "trnid is not allowed to be empty!";
				}
				if (insdate.equalsIgnoreCase("")) {
					return "insdate is not allowed to be empty!";
				}
				if (trntype.equalsIgnoreCase("")) {
					return "trntype is not allowed to be empty!";
				}
				if (measuredate.equalsIgnoreCase("")) {
					return "measuredate is not allowed to be empty!";
				}
				if (equipcode.equalsIgnoreCase("")) {
					return "equipcode is not allowed to be empty!";
				}
				if (measuretype.equalsIgnoreCase("")) {
					return "measuretype is not allowed to be empty!";
				}
				if (measureunit.equalsIgnoreCase("")) {
					return "measureunit is not allowed to be empty!";
				}
				if (measurevalue.equalsIgnoreCase("")) {
					return "measurevalue is not allowed to be empty!";
				}
				if (!trntype.equalsIgnoreCase("") && trntype.equalsIgnoreCase("I")) {
					MboSetRemote udassetMeterYZSet = MXServer.getMXServer().getMboSet("UDASSETMETER",
							MXServer.getMXServer().getSystemUserInfo());
					udassetMeterYZSet.setWhere(" trnid='" + trnid + "' ");
					udassetMeterYZSet.reset();
					if (!udassetMeterYZSet.isEmpty() && udassetMeterYZSet.count() > 0) {
						flag = "The trnid already exists and cannot be repeated add!";
						return flag;
					}
					udassetMeterYZSet.close();
					MboSetRemote udassetMeterSet = MXServer.getMXServer().getMboSet("UDASSETMETER",
							MXServer.getMXServer().getSystemUserInfo());
					MboRemote udTmaset = udassetMeterSet.add(11L);
					udTmaset.setValue("trnid", trnid, 11L);
					udTmaset.setValue("insdate", insdate, 11L);
					udTmaset.setValue("trntype", trntype, 11L);
					udTmaset.setValue("measuredate", measuredate, 11L);
					udTmaset.setValue("assetnum", equipcode, 11L);
					udTmaset.setValue("readingtype", measuretype, 11L);
					udTmaset.setValue("pmtype", measureunit, 11L);
					udTmaset.setValue("value", measurevalue, 11L);
					udTmaset.setValue("createtime", MXServer.getMXServer().getDate(), 11L);
					udassetMeterSet.save();
					udassetMeterSet.close();
				} else if (!trntype.equalsIgnoreCase("") && trntype.equalsIgnoreCase("U")) {
					MboSetRemote updateMeterSet = MXServer.getMXServer().getMboSet("UDASSETMETER",
							MXServer.getMXServer().getSystemUserInfo());
					updateMeterSet.setWhere("trnid='" + trnid + "'");
					updateMeterSet.reset();
					if (!updateMeterSet.isEmpty() && updateMeterSet.count() > 0) {
						MboRemote updateMeter = updateMeterSet.getMbo(0);
						updateMeter.setValue("insdate", insdate, 11L);
						updateMeter.setValue("trntype", trntype, 11L);
						updateMeter.setValue("measuredate", measuredate, 11L);
						updateMeter.setValue("assetnum", equipcode, 11L);
						updateMeter.setValue("readingtype", measuretype, 11L);
						updateMeter.setValue("pmtype", measureunit, 11L);
						updateMeter.setValue("value", measurevalue, 11L);
						updateMeter.setValue("createtime", MXServer.getMXServer().getDate(), 11L);
						updateMeterSet.save();
						updateMeterSet.close();
					} else {
						return "No trnid found:" + trnid + ", cannot be updated, please check!";
					}
					updateMeterSet.close();
				}
			}
			flag = "success";
		} catch (JSONException e) {
			e.printStackTrace();
			flag = "failure";
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

}
