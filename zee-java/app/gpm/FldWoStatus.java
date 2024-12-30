package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWoStatus extends MAXTableDomain {

	public FldWoStatus(MboValue mbv) throws MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("SYNONYMDOMAIN", "domainid='WOSTATUS' and value=:" + thisAttr);
		String[] FromStr = { "value" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "domainid='WOSTATUS'";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void init() throws RemoteException, MXException {
		super.init();
		
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
	
}
