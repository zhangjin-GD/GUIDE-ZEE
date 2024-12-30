package guide.webclient.beans.po;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

import java.rmi.RemoteException;
import java.util.Vector;

public class SelPOLineUnacceptedDateBean extends DataBean {
	/**
	 * ZEE - 接收应用程序：拒收功能
	 * 2024-10-9 11:29
	 * 33-66
	 */
	public synchronized int execute() throws MXException, RemoteException {
		DataBean table = app.getDataBean("polines_unaccepted_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = table.getParent().getMbo();
		if (owner != null) {
			MboSetRemote mboSet = owner.getMboSet("POLINEUNACCEPTED");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote mbo = mboSet.add();
				mbo.setValue("polineid", mr.getLong("polineid"), 11L);
				mbo.setValue("ponum", mr.getString("ponum"), 11L);
				mbo.setValue("polinenum", mr.getString("polinenum"), 11L);
				mbo.setValue("itemnum", mr.getString("itemnum"), 11L);
				mbo.setValue("description", mr.getString("description"), 11L);
				mbo.setValue("tax1code", mr.getString("tax1code"), 11L);
				mbo.setValue("udpredictprice", mr.getDouble("udpredictprice"), 11L);
				mbo.setValue("udtotalprice", mr.getDouble("udtotalprice"), 11L);
				mbo.setValue("orderqty", mr.getDouble("udunreceivedqty"), 2L);
				mbo.setValue("udbudgetnum", mr.getString("udbudgetnum"), 11L);
				mbo.setValue("udprojectnum", mr.getString("udprojectnum"), 11L);
			}
		}
		table.reloadTable();
		return 1;
	}

}
