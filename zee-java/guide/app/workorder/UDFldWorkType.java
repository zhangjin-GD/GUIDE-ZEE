package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.app.workorder.FldWorkType;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDFldWorkType extends FldWorkType {

	public UDFldWorkType(MboValue mbv) throws MXException {
		super(mbv);
	}

	public void validate() throws RemoteException, MXException {
		super.validate();

		MboRemote mbo = getMboValue().getMbo();
		String appName = mbo.getThisMboSet().getApp();
		if (appName != null) {
			String worktype = mbo.getString("worktype");
			if (appName != null && appName.equalsIgnoreCase("UDWOPM") && worktype != null
					&& !worktype.equalsIgnoreCase("PM") && !worktype.equalsIgnoreCase("IM")) {
				throw new MXApplicationException("guide", "1004");
			}
		}

	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		String appName = mbo.getThisMboSet().getApp();
		if (appName != null && appName.equalsIgnoreCase("UDWOPM")) {
			listSet.setWhere("worktype in ('PM','IM')");
		} else if (appName != null && appName.equalsIgnoreCase("UDWOZEE")) {
			String udgdlx = mbo.getString("udgdlx");
			if (!udgdlx.equalsIgnoreCase("") && udgdlx.equalsIgnoreCase("A")) {
				listSet.setWhere("worktype in ('BDMS','BDZS','BDEW','AO','DAM')");
			} else if (!udgdlx.equalsIgnoreCase("") && udgdlx.equalsIgnoreCase("B")) {
				listSet.setWhere("worktype in ('PM','MOD')");
			} else if (!udgdlx.equalsIgnoreCase("") && udgdlx.equalsIgnoreCase("C")) {
				listSet.setWhere("worktype in ('CM')");
			} else {
				listSet.setWhere("1=1");
			}
		} else {
			listSet.setWhere("worktype in ('PM','IM','EM','FM','SW')");
		}
		return listSet;
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String worktype = mbo.getString("worktype");
		if (worktype != null && "SW".equalsIgnoreCase(worktype)) {
			mbo.setFieldFlag("udassettypecode", 128L, false);
			mbo.setFieldFlag("assetnum", 128L, false);
		} else {
			mbo.setFieldFlag("udassettypecode", 128L, true);
			mbo.setFieldFlag("assetnum", 128L, true);
		}
		mbo.setValue("udrepairtype", "INSIDE", 11L);
	}
}
