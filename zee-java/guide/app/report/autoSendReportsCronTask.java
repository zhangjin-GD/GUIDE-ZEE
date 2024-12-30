package guide.app.report;


import guide.app.common.CommonUtil;

import java.io.File;
import java.rmi.RemoteException;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class autoSendReportsCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			MboRemote crontaskInstance = getCrontaskInstance();
			UserInfo runAsUserInfo = getRunasUserInfo();
			String reportName = crontaskInstance.getString("udreportname");
			String reportAppName = CommonUtil.getValue(crontaskInstance, "REPORT", "appname");
			String reportDesc = CommonUtil.getValue(crontaskInstance, "REPORT", "description");
			String reportDescZH = CommonUtil.getValue("L_REPORT", "ownerid=(select reportid from report where reportname='"+reportName+"')", "description");
			if(reportDesc == null || reportDesc.equalsIgnoreCase("")){
				reportDesc = reportDescZH;
			}
			String currentTime = CommonUtil.getCurrentDateFormat("yyyyMMddHHmmss");
			
			JSONObject paramData = new JSONObject();
			MboSetRemote parameterSet = crontaskInstance.getMboSet("$CRONTASKPARAM", "CRONTASKPARAM", "instanceName=:instanceName and value is not null");
			if (!parameterSet.isEmpty() && parameterSet.count() > 0) {
				MboRemote parameter = null;
				String value = null;
				for (int i = 0; (parameter = parameterSet.getMbo(i)) != null; i++) {
					value = parameter.getString("value");
					paramData.put(parameter.getString("parameter"), value);
				}
			}
			
			// 报表参数
			JSONObject paramRpt = new JSONObject();
			paramRpt.put("reportName", reportName);
			paramRpt.put("description", reportDesc);
			paramRpt.put("appName", reportAppName);
			paramRpt.put("keyNum", currentTime);
			
			// 报表执行
			File attachment = CommonUtil.getReport(runAsUserInfo, paramRpt, paramData);
			String returnMsg = sendEmail(runAsUserInfo, paramRpt, attachment);
			System.out.println("\n--------------returnMsg:"+returnMsg);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private String sendEmail(UserInfo userInfo, JSONObject paramRpt, File attachment) {
		try {
			String personId = userInfo.getPersonId();
			String toAddress = userInfo.getEmail();
			String title = "EAM "+paramRpt.getString("description");
			String message = title;
			if(toAddress != null && !toAddress.equalsIgnoreCase("")){
				//消息参数
				String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
				JSONObject jsonData = new JSONObject();
				jsonData.put("id", paramRpt.getString("keyNum"));
				jsonData.put("to_user", toAddress);
				jsonData.put("subject", title);
				jsonData.put("content", message);
				jsonData.put("create_time", currentDate);
				jsonData.put("create_by", personId);
				jsonData.put("change_time", currentDate);
				jsonData.put("change_by", personId);
				jsonData.put("file_path", attachment.getAbsolutePath());

				//消息执行
				try {
					String returnResult = CommonUtil.sendGDEam(MXServer.getMXServer().getProperty("guide.gdnotify.url"), jsonData);
					String returnCode = CommonUtil.getString(new JSONObject(returnResult), "code");
					if(returnCode != null && returnCode.equalsIgnoreCase("200")){
						return "已发送";
					}else{
						String error = CommonUtil.getString(new JSONObject(returnResult), "result");
						if (error.length() > 300) {
							return error.substring(0, 300);
						}else{
							return "发送失败："+error;
						}
					}
				} catch (Exception e) {
					String error = e.toString();
					if (error.length() > 300) {
						return error.substring(0, 300);
					}else{
						return error;
					}
				}
			}else{
				return "无收件人";
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		return "内部错误";
	}
	
	
}
