package guide.webclient.beans.share;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.share.ShareUse;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelBorrowDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean borrowline = app.getDataBean("borrowline_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote mbo = borrowline.getParent().getMbo();
		if (mbo != null && mbo instanceof ShareUse) {
			MboSetRemote borrowSet = mbo.getMboSet("BORROWLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote borrow = borrowSet.add();
				borrow.setValue("shareusenum", mbo.getString("shareusenum"), 11L);
				borrow.setValue("linenum", (int) borrowSet.max("linenum") + 1, 11L);
				borrow.setValue("usetype", "BORROW", 11L);
				borrow.setValue("orderqty", 1, 11L);
				borrow.setValue("createby", mbo.getUserInfo().getPersonId(), 11L);
				borrow.setValue("createtime", MXServer.getMXServer().getDate(), 11L);
				borrow.setValue("itemnum", mr.getString("itemnum"), 11L);
				borrow.setValue("storeloc", mr.getString("location"), 11L);
				borrow.setValue("invbalancesid", mr.getInt("invbalancesid"), 11L);
			}
		}
		borrowline.reloadTable();
		return 1;
	}

}
