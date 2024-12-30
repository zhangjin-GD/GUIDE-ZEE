package guide.webclient.beans.workplan;

import java.rmi.RemoteException;
import java.util.Vector;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWorkLaborDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner != null) {
			MboSetRemote mboSet = owner.getMboSet("UDWORKLABOR");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote mbo = mboSet.add();
				mbo.setValue("plannum", owner.getString("plannum"), 11L);
				mbo.setValue("personid", mr.getString("personid"), 11L);
				mbo.setValue("description", mr.getString("displayname"), 11L);
				mbo.setValue("status", "Y", 11L);
			}
		}
		return super.execute();
	}
}
