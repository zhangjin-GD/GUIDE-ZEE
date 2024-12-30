package guide.app.matrecord;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class MatrecordSet extends MboSet implements MboSetRemote {

	public MatrecordSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet var1) throws MXException, RemoteException {
		return new Matrecord(var1);
	}

}
