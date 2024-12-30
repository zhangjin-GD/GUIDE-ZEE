package guide.app.workplan;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWoPermitHeadName extends MAXTableDomain {

	public FldWoPermitHeadName(MboValue mbv) {
		super(mbv);
		String thisAttr = this.getMboValue().getAttributeName();
		this.setRelationship("PERSON", "displayname=:" + thisAttr);
		String[] FromStr = { "displayname" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String sql = "status in ('ACTIVE')";
			if (!owner.isNull("udcompany")) {
				String udcompany = owner.getString("udcompany");
				sql += " and udcompany='" + udcompany + "'";
			}
			if (!owner.isNull("uddept")) {
				String uddept = owner.getString("uddept");
				sql += " and uddept='" + uddept + "'";
			}
			if (!owner.isNull("udofs")) {
				String udofs = owner.getString("udofs");
				sql += " and udofs='" + udofs + "'";
			}
			setListCriteria(sql);
		}
		return super.getList();
	}
}
