package guide.webclient.beans.invuse;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.inventory.InvUseSplit;
import guide.app.inventory.InvUseSplitSet;
import guide.app.inventory.UDInvUse;
import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelInvEntoryToInvUseSplitDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean tableBean = app.getDataBean("invusesplit_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = tableBean.getParent().getMbo();
		if (owner != null && owner instanceof UDInvUse) {
			InvUseSplitSet lineSet = (InvUseSplitSet) owner.getMboSet("udinvusesplit");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				InvUseSplit line = (InvUseSplit) lineSet.addAtEnd();
				line.setValue("itemnum", mr.getString("itemnum"), 2L);
			}
		}
		tableBean.reloadTable();
		return 1;
	}
}
