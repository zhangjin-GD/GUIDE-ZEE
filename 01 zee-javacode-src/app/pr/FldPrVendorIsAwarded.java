package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPrVendorIsAwarded extends MboValueAdapter {

	public FldPrVendorIsAwarded(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null && !owner.getString("udcompany").equalsIgnoreCase("ZEE")) {
			boolean isawarded = mbo.getBoolean("isawarded");
			int udprvendorid1 = mbo.getInt("udprvendorid");
			if (isawarded) {
				String vendor = mbo.getString("vendor");
				owner.setValue("vendor", vendor, 11L);
			} else {
				owner.setValueNull("vendor");
			}
			MboSetRemote mboSet = mbo.getThisMboSet();
			if (!mboSet.isEmpty() && mboSet.count() > 0) {
				for (int i = 0; mboSet.getMbo(i) != null; i++) {
					MboRemote line = mboSet.getMbo(i);
					int udprvendorid2 = line.getInt("udprvendorid");
					if (udprvendorid1 != udprvendorid2) {
						line.setValue("isawarded", false, 11L);
					}
				}
			}
		}
	}
}
