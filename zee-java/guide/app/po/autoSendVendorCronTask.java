package guide.app.po;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class autoSendVendorCronTask extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			String sqlWhere = getParamAsString("sqlWhere");
			MXServer server = MXServer.getMXServer();
			UserInfo userInfo = MXServer.getMXServer().getSystemUserInfo();
			userInfo.setLangCode("ZH");
			MboSetRemote matrectransSet = server.getMboSet("MATRECTRANS", userInfo);
			matrectransSet.setWhere(sqlWhere);
			if (!matrectransSet.isEmpty() && matrectransSet.count() > 0) {
				int count = matrectransSet.count();
				MboRemote matrectrans = null;
				for (int i = 0; count > i; i++) {
					matrectrans = matrectransSet.getMbo(0);
					matrectrans.setValue("udnotifystatus", sendEmail(matrectrans), 11L);
					matrectransSet.save();
				}
			}
			matrectransSet.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}

	}

	private String sendEmail(MboRemote matrectrans) {
		try {
			MboSetRemote recNumSet = matrectrans.getMboSet("UDRECNUM");
			MboSetRemote poSet = matrectrans.getMboSet("PO");
			if (!recNumSet.isEmpty() && recNumSet.count() > 0 && !poSet.isEmpty() && poSet.count() > 0) {
				MboRemote po = poSet.getMbo(0);
				String recNum = matrectrans.getString("udrecnum");
				String toAddress = CommonUtil.getValue(matrectrans, "VENDOR", "udemail");
				String purchase = po.getString("PURCHASE.displayname");
				String title = "订单"+matrectrans.getString("ponum")+"，已"+po.getString("RECEIPTS.description").replace("部件", "")+"，请开具相应发票。";
				String message = "订单编号："+matrectrans.getString("ponum")+"，采购员："+purchase+"；"
						+ "\n订单不含税金额："+po.getDouble("pretaxtotal")+"，订单税额："+po.getDouble("totaltax1")+"；"
						+ "\n本次开票不含税金额："+recNumSet.sum("linecost")+"，本次开票税额："+recNumSet.sum("tax1")+"。";
				if(toAddress != null && !toAddress.equalsIgnoreCase("")){
					//消息参数
					String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
					JSONObject jsonData = new JSONObject();
					jsonData.put("id", recNum);
					jsonData.put("to_user", toAddress);
					jsonData.put("subject", title);
					jsonData.put("content", message);
					jsonData.put("create_time", currentDate);
					jsonData.put("create_by", purchase);
					jsonData.put("change_time", currentDate);
					jsonData.put("change_by", purchase);
					jsonData.put("file_path", "");
					System.out.println("\n----------------------"+jsonData);
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
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MXException e1) {
			e1.printStackTrace();
		}
		return "内部错误";
	}

}
