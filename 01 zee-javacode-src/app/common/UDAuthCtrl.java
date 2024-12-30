package guide.app.common;


import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;


public class UDAuthCtrl extends Mbo implements MboRemote{
	
	public UDAuthCtrl(MboSet ms) throws RemoteException {
		super(ms);
	}
	
	public void authctrl(MboRemote appmbo,MboRemote mbo) throws MXException, RemoteException {
		
		//对象控制
		String status = getString("status");
		String values = getString("statusvalues");
		if(!status.equalsIgnoreCase("") && !values.equalsIgnoreCase("")){
			String[] value = values.split(",");
			for (int i = 0; i < value.length; i++) {
				if(appmbo.getString(status).equalsIgnoreCase(value[i])){
					mbo.setFlags(7L);
					return;
				}
			}
		}
		//是否管理员且在控制中
		if(isAdmin(appmbo) && getInt("admin") == 0)
			return;
		
		//是否流程中
		int nodeid = 0;
		if(hasWorkFlow(appmbo)){
			MboSetRemote wfassignmentSet = appmbo.getMboSet("WFASSIGNMENT");
			if(!wfassignmentSet.isEmpty() && wfassignmentSet.count() > 0){
				nodeid = wfassignmentSet.getMbo(0).getInt("nodeid");
				if(!isCurrent(appmbo,mbo,wfassignmentSet))//是否当前活动人
					return;
			}
		}
		
		//字段控制
		authctrlattr(mbo,nodeid,appmbo.getString(status));

	}

	public void authctrlattr(MboRemote mbo, int nodeid, String appstatus) throws MXException, RemoteException {
		
		//字段权限验证
		MboSetRemote authctrllineSet = getMboSet("UDAUTHCTRLLINE");
		if(!authctrllineSet.isEmpty() && authctrllineSet.count() > 0){
			
			MboRemote authctrlline = null;
			String type = null;
			String[] attrs = null;
			String attributename = null;
			String values = null;
			
			for(int i=0;(authctrlline=authctrllineSet.getMbo(i))!=null;i++){
				if(!authctrlline.getString("attrs").equalsIgnoreCase("")){
					
					type = authctrlline.getString("type");
					attrs = authctrlline.getString("attrs").split(",");
					attributename = authctrlline.getString("attributename");
					values = authctrlline.getString("attributenamevalue");
					if(!attributename.equalsIgnoreCase("") && !values.equalsIgnoreCase("") && mbo.getString(attributename).equalsIgnoreCase(values))
						Flag(mbo,type,attrs);//字段
					else if(attributename.equalsIgnoreCase("") && !values.equalsIgnoreCase("") && appstatus.equalsIgnoreCase(values))
						Flag(mbo,type,attrs);//程序状态
					else if(attributename.equalsIgnoreCase("") && values.equalsIgnoreCase("") && nodeid == authctrlline.getInt("nodeid"))
						Flag(mbo,type,attrs);//节点
				}
			}
		}
		
	}

	public void Flag(MboRemote mbo, String type, String[] attrs) throws RemoteException {
		if(type.equalsIgnoreCase("Readonly"))
			mbo.setFieldFlag(attrs, 7L, true);
		else if(type.equalsIgnoreCase("Required"))
			mbo.setFieldFlag(attrs, 128L, true);
	}
	
	public boolean isAdmin(MboRemote appmbo) throws RemoteException, MXException {
		MboSetRemote groupuserSet = appmbo.getMboSet("$GROUPUSER", "GROUPUSER", "groupname in('MAXADMIN') and userid='"+appmbo.getUserName()+"'");
		if(!groupuserSet.isEmpty() && groupuserSet.count() > 0)
			return true;
		else
			return false;
	}
	
	public boolean hasWorkFlow(MboRemote appmbo) throws RemoteException, MXException {
		MboSetRemote maxrelationshipSet = appmbo.getMboSet("$MAXRELATIONSHIP", "MAXRELATIONSHIP", "parent='"+appmbo.getName()+"' and child='WFASSIGNMENT'");
		if(!maxrelationshipSet.isEmpty() && maxrelationshipSet.count() > 0)
			return true;
		else
			return false;
	}
	
	public boolean isCurrent(MboRemote appmbo, MboRemote mbo, MboSetRemote wfassignmentSet) throws RemoteException, MXException {
		MboRemote wfassignment = null;
		for(int i=0;(wfassignment=wfassignmentSet.getMbo(i))!=null;i++){
			if(wfassignment.getString("assigncode").equalsIgnoreCase(appmbo.getUserName()))
				return true;
		}
		mbo.setFlags(7L);
		return false;
	}
	
	public void init() throws MXException {
		super.init();

	}
	
	public void add() throws MXException, RemoteException {
		super.add();

	}
	
	public void save() throws MXException, RemoteException{
		super.save();
		
	}

	public void delete(long accessModifier) throws MXException, RemoteException{
		super.delete(accessModifier);
		
		MboSetRemote childSet = getMboSet("UDAUTHCTRLLINE");
		if (!childSet.isEmpty() && childSet.count()>0)
			childSet.deleteAll(11L);
		
	}
	
	public void modify() throws MXException, RemoteException{
		super.modify();
		setValue("changeby", getUserInfo().getPersonId(), 11L);
		setValue("changedate", MXServer.getMXServer().getDate(), 11L);
	}
	
}