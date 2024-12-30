package guide.webclient.beans.common;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPersonDataBean extends DataBean{

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			MboSetRemote asgnpersonSet = owner.getMboSet("UDASGNPERSON");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote asgnperson = asgnpersonSet.add();
				asgnperson.setValue("ownertable", owner.getName(),11L);
				asgnperson.setValue("ownerid", owner.getUniqueIDValue(),11L);
				asgnperson.setValue("personid", mr.getString("personid"),11L);
			}
		}
		return super.execute();
	}
}
