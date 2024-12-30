package guide.webclient.beans.inventory;

import java.rmi.RemoteException;

import org.json.JSONException;

import guide.app.inventory.InvMthly;
import guide.iface.sap.SapHeader;
import guide.iface.sap.SapHeaderSet;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SapHeaderTableBean extends DataBean {

	public void sendSap() throws RemoteException, MXException {
		InvMthly mbo = (InvMthly) this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		SapHeaderSet sapHeaderSet = (SapHeaderSet) mbo.getMboSet("UDSAPHEADER");
		if (!sapHeaderSet.isEmpty() && sapHeaderSet.count() > 0) {
			for (int i = 0; sapHeaderSet.getMbo(i) != null; i++) {
				SapHeader sapHeader = (SapHeader) sapHeaderSet.getMbo(i);
				if (sapHeader.isNull("sapstatus")) {
					try {
						sapHeader.dataToSap();
						sapHeaderSet.save();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		this.app.getAppBean().save();
	}
}
