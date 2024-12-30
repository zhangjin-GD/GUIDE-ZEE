package guide.app.udwohc;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 *@function:ZEE-用于A+B=C
 *@author:zj
 *@modify:
 */
public class UDFldWOHcquantity extends MboValueAdapter {

	public UDFldWOHcquantity() {
		super();
	}

	public UDFldWOHcquantity(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		double sumcost = getSumcost(mbo);
		double quantity = mbo.getDouble("quantity");
		mbo.setValue("unitcost", sumcost/quantity, 11L);
	}
	
	private double getSumcost(MboRemote mbo) throws MXException,RemoteException {
		double sumcost = 0.0D;
		String wonum = mbo.getString("wonum");
		MboSetRemote matusetransSet = MXServer.getMXServer().getMboSet("MATUSETRANS", MXServer.getMXServer().getSystemUserInfo());
		matusetransSet.setWhere(" issuetype in ('ISSUE','RETURN') and refwo='"+wonum+"' ");
		matusetransSet.reset();
		if (!matusetransSet.isEmpty() && matusetransSet.count() > 0) {
			sumcost = matusetransSet.sum("linecost");
		}
		matusetransSet.close();
		return sumcost;
	}
	
}
