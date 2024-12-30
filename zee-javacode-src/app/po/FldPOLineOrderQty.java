package guide.app.po;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurOrderQty;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPOLineOrderQty extends FldPurOrderQty {

	public FldPOLineOrderQty(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		Mbo mbo = this.getMboValue().getMbo();
		double unitcost = mbo.getDouble("unitcost");// 不含税单价
		double orderqty = mbo.getDouble("orderqty");
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}

		double percentTaxRate = taxrate / 100;// 税率

		double totalprice = unitcost * (1 + percentTaxRate);// 含税单价

		double totalcost = totalprice * orderqty; // 含税总价

		double linecost = unitcost * orderqty;// 不含税总价

		double tax1 = totalcost - linecost;// 税额

		mbo.setValue("udtotalprice", totalprice, 11L);
		mbo.setValue("udtotalcost", totalcost, 11L);
		mbo.setValue("tax1", tax1, 11L);

		if (totalprice > 0) {
			mbo.setValue("udpredicttaxprice", totalprice, 2L);
		}
	}
}
