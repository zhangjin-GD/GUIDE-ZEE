package guide.webclient.beans.pr;

import java.rmi.RemoteException;
import java.util.LinkedHashSet;
import java.util.Set;

import guide.app.pr.UDPR;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class PRVendorTableBean extends DataBean {

	public int selPRVendor() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("selPRVendor");
		return 1;
	}

	public int udaddpos() throws MXException, RemoteException {
		UDPR owner = (UDPR) this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("udaddpos");
		return 1;
	}

	public int udaddpo() throws MXException, RemoteException {
		UDPR owner = (UDPR) this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}

		Set<String> poHash = new LinkedHashSet<String>();

		MboSetRemote prlineSet = owner.getMboSet("PRLINE");
		for (int i = 0; prlineSet.getMbo(i) != null; i++) {
			MboRemote prline = prlineSet.getMbo(i);
			if (!prline.isNull("ponum")) {
				String ponum = prline.getString("ponum");
				poHash.add(ponum);
			}
		}

		if (poHash.size() > 0) {
			StringBuffer buf = new StringBuffer();
			for (String ponum : poHash) {
				buf.append(ponum).append(",");
			}
			String params = buf.substring(0, buf.length() - 1);
			Object[] obj = { params };
			throw new MXApplicationException("guide", "1187", obj);
		}

		if (owner.isNull("vendor")) {
			throw new MXApplicationException("guide", "1188");
		}

		String ponum = owner.addPOsFromPR();
		this.app.getAppBean().save();
		Object[] obj = { ponum };
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "guide", "1187", obj);
		return 1;

	}
}
