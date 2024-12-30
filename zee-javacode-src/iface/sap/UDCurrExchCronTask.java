package guide.iface.sap;

import guide.app.common.CommonUtil;
import guide.iface.sap.webservice.ItemWebServiceOfCurrency;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class UDCurrExchCronTask extends SimpleCronTask {

	public UDCurrExchCronTask() throws RemoteException, MXException {

	}

	public void cronAction() {
		try {
			System.out.println("----开始 实时汇率---");
			String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status.currency");
			String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
			System.out.println("\nCronCurrExch-----------status" + sapStatus + "-----------debug" + sapDebug);
			if (sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
				MboSetRemote currExchlistSet = MXServer.getMXServer().getMboSet("UDCURREXCHLIST",
						MXServer.getMXServer().getSystemUserInfo());
				if (!currExchlistSet.isEmpty() && currExchlistSet.count() > 0) {
					MboRemote currExchlist = null;
					JSONObject Header = new JSONObject();
					JSONObject Item = new JSONObject();
					String crdate = CommonUtil.getCurrentDateFormat("yyyyMMdd");
					String crname = "";
					String zrtoall = "1";
					String packgid = CommonUtil.getCurrentDateFormat("yyyyMMddHHmmss");
					String itemid = "1";
					String kurst = "Z";
					Date monthLastDay = getMonth(-1);
					String gdatu = CommonUtil.getDateFormat(monthLastDay, "yyyyMMdd");
					for (int i = 0; (currExchlist = currExchlistSet.getMbo(i)) != null; i++) {
						Header.put("CRDATE", crdate);
						Header.put("CRNAME", crname);
						Header.put("ZRTOALL", zrtoall);
						Header.put("PACKGID", packgid + i);
						Item.put("ITEMID", itemid);
						Item.put("KURST", kurst);
						Item.put("GDATU", gdatu);
						Item.put("FCURR", currExchlist.getString("fcurr"));
						Item.put("TCURR", currExchlist.getString("tcurr"));
						if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
							System.out.println("\n--------------------" + Header);
							System.out.println("\n--------------------" + Item);
							// {"CRDATE":"20221221","CRNAME":"","PACKGID":"20221221114227","ZRTOALL":"1"}
							// {"KURST":"Z","FCURR":"USD","TCURR":"ADE","ITEMID":"1","GDATU":"20221131"}
							try {
								String resultStr = ItemWebServiceOfCurrency
										.itemRequestWebServiceOfCurrency(Header.toString(), Item.toString());
								addCurrexch(resultStr, monthLastDay);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {

						}
					}
				}
				currExchlistSet.close();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("----结束 实时汇率---");
	}

	private void addCurrexch(String resultStr, Date monthLastDay) throws JSONException, RemoteException, MXException {
		JSONObject result = new JSONObject(resultStr);
		String fCURR = CommonUtil.getString(result, "fCURR");
		String tCURR = CommonUtil.getString(result, "tCURR");
		String uKURS = CommonUtil.getString(result, "uKURS");
		if (fCURR != null && !fCURR.equalsIgnoreCase("") && tCURR != null && !tCURR.equalsIgnoreCase("")
				&& uKURS != null && !uKURS.equalsIgnoreCase("") && !uKURS.equalsIgnoreCase("0")) {
			MboSetRemote currExchSet = MXServer.getMXServer().getMboSet("UDCURREXCH",
					MXServer.getMXServer().getSystemUserInfo());
			MboRemote currExch = currExchSet.add();
			currExch.setValue("fcurr", fCURR, 11L);
			currExch.setValue("tcurr", tCURR, 11L);
			currExch.setValue("ukurs", uKURS, 11L);
			currExch.setValue("activedate", CommonUtil.getCalDate(monthLastDay, 1), 11L);
			currExch.setValue("expiredate", getMonth(0), 11L);
			currExchSet.save();
			currExchSet.close();
		}
	}

	private Date getMonth(int n) throws RemoteException {
		Date date = MXServer.getMXServer().getDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, n);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

}