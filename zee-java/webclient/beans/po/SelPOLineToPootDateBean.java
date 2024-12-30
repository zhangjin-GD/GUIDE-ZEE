package guide.webclient.beans.po;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPOLineToPootDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		Vector<MboRemote> vector = this.getSelection();
		if (vector.size() > 0) {
			String personid = mbo.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			String company = mbo.getString("udcompany");
			String description = mbo.getString("description");
			String ponum = mbo.getString("ponum");
			String vendor = mbo.getString("vendor");
			String location = "";
			MboSetRemote locSet = mbo.getMboSet("$LOCATIONS", "LOCATIONS",
					"udcompany = '" + company + "' and udloctype = '02'");
			if (!locSet.isEmpty() && locSet.count() > 0) {
				location = locSet.getMbo(0).getString("location");
			}
			MboSetRemote poSet = mbo.getMboSet("$PO", "PO", "1=2");
			MboRemote po = poSet.add();
			po.setValue("udapptype", "POOT", 11L);
			po.setValue("description", ponum + "-" + description, 11L);
			po.setValue("purchaseagent", personid, 2L);
			po.setValue("udcreateby", personid, 2L);// 创建人
			po.setValue("udcreatetime", currentDate, 11L);// 创建时间
			po.setValue("vendor", vendor, 2L);
			MboSetRemote polineSet = po.getMboSet("POLINE");

			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote poline = polineSet.addAtEnd();
				String itemnum = mr.getString("prline.udseritemnum");
				double orderqty = mr.getDouble("orderqty");
				double udtotalprice = mr.getDouble("udtotalprice");
				poline.setValue("itemnum", itemnum, 2L);
				poline.setValue("storeloc", location, 2L);
				poline.setValue("orderqty", orderqty, 2L);
				poline.setValue("udpredicttaxprice", udtotalprice, 2L);
				int polineid = poline.getInt("polineid");
				mr.setValue("udpolineid", polineid, 11L);
			}
		}
		return super.execute();
	}
}
