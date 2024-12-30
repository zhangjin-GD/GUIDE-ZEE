package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldMatSafePart extends MAXTableDomain {

	public FldMatSafePart(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("alndomain", "value =:" + thisAttr);
		String[] FromStr = { "value" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String matsafetype = mbo.getString("matsafetype");
		String sql = "domainid = 'UDPART'";
		if ("WL".equalsIgnoreCase(matsafetype)) {
			sql += " and value !='B'";
		}
		setListCriteria(sql);
		return super.getList();
	}
}
