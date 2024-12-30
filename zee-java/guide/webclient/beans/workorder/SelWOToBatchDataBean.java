package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWOToBatchDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDWOBATCHLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote line = lineSet.addAtEnd();
				line.setValue("wonum", mr.getString("wonum"), 11L);
			}
		}
		return super.execute();
	}
}
