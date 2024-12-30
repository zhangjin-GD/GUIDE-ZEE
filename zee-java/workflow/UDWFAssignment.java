package guide.workflow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

//import com.google.api.client.util.Base64;

import guide.app.common.CommonUtil;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.workflow.WFAssignment;
import psdi.workflow.WFAssignmentRemote;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

public class UDWFAssignment extends WFAssignment implements WFAssignmentRemote {

	private static final MXLogger MY_LOGGER = MXLoggerFactory.getLogger("maximo.application");

	public UDWFAssignment(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		dataToOA();
	}

	private void dataToOA() throws RemoteException, MXException {
		String oaStatus = MXServer.getMXServer().getProperty("guide.oa.status");
		if (oaStatus != null && oaStatus.equalsIgnoreCase("ACTIVE")) {
			String ownerTable = this.getString("ownertable");
			if(ownerTable.equalsIgnoreCase("PR") || ownerTable.equalsIgnoreCase("PO")
					|| ownerTable.equalsIgnoreCase("INVUSE") || ownerTable.equalsIgnoreCase("WORKORDER")){
	//			String udhrusernum = "27540169"; //测试OA工号
				String udhrusernum = CommonUtil.getValue(this, "ASSIGNEE", "udhrusernum");// 当前流程人员对应OA编号
				String weChatId = CommonUtil.getValue(this, "ASSIGNEE", "udwechatid");// 当前流程人员对应微信编号
				String email = CommonUtil.getValue(this, "ASSIGNEE", "udemail");// 当前流程人员对应邮箱编号
				if (udhrusernum != null && !udhrusernum.isEmpty()) {
					sendOA(udhrusernum);
				} else if (weChatId != null && !weChatId.equalsIgnoreCase("")) {
					sendWeChat(weChatId);
				} else if (email != null && !email.equalsIgnoreCase("")) {
					sendEmail(email);
				} else {
					this.setValue("udoastatus", "未传输", 11L);
				}
			}
		}
	}
	
	private void sendEmail(String email) throws RemoteException, MXException {
		String udoastatus = "内部异常";
		try {
			String assignstatus = this.getString("assignstatus");
			if ("ACTIVE".equalsIgnoreCase(assignstatus)) {
				String wfassDesc = this.getString("description").replaceAll("[<>]", "").replaceAll("请审批", "").replaceAll("[+-//*&&[^.]&&[^(-]]"," ");
				int wfassignmentid = this.getInt("wfassignmentid");
				int ownerid = this.getInt("ownerid");
				String personId = getUserInfo().getPersonId();
				String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					JSONObject emailJson = new JSONObject();
					emailJson.put("id", "WFASSIGNMENT"+wfassignmentid);
					emailJson.put("to_user", email);
					emailJson.put("subject", "EAM待办提醒");
					emailJson.put("content", wfassDesc);
					emailJson.put("create_time", currentDate);
					emailJson.put("create_by", personId);
					emailJson.put("change_time", currentDate);
					emailJson.put("change_by", personId);
					emailJson.put("file_path", "");
					String returnResult = CommonUtil.sendGDEam(MXServer.getMXServer().getProperty("guide.gdnotify.url"), emailJson);
					String returnCode = CommonUtil.getString(new JSONObject(returnResult), "code");
					if(returnCode != null && returnCode.equalsIgnoreCase("200")){
						udoastatus = "已发送";
					}else {
						CommonUtil.ifaceLog(emailJson.toString(), getUserInfo().getPersonId(), getName(), wfassignmentid + "", ownerid + "", CommonUtil.getString(new JSONObject(returnResult), "result"));
						udoastatus = "接口传输失败";
					}
					this.setValue("udoastatus", udoastatus, 11L);
				} catch (Exception e) {
					this.setValue("udoastatus", "接口请求失败", 11L);
					CommonUtil.ifaceLog("接口请求失败", getUserInfo().getPersonId(), getName(), wfassignmentid + "", ownerid + "", e.toString());
				}
			}
		} catch (Exception e) {
			this.setValue("udoastatus", udoastatus, 11L);
			MY_LOGGER.info(e);
		}
	}
	
	private void sendWeChat(String weChatId) throws RemoteException, MXException {
		String udoastatus = "内部异常";
		try {
			String assignstatus = this.getString("assignstatus");
			if ("ACTIVE".equalsIgnoreCase(assignstatus)) {
				String wfassDesc = this.getString("description").replaceAll("[<>]", "").replaceAll("请审批", "").replaceAll("[+-//*&&[^.]&&[^(-]]"," ");
				String appDesc = this.getString("wfassignapp.description");
				int wfassignmentid = this.getInt("wfassignmentid");
				int ownerid = this.getInt("ownerid");
				String applicantCode = CommonUtil.getValue(this, "WFINSTANCE", "originator");
				if(applicantCode == null || applicantCode.equalsIgnoreCase("")){
					applicantCode = getUserInfo().getPersonId();
				}
				String applicant = CommonUtil.getValue("PERSON", "personid='"+applicantCode+"'", "displayname");
				String applyDeptCode = CommonUtil.getValue("PERSON", "personid='"+applicantCode+"'", "uddept");
				String applyDept = CommonUtil.getValue("UDDEPT", "deptnum='"+applyDeptCode+"'", "description");
				try {
					JSONObject weChatJson = new JSONObject();
					weChatJson.put("content", wfassDesc);// 内容
					weChatJson.put("form", "EAM"+appDesc);// 异构系统标识
					weChatJson.put("applyDept", applyDept);// 申请部门
					weChatJson.put("applicant", applicant);// 申请人
					weChatJson.put("approver", weChatId);
					String toJson = weChatJson.toString();
					String result = getResultOA("WFASSIGNMENT"+wfassignmentid, toJson, "2", MXServer.getMXServer().getProperty("guide.wechat.goct.url"));
					if ("200".equalsIgnoreCase(result)) {
						udoastatus = "已推送";
					}else {
						CommonUtil.ifaceLog(toJson, getUserInfo().getPersonId(), getName(), wfassignmentid + "", ownerid + "", result);
						udoastatus = "接口传输失败";
					}
					this.setValue("udoastatus", udoastatus, 11L);
				} catch (Exception e) {
					this.setValue("udoastatus", "接口请求失败", 11L);
					CommonUtil.ifaceLog("接口请求失败", getUserInfo().getPersonId(), getName(), wfassignmentid + "", ownerid + "", e.toString());
				}
			}
		} catch (Exception e) {
			this.setValue("udoastatus", udoastatus, 11L);
			MY_LOGGER.info(e);
		}
	}

	private void sendOA(String udhrusernum) throws MXException, RemoteException {
		String udoastatus = "内部异常";
		try {
			String wfassDesc = this.getString("description").replaceAll("[<>]", "").replaceAll("请审批", "").replaceAll("[+-//*&&[^.]&&[^(-]]"," ").replaceAll("'", "");
			String appDesc = this.getString("wfassignapp.description");
			int wfassignmentid = this.getInt("wfassignmentid");
			int ownerid = this.getInt("ownerid");
			String pcUrl = MXServer.getMXServer().getProperty("guide.oa.pcurl");
			String appUrl = MXServer.getMXServer().getProperty("guide.oa.appurl");
			Date sysdate = MXServer.getMXServer().getDate();
			String sysDateStr = CommonUtil.getDateFormat(sysdate, "yyyy-MM-dd HH:mm:ss");
			String app = this.getString("app");
			String nodename = this.getString("wfnode.title");
			String assigncode = this.getString("assigncode");
			String assignstatus = this.getString("assignstatus");
			String transtype = "失败";
			String maxUrl = "userName=" + assigncode + "&appName=" + app + "&recordId=" + ownerid;
//			String encodeBase64Str = Base64.encodeBase64String(maxUrl.getBytes(StandardCharsets.UTF_8));
			if (pcUrl != null && !pcUrl.isEmpty()) {
//				pcUrl += "?url=" + encodeBase64Str;
			}
			if (appUrl != null && !appUrl.isEmpty()) {
//				appUrl += "?url=" + encodeBase64Str;
			}
			JSONObject resJsonObj = new JSONObject();
			resJsonObj.put("syscode", "EAM");// 异构系统标识
			resJsonObj.put("flowid", wfassignmentid + "");// 流程任务id
			resJsonObj.put("requestname", wfassDesc);// 标题
			resJsonObj.put("workflowname", appDesc);// 流程类型名称
			resJsonObj.put("nodename", nodename);// 步骤名称（节点名称）
			resJsonObj.put("pcurl", pcUrl);// PC地址
			resJsonObj.put("appurl", appUrl);// APP地址
			String isremark = "0";
			if ("ACTIVE".equalsIgnoreCase(assignstatus)) {
				isremark = "0";
				transtype = "待办";
			} else if ("INACTIVE".equalsIgnoreCase(assignstatus)) {
				isremark = "4";
				transtype = "办结";
			} else if ("COMPLETE".equalsIgnoreCase(assignstatus)) {
				isremark = "2";
				transtype = "已办";
			}
			resJsonObj.put("isremark", isremark);// 流程处理状态 0：待办 2：已办 4：办结
			resJsonObj.put("viewtype", "0");// 流程查看状态 0：未读 1：已读
			resJsonObj.put("creator", getUserInfo().getPersonId());// 创建人 duxiaokun.csp 编号 27540169
			resJsonObj.put("createdatetime", sysDateStr);// 创建日期时间
			resJsonObj.put("receiver", udhrusernum);// 接收人（原值）duxiaokun.csp 编号 27540169
			resJsonObj.put("receivedatetime", sysDateStr);// 接收日期时间
			resJsonObj.put("receivets", System.currentTimeMillis()+"");
			String toJson = resJsonObj.toString();// 传给OA的JSON
			// 获取OA接口数据
//			Map<String, String> jsonMaps = OaWebService.getOfficeAuto(toJson);
//			String operResult = jsonMaps.get("operResult");
//			String message = jsonMaps.get("message");
//			if ("1".equalsIgnoreCase(operResult)) {
//				udoastatus = transtype;
//			}else {
//				CommonUtil.ifaceLog(toJson, getUserInfo().getPersonId(), getName(), wfassignmentid + "", ownerid + "", message);
//			}
			try {
				String result = getResultOA("WFASSIGNMENT"+wfassignmentid, toJson, "1", MXServer.getMXServer().getProperty("guide.oa.url"));
				if ("200".equalsIgnoreCase(result)) {
					udoastatus = transtype;
				}else {
					CommonUtil.ifaceLog(toJson, getUserInfo().getPersonId(), getName(), wfassignmentid + "", ownerid + "", result);
					udoastatus = "接口传输失败";
				}
			} catch (Exception e) {
				this.setValue("udoastatus", "接口请求失败", 11L);
				CommonUtil.ifaceLog("接口请求失败", getUserInfo().getPersonId(), getName(), wfassignmentid + "", ownerid + "", e.toString());
			}
			this.setValue("udoastatus", udoastatus, 11L);
		} catch (IOException | JSONException e) {
			this.setValue("udoastatus", udoastatus, 11L);
			MY_LOGGER.info(e);
		}
	}

	public String getResultOA(String Id, String toJson, String msg_type, String url) throws MXException, RemoteException, JSONException {
		//消息参数
		String flag = "\"result\":\"接口异常\"";
		String personId = getUserInfo().getPersonId();
		String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject jsonData = new JSONObject();
		jsonData.put("id", Id);
		jsonData.put("content", toJson);
		jsonData.put("msg_type", msg_type);
		jsonData.put("msg_url", url);
		jsonData.put("create_time", currentDate);
		jsonData.put("create_by", personId);
		jsonData.put("change_time", currentDate);
		jsonData.put("change_by", personId);
		//消息执行
		String returnResult = CommonUtil.sendGDEam(MXServer.getMXServer().getProperty("guide.gdoa.url"), jsonData);//:6001/v1/api/transfermsg
		String returnCode = CommonUtil.getString(new JSONObject(returnResult), "code");
		if(returnCode != null && returnCode.equalsIgnoreCase("200")){
			flag = returnCode;
		}else{
			flag = CommonUtil.getString(new JSONObject(returnResult), "result");
		}
		return flag;
	}
	
	
}
