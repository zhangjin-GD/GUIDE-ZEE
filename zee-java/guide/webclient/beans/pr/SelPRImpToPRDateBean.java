package guide.webclient.beans.pr;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import guide.app.common.CommonUtil;
import guide.app.pr.PRImp;
import guide.app.pr.UDPR;
import guide.app.pr.UDPRLine;
import guide.app.pr.UDPRSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class SelPRImpToPRDateBean extends DataBean {

	@Override
	public synchronized int execute() throws MXException, RemoteException {

		PRImp owner = (PRImp) this.app.getAppBean().getMbo();
		Vector<MboRemote> vector = this.getSelection();
		if (owner != null) {
			String personid = owner.getUserInfo().getPersonId();
			Date currentDate = MXServer.getMXServer().getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String sysdatestr = sdf.format(currentDate);
			UDPRSet prSet = (UDPRSet) owner.getMboSet("$PR", "PR", "1=2");
			UDPR pr = (UDPR) prSet.add();
			pr.setValue("udapptype", "PRMAT", 11L);
			pr.setValue("udmatstatus", "MONTHLY", 11L);
			pr.setValue("description", "月度申请汇总" + sysdatestr, 11L);
			pr.setValue("exchangerate", 1, 11L);
			pr.setValue("exchangedate", currentDate, 11L);
			pr.setValue("udcreateby", personid, 2L);// 创建人
			pr.setValue("requestedby", personid, 2L);// 请求者
			pr.setValue("udcreatetime", currentDate, 11L);// 创建时间
			pr.setValue("requireddate", CommonUtil.getCalDate(currentDate, 14), 2L);// 要求日期，默认14天后
			String udcompany = pr.getString("udcompany");
			MboSetRemote prlineSet = pr.getMboSet("PRLINE");
			for (int i = 0; i < vector.size(); i++) {
				MboRemote mr = (MboRemote) vector.elementAt(i);
				MboSetRemote prImpLineSet = mr.getMboSet("UDPRIMPLINEAPPR");
				if (!prImpLineSet.isEmpty() && prImpLineSet.count() > 0) {
					for (int j = 0; prImpLineSet.getMbo(j) != null; j++) {
						UDPRLine prline = (UDPRLine) prlineSet.add();
						MboRemote prImpLine = prImpLineSet.getMbo(j);
						String itemnum = prImpLine.getString("itemnum");
						double orderqty = prImpLine.getDouble("orderqty");
						double unitcost = prImpLine.getDouble("unitcost");// 含税单价
						String esttime = prImpLine.getString("esttime");
						String remark = prImpLine.getString("remark");
						prline.setValue("itemnum", itemnum, 2L);
						String tax1code = CommonUtil.getValue("UDDEPT", "type='COMPANY' and deptnum='"+udcompany+"'", "TAX1CODE");
						prline.setValue("tax1code", tax1code, 2L);
						prline.setValue("orderqty", orderqty, 2L);
						prline.setValue("udtotalprice", unitcost, 2L);
						prline.setValue("udesttime", esttime, 11L);
						prline.setValue("remark", remark, 11L);
						prImpLine.setValue("prlineid", prline.getInt("prlineid"), 11L);
					}
				}
			}
			String prnum = pr.getString("prnum");
			String params = "创建完成，单号：" + prnum + "！/Creation completed, No.:" + prnum;
			clientSession.showMessageBox(clientSession.getCurrentEvent(), "温馨提示/Reminder", params, 1);
		}
		return super.execute();
	}
}
