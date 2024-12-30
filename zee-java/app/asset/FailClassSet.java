package guide.app.asset;

import java.rmi.RemoteException;

import psdi.app.assetcatalog.ClassStructureSetRemote;
import psdi.mbo.HierarchicalMboSet;
import psdi.mbo.HierarchicalMboSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboValueData;
import psdi.util.MXException;

public class FailClassSet extends HierarchicalMboSet implements HierarchicalMboSetRemote, ClassStructureSetRemote {

	private String table = "UDFAILCLASS";
	private String keynum = "failclassnum";
	private String keyid = "udfailclassid";

	public FailClassSet(MboServerInterface ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new FailClass(ms);
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

	@Override
	public void setAnyLevel(boolean var1) throws MXException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getAnyLevel() throws MXException, RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reSetForNewUniqueId(String var1) throws MXException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOriginatingObjectAndAttribute(String var1, String var2, MboRemote var3)
			throws MXException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOriginatingObject(MboRemote var1) throws MXException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOriginatingObject(String var1) throws MXException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasAFakeTreeNode() throws MXException, RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUseWithSql() throws MXException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIsLookup(boolean var1) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MboValueData[][] getMboValueDataForNoTreeNodes(String[] var1, String var2)
			throws MXException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCheckIfClassUsedByObject(boolean var1) throws MXException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reprocessSortOrder() throws MXException, RemoteException {
		// TODO Auto-generated method stub
		
	}

}
