package guide.webclient.beans.workplan;

import java.rmi.RemoteException;
import java.util.Vector;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWorkHazardDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner != null) {
			MboSetRemote mboSet = owner.getMboSet("UDWORKHAZARD");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote mbo = mboSet.add();
				mbo.setValue("plannum", owner.getString("plannum"), 11L);
				mbo.setValue("hiddannum", mr.getString("udhiddannum"), 11L);
				mbo.setValue("risk", mr.getString("risk"), 11L);
				mbo.setValue("description", mr.getString("description"), 11L);
			}
		}
		return super.execute();
	}
}
