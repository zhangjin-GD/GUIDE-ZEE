package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldInvUseLineQuantity;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldInvUseLineQuantity extends FldInvUseLineQuantity {

	public UDFldInvUseLineQuantity(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		if (parent != null && parent instanceof UDInvUse) {
			String udapptype = parent.getString("udapptype");
			if (udapptype != null
					&& ("MATUSECS".equalsIgnoreCase(udapptype) || "MATRETCS".equalsIgnoreCase(udapptype))) {
				double taxrate = 0;
				double udtotalprice = mbo.getDouble("udtotalprice");
				double quantity = mbo.getDouble("quantity");
				MboSetRemote taxSet = mbo.getMboSet("UDTAX");
				if (taxSet != null && !taxSet.isEmpty()) {
					MboRemote tax = taxSet.getMbo(0);
					taxrate = tax.getDouble("taxrate");
				}
				double udtotalcost = udtotalprice * quantity;
				double percentTaxRate = taxrate / 100;
				double udlinecost = udtotalcost / (1 + percentTaxRate);
				double udtax1 = udlinecost * percentTaxRate;
				double udunitcost;
				if (quantity == 0) {
					udunitcost = 0;
				} else {
					udunitcost = udlinecost / quantity;
				}
				mbo.setValue("udtotalcost", udtotalcost, 11L);
				mbo.setValue("udunitcost", udunitcost, 11L);
				mbo.setValue("udlinecost", udlinecost, 11L);
				mbo.setValue("udtax1", udtax1, 11L);
			}
		}
	}
}
