package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.workorder.UDWO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWolaborDataBean extends DataBean {

	public synchronized int execute() throws MXException, RemoteException {

		DataBean wplaborLine = app.getDataBean("udwplabor_table");
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = wplaborLine.getParent().getMbo();
		if (owner != null && owner instanceof UDWO) {
			String udshift = owner.getString("udshift");
			MboSetRemote laborSet = owner.getMboSet("UDWPLABOR");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboSetRemote mboSet = mr.getMboSet("LABORCRAFTRATEDEFAULT");
				MboRemote labor = laborSet.add();
				labor.setValue("wonum", owner.getString("wonum"), 11L);
				labor.setValue("laborcode", mr.getString("laborcode"), 2L);
				if (!mboSet.isEmpty() && mboSet.count() > 0) {
					labor.setValue("unitcost", mboSet.getMbo(0).getString("displayrate"), 11L);
				}
				if (!udshift.equalsIgnoreCase("") && udshift != null) {
					labor.setValue("udshift", udshift, 11L);
				}
			}
		}
		wplaborLine.reloadTable();
		return 1;
	}

	protected MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		MboSetRemote mboSet = super.getMboSetRemote();
		MboRemote owner = getParent().getMbo();
		if ((owner != null) && ((owner instanceof UDWO))) {
			String appname = owner.getThisMboSet().getApp();
			if (("UDWOCM".equalsIgnoreCase(appname)) || ("UDWOPM".equalsIgnoreCase(appname))) {
				String udofs = owner.getString("udofs");
				mboSet.setOrderBy("case when upper('" + udofs + "') "
						+ " in (select nvl(udofs,'NA') from person where person.personid=labor.personid) then 0 "
						+ " when instr(upper('" + udofs + "'),(select udofs from person "
						+ " where personid=labor.personid)) >0 then 1 else 2 end,"
						+ " (select udofs from person where person.personid = labor.personid)");
				mboSet.reset();
			}
		}
		return mboSet;
	}
}