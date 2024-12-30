package guide.webclient.beans.invuse;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.CommonUtil;
import guide.app.inventory.UDInvUse;
import guide.app.inventory.UDInvUseLine;
import guide.app.inventory.UDInvUseSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;
import psdi.webclient.system.controller.WebClientEvent;

public class UDFuelstAppBean extends AppBean {

	public int goctFuel() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();

		String status = mbo.getString("status");
		if (status == null || !status.equalsIgnoreCase("WAPPR")) {
			Object params[] = { "提示，当前状态不允许执行同步操作！" };
			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
		}

		Date startDate = mbo.getDate("startdate");
		Date endDate = mbo.getDate("enddate");
		if (startDate == null || endDate == null) {
			Object params[] = { "提示，请填写查询的开始时间及截至时间！" };
			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
		}

		String flag = "提示，油站数据同步失败！";
		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
			eventValue = "1045";
		}
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			endDate = calendar.getTime();

			JSONObject returnData = CommonUtil.getFuelGoct(eventValue, startDate, endDate);
			String result = returnData.getString("result");
			if (result != null && result.equalsIgnoreCase("Y")) {
				JSONArray fuelDataSet = new JSONArray(returnData.getString("data"));
				String fuelstnum = mbo.getString("fuelstnum");
				MboRemote fuelStation = null;
				MboSetRemote fuelStationSet = mbo.getMboSet("UDFUELSTATION");
				if (!fuelStationSet.isEmpty() && fuelStationSet.count() > 0) {
					fuelStationSet.deleteAll();
				}
				int fuelCount = fuelDataSet.length();
				for (int i = 0; i < fuelDataSet.length(); i++) {
					JSONObject fuelData = fuelDataSet.getJSONObject(i);
					fuelStation = fuelStationSet.add();
					fuelStation.setValue("fuelstnum", fuelstnum, 11L);
					fuelStation.setValue("carnum", fuelData.getString("CarNum"), 11L);
					fuelStation.setValue("changetime", MXServer.getMXServer().getDate(), 11L);
					fuelStation.setValue("quantity", fuelData.getString("OnceLiter"), 11L);
					fuelStation.setValue("companyName", fuelData.getString("CompanyName"), 11L);
					fuelStation.setValue("assetnum", CommonUtil.getValue(fuelStation, "ASSET", "assetnum"), 11L);
					fuelStation.setValue("starttime", startDate, 11L);
					fuelStation.setValue("endtime", endDate, 11L);
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
		mbo.setValue("changetime", MXServer.getMXServer().getDate(), 11L);
		app.getAppBean().save();
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", flag, 1);
		return 1;
	}

	public int syncInvuseline() throws MXException, RemoteException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}

		String status = mbo.getString("status");
		if (status == null || !status.equalsIgnoreCase("WAPPR")) {
			Object params[] = { "提示，当前状态不允许执行同步操作！" };
			throw new MXApplicationException("instantmessaging", "tsdimexception", params);
		}

		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
			eventValue = "7101010004";
		}

		MboSetRemote fuelBalSet = mbo.getMboSet("$UDFUELBAL", "INVBALANCES",
				"itemnum='" + eventValue + "' and location=:location and curbal>0");
		if (!fuelBalSet.isEmpty() && fuelBalSet.count() > 0) {
			if (mbo.getDouble("fuelqty") > fuelBalSet.sum("curbal")) {
				throw new MXApplicationException("guide", "1049");
			}
		} else {
			throw new MXApplicationException("guide", "1049");
		}

		String movementType = "207";
		UDInvUseSet invuseSet = (UDInvUseSet) mbo.getMboSet("$INVUSE", "INVUSE", "1=2");

		MboSetRemote listSet = mbo.getMboSet("UDFUELSTATION");
		listSet.setWhere("assetnum is not null");
		listSet.setOrderBy("companyname,assetnum");
		listSet.reset();
		// 去重
		HashSet<String> hashs = new HashSet<String>();
		if (!listSet.isEmpty() && listSet.count() > 0) {
			for (int i = 0; listSet.getMbo(i) != null; i++) {
				MboRemote list = listSet.getMbo(i);
				String companyname = list.getString("companyname");
				hashs.add(companyname);
			}
		}
		if (hashs.size() > 0) {
			int num = 0;
			String fuelstnum = mbo.getString("fuelstnum");
			String createby = mbo.getString("createby");
			String location = mbo.getString("location");
			for (String hash : hashs) {
				MboSetRemote fuelstAtionSet = mbo.getMboSet("$UDFUELSTATION" + num, "UDFUELSTATION",
						"fuelstnum='" + fuelstnum + "' and companyname='" + hash + "' and assetnum is not null");
				fuelstAtionSet.setOrderBy("assetnum");
				if (!fuelstAtionSet.isEmpty() && fuelstAtionSet.count() > 0) {
					UDInvUse invuse = (UDInvUse) invuseSet.add();
					invuse.setValue("udcreateby", createby, 2L);
					invuse.setValue("udapptype", "MATUSEOT", 11L);
					invuse.setValue("udmovementtype", movementType, 2L);
					invuse.setValue("fromstoreloc", location, 2L);
					invuse.setValue("udusestatus", "COMMONLY", 2L);
					invuse.setValue("description", "站内发出燃油 " + hash, 11L);

					String invusenum = invuse.getString("invusenum");
					MboSetRemote invuseLineSet = invuse.getMboSet("INVUSELINE");
					for (int i = 0; fuelstAtionSet.getMbo(i) != null; i++) {
						MboRemote fuelStation = fuelstAtionSet.getMbo(i);
						String assetnum = fuelStation.getString("assetnum");
						double quantity = fuelStation.getDouble("quantity");

						UDInvUseLine invuseLise = (UDInvUseLine) invuseLineSet.add();
						invuseLise.setValue("itemnum", eventValue, 2L);
						invuseLise.setValue("quantity", quantity, 2L);
						invuseLise.setValue("assetnum", assetnum, 2L);
						invuseLise.setValue("udordertype", CommonUtil.getValue(fuelStation, "ASSETNUM", "udordertype"),
								11L);
						fuelStation.setValue("invusenum", invusenum, 11L);
					}
				}
				num++;
			}
		}

		mbo.setValue("status", "APPR", 11L);
		app.getAppBean().save();
		return 1;
	}

}
