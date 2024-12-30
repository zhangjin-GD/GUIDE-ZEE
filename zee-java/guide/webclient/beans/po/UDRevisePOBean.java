package guide.webclient.beans.po;

import java.rmi.RemoteException;

import guide.app.po.UDPO;
import psdi.mbo.MboRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class UDRevisePOBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		String ponum = this.getMbo().getString("udrevponum");
		String podesc = this.getMbo().getString("revdescription");
		int revnum = this.getMbo().getInt("udrevnum");
		if (getMbo().isNull("udrevponum")) {
			throw new MXApplicationException("guide", "1125");
		}
		if (getMbo().isNull("revdescription")) {
			throw new MXApplicationException("guide", "1126");
		}
		if (getMbo().isNull("udrevnum")) {
			throw new MXApplicationException("guide", "1127");
		}
		UDPO mbo = (UDPO) parent.getMbo();
		MboRemote po = mbo.revPO(ponum, podesc, revnum);
		long uniqueid = po.getUniqueIDValue();
		parent.save();
		parent.getMboForUniqueId(uniqueid);
		return 1;
	}
}
