package guide.webclient.beans.fixed;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.fixed.FixStock;
import guide.app.fixed.FixStockLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelectFixStockDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		FixStock owner = (FixStock) this.app.getAppBean().getMbo();
		MboSetRemote lineSet = owner.getMboSet("UDFIXSTOCKLINE");
		for (int i = 0; i < vector.size(); i++) {
			MboRemote mr = (MboRemote) vector.elementAt(i);
			FixStockLine line = (FixStockLine) lineSet.add();
			line.setValue("fixassetnum", mr.getString("fixassetnum"), 11L);
		}
		return super.execute();
	}

}
