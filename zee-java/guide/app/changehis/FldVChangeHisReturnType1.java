package guide.app.changehis;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldVChangeHisReturnType1 extends MAXTableDomain {

	public FldVChangeHisReturnType1(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDMATRETURNTYPE", "returntype1 =:" + thisAttr);
		String[] FromStr = { "returntype1" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		if (!mbo.isNull("returntype")) {
			String returntype = mbo.getString("returntype");
			setListCriteria("returntype ='" + returntype + "'");
		} else {
			MboSetRemote matreturnSet = mbo.getMboSet("UDMATRETURN");
			if (!matreturnSet.isEmpty() && matreturnSet.count() > 0) {
				MboRemote matreturn = matreturnSet.getMbo(0);
				if (!matreturn.isNull("returntype")) {
					String returntype = matreturn.getString("returntype");
					setListCriteria("returntype ='" + returntype + "'");
				}
			}
		}
		return super.getList();
	}
}
