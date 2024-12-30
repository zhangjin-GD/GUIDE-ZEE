package guide.app.contract;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldContractSpentTaxCost extends MboValueAdapter {

	public FldContractSpentTaxCost(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = this.getMboValue().getMbo();
		double limitmaxcost = mbo.getDouble("limitmaxcost");
		double usetaxcost = mbo.getDouble("usetaxcost");
		double spenttaxcost = limitmaxcost - usetaxcost;
		mbo.setValue("spenttaxcost", spenttaxcost, 11L);
	}
}
