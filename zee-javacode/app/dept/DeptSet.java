package guide.app.dept;

import java.rmi.RemoteException;

import psdi.mbo.HierarchicalMboSet;
import psdi.mbo.HierarchicalMboSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboValueData;
import psdi.util.MXException;

public class DeptSet extends HierarchicalMboSet implements HierarchicalMboSetRemote {

	private String table = "UDDEPT";
	private String keynum = "deptnum";
	private String keyid = "uddeptid";

	public DeptSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new Dept(ms);
	}

	@Override
	public MboValueData[][] getChildren(String arg0, String arg1, String[] arg2, int arg3)
			throws MXException, RemoteException {
		reset();
		resetQbe();
		int selectedUniqueId = Integer.parseInt(arg1.replace(",", ""));
		setWhere("parent in (select " + keynum + " from " + table + " where " + keyid + " = " + selectedUniqueId + ")");
		reset();
		if (!isEmpty())
			return getMboValueData(0, arg3 + 1, arg2);
		else
			return (MboValueData[][]) null;
	}

	@Override
	public MboValueData[] getParent(String arg0, String arg1, String[] arg2) throws MXException, RemoteException {
		return null;
	}

	@Override
	public MboValueData[][] getPathToTop(String arg0, String arg1, String[] arg2, int arg3)
			throws MXException, RemoteException {
		return null;
	}

	@Override
	public MboValueData[][] getSiblings(String arg0, String arg1, String[] arg2, int arg3)
			throws MXException, RemoteException {
		return (MboValueData[][]) null;
	}

	@Override
	public MboValueData[][] getTop(String[] arg0, int arg1) throws MXException, RemoteException {
		reset();
		resetQbe();
		setWhere("parent is null");
		reset();
		if (!isEmpty())
			return getMboValueData(0, arg1 + 1, arg0);
		else
			return (MboValueData[][]) null;
	}

}
