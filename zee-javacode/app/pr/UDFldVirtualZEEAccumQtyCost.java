package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 *@function:
 *@author:zj
 *@date:下午5:14:52
 *@modify:
 */
public class UDFldVirtualZEEAccumQtyCost extends MboValueAdapter{

	public UDFldVirtualZEEAccumQtyCost() {
		super();
	}

	public UDFldVirtualZEEAccumQtyCost(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = getMboValue().getMbo();
		int qty = 0;
		double cost = 0.0D;
		MboSetRemote prlineSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
		prlineSet.setWhere(" prnum in (select prnum from pr where udcompany='ZEE' and status='APPR') and rfqnum is null and ponum is null and udprevendor='"+mbo.getString("company")+"' ");
		prlineSet.reset();
		if (!prlineSet.isEmpty() && prlineSet.count() > 0) {
			qty = prlineSet.count();
			cost = prlineSet.sum("udtotalcost");
		}
		prlineSet.close();
		mbo.setValue("udzeeqty",String.valueOf(qty), 11L);
		mbo.setValue("udzeecost",String.valueOf(cost), 11L);
	}
	
}
