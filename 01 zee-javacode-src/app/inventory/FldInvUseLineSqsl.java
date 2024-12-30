package guide.app.inventory;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class FldInvUseLineSqsl extends MboValueAdapter {
    public FldInvUseLineSqsl(MboValue mbv) throws MXException {
        super(mbv);
    }

    @Override
    public void initValue() throws MXException, RemoteException {
        super.initValue();
        MboRemote mbo = getMboValue().getMbo();
        MboSetRemote mboSet = mbo.getMboSet("udinvbalcurbal");

        double curbal = 0;
        if (!mboSet.isEmpty() && mboSet.count() > 0) {
            curbal = mboSet.getMbo(0).getDouble("curbal");
        }
        double quantity = mbo.getDouble("quantity");
        mbo.setValue("udsqsl", curbal - quantity, 11L);
    }
}
