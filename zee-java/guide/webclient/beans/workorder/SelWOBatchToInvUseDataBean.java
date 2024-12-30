package guide.webclient.beans.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelWOBatchToInvUseDataBean  extends DataBean{

	
	@Override
	public synchronized int execute() throws MXException, RemoteException {
		Vector<MboRemote> vector = this.getSelection();
		MboRemote owner = this.getParent().getMbo();
		if (owner != null) {
			Date currentDate = MXServer.getMXServer().getDate();
			String createby = owner.getString("createby");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				String wonum = mr.getString("wonum");
				MboSetRemote invUseWoSet = mr.getMboSet("UDINVUSEWO");
				MboRemote invUseWo = invUseWoSet.add();
				invUseWo.setValue("udapptype", "MATUSEWO", 11L);
				invUseWo.setValue("udcreateby", createby, 2L);// 创建人
				invUseWo.setValue("udcreatetime", currentDate, 11L);// 创建时间
				invUseWo.setValue("usetype", "ISSUE", 2L);
				invUseWo.setValue("udmovementtype", "205", 2L);
				MboSetRemote maxUserSet = owner.getMboSet("$MAXUSER", "MAXUSER");
				maxUserSet.setWhere("personid ='" + createby + "'");
				maxUserSet.reset();
				if (maxUserSet != null && !maxUserSet.isEmpty()) {
					MboRemote maxUser = maxUserSet.getMbo(0);
					invUseWo.setValue("fromstoreloc", maxUser.getString("defstoreroom"), 2L);
				}
				invUseWo.setValue("udwonum", wonum, 2L);
			}
		}
		return super.execute();
	}
}
