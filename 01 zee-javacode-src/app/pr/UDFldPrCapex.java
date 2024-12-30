package guide.app.pr;

import java.rmi.RemoteException;

import guide.app.common.FldProjectNum;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class UDFldPrCapex extends MboValueAdapter {

	public UDFldPrCapex() {
		super();
	}

	public UDFldPrCapex(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String udcapex = mbo.getString("udcapex");
		if (udcapex.equalsIgnoreCase("N")) {
			mbo.setValue("udprojectnum", "", 11L);
		}
		String udprojectnum = mbo.getString("udprojectnum");
		MboSetRemote lineSet = mbo.getMboSet("PRLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; i < lineSet.count(); i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("udcapex", udcapex, 11L);
				line.setValue("udprojectnum", udprojectnum, 11L);
			}
		}

	}
}

