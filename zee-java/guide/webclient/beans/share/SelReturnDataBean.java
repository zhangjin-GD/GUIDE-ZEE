package guide.webclient.beans.share;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.share.ShareUse;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelReturnDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean returnline = app.getDataBean("returnline_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote mbo = returnline.getParent().getMbo();
		if (mbo != null && mbo instanceof ShareUse) {
			MboSetRemote returnLineSet = mbo.getMboSet("RETURNLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote returnMbo = returnLineSet.add();
				returnMbo.setValue("shareusenum", mbo.getString("shareusenum"), 11L);
				returnMbo.setValue("linenum", mr.getInt("linenum"), 11L);
				returnMbo.setValue("usetype", "RETURN", 11L);
				returnMbo.setValue("createby", mbo.getUserInfo().getPersonId(), 11L);
				returnMbo.setValue("createtime", MXServer.getMXServer().getDate(), 11L);
				returnMbo.setValue("itemnum", mr.getString("itemnum"), 11L);
				returnMbo.setValue("borrowid", mr.getInt("udshareuselineid"), 11L);
				returnMbo.setValue("storeloc", mr.getString("storeloc"), 11L);
				returnMbo.setValue("invbalancesid", mr.getInt("invbalancesid"), 11L);
				double orderqty = mr.getDouble("orderqty");
				double returnQty = 0;
				if (!returnLineSet.isEmpty() && returnLineSet.count() > 0) {
					int udshareuselineid = mr.getInt("udshareuselineid");
					for (int j = 0; returnLineSet.getMbo(j) != null; j++) {
						MboRemote returnLineMbo = returnLineSet.getMbo(j);
						int borrowid = returnLineMbo.getInt("borrowid");
						if (borrowid == udshareuselineid) {
							returnQty += returnLineMbo.getDouble("orderqty");
						}
					}
				}
				double qty = orderqty - returnQty;
				returnMbo.setValue("orderqty", qty, 11L);
			}
		}
		returnline.reloadTable();
		return 1;
	}

}
