package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelInvUseLineScrapDataBean extends DataBean {

	public synchronized int execute() throws MXException, RemoteException {
		DataBean table = app.getDataBean("1674978954371");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = table.getParent().getMbo();
		if (owner != null) {
			MboSetRemote mboSet = owner.getMboSet("UDMATDSPOLINESCRAP");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote mbo = mboSet.add();
				mbo.setValue("matdsponum", owner.getString("matdsponum"), 11L);
				mbo.setValue("INVUSELINEID", mr.getString("INVUSELINEID"), 11L);
				mbo.setValue("linetype", "SCRAP", 11L);

				mbo.setValue("ITEMNUM", mr.getString("ITEMNUM"), 11L);
				mbo.setValue("DESCRIPTION", mr.getString("DESCRIPTION"), 11L);
				mbo.setValue("ORDERQTY", mr.getString("QUANTITY"), 11L);
				mbo.setValue("UNITCOST", mr.getString("UNITCOST"), 11L);
				mbo.setValue("LINECOST", mr.getString("LINECOST"), 11L);

				mbo.setValue("ENTERBY", mr.getString("ENTERBY"), 11L);
				mbo.setValue("ENTERDATE", mr.getString("ACTUALDATE"), 11L);

			}
		}
		table.reloadTable();
		return 1;
	}
}
