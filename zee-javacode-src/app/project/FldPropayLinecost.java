package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldPropayLinecost extends MboValueAdapter {

	public FldPropayLinecost(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		double linecost = this.getMboValue().getDouble();
		double totallinetaxcost = mbo.getDouble("totallinetaxcost");

		if (totallinetaxcost > linecost) {
			Object[] obj = { "温馨提示：已超出需要支付的总金额！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}

		double residualcost = linecost - totallinetaxcost;
		mbo.setValue("residualcost", residualcost, 11L);
	}
}
