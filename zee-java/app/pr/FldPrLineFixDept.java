package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrLineFixDept extends MAXTableDomain {

	public FldPrLineFixDept(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "deptnum = :" + thisAttr);
		String[] FromStr = { "deptnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			setListCriteria("type = 'DEPARTMENT' and parent='" + udcompany + "'");
		}
		return super.getList();
	}
}
