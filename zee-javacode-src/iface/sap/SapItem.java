package guide.iface.sap;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class SapItem extends Mbo implements MboRemote {

	public SapItem(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			MboRemote owner = getOwner();
			if (owner != null) {
				String ztran = owner.getString("ztran");
				String[] recAttrs = { "ebeln", "ebelp" };
				String[] useAttrs = { "kostl" };
				if (ztran != null && ztran.indexOf("10") == 0) {
					setFieldFlag(recAttrs, 7L, false);
					setFieldFlag(recAttrs, 128L, true);
					setFieldFlag(useAttrs, 128L, false);
					setFieldFlag(useAttrs, 7L, true);
				} else {
					setFieldFlag(useAttrs, 7L, false);
					setFieldFlag(useAttrs, 128L, true);
					setFieldFlag(recAttrs, 128L, false);
					setFieldFlag(recAttrs, 7L, true);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote owner = getOwner();
		if (owner != null) {
			owner.setFieldFlag("ztran", 7L, true);
			String zstockno = owner.getString("zstockno");
			int zstockitemno = (int) getThisMboSet().max("zstockitemno") + 1;
			String zstockitemnoStr = String.valueOf(zstockitemno);
			this.setValue("zstockno", zstockno, 11L);
			this.setValue("zstockitemno", zstockitemnoStr, 11L);
			MboSetRemote companySet = owner.getMboSet("UDCOMPANY");
			if (!companySet.isEmpty() && companySet.count() > 0) {
				MboRemote company = companySet.getMbo(0);
				String currency = company.getString("currency");
				this.setValue("waers", currency, 11L);
			}
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}

}
