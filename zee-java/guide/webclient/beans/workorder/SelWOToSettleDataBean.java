package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWOToSettleDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			MboSetRemote laborSet = owner.getMboSet("UDWOSETTLELINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote labor = laborSet.add();
				labor.setValue("wonum", mr.getString("wonum"), 2L);
			}
		}
		return super.execute();
	}
}
