package guide.app.project;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class FldProconLinePayCost extends MboValueAdapter {

	public FldProconLinePayCost(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();

		Mbo mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();

		if (this.getMboValue().isNull()) {
			this.getMboValue().setValue(0.0D, 11L);
		}

		if (parent != null && parent instanceof ProCon) {
			double paycostSum = mbo.getThisMboSet().sum("paycost");
			double totalcost = parent.getDouble("totalcost");

			if (paycostSum > totalcost) {
				Object[] obj = { "温馨提示：已超出项目合同的税后金额！" };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
		}
	}
}
