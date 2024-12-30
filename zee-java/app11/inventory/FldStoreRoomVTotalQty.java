package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldStoreRoomVTotalQty extends MboValueAdapter {

	public FldStoreRoomVTotalQty(MboValue mbv) throws MXException {
		super(mbv);
	}
	
	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote invEntorySet = mbo.getMboSet("UDINVENTORY");
		double totalqty = 0;
		if (!invEntorySet.isEmpty() && invEntorySet.count() > 0) {
			totalqty = invEntorySet.sum("totalqty");
		}
		this.getMboValue().setValue(totalqty, 11L);
	}
}
