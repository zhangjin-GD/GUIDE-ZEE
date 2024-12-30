package guide.webclient.beans.workplan;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;

public class WoreTaskAppBean extends AppBean {

	public int udaddwo() throws RemoteException, MXException {
		MboRemote owner = this.app.getAppBean().getMbo();
		if (owner.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
		// 调用dialog
		this.clientSession.loadDialog("udaddwo");
		return 1;
	}

}
