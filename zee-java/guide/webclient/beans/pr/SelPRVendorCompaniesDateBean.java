package guide.webclient.beans.pr;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.pr.PRVendor;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPRVendorCompaniesDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean table = app.getDataBean("udprvendor_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = table.getParent().getMbo();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDPRVENDOR");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				PRVendor line = (PRVendor) lineSet.add();
				line.setValue("vendor", mr.getString("company"), 2L);
			}
		}
		table.reloadTable();
		return 1;
	}
}
