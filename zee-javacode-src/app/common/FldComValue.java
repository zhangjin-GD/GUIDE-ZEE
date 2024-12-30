package guide.app.common;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;


public class FldComValue extends MAXTableDomain {
	
	public FldComValue(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String attrName = this.getMboValue().getName();
		String mboName = this.getMboValue().getMbo().getName();
		
		MboRemote maxAttribute = getMaxAttribute(mboName, attrName);
		String sameAsObject = "ORGANIZATION";
		String sameAsAttribute = "orgid";
		if(maxAttribute != null){
			sameAsObject = maxAttribute.getString("sameAsObject");
			sameAsAttribute = maxAttribute.getString("sameAsAttribute");
		}
		
		this.setRelationship(sameAsObject, sameAsAttribute + " = :" + attrName);
		this.setKeyMap(sameAsObject, new String[] { attrName }, new String[] { sameAsAttribute });
	}
	
	private MboRemote getMaxAttribute(String mboName, String attrName) throws RemoteException, MXException {
		MboSetRemote maxAttributeSet = MXServer.getMXServer().getMboSet("MAXATTRIBUTE", MXServer.getMXServer().getSystemUserInfo());
		maxAttributeSet.setWhere("objectname='"+mboName+"' and attributename='"+attrName+"'");
		if(!maxAttributeSet.isEmpty() && maxAttributeSet.count()>0){
			MboRemote mbo = maxAttributeSet.getMbo(0);
			maxAttributeSet.close();
			return mbo;
		}
		maxAttributeSet.close();
		return null;
	}

	public MboSetRemote getList() throws RemoteException, MXException{
		String sql = "1=1";
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