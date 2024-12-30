package guide.app.asset;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldAssettypeFloors extends MAXTableDomain {
	
	public FldAssettypeFloors(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDASSETTYPE", "code=:" + thisAttr);
		String[] FromStr = { "code" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		setListCriteria("parent='"+mbo.getString("udassettypecode")+"'");
		return super.getList();
	}
	
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
	public void action() throws RemoteException, MXException {
		super.action();
		
	}

	
}