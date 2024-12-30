package guide.iface.bpc;

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
 * @function:BPC>-EAM
 * @author:zj
 * @date:2023-02-08 10:20:42
 * @modify:
 */
public class BpcToEamService extends AppService implements BpcToEamServiceRemote {

	public BpcToEamService() throws RemoteException {
		super();
	}

	public BpcToEamService(MXServer mxServer) throws RemoteException {
		super(mxServer);
	}

	// http://localhost:7001/meaweb/wsdl/UDBPC.wsdl -本机
	// http://10.18.11.156:9080/meaweb/wsdl/UDBPC.wsdl -156测试
	@WebMethod
	public String UdBpcService(String json) throws RemoteException, MXException {
		String flag = "";
		try {
			JSONArray ja = new JSONArray(json);
			String trnid = "";// 主键
			String insdate = "";// 创建日期 createtime
			String trntype = "";// 事务类型
			String budgetyear = "";// 预算年度 year
			String department = "";// 部门 udgkdept
			String budgetcode = "";// 预算代码 BUDGETNUM
			String budgetcodedescr = "";// 预算代码描述 description
			String budgetcategory = "";// 预算类别
			String budgetamount = "";// 预算金额 budgetamount
			String inactiveled = "";// 不活动 status

			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				trnid = getString(jo, "trnid");
				insdate = getString(jo, "insdate");
				trntype = getString(jo, "trntype");
				budgetyear = getString(jo, "budgetyear");
				department = getString(jo, "department");
				budgetcode = getString(jo, "budgetcode");
				budgetcodedescr = getString(jo, "budgetcodedescr");
				budgetcategory = getString(jo, "budgetcategory");
				budgetamount = getString(jo, "budgetamount");
				inactiveled = getString(jo, "inactiveled");

				if (budgetcode.equalsIgnoreCase("")) {
					return "budgetcode is not allowed to be empty!";
				}
				if (budgetcodedescr.equalsIgnoreCase("")) {
					return "budgetcodedescr is not allowed to be empty!";
				}
				if (budgetyear.equalsIgnoreCase("")) {
					return "budgetyear is not allowed to be empty!";
				}
				if (budgetamount.equalsIgnoreCase("")) {
					return "budgetamount is not allowed to be empty!";
				}
				if (inactiveled.equalsIgnoreCase("")) {
					return "inactiveled is not allowed to be empty!";
				}
				if (!trntype.equalsIgnoreCase("") && trntype.equalsIgnoreCase("I")) {
					MboSetRemote udBpcYZSet = MXServer.getMXServer().getMboSet("UDBUDGET",
							MXServer.getMXServer().getSystemUserInfo());
					udBpcYZSet.setWhere(" budgetnum='" + budgetcode + "' ");
					udBpcYZSet.reset();
					if (!udBpcYZSet.isEmpty() && udBpcYZSet.count() > 0) {
						flag = "The budgetcode already exists and cannot be repeated add!";
						return flag;
					}
					udBpcYZSet.close();
					MboSetRemote udBpcSet = MXServer.getMXServer().getMboSet("UDBUDGET",
							MXServer.getMXServer().getSystemUserInfo());
					MboRemote udbpcset = udBpcSet.add(11L);
					udbpcset.setValue("trnid", trnid, 11L);
					udbpcset.setValue("insdate", insdate, 11L);
					udbpcset.setValue("trntype", trntype, 11L);
					udbpcset.setValue("year", budgetyear, 11L);
					udbpcset.setValue("udgkdept", getDepartment(department), 11L);
					udbpcset.setValue("budgetnum", budgetcode, 11L);
					udbpcset.setValue("description", budgetcodedescr, 11L);
					udbpcset.setValue("budgetcategory", budgetcategory, 11L);
					udbpcset.setValue("budgetcost", budgetamount, 11L);
					if (!inactiveled.equalsIgnoreCase("") && inactiveled.equalsIgnoreCase("0")) {
						udbpcset.setValue("status", "WAPPR", 11L);
					} else {
						udbpcset.setValue("status", "APPR", 11L);
					}
					udbpcset.setValue("udcompany", "GR02PCT", 11L);
					udBpcSet.save();
					udBpcSet.close();
				} else if (!trntype.equalsIgnoreCase("") && trntype.equalsIgnoreCase("U")) {
					MboSetRemote updateUdbudgetSet = MXServer.getMXServer().getMboSet("UDBUDGET",
							MXServer.getMXServer().getSystemUserInfo());
					updateUdbudgetSet.setWhere("budgetnum='" + budgetcode + "'");
					updateUdbudgetSet.reset();
					if (!updateUdbudgetSet.isEmpty() && updateUdbudgetSet.count() > 0) {
						MboRemote updateUdbudget = updateUdbudgetSet.getMbo(0);
						updateUdbudget.setValue("trnid", trnid, 11L);
						updateUdbudget.setValue("insdate", insdate, 11L);
						updateUdbudget.setValue("trntype", trntype, 11L);
						updateUdbudget.setValue("year", budgetyear, 11L);
						updateUdbudget.setValue("udgkdept", getDepartment(department), 11L);
						updateUdbudget.setValue("description", budgetcodedescr, 11L);
						updateUdbudget.setValue("budgetcategory", budgetcategory, 11L);
						updateUdbudget.setValue("budgetcost", budgetamount, 11L);
						if (!inactiveled.equalsIgnoreCase("") && inactiveled.equalsIgnoreCase("0")) {
							updateUdbudget.setValue("status", "WAPPR", 11L);
						} else {
							updateUdbudget.setValue("status", "APPR", 11L);
						}
						updateUdbudgetSet.save();
						updateUdbudgetSet.close();
					} else {
						return "No budget code:" + budgetcode + ", cannot be updated, please check!";
					}
					updateUdbudgetSet.close();
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

	private String getDepartment(String department) throws MXException, RemoteException {
		String dept = "";
		if (!department.equalsIgnoreCase("")) {
			if (department.equalsIgnoreCase("ADMIN")) {
				dept = "GR02110001";
			} else if (department.equalsIgnoreCase("FIN")) {
				dept = "GR02110003";
			} else if (department.equalsIgnoreCase("HR")) {
				dept = "GR02110007";
			} else if (department.equalsIgnoreCase("PRC")) {
				dept = "GR02110012";
			} else if (department.equalsIgnoreCase("OPS")) {
				dept = "GR02160017";
			} else if (department.equalsIgnoreCase("ENG")) {
				dept = "GR02120002";
			} else if (department.equalsIgnoreCase("FZ")) {
				dept = "GR02160012";
			} else if (department.equalsIgnoreCase("ISPS")) {
				dept = "";
			} else if (department.equalsIgnoreCase("INS")) {
				dept = "GR02120009";
			} else if (department.contains("IT")) {
				dept = "GR02120010";
			} else if (department.equalsIgnoreCase("SM")) {
				dept = "GR02120012";
			} else if (department.equalsIgnoreCase("SM")) {
				dept = "GR02120012";
			} else if (department.contains("FAC")) {
				dept = "GR02120013";
			} else {
				dept = "";
			}
		}
		return dept;
	}

}
