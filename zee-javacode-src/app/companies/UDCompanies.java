package guide.app.companies;

import java.rmi.RemoteException;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDCompanies extends UDMbo implements MboRemote {

	public UDCompanies(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			if (!this.toBeAdded()) {
				String status = this.getString("status");
				if ("APPR".equalsIgnoreCase(status)) {
					this.setFlag(READONLY, true);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();

		if (!getMboValue("status").getInitialValue().asString().equalsIgnoreCase("APPR")
				&& getString("status").equalsIgnoreCase("APPR")) {
			MboSetRemote mboSet = MXServer.getMXServer().getMboSet("COMPANIES", getUserInfo());
			mboSet.setWhere("1=2");
			MboRemote add = mboSet.add();
			// 部分一
			add.setValue("COMPANY", getString("COMPANYNUM"), 11L);
			add.setValue("NAME", getString("NAME"), 11L);
			add.setValue("UDEMAIL", getString("UDEMAIL"), 11L);
			add.setValue("REGISTRATION1", getString("REGISTRATION1"), 11L);
			add.setValue("HOMEPAGE", getString("HOMEPAGE"), 11L);
			add.setValue("UDBUSINESS", getString("UDBUSINESS"), 11L);
			// 部分二
			add.setValue("CURRENCYCODE", "CNY", 11L);
			add.setValue("TAX1CODE", getString("TAX1CODE"), 11L);
			add.setValue("UDSOURCE", getString("UDSOURCE"), 11L);
			add.setValue("UDMDMNUM", getString("UDMDMNUM"), 11L);
			add.setValue("UDTYPE1", getString("UDTYPE1"), 11L);
			add.setValue("UDTYPE2", getString("UDTYPE2"), 11L);
			add.setValue("UDTYPE3", getString("UDTYPE3"), 11L);

			mboSet.save();
			mboSet.close();
		}
	}

}
