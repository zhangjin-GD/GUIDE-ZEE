package guide.app.persongroup;

import java.rmi.RemoteException;

import psdi.app.persongroup.PersonGroupSet;
import psdi.app.persongroup.PersonGroupSetRemote;
import psdi.mbo.HierarchicalMboSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboValueData;
import psdi.util.MXException;

public class CustPersonGroupSet extends PersonGroupSet implements HierarchicalMboSetRemote, PersonGroupSetRemote {

	private String table = "PERSONGROUP"; // 表名
	private String keynum = "PERSONGROUP"; // 编号
	private String keyid = "PERSONGROUPID";// ID

	public CustPersonGroupSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new CustPersonGroup(ms);
	}

	public MboValueData[][] getChildren(String s, String s1, String as[], int i) throws MXException, RemoteException {
		reset();
		resetQbe();
		int selUniqueId = Integer.parseInt(s1.replace(",", ""));
		String sql = "parent in (select " + keynum + " from " + table + " where " + keyid + " = " + selUniqueId + ")";
		setWhere(sql);
		reset();

		if (!isEmpty())
			return getMboValueData(0, i + 1, as);
		else
			return (MboValueData[][]) null;
	}

	public MboValueData[] getParent(String s, String s1, String as[]) throws MXException, RemoteException {
		return null;
	}

	public MboValueData[][] getPathToTop(String s, String s1, String as[], int i) throws MXException, RemoteException {
		return null;
	}

	public MboValueData[][] getSiblings(String s, String s1, String as[], int i) throws MXException, RemoteException {
		return (MboValueData[][]) null;
	}

	public MboValueData[][] getTop(String as[], int i) throws MXException, RemoteException {
		reset();
		resetQbe();
		setWhere("parent is null");
		reset();
		if (!isEmpty())
			return getMboValueData(0, i + 1, as);
		else
			return (MboValueData[][]) null;
	}

	@Override
	public MboValueData[][] getAllHierarchies(String var1, String var2, String[] var3, int var4)
			throws MXException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MboValueData[] getHierarchy(String var1, String var2) throws MXException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHierarchy(String var1, String var2, String var3) throws MXException, RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public MboValueData getUniqueIDValue(String var1, String[] var2, String[] var3)
			throws MXException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
