package guide.app.asset;

import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;


import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class AssetStopLine extends Mbo implements MboRemote {

	public AssetStopLine(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		
		if(!toBeAdded()){
			try {
				String isAgree = getString("isagree");
				String[] oacAttrs = {"actstarttime", "actendtime"};
				String[] engReadonlyAttrs = { "assetnum", "planstarttime", "planendtime", "emergent", "reason",	"stoploc", "influence", "laborcode" };
				String[] oacReadonlyAttrs = {"isagree", "result", "checkby", "actstarttime", "actendtime"};
				String company = CommonUtil.getValue(this, "UDASSETSTOP", "udcompany");
				Date currentDate = MXServer.getMXServer().getDate();
				String currentDateStr = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
				String planStartTimeStr = getString("planstarttime");
				String actStartTimeStr = getString("actstarttime");
				Date planStartTime = getDate("planstarttime");
				String appType = CommonUtil.getValue(this, "UDASSETSTOP", "udapptype");
				if(appType != null && appType.equalsIgnoreCase("ASSETSTOP")){
					if(planStartTimeStr != null && !planStartTimeStr.equalsIgnoreCase("") && company != null && company.equalsIgnoreCase("2533XOCT")){
						String reqTimeStr = MXServer.getMXServer().getProperty("guide.assetstop.reqtime");
						if(reqTimeStr == null || reqTimeStr.equalsIgnoreCase("")){
							reqTimeStr = "17:30:00";
						}
						String conTimeStr = MXServer.getMXServer().getProperty("guide.assetstop.contime");
						if(conTimeStr == null || conTimeStr.equalsIgnoreCase("")){
							conTimeStr = "8:30:00";
						}
						String planStartDateStr = CommonUtil.getDateFormat(planStartTime, "yyyy-MM-dd");
						Date conDateTime = CommonUtil.getDateFormat(planStartDateStr + " " + conTimeStr, "yyyy-MM-dd HH:mm:ss");
						Date planStartDate = CommonUtil.getCalDate(planStartTime, -1);
						String planStartYesDateStr = CommonUtil.getDateFormat(planStartDate, "yyyy-MM-dd");
						Date reqDateTime = CommonUtil.getDateFormat(planStartYesDateStr + " " + reqTimeStr, "yyyy-MM-dd HH:mm:ss");
						if(currentDate.after(reqDateTime)){
							setFieldFlag(engReadonlyAttrs, 7L, true);
						}else {
							setFieldFlag(engReadonlyAttrs, 7L, false);
						}
						if(currentDate.after(conDateTime)){
							setFieldFlag(oacReadonlyAttrs, 7L, true);
						}else {
							setFieldFlag(oacReadonlyAttrs, 7L, false);
							if(isAgree != null && isAgree.equalsIgnoreCase("Y")){
								setFieldFlag(oacAttrs, 7L, false);
								setFieldFlag(oacAttrs, 128L, true);
							} else {
								setFieldFlag(oacAttrs, 128L, false);
								setFieldFlag(oacAttrs, 7L, true);
							}
						}
					}else{
						if(currentDate.after(planStartTime)){
							setFieldFlag(engReadonlyAttrs, 7L, true);
							setFieldFlag(oacReadonlyAttrs, 7L, true);
						}else {
							setFieldFlag(engReadonlyAttrs, 7L, false);
							setFieldFlag(oacReadonlyAttrs, 7L, false);
							if(isAgree != null && isAgree.equalsIgnoreCase("Y")){
								setFieldFlag(oacAttrs, 7L, false);
								setFieldFlag(oacAttrs, 128L, true);
							} else {
								setFieldFlag(oacAttrs, 128L, false);
								setFieldFlag(oacAttrs, 7L, true);
							}
						}
					}
					if(actStartTimeStr != null && !actStartTimeStr.equalsIgnoreCase("")){
						setFieldFlag(engReadonlyAttrs, 7L, true);
					}
				}else if(appType != null && appType.equalsIgnoreCase("ASSETPM")){
					String planStartDateStr = CommonUtil.getDateFormat(planStartTime, "yyyy-MM-dd");
					if(currentDateStr != null && planStartDateStr != null && !currentDateStr.equalsIgnoreCase(planStartDateStr)){
						setFieldFlag(engReadonlyAttrs, 7L, true);
//						setFieldFlag(oacReadonlyAttrs, 7L, true);
					}else {
						setFieldFlag(engReadonlyAttrs, 7L, false);
						setFieldFlag(oacReadonlyAttrs, 7L, false);
						if(isAgree != null && isAgree.equalsIgnoreCase("Y")){
							setFieldFlag(oacAttrs, 7L, false);
							setFieldFlag(oacAttrs, 128L, true);
						} else {
							setFieldFlag(oacAttrs, 128L, false);
							setFieldFlag(oacAttrs, 7L, true);
						}
					}
				}
				
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof AssetStop) {
			String asnum = parent.getString("asnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("asnum", asnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
	
	@Override
	public void save() throws MXException, RemoteException {
		super.save();
		
		if(isModified("assetnum") || isModified("planstarttime") || isModified("planendtime")
				|| isModified("emergent") || isModified("reason") || isModified("stoploc") 
				|| isModified("influence") || isModified("laborcode") || toBeAdded()) {
			try {
				MboRemote owner = getOwner();
				if (owner != null) {
					String company = owner.getString("udcompany");
					String dept = owner.getString("uddept");
					if(company != null){
						if(company.equalsIgnoreCase("2528QPCT")){
							qpctConfirm("1");
						}else if(company.equalsIgnoreCase("2529JPPDC") && dept.equalsIgnoreCase("2529120101")){
							qpctConfirm("2");
						}
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}
	
	private void qpctConfirm(String PORT_ID) throws RemoteException, MXException, ParseException {
		// 停机申请参数
		String param = "{\r\n" + 
				"   \"PORT_ID\":\""+PORT_ID+"\",\r\n" + 
				"	\"BIANHAO\": \""+getInt("udassetstoplineid")+"\",\r\n" + 
				"	\"SBID\": \""+getString("assetnum")+"\",\r\n" + 
				"	\"PLANBEGINTIME\": \""+CommonUtil.getDateFormat(getDate("planstarttime"), "yyyy-MM-dd HH:mm:ss")+"\",\r\n" + 
				"	\"PLANENDTIME\": \""+CommonUtil.getDateFormat(getDate("planendtime"), "yyyy-MM-dd HH:mm:ss")+"\",\r\n" + 
				"	\"CONTENT\": \""+getString("reason")+"\",\r\n" + 
				"	\"ISFORCE\": \""+getString("emergent")+"\",\r\n" + 
				"	\"HELP\": \""+getString("stoploc")+"\",\r\n" + 
				"	\"EFFECT\": \""+getString("influence")+"\",\r\n" + 
				"	\"PLANNER\": \""+getUserInfo().getPersonId()+"\",\r\n" + 
				"	\"PLANSTATE\": \"未确认\",\r\n" + 
				"	\"PLANTIME\": \""+CommonUtil.getCurrentDateFormat("yyyy-MM-dd HH:mm:ss")+"\",\r\n" + 
				"	\"FZR\": \""+getString("laborcode")+"\",\r\n" + 
				"	\"FINISHTIME\": \"\",\r\n" + 
				"	\"FINISHUSER\": \"\"\r\n" + 
				"}";
		System.out.println("\n-------------------------"+param);
		String result = CommonUtil.sendQpctTos(param);
		System.out.println("\n-------------------------"+result);
		if(result.toUpperCase().contains("OK")) {
			
		}else{
			Object params[] = { "停机申请推送失败，“" +result+ "”！" };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}
	}

	public void qpctComp() throws RemoteException, MXException, ParseException {
		String PORT_ID = "0";
		MboRemote owner = getOwner();
		if (owner != null) {
			String company = owner.getString("udcompany");
			if(company != null){
				if(company.equalsIgnoreCase("2528QPCT")){
					PORT_ID = "1";
				}else if(company.equalsIgnoreCase("2529JPPDC")){
					PORT_ID = "2";
				}
			}
		}
		// 完工参数
		String param = "{\r\n" + 
				"   \"PORT_ID\":\""+PORT_ID+"\",\r\n" + 
				"	\"BIANHAO\": \""+getInt("udassetstoplineid")+"\",\r\n" + 
				"	\"SBID\": \""+getString("assetnum")+"\",\r\n" + 
				"	\"PLANBEGINTIME\": \""+CommonUtil.getDateFormat(getDate("planstarttime"), "yyyy-MM-dd HH:mm:ss")+"\",\r\n" + 
				"	\"PLANENDTIME\": \""+CommonUtil.getDateFormat(getDate("planendtime"), "yyyy-MM-dd HH:mm:ss")+"\",\r\n" + 
				"	\"CONTENT\": \""+getString("reason")+"\",\r\n" + 
				"	\"ISFORCE\": \""+getString("emergent")+"\",\r\n" + 
				"	\"HELP\": \""+getString("stoploc")+"\",\r\n" + 
				"	\"EFFECT\": \""+getString("assetnum")+"\",\r\n" + 
				"	\"PLANNER\": \""+getUserInfo().getPersonId()+"\",\r\n" + 
				"	\"PLANSTATE\": \"完成\",\r\n" + 
				"	\"PLANTIME\": \""+CommonUtil.getCurrentDateFormat("yyyy-MM-dd HH:mm:ss")+"\",\r\n" + 
				"	\"FZR\": \""+getUserInfo().getPersonId()+"\",\r\n" + 
				"	\"FINISHTIME\": \""+CommonUtil.getCurrentDateFormat("yyyy-MM-dd HH:mm:ss")+"\",\r\n" + 
				"	\"FINISHUSER\": \""+getUserInfo().getPersonId()+"\"\r\n" + 
				"}";
		System.out.println("\n-------------------------"+param);
		String result = CommonUtil.sendQpctTos(param);
		System.out.println("\n-------------------------"+result);
		if(result.toUpperCase().contains("OK")) {
			
		}else{
			Object params[] = { "停机结束推送失败，“" +result+ "”！" };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}

	}
	
	@Override
	public void delete(long accessModifier) throws MXException, RemoteException {
		super.delete(accessModifier);

		MboRemote owner = getOwner();
		if(owner != null && owner instanceof AssetStop){
			String status = owner.getString("status");
			if(status != null && !status.equalsIgnoreCase("WAPPR")){
				throw new MXApplicationException("guide", "1066");
			}
		}
		
	}
	
	
}
