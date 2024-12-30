package guide.webclient.beans.pr;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.common.CommonUtil;
import guide.app.pr.PRImp;
import guide.app.pr.UDPR;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelInventoryDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean prLine = app.getDataBean("prlines_prlines_table");

		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = prLine.getParent().getMbo();

		if (owner != null && owner instanceof UDPR) {
			String udcompany = owner.getString("udcompany");
			MboSetRemote prlineSet = owner.getMboSet("prline");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote prline = prlineSet.add();
				prline.setValue("itemnum", mr.getString("itemnum"), 2L);
				String tax1code = CommonUtil.getValue("UDDEPT", "type='COMPANY' and deptnum='"+udcompany+"'", "TAX1CODE");
				prline.setValue("tax1code", tax1code, 2L);
				prline.setValue("storeloc", mr.getString("location"), 2L);
			}
		}

		if (owner != null && owner instanceof PRImp) {
			MboSetRemote prlineSet = owner.getMboSet("udprimpline");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote prline = prlineSet.add();
				prline.setValue("itemnum", mr.getString("itemnum"), 2L);
				prline.setValue("storeloc", mr.getString("location"), 2L);
			}
		}
		prLine.reloadTable();
		return 1;
	}
}
