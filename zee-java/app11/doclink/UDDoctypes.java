package guide.app.doclink;

import java.rmi.RemoteException;

import psdi.app.common.AncMbo;
import psdi.app.doclink.Doctypes;
import psdi.mbo.HierarchicalMboRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;

public class UDDoctypes extends Doctypes implements HierarchicalMboRemote, AncMbo {
	private String table = "DOCTYPES";
	private String keynum = "doctype";

	public UDDoctypes(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && (parent instanceof UDDoctypes)
				&& parent.getThisMboSet().getApp().equalsIgnoreCase("UDDOCTYPES")) {
			this.setValue("parent", parent.getString("doctype"), 11L);
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

		if (isNull("parent"))
			return null;

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
		if (parent != null)
			treeSet = parent.getMboSet("$TREE", table,
					"parent='" + parent.getString(keynum) + "' and " + keynum + " != '" + getString(keynum) + "'");

		return treeSet;
	}
}
