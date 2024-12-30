package guide.app.item;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class UDItemcpVenSet extends MboSet implements MboSetRemote{

	public UDItemcpVenSet(MboServerInterface ms) throws RemoteException {
		super(ms);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Mbo getMboInstance(MboSet arg0) throws MXException,
			RemoteException {
		// TODO Auto-generated method stub
		return new UDFldUdItemcpVen(arg0);
	}

}
