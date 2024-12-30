package guide.app.woremain;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldWoreTaskStatus extends MAXTableDomain {

	public FldWoreTaskStatus(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("alndomain", "value =:" + thisAttr);
		String[] FromStr = { "value" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String status = mbo.getString("status");
		Date curDate = MXServer.getMXServer().getDate();
		if (status != null && !status.equalsIgnoreCase("")) {
			if (status.equalsIgnoreCase("ACTIVE")) {
				mbo.setValue("status", "E", 11L);
				mbo.setValue("etime", curDate, 11L);
			} else if (status.equalsIgnoreCase("C")) {
				mbo.setValue("ctime", curDate, 11L);
			} else if (status.equalsIgnoreCase("E")) {
				mbo.setValue("etime", curDate, 11L);
			} else if (status.equalsIgnoreCase("INACTIVE")) {
				mbo.setValue("status", "A", 11L);
			}
		}
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String status = mbo.getString("status");
		String sql = "DOMAINID ='UDWORETASKSTATUS'";
		if ("B".equalsIgnoreCase(status) || "D".equalsIgnoreCase(status)) {
			sql += " and value not in ('E')";
		}
		setListCriteria(sql);
		return super.getList();
	}

}
