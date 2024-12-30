package guide.workflow;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WFActionCheckPoBudget implements ActionCustomClass {

	@Override
	public void applyCustomAction(MboRemote mbo, Object[] obj) throws MXException, RemoteException {
		// PCT 添加操作 校验 PO 预算金额
		double totalcost = mbo.getDouble("TOTALCOST");
		MboSetRemote polineSet = mbo.getMboSet("POLINE");
		if (!polineSet.isEmpty() && polineSet.count() > 0) {
			MboRemote poline = polineSet.getMbo(0);
			double aDouble = poline.getDouble("UDBUDGET.PURRECOST");
			if (totalcost > aDouble) {
				Object[] objects = { "报错报错报错报错报错报错报错报错报错报错报错报错报错报错报错报错报错!" };
				throw new MXApplicationException("instantmessaging", "tsdimexception", objects);
			}
		}

	}

}
