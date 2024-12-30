package guide.webclient.beans.po;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelMatReturnToPolineDateBean extends DataBean {

	public synchronized int execute() throws MXException, RemoteException {
		DataBean table = app.getDataBean("polines_poline_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = table.getParent().getMbo();
		if (owner != null) {
			String company = owner.getString("udcompany");
			String location = "";
			MboSetRemote locSet = owner.getMboSet("$LOCATIONS", "LOCATIONS",
					"udcompany = '" + company + "' and udloctype = '02'");
			if (!locSet.isEmpty() && locSet.count() > 0) {
				location = locSet.getMbo(0).getString("location");
			}
			MboSetRemote lineSet = owner.getMboSet("POLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote line = lineSet.addAtEnd();
				String itemnum = mr.getString("itemnum");
				double orderqty = mr.getDouble("returqty");
				int udmatreturnid = mr.getInt("udmatreturnid");
				line.setValue("itemnum", itemnum, 2L);
				line.setValue("storeloc", location, 2L);
				line.setValue("orderqty", orderqty, 2L);
				line.setValue("udpredicttaxprice", 0, 2L);
				line.setValue("udmatreturnid", udmatreturnid, 11L);
			}
		}
		table.reloadTable();
		return 1;
	}
}
