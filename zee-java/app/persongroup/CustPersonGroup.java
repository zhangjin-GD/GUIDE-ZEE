package guide.app.persongroup;

import java.rmi.RemoteException;

import psdi.app.common.AncMbo;
import psdi.app.persongroup.PersonGroup;
import psdi.mbo.HierarchicalMboRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class CustPersonGroup extends PersonGroup implements HierarchicalMboRemote, AncMbo, MboRemote {
	private String table = "PERSONGROUP"; // 表名
	private String keynum = "PERSONGROUP"; // 编号

	public CustPersonGroup(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof CustPersonGroup) {
			String persongroup = parent.getString("persongroup");
			this.setValue("parent", persongroup, 11L);
		}
	}

	@Override
	public boolean hasChildren() throws MXException, RemoteException {
		MboSetRemote childrenSet = getMboSet("CHILDREN");
		return !childrenSet.isEmpty();
	}

	@Override
	public boolean hasParents() throws MXException, RemoteException {

		return !isNull("parent");
	}

	@Override
	public boolean isTop() throws MXException, RemoteException {

		return !isTop();
	}

	public MboRemote getParent() throws MXException, RemoteException {

		if (isNull("parent")) {
			return null;
		}

		MboRemote parent = null;
		MboSetRemote parentSet = getMboSet("PARENT");
		if (!parentSet.isEmpty() && parentSet.count() > 0)
			parent = parentSet.getMbo(0);

		return parent;
	}

	public MboSetRemote getChildren() throws MXException, RemoteException {
		return getMboSet("CHILDREN");
	}

	public MboSetRemote getBrothers() throws MXException, RemoteException {
		MboSetRemote treeSet = null;
		MboRemote parent = getParent();
		if (parent != null) {
			treeSet = parent.getMboSet("$TREE", table,
					"parent='" + parent.getString(keynum) + "' and " + keynum + " != '" + getString(keynum) + "'");
		}
		return treeSet;
	}
}
