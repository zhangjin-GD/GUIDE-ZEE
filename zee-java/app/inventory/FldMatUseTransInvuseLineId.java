package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldMatUseTransInvuseLineId extends MboValueAdapter {
	public FldMatUseTransInvuseLineId(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		if (!this.getMboValue().isNull()) {
			Mbo mbo = this.getMboValue().getMbo();
			MboSetRemote invuselineSet = mbo.getMboSet("INVUSELINE");
			if (invuselineSet != null && !invuselineSet.isEmpty()) {
				MboRemote invuseline = invuselineSet.getMbo(0);
				String udprojectnum = invuseline.getString("udprojectnum");
				String udbudgetnum = invuseline.getString("udbudgetnum");
				String ordertype = invuseline.getString("udordertype");
				String udtax1code = invuseline.getString("udtax1code");
				double udtotalcost = invuseline.getDouble("udtotalcost");
				mbo.setValue("udprojectnum", udprojectnum, 11L);
				mbo.setValue("udbudgetnum", udbudgetnum, 11L);
				mbo.setValue("udordertype", ordertype, 11L);
				mbo.setValue("udtax1code", udtax1code, 11L);
				mbo.setValue("udtotalcost", udtotalcost, 11L);

				Date udbudat = invuseline.getDate("invuse.udbudat");
				if (udbudat != null) {
					mbo.setValue("transdate", udbudat, 2L);
				}
			}
		}
	}
}
