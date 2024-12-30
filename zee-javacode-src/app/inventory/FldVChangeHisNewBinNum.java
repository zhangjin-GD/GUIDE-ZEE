package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldVChangeHisNewBinNum extends MAXTableDomain {

	public FldVChangeHisNewBinNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDBIN", "BINNUM=:" + thisAttr);
		String[] FromStr = { "BINNUM" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDInvBalances) {
			String location = owner.getString("location");
			String sql = "location = '" + location + "'";
			setListCriteria(sql);
		}
		return super.getList();
	}

	@Override
	public void validate() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDInvBalances) {
			MboSetRemote locationSet = owner.getMboSet("LOCATION");
			if (!locationSet.isEmpty() && locationSet.count() > 0) {
				String udcompany = locationSet.getMbo(0).getString("udcompany");
				if (udcompany.equalsIgnoreCase("GR02PCT")) {
					super.validate();
				}
			}
		}
	}
}
