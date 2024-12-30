package guide.app.woremain;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import guide.app.workorder.UDWO;
import guide.app.workorder.WOBatch;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class WoreMain extends UDMbo implements MboRemote {

	public WoreMain(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			if (parent instanceof UDWO) {
				this.setValue("wonum", parent.getString("wonum"), 11L);
				this.setValue("assetnum", parent.getString("assetnum"), 2L);
			}
			if (parent instanceof WOBatch) {
				this.setValue("wobatchnum", parent.getString("wobatchnum"), 11L);
			}
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (!this.isNull("wonum")) {
			String wonum = this.getString("wonum");
			MboSetRemote woreTaskSet = this.getMboSet("UDWORETASK");
			if (woreTaskSet.isEmpty()) {
				String description = this.getString("description");
				String assetnum = this.getString("assetnum");
				MboRemote woreTask = woreTaskSet.add();
				woreTask.setValue("wodesc", description, 11L);
				if (!this.isNull("rank")) {
					String rank = this.getString("rank");
					woreTask.setValue("rank", rank, 11L);
				}
				if (!this.isNull("worklevel")) {
					String worklevel = this.getString("worklevel");
					woreTask.setValue("worklevel", worklevel, 11L);
				}
				woreTask.setValue("assetnum", assetnum, 2L);
				woreTask.setValue("refwo", wonum, 11L);
			}
		}
	}

	@Override
	public void delete(long accessModifier) throws MXException, RemoteException {
		String status = this.getString("status");
		if ("COMP".equalsIgnoreCase(status)) {
			throw new MXApplicationException("guide", "1057");
		}
		super.delete(accessModifier);
		this.getMboSet("UDWORETASK").deleteAll(2L);
	}

	@Override
	public void undelete() throws MXException, RemoteException {
		super.undelete();
		this.getMboSet("UDWORETASK").undeleteAll();
	}
}
