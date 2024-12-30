package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.common.action.ActionCustomClass;
import psdi.mbo.MboRemote;
import psdi.util.MXException;

public class WFActionCheckInvUseLot implements ActionCustomClass {

	@Override
	public void applyCustomAction(MboRemote mbo, Object[] arg1) throws MXException, RemoteException {
		UDInvUse invUse = (UDInvUse) mbo;
		invUse.checkLot();
	}

}
