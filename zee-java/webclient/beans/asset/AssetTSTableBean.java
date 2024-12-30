package guide.webclient.beans.asset;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class AssetTSTableBean extends DataBean {

	public int udupper() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("udupper");
		return 1;
	}

	public int udlower() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("udlower");
		return 1;
	}
}
