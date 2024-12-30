package guide.app.signin;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldSchshiftSignshiftNum extends MAXTableDomain
{

	public FldSchshiftSignshiftNum(MboValue mbv) throws RemoteException, MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDSIGNSHIFT", "signshiftnum=:" + thisAttr);
		String[] FromStr = { "signshiftnum" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if(owner != null){
			String company = owner.getString("udcompany");
			String department = owner.getString("uddept");
			String office = owner.getString("udofs");
			if(department == null || department.equalsIgnoreCase("")){
				department = "null";
			}
			if(office == null || office.equalsIgnoreCase("")){
				office = "null";
			}
			String sql = "udcompany=decode('"+company+"','CSPL',udcompany,'"+company+"') and nvl(uddept,'"+department+"')='"+department+"' and nvl(udofs,'"+office+"')='"+office+"'";
			setListCriteria(sql);
		}
		return super.getList();
	}
    
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
    public void action() throws RemoteException, MXException{
    	super.action();
    	
    	MboRemote mbo = getMboValue().getMbo();
    	String description = mbo.getString("description");
    	Date schPlanDate = mbo.getDate("schplandate");
		MboSetRemote signShiftSet = mbo.getMboSet("UDSIGNSHIFT");
		if (signShiftSet != null && !signShiftSet.isEmpty() && schPlanDate != null) {
			MboRemote signShift = signShiftSet.getMbo(0);
			if(description != null && !description.equalsIgnoreCase("")){
				mbo.setValue("description", description, 11L);
			}
			mbo.setValue("starttime", CommonUtil.getDateFormat(schPlanDate, "yyyy-MM-dd")+" "+signShift.getString("starttime"), 11L);
			if(signShift.getDate("starttime").after(signShift.getDate("endtime"))){
				schPlanDate = CommonUtil.getCalDate(schPlanDate, 1);
			}
			mbo.setValue("endtime", CommonUtil.getDateFormat(schPlanDate, "yyyy-MM-dd")+" "+signShift.getString("endtime"), 11L);
		}

    }
    
    
}