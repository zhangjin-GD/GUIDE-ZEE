package guide.app.signin;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;


public class FldSigninSignShiftNum extends MAXTableDomain
{

	public FldSigninSignShiftNum(MboValue mbv) throws RemoteException, MXException {
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
		String company = mbo.getString("udcompany");
		String department = mbo.getString("uddept");
		String office = mbo.getString("udofs");
		if(department == null || department.equalsIgnoreCase("")){
			department = "null";
		}
		if(office == null || office.equalsIgnoreCase("")){
			office = "null";
		}
		String sql = "udcompany=decode('"+company+"','CSPL',udcompany,'"+company+"') and nvl(uddept,'"+department+"')='"+department+"' and nvl(udofs,'"+office+"')='"+office+"'";
		setListCriteria(sql);
		return super.getList();
	}
    
	public void validate() throws RemoteException, MXException {
		super.validate();
		
	}
	
    public void action() throws RemoteException, MXException{
    	super.action();
    	
    	MboRemote mbo = getMboValue().getMbo();
    	Date signtime = mbo.getDate("signtime");
		MboSetRemote signShiftSet = mbo.getMboSet("UDSIGNSHIFT");
		if (signShiftSet != null && !signShiftSet.isEmpty()) {
			MboRemote signShift = signShiftSet.getMbo(0);
			mbo.setValue("description", signShift.getString("description"), 11L);
			Date startTime = signtime;
			Date endTime = signtime;
			Date setStartTime = signtime;
			Date setEndTime = signtime;
			try {
				startTime = CommonUtil.getDateFormat(CommonUtil.getDateFormat(signtime, "yyyy-MM-dd")+" "+signShift.getString("starttime"), "yyyy-MM-dd HH:mm:ss");
				endTime = CommonUtil.getDateFormat(CommonUtil.getDateFormat(signtime, "yyyy-MM-dd")+" "+signShift.getString("endtime"), "yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(startTime.after(signtime) && endTime.after(signtime) && startTime.after(endTime)){
				setStartTime = CommonUtil.getCalDate(signtime, -1);
				setEndTime = signtime;
			}else if(signtime.after(startTime) && signtime.after(endTime) && startTime.after(endTime)){
				setStartTime = signtime;
				setEndTime = CommonUtil.getCalDate(signtime, 1);
			}else if(signtime.after(startTime) && endTime.after(signtime) && endTime.after(startTime)){
				setStartTime = signtime;
				setEndTime = signtime;
			}
			mbo.setValue("starttime", CommonUtil.getDateFormat(setStartTime, "yyyy-MM-dd")+" "+signShift.getString("starttime"), 11L);
			mbo.setValue("endtime", CommonUtil.getDateFormat(setEndTime, "yyyy-MM-dd")+" "+signShift.getString("endtime"), 11L);
		}

    }
    
    
}