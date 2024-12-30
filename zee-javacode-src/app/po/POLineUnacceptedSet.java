package guide.app.po;

import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class POLineUnacceptedSet extends MboSet implements MboSetRemote {
    public POLineUnacceptedSet(MboServerInterface ms) throws RemoteException {
        super(ms);
    }

    @Override
    protected Mbo getMboInstance(MboSet mboSet) throws MXException, RemoteException {
        return new POLineUnaccepted(mboSet);
    }
}
