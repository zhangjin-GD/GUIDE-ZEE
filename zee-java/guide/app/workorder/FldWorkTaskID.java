package guide.app.workorder;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldWorkTaskID extends MAXTableDomain {
	
	public FldWorkTaskID(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDWORKTASK", "assetnum = :assetnum and udworktaskid =:" + thisAttr);
		String[] FromStr = { "assetnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "assetnum = :assetnum and udworktaskid not in(select nvl(udworktaskid,1) from workorder where assetnum=:assetnum)";
		System.out.println(sql);
		setListCriteria(sql);
		return super.getList();
	}
	
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
	public void action() throws RemoteException, MXException {
		super.action();
		
	}

	
}