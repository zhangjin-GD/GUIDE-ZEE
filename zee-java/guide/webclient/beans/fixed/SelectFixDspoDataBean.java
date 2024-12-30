package guide.webclient.beans.fixed;

import java.rmi.RemoteException;
import java.util.Vector;
import guide.app.fixed.FixDspo;
import guide.app.fixed.FixDspoLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelectFixDspoDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		FixDspo owner = (FixDspo) this.app.getAppBean().getMbo();
		MboSetRemote lineSet = owner.getMboSet("UDFIXDSPOLINE");
		for (int i = 0; i < vector.size(); i++) {
			MboRemote mr = (MboRemote) vector.elementAt(i);
			FixDspoLine line = (FixDspoLine) lineSet.add();
			line.setValue("fixassetnum", mr.getString("fixassetnum"), 11L);
		}
		return super.execute();
	}
}
