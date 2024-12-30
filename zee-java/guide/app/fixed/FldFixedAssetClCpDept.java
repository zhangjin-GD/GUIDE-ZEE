package guide.app.fixed;

import java.rmi.RemoteException;

import guide.app.common.FldComDept;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldFixedAssetClCpDept extends FldComDept {

	public FldFixedAssetClCpDept(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote personSet = mbo.getMboSet("$PERSON", "PERSON", "uddept=:uddept and title like '%固定资产管理员%'");
		if (!personSet.isEmpty() && personSet.count() > 0) {
			mbo.setValue("fixassetadmin", personSet.getMbo(0).getString("personid"), 11L);
		}
	}
}
