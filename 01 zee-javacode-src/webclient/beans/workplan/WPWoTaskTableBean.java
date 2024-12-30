package guide.webclient.beans.workplan;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class WPWoTaskTableBean extends DataBean {

	public void udaddwo() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
	}

	public void createWO2() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		HashSet<String> hashSet = new HashSet<String>();
		Vector<MboRemote> selectedMbos = this.getMboSetRemote().getSelection();
		if (selectedMbos.size() == 0) {
			throw new MXApplicationException("guide", "1081");
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < selectedMbos.size(); i++) {
			MboRemote mr = (MboRemote) selectedMbos.elementAt(i);
			String assetnum = mr.getString("assetnum");
			if (!mr.isNull("wonum")) {
				buf.append(mr.getString("wonum")).append(",");
			}
			hashSet.add(assetnum);
		}
		if (hashSet.size() > 1) {
			throw new MXApplicationException("guide", "1080");
		}
		if (buf.length() > 0) {
			// 去掉逗号
			String params = buf.substring(0, buf.length() - 1);
			Object[] obj = { params };
			throw new MXApplicationException("guide", "1183", obj);
		}
	}
}
