package guide.webclient.beans.workplan;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWPLaborDataBean extends DataBean {

	@Override
	protected MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		MboSetRemote mboSet = super.getMboSetRemote();
		MboRemote mbo = this.app.getAppBean().getMbo();
		String company = mbo.getString("udcompany");
		String udofs = mbo.getString("udofs");
		String sql = "udcompany='" + company + "'";
		// MboRemote parent = getParent().getMbo();
		mboSet.setWhere(sql);
		mboSet.setOrderBy("case when upper('" + udofs + "') "
				+ " in (select nvl(udofs,'NA') from person where person.personid=labor.personid) then 0 "
				+ " when instr(upper('" + udofs + "'),(select udofs from person "
				+ " where personid=labor.personid)) >0 then 1 else 2 end,"
				+ " (select udofs from person where person.personid = labor.personid)");
		mboSet.reset();
		return mboSet;
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			String wonum = owner.getString("wonum");
			MboSetRemote woSet = owner.getMboSet("$WORKORDER", "workorder", "wonum = '" + wonum + "'");
			MboRemote wo = woSet.getMbo(0);
			MboSetRemote lineSet = wo.getMboSet("UDWPLABOR");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote line = lineSet.addAtEnd();
				line.setValue("wonum", owner.getString("wonum"), 11L);
				line.setValue("laborcode", mr.getString("laborcode"), 2L);
			}
		}
		return super.execute();
	}
}
