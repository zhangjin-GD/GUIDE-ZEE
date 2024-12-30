package guide.webclient.beans.pr;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.common.CommonUtil;
import guide.app.pr.UDPR;
import guide.app.pr.UDPRLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPRImpItemsDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		UDPR owner = (UDPR) this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		Vector<MboRemote> vector = this.getSelection();
		if (owner != null) {
			String udcompany = owner.getString("udcompany");
			MboSetRemote prlineSet = owner.getMboSet("prline");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				UDPRLine prline = (UDPRLine) prlineSet.addAtEnd();
				prline.setValue("itemnum", mr.getString("itemnum"), 2L);
				String tax1code = CommonUtil.getValue("UDDEPT", "type='COMPANY' and deptnum='"+udcompany+"'", "TAX1CODE");
				prline.setValue("tax1code", tax1code, 2L);
				prline.setValue("orderqty", mr.getDouble("orderqty"), 2L);
				prline.setValue("udtotalprice", mr.getDouble("unitcost"), 2L);
				if (!mr.isNull("storeloc")) {
					prline.setValue("storeloc", mr.getString("storeloc"), 2L);
				}
				if (!mr.isNull("enterby")) {
					prline.setValue("requestedby", mr.getString("enterby"), 11L);
				}
				prline.setValue("remark", mr.getString("remark"), 11L);
				prline.setValue("udesttime", mr.getString("esttime"), 11L);
				mr.setValue("prlineid", prline.getInt("prlineid"), 11L);
			}
		}
		return super.execute();
	}
}
