package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelInvBalToInvStockBinDateBean extends DataBean {

	@Override
	protected MboSetRemote getMboSetRemote() throws MXException, RemoteException {
		String sql = "";
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboSetRemote mboSet = super.getMboSetRemote();
		String udcompany = mbo.getString("UDCOMPANY");
		MboSetRemote mboLocSet = mbo.getMboSet("UDINVSTOCKLOC");
		if (!mboLocSet.isEmpty() && mboLocSet.count() > 0) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < mboLocSet.count(); i++) {
				MboRemote mboloc = mboLocSet.getMbo(i);
				buf.append("'").append(mboloc.getString("storeloc")).append("',");
			}
			sql = "invbalancesid in (select max(invbalancesid) from invbalances where location in ("
					+ buf.substring(0, buf.length() - 1) + ") group by binnum)";
		} else {
			sql = "invbalancesid in (select max(invbalancesid) from invbalances where location in (select location from locations where udcompany='"
					+ udcompany + "') group by binnum)";
		}
		mboSet.setWhere(sql);
		mboSet.reset();
		return mboSet;
	}

	public void allok() throws MXException {
		try {
			DataBean tableTable = this.app.getDataBean("selinvbalbin");
			MboSetRemote mboSet = tableTable.getMboSet();
			for (int i = 0; mboSet.getMbo(i) != null; i++) {
				mboSet.getMbo(i).select();
			}
			this.app.getDataBean("selinvbalbin").refreshTable();
		} catch (RemoteException | MXException e) {
			e.printStackTrace();
		}
	}

	public void unallok() throws MXException, RemoteException {
		this.app.getDataBean("selinvbalbin").getMboSet().unselectAll();
		this.app.getDataBean("selinvbalbin").refreshTable();
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean lineTable = this.app.getDataBean("udinvstockbin_table");
		Vector<MboRemote> vector = getSelection();
		MboRemote owner = lineTable.getParent().getMbo();
		String invstocknum = owner.getString("invstocknum");
		if (owner != null) {
			MboSetRemote lineSet = owner.getMboSet("UDINVSTOCKBIN");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboRemote line = lineSet.addAtEnd();
				line.setValue("invstocknum", invstocknum, 11L);
				line.setValue("udbinnum", mr.getString("binnum"), 11L);

			}
		}
		lineTable.reloadTable();
		return 1;
	}
}
