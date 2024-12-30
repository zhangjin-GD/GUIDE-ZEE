package guide.app.signin;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDSignIn extends Mbo implements MboRemote {
	public UDSignIn(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();

		String[] readOnlyAttrs = {"signby", "signtime", "signshiftnum", "starttime", "endtime", "description"};
		if(toBeAdded()){
			setFieldFlag(readOnlyAttrs, 7L, false);
			setFieldFlag(readOnlyAttrs, 128L, true);
		} else {
			setFieldFlag(readOnlyAttrs, 128L, false);
			setFieldFlag(readOnlyAttrs, 7L, true);
		}
		
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String personId = this.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		setValue("signby", personId, 2L);// 签到人
		setValue("signtime", currentDate, 2L);// 签到时间
		String signShiftNum = CommonUtil.getValue("UDSIGNSHIFT", 
				"(sysdate>to_date((to_char(sysdate,'yyyy-mm-dd')||to_char(starttime,'hh24:mi:ss')),'yyyy-mm-dd hh24:mi:ss') and sysdate<to_date((to_char(sysdate,'yyyy-mm-dd')||to_char(endtime,'hh24:mi:ss')),'yyyy-mm-dd hh24:mi:ss') and starttime<endtime)"
				+ " or (sysdate<to_date((to_char(sysdate,'yyyy-mm-dd')||to_char(starttime,'hh24:mi:ss')),'yyyy-mm-dd hh24:mi:ss') and sysdate<to_date((to_char(sysdate,'yyyy-mm-dd')||to_char(endtime,'hh24:mi:ss')),'yyyy-mm-dd hh24:mi:ss') and starttime>endtime)"
				+ " or (sysdate>to_date((to_char(sysdate,'yyyy-mm-dd')||to_char(starttime,'hh24:mi:ss')),'yyyy-mm-dd hh24:mi:ss') and sysdate>to_date((to_char(sysdate,'yyyy-mm-dd')||to_char(endtime,'hh24:mi:ss')),'yyyy-mm-dd hh24:mi:ss') and starttime>endtime)"
				, "signshiftnum");
		if(signShiftNum != null && !signShiftNum.equalsIgnoreCase("")){
			setValue("signshiftnum", signShiftNum, 2L);// 签到班次
		}
	}
	
}
