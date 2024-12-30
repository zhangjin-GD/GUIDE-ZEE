package guide.app.workorder;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldWOFailMech extends MAXTableDomain {

	public FldWOFailMech(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDFAILCLASS", "failclassnum = :" + thisAttr);
		String[] FromStr = { "failclassnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public void validate() throws MXException, RemoteException {
//		super.validate();
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		StringBuffer strBuff = new StringBuffer();
		List<String> lists = new ArrayList<String>();
		MboSetRemote failClassSet = mbo.getMboSet("UDFAILMECH");
		if (!failClassSet.isEmpty() && failClassSet.count() > 0) {
			MboRemote failClass = failClassSet.getMbo(0);
			String thisdesc = failClass.getString("description");
			String numbertype = failClass.getString("numbertype");
			lists.add(thisdesc);
			while (true) {
				MboSetRemote parentSet = getParent(failClass);
				if (!parentSet.isEmpty() && parentSet.count() > 0) {
					MboRemote parent = parentSet.getMbo(0);
					String description = parent.getString("description");
					lists.add("-");
					lists.add(description);
					failClass = parent;
				} else {
					break;
				}
			}
			Collections.reverse(lists);
			for (String list : lists) {
				strBuff.append(list);
			}
			mbo.setValue("udfailmechdesc", strBuff.toString(), 11L);
			if ("A".equalsIgnoreCase(numbertype)) {
				MboSetRemote assetSet = mbo.getMboSet("ASSET");
				if (!assetSet.isEmpty() && assetSet.count() > 0) {
					MboRemote asset = assetSet.getMbo(0);
					MboSetRemote assettsSet = asset.getMboSet("UDASSETTS");
					if (!assettsSet.isEmpty() && assettsSet.count() > 0) {
						MboRemote assetts = assettsSet.getMbo(0);
						String assetnum = assetts.getString("assetnum");
						mbo.setValue("udeqnum", assetnum, 11L);
					}
				}
			} else {
				mbo.setValueNull("udeqnum");
			}
		} else {
			mbo.setValueNull("udfailmechdesc");
			mbo.setValueNull("udeqnum");
		}
	}

	private MboSetRemote getParent(MboRemote mbo) throws RemoteException, MXException {
		MboSetRemote parent = mbo.getMboSet("PARENT");
		return parent;
	}
}
