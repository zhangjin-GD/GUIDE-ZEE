package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class UDFldPoCapex extends MboValueAdapter{
	public UDFldPoCapex() {
		super();
	}
	public UDFldPoCapex(MboValue mbv) {
		super(mbv);
	}
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String udcapex = mbo.getString("udcapex");
		MboSetRemote lineSet = mbo.getMboSet("POLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; i < lineSet.count(); i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("udcapex", udcapex, 11L);
			}
            if (udcapex.equalsIgnoreCase("N")) {
                mbo.setValue("udprojectnum", "", 2L);
        }
		}
	}
		
}
