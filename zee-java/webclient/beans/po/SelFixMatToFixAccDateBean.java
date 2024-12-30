package guide.webclient.beans.po;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import guide.app.fixed.FixAcc;
import guide.app.fixed.FixAccSet;
import guide.app.fixed.FixEd;
import guide.app.fixed.FixEdSet;
import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelFixMatToFixAccDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.app.getAppBean().getMbo();
		String ponum = owner.getString("ponum");
		String purchaseagent = owner.getString("purchaseagent");
		String podesc = owner.getString("description");
		String vendor = owner.getString("vendor");
		if (owner != null) {
			FixAccSet mboSet = (FixAccSet) owner.getMboSet("UDFIXACC");
			FixAcc mbo = (FixAcc) mboSet.add();
			mbo.setValue("buyer", purchaseagent, 2L);
			mbo.setValue("description", ponum + " " + podesc, 11L);
			String fixaccnum = mbo.getString("fixaccnum");
			FixEdSet lineSet = (FixEdSet) mbo.getMboSet("UDFIXED");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				String itemnum = mr.getString("itemnum");
				String itemdesc = mr.getString("item.description");
				String modelnum = mr.getString("item.udmodelnum");
				String specs = mr.getString("item.udspecs");
				String manufacturer = mr.getString("item.udmanufacturer");
				int quantity = mr.getInt("quantity");
				double totalunitcost = mr.getDouble("totalunitcost");
				Date transtime = mr.getDate("transtime");
				String requestedby = mr.getString("requestedby");
				int fixrectransid = mr.getInt("udfixrectransid");
				for (int j = 0; j < quantity; j++) {
					FixEd line = (FixEd) lineSet.addAtEnd();
					line.setValue("itemnum", itemnum, 11L);
					line.setValue("vendor", vendor, 11L);
					line.setValue("description", itemdesc, 11L);
					line.setValue("modelnum", modelnum, 11L);
					line.setValue("specs", specs, 11L);
					line.setValue("manufacturer", manufacturer, 11L);
					line.setValue("useby", requestedby, 2L);
					line.setValue("quantity", 1, 11L);
					line.setValue("originalvalue", totalunitcost, 11L);
					line.setValue("purchasedate", transtime, 11L);
					line.setValue("fixrectransid", fixrectransid, 11L);
				}
				mr.setValue("fixaccnum", fixaccnum, 11L);
			}
			String message = "提示，转固创建成功！单号：" + fixaccnum;
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", message, 1);
		}
		return super.execute();
	}
}
