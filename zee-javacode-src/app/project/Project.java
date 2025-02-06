package guide.app.project;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class Project extends UDMbo implements MboRemote {

	public Project(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		try {
			String appname = getThisMboSet().getApp();
			String status = getString("status");
			if(appname==null){
				return;
			}
			/** 
			 * ZEE - 预算管理应用程序，财务流程审批节点udprojectnum必填
			 * 2025-1-20  9:17  
			 * 22-47
			 */
			String[] str = {"udprojectnum"};
			if ("UDPROJECTZEE".equalsIgnoreCase(appname)) {
				String personid = getUserInfo().getPersonId();
				String createby = getString("createby");
				if (status.equalsIgnoreCase("WAPPR")) {
					if(udinfinancewflow(personid)){
						setFieldFlag(str, 7L, false);//取消只读
						setFieldFlag(str, 128L, true);//设置必填
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		Date currentDate = MXServer.getMXServer().getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
		String year = sdf.format(currentDate);
		this.setValue("budgetcost", 0, 11L);
		this.setValue("yearcost", 0, 11L);
		this.setValue("year", year, 11L);
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}
	
    public boolean udinfinancewflow(String personid) throws RemoteException, MXException{
        boolean flag = false;
        MboSetRemote personidSet =  MXServer.getMXServer().getMboSet("PERSON", MXServer.getMXServer().getSystemUserInfo());
        personidSet.setWhere(" personid in (select personid from person where uddept = 'ZEE02') and udcompany = 'ZEE' and personid = '"+personid+"' ");
        personidSet.reset();
        if(!personidSet.isEmpty() && personidSet.count()>0){
                MboSetRemote wf = this.getMboServer().getMboSet("wfassignment",
                                getUserInfo());
                wf.setWhere("ASSIGNSTATUS='ACTIVE' and OWNERTABLE = '" + this.getName()
                                + "' and OWNERID = '" + this.getUniqueIDValue()
                                + "' and ASSIGNCODE = '" + this.getUserInfo().getPersonId()
                                + "'");
                if (wf != null && wf.count() > 0) {
                        flag = true;
                }
                wf.close();
        }
        personidSet.close();
        return flag;                
}
}
