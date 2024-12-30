package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.NonPersistentMboRemote;
import psdi.util.MXException;

public interface UDVIsReturnRemote extends NonPersistentMboRemote {

	MboRemote createReturnReceipt(MboSetRemote mboSet) throws MXException, RemoteException;
}
