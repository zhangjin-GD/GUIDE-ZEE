package guide.iface.webservice;

import guide.app.common.CommonUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

import psdi.app.doclink.Docinfo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.AppService;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.util.MaxType;
import psdi.workflow.WFActionSetRemote;
import psdi.workflow.WFInstanceRemote;
import psdi.workflow.WFInstanceSetRemote;
import psdi.workflow.WorkFlowServiceRemote;
import sun.misc.BASE64Decoder;


public class MobileService extends AppService implements MobileServiceRemote {

	public MobileService() throws RemoteException {
		super();
	}

	public MobileService(MXServer mxServer) throws RemoteException {
		super(mxServer);
	}
	
	private String succeedCode = "\"code\":\"1\"";
	private String failedCode = "\"code\":\"0\"";
	
//	http://123.60.22.73:7001/meaweb/wsdl/EAMMOBILE.wsdl
//	http://10.18.11.133/meaweb/wsdl/EAMMOBILE.wsdl
//	https://cspeam.coscoshipping.com/meaweb/wsdl/EAMMOBILE.wsdl
	
	@WebMethod
	public String WebServ(String userId, String langCode, String option, String data) throws RemoteException, MXException {
		data = CommonUtil.getStrDecode(data);
		String returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1033", langCode)+"\"}";
		userId = userId.toUpperCase();
		langCode = langCode.toUpperCase();
		option = option.toUpperCase();
		try {
			String checkMsg = getCheckHeaderMsg(userId, langCode, option, data);
			if(!checkMsg.equalsIgnoreCase(""))
				return checkMsg;
			
			UserInfo adminInfo = mxServer.getSystemUserInfo();
			adminInfo.setLangCode(langCode);
			UserInfo userInfo = mxServer.getUserInfo(userId);
			userInfo.setLangCode(langCode);
			
			if(option.equalsIgnoreCase("READ")){
				returnMsg = getData(userInfo, userId, langCode, new JSONObject(data));
			}/*else if(option.equalsIgnoreCase("WFCOMP")){
				returnMsg = getWFComp(userInfo, userId, langCode, new JSONObject(data));
			}*/else if(option.equalsIgnoreCase("LOGIN")){
				returnMsg = getLogin(userInfo, userId, langCode, new JSONObject(data));
			}else if(option.equalsIgnoreCase("GETPW")){
				returnMsg = getPassWord(userInfo, userId, langCode, new JSONObject(data));
			}else if(option.equalsIgnoreCase("ADD") || option.equalsIgnoreCase("SYNC")){
				System.out.println("\n-----------------"+userId+":"+langCode+option+":"+data);
				returnMsg = syncData(userInfo, userId, langCode, data);
			}else if(option.equalsIgnoreCase("MODIFY")){
				System.out.println("\n-----------------"+userId+":"+langCode+option+":"+data);
				returnMsg = modifyData(userInfo, userId, langCode, new JSONArray(data));
			}/*else if(option.equalsIgnoreCase("WFSTAR")){
				returnMsg = getWFStar(userInfo, userId, langCode, new JSONObject(data));
			}*/else if(option.equalsIgnoreCase("WF")){
				returnMsg = getWF(userInfo, userId, langCode, new JSONObject(data));
			}else if(option.equalsIgnoreCase("DELETE")){
				returnMsg = deleteData(userInfo, userId, langCode, new JSONArray(data));
			}else if(option.equalsIgnoreCase("ADDIMAGES")){
				returnMsg = uploadImage(userInfo, userId, langCode, new JSONArray(data));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return e.toString();
		}
		return returnMsg;
	}

	//[{"objectName": "INVUSE","keyName": "invusenum","keyValue": "1412","description": "领料-接口测试2203231713"}]
	private String modifyData(UserInfo userInfo, String userId, String langCode, JSONArray jsonSet) throws RemoteException, MXException, JSONException {
		String returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1027", langCode)+"\"}";
		String objectName = "";
		String keyName = "";
		String keyValue = "";
		MboSetRemote objectSet = null;
		MboRemote object = null;
		for (int i = 0; i < jsonSet.length(); i++) {
			JSONObject jsonData = jsonSet.getJSONObject(i);
			objectName = getString(jsonData, "objectName");
			keyName = getString(jsonData, "keyName");
			keyValue = getString(jsonData, "keyValue");
			objectSet = mxServer.getMboSet(objectName, userInfo);
			objectSet.setWhere(keyName+"='"+keyValue+"'");
			if(!objectSet.isEmpty() && objectSet.count()>0){
				object = objectSet.getMbo(0);
				Iterator keys = jsonData.keys();
				while (keys.hasNext()) {
					String key = keys.next().toString();
					if(key.equalsIgnoreCase("objectName") || key.equalsIgnoreCase("keyName") || key.equalsIgnoreCase("keyValue")){
						
					}else{
						String value = jsonData.getString(key);
						object.setValue(key, value, 2L);
					}
				}
				objectSet.save();
			}
		}
		objectSet.close();
		returnMsg = "{"+succeedCode+",\"msg\":\""+mxServer.getMessage("guide", "1038", langCode)+"\"}";
		return returnMsg;
	}

	//{"objectName": "WORKORDER","keyName": "wonum",keyValue": "keyValue","description": "吊具故障-接口测试","udassettypecode": "QC","assetnum": "QC001-NTTH","worktype": "EM","targstartdate": "2022-2-16 13:41:42","LineUDSAPITEM": [{"mtart": "1001","description": "接口明细1"}, {"mtart": "1002","description": "接口明细2"}]}
	//{"objectName": "INVUSE","keyName": "invusenum",keyValue": "keyValue","description": "领料-接口测试1","udapptype": "MATUSEOT","usetype": "ISSUE","UDMOVEMENTTYPE": "207","LineINVUSELINE": [{"itemnum": "8604010015","fromlot": "INIT"}, {"itemnum": "8601070010","fromlot": "INIT"}]}
	private String syncData(UserInfo userInfo, String userId, String langCode, String data) throws RemoteException, MXException, JSONException {
		String returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1037", langCode)+"\"}";
		JSONObject jsonData = new JSONObject(data);
		String objectName = getString(jsonData, "objectName");
		String keyName = getString(jsonData, "keyName");
		String keyValue = getString(jsonData, "keyValue");//keyValue
		MboSetRemote objectSet = mxServer.getMboSet(objectName, userInfo);
		objectSet.setWhere(keyName+"='"+keyValue+"'");
		MboRemote object = null;
		if(!objectSet.isEmpty() && objectSet.count()>0){
			object = objectSet.getMbo(0);
		}else{
			object = objectSet.add();
		}
		Map<String, Object> resultMap = JSON.parseObject(data, LinkedHashMap.class, Feature.OrderedField);
		for (Entry<String, Object> entry : resultMap.entrySet()) {
			String key = entry.getKey().toString();
			if(key.equalsIgnoreCase("objectName") || key.equalsIgnoreCase("keyName") || key.equalsIgnoreCase("keyValue")){
				keyValue = object.getString(keyName);
			}else if(key.startsWith("Line")){
				List value = (List) JSON.parse(entry.getValue().toString(), Feature.OrderedField);
				String lineFlag = getAddLineSet(object, key.replace("Line", ""), keyName, value);
				if(lineFlag != null)
					returnMsg = "{"+failedCode+",\"msg\":\""+lineFlag+"\"}";
			}else{
				String value = jsonData.getString(key);
				object.setValue(key, value, 2L);
			}
		}
		try {
			objectSet.save();
			returnMsg = "{"+succeedCode+",\"msg\":\""+keyValue+"\"}";
		} catch (Exception e) {
			returnMsg = "{"+failedCode+",\"msg\":\""+e.toString()+"\"}";
		}
		objectSet.close();
		return returnMsg;
	}
	
	private String getAddLineSet(MboRemote owner, String relationship, String keyName, List list) throws JSONException, RemoteException, MXException {
		String lineFlag = null;
		try {
			MboSetRemote objectSet = owner.getMboSet(relationship);
	 		MboRemote object = null;
	 		for(int i = 0 ;i<list.size(); i++) {
				object = objectSet.add();
				object.setValue(keyName, owner.getString(keyName), 2L);
				Map<String, Object> resultMap = JSON.parseObject(list.get(i).toString(), LinkedHashMap.class, Feature.OrderedField);
				for (Entry<String, Object> entry : resultMap.entrySet()) {
					String key = entry.getKey().toString();
					String value = entry.getValue().toString();
					object.setValue(key, value, 2L);
				}
			}
		} catch (Exception e) {
			lineFlag = e.toString();
		}
		return lineFlag;
	}

	
	
	private String getData(UserInfo userInfo, String userId, String langCode, JSONObject jsonData) throws RemoteException, MXException, JSONException {
		String returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1027", langCode)+"\"}";
		String keyNum = getString(jsonData, "keyNum");
		String appWhere = getString(jsonData, "sqlWhere");
		int startRow = Integer.parseInt(getString(jsonData, "startRow"));
		int endRow = Integer.parseInt(getString(jsonData, "endRow"));
		String sinorSearch = getString(jsonData, "sinorSearch");
		MboSetRemote mobileSerSet = mxServer.getMboSet("UDWEBSERVQUERY", userInfo);
		mobileSerSet.setWhere("keynum='"+keyNum+"'");
		if(!mobileSerSet.isEmpty() && mobileSerSet.count()>0){
			MboRemote mobileSer = mobileSerSet.getMbo(0);
			String objectName = mobileSer.getString("objectName");
			String sqlWhere = mobileSer.getString("sqlWhere");
			String orderBy = mobileSer.getString("orderBy");
			String keySearch = "and 1=1";
			if(sinorSearch !=null && !sinorSearch.equalsIgnoreCase(""))
				keySearch = mobileSer.getString("keySearch").replaceAll(":keyValue", sinorSearch);
			String[] attrs = mobileSer.getString("attrs").split(",");
			JSONArray jsonSet = new JSONArray();
			MboSetRemote objectSet = mxServer.getMboSet(objectName, userInfo);
			objectSet.setWhere(sqlWhere+" "+appWhere+" "+keySearch);
			if(orderBy != null && !orderBy.equalsIgnoreCase(""))
				objectSet.setOrderBy(orderBy);
			MboRemote object = null;
			if(!objectSet.isEmpty() && objectSet.count()>0){
				int objectCount = objectSet.count();
				if(startRow > 0 && endRow > startRow && objectCount >= startRow && objectCount >= endRow){//正常过滤
					startRow = startRow-1;
				}else if(startRow > 0 && endRow > startRow && objectCount >= startRow && objectCount < endRow){//到最大条
					startRow = startRow-1;
					endRow = objectCount;
				}else {//无返回
					startRow = 0;
					endRow = 0;
				}
				for(int i=startRow;i<endRow;i++){
					object = objectSet.getMbo(i);
					JSONObject json = new JSONObject();
					for (int j = 0; j < attrs.length; j++) {
						json.put(attrs[j].split(":")[0], object.getString(attrs[j].split(":")[1]));
					}
					json.put("ownerTable", object.getName());
					json.put("ownerId", ""+object.getUniqueIDValue()+"");
					if(keyNum.equalsIgnoreCase("1005")){
						json.put("ownerTable", object.getString("ownertable"));
						json.put("ownerId", ""+object.getInt("ownerid")+"");
						json.put("ownerWhere", getOwnerWhere(object));
					}else if(keyNum.equalsIgnoreCase("1008")){
						json.put("urlname", getDocPath(json.getString("urlname"), "mxe.doclink.path02"));
					}
					jsonSet.put(json);
				}
				returnMsg = "{"+succeedCode+",\"msg\":"+jsonSet.toString().replaceAll("<", " ").replaceAll(">", " ")+",\"total\":\""+objectCount+"\"}";
			}
			objectSet.close();
		}else{
			returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1028", langCode)+"\"}";
		}
		return returnMsg;
	}
	
	private String getDocPath(String urlName, String propName) {
		String propValue = mxServer.getProperty(propName);
		String docPath = propValue.split("=")[0].trim();
		String url = propValue.split("=")[1].trim();
		if(url.length() > 1)
			url = url.substring(0, url.length() - 1);
		urlName = urlName.replace(docPath, url).replace("\\", "/");
		return urlName;
	}

	private String getOwnerWhere(MboRemote object) throws RemoteException, MXException {
		String ownerWhere = "1=2";
		MboSetRemote maxtableSet = MXServer.getMXServer().getMboSet("MAXTABLE", MXServer.getMXServer().getSystemUserInfo());
		maxtableSet.setWhere("tablename='"+object.getString("ownertable")+"'");
		if(!maxtableSet.isEmpty() && maxtableSet.count()>0){
			String tableName = object.getString("ownertable");
			MboSetRemote maxattributeSet = MXServer.getMXServer().getMboSet("MAXATTRIBUTE", MXServer.getMXServer().getSystemUserInfo());
			maxattributeSet.setWhere("objectname='"+tableName+"' and autokeyname is not null");
			if(!maxattributeSet.isEmpty() && maxattributeSet.count()>0){
				String autokeyName = maxattributeSet.getMbo(0).getString("attributename");
				String idName = maxtableSet.getMbo(0).getString("uniquecolumnname");
				int tableId = object.getInt("ownerid");
				ownerWhere = " and "+autokeyName+"='"+CommonUtil.getValue(tableName, idName+"="+tableId, autokeyName)+"'";
			}
			maxattributeSet.close();
		}
		maxtableSet.close();
		return ownerWhere;
	}
	
	private String getLogin(UserInfo adminInfo, String userId, String langCode, JSONObject jsonData) throws JSONException, RemoteException, MXException {
		String returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1025", langCode)+"\"}";
		String passWord = getString(jsonData, "passWord");
		MboSetRemote maxuserSet = mxServer.getMboSet("MAXUSER", adminInfo);
		maxuserSet.setWhere("userid='"+userId+"'");
		if(!maxuserSet.isEmpty() && maxuserSet.count()>0){
			JSONObject json = new JSONObject();
			MboRemote maxuser = maxuserSet.getMbo(0);
			byte[] storedPassword = maxuser.getBytes("password");
			MaxType storedMT = MaxType.createMaxType(null, null, 16);
			storedMT.setValue(storedPassword);
			byte[] enteredPasswordBytes = mxServer.getMXCipherX().encData(passWord);
			MaxType enteredMT = MaxType.createMaxType(null, null, 16);
			enteredMT.setValue(enteredPasswordBytes);
			if (storedMT.equals(enteredMT)){
				json.put("description", mxServer.getMessage("guide", "1026", langCode)); 
				json.put("name", maxuser.getString("PERSON.displayname"));
				returnMsg = "{"+succeedCode+",\"msg\":"+json.toString()+"}";
			}else{
				returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("signature", "badVerify", langCode)+"\"}";
			}
		}
		maxuserSet.close();
		return returnMsg;
	}
	
	private String getPassWord(UserInfo adminInfo, String userId, String langCode, JSONObject jsonData) throws JSONException, RemoteException, MXException {
		String loginId = getString(jsonData, "loginId");
		String returnMsg = "{"+failedCode+",\"msg\":\""+loginId+"\"}";
		MboSetRemote maxuserSet = mxServer.getMboSet("MAXUSER", adminInfo);
		maxuserSet.setWhere("loginid='"+loginId+"'");
		if(!maxuserSet.isEmpty() && maxuserSet.count()>0){
			JSONObject json = new JSONObject();
			MboRemote maxuser = maxuserSet.getMbo(0);
			try {
				String passWord = CommonUtil.getPassWord(maxuser.getBytes("password"));
				System.out.println("\n-------------"+passWord);
				json.put("passWord", passWord);
				json.put("eMail", maxuser.getString("PERSON.primaryemail"));
				returnMsg = "{"+succeedCode+",\"msg\":"+json.toString()+"}";
			} catch (SQLException e) {
				returnMsg = "{"+failedCode+",\"msg\":\""+e.toString()+"\"}";
			} catch (ParseException e) {
				returnMsg = "{"+failedCode+",\"msg\":\""+e.toString()+"\"}";
			}
		}else{
			returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1119", langCode)+"\"}";
		}
		maxuserSet.close();
		return returnMsg;
	}
	
	private String getCheckHeaderMsg(String userId, String langCode, String option, String data) throws RemoteException, MXException {
		String returnMsg = "";
		if (userId == null || userId.equalsIgnoreCase("")) {
			returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1022", langCode)+"\"}";
		}else if(CommonUtil.getMaxUser(userId) == null){
			returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1025", langCode)+"\"}";
		}
		if (langCode == null || langCode.equalsIgnoreCase("")) {
			returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1021", "ZH")+mxServer.getMessage("guide", "1021", "EN")+"\"}";
		}
		if (option == null || option.equalsIgnoreCase("")) {
			returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1023", langCode)+"\"}";
		}
		if (data == null || data.equalsIgnoreCase("")) {
			returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1008", langCode)+"\"}";
		}
		return returnMsg;
	}
	
	private String getString(JSONObject js, String attr) throws JSONException {
		String lsrtn = "";
		Object object = js.get(attr);
		if (object != null)
			lsrtn = object.toString();
		return lsrtn;
	}
	

	private String getWFComp(UserInfo userInfo, String userId, String langCode, JSONObject jsonData) throws RemoteException, MXException, JSONException {
		String returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1027", langCode)+"\"}";
		String objectName = getString(jsonData, "objectName");
		String sqlWhere = getString(jsonData, "sqlWhere");
		String isPositive = getString(jsonData, "isPositive");
		String memo = getString(jsonData, "memo");
		MboSetRemote objectSet = mxServer.getMboSet(objectName, userInfo);
		objectSet.setWhere("1=1 "+sqlWhere);
		if(!objectSet.isEmpty() && objectSet.count()>0){
			MboRemote object = objectSet.getMbo(0);
			if(CommonUtil.isAssign(object, userId)){
				WFInstanceSetRemote wfInstanceSet = ((WorkFlowServiceRemote) mxServer.lookup("WORKFLOW")).getActiveInstances(object);
				if(!wfInstanceSet.isEmpty() && wfInstanceSet.count()>0){
					WFInstanceRemote wfInstanceRemote = (WFInstanceRemote) wfInstanceSet.getMbo(0);
					int actionid = 0;
					WFActionSetRemote actionSet = wfInstanceRemote.getActions();
					if(!actionSet.isEmpty() && actionSet.count()>0){
						MboRemote action = null;
						for(int i=0;(action=actionSet.getMbo(i))!=null;i++){
							if (isPositive.equalsIgnoreCase(action.getString("ispositive"))) {
								actionid = action.getInt("actionid");
								try {
									wfInstanceRemote.completeWorkflowAssignment(CommonUtil.getWfassignId(object, userId), actionid, memo);
									returnMsg = "{"+succeedCode+",\"msg\":\""+mxServer.getMessage("guide", "1032", langCode)+"\"}";
								} catch (Exception e) {
									returnMsg = "{"+failedCode+",\"msg\":\""+e.toString()+"\"}";
								}
							}
						}
						if(actionid == 0){
							returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1031", langCode)+"\"}";
						}
					}else{
						returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1030", langCode)+"\"}";
					}
				}else{
					returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1029", langCode)+"\"}";
				}
			}else{
				returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1024", langCode)+"\"}";
			}
		}
		objectSet.close();
		return returnMsg;
	}
	
	
	//{"objectName":"PR","sqlWhere":" and prnum='1039'","processname":"UDMATUSEOT"}
	private String getWFStar(UserInfo userInfo, String userId, String langCode, JSONObject jsonData) throws RemoteException, MXException, JSONException {
		String returnMsg = "{" + failedCode + ",\"msg\":\"" + mxServer.getMessage("guide", "1027", langCode) + "\"}";
		String objectName = getString(jsonData, "objectName");
		String sqlWhere = getString(jsonData, "sqlWhere");
		String processname = getString(jsonData, "processname");
		MboSetRemote objectSet = mxServer.getMboSet(objectName, userInfo);
		objectSet.setWhere("1=1 " + sqlWhere);
		if (!objectSet.isEmpty() && objectSet.count() > 0) {
			try {
				MboRemote object = objectSet.getMbo(0);
				((WorkFlowServiceRemote) MXServer.getMXServer().lookup("WORKFLOW")).initiateWorkflow(processname, object);
				returnMsg = "{" + succeedCode + ",\"msg\":\"" + mxServer.getMessage("guide", "1032", langCode) + "\"}";
			} catch (Exception e) {
				returnMsg = "{" + failedCode + ",\"msg\":\"" + e.toString() + "\"}";
			}

		}

		objectSet.close();
		return returnMsg;
	}
	

	/**
	 * 工作流
	 * @创建人: pr
	 * @创建时间:2022年4月26日
	 * @修改时间:
	 */
	//{"objectName":"PR","sqlWhere":" and prnum='1039'","isPositive":"Y","memo":"同意","processname":"UDMATUSEOT"}
	private String getWF(UserInfo userInfo, String userId, String langCode, JSONObject jsonData) throws RemoteException, MXException, JSONException {
		String returnMsg = "{" + failedCode + ",\"msg\":\"" + mxServer.getMessage("guide", "1027", langCode) + "\"}";
		String objectName = getString(jsonData, "objectName");
		String sqlWhere = getString(jsonData, "sqlWhere");
		String isPositive = getString(jsonData, "isPositive");
		String memo = getString(jsonData, "memo");
		String processname = getString(jsonData, "processname");
		
		MboSetRemote objectSet = mxServer.getMboSet(objectName, userInfo);
		objectSet.setWhere("1=1 " + sqlWhere);
		if (!objectSet.isEmpty() && objectSet.count() > 0) {
			
			MboRemote object = objectSet.getMbo(0);
			
			if (CommonUtil.isInWF(object)) {// 审批

				if (CommonUtil.isAssign(object, userId)) {
					WFInstanceSetRemote wfInstanceSet = ((WorkFlowServiceRemote) mxServer.lookup("WORKFLOW")).getActiveInstances(object);
					if (!wfInstanceSet.isEmpty() && wfInstanceSet.count() > 0) {
						WFInstanceRemote wfInstanceRemote = (WFInstanceRemote) wfInstanceSet.getMbo(0);
						int actionid = 0;
						WFActionSetRemote actionSet = wfInstanceRemote.getActions();
						if (!actionSet.isEmpty() && actionSet.count() > 0) {
							MboRemote action = null;
							for (int i = 0; (action = actionSet.getMbo(i)) != null; i++) {
								if (isPositive.equalsIgnoreCase(action.getString("ispositive"))) {
									actionid = action.getInt("actionid");
									try {
										wfInstanceRemote.completeWorkflowAssignment(
												CommonUtil.getWfassignId(object, userId), actionid, memo);
										returnMsg = "{" + succeedCode + ",\"msg\":\""
												+ mxServer.getMessage("guide", "1032", langCode) + "\"}";
									} catch (Exception e) {
										returnMsg = "{" + failedCode + ",\"msg\":\"" + e.toString() + "\"}";
									}
								}
							}
							if (actionid == 0) {
								returnMsg = "{" + failedCode + ",\"msg\":\""
										+ mxServer.getMessage("guide", "1031", langCode) + "\"}";
							}
						} else {
							returnMsg = "{" + failedCode + ",\"msg\":\""
									+ mxServer.getMessage("guide", "1030", langCode) + "\"}";
						}
					} else {
						returnMsg = "{" + failedCode + ",\"msg\":\"" + mxServer.getMessage("guide", "1029", langCode)+ "\"}";
					}
				} else {
					returnMsg = "{" + failedCode + ",\"msg\":\"" + mxServer.getMessage("guide", "1024", langCode)+ "\"}";
				}

			} else {// 启动

				try {
					((WorkFlowServiceRemote) MXServer.getMXServer().lookup("WORKFLOW")).initiateWorkflow(processname,
							object);
					returnMsg = "{" + succeedCode + ",\"msg\":\"" + mxServer.getMessage("guide", "1032", langCode)+ "\"}";
				} catch (Exception e) {
					returnMsg = "{" + failedCode + ",\"msg\":\"" + e.toString() + "\"}";
				}
			}

		}

		objectSet.close();
		return returnMsg;
	}

	//[{"objectName": "INVUSE","keyName": "invusenum","keyValue": "1412"}]
	private String deleteData(UserInfo userInfo, String userId, String langCode, JSONArray jsonSet) throws RemoteException, MXException, JSONException {
		String returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1027", langCode)+"\"}";
		String objectName = "";
		String keyName = "";
		String keyValue = "";
		MboSetRemote objectSet = null;
		for (int i = 0; i < jsonSet.length(); i++) {
			JSONObject jsonData = jsonSet.getJSONObject(i);
			objectName = getString(jsonData, "objectName");
			keyName = getString(jsonData, "keyName");
			keyValue = getString(jsonData, "keyValue");
			objectSet = mxServer.getMboSet(objectName, userInfo);
			objectSet.setWhere(keyName+"='"+keyValue+"'");
			if(!objectSet.isEmpty() && objectSet.count()>0){
				objectSet.deleteAll(11L);
				objectSet.save();
			}
		}
		objectSet.close();
		returnMsg = "{"+succeedCode+",\"msg\":\""+mxServer.getMessage("guide", "1038", langCode)+"\"}";
		return returnMsg;
	}
	
	
	public String uploadImage(UserInfo userInfo, String userId, String langCode, JSONArray jsonSet) throws RemoteException, MXException, JSONException {
		String returnMsg = "{"+failedCode+",\"msg\":\""+mxServer.getMessage("guide", "1102", langCode)+"\"}";
		for (int i = 0; i < jsonSet.length(); i++) {
			JSONObject jsonData = jsonSet.getJSONObject(i);
			returnMsg = writeImage(userInfo, langCode, jsonData);
		}
		return returnMsg;
	}
	
	
	private String writeImage(UserInfo userInfo, String langCode, JSONObject jsonData) throws JSONException {
		String num = getString(jsonData, "num");
		String image = getString(jsonData, "image");
		String fileName = num+"-"+getString(jsonData, "fileName");
		String ownerTable = getString(jsonData, "ownerTable");
		String ownerId = getString(jsonData, "ownerId");
		String docType = getString(jsonData, "docType");
		FileOutputStream fos = null;
		try {
			// 存储路径
			String docpath = MXServer.getMXServer().getProperty("mxe.doclink.doctypes.defpath");
			String toDir = docpath+"\\ATTACHMENTS\\ITEM\\";
			
			File destDir = new File(toDir);
			if (!destDir.exists()) {
				destDir.mkdir();
			}
			// 对android传过来的图片字符串进行解码
			byte[] buffer = new BASE64Decoder().decodeBuffer(image);
			// 处理数据
			for(int i=0;i< buffer.length; i++){//调整异常数据
				 if(buffer[i]<0){
					 buffer[i] +=256;
				 }
			}
			
			// 保存图片
			fos = new FileOutputStream(new File(destDir, fileName));
			fos.write(buffer);
			fos.flush();
			fos.close();
			//创建DOCINFO，DOCLINKS记录
			try {
				if (fileName != null && !fileName.equalsIgnoreCase("") && ownerTable != null && !ownerTable.equalsIgnoreCase("")) {
					MboSetRemote docinfoSet = MXServer.getMXServer().getMboSet("DOCINFO", userInfo);
					Docinfo docinfo = (Docinfo) docinfoSet.add();
					docinfo.getMboValue("DOCUMENT").autoKey();
					docinfo.setValue("DESCRIPTION", fileName);
					docinfo.setValue("newurlname", toDir + fileName);//测试与正式系统图片路径
	                docinfo.setValue("URLNAME", toDir + fileName);//测试与正式系统图片路径
					docinfo.setValue("DOCTYPE", docType);
					docinfo.setValue("URLTYPE", "FILE");
					docinfo.setValue("SHOW", 0);
					docinfoSet.save();
					int docinfoid = docinfo.getInt("DOCINFOID");
					String document = docinfo.getString("DOCUMENT");
					docinfoSet.close();

					MboSetRemote doclinksSet = MXServer.getMXServer().getMboSet("DOCLINKS", userInfo);
					MboRemote doclinks = doclinksSet.add();
					doclinks.setValue("DOCUMENT", document, 11L);
					doclinks.setValue("OWNERTABLE", ownerTable);
					doclinks.setValue("OWNERID", ownerId);
					doclinks.setValue("DOCTYPE", docType);
					doclinks.setValue("DOCINFOID", docinfoid);
					doclinksSet.save();
					doclinksSet.close();
				}
			} catch (RemoteException e) {
				return "{" + failedCode + ",\"msg\":\"" + e.toString() + "\"}";
			} catch (MXException e) {
				return "{" + failedCode + ",\"msg\":\"" + e.toString() + "\"}";
			}
			return "{"+succeedCode+",\"msg\":\""+mxServer.getMessage("guide", "1103", langCode)+"\"}";
		} catch (Exception e) {
			return "{" + failedCode + ",\"msg\":\"" + e.toString() + "\"}";
		}
	}
	

}
