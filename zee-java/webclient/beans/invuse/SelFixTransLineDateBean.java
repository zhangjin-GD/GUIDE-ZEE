package guide.webclient.beans.invuse;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelFixTransLineDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote invuse = this.app.getAppBean().getMbo();
		MboRemote fixed = this.getMbo();
		MboRemote invuseline = fixed.getOwner();
		Vector<MboRemote> vector = this.getSelection();
		if (vector.size() > 0) {
			String personId = invuse.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			MboSetRemote fixtranslineSet = invuse.getMboSet("UDFIXTRANSLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				int linenum = (int) fixtranslineSet.max("linenum") + 1;
				int invuseid = invuse.getInt("invuseid");
				int invuselineid = invuseline.getInt("invuselineid");
				String usetype = invuseline.getString("usetype");
				String enterby = invuseline.getString("enterby");
				int udfixedid = mr.getInt("udfixedid");
				String fixassetnum = mr.getString("fixassetnum");
				String useby = mr.getString("useby");
				String usebynew = mr.getString("usebynew");
				if ("ISSUE".equalsIgnoreCase(usetype) && mr.isNull("usebynew")) {
					throw new MXApplicationException("guide", "1119");
				}
				MboRemote fixtransline = fixtranslineSet.addAtEnd();
				fixtransline.setValue("linenum", linenum, 11L);
				fixtransline.setValue("fixassetnum", fixassetnum, 11L);
				fixtransline.setValue("invuseid", invuseid, 11);
				fixtransline.setValue("invuselineid", invuselineid, 11L);
				fixtransline.setValue("udfixedid", udfixedid, 11L);
				if ("ISSUE".equalsIgnoreCase(usetype)) {
					fixtransline.setValue("usebyold", useby, 11L);
					fixtransline.setValue("useby", usebynew, 11L);
				} else if ("RETURN".equalsIgnoreCase(usetype)) {
					fixtransline.setValue("usebyold", useby, 11L);
					fixtransline.setValue("useby", enterby, 11L);
				}
				fixtransline.setValue("changeby", personId, 11L);
				fixtransline.setValue("changedate", currentDate, 11L);
				if ("ISSUE".equalsIgnoreCase(usetype)) {
					mr.setValue("useby", usebynew, 2L);
					mr.setValue("status", "7", 11L);// 领用 - 在用（7）
				} else if ("RETURN".equalsIgnoreCase(usetype)) {
					mr.setValue("useby", enterby, 2L);
					mr.setValue("status", "5", 11L);// 退回 - 闲置（5）
				}
			}
		}
		return super.execute();
	}
}
