package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldVendorAwardedCost extends MboValueAdapter {

	public FldVendorAwardedCost(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double totalCost = 0.00d;
		double orderQty = 0.00d;
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote quotationLineSet = mbo.getMboSet("UDISAWARDED");
		if (!quotationLineSet.isEmpty() && quotationLineSet.count() > 0) {
			totalCost = quotationLineSet.sum("udtotalcost");
			orderQty = quotationLineSet.sum("orderqty");
		}
		mbo.setValue("udAwardedcost", totalCost, 11L);
		mbo.setValue("udAwardedQty", orderQty, 11L);
	}
	
}
