package guide.app.workorder;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class FldScore extends MboValueAdapter {

	public FldScore(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote mboSet = mbo.getMboSet("UDWPLABOR");

		String name = getMboValue().getName();

		if (!mboSet.isEmpty() && mboSet.count() > 0) {
			if (name != null && name.equals("SAFEY")) {
				for (int i = 0; i < mboSet.count(); i++) {
					MboRemote safey = mboSet.getMbo(i);
					safey.setValue("safey", mbo.getString("safey"), 11L);
				}
			}
			if (name != null && name.equals("EFFICIENCY")) {
				for (int i = 0; i < mboSet.count(); i++) {
					MboRemote Efficiency = mboSet.getMbo(i);
					Efficiency.setValue("efficiency", mbo.getString("efficiency"), 11L);
				}
			}
			if (name != null && name.equals("WORKQUALITY")) {
				for (int i = 0; i < mboSet.count(); i++) {
					MboRemote workquality = mboSet.getMbo(i);
					workquality.setValue("workquality", mbo.getString("workquality"), 11L);
				}
			}
		}
	}
}
