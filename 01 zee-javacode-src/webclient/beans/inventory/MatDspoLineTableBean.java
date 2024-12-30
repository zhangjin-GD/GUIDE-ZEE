package guide.webclient.beans.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class MatDspoLineTableBean extends DataBean {

	@Override
	public int addrow() throws MXException {
		super.addrow();
		try {
			MboRemote mbo = getMbo();
			mbo.setValue("linetype", "SERVICE", 11L);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
		return 1;
	}
}
