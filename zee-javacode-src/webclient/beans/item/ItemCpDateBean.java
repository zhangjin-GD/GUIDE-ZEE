package guide.webclient.beans.item;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class ItemCpDateBean extends DataBean{

public int addrow() throws MXException {
		
		try {
			MboRemote mbo = app.getAppBean().getMbo();
			String company = CommonUtil.getValue("PERSON", "personid='"+mbo.getUserInfo().getPersonId()+"'", "udcompany");
			MboSetRemote itemcpSet = mbo.getMboSet("UDITEMCP");
			if(itemcpSet != null && !itemcpSet.isEmpty()) {
				MboRemote itemcp = null;
				for (int i = 0; (itemcp = itemcpSet.getMbo(i)) != null; i++) {
					if(itemcp.getString("udcompany") != null && itemcp.getString("udcompany").equalsIgnoreCase(company)){
						throw new MXApplicationException("guide", "1062");
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		super.addrow();
		return 1;
		
	}
	
	public int toggledeleterow() throws MXException {
		
		try {
			MboRemote mbo = app.getAppBean().getMbo();
			String company = CommonUtil.getValue("PERSON", "personid='"+mbo.getUserInfo().getPersonId()+"'", "udcompany");
			if(company == null || !company.equalsIgnoreCase("CSPL")){
				throw new MXApplicationException("guide", "1062");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		super.toggledeleterow();
		return 1;
		
	}
	
	
}
