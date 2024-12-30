package guide.webclient.beans.item;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class MatTransOcpDateBean extends DataBean {

	public int inStock() throws MXException, RemoteException {
		super.addrow();
		MboRemote mbo = getMbo();
		mbo.setValue("issuetype", "RECEIPT", 11L);
		mbo.setValue("orderqty", 1, 2L);
		return 1;
	}

	public int outStock() throws MXException, RemoteException {
		super.addrow();
		MboRemote mbo = getMbo();
		mbo.setValue("issuetype", "RETURN", 11L);
		mbo.setValue("orderqty", -1, 2L);
		return 1;
	}

}
