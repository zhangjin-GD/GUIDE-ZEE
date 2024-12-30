package guide.app.common;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldComPersonDept extends MAXTableDomain {

	public FldComPersonDept(MboValue mbv) {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDDEPT", "deptnum = :" + thisAttr);
		String[] FromStr = { "deptnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		String sql = "1=2";
		MboRemote mbo = this.getMboValue().getMbo();
		String personId = mbo.getUserInfo().getPersonId();
		String company = " and 1=2";
		String department = " and 1=2";
		String office = " and 1=2";
		
		MboSetRemote personSet = MXServer.getMXServer().getMboSet("PERSON", MXServer.getMXServer().getSystemUserInfo());
		personSet.setWhere("personid='" + personId + "'");
		if(!personSet.isEmpty() && personSet.count() > 0){
			MboRemote person = personSet.getMbo(0);
			company = person.getString("udcompany");
			if(company == null || !company.equalsIgnoreCase("CSPL")){
				company = " and deptnum like '"+CommonUtil.getValue(person, "UDCOMPANY", "COSTCENTER")+"%'";
				department = person.getString("uddept");
				String departType = CommonUtil.getValue(person, "UDDEPT", "PROP");
				if(departType != null && departType.equalsIgnoreCase("FUNCTION")){
					department = company + " and 1=1";
				}else{
					department = company + " and deptnum like'"+person.getString("uddept")+"%'";
				}
				office = department + " and deptnum=nvl('"+person.getString("udofs")+"',deptnum)";
			}else{
				company = " and 1=1";
				department = " and 1=1";
				office = " and 1=1";
			}
		}
		personSet.close();
		
		String thisAttr = getMboValue().getAttributeName();
		if ("udselcompany".equalsIgnoreCase(thisAttr)) {
			sql = "type = 'COMPANY'" + company;
		} else if ("udseldept".equalsIgnoreCase(thisAttr)) {
			sql = "type = 'DEPARTMENT'" + department;
		} else if ("udselofs".equalsIgnoreCase(thisAttr)) {
			sql = "type = 'OFFICE'" + office;
		}
		setListCriteria(sql);
		return super.getList();
	}
	
	public void initValue() throws MXException, RemoteException {
		super.initValue();
	}
	
	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		String thisAttr = getMboValue().getAttributeName();
		MboRemote mbo = this.getMboValue().getMbo();
		if ("udcompany".equalsIgnoreCase(thisAttr)) {
			mbo.setValueNull("uddept", 11L);
			mbo.setValueNull("udofs", 11L);
		} else if ("uddept".equalsIgnoreCase(thisAttr)) {
			mbo.setValueNull("udofs", 11L);
		} 
		
	}
	
}
