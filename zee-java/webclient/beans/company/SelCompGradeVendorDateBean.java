package guide.webclient.beans.company;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelCompGradeVendorDateBean extends DataBean {
	
	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			MboSetRemote compGradeVendorSet = owner.getMboSet("UDCOMPGRADEVENDOR");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote compGradeVendor = compGradeVendorSet.add();
				compGradeVendor.setValue("linenum", i+1, 11L);
				compGradeVendor.setValue("cgnum", owner.getString("cgnum"), 11L);
				compGradeVendor.setValue("vendor", mr.getString("company"), 11L);
			}
		}
		return super.execute();
	}
	
}
