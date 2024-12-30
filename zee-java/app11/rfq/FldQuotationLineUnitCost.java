package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.common.purchasing.FldPurUnitCost;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldQuotationLineUnitCost extends FldPurUnitCost {

	public FldQuotationLineUnitCost(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
	}
}
