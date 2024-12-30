package guide.webclient.beans.matsafe;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelMatSafeTypeDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean table = app.getDataBean("udvmatsafeline_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = table.getParent().getMbo();
		if (owner != null) {
			MboSetRemote mboSet = owner.getMboSet("UDVMATSAFELINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote mbo = mboSet.addAtEnd();
				mbo.setValue("matsafedesc", mr.getString("matsafedesc"), 11L);
			}
		}
		table.reloadTable();
		return 1;
	}
}
