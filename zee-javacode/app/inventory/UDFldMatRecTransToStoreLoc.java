package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldMatRecTransToStoreLoc;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 *@function:
 *@author:zj
 *@date:下午3:58:27
 *@modify:
 */
public class UDFldMatRecTransToStoreLoc extends FldMatRecTransToStoreLoc{

	public UDFldMatRecTransToStoreLoc(MboValue mbv) throws MXException,
			RemoteException {
		super(mbv);
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String itemnum = mbo.getString("itemnum");
		String storeloc = "";
			MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
			uditemcpSet.setWhere(" itemnum = '" + itemnum + "' and udcompany = 'ZEE' ");
			uditemcpSet.reset();
			if(!uditemcpSet.isEmpty() && uditemcpSet.count() > 0){
				MboRemote uditemcp = uditemcpSet.getMbo(0);
				storeloc = uditemcp.getString("storeloc");
				if(!storeloc.equalsIgnoreCase("")){
				mbo.setValue("tostoreloc", storeloc, 11L);
				mbo.setValue("udstoreroom", storeloc, 11L);
				}
			}
			uditemcpSet.close();
	}
	
}
