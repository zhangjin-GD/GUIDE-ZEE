package guide.app.udwohc;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDWORepairponum extends MAXTableDomain{

	public UDWORepairponum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("PO", "PONUM =:" + thisAttr);
		String[] FromStr = { "PONUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}
	
	public MboSetRemote getList() throws MXException, RemoteException{
		MboSetRemote list = super.getList();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		String wonum = mbo.getString("wonum");
		list.setWhere(" ponum in (select distinct ponum from poline where refwo = '" + wonum+ "' )");
		list.reset();
		return list;
	}//获取与此wo相关的po
	
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		Double polinecost = 0.0D;
		String wonum = mbo.getString("wonum");
		MboSetRemote polineSet= MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
		polineSet.setWhere(" refwo = '" + wonum+ "' ");
		polineSet.reset();
		if(!polineSet.isEmpty() && polineSet.count() > 0){
			polinecost = polineSet.sum("linecost");//获取维修po的总价
		}
		polineSet.close();
		mbo.setValue("pocost", polinecost, 11L);//给虚拟框pocost赋值
	}
}
