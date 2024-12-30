package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWoreTaskToWoDateBean extends DataBean {

	public synchronized int execute() throws MXException, RemoteException {
		DataBean table = app.getDataBean("udgwotask_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = table.getParent().getMbo();
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDGWOTASK");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote line = lineSet.addAtEnd();
				String wojo1 = mr.getString("wojo1");
				String wodesc = mr.getString("wodesc");
				String wojo2 = mr.getString("wojo2");
				String woretasknum = mr.getString("woretasknum");
				line.setValue("mechname", wojo1, 11L);
				line.setValue("content", wodesc, 11L);
				line.setValue("inspection", wojo2, 11L);
				line.setValue("udworetasknum", woretasknum, 11L);
			}
		}
		table.reloadTable();
		return 1;
	}
}
