package guide.webclient.beans.asset;

import java.rmi.RemoteException;

import guide.app.asset.FailClass;
import psdi.mbo.MboRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class FailTypeTableBean extends DataBean {
	@Override
	public int addrow() throws MXException {
		super.addrow();
		String sid = this.getId();
		DataBean line = app.getDataBean(sid);
		try {
			MboRemote mbo = getMbo();
			MboRemote parent = line.getParent().getMbo();
			if (parent != null && parent instanceof FailClass) {
				String type = "";
				String failclassnum = parent.getString("failclassnum");
				if ("failproblem".equalsIgnoreCase(sid)) {
					type = "P";
				} else if ("failcause".equalsIgnoreCase(sid)) {
					type = "C";
				}
				int count = mbo.getThisMboSet().count();
				mbo.setValue("failtypenum", type + String.format("%02d", count), 11L);
				mbo.setValue("failclassnum", failclassnum, 11L);
				mbo.setValue("type", type, 11L);
			}
		} catch (RemoteException | MXException e) {
			e.printStackTrace();
		}
		return 1;
	}
}
