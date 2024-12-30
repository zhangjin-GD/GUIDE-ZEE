package guide.webclient.beans.gjobplan;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.gjobplan.UDGjobPlan;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelItemDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean lineData = app.getDataBean("gjobmaterial_table");

		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = lineData.getParent().getMbo();

		if (owner != null && owner instanceof UDGjobPlan) {
			MboSetRemote lineSet = owner.getMboSet("UDGJOBMATERIAL");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote line = lineSet.addAtEnd();
				line.setValue("itemnum", mr.getString("itemnum"), 2L);
			}
		}

		lineData.reloadTable();
		return 1;
	}
}
