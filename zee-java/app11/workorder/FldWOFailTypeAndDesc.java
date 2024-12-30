package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWOFailTypeAndDesc extends MAXTableDomain {

	public FldWOFailTypeAndDesc(MboValue mbv) {
		super(mbv);
		setRelationship("FAILURELIST", "failurecode = :udfailtype");
		String[] FromStr = { "failurecode" };
		String[] ToStr = { "udfailtype" };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public void validate() throws MXException, RemoteException {
//		super.validate();
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		setListCriteria("parent is null");
		return super.getList();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String thisAttr = getMboValue().getAttributeName();
		if ("UDFAILTYPE".equalsIgnoreCase(thisAttr)) {
			MboSetRemote codeSet = mbo.getMboSet("UDFAILURECODE");
			if (!codeSet.isEmpty() && codeSet.count() > 0) {
				MboRemote failtype = codeSet.getMbo(0);
				String desc = failtype.getString("description");
				mbo.setValue("udfailtypedesc", desc, 11L);
			}
		}
	}
}
