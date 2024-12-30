package guide.webclient.beans.person;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class WFassignmentTableBean extends DataBean {

	public void wftrans() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		if (owner.isNull("delegate")) {
			throw new MXApplicationException("guide", "1117");
		}
		if (owner != null) {
			String delegate = owner.getString("delegate");
			Vector<MboRemote> vector = this.getSelection();
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				mr.setValue("assigncode", delegate, 11L);
			}
		}
		this.app.getAppBean().save();
	}
}
