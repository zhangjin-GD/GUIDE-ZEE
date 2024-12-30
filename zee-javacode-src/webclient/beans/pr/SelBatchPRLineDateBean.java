package guide.webclient.beans.pr;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelBatchPRLineDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();

		MboSetRemote vprSet = mbo.getMboSet("UDVPR");
		if (vprSet != null && !vprSet.isEmpty()) {
			MboRemote vpr = vprSet.getMbo(0);
			if (!vpr.isNull("esttime") || !vpr.isNull("remark")) {
				MboSetRemote prLineSet = this.getMbo().getMboSet("PRNOTRFQ");
				Vector<MboRemote> vector = prLineSet.getSelection();
				for (int i = 0; i < vector.size(); i++) {
					MboRemote mr = (MboRemote) vector.elementAt(i);
					if (!vpr.isNull("esttime")) {
						mr.setValue("udesttime", vpr.getString("esttime"), 11L);
					}
					if (!vpr.isNull("remark")) {
						mr.setValue("remark", vpr.getString("remark"), 11L);
					}
				}
			}
		}
		return super.execute();
	}

}
