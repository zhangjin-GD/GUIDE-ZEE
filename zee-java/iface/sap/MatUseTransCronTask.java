package guide.iface.sap;


import guide.app.common.CommonUtil;
import guide.iface.sap.webservice.HearBean;
import guide.iface.sap.webservice.ItemWebService;

import java.rmi.RemoteException;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class MatUseTransCronTask extends SimpleCronTask {

  
	public MatUseTransCronTask() throws RemoteException, MXException {

	}

	public void cronAction() {
		try {
			String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
			String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
			System.out.println("\nCronINVUSE-----------status" + sapStatus + "-----------debug" + sapDebug);
			if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
				String sqlWhere = getParamAsString("sqlWhere");
				if(sqlWhere == null || sqlWhere.equalsIgnoreCase(""))
					sqlWhere = "1=2";
				MboSetRemote invuseSet = MXServer.getMXServer().getMboSet("INVUSE",MXServer.getMXServer().getSystemUserInfo());
				invuseSet.setWhere(sqlWhere);
				if(!invuseSet.isEmpty() && invuseSet.count() > 0){
					MboRemote invuse = null;
					JSONObject Header = new JSONObject();
					String num = "";
					String status = "";
					for(int i=0;(invuse=invuseSet.getMbo(i))!=null;i++){
						Header = CommonUtil.getMatUseHeader(invuse);
						Header.put("item", CommonUtil.getMatUseItem(invuse));
						if (CommonUtil.getString(Header, "item").toString().length() > 2) {
							if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
								System.out.println("\n---------XML:" + Header.toString());
								CommonUtil.setSapStatus("INVUSE", "invusenum='"+invuse.getString("invusenum")+"'", "0", "DEBUG");
								continue;
							}
							num = "";
							status = "";
							try {
								HearBean result = ItemWebService.itemRequestWebService(Header.toString());
								num = result.getBELNR();
								status = result.getZHEADMSG();
								CommonUtil.ifaceLog(Header.toString(), getRunasUserInfo().getPersonId(), "INVUSE", Header.getString("ZSTOCKNO"), num, status);
								CommonUtil.setSapStatus("INVUSE", "invusenum='"+invuse.getString("invusenum")+"'", num, status);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				invuseSet.close();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} 
	}
	
}