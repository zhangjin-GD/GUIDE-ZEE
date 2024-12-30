package guide.app.pr;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPrSerType extends MboValueAdapter {

	public FldPrSerType(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = getMboValue().getMbo();
		String prSerType = mbo.getString("udprsertype");
		String apptype = mbo.getString("udapptype");
		String[] attrs = { "udreason", "udtechspec", "accepmethod" };
		if ("PRSER".equalsIgnoreCase(apptype)) {
			MboSetRemote prlineSet = mbo.getMboSet("PRLINE");
			if ("A".equalsIgnoreCase(prSerType)) {
				mbo.setFieldFlag(attrs, 128L, true);
				for (int i = 0; prlineSet.getMbo(i) != null; i++) {
					MboRemote prline = prlineSet.getMbo(i);
					prline.setFieldFlag("assetnum", 128L, true);
				}
			} else {
				mbo.setFieldFlag(attrs, 128L, false);
				for (int i = 0; prlineSet.getMbo(i) != null; i++) {
					MboRemote prline = prlineSet.getMbo(i);
					prline.setFieldFlag("assetnum", 128L, false);
				}
			}
		}
	}
}
