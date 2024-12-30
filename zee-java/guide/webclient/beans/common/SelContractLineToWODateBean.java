package guide.webclient.beans.common;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.workorder.UDWO;
import guide.app.workorder.WOContractLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelContractLineToWODateBean extends DataBean {

	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo != null && mbo instanceof UDWO) {
			String udrepairtype = mbo.getString("udrepairtype");
			if ("INSIDE".equalsIgnoreCase(udrepairtype)) {
				throw new MXApplicationException("guide", "1089");
			}
		}
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		MboRemote owner = this.getParent().getMbo();
		Vector<MboRemote> vector = this.getSelection();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDWOCONTRACTLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				WOContractLine line = (WOContractLine) lineSet.add();
				line.setValue("contractlineid", mr.getInt("udcontractlineid"), 11L);
				line.setValue("linetype", mr.getString("linetype"), 11L);
				line.setValue("description", mr.getString("description"), 11L);
				line.setValue("orderunit", mr.getString("orderunit"), 11L);
				line.setValue("orderqty", mr.getDouble("orderqty"), 2L);
				line.setValue("unitcost", mr.getDouble("totalunitcost"), 2L);
			}
		}
		return super.execute();
	}
}
