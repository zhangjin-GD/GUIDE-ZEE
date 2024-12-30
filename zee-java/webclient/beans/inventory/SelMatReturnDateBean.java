package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import guide.app.inventory.MatReturn;
import guide.app.inventory.UDInvUse;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelMatReturnDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		DataBean lineTable = app.getDataBean("matreturn_line_table");
		Vector<MboRemote> vector = this.getSelection();
		UDInvUse owner = (UDInvUse) lineTable.getParent().getMbo();
		String personid = owner.getUserInfo().getPersonId();
		String udcompany = owner.getString("udcompany");
		Date currentDate = MXServer.getMXServer().getDate();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDMATRETURN");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MatReturn line = (MatReturn) lineSet.add();
				line.setValue("udcompany", udcompany, 11L);
				line.setValue("invuselineid", mr.getInt("invuselineid"), 11L);
				line.setValue("invuselinenum", mr.getInt("invuselinenum"), 11L);
				line.setValue("itemnum", mr.getString("itemnum"), 11L);
				line.setValue("description", mr.getString("description"), 11L);
				line.setValue("location", mr.getString("fromstoreloc"), 11L);
				line.setValue("issurqty", mr.getDouble("quantity"), 11L);
//				line.setValue("returqty", 0, 11L);
				line.setValue("returnby", personid, 11L);
				line.setValue("returndate", currentDate, 11L);
			}
		}

		lineTable.reloadTable();
		return 1;
	}
}
