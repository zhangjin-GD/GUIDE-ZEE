package guide.app.company;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldBankInfoIsDefault extends MboValueAdapter {

	public FldBankInfoIsDefault(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		boolean isdefault = this.getMboValue().getBoolean();
		MboSetRemote thisMboSet = mbo.getThisMboSet();
		if (!thisMboSet.isEmpty() && isdefault) {
			for (int i = 0; thisMboSet.getMbo(i) != null; i++) {
				MboRemote thisMbo = thisMboSet.getMbo(i);
				if (mbo != thisMbo && (thisMbo.getInt("linenum") != mbo.getInt("linenum"))) {
					thisMbo.setValue("isdefault", false, 11L);
				}
			}
		}
	}
}
