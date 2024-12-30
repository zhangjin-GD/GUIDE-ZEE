package guide.app.inventory;

import java.rmi.RemoteException;

import guide.app.common.FldProjectNum;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldInvUseProjectNum extends FldProjectNum {

	public FldInvUseProjectNum(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String udprojectnum = this.getMboValue().getString();
		MboSetRemote lineSet = mbo.getMboSet("INVUSELINE");
		if (lineSet != null && !lineSet.isEmpty()) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("udprojectnum", udprojectnum, 11L);
			}
		}
	}
}
