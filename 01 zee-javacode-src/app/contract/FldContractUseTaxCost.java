package guide.app.contract;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldContractUseTaxCost extends MboValueAdapter {

	public FldContractUseTaxCost(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = this.getMboValue().getMbo();
		double usetaxcost = 0.0d;
		MboSetRemote poLineSet = mbo.getMboSet("POLINE");
		if (!poLineSet.isEmpty() && poLineSet.count() > 0) {
			usetaxcost = poLineSet.sum("udtotalcost");
		}
		mbo.setValue("usetaxcost", usetaxcost, 11L);
	}
}
