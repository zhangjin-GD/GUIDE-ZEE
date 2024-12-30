package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.receipts.ReceiptsTableBean;

public class UDInvPrintTableBean extends ReceiptsTableBean{
	/**
	 * ZEE - 库存打印物资二维码
	 * DJY
	 * 21-81
	 * 2024-07-22 9:39:13
	 */	
	public int UDZEEPRINT() throws RemoteException, MXException, JSONException {
		MboRemote mbo = this.getMbo();
		String printUrl = MXServer.getMXServer().getProperty("guide.udzeePrint.url");
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		Integer ponum = mbo.getInt("udponum");
		String receivedunit = mbo.getString("matrectrans.receivedunit");
		String issueunit = mbo.getString("inventory.issueunit");
		String binnum = mbo.getString("binnum");
		String itemnum = mbo.getString("itemnum");
		String description = mbo.getString("item.description");
		String udmatnum = mbo.getString("uditemcp.udmatnum");		
		Date transdate = mbo.getDate("physcntdate");//最后一次入库日期
		String transdateStr = CommonUtil.getDateFormat(transdate, "yyyy-MM-dd");
		long invbalancesid = mbo.getLong("invbalancesid");// ID
		//ZEE条形码key
		String udapikey = mbo.getString("uditemcp.udapikey");		
		
		jo.put("ponum", ponum);// po单号
		jo.put("receivedunit", receivedunit);// 订购单位
		jo.put("issueunit", issueunit);// 发放单位
		jo.put("binnum", binnum);// 默认货位
		jo.put("itemnum", itemnum);// 物资编码
		jo.put("description", replaceSpecStr(description));// 物资长描述（物资名称、规格、型号）
		jo.put("udmatnum", udmatnum);// 原编码
		jo.put("actualdate", transdateStr);// 入库时间
		
		jo.put("udprintnum", 1);// 打印数量
		jo.put("id", invbalancesid);// 二维码
		jo.put("udapikey", udapikey);// 二维码
		
		ja.put(jo);
		
		String jsp_url = printUrl + ja.toString();
		clientSession.getCurrentApp().openURL(jsp_url, true);
		return 1;
		}
	
	/**
	 * 正则替换所有特殊字符
	 * 
	 * @param orgStr
	 * @return
	 */
	public String replaceSpecStr(String input) {
		if (input != null && !"".equals(input.trim())) {
			String regex = "[~`·；;：:？?，,、|。.!！@#￥$%^&*_+=《》！……&（）——“”''｛{}｝【】]";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(input);
			return matcher.replaceAll("");
		}
		return null;
	}

}
