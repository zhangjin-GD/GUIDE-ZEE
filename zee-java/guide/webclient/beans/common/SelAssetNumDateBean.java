package guide.webclient.beans.common;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelAssetNumDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.parent.getMbo();
		if (owner != null) {
			StringBuffer bufid = new StringBuffer();
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				String assetnum = mr.getString("assetnum");
				bufid.append(assetnum).append(",");
			}
			if (bufid.length() > 0) {
				String value = bufid.substring(0, bufid.length() - 1);
				owner.setValue("assetnum", value, 11L);
			}
		}
		return super.execute();
	}
}
