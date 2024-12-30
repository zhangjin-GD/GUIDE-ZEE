package guide.webclient.beans.fixed;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.fixed.FixTrans;
import guide.app.fixed.FixTransLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelectFixTransDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		FixTrans owner = (FixTrans) this.app.getAppBean().getMbo();
		MboSetRemote lineSet = owner.getMboSet("UDFIXTRANSLINE");
		for (int i = 0; i < vector.size(); i++) {
			MboRemote mr = (MboRemote) vector.elementAt(i);
			FixTransLine line = (FixTransLine) lineSet.add();
			line.setValue("fixassetnum", mr.getString("fixassetnum"), 11L);
		}
		return super.execute();
	}
}
