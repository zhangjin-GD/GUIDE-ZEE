package guide.app.common;


import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;


public class FldAppLinkOwnerid extends MAXTableDomain {
	
	public FldAppLinkOwnerid(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		MboRemote mbo = this.getMboValue().getMbo();
		String attrName = this.getMboValue().getName();
		
		String mboName = mbo.getString("ownertable");
		if(mboName == null || mboName.equalsIgnoreCase(""))
			mboName = "UDITEMREQ";
		String idName = getIDName(mboName);
		this.setRelationship(mboName, idName + " = :" + attrName);
		this.setKeyMap(mboName, new String[] { attrName }, new String[] { idName });
	}
	
	private String getIDName(String mboName) throws RemoteException, MXException {
		MboSetRemote maxObjectSet = MXServer.getMXServer().getMboSet(mboName, MXServer.getMXServer().getSystemUserInfo());
		maxObjectSet.setWhere("udcompany='23K9NTTH'");
		if(!maxObjectSet.isEmpty() && maxObjectSet.count()>0){
			String idName = maxObjectSet.getMbo(0).getUniqueIDName();
			maxObjectSet.close();
			return idName;
		}
		maxObjectSet.close();
		return mboName+"ID";
	}

	public MboSetRemote getList() throws RemoteException, MXException{
		String sql = "1=2";
		setListCriteria(sql);
		return super.getList();
	}
	
	public void validate() throws RemoteException, MXException {
//		super.validate();
		
	}
	
	public void action() throws RemoteException, MXException {
		super.action();
		
	}

	public String[] getAppLink() throws MXException, RemoteException {
		
		MboRemote mbo = this.getMboValue().getMbo();
		String mboName = mbo.getString("ownertable");
		int ownerId = mbo.getInt("ownerId");
		if(mboName == null || mboName.equalsIgnoreCase(""))
			mboName = "WFINSTANCE";
		
		if (mboName.equalsIgnoreCase("WORKORDER")) {
			String appType = getApptype(ownerId, mboName, "worktype");
			if(appType == null || appType.equalsIgnoreCase("")){
				return new String[] { "" };
			}else if(appType.equalsIgnoreCase("PM") || appType.equalsIgnoreCase("IM")){
				return new String[] { "UDWOPM" };
			}else if(appType.equalsIgnoreCase("CM")){
				return new String[] { "UDWOCM" };
			}else if(appType.equalsIgnoreCase("EM")){
				return new String[] { "UDWOEM" };
			}else if(appType.equalsIgnoreCase("FM")){
				return new String[] { "UDWOFM" };
			}
		}else if (mboName.equalsIgnoreCase("PR")) {
			String appType = getApptype(ownerId, mboName, "udapptype");
			if(appType == null || appType.equalsIgnoreCase("")){
				return new String[] { "" };
			}else if(appType.equalsIgnoreCase("PRMAT")){
				return new String[] { "UDPRMAT" };
			}else if(appType.equalsIgnoreCase("PRSER")){
				return new String[] { "UDPRSER" };
			}else if(appType.equalsIgnoreCase("PRFIX")){
				return new String[] { "UDPRFIX" };
			}
		}else if (mboName.equalsIgnoreCase("PO")) {
			String appType = getApptype(ownerId, mboName, "udapptype");
			if(appType == null || appType.equalsIgnoreCase("")){
				return new String[] { "" };
			}else if(appType.equalsIgnoreCase("POMAT")){
				return new String[] { "UDPOMAT" };
			}else if(appType.equalsIgnoreCase("POSER")){
				return new String[] { "UDPOSER" };
			}else if(appType.equalsIgnoreCase("POFIX")){
				return new String[] { "UDPOFIX" };
			}
		}else if (mboName.equalsIgnoreCase("INVUSE")) {
			String appType = getApptype(ownerId, mboName, "udapptype");
			if(appType == null || appType.equalsIgnoreCase("")){
				return new String[] { "" };
			}else if(appType.equalsIgnoreCase("MATUSEWO")){
				return new String[] { "UDMATUSEWO" };
			}else if(appType.equalsIgnoreCase("MATRETWO")){
				return new String[] { "UDMATRETWO" };
			}else if(appType.equalsIgnoreCase("MATUSEOT")){
				return new String[] { "UDMATUSEOT" };
			}else if(appType.equalsIgnoreCase("MATRETOT")){
				return new String[] { "UDMATRETOT" };
			}else if(appType.equalsIgnoreCase("MATUSECS")){
				return new String[] { "UDMATUSECS" };
			}else if(appType.equalsIgnoreCase("MATRETCS")){
				return new String[] { "UDMATRETCS" };
			}
		}else {
			return new String[] { mboName };
		}
		return super.getAppLink();
		
	}

	private String getApptype(int ownerId, String mboName, String typeName) throws RemoteException, MXException {
		String idName = getIDName(mboName);
		MboSetRemote maxObjectSet = MXServer.getMXServer().getMboSet(mboName, MXServer.getMXServer().getSystemUserInfo());
		maxObjectSet.setWhere(idName+"="+ownerId);
		if(!maxObjectSet.isEmpty() && maxObjectSet.count()>0){
			String typeValue = maxObjectSet.getMbo(0).getString(typeName);
			maxObjectSet.close();
			return typeValue;
		}
		maxObjectSet.close();
		return "";
	} 
	
}