package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Vector;

import guide.app.inventory.UDInvUse;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelInvUseLineAssetDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {
		DataBean mainLine = app.getDataBean("main_invuselinetab_table");
		MboRemote mbo = this.app.getAppBean().getMbo();
		MboSetRemote vInvUseLineSet = mbo.getMboSet("UDVINVUSELINE");
		if (!vInvUseLineSet.isEmpty() && vInvUseLineSet.count() > 0) {
			MboRemote vInvUseLine = vInvUseLineSet.getMbo(0);
			if (!vInvUseLine.isNull("itemnum")) {
				String itemnum = vInvUseLine.getString("itemnum");
				MboSetRemote assetSet = vInvUseLine.getMboSet("ASSET");
				Vector<MboRemote> vector = assetSet.getSelection();
				MboRemote owner = mainLine.getParent().getMbo();
				if (owner != null && owner instanceof UDInvUse) {
					MboSetRemote invuseLineSet = owner.getMboSet("INVUSELINE");
					for (int i = 0; i < vector.size(); i++) {
						MboRemote mr = (MboRemote) vector.elementAt(i);
						MboRemote invuseLine = invuseLineSet.add();
						invuseLine.setValue("itemnum", itemnum, 2L);
						invuseLine.setValue("assetnum", mr.getString("assetnum"), 2L);
					}
				}
			}
		}
		mainLine.reloadTable();
		return 1;
	}
}
