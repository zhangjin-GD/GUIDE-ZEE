package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import guide.app.inventory.MatDspo;
import guide.app.inventory.MatDspoLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelMatReturnToMatDspoLineDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean lineTable = app.getDataBean("matdspoline_line_table");
		Vector<MboRemote> vector = this.getSelection();
		MatDspo owner = (MatDspo) lineTable.getParent().getMbo();
		String personid = owner.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDMATDSPOLINEITEM");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MatDspoLine line = (MatDspoLine) lineSet.addAtEnd();
				line.setValue("udmatreturnid", mr.getInt("udmatreturnid"), 11L);
				line.setValue("itemnum", mr.getString("itemnum"), 11L);
				line.setValue("linetype", "ITEM", 11L);
				line.setValue("description", mr.getString("description"), 11L);
				line.setValue("returntype", mr.getString("returntype"), 11L);
				line.setValue("returntype1", mr.getString("returntype1"), 11L);
				line.setValue("orderqty", mr.getDouble("requeqty"), 2L);
				line.setValue("unitcost", mr.getDouble("invuseline.unitcost"), 2L);
				line.setValue("location", mr.getString("location"), 11L);
				line.setValue("remarks", mr.getString("remarks"), 11L);
				line.setValue("enterby", personid, 11L);
				line.setValue("enterdate", currentDate, 11L);
			}
		}
		lineTable.reloadTable();
		return 1;
	}
}
