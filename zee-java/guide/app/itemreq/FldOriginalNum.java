package guide.app.itemreq;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldOriginalNum extends MAXTableDomain {
	
	public FldOriginalNum(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String attrName = this.getMboValue().getName();
		this.setRelationship("ITEM", "itemnum = :" + attrName);
		this.setKeyMap("ITEM", new String[] { attrName }, new String[] { "itemnum" });
	}

	public MboSetRemote getList() throws RemoteException, MXException{
		MboRemote mbo = this.getMboValue().getMbo();
		String sql = "classstructureid='"+mbo.getString("classstructureid")+"'";
		setListCriteria(sql);
		return super.getList();
	}
	
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
	public void action() throws RemoteException, MXException {
		super.action();
		
	}

	
}