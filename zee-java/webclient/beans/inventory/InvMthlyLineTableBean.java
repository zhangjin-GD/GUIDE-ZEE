package guide.webclient.beans.inventory;

import java.rmi.RemoteException;
import java.util.Date;

import guide.app.common.CommonUtil;
import guide.app.inventory.InvMthly;
import guide.iface.sap.SapHeader;
import guide.iface.sap.SapHeaderSet;
import guide.iface.sap.SapItem;
import guide.iface.sap.SapItemSet;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXApplicationYesNoCancelException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class InvMthlyLineTableBean extends DataBean {

	public void insertInvMonthly() throws RemoteException, MXException {

		DataBean appBean = this.app.getAppBean();
		InvMthly mbo = (InvMthly) this.app.getAppBean().getMbo();
		String personid = mbo.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String invmthlyNum = mbo.getString("invmthlynum");

		if (toBeSaved()) {
			Object[] obj = { "温馨提示：请先保存后，再点击库存月结按钮！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}

		String status = mbo.getString("status");
		if (status != null && status.equalsIgnoreCase("APPR")) {
			throw new MXApplicationException("guide", "1039");
		}

		MboSetRemote lineSet = mbo.getMboSet("UDINVMTHLYLINE");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			int userInput = MXApplicationYesNoCancelException.getUserInput("check", MXServer.getMXServer(),
					mbo.getUserInfo());
			switch (userInput) {
			case MXApplicationYesNoCancelException.NULL:
				Object[] obj = { "温馨提示：已存在月结数据，是否重新月结 \n 选择 Yes 继续，选择 No 取消！" };
				throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
			case MXApplicationYesNoCancelException.YES:
				// 删除
				mbo.invMonthlyLineDelete(
						"delete from udinvmthlyline where udinvmthlyline.invmthlynum = '" + invmthlyNum + "'");
				mbo.invMonthlyLineDelete(
						"delete from udinvmthlycost where udinvmthlycost.invmthlynum = '" + invmthlyNum + "'");
				mbo.invMonthlyLineDelete(
						"delete from udinvmthlygl where udinvmthlygl.invmthlynum = '" + invmthlyNum + "'");
				mbo.invMonthly(invmthlyNum);
				break;
			case MXApplicationYesNoCancelException.NO:
				break;
			case MXApplicationYesNoCancelException.CANCEL:
				break;
			}
		} else {
			// 新增
			mbo.invMonthly(invmthlyNum);
		}
		mbo.setValue("changeby", personid, 11L);
		mbo.setValue("changetime", currentDate, 11L);
		appBean.save();
	}

	public void insertInvMonthlyCost() throws RemoteException, MXException {

		DataBean appBean = this.app.getAppBean();
		InvMthly mbo = (InvMthly) this.app.getAppBean().getMbo();
		String personid = mbo.getUserInfo().getPersonId();
		Date currentDate = MXServer.getMXServer().getDate();
		String invmthlyNum = mbo.getString("invmthlynum");

		if (toBeSaved()) {
			Object[] obj = { "温馨提示：请先保存后，再点击库存月结按钮！" };
			throw new MXApplicationException("udmessage", "error1", obj);
		}

		String status = mbo.getString("status");
		if (status != null && status.equalsIgnoreCase("APPR")) {
			throw new MXApplicationException("guide", "1039");
		}

		MboSetRemote lineSet = mbo.getMboSet("UDINVMTHLYCOST");
		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			int userInput = MXApplicationYesNoCancelException.getUserInput("check", MXServer.getMXServer(),
					mbo.getUserInfo());
			switch (userInput) {
			case MXApplicationYesNoCancelException.NULL:
				Object[] obj = { "温馨提示：已存在月结数据，是否重新月结 \n 选择 Yes 继续，选择 No 取消！" };
				throw new MXApplicationYesNoCancelException("check", "udmessage", "error0", obj);
			case MXApplicationYesNoCancelException.YES:
				// 删除
				mbo.invMonthlyLineDelete(
						"delete from udinvmthlycost where udinvmthlycost.invmthlynum = '" + invmthlyNum + "'");
				mbo.invMonthlyLineDelete(
						"delete from udinvmthlygl where udinvmthlygl.invmthlynum = '" + invmthlyNum + "'");
				mbo.invMonthlyCostInsert(invmthlyNum);
				mbo.invMonthlyGLInsert(invmthlyNum);
				break;
			case MXApplicationYesNoCancelException.NO:
				break;
			case MXApplicationYesNoCancelException.CANCEL:
				break;
			}
		} else {
			// 新增
			mbo.invMonthlyCostInsert(invmthlyNum);
			mbo.invMonthlyGLInsert(invmthlyNum);
		}
		mbo.setValue("changeby", personid, 11L);
		mbo.setValue("changetime", currentDate, 11L);
		appBean.save();
	}

	// 创建差异数据
	public void addSapHeader() throws RemoteException, MXException {
		InvMthly mbo = (InvMthly) this.app.getAppBean().getMbo();
		String company = mbo.getString("udcompany");
		Date createtime = mbo.getDate("createtime");
		String createDateStr = CommonUtil.getDateFormat(createtime, "yyyy-MM");
		String invmthlynum = mbo.getString("invmthlynum");
		MboSetRemote lineSet = mbo.getMboSet("UDINVMTHLYGL");
		SapHeaderSet sapheaderSet = (SapHeaderSet) mbo.getMboSet("UDSAPHEADER");
		if (!sapheaderSet.isEmpty() && sapheaderSet.count() > 0) {
			sapheaderSet.deleteAll(11L);
			this.app.getAppBean().save();
		}
		if (sapheaderSet.isEmpty()) {
			if (!lineSet.isEmpty() && lineSet.count() > 0) {
				for (int i = 0; lineSet.getMbo(i) != null; i++) {
					MboRemote line = lineSet.getMbo(i);
					double sapdiscost = line.getDouble("sapdiscost");
					String glaccount = line.getString("glaccount");
					String materialtype = "";
					String materialtypeDesc = "";
					String movementtype = "";
					String costcenter = "";
					String zunit = "";
					String aufnr = "";
					double dmbtr3 = 0;
					boolean flag = false;
					if (sapdiscost > 0) {
						MboSetRemote sapGlMappingSet = mbo.getMboSet("$UDSAPGLMAPPING" + i, "UDSAPGLMAPPING",
								"udcompany='" + company + "' and glaccount='" + glaccount + "' and issuetype='RETURN'");
						if (!sapGlMappingSet.isEmpty() && sapGlMappingSet.count() > 0) {
							MboRemote sapGlMapping = sapGlMappingSet.getMbo(0);
							materialtype = sapGlMapping.getString("materialtype");// 物资类型
							materialtypeDesc = sapGlMapping.getString("materialtype.description");// 物资类型描述
							movementtype = sapGlMapping.getString("movementtype");// 移动类型
							costcenter = sapGlMapping.getString("costcenter");// 成本中心
							zunit = sapGlMapping.getString("zunit");// 单位
							aufnr = sapGlMapping.getString("aufnr");// 内部订单
							dmbtr3 = Math.abs(sapdiscost);
							flag = true;
						}
					}
					if (sapdiscost < 0) {
						MboSetRemote sapGlMappingSet = mbo.getMboSet("$UDSAPGLMAPPING" + i, "UDSAPGLMAPPING",
								"udcompany='" + company + "' and glaccount='" + glaccount + "' and issuetype='ISSUE'");
						if (!sapGlMappingSet.isEmpty() && sapGlMappingSet.count() > 0) {
							MboRemote sapGlMapping = sapGlMappingSet.getMbo(0);
							materialtype = sapGlMapping.getString("materialtype");// 物资类型
							materialtypeDesc = sapGlMapping.getString("materialtype.description");// 物资类型描述
							movementtype = sapGlMapping.getString("movementtype");// 移动类型
							costcenter = sapGlMapping.getString("costcenter");// 成本中心
							zunit = sapGlMapping.getString("zunit");// 单位
							aufnr = sapGlMapping.getString("aufnr");// 内部订单
							dmbtr3 = Math.abs(sapdiscost);
							flag = true;
						}
					}
					if (flag) {
						SapHeader sapheader = (SapHeader) sapheaderSet.add();
						sapheader.setValue("udcompany", company, 2L);// 公司
						sapheader.setValue("description", createDateStr + materialtypeDesc + "差异", 11L);// 描述
                        if(company.equalsIgnoreCase("ZEE")){
                        	sapheader.setValue("description", createDateStr + materialtypeDesc + " Gap", 11L);// 描述
                        }
						sapheader.setValue("budat", createtime, 11L);// 凭证日期
						sapheader.setValue("zdate1", createtime, 11L);// 传输日期
						sapheader.setValue("ztran", movementtype, 11L);// 移动类型
						sapheader.setValue("invmthlynum", invmthlynum, 11L);
						SapItemSet sapItemSet = (SapItemSet) sapheader.getMboSet("UDSAPITEM");
						SapItem sapItem = (SapItem) sapItemSet.add();
						sapItem.setValue("maktx", materialtypeDesc, 11L);// 描述
						sapItem.setValue("mtart", materialtype, 11L);// 物料类型
						sapItem.setValue("dmbtr3", dmbtr3, 11L);// 成本
						sapItem.setValue("zunit", zunit, 11L);// 单位
						sapItem.setValue("zquantity", 0, 11L);// 数量
						sapItem.setValue("kostl", costcenter, 11L);// 成本中心
						sapItem.setValue("aufnr", aufnr, 11L);// 内部订单号
					}
				}
			}
		} 
		this.app.getAppBean().save();
	}
}
