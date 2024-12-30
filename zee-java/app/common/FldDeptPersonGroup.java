package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldDeptPersonGroup extends MAXTableDomain {

	public FldDeptPersonGroup(MboValue mbv) throws MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("PERSONGROUP", "persongroup=:" + thisAttr);
		String[] FromStr = { "persongroup" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String dept = this.getMboValue().getMbo().getString("uddept");
		String sql = "uddept like '"+dept+"%'";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
	
}
