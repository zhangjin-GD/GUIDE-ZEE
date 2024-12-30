package guide.app.signin;

import guide.app.common.UDMboSet;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class UDSchPlanSet extends UDMboSet implements MboSetRemote {

	public UDSchPlanSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDSchPlan(ms);
	}

}
