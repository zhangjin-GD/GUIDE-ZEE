package guide.iface.webservice;


import java.rmi.RemoteException;

import javax.jws.WebMethod;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.AppService;
import psdi.server.MXServer;
import psdi.util.MXException;


public class CompanyService extends AppService implements CompanyServiceRemote {

	public CompanyService() throws RemoteException {
		super();
	}

	public CompanyService(MXServer mxServer) throws RemoteException {
		super(mxServer);
	}
	
//	http://123.60.22.73:7002/meaweb/wsdl/EAMCOMPANY.wsdl
	@WebMethod
	public String WebServ(String company, String langCode, String data) throws RemoteException, MXException {
		String returnMsg = "failed";
		company = company.toUpperCase();
		langCode = langCode.toUpperCase();
		System.out.println("\n-------------------"+company+" for "+langCode+":"+data);
		try {
			String checkMsg = getCheckMsg(company, langCode, data);
			if(!checkMsg.equalsIgnoreCase(""))
				return checkMsg;
			
			JSONObject jsonData = new JSONObject(data);
			UserInfo userInfo = mxServer.getSystemUserInfo();
			userInfo.setLangCode(langCode);
			
			MboSetRemote companiesSet = mxServer.getMboSet("COMPANIES", userInfo);
			companiesSet.setWhere("company = '"+company+"'");
			MboRemote companies = null;
			if(!companiesSet.isEmpty() && companiesSet.count()>0){
				companies = companiesSet.getMbo(0);
			}else {
				companies = companiesSet.add();
				companies.setValue("company", company);
			}
			companies.setValue("name", getString(jsonData, "name"));
			companiesSet.save();
			companiesSet.close();
		} catch (JSONException e) {
			e.printStackTrace();
			return e.toString();
		}
		returnMsg = "success";
		return returnMsg;
	}
	
	private String getCheckMsg(String company, String langCode, String data) throws RemoteException, MXException {
		String returnMsg = "";
		if (company == null || company.equalsIgnoreCase("")) {
			returnMsg += mxServer.getMessage("guide", "1007", langCode);
		}
		if (langCode == null || langCode.equalsIgnoreCase("")) {
			returnMsg += mxServer.getMessage("guide", "1021", "ZH")+mxServer.getMessage("guide", "1021", "EN");
		}
		if (data == null || data.equalsIgnoreCase("")) {
			returnMsg += mxServer.getMessage("guide", "1008", langCode);
		}
		return returnMsg;
	}

	private String getString(JSONObject js, String attr) throws JSONException {
		String lsrtn = "";
		Object object = js.get(attr);
		if (object != null)
			lsrtn = object.toString();
		return lsrtn;
	}

}
