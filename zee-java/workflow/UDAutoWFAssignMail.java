package guide.workflow;

import java.rmi.RemoteException;

import org.json.JSONException;
import org.json.JSONObject;

import guide.app.common.CommonUtil;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;
import psdi.util.MXException;

public class UDAutoWFAssignMail extends SimpleCronTask {

	@Override
	public void cronAction() {
		try {
			System.out.println("---开始 代办任务---");
			autoWFAssignMail();
			System.out.println("---结束 代办任务---");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void autoWFAssignMail() throws RemoteException, MXException, JSONException {
		int maxQty = 20;
		String sqlWhere = getParamAsString("sqlWhere");
		String currentDate = CommonUtil.getCurrentDateFormat("yyyy-MM-dd");
		String packgid = CommonUtil.getCurrentDateFormat("yyyyMMddHHmmss");
		MboSetRemote perSet = MXServer.getMXServer().getMboSet("person", getRunasUserInfo());
		perSet.setWhere(sqlWhere);
		perSet.setOrderBy("personid");
		perSet.reset();
		if (!perSet.isEmpty() && perSet.count() > 0) {
			for (int i = 0; perSet.getMbo(i) != null; i++) {

				MboRemote per = perSet.getMbo(i);
				String personid = per.getString("personid");
				String personname = per.getString("displayname");
				String language = per.getString("language");
				String primaryemail = per.getString("primaryemail");
				MboSetRemote wfAssigSet = per.getMboSet("UDWFASSIGACTIVE");
				wfAssigSet.setOrderBy("app,startdate");
				wfAssigSet.reset();
				String title = "";
				if (language.equalsIgnoreCase("EN")) {
					title = "Unprocessed information for more than 3 days " + currentDate;
				} else {
					title = "代办超过3天未处理信息 " + currentDate;
				}
				if (primaryemail != null && !primaryemail.equalsIgnoreCase("")) {
//					StringBuilder contentHtml = new StringBuilder();
					StringBuilder contentStr = new StringBuilder();
					if (!wfAssigSet.isEmpty() && wfAssigSet.count() > 0) {
						int wfCount = wfAssigSet.count();
//						contentHtml.append("<!DOCTYPE html>\n");
//						contentHtml.append("<html lang=\"zh\">\n");
//						contentHtml.append("<head>\n");
//						contentHtml.append("    <meta charset=\"UTF-8\">\n");
//						contentHtml.append("</head>");
//						contentHtml.append("<body>");
//						contentHtml.append("<h4>您好！</h4>");
//						contentHtml.append("<h4>&nbsp;&nbsp;&nbsp;&nbsp;代办数量合计：" + wfCount + "条。下面是代办内容请查收！(代办内容只显示前20条)</h4>");
//						contentHtml.append("<table border=\"1\" >");
						int countQty;
						if (wfCount > maxQty) {
							countQty = maxQty;
						} else {
							countQty = wfCount;
						}
//						contentHtml.append("<tr>");
//						contentHtml.append("<th align=\"left\">序号</th>");
//						contentHtml.append("<th align=\"left\">代办内容</th>");
//						contentHtml.append("</tr>");
						if (language.equalsIgnoreCase("EN")) {
							contentStr.append("Hello!\n");
							contentStr.append("  Total number of agents:" + wfCount
									+ "piece. Please check the following contents! (Only the first 20 items are displayed)\n");
							contentStr.append("  Serial number    Agency content\n");
						} else {
							contentStr.append("您好！\n");
							contentStr.append("  代办数量总数：" + wfCount + "条。下面是代办内容请查收！(代办内容只显示前20条)\n");
							contentStr.append("  序号    代办内容\n");
						}

						for (int j = 0; j < countQty; j++) {
							MboRemote wfAssig = wfAssigSet.getMbo(j);
							String wfdesc = wfAssig.getString("description");
							int linenum = j + 1;
//							contentHtml.append("<tr>");
//							contentHtml.append("<td align=\"left\">" + linenum + "</td>");
//							contentHtml.append("<td align=\"left\">" + wfdesc + "</td>");
//							contentHtml.append("</tr>");

							contentStr.append("  " + linenum + "    " + wfdesc + "\n");
						}
//						contentHtml.append("</table>");
//						contentHtml.append("<h4>请各位领导及时确认信息，谢谢配合！</h4>");
//						contentHtml.append("</body></html>");
//						System.out.println("---->" + contentHtml.toString());

						if (language.equalsIgnoreCase("EN")) {
							contentStr.append("Please confirm the information in time. Thank you for your cooperation!\n");
						} else {
							contentStr.append("请各位领导及时确认信息，谢谢配合！\n");
						}
						// 消息参数
						JSONObject jsonData = new JSONObject();
						jsonData.put("id", personid + packgid + i);
						jsonData.put("to_user", primaryemail);
						jsonData.put("subject", title);
						jsonData.put("content", contentStr.toString());
						jsonData.put("create_time", currentDate);
						jsonData.put("create_by", personname);
						jsonData.put("change_time", currentDate);
						jsonData.put("change_by", personname);
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
					wfAssigSet.close();
				}
			}
		}
		perSet.close();
	}

}
