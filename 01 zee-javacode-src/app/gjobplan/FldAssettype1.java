package guide.app.gjobplan;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldAssettype1 extends MAXTableDomain {
	
	public FldAssettype1(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDASSETTYPE", "parent=:assettype and code=:" + thisAttr);
		String[] FromStr = { "code" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("parent=:assettype");
		return super.getList();
	}
	
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
	public void action() throws RemoteException, MXException {
		super.action();
	}

	
}