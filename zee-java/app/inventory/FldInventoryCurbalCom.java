package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldInventoryCurbalCom extends MboValueAdapter {

	public FldInventoryCurbalCom(MboValue mbv) throws MXException {
		super(mbv);
	}

	
	public void initValue() throws MXException, RemoteException {
		double curBal = 0.0D;
		super.initValue();
		MboValue curBalTotalFldValue = this.getMboValue();
		UDInventory inv = (UDInventory) curBalTotalFldValue.getMbo();
		if (!inv.isZombie() && !inv.toBeAdded()) {
			curBal = inv.calculateCurrentBalanceCom();
			curBalTotalFldValue.setValue(curBal, 11L);
		}
	}
}
