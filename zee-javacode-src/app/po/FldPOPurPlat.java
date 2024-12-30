package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPOPurPlat extends MboValueAdapter {

	public FldPOPurPlat(MboValue mbv) {
		super(mbv);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		if (mbo != null && mbo instanceof UDPO) {
			String apptype = mbo.getString("udapptype");
			String udpurplat = mbo.getString("udpurplat");
			if ("POFIX".equalsIgnoreCase(apptype) || "POMAT".equalsIgnoreCase(apptype)
					|| "POSER".equalsIgnoreCase(apptype)) {
				MboSetRemote polineSet = mbo.getMboSet("POLINE");
				if ("CON".equalsIgnoreCase(udpurplat)) {
					for (int i = 0; polineSet.getMbo(i) != null; i++) {
						MboRemote poline = polineSet.getMbo(i);
						poline.setFieldFlag("udcontractlineid", 128L, true);
					}
				} else {
					for (int i = 0; polineSet.getMbo(i) != null; i++) {
						MboRemote poline = polineSet.getMbo(i);
						poline.setFieldFlag("udcontractlineid", 128L, false);
					}
				}
			}
		}
	}
}
