package guide.app.project;

import java.rmi.RemoteException;

import psdi.app.financial.FldTaxCode;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPropaylineTax1code extends FldTaxCode {

	public FldPropaylineTax1code(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0.0D;
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		double linetaxcost = mbo.getDouble("linetaxcost");
		MboSetRemote taxSet = mbo.getMboSet("TAX");
		if (!taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		double percentTaxRate = taxrate / 100;
		double linecost = linetaxcost / (1 + percentTaxRate);
		double taxcost = linecost * percentTaxRate;
		mbo.setValue("linecost", linecost, 11L);
		mbo.setValue("taxcost", taxcost, 11L);

		if (parent != null && parent instanceof ProPay) {
			parent.getMboSet("UDPROPAYLINE").resetQbe();
			double totallinetaxcost = mbo.getThisMboSet().sum("linetaxcost");
			double totallinecost = mbo.getThisMboSet().sum("linecost");
			double totaltaxcost = mbo.getThisMboSet().sum("taxcost");
			
			parent.setValue("totallinetaxcost", totallinetaxcost, 2L);
			parent.setValue("totallinecost", totallinecost, 2L);
			parent.setValue("totaltaxcost", totaltaxcost, 2L);
		}
	}
}
