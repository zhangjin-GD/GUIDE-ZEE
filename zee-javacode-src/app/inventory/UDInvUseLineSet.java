package guide.app.inventory;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Vector;

import psdi.app.inventory.InvUseLineSet;
import psdi.app.inventory.InvUseLineSetRemote;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDInvUseLineSet extends InvUseLineSet implements InvUseLineSetRemote {

	public UDInvUseLineSet(MboServerInterface ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException {
		return new UDInvUseLine(ms);
	}

	@Override
	public void validateInvUseData() throws MXException, RemoteException {
		super.validateInvUseData();
		MboRemote owner = this.getOwner();
		if (owner != null && owner.isBasedOn("INVUSE")) {
			String appType = owner.getString("udapptype");
//			String udbudgetnum = owner.getString("udbudgetnum");
			String udwonum = owner.getString("udwonum");
			String udmovementtype = owner.getString("udmovementtype");
			if ("MATUSEWO".equalsIgnoreCase(appType) || "MATUSEOT".equalsIgnoreCase(appType)
					|| "MATUSECS".equalsIgnoreCase(appType)) {
				if (udmovementtype != null && udmovementtype.isEmpty()) {
					Object[] obj = { "温馨提示：移动类型必填！" };
					throw new MXApplicationException("udmessage", "error1", obj);
				}
			}
			if ("MATUSEWO".equalsIgnoreCase(appType)) {
				if (udwonum != null && udwonum.isEmpty()) {
					Object[] obj = { "温馨提示：工单编号必填！" };
					throw new MXApplicationException("udmessage", "error1", obj);
				}
			}
		}
	}

	@Override
	public MboRemote addInvUseLineFromMatUseTrans(MboRemote matUseTrans) throws MXException, RemoteException {

		MboRemote newInvUseLine = super.addInvUseLineFromMatUseTrans(matUseTrans);
		newInvUseLine.setValue("udbudgetnum", matUseTrans.getString("udbudgetnum"), 11L);
		newInvUseLine.setValue("udprojectnum", matUseTrans.getString("udprojectnum"), 11L);
		newInvUseLine.setValue("udordertype", matUseTrans.getString("udordertype"), 11L);
		newInvUseLine.setValue("udtax1code", matUseTrans.getString("udtax1code"), 2L);
		newInvUseLine.setValue("udtotalcost", matUseTrans.getDouble("udtotalcost"), 2L);

		return newInvUseLine;
	}

	@Override
	public void copyInvBalancesSet(MboSetRemote invBalancesSet) throws RemoteException, MXException {

		MboRemote owner = this.getOwner();
		if (owner != null && owner instanceof UDInvUse) {
			String apptype = owner.getString("udapptype");
			// 寄售领用
			// 只能选择一家供应商，并将供应商带入主表
			if ("MATUSECS".equalsIgnoreCase(apptype)) {
				HashSet<String> hashSet = new HashSet<String>();
				Vector<MboRemote> selectedMbos = invBalancesSet.getSelection();
				String udvendor = "";
				for (int i = 0; i < selectedMbos.size(); i++) {
					MboRemote mr = (MboRemote) selectedMbos.elementAt(i);
					udvendor = mr.getString("UDVENDOR");
					hashSet.add(udvendor);
				}
				if (hashSet.size() > 1) {
					throw new MXApplicationException("guide", "1073");
				}
				owner.setValue("udvendor", udvendor, 11L);
			}
		}

		super.copyInvBalancesSet(invBalancesSet);

		if (owner != null && owner instanceof UDInvUse) {
			String apptype = owner.getString("udapptype");
			MboSetRemote invUseLineSet = owner.getMboSet("INVUSELINE");
			if (!invUseLineSet.isEmpty() && invUseLineSet.count() > 0) {
				for (int i = 0; invUseLineSet.getMbo(i) != null; i++) {
					MboRemote invUseLine = invUseLineSet.getMbo(i);
					String itemnum = invUseLine.getString("itemnum");
					String fromstoreloc = invUseLine.getString("fromstoreloc");
					String frombin = invUseLine.getString("frombin");
					String fromlot = invUseLine.getString("fromlot");
					Vector selectedMbos = invBalancesSet.getSelection();
					for (int j = 0; j < selectedMbos.size(); j++) {
						MboRemote mr = (MboRemote) selectedMbos.elementAt(j);
						String itemnumBal = mr.getString("itemnum");
						String locationBal = mr.getString("location");
						String binnumBal = mr.getString("binnum");
						String lotnumBal = mr.getString("lotnum");
						int udmatrectransid = mr.getInt("udmatrectransid");
						double predicttaxprice = mr.getDouble("udpredicttaxprice");
						String udponum = mr.getString("udponum");
						if (itemnum.equalsIgnoreCase(itemnumBal) && fromstoreloc.equalsIgnoreCase(locationBal)
								&& frombin.equalsIgnoreCase(binnumBal) && fromlot.equalsIgnoreCase(lotnumBal)) {
							// 领用单 导入入库ID和PO号
							if (mr.isNull("udmatrectransid")) {
								invUseLine.setValue("udmatrectransid", udmatrectransid, 11L);
							}
							invUseLine.setValue("udponum", udponum, 11L);
							// 寄售领用 带入税率和含税单价
							if ("matusecs".equalsIgnoreCase(apptype)) {
								String tax1code = owner.getString("udvendor.tax1code");
								invUseLine.setValue("udtax1code", tax1code, 2L);
								if (predicttaxprice > 0) {
									invUseLine.setValue("udtotalprice", predicttaxprice, 2L);
								}
							}
						}
					}
				}
			}
		}
	}
}
