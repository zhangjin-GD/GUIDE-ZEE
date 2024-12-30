package guide.app.fixed;

import guide.app.common.CommonUtil;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

import java.rmi.RemoteException;
import java.util.Date;

public class FixEd extends UDMbo implements MboRemote {

	public FixEd(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		String status = "8";
		String personId = this.getUserInfo().getPersonId();
		Date sysdate = MXServer.getMXServer().getDate();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof FixAcc) {
			String fixaccnum = parent.getString("fixaccnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("fixaccnum", fixaccnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
		this.setValue("status", status, 11L);// 状态
		this.setValue("useby", personId, 2L);
		this.setValue("installdate", sysdate, 11L);
		this.setValue("assetdate", sysdate, 11L);
		this.setValue("deprstartdate", sysdate, 11L);
		this.setValue("deprratey", 0, 11L);
		this.setValue("deprratem", 0, 11L);
		this.setValue("deprvaluey", 0, 11L);
		this.setValue("originalvalue", 0, 11L);
		this.setValue("deprvalue", 0, 11L);
		this.setValue("netvalue", 0, 11L);
		this.setValue("netsalvage", 0, 11L);
		this.setValue("netsalvagereate", 0, 11L);
		this.setValue("quantity", 0, 11L);
		this.setValue("uselife", "0", 11L);
		this.setValue("useperiods", "0", 11L);
		this.setValue("resperiods", "0", 11L);
		this.setValue("currencycode", CommonUtil.getValue(this, "UDCOMPANY", "currency"), 11L);
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
		String id = getUserInfo().getPersonId();
		Date sj = MXServer.getMXServer().getDate();
		if (!this.toBeAdded()) {
			// 详细
			if (isModified("FIXASSETNUM")) {
				ff("FIXASSETNUM", "固定资产卡片编号（SAP资产卡片号）", id, sj);
			}
			if (isModified("DESCRIPTION")) {
				ff("DESCRIPTION", "固定资产名称", id, sj);
			}
			if (isModified("USEBY")) {
				ff("USEBY", "使用人", id, sj);
			}
			if (isModified("UDCOMPANY")) {
				ff("UDCOMPANY", "公司", id, sj);
			}
			if (isModified("UDDEPT")) {
				ff("UDDEPT", "使用部门", id, sj);
			}
			if (isModified("UDOFS")) {
				ff("UDOFS", "工作组", id, sj);
			}
			if (isModified("DEPTMG")) {
				ff("DEPTMG", "归口管理部门", id, sj);
			}
			if (isModified("ADMINISTRATOR")) {
				ff("ADMINISTRATOR", "固定资产管理员", id, sj);
			}
			if (isModified("STATUS")) {
				ff("STATUS", "状态", id, sj);
			}
			if (isModified("ASSETLEVEL")) {
				ff("ASSETLEVEL", "资产层级", id, sj);
			}
			if (isModified("CLASSSTRUCTURE")) {
				ff("CLASSSTRUCTURE", "资产分类", id, sj);
			}
			if (isModified("ASSETTYPE")) {
				ff("ASSETTYPE", "资产类型", id, sj);
			}
			if (isModified("ASSETNUM")) {
				ff("ASSETNUM", "设备编码", id, sj);
			}
			if (isModified("FINASSETNUM")) {
				ff("FINASSETNUM", "原设备编码", id, sj);
			}
			if (isModified("LOCATION")) {
				ff("LOCATION", "存放地点", id, sj);
			}
			if (isModified("MODELNUM")) {
				ff("MODELNUM", "型号", id, sj);
			}
			if (isModified("SPECS")) {
				ff("SPECS", "规格", id, sj);
			}
			if (isModified("SERIALNUMBER")) {
				ff("SERIALNUMBER", "序列号", id, sj);
			}
			if (isModified("VENDOR")) {
				ff("VENDOR", "供应商", id, sj);
			}
			if (isModified("MANUFACTURER")) {
				ff("MANUFACTURER", "制造商", id, sj);
			}
			if (isModified("REMARK")) {
				ff("REMARK", "备注", id, sj);
			}
			if (isModified("ITEMNUM")) {
				ff("ITEMNUM", "物资编号", id, sj);
			}
			// 相关日期
			if (isModified("PURCHASEDATE")) {
				ff("PURCHASEDATE", "采购日期", id, sj);
			}
			if (isModified("INSTALLDATE")) {
				ff("INSTALLDATE", "安装日期", id, sj);
			}
			if (isModified("ASSETDATE")) {
				ff("ASSETDATE", "资本化日期", id, sj);
			}
			if (isModified("INACTIVEDATE")) {
				ff("INACTIVEDATE", "不活动日期", id, sj);
			}
			// 使用期限
			if (isModified("QUANTITY")) {
				ff("QUANTITY", "数量", id, sj);
			}
			if (isModified("USELIFE")) {
				ff("USELIFE", "使用年限", id, sj);
			}
			if (isModified("USEPERIODS")) {
				ff("USEPERIODS", "已使用期数", id, sj);
			}
			if (isModified("RESPERIODS")) {
				ff("RESPERIODS", "可使用期数", id, sj);
			}
			// 成本科目
			if (isModified("CURRENCYCODE")) {
				ff("CURRENCYCODE", "币种", id, sj);
			}
			if (isModified("COSTCENTER")) {
				ff("COSTCENTER", "成本中心", id, sj);
			}
			if (isModified("GLCOUNT")) {
				ff("GLCOUNT", "会计科目", id, sj);
			}
			if (isModified("DEPRGL")) {
				ff("DEPRGL", "折旧科目", id, sj);
			}
			// 折旧周期
			if (isModified("DEPRSTARTDATE")) {
				ff("DEPRSTARTDATE", "折旧计算开始日期", id, sj);
			}
			if (isModified("DEPRENDDATE")) {
				ff("DEPRENDDATE", "折旧计算结束日期", id, sj);
			}
			// 折旧率
			if (isModified("DEPRRATEY")) {
				ff("DEPRRATEY", "年折旧率", id, sj);
			}
			if (isModified("DEPRRATEM")) {
				ff("DEPRRATEM", "月折旧率", id, sj);
			}
			if (isModified("DEPRVALUEY")) {
				ff("DEPRVALUEY", "本年折旧", id, sj);
			}
			// 价值
			if (isModified("ORIGINALVALUE")) {
				ff("ORIGINALVALUE", "原值", id, sj);
			}
			if (isModified("DEPRVALUE")) {
				ff("DEPRVALUE", "累计折旧", id, sj);
			}
			if (isModified("NETVALUE")) {
				ff("NETVALUE", "净值", id, sj);
			}
			// 残值
			if (isModified("NETSALVAGE")) {
				ff("NETSALVAGE", "净残值", id, sj);
			}
			if (isModified("NETSALVAGEREATE")) {
				ff("NETSALVAGEREATE", "净残值率", id, sj);
			}
			// 新建增
			if (isModified("BINNUM")) {
				ff("BINNUM", "Binnum", id, sj);
			}
			if (isModified("FINANCECODE")) {
				ff("FINANCECODE", "财务资产编码", id, sj);
			}
			if (isModified("PONUM")) {
				ff("PONUM", "Ponum", id, sj);
			}
			if (isModified("RFID")) {
				ff("RFID", "Rfid", id, sj);
			}
		}
	}

	protected void ff(String zd, String mc, String id, Date sj) throws RemoteException, MXException {
		MboSetRemote mboSet = getMboSet("UDCHANGEHIS");
		MboRemote add = mboSet.add(11L);

		add.setValue("ownerid", getString("UDFIXEDID"), 11L);
		add.setValue("ownertable", "UDFIXED", 11L);

		add.setValue("attributename", zd, 11L);
		add.setValue("description", mc, 11L);

		add.setValue("oldvalue", getMboValue(zd).getPreviousValue().asString(), 11L);
		add.setValue("newvalue", getString(zd), 11L);

		add.setValue("changeby", id, 11L);
		add.setValue("changedate", sj, 11L);

	}
}
