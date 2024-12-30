package guide.webclient.beans.po;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPOStorelocDateBean extends DataBean {

	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1034");
		}
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboSetRemote vpoSet = mbo.getMboSet("UDVPO");
		if (vpoSet != null && !vpoSet.isEmpty()) {
			MboRemote vpr = vpoSet.getMbo(0);
			if (!vpr.isNull("storeloc")) {
				String newValue = vpr.getString("storeloc");
				String personId = mbo.getUserInfo().getPersonId();
				Date currentDate = MXServer.getMXServer().getDate();
				MboSetRemote thispoLineSet = this.getMbo().getMboSet("POLINERECEIVEDQTY");
				Vector<MboRemote> vector = thispoLineSet.getSelection();
				for (int i = 0; i < vector.size(); i++) {
					MboRemote mr = (MboRemote) vector.elementAt(i);
					int polineid = mr.getInt("polineid");
					String oldValue = mr.getString("storeloc");
					MboSetRemote polineSet = mbo.getMboSet("POLINE");
					if (polineSet != null && !polineSet.isEmpty()) {
						for (int j = 0; polineSet.getMbo(j) != null; j++) {
							MboRemote poline = polineSet.getMbo(j);
							int polid = poline.getInt("polineid");
							if (polid == polineid) {
								poline.setValue("storeloc", newValue, 2L);
								if (!oldValue.isEmpty() && !newValue.isEmpty()) {
									MboSetRemote changeHisSet = vpr.getMboSet("UDCHANGEHIS");
									MboRemote changeHis = changeHisSet.add(11L);
									changeHis.setValue("ownerid", polineid, 11L);
									changeHis.setValue("ownertable", "POLINE", 11L);
									changeHis.setValue("attributename", "STORELOC", 11L);
									changeHis.setValue("oldvalue", oldValue, 11L);
									changeHis.setValue("newvalue", newValue, 11L);
									changeHis.setValue("reason", "库房变更", 11L);
									changeHis.setValue("changeby", personId, 11L);
									changeHis.setValue("changedate", currentDate, 11L);
								}
							}
						}
					}
				}
			}
		}
		return super.execute();
	}
}
