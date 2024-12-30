package guide.iface.datawarehouse;

import java.rmi.RemoteException;

import javax.jws.WebMethod;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.AppService;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 *@function:ZEE-数据仓库接口
 *@date:2023-08-21 13:49:05
 *@modify:
 */
public class ReturnDataHouseService extends AppService implements ReturnDataHouseServiceRemote {
	
	public ReturnDataHouseService() throws RemoteException {
		super();
	}

	public ReturnDataHouseService(MXServer mxServer) throws RemoteException {
		super(mxServer);
	}
	
	// http://10.18.11.156:9080/meaweb/wsdl/UDDATAHOUSE.wsdl -156测试
	@WebMethod
	public String UdReturnDataHouseService(String datakey) throws RemoteException, MXException {
		String jsonResultStr = "";
		try {
			JSONObject jsonResult = new JSONObject();
			
			//校验参数
			if (datakey ==null || datakey.equalsIgnoreCase("")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'1','msg':'Not allowing datakey parameters to be empty','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
			} else if (!datakey.equalsIgnoreCase("Agreement") && !datakey.equalsIgnoreCase("Agreement_SP") && !datakey.equalsIgnoreCase("Counters") && !datakey.equalsIgnoreCase("Downtime") && !datakey.equalsIgnoreCase("Invoice_line") && !datakey.equalsIgnoreCase("Invoices") && !datakey.equalsIgnoreCase("Maint_obj") && !datakey.equalsIgnoreCase("Notes") && !datakey.equalsIgnoreCase("Spareparts") && !datakey.equalsIgnoreCase("projects") && !datakey.equalsIgnoreCase("PO") && !datakey.equalsIgnoreCase("Po_line") && !datakey.equalsIgnoreCase("Main_supp") && !datakey.equalsIgnoreCase("sp_supp_comb") && !datakey.equalsIgnoreCase("work_supp") && !datakey.equalsIgnoreCase("supplier") && !datakey.equalsIgnoreCase("Transactions") && !datakey.equalsIgnoreCase("Pm_plan") && !datakey.equalsIgnoreCase("plan_work") && !datakey.equalsIgnoreCase("Stock_control") && !datakey.equalsIgnoreCase("Stock_contr_loc") && !datakey.equalsIgnoreCase("work_order")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'2','msg':'Without this datakey,please enter the correct datakey:Agreement||Agreement_SP||Counters||Downtime||Invoice_line||Invoices||Maint_obj||Notes||Spareparts||projects||PO||Po_line||Main_supp||sp_supp_comb||work_supp||supplier||Transactions||Pm_plan||plan_work||Stock_control||Stock_contr_loc||work_order','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
			}
			
			//通过校验,接口返回
			if (datakey.equalsIgnoreCase("Agreement")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Agreement_SP")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Counters")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Downtime")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Invoice_line")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Invoices")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Maint_obj")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Notes")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Spareparts")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("projects")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("PO")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Po_line")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Main_supp")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("sp_supp_comb")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("work_supp")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("supplier")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Transactions")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Pm_plan")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("plan_work")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Stock_control")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("Stock_contr_loc")) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'3','msg':'This datakey is currently not supported. Please enter datakey: work_order for testing','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if (datakey.equalsIgnoreCase("work_order")) {
				MboSetRemote woSet = MXServer.getMXServer().getMboSet("WORKORDER",MXServer.getMXServer().getSystemUserInfo());
				woSet.setWhere(" udcompany='ZEE' ");
				woSet.reset();
				if (!woSet.isEmpty() && woSet.count() > 0) {
					for (int i = 0; i < woSet.count(); i++) {
						MboRemote wo = woSet.getMbo(i);
						String WO_key = wo.getString("wonum");
						String MO_key = wo.getString("wonum");
						String Work_supplier_key = wo.getString("description");
						String Counter_key = getCounterKey(wo);
						String Agreement_key = getAgreementKey(wo);
						String SP_key = getSPKey(wo);

						jsonResult.put("WO_key", WO_key);
						jsonResult.put("MO_key", MO_key);
						jsonResult.put("Work_supplier_key", Work_supplier_key);
						jsonResult.put("Counter_key", Counter_key);
						jsonResult.put("Agreement_key", Agreement_key);
						jsonResult.put("SP_key", SP_key);
						jsonResultStr = jsonResultStr + jsonResult.toString();
						if (i!=woSet.count()-1) {
							jsonResultStr = jsonResultStr + ",";
						}
					}
					jsonResultStr = "{'status':'0','msg':'Success','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
					return jsonResultStr;
				}
				woSet.close();
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject jsonResult = new JSONObject();
			try {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{'status':'-1','msg':'Failure','dataKey':'"+datakey+"','datas':["+jsonResultStr+"]}";
				return jsonResultStr;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return jsonResultStr;
	}
	
	public String getCounterKey(MboRemote wo) throws MXException,RemoteException {
		String counterKey = "";
		String udgpmnum = wo.getString("udgpmnum");
		if (udgpmnum!=null && !udgpmnum.equalsIgnoreCase("")) {
			MboSetRemote udgpmSet = wo.getMboSet("UDGPM");
			if (!udgpmSet.isEmpty() && udgpmSet.count() > 0) {
				MboRemote udgpm = udgpmSet.getMbo(0);
				MboSetRemote udgpmmeterSet = udgpm.getMboSet("UDGPMMETER");
				if (!udgpmmeterSet.isEmpty() && udgpmmeterSet.count() > 0) {
					MboRemote udgpmmeter = udgpmmeterSet.getMbo(0);
					counterKey = udgpmmeter.getString("mpointnum");
				}
			}
		}
		return counterKey;
	}
	
	public String getAgreementKey(MboRemote wo) throws MXException,RemoteException {
		String agreementKey = "";
		
		return agreementKey;
	}
	
	public String getSPKey(MboRemote wo) throws MXException,RemoteException {
		String spKey = "";
		
		return spKey;
	}
	
}
