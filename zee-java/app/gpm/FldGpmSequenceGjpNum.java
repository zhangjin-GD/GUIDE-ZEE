package guide.app.gpm;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldGpmSequenceGjpNum extends MAXTableDomain {

	public FldGpmSequenceGjpNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDGJOBPLAN", "gjpnum=:" + thisAttr);
		String[] FromStr = { "gjpnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String sql = "status='ACTIVE'";
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			String uddept = owner.getString("uddept");
			String udofs = owner.getString("udofs");
			String assetnum = owner.getString("assetnum");
			if (!owner.isNull("udofs")) {
				sql += " and udofs='" + udofs + "'";
			}
			sql += " and udcompany ='" + udcompany + "' and uddept='" + uddept
					+ "' and assettype=(select udassettypecode from asset where assetnum='" + assetnum
					+ "' and status='ACTIVE')";
		}
		setListCriteria(sql);
		return super.getList();
	}
}
