package guide.webclient.beans.gpm;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.gpm.UDGpm;
import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelCreateWoPmDataBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		String flag = "";
		String wonumList = "";
		for (int i = 0; i < vector.size(); i++) {
			flag = null;
			MboRemote mr = (MboRemote) vector.elementAt(i);
			flag = ((UDGpm) mr).addWoPm(null, null);
			if (flag != null && !flag.equalsIgnoreCase("")) {
				wonumList += flag + ",";
			}
		}
		if (!wonumList.isEmpty()) {
			wonumList = wonumList.substring(0, wonumList.length() - 1);
			Object[] obj = { wonumList };
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "guide", "1134", obj);
		}
		return super.execute();
	}

}
