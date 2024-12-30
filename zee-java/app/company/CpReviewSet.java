package guide.app.company;

import java.rmi.RemoteException;

import guide.app.common.UDMboSet;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class CpReviewSet extends UDMboSet implements MboSetRemote {

	public CpReviewSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new CpReview(ms);
	}

}
