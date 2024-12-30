package guide.webclient.beans.inventory;

import java.rmi.RemoteException;

import guide.app.inventory.UDInvUse;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelInvUserToStorelocDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		UDInvUse owner = (UDInvUse) this.app.getAppBean().getMbo();
		MboSetRemote lineSet = owner.getMboSet("INVUSELINE");

		if (lineSet != null && !lineSet.isEmpty()) {

			String udtostoreloc = this.getString("udtostoreloc");
			String udtobin = this.getString("udtobin");

			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				String fromlot = line.getString("fromlot");
				line.setValue("tostoreloc", udtostoreloc, 2L);
				line.setValue("tobin", udtobin, 2L);
				line.setValue("tolot", fromlot, 2L);
			}
		}
		return super.execute();
	}
}
