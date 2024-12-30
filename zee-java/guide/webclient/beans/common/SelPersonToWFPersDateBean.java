package guide.webclient.beans.common;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.common.WFPers;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPersonToWFPersDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		MboRemote owner = this.getParent().getMbo();
		Vector<MboRemote> vector = this.getSelection();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDWFPERS");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				WFPers line = (WFPers) lineSet.add();
				line.setValue("personid", mr.getString("personid"), 2L);
			}
		}
		return super.execute();
	}
}
