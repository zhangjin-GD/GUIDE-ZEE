package guide.app.common;

import guide.app.po.UDPO;

import java.rmi.RemoteException;

import psdi.common.commlog.CommLog;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 *@function:给PO的sent时间赋值
 *@author:zj
 *@date:2024-06-07 11:17:26
 *@modify:
 */
public class UDCommLog extends CommLog {

	public UDCommLog(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}
	
	public void sendMessage() throws MXException, RemoteException {
		super.sendMessage();
		MboRemote owner = getOwner();
	    if (owner != null && owner instanceof UDPO) {
	    	String udcompany = owner.getString("udcompany");
	    	if (udcompany != null && udcompany.equalsIgnoreCase("ZEE")) {
	    		owner.setValue("udposent", MXServer.getMXServer().getDate(), 11L);
	    		
	    		MboSetRemote polineSet = owner.getMboSet("POLINE");
	    		if (!polineSet.isEmpty() && polineSet.count() > 0) {
					for (int i = 0; i < polineSet.count(); i++) {
						MboRemote poline = polineSet.getMbo(i);
						poline.setValue("udstatus", "SENT", 11L);
					}
				}
	    	}
		}
	}
	
}
