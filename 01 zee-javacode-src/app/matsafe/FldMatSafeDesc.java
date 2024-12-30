package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldMatSafeDesc extends MAXTableDomain {

	public FldMatSafeDesc(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDMATSAFETYPE", "matsafedesc=:" + thisAttr);
		String[] FromStr = { "matsafedesc" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String sql = "matsafetype=:matsafetype";
		if (!mbo.isNull("part")) {
			sql += " and part=:part";
		} else {
			sql += " and part is null";
		}
		setListCriteria(sql);
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote matsafedescSet = mbo.getMboSet("MATSAFEDESC");
		if (!matsafedescSet.isEmpty() && matsafedescSet.count() > 0) {
			MboRemote matsafedesc = matsafedescSet.getMbo(0);
			String description = matsafedesc.getString("description");
			mbo.setValue("description", description, 11L);
		}
	}
}
