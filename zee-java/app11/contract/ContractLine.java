package guide.app.contract;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class ContractLine extends Mbo implements MboRemote {

	public ContractLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof Contract) {
			String gconnum = parent.getString("gconnum");
			String tax1code = parent.getString("vendor.tax1code");
			int linenum = (int) getThisMboSet().max("linenum") + 1;

			this.setValue("gconnum", gconnum, 11L);
			this.setValue("linenum", linenum, 11L);
			this.setValue("tax1code", tax1code, 2L);
			this.setValue("orderqty", 1, 2L);
			this.setValue("totalunitcost", 0, 2L);
		}
	}
}
