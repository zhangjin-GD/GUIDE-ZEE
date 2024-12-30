package guide.iface.invoice;

import java.rmi.RemoteException;

import javax.jws.WebMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.python.antlr.PythonParser.else_clause_return;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.AppService;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * @function:EAM返回需添加水印的发票
 * @author:zj
 * @date:2023-06-30 11:19:38
 * @modify:
 */
public class ReturnAPPRInvoiceService extends AppService implements ReturnAPPRInvoiceServiceRemote {

	public ReturnAPPRInvoiceService() throws RemoteException {
		super();
	}

	public ReturnAPPRInvoiceService(MXServer mxServer) throws RemoteException {
		super(mxServer);
	}

	// http://10.18.11.156:9080/meaweb/wsdl/UDINVOICEDS.wsdl -156测试
	@WebMethod
	public String UdReturnAPPRInvoiceService(String datakey,String startdate,String enddate) throws RemoteException, MXException {
		String jsonResultStr = "";
		try {
			JSONObject jsonResult = new JSONObject();
			
			//校验参数
			if ((datakey ==null || datakey.equalsIgnoreCase("")) && (startdate ==null || startdate.equalsIgnoreCase("")) && (enddate ==null || enddate.equalsIgnoreCase(""))) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{\"status\":\"1\",\"msg\":\"Not allowing all parameters to be empty\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if ((datakey !=null && !datakey.equalsIgnoreCase("")) && (startdate !=null && !startdate.equalsIgnoreCase("")) && (enddate !=null && !enddate.equalsIgnoreCase(""))) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{\"status\":\"2\",\"msg\":\"Please delete the value of the datakey parameter\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if ((datakey !=null && !datakey.equalsIgnoreCase("")) && (startdate !=null && !startdate.equalsIgnoreCase("")) && (enddate ==null || enddate.equalsIgnoreCase(""))) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{\"status\":\"3\",\"msg\":\"Please enter the enddate parameter value and delete the datakey parameter value\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if ((datakey !=null && !datakey.equalsIgnoreCase("")) && (enddate !=null && !enddate.equalsIgnoreCase("")) && (startdate ==null || startdate.equalsIgnoreCase(""))) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{\"status\":\"4\",\"msg\":\"Please enter the startdate parameter value and delete the datakey parameter value\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if ((datakey ==null || datakey.equalsIgnoreCase("")) && (startdate !=null && !startdate.equalsIgnoreCase("")) && (enddate ==null || enddate.equalsIgnoreCase(""))) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{\"status\":\"5\",\"msg\":\"Please enter the enddate parameter value\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				return jsonResultStr;
			} else if ((datakey ==null || datakey.equalsIgnoreCase("")) && (enddate !=null && !enddate.equalsIgnoreCase("")) && (startdate ==null || startdate.equalsIgnoreCase(""))) {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{\"status\":\"6\",\"msg\":\"Please enter the startdate parameter value\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				return jsonResultStr;
			} 
			
			//通过校验,接口返回
			if ((datakey !=null && !datakey.equalsIgnoreCase("")) && (startdate ==null || startdate.equalsIgnoreCase("")) && (enddate ==null || enddate.equalsIgnoreCase(""))) {
				MboSetRemote invoiceSet = MXServer.getMXServer().getMboSet("INVOICE",MXServer.getMXServer().getSystemUserInfo());
				invoiceSet.setWhere(" status='APPR' and invoicenum='"+datakey+"' ");
				invoiceSet.reset();
				if (!invoiceSet.isEmpty() && invoiceSet.count() > 0) {
					for (int i = 0; i < invoiceSet.count(); i++) {
						MboRemote invoice = invoiceSet.getMbo(i);
						String key = invoice.getString("invoicenum");
						String invoicenum = invoice.getString("vendorinvoicenum");
						String invoicedate = invoice.getString("invoicedate");
						String invoicestatus = invoice.getString("status");
						String approver = invoice.getString("changeby");
						String approvedate = invoice.getString("statusdate");
						String ponum = invoice.getString("ponum");
						String projecode = invoice.getString("INVC_PO.udprojectnum");

						jsonResult.put("key", key);
						jsonResult.put("invoicenum", invoicenum);
						jsonResult.put("invoicedate", invoicedate);
						jsonResult.put("invoicestatus", invoicestatus);
						jsonResult.put("approver", approver);
						jsonResult.put("approvedate", approvedate);
						jsonResult.put("ponum", ponum);
						jsonResult.put("projecode", projecode);
						jsonResultStr = jsonResultStr + jsonResult.toString();
						if (i!=invoiceSet.count()-1) {
							jsonResultStr = jsonResultStr + ",";
						}
					}
					jsonResultStr = "{\"status\":\"0\",\"msg\":\"Success\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
					return jsonResultStr;
				} else {
					jsonResult.put("datas", "");
					jsonResultStr = jsonResult.toString();
					jsonResultStr = "{\"status\":\"7\",\"msg\":\"Without this invoicenum\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				}	
			} else if ((datakey ==null || datakey.equalsIgnoreCase("")) && (startdate !=null && !startdate.equalsIgnoreCase("")) && (enddate !=null && !enddate.equalsIgnoreCase(""))) {
				MboSetRemote invoiceSet = MXServer.getMXServer().getMboSet("INVOICE",MXServer.getMXServer().getSystemUserInfo());
				invoiceSet.setWhere(" status='APPR' and statusdate>=to_date('"+startdate+"','yyyy-MM-dd HH24:mi:ss') and statusdate<=to_date('"+enddate+"','yyyy-MM-dd HH24:mi:ss') ");
				invoiceSet.reset();
				if (!invoiceSet.isEmpty() && invoiceSet.count() > 0) {
					for (int i = 0; i < invoiceSet.count(); i++) {
						MboRemote invoice = invoiceSet.getMbo(i);
						String key = invoice.getString("invoicenum");
						String invoicenum = invoice.getString("vendorinvoicenum");
						String invoicedate = invoice.getString("invoicedate");
						String invoicestatus = invoice.getString("status");
						String approver = invoice.getString("changeby");
						String approvedate = invoice.getString("statusdate");
						String ponum = invoice.getString("ponum");
						String projecode = invoice.getString("INVC_PO.udprojectnum");
						jsonResult.put("key", key);
						jsonResult.put("invoicenum", invoicenum);
						jsonResult.put("invoicedate", invoicedate);
						jsonResult.put("invoicestatus", invoicestatus);
						jsonResult.put("approver", approver);
						jsonResult.put("approvedate", approvedate);
						jsonResult.put("ponum", ponum);
						jsonResult.put("projecode", projecode);
						jsonResultStr = jsonResultStr + jsonResult.toString();
						if (i!=invoiceSet.count()-1) {
							jsonResultStr = jsonResultStr + ",";
						}
					}
					jsonResultStr = "{\"status\":\"0\",\"msg\":\"Success\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
					return jsonResultStr;
				} else {
					jsonResult.put("datas", "");
					jsonResultStr = jsonResult.toString();
					jsonResultStr = "{\"status\":\"8\",\"msg\":\"There are no approved invoices during this time period\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				}	
			}
		} catch (JSONException e) {
			e.printStackTrace();
			JSONObject jsonResult = new JSONObject();
			try {
				jsonResult.put("datas", "");
				jsonResultStr = jsonResult.toString();
				jsonResultStr = "{\"status\":\"-1\",\"msg\":\"Failure\",\"dataKey\":\""+datakey+"\",\"startdate\":\""+startdate+"\",\"enddate\":\""+enddate+"\",\"datas\":["+jsonResultStr+"]}";
				return jsonResultStr;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return jsonResultStr;
	}

	private String getString(JSONObject js, String attr) throws JSONException {
		String lsrtn = "";
		Object object = js.get(attr);
		if (object != null)
			lsrtn = object.toString();
		return lsrtn;
	}

}
