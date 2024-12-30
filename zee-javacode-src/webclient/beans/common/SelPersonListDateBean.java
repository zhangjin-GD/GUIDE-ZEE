package guide.webclient.beans.common;

import java.rmi.RemoteException;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPersonListDateBean extends DataBean {

	@Override
	public MboSetRemote getMboSet() throws MXException, RemoteException {
		MboSetRemote mboSet = super.getMboSet();
		MboRemote mbo = this.app.getAppBean().getMbo();
		String sql = "status in ('ACTIVE')";
		if (!mbo.isNull("udcompany")) {
			String udcompany = mbo.getString("udcompany");
			sql += " and udcompany='" + udcompany + "'";
		}
		if (!mbo.isNull("uddept")) {
			String uddept = mbo.getString("uddept");
			sql += " and uddept='" + uddept + "'";
		}
		if (!mbo.isNull("udofs")) {
			String udofs = mbo.getString("udofs");
			sql += " and udofs='" + udofs + "'";
		}
		mboSet.setAppWhere(sql);
		return mboSet;
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.parent.getMbo();
		if (owner != null) {
			StringBuffer idList = new StringBuffer();
			StringBuffer nameList = new StringBuffer();
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				String personId = mr.getString("personid");
				String displayName = mr.getString("displayname");
				idList.append(personId).append(",");
				nameList.append(displayName).append(",");
			}
			if (idList.length() > 0) {
				String value = idList.substring(0, idList.length() - 1);
				owner.setValue("udpersonidlist", value);
			}
			if (nameList.length() > 0) {
				String value = nameList.substring(0, nameList.length() - 1);
				owner.setValue("udpersonnamelist", value);
			}
		}
		return super.execute();
	}
	
}
