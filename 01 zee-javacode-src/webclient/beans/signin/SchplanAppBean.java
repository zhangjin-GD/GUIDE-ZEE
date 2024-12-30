package guide.webclient.beans.signin;


import guide.app.common.CommonUtil;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.AppBean;

public class SchplanAppBean extends AppBean{

	public void delAutoSchedule() throws RemoteException, MXException, JSONException, ParseException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			Object[] params = { "温馨提示：请先保存后，再点击自动排班按钮！" };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}
		MboSetRemote schshiftSet = mbo.getMboSet("AUTOSCHSHIFT");
		if (!schshiftSet.isEmpty() && schshiftSet.count() > 0) {
			schshiftSet.deleteAll();
		}
	}
	
	public void autoSchedule() throws RemoteException, MXException, JSONException, ParseException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			Object[] params = { "温馨提示：请先保存后，再点击自动排班按钮！" };
			throw new MXApplicationException("instantmessaging","tsdimexception", params);
		}
		MboSetRemote schshiftSet = mbo.getMboSet("UDSCHSHIFT");
		schshiftSet.setOrderBy("linenum");
		if (!schshiftSet.isEmpty() && schshiftSet.count() > 0) {
			MboRemote schshift = null;
			int schShiftCount = schshiftSet.count();//一周期班次
			int dayCount = 0;//一周期天数
			String schplandateStr = "null";
			Date schplandate = MXServer.getMXServer().getDate();
			HashMap schshiftMapSet = new HashMap();//一周期数据
			for (int i = 0; (schshift = schshiftSet.getMbo(i)) != null; i++) {
				JSONObject jsonValue = new JSONObject();
				jsonValue.put("udschshiftid", schshift.getInt("udschshiftid"));
				jsonValue.put("schplandate", schshift.getString("schplandate"));
				jsonValue.put("signshiftnum", schshift.getString("signshiftnum"));
				jsonValue.put("udschcrewid", schshift.getInt("udschcrewid"));
				jsonValue.put("description", schshift.getString("description"));
				schplandate = schshift.getDate("schplandate");
				if(!schplandateStr.equalsIgnoreCase(schshift.getString("schplandate"))){
					dayCount++;//一周期天数
					schplandateStr = schshift.getString("schplandate");
				}
				schshiftMapSet.put(schshift.getInt("linenum"), jsonValue.toString());
			}
			int monthDays = CommonUtil.getMonthMaxDay(schplandate);//排班当月共多少天
			int addDays = (monthDays-dayCount);//需自动排班天数
			int listCount = addDays/dayCount;//整数排班次数
			double listCountD = 1.00;
			listCountD = listCountD*addDays/dayCount;//排班次数
			int listCount1 = (int) ((listCountD-listCount)*schShiftCount);//非整数排班
			MboRemote addSchshift = null;
			//整数排班
			int n = 0;
			int j = 0;
			for (j = 0; listCount > j; j++) {
				Iterator<Map.Entry<Integer, String>> iterator = schshiftMapSet.entrySet().iterator();
				while (iterator.hasNext()) {
					n++;
					Map.Entry<Integer, String> entry = iterator.next(); 
					JSONObject value = new JSONObject(entry.getValue());
//					System.out.println(entry.getKey() + "=" + entry.getValue());
					addSchshift = schshiftSet.add();
					addSchshift.setValue("schplandate", CommonUtil.getCalDate(CommonUtil.getDateFormat(value.getString("schplandate"), "yyyy-MM-dd"), (j+1)*dayCount), 2L);
					addSchshift.setValue("signshiftnum", value.getString("signshiftnum"), 2L);
					addSchshift.setValue("udschcrewid", value.getString("udschcrewid"), 2L);
					addSchshift.setValue("description", value.getString("description"), 2L);
					addSchshift.setValue("parent", value.getString("udschshiftid"), 11L);
					addSchshift.setValue("linenum", n+schShiftCount, 11L);
				}
			}
			//非整数排班
			int n1 = 0;
			Iterator<Map.Entry<Integer, String>> iterator1 = schshiftMapSet.entrySet().iterator();
			while (iterator1.hasNext() && n1 < listCount1) {
				n++;
				n1++;
				Map.Entry<Integer, String> entry = iterator1.next(); 
				JSONObject value1 = new JSONObject(entry.getValue());
//				System.out.println(entry.getKey() + "=" + entry.getValue());
				addSchshift = schshiftSet.add();
				addSchshift.setValue("schplandate", CommonUtil.getCalDate(CommonUtil.getDateFormat(value1.getString("schplandate"), "yyyy-MM-dd"), (j+1)*dayCount), 2L);
				addSchshift.setValue("signshiftnum", value1.getString("signshiftnum"), 2L);
				addSchshift.setValue("udschcrewid", value1.getString("udschcrewid"), 2L);
				addSchshift.setValue("description", value1.getString("description"), 2L);
				addSchshift.setValue("parent", value1.getString("udschshiftid"), 11L);
				addSchshift.setValue("linenum", n+schShiftCount, 11L);
			}
		}
		this.app.getAppBean().save();
	}
	
}
