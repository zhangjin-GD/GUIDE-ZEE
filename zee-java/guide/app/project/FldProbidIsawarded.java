package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldProbidIsawarded extends MboValueAdapter {

	public FldProbidIsawarded(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote thisMboSet = mbo.getThisMboSet();
		if (!thisMboSet.isEmpty()) {
			for (int i = 0; thisMboSet.getMbo(i) != null; i++) {
				MboRemote thisMbo = thisMboSet.getMbo(i);
				if (mbo != thisMbo && (thisMbo.getInt("linenum") != mbo.getInt("linenum"))) {
					thisMbo.setValue("isawarded", false, 11L);
				}
			}
		}
	}
}
