package guide.app.pr;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurUnitCost;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrLineUnitCost extends FldPurUnitCost {

	public FldPrLineUnitCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
}
