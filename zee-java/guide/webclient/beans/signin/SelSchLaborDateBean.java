package guide.webclient.beans.signin;

import java.rmi.RemoteException;
import java.util.Vector;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelSchLaborDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.app.getAppBean().getMbo();
		DataBean db = app.getDataBean("1666093286728");
		MboRemote linembo = db.getMbo(db.getCurrentRow());
		if (owner != null && linembo != null) {
			MboSetRemote mboSet = linembo.getMboSet("UDSCHLABOR");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote mbo = mboSet.add();
				mbo.setValue("schplannum", owner.getString("schplannum"), 11L);
				mbo.setValue("parent", ""+linembo.getInt("udschcrewid"), 11L);
				mbo.setValue("personid", mr.getString("personid"), 11L);
				mbo.setValue("description", mr.getString("displayname"), 11L);
			}
		}
		return super.execute();
	}
}
