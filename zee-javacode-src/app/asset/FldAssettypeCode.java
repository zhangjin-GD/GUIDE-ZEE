package guide.app.asset;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldAssettypeCode extends MAXTableDomain {
	
	public FldAssettypeCode(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDASSETTYPE", "parent is null and code=:" + thisAttr);
		String[] FromStr = { "code" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria("parent is null");
		return super.getList();
	}
	
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
	public void action() throws RemoteException, MXException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		if (mbo != null && mbo.getName().equalsIgnoreCase("ASSET")) {
			mbo.setValueNull("udassettypecode1", 2L);
		}
	}

	
}