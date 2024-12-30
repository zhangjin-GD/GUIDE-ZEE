package guide.app.inventory;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class FldInvUseVTotalQty extends MboValueAdapter {

	public FldInvUseVTotalQty(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = getMboValue().getMbo();
		MboSetRemote mboSet = mbo.getMboSet("INVUSELINE");
		if (!mboSet.isEmpty() && mboSet.count() > 0) {
			double quantity = mboSet.sum("quantity");
			mbo.setValue("udvtotalqty", quantity, 11L);
		}
	}
}
