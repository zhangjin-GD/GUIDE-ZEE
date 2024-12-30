package guide.app.workorder;

import java.rmi.RemoteException;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDWoIt extends UDMbo implements MboRemote {

	public UDWoIt(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		
		try {
			String[] adminAattrs = {"udlevel", "suggestion", "class", "principal", "plancompdate", "actcompdate", "result", "remark", "status"};
			String curUserId = getUserName();
			if(!CommonUtil.isAdmin(curUserId)){
				setFieldFlag(adminAattrs, 7L, true);
			}else {
				setFieldFlag(adminAattrs, 7L, false);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		this.setValue("reportedby", getUserInfo().getPersonId(), 2L);
		this.setValue("reporteddate", MXServer.getMXServer().getDate(), 11L);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}
	
}
