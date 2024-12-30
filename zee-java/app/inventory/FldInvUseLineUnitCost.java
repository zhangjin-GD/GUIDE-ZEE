package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldInvUseLineUnitCost extends MboValueAdapter {

	public FldInvUseLineUnitCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		Mbo mbo = this.getMboValue().getMbo();
		double unitcost = mbo.getDouble("udunitcost");
		double quantity = mbo.getDouble("quantity");
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		double linecost = unitcost * quantity;
		double percentTaxRate = taxrate / 100;
		double tax1 = linecost * percentTaxRate;
		double totalcost = linecost + tax1;
		double totalprice;
		if (quantity == 0) {
			totalprice = 0;
		} else {
			totalprice = totalcost / quantity;
		}
		mbo.setValue("udlinecost", linecost, 11L);
		mbo.setValue("udtotalprice", totalprice, 11L);
		mbo.setValue("udtotalcost", totalcost, 11L);
		mbo.setValue("udtax1", tax1, 11L);
	}
}
