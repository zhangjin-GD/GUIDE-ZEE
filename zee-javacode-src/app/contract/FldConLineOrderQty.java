package guide.app.contract;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldConLineOrderQty extends MboValueAdapter {

	public FldConLineOrderQty(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0.0D;
		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		MboSetRemote taxSet = mbo.getMboSet("TAX");
		if (!taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		double percentTaxRate = taxrate / 100;
		// 不含税价=含税价÷（1+税率）
		double orderqty = mbo.getDouble("orderqty");// 含税单价
		double totalunitcost = mbo.getDouble("totalunitcost");// 含税单价
		double unitcost = totalunitcost / (1 + percentTaxRate);
		double totallinecost = orderqty * totalunitcost;
		double linecost = orderqty * unitcost;
		double tax1 = totallinecost - linecost;
		mbo.setValue("totallinecost", totallinecost, 11L);
		mbo.setValue("unitcost", unitcost, 11L);
		mbo.setValue("linecost", linecost, 11L);
		mbo.setValue("tax1", tax1, 11L);
		if (parent != null && parent instanceof Contract) {
			parent.getMboSet("UDCONTRACTLINE").resetQbe();
			double totallinecostsum = mbo.getThisMboSet().sum("totallinecost");
			double linecostsum = mbo.getThisMboSet().sum("linecost");
			parent.setValue("totalcost", totallinecostsum, 11L);
			parent.setValue("pretaxtotal", linecostsum, 11L);
		}
	}
}
