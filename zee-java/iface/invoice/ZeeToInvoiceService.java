package guide.iface.invoice;

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
 * @function:ZEE>-INVOICE中间表
 * @author:zj
 * @date:2023-06-29 09:10:47
 * @modify:
 */
public class ZeeToInvoiceService extends AppService implements ZeeToInvoiceServiceRemote {

	public ZeeToInvoiceService() throws RemoteException {
		super();
	}

	public ZeeToInvoiceService(MXServer mxServer) throws RemoteException {
		super(mxServer);
	}

	// http://localhost:7001/meaweb/wsdl/UDBPC.wsdl -本机
	// http://10.18.11.156:9080/meaweb/wsdl/UDINVOICEINIT.wsdl -156测试
	@WebMethod
	public String UdZeeToInvoiceService(String json) throws RemoteException, MXException {
		String flag = "";
		try {
			JSONArray ja = new JSONArray(json);
			
			String key = "";// 主键
			String grossamount = "";// 总含税金额
			String vatamount = "";// 总税额
			String netamount = "";// 总不含税金额
			String vatnumber = "";// 税代码
			String invoicedescription = "";// 发票描述
			String cocontractor  = "";// 是否共同承包商
			String supplier = "";// 供应商名称
			String invoicenum = "";// 发票号码
			String invoicedate = "";// 发票日期
			String ponum = "";// 订单号
			String filename = "";// 文件名
			String filepath = "";// 文件路径
			
			JSONObject jsonResult = new JSONObject();
			String jsonResultStr = "";
			
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				
				key = getString(jo, "key");
				grossamount = getString(jo, "grossamount");
				vatamount = getString(jo, "vatamount");
				netamount = getString(jo, "netamount");
				vatnumber = getString(jo, "vatnumber");
				invoicedescription = getString(jo, "invoicedescription");
				cocontractor = getString(jo, "cocontractor");
				supplier = getString(jo, "supplier");
				invoicenum = getString(jo, "invoicenum");
				invoicedate = getString(jo, "invoicedate");
				ponum = getString(jo, "ponum");
				filename = getString(jo, "filename");
				filepath = getString(jo, "filepath");

				if (key.equalsIgnoreCase("")) {
					jsonResult.put("status", "1");
					jsonResult.put("msg", "key is not allowed to be empty");
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (grossamount.equalsIgnoreCase("")) {
					jsonResult.put("status", "2");
					jsonResult.put("msg", "grossamount is not allowed to be empty");
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (vatamount.equalsIgnoreCase("")) {
					jsonResult.put("status", "3");
					jsonResult.put("msg", "vatamount is not allowed to be empty");
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (netamount.equalsIgnoreCase("")) {
					jsonResult.put("status", "4");
					jsonResult.put("msg", "netamount is not allowed to be empty");
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (vatnumber.equalsIgnoreCase("")) {
					jsonResult.put("status", "5");
					jsonResult.put("msg", "vatnumber is not allowed to be empty");
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (filename.equalsIgnoreCase("")) {
					jsonResult.put("status", "6");
					jsonResult.put("msg", "filename is not allowed to be empty");
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				if (filepath.equalsIgnoreCase("")) {
					jsonResult.put("status", "7");
					jsonResult.put("msg", "filepath is not allowed to be empty");
					jsonResultStr = jsonResult.toString();
					return jsonResultStr;
				}
				MboSetRemote udinvoiceinitSet = MXServer.getMXServer().getMboSet("UDINVOICEINIT",MXServer.getMXServer().getSystemUserInfo());
				udinvoiceinitSet.setWhere(" 1=2 ");
				udinvoiceinitSet.reset();
				MboRemote udinvoiceinit = udinvoiceinitSet.add(11L);
				udinvoiceinit.setValue("key", key, 11L);
				udinvoiceinit.setValue("grossamount", grossamount, 11L);
				udinvoiceinit.setValue("vatamount", vatamount, 11L);
				udinvoiceinit.setValue("netamount", netamount, 11L);
				udinvoiceinit.setValue("vatnumber", vatnumber, 11L);
				udinvoiceinit.setValue("invoicedescription", invoicedescription, 11L);
				udinvoiceinit.setValue("cocontractor", cocontractor, 11L);
				udinvoiceinit.setValue("supplier", supplier, 11L);
				udinvoiceinit.setValue("invoicenum", invoicenum, 11L);
				udinvoiceinit.setValue("invoicedate", invoicedate, 11L);
				udinvoiceinit.setValue("ponum", ponum, 11L);
				udinvoiceinit.setValue("status", "Draft", 11L);
				udinvoiceinit.setValue("filename", filename, 11L);
				udinvoiceinit.setValue("filepath", filepath, 11L);
				udinvoiceinitSet.save();
				udinvoiceinitSet.close();
			}
			jsonResult.put("status", "0");
			jsonResult.put("msg", "Success");
			flag = jsonResult.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			JSONObject jsonResult = new JSONObject();
			try {
				jsonResult.put("status", "-1");
				jsonResult.put("msg", "Failure");
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

}
