package guide.app.company;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.app.common.AncMbo;
import psdi.mbo.HierarchicalMboRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class CompRules extends Mbo implements HierarchicalMboRemote, AncMbo {

	private String table = "UDCOMPRULES";
	private String keynum = "comprulesnum";

	public CompRules(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && (parent instanceof CompRules)) {
			this.getMboValue("comprulesnum").autoKey();
			this.setValue("parent", parent.getString("comprulesnum"), 11L);
		}
		// 新增默认值（状态、状态时间、创建人、创建时间、公司、部门）
		String status = "WAPPR";
		String personId = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		this.setValue("createby", personId, 2L);// 创建人
		this.setValue("createtime", currentDate, 11L);// 创建时间
		this.setValue("status", status, 11L);// 状态
		this.setValue("statustime", currentDate, 11L);// 状态时间
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
		setValue("changeby", getUserInfo().getPersonId(), 11L);
		setValue("changetime", MXServer.getMXServer().getDate(), 11L);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		if (!getMboValue("status").getInitialValue().asString().equalsIgnoreCase("APPR")
				&& getString("status").equalsIgnoreCase("APPR")) {
			// 批准人和批准时间
			setValue("apprby", getUserInfo().getPersonId(), 11L);
			setValue("apprtime", MXServer.getMXServer().getDate(), 11L);
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
		return false;
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
