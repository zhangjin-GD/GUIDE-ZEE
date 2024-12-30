package guide.app.fixed;

import java.rmi.RemoteException;

import guide.app.common.FldComDepartment;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldFixEdDeptmg extends FldComDepartment {

	public FldFixEdDeptmg(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote personSet = mbo.getMboSet("$PERSON", "PERSON",
				"uddept=:deptmg and (title like '%固定资产%' or title like '%Fixed Asset%')");
		if (!personSet.isEmpty() && personSet.count() > 0) {
			mbo.setValue("administrator", personSet.getMbo(0).getString("personid"), 11L);
		}
		if (this.getMboValue().isNull()) {
			mbo.setValueNull("administrator", 11L);
		}
	}
}
