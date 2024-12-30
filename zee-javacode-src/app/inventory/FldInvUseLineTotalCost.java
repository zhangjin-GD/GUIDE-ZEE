package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldInvUseLineTotalCost extends MboValueAdapter {

	public FldInvUseLineTotalCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		double taxrate = 0;
		Mbo mbo = this.getMboValue().getMbo();
		double udtotalcost = mbo.getDouble("udtotalcost");// 含税总计
		double quantity = mbo.getDouble("quantity");// 数量
		MboSetRemote taxSet = mbo.getMboSet("UDTAX");
		if (taxSet != null && !taxSet.isEmpty()) {
			MboRemote tax = taxSet.getMbo(0);
			taxrate = tax.getDouble("taxrate");
		}
		double percentTaxRate = taxrate / 100;
		double udlinecost = udtotalcost / (1 + percentTaxRate);// 不含税总价
		double udtax1 = udlinecost * percentTaxRate;// 税额
		double udunitcost, udtotalprice;
		if (quantity == 0) {
			udunitcost = 0;
			udtotalprice = 0;
		} else {
			udunitcost = udlinecost / quantity; // 不含税单价
			udtotalprice = udtotalcost / quantity;
		}
		mbo.setValue("udtotalprice", udtotalprice, 11L);
		mbo.setValue("udunitcost", udunitcost, 11L);
		mbo.setValue("udlinecost", udlinecost, 11L);
		mbo.setValue("udtax1", udtax1, 11L);
	}
}
