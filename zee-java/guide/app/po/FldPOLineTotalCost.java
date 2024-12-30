package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPOLineTotalCost extends MboValueAdapter {

	public FldPOLineTotalCost(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		double totalprice, unitcost;
		Mbo mbo = this.getMboValue().getMbo();
		double totalcost = mbo.getDouble("udtotalcost");// 含税总价
		double orderqty = mbo.getDouble("orderqty");// 数量
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}

		double percentTaxRate = taxrate / 100;// 税率

		double linecost = totalcost / (1 + percentTaxRate);// 不含税总价

		if (orderqty == 0) {
			totalprice = 0;
			unitcost = 0;
		} else {
			totalprice = totalcost / orderqty;// 含税单价
			unitcost = linecost / orderqty;// 不含税单价
		}

		double tax1 = totalcost - linecost;// 税额

		mbo.setValue("udtotalprice", totalprice, 11L);
		mbo.setValue("unitcost", unitcost, 2L);
		mbo.setValue("tax1", tax1, 11L);
		
//		if (totalprice > 0) {
//			mbo.setValue("udpredicttaxprice", totalprice, 2L);
//		}
		//ZEE-生产库不走此逻辑，已将寄售库、生产库金额字段逻辑绑定在poline.storeloc上，53-58
		MboRemote owner = mbo.getOwner();
		if (owner!=null) {
			String udcompany = owner.getString("udcompany");		
			if (totalprice > 0 && !udcompany.equalsIgnoreCase("ZEE")) {
				mbo.setValue("udpredicttaxprice", totalprice, 2L);
			}
		}
	}
}
