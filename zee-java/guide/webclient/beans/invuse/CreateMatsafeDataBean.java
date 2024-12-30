package guide.webclient.beans.invuse;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class CreateMatsafeDataBean extends DataBean {

	@Override
	protected void initialize() throws MXException, RemoteException {
		super.initialize();
	}

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		MboRemote vmatsafe = this.getMbo();
		MboRemote owner = vmatsafe.getOwner();
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboSetRemote matsafeSet = MXServer.getMXServer().getMboSet("UDMATSAFE", mbo.getUserInfo());
		int quantity = vmatsafe.getInt("quantity");
		if (owner != null) {
			if (CheckNum(vmatsafe.getString("matsafenum"), matsafeSet)) {
				for (int i = 0; i < quantity; i++) {
					MboRemote matsafe = matsafeSet.add();
					matsafe.setValue("matsafenum", vmatsafe.getString("matsafenum") + "-" + (i + 1), 11L);
					matsafe.setValue("description", vmatsafe.getString("description"), 11L);
					matsafe.setValue("actionstd", vmatsafe.getDouble("actionstd"), 11L);
					matsafe.setValue("actionact", vmatsafe.getDouble("actionact"), 11L);
					matsafe.setValue("actionunit", vmatsafe.getString("actionunit"), 11L);
					matsafe.setValue("runstd", vmatsafe.getDouble("runstd"), 11L);
					matsafe.setValue("runact", vmatsafe.getDouble("runact"), 11L);
					matsafe.setValue("calstd", vmatsafe.getDouble("calstd"), 11L);
					matsafe.setValue("calact", vmatsafe.getDouble("calact"), 11L);
					matsafe.setValue("invusenum", mbo.getString("invusenum"), 11L);
					matsafe.setValue("invuselinenum", owner.getInt("invuselinenum"), 11L);
					matsafe.setValue("invuselineid", owner.getInt("invuselineid"), 2L);
					matsafe.setValue("status", "RUN", 2L);
				}
				matsafeSet.save();
			} else {
				Object[] obj = { "已经生成安全件" };
				throw new MXApplicationException("udmessage", "error1", obj);
			}
		}
		matsafeSet.close();
		return super.execute();
	}

	private boolean CheckNum(String matsafenum, MboSetRemote matsafeSet) throws RemoteException, MXException {
		matsafeSet.setUserWhere("matsafenum like '" + matsafenum + "%'");
		if (matsafeSet.isEmpty()) {
			return true;
		}
		return false;
	}
}
