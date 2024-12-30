package guide.app.pr;

import java.rmi.RemoteException;
import java.util.HashSet;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValueInfo;
import psdi.util.MXException;

public class PRImp extends UDMbo implements MboRemote {

	private static boolean isHashSetLoaded = false;

	private static HashSet<String> skipFieldCopy = new HashSet<String>();

	public PRImp(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public MboRemote duplicate() throws MXException, RemoteException {

		if (!isHashSetLoaded) {
			loadSkipFieldCopyHashSet();
		}
		MboRemote newMboRemote = copy();
		MboSetRemote lineSetRemote = this.getMboSet("UDPRIMPLINE");
		lineSetRemote.resetQbe();
		lineSetRemote.reset();
		if (!lineSetRemote.isEmpty()) {
			lineSetRemote.copy(newMboRemote.getMboSet("UDPRIMPLINE"));
		}
		newMboRemote.setValue("TOTALCOST", lineSetRemote.sum("LINECOST"), 11L);
		return newMboRemote;
	}

	@Override
	protected boolean skipCopyField(MboValueInfo mvi) {

		return skipFieldCopy.contains(mvi.getName());
	}

	/**
	 * 加载跳过字段复制散列集
	 */
	private void loadSkipFieldCopyHashSet() {

		isHashSetLoaded = true;
		skipFieldCopy.add("PRIMPNUM");
		skipFieldCopy.add("CREATEBY");
		skipFieldCopy.add("CREATETIME");
		skipFieldCopy.add("STATUS");
		skipFieldCopy.add("STATUSTIME");
		skipFieldCopy.add("CHANGEBY");
		skipFieldCopy.add("CHANGETIME");
		skipFieldCopy.add("APPRBY");
		skipFieldCopy.add("APPRTIME");
		skipFieldCopy.add("TOTALCOST");
	}
}
