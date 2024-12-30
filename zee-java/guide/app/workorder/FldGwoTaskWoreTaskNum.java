package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldGwoTaskWoreTaskNum extends MAXTableDomain {

	public FldGwoTaskWoreTaskNum(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDWORETASK", "woretasknum=:" + thisAttr);
		String[] FromStr = { "woretasknum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "status not in 'E'";
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && owner instanceof UDWO) {
			String assetnum = owner.getString("assetnum");
			sql += " and assetnum='" + assetnum + "'";
		}
		setListCriteria(sql);
		return super.getList();
	}
}
