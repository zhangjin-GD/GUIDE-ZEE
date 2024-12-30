package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldMatSafeInvUseLineId extends MAXTableDomain {

	public FldMatSafeInvUseLineId(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("INVUSELINE", "INVUSELINEID=:" + thisAttr);
		String[] FromStr = { "INVUSELINEID" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		setListCriteria(
				"itemnum=:itemnum and assetnum=:assetnum and not exists(select 1 from udmatsafe where invuselineid=invuseline.invuselineid)");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote invuseLineSet = mbo.getMboSet("INVUSELINE");
		if (!invuseLineSet.isEmpty() && invuseLineSet.count() > 0) {
			MboRemote invuseLine = invuseLineSet.getMbo(0);
			String wonum = invuseLine.getString("invuse.udwonum");
			String invusenum = invuseLine.getString("invusenum");
			int invuselinenum = invuseLine.getInt("invuselinenum");
			String itemnum = invuseLine.getString("itemnum");
			String assettypecode = invuseLine.getString("asset.udassettypecode");
			String assetnum = invuseLine.getString("assetnum");

			mbo.setValue("wonum", wonum, 11L);
			mbo.setValue("invusenum", invusenum, 11L);
			mbo.setValue("invuselinenum", invuselinenum, 11L);
			mbo.setValue("itemnum", itemnum, 11L);
			mbo.setValue("assetnum", assetnum, 11L);
			mbo.setValue("assettypecode", assettypecode, 11L);
		}
	}
}
