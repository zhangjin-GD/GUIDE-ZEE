package guide.webclient.beans.gjobplan;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

import java.rmi.RemoteException;
import java.util.Vector;

public class SelHiddanDateBean extends DataBean {

	public synchronized int execute() throws MXException, RemoteException {
		DataBean lineTable = this.app.getDataBean("hiddan_table");
		Vector vector = getSelection();
		MboRemote owner = lineTable.getParent().getMbo();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDGJOBHIDDAN");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote line = lineSet.addAtEnd();
				line.setValue("gjpnum", owner.getString("gjpnum"), 11L);
				line.setValue("udhiddannum", mr.getString("udhiddannum"), 11L);
				line.setValue("udtype", mr.getString("udtype"), 11L);
				line.setValue("risk", mr.getString("risk"), 11L);
				line.setValue("description", mr.getString("description"), 11L);
			}
		}
		lineTable.reloadTable();
		return 1;
	}
}
