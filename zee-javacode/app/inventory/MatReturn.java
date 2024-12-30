package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class MatReturn extends Mbo implements MboRemote {

	public MatReturn(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDInvUse) {
			String invusenum = parent.getString("invusenum");
			this.setValue("invusenum", invusenum, 11L);
		}
	}

	@Override
	public void init() throws MXException {
		super.init();

		try {
			String[] attrs1 = { "returqty", "returntype" };
			String[] attrs2 = { "returntype1", "location" };
			String[] attrs3 = { "remarks" };
			MboRemote parent = this.getOwner();
			if (parent != null && parent instanceof UDInvUse) {
				String udoldretstatus = parent.getString("udoldretstatus");
				if ("INPRG1".equalsIgnoreCase(udoldretstatus)) {
					this.setFieldFlag(attrs1, 128L, true);
					this.setFieldFlag(attrs2, 7L, true);
				} else if ("INPRG2".equalsIgnoreCase(udoldretstatus)) {
					this.setFieldFlag(attrs1, 7L, true);
					this.setFieldFlag(attrs2, 128L, true);
				} else if ("WAPPR".equalsIgnoreCase(udoldretstatus)) {
					this.setFieldFlag(attrs1, 7L, false);
					this.setFieldFlag(attrs2, 7L, false);
					this.setFieldFlag(attrs3, 7L, false);
				} else {
					this.setFieldFlag(attrs1, 7L, true);
					this.setFieldFlag(attrs2, 7L, true);
					this.setFieldFlag(attrs3, 7L, true);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}
}
