package guide.app.item;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldItemVenisActive extends MboValueAdapter{
	public UDFldItemVenisActive(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
}
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			boolean udactive = mbo.getBoolean("udactive");
			int uditemcpvenid1 = mbo.getInt("uditemcpvenid");

			MboSetRemote mboSet = mbo.getThisMboSet();
			if (!mboSet.isEmpty() && mboSet.count() > 0) {
				for (int i = 0; mboSet.getMbo(i) != null; i++) {
					MboRemote line = mboSet.getMbo(i);
					int uditemcpvenid2 = line.getInt("uditemcpvenid");
					if (uditemcpvenid1 != uditemcpvenid2) {
						line.setValue("udactive", false, 11L);
					}
				}
			}
		}
	}
}