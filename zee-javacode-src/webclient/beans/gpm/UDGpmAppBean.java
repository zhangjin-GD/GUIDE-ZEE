package guide.webclient.beans.gpm;

import guide.app.gpm.UDGpm;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;

public class UDGpmAppBean extends AppBean {

	public int CREATEWOPM() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		String wonum = ((UDGpm) mbo).addWoPm(null, null);
		Object[] obj = { wonum };
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "guide", "1134", obj);
		this.app.getAppBean().save();
		return 1;
	}

}
