package guide.app.report;

import guide.app.common.ComExecute;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldReport extends MAXTableDomain {

	public FldReport(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		this.setRelationship("REPORT", "reportnum = :" + thisAttr);
		this.setKeyMap("REPORT", new String[] { thisAttr }, new String[] { "reportnum" });
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String sql = "ql=0 and reportnum in(select reportnum from REPORTAUTH,GROUPUSER "
				+ "where REPORTAUTH.groupname=GROUPUSER.groupname and userid='"+mbo.getUserInfo().getPersonId()+"')";
		setListCriteria(sql);
		return super.getList();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String cronTaskName = mbo.getString("crontaskname");
		int reportNum = mbo.getInt("udreportnum");
		String instancename = mbo.getString("instancename");
		
		if(instancename == null || instancename.equalsIgnoreCase("")){
			Date currentDate = MXServer.getMXServer().getDate();
			instancename = ""+currentDate.getTime();
			mbo.setValue("instancename", instancename, 11L);
		}
		mbo.setValue("runasuserid", mbo.getUserInfo().getPersonId(), 11L);
		
		try {
			if(cronTaskName != null && !cronTaskName.equalsIgnoreCase("")){
				String deleteSql = "delete from CRONTASKPARAM where crontaskname='"+cronTaskName+"' and instancename='"+instancename+"'";
				ComExecute.executeSql(deleteSql);
			}
			if(reportNum != 0){
				String insertSql = "insert into CRONTASKPARAM(crontaskname,Instancename,Parameter,Value,crontaskparamid)"
						+ "select '"+cronTaskName+"','"+instancename+"',REPORTLOOKUP.parametername,nvl(REPORTLOOKUP.defaultvalue,decode(REPORTLOOKUP.required,1,1,null)),crontaskparamseq.nextval "
						+ "from REPORTLOOKUP where REPORTLOOKUP.reportnum='"+""+reportNum+"'";
				ComExecute.executeSql(insertSql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
