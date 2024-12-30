package guide.app.udwohc;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 *@function:ZEE-用于A+B=C
 *@author:zj
 *@modify:
 */
public class UDFldWOHcitemnum extends MAXTableDomain {

	public UDFldWOHcitemnum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("ITEM", "ITEMNUM =:" + thisAttr);
		String[] FromStr = { "ITEMNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	@Override
	public MboSetRemote getList() throws MXException, RemoteException {  
		String sql=" itemnum in (select itemnum from uditemcp where udcompany='ZEE') ";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		double sumprcost = 0.0D;
		MboSetRemote prlineSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
		prlineSet.setWhere(" itemnum='"+getMboValue()+"' ");
		prlineSet.reset();
		if (!prlineSet.isEmpty() && prlineSet.count() > 0) {
			sumprcost = prlineSet.sum("linecost");
		}
		prlineSet.close();
		mbo.setValue("prcost", sumprcost, 11L);
		if (getMboValue().isNull()) {
			mbo.setValue("prcost", "", 11L);
		}
	}
}
