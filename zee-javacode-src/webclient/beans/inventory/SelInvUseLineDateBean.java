package guide.webclient.beans.inventory;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

public class SelInvUseLineDateBean extends DataBean {

	public synchronized int execute() throws MXException, RemoteException {
		DataBean lineTable = this.app.getDataBean("invuseline_table");
		Vector vector = getSelection();
		MboRemote owner = lineTable.getParent().getMbo();
		String personid = owner.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDMATDSPOLINEDISCARD");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote line = lineSet.addAtEnd();
				line.setValue("itemnum", mr.getString("itemnum"), 11L);
				line.setValue("linetype", "DISCARD", 11L);
				line.setValue("description", mr.getString("item.description"), 11L);
				line.setValue("orderqty", mr.getString("quantity"), 11L);
				line.setValue("unitcost", mr.getDouble("unitcost"), 2L);
				line.setValue("location", mr.getString("location"), 11L);
				line.setValue("enterby", personid, 11L);
				line.setValue("enterdate", currentDate, 11L);
			}
		}
		lineTable.reloadTable();
		return 1;
	}
}
