package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrLineFixAssetAdmin extends MAXTableDomain {

	public FldPrLineFixAssetAdmin(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("person", "personid = :" + thisAttr);
		String[] FromStr = { "personid" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			setListCriteria("udcompany='" + udcompany + "' and status='ACTIVE'");
		}
		return super.getList();
	}
}
