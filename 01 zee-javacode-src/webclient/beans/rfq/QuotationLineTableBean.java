package guide.webclient.beans.rfq;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class QuotationLineTableBean extends DataBean {

	public int costAwarded() throws RemoteException, MXException {
		int rfqlinenumnew = -1;
		int ranking = -1;
		MboRemote mbo = this.getMbo();
		MboSetRemote quoLineSet = mbo.getThisMboSet();
		quoLineSet.setOrderBy("rfqlinenum,decode(udtotalcost,0,99999999,udtotalcost)");
		quoLineSet.reset();
		if (!quoLineSet.isEmpty() && quoLineSet.count() > 0) {
			for (int i = 0; quoLineSet.getMbo(i) != null; i++) {
				MboRemote quoLine = quoLineSet.getMbo(i);
				int rfqlinenum = quoLine.getInt("rfqlinenum");
				if (rfqlinenumnew != rfqlinenum) {
					ranking = 1;
				}
				quoLine.setValue("udranking", "" + ranking + "", 11L);
				if (ranking == 1) {
					quoLine.setValue("isawarded", true, 2L);
				}
				rfqlinenumnew = rfqlinenum;
				ranking++;
			}
		}
		this.app.getAppBean().save();
		return 1;
	}

	public int canAwarded() throws RemoteException, MXException {
		MboRemote mbo = this.getMbo();
		MboSetRemote quoLineSet = mbo.getThisMboSet();
		for (int i = 0; quoLineSet.getMbo(i) != null; i++) {
			MboRemote quoLine = quoLineSet.getMbo(i);
			if (quoLine.getBoolean("isawarded")) {
				quoLine.setValue("isawarded", false, 2L);
			}
			quoLine.setValueNull("udranking");
		}
		this.app.getAppBean().save();
		return 1;
	}
}
