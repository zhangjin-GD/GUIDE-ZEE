package guide.app.assetcatalog;

import java.rmi.RemoteException;

import psdi.app.assetcatalog.ClassStructure;
import psdi.app.assetcatalog.ClassStructureRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class CustClassStructure extends ClassStructure implements ClassStructureRemote {

	public CustClassStructure(MboSet ms) throws RemoteException, MXException {
		super(ms);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (this.getMboValue("uditemtype").isModified()) {
			String uditemtype = this.getString("uditemtype");
			MboSetRemote classificationSet = this.getMboSet("CLASSIFICATION_ADD");// 原关系
			if (classificationSet != null && !classificationSet.isEmpty()) {
				MboRemote classification = classificationSet.getMbo(0);
				classification.setValue("uditemtype", uditemtype, 11L);
			}
		}
	}
}
