package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.CommonUtil;
import guide.app.inventory.bean.LocationBean;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class UDAutoReorderMailCronTask extends SimpleCronTask {

	// 重订购
	@Override
	public void cronAction() {
		try {
			System.out.println("----开始--低于安全库存---");
			List<LocationBean> list = getInventoryInfo();
			reorderMail(list);
			System.out.println("----结束--低于安全库存---");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private List<LocationBean> getInventoryInfo() throws RemoteException, MXException {
		String sqlWhere = getParamAsString("sqlWhere");
		List<LocationBean> arrayList = new ArrayList<LocationBean>();
		MboSetRemote locSet = MXServer.getMXServer().getMboSet("locations", getRunasUserInfo());
		locSet.setWhere(sqlWhere);
		locSet.setOrderBy("location");
		locSet.reset();
		if (!locSet.isEmpty() && locSet.count() > 0) {

			for (int i = 0; locSet.getMbo(i) != null; i++) {
				MboRemote loc = locSet.getMbo(i);
				String location = loc.getString("location");
				String invowner = loc.getString("invowner");
				String locDesc = loc.getString("description");
				String primaryeMail = loc.getString("invowner.primaryemail");
				boolean isemail = loc.getBoolean("invowner.udisreceivemail");
				if (isemail && primaryeMail != null && !primaryeMail.equalsIgnoreCase("")) {
					MboSetRemote invSet = loc.getMboSet("UDINVMINLEVEL");
					int invCount = invSet.count();
					if (invCount > 0) {
						LocationBean locBean = new LocationBean();
						locBean.setPersonId(invowner);
						locBean.setLocation(location);
						locBean.setLocationDesc(locDesc);
						locBean.setQuantity(invCount);
						arrayList.add(locBean);
					}
				}
			}

		}
		locSet.close();
		return arrayList;
	}

	private void reorderMail(List<LocationBean> list) throws RemoteException, MXException, JSONException {
		String packgid = CommonUtil.getCurrentDateFormat("yyyyMMddHHmmss");
		String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");

		Map<String, List<LocationBean>> groupObj = list.stream()
				.collect(Collectors.groupingBy(LocationBean::getPersonId));
		for (Entry<String, List<LocationBean>> entry : groupObj.entrySet()) {
			String key = entry.getKey();
			System.out.println("key -->" + key);
			MboSetRemote mboSet = MXServer.getMXServer().getMboSet("person", getRunasUserInfo());
			mboSet.setWhere("personid = '" + key + "'");
			mboSet.setOrderBy("personid");
			mboSet.reset();
			if (!mboSet.isEmpty() && mboSet.count() > 0) {
				MboRemote mbo = mboSet.getMbo(0);
				boolean isMail = mbo.getBoolean("udisreceivemail");
				if (isMail && !mbo.isNull("primaryemail")) {
					String language = mbo.getString("language");
					String toAddress = mbo.getString("primaryemail");

					String title = "";
					if (language.equalsIgnoreCase("EN")) {
						title = "Below safety stock information " + currentDate;
					} else {
						title = "低于安全库存信息 " + currentDate;
					}
					StringBuilder contentStr = new StringBuilder();
					if (language.equalsIgnoreCase("EN")) {
						contentStr.append("Hello！\n");
					} else {
						contentStr.append("您好！\n");
					}
					for (LocationBean inventory : entry.getValue()) {
						String location = inventory.getLocation();
						String locationDesc = inventory.getLocationDesc();
						Integer quantity = inventory.getQuantity();
						if (language.equalsIgnoreCase("EN")) {
							contentStr.append(
									"  Storage Room：" + location + ", Description:" + locationDesc + "The material information below the safety stock includes:" + quantity + "piece;\n");
						} else {
							contentStr.append(
									"  库房：" + location + "，描述：" + locationDesc + "，低于安全库存的物资信息共有：" + quantity + "条；\n");
						}
					}
					if (language.equalsIgnoreCase("EN")) {
						contentStr.append("For details, please log in to the EAM system and view the note on the left<below the lower limit> in the <Inventory> application.\n");
					} else {
						contentStr.append("详细内容，请登录EAM系统，<库存> 应用中的左侧便签 <低于下限> 查看。\n");
					}
					// 消息参数
					JSONObject jsonData = new JSONObject();
					jsonData.put("id", key + packgid);
					jsonData.put("to_user", toAddress);
					jsonData.put("subject", title);
					jsonData.put("content", contentStr);
					jsonData.put("create_time", currentDate);
					jsonData.put("create_by", key);
					jsonData.put("change_time", currentDate);
					jsonData.put("change_by", key);
					jsonData.put("file_path", "");
					// 消息执行
					try {
						String returnResult = CommonUtil
								.sendGDEam(MXServer.getMXServer().getProperty("guide.gdnotify.url"), jsonData);
						String returnCode = CommonUtil.getString(new JSONObject(returnResult), "code");
						if (returnCode != null && returnCode.equalsIgnoreCase("200")) {
							System.out.println("发送成功");

						} else {
							String error = CommonUtil.getString(new JSONObject(returnResult), "result");
							if (error.length() > 300) {
								System.out.println(error.substring(0, 300));
							} else {
								System.out.println(error);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			mboSet.close();
		}
	}
}
