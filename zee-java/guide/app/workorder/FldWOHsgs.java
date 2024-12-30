package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldWOHsgs extends MboValueAdapter {

	public FldWOHsgs(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();

		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote mboSet = mbo.getMboSet("UDWPLABORGS");
		if (!mboSet.isEmpty() && mboSet.count() > 0) {
			mbo.setValue("UDHSGS", mboSet.sum("WORKTIME"), 11L);
		}
	}
}
