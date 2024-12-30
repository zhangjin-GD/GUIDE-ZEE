package guide.webclient.beans.jobplan;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelectJoblaborDataBean extends DataBean{

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			MboSetRemote laborSet = owner.getMboSet("joblabor");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote labor = laborSet.add();
				labor.setValue("jpnum", owner.getString("jpnum"),2L);
				labor.setValue("orgid", mr.getString("orgid"),2L);
				labor.setValue("laborcode", mr.getString("laborcode"),2L);

			}
		}
		return super.execute();
	}
}
