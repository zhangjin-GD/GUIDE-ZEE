package guide.webclient.beans.invuse;

import guide.app.common.CommonUtil;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.invusage.InvUseAppBean;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;

public class UDInvUseAppBean extends InvUseAppBean {

	public void udmatby1() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		String usestatus = mbo.getString("UDUSESTATUS");
		if (!mbo.isNull("udmatby1")) {
			throw new MXApplicationException("guide", "1059");
		}
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		if (!"EMERGENCY".equalsIgnoreCase(usestatus)) {
			throw new MXApplicationException("guide", "1058");
		}
	}

	public int sign() throws MXException, RemoteException, MalformedURLException {
		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
			eventValue = "http://221.234.36.40:9999/03.html?id=";
		}
		MboRemote mbo = app.getAppBean().getMbo();
//		clientSession.gotoApplink(new URL(new URL(request.getRequestURL().toString()),request.getContextPath() + "/ui/maximo.jsp?event=loadapp&value="+mbo.getName()+"&uniqueid="+mbo.getUniqueIDValue()).toString());
		String url = eventValue + mbo.getString("invusenum");
		this.app.openURL(url, true);
		return 1;
	}

	public int signView() throws MXException, RemoteException, MalformedURLException {
		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
			eventValue = "http://221.234.36.40:6001/upload/";
		}
		MboRemote mbo = app.getAppBean().getMbo();
//		clientSession.gotoApplink(new URL(new URL(request.getRequestURL().toString()),request.getContextPath() + "/ui/maximo.jsp?event=loadapp&value="+mbo.getName()+"&uniqueid="+mbo.getUniqueIDValue()).toString());
		String url = eventValue + mbo.getString("invusenum") + ".png";
		this.app.openURL(url, true);
		return 1;
	}

	public int goctFuel() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		Date startTime = mbo.getDate("udstarttime");
		Date endTime = mbo.getDate("udendtime");
		if (startTime == null || endTime == null) {
			Object params[] = { "提示，请填写查询的开始时间及截至时间！" };
			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
		}
		String status = mbo.getString("status");
		if (status == null || !status.equalsIgnoreCase("ENTERED")) {
			Object params[] = { "提示，当前状态不允许执行同步操作！" };
			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
		}
		String flag = "提示，油站数据同步失败！";
		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
			eventValue = "1045";
		}
		try {
			endTime = CommonUtil.getCalDate(endTime, 1);
			JSONObject returnData = CommonUtil.getFuelGoct(eventValue, startTime, endTime);
			String result = returnData.getString("result");
			if (result != null && result.equalsIgnoreCase("Y")) {
				JSONArray fuelDataSet = new JSONArray(returnData.getString("data"));
				System.out.println("\n-------------------------" + fuelDataSet.toString());
				String invusenum = mbo.getString("invusenum");
				MboRemote fuelStation = null;
				MboSetRemote fuelStationSet = mbo.getMboSet("UDFUELSTATION");
				if (!fuelStationSet.isEmpty() && fuelStationSet.count() > 0) {
					fuelStationSet.deleteAll();
				}
				int fuelCount = fuelDataSet.length();
				for (int i = 0; i < fuelDataSet.length(); i++) {
					JSONObject fuelData = fuelDataSet.getJSONObject(i);
					fuelStation = fuelStationSet.add();
					fuelStation.setValue("invusenum", invusenum, 11L);
					fuelStation.setValue("carnum", fuelData.getString("CarNum"), 11L);
					fuelStation.setValue("changetime", MXServer.getMXServer().getDate(), 11L);
					fuelStation.setValue("quantity", fuelData.getString("OnceLiter"), 11L);
					fuelStation.setValue("companyName", fuelData.getString("CompanyName"), 11L);
					fuelStation.setValue("assetnum", CommonUtil.getValue(fuelStation, "ASSET", "assetnum"), 11L);
					fuelStation.setValue("starttime", startTime, 11L);
					fuelStation.setValue("endtime", endTime, 11L);
				}
				fuelStationSet.save();
				fuelStationSet.close();
				flag = "提示，" + fuelCount + "条记录同步成功！";
			} else {
				flag += result;
			}
		} catch (JSONException e) {
			flag += e.toString();
			e.printStackTrace();
		}
		mbo.setValue("changedate", MXServer.getMXServer().getDate(), 11L);
		app.getAppBean().save();
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", flag, 1);
		return 1;
	}

	public int syncInvuseline() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}

		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
			eventValue = "7101010004";
		}

		MboSetRemote fuelBalSet = mbo.getMboSet("$UDFUELBAL", "INVBALANCES",
				"itemnum='" + eventValue + "' and location=:fromstoreloc and curbal>0");
		if (!fuelBalSet.isEmpty() && fuelBalSet.count() > 0) {
			if (mbo.getDouble("udfuelqty") > fuelBalSet.sum("curbal")) {
				throw new MXApplicationException("guide", "1049");
			}
		} else {
			throw new MXApplicationException("guide", "1049");
		}

		DataBean invuseline_table = app.getDataBean("main_invuselinetab_table");
		String movementType = mbo.getString("udmovementtype");
		MboSetRemote alCreatedSet = mbo.getMboSet("UDALCREATED");
		MboSetRemote notCreatedSet = mbo.getMboSet("UDNOTCREATED");
		if ((alCreatedSet.isEmpty() && notCreatedSet.isEmpty()) || movementType == null
				|| !movementType.equalsIgnoreCase("207")) {
			Object params[] = { "提示，找不到需要创建的领料数据！" };
			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
		}

		int alCount = 0;
		int notCount = 0;
		MboSetRemote invuselineSet = mbo.getMboSet("INVUSELINE");
		MboRemote invuseline = null;

		if (!alCreatedSet.isEmpty() && alCreatedSet.count() > 0) {
			alCount = alCreatedSet.count();
			MboRemote alCreated = null;
			HashMap<String, Double> carMap = new HashMap<String, Double>();
			for (int i = 0; (alCreated = alCreatedSet.getMbo(i)) != null; i++) {
				carMap.put(alCreated.getString("assetnum"), alCreated.getDouble("quantity"));
			}
			for (int i = 0; (invuseline = invuselineSet.getMbo(i)) != null; i++) {
				invuseline.setValue("quantity", carMap.get(invuseline.getString("assetnum")), 2L);
			}
		}

		if (!notCreatedSet.isEmpty() && notCreatedSet.count() > 0) {
			notCount = notCreatedSet.count();
			MboRemote notCreated = null;
			for (int i = 0; (notCreated = notCreatedSet.getMbo(i)) != null; i++) {
				invuseline = invuselineSet.add();
				invuseline.setValue("itemnum", eventValue, 2L);
				invuseline.setValue("quantity", notCreated.getString("quantity"), 2L);
				invuseline.setValue("assetnum", notCreated.getString("assetnum"), 2L);
			}
		}

		invuseline_table.reloadTable();
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示",
				"提示，领料数据已更新" + alCount + "条，创建" + notCount + "条！", 1);
		return 1;
	}

}
