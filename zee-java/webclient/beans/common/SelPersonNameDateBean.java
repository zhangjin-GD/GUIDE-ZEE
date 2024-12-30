package guide.webclient.beans.common;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPersonNameDateBean extends DataBean {

	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
		MboRemote mbo = this.app.getAppBean().getMbo();
		String appname = mbo.getThisMboSet().getApp();
		// 遗留项
		if ("udworetask".equalsIgnoreCase(appname)) {
			String personId = mbo.getUserInfo().getPersonId();
			MboSetRemote personSet = mbo.getMboSet("$PERSON", "PERSON", "personid ='" + personId + "'");
			// 当前人员 头衔内容包含主管的才允许操作
			if (personSet != null && !personSet.isEmpty()) {
				MboRemote person = personSet.getMbo(0);
				String title = person.getString("title");
				if (!title.contains("主管")) {
					throw new MXApplicationException("guide", "1129");
				}
			}
		}
	}

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
			Date sysDate = MXServer.getMXServer().getDate();
			String personId = owner.getUserInfo().getPersonId();
			StringBuffer bufid = new StringBuffer();
			StringBuffer bufdesc = new StringBuffer();
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				String personid = mr.getString("personid");
				String displayname = mr.getString("displayname");
				bufid.append(personid).append(",");
				bufdesc.append(displayname).append(",");
			}
			if (bufid.length() > 0) {
				String value = bufid.substring(0, bufid.length() - 1);
				owner.setValue("displayid", value, 11L);
			}
			if (bufdesc.length() > 0) {
				String value = bufdesc.substring(0, bufdesc.length() - 1);
				owner.setValue("displayname", value, 11L);
			}
			owner.setValue("dispatcherby", personId, 2L);
			owner.setValue("dispatcherdate", sysDate, 11L);
		}
		return super.execute();
	}
}
