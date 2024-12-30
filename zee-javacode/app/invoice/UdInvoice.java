package guide.app.invoice;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import psdi.app.invoice.Invoice;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;








import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @function:
 * @author:INVOICE主类
 * @date:2023-07-17 13:43:3
 * @modify:
 */
public class UdInvoice extends Invoice {
	public static Map<String,Object> map = new HashMap<>();
	public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public UdInvoice(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			String appname = getThisMboSet().getApp();
			String status = getString("status");
			if (appname == null) {
				return;
			}
			MboSetRemote invoiceline = getMboSet("INVOICELINE");
			String[] str = { "description", "udmatchstatus", "udinremark",
					"vendorinvoicenum", "invoicedate", "duedate",
					"udsaplinecost", "udsaptax", "udsaphszj", "ponum",
					"udcosttype", "udextracost" };
			if ("UDINVOICE".equalsIgnoreCase(appname)) { // 发票移交
				String personid = getUserInfo().getPersonId();
				String reporter = getString("enterby");

				invoiceline.setFlag(7L, true);
				setFieldFlag(str, 7L, true);

				if ((!reporter.equalsIgnoreCase(personid) && !personid
						.equalsIgnoreCase("MAXADMIN"))
						&& !isNew()
						&& status.equalsIgnoreCase("ENTERED")) {
					setFlag(7L, true);
				} else if ((reporter.equalsIgnoreCase(personid) || personid
						.equalsIgnoreCase("MAXADMIN"))
						&& !isNew()
						&& status.equalsIgnoreCase("ENTERED")) {
					invoiceline.setFlag(7L, false);
					setFieldFlag(str, 7L, false);
				}

				if (status.equalsIgnoreCase("ENTERED")
						|| status.equalsIgnoreCase("")) {
					invoiceline.setFlag(7L, false);
					setFieldFlag(str, 7L, false);
				}
				if (status.equalsIgnoreCase("BACK") && inwflow()) {
					invoiceline.setFlag(7L, false);
					setFieldFlag(str, 7L, false);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String personid = getUserInfo().getPersonId();
		setValue("udcreateby", personid, 2L);
	}
	
	@Override
	public void save() throws MXException, RemoteException {
		super.save();
		String udcompany = getString("udcompany");
		if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
			/*******************APPR接口推送start*******************************/
			if (isModified("status")) {
				if (getString("status").equalsIgnoreCase("APPR") || getString("status").equalsIgnoreCase("APPRCN")) {
					if (getString("udface1").equalsIgnoreCase("N") || getString("udface1").equalsIgnoreCase("n") || getString("udface1").equalsIgnoreCase("0")) {
						JSONObject jo = new JSONObject();
						try {
							jo.put("key", getString("udkey"));
							jo.put("invoicestatus", getString("status"));
							jo.put("approver", getApprMessage("personid"));
							jo.put("approvedate", getApprMessage("date"));
							jo.put("ponum", getPonum());
							jo.put("projecode", getProjectcode());
							String result = postInvoiceAppr(jo.toString());
							String result1 = StringEscapeUtils.unescapeHtml(result);
							try {
								Map<String, Object> parse = UdInvoice.parse(result1);
								String response = (String) parse.get("ApprovalResponse");
								setValue("udfaceremark1", response, 11L);
								JSONObject jsonObject = new JSONObject(response);
								String ifaceYorN = getIfaceStatus((String) jsonObject.get("status"));
								setValue("udface1", ifaceYorN, 11L);
								setValue("udfacesent1", jo.toString(), 11L);
							} catch (DocumentException e) {
								e.printStackTrace();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
			/*******************APPR接口推送end*******************************/
		}
	}
	
	public void UDPUSHAPPR() throws MXException,RemoteException, JSONException {
		if (getString("udface1").equalsIgnoreCase("Y") || getString("udface1").equalsIgnoreCase("y") || getString("udface1").equalsIgnoreCase("1")) {
			Object params[] = { "Approver Data has been transmitted, duplicate push not allowed." };
			throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}
		if (!getString("status").equalsIgnoreCase("APPR") && !getString("status").equalsIgnoreCase("APPRCN")) {
			Object params[] = { "Invoices must be approved to be pushed." };
			throw new MXApplicationException("instantmessaging", "tsdimexception",params);
		}
		JSONObject jo = new JSONObject();
		jo.put("key", getString("udkey"));
		jo.put("invoicestatus", getString("status"));
		jo.put("approver", getApprMessage("personid"));
		jo.put("approvedate", getApprMessage("date"));
		jo.put("ponum", getPonum());
		jo.put("projecode", getProjectcode());
		String result = postInvoiceAppr(jo.toString());
		String result1 = StringEscapeUtils.unescapeHtml(result);
		try {
			Map<String, Object> parse = UdInvoice.parse(result1);
			String response = (String) parse.get("ApprovalResponse");
			setValue("udfaceremark1", response, 11L);
			JSONObject jsonObject = new JSONObject(response);
			String ifaceYorN = getIfaceStatus((String) jsonObject.get("status"));
			setValue("udface1", ifaceYorN, 11L);
			setValue("udfacesent1", jo.toString(), 11L);
			this.getThisMboSet().save();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public boolean inwflow() throws RemoteException, MXException {
		boolean flag = false;
		MboSetRemote wf = this.getMboServer().getMboSet("wfassignment",
				getUserInfo());
		wf.setWhere("ASSIGNSTATUS='ACTIVE' and OWNERTABLE = '" + this.getName()
				+ "' and OWNERID = '" + this.getUniqueIDValue()
				+ "' and ASSIGNCODE = '" + this.getUserInfo().getPersonId()
				+ "'");
		if (wf != null && wf.count() > 0) {
			flag = true;
		}
		wf.close();
		return flag;
	}
	
    public static String postInvoiceAppr (String params) {
    	String result = "";
    	try {
            //服务的地址
            URL wsUrl = new URL("https://apps.cspterminals.be/approvals/soap/wsdl");

            HttpURLConnection conn = (HttpURLConnection) wsUrl.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            OutputStream os = conn.getOutputStream();

            String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <tem:ApprovalRequest>\n" +
                    "         <tem:approvalDetails>"+params+"</tem:approvalDetails>\n" +
                    "      </tem:ApprovalRequest>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>\n";
            
            os.write(soap.getBytes());

            InputStream is = conn.getInputStream();

            byte[] b = new byte[1024];
            int len = 0;
            
            while ((len = is.read(b)) != -1) {
                String ss = new String(b, 0, len, "UTF-8");
                result+=ss;
            }

            is.close();
            os.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return result;
    }
    
    //需要使用jar---dom4j-2.1.4.jar   commons-lang-2.6.jar
    public static Map<String,Object> parse(String soap) throws DocumentException{
        Document doc = DocumentHelper.parseText(soap);
        Element root = doc.getRootElement();
        iteratorE(root);
        return map;
    }
    
    public static void iteratorE(Element root){
        if(root.elements()!=null){
            List<Element>list = root.elements();
            for(Element e:list){
                if(e.elements().size()>0){
                    iteratorE(e);
                }
                if(e.elements().size()==0){
                    map.put(e.getName(), e.getTextTrim());
                }
            }
        }
    }
    
    public String getApprMessage(String param) throws MXException,RemoteException {
    	String result = "";
    	if (param.equalsIgnoreCase("personid")) {
			MboSetRemote invoicestatusSet = MXServer.getMXServer().getMboSet("INVOICESTATUS", MXServer.getMXServer().getSystemUserInfo());
			invoicestatusSet.setWhere(" invoicenum='"+getString("invoicenum")+"' order by changedate asc ");
			invoicestatusSet.reset();
			if (!invoicestatusSet.isEmpty() && invoicestatusSet.count() > 0) {
				result = invoicestatusSet.getMbo(0).getString("changeby");
			}
			invoicestatusSet.close();
		} else if (param.equalsIgnoreCase("date")) {
			MboSetRemote invoicestatusSet = MXServer.getMXServer().getMboSet("INVOICESTATUS", MXServer.getMXServer().getSystemUserInfo());
			invoicestatusSet.setWhere(" invoicenum='"+getString("invoicenum")+"' order by changedate asc ");
			invoicestatusSet.reset();
			if (!invoicestatusSet.isEmpty() && invoicestatusSet.count() > 0) {
				result = ldateToString(invoicestatusSet.getMbo(0).getDate("changedate"));
			}
			invoicestatusSet.close();
		}
    	return result;
    }
    
	public String getPonum() throws MXException,RemoteException {
		String ponum = "";
		if (getString("ponum")!=null && !getString("ponum").equalsIgnoreCase("")) {
			ponum = getString("ponum");
		} else {
			if (!getMboSet("INVOICELINE").isEmpty() && getMboSet("INVOICELINE").count() > 0) {
				ponum = getMboSet("INVOICELINE").getMbo(0).getString("ponum");
			}
		}
		return ponum;
	}
	
	public String getProjectcode() throws MXException,RemoteException {
		String projectcode = "";
		if (getString("ponum")!=null && !getString("ponum").equalsIgnoreCase("")) {
			projectcode = getString("INVC_PO.udprojectnum");
		} else {
			if (!getMboSet("INVOICELINE").isEmpty() && getMboSet("INVOICELINE").count() > 0) {
				projectcode = getMboSet("INVOICELINE").getMbo(0).getString("PO.udprojectnum");
			}
		}
		if (projectcode.equalsIgnoreCase("")) {
			projectcode = "NO";
		}
		return projectcode;
	}
	
	public String getIfaceStatus(String status) throws MXException,RemoteException {
		String value = "";
		if (status.equalsIgnoreCase("0")) {
			value = "Y";
		} else {
			value = "N";
		}
		return value;
	}
	
	public static String ldateToString(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
			String time = sdf.format(date);
			return time;
		} else {
			return "";
		}
	}
	
}
