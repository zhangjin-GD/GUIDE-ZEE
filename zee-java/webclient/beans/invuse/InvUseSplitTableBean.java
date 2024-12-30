package guide.webclient.beans.invuse;

import java.rmi.RemoteException;

import guide.app.inventory.UDInvUse;
import guide.app.inventory.UDInvUseLine;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;

public class InvUseSplitTableBean extends DataBean {

	public int splititems() throws RemoteException, MXException {
		MboRemote mbo = this.app.getAppBean().getMbo();
		DataBean invUseSplitBean = app.getDataBean("invusesplit_table");
		DataBean invUseLineBean = app.getDataBean("main_invuselinetab_table");
		if (mbo != null && mbo instanceof UDInvUse) {
			String appType = mbo.getString("udapptype");
			if ("MATUSEWO".equalsIgnoreCase(appType) || "MATUSECS".equalsIgnoreCase(appType)) {// 工单领料
				MboSetRemote invBalancesSet = mbo.getMboSet("INVBALANCESOUTWO");
				addInvUseLine(mbo, invBalancesSet);
			} else if ("MATUSEOT".equalsIgnoreCase(appType)) {// 非工单领料
				MboSetRemote invBalancesSet = mbo.getMboSet("INVBALANCESOUTOT");
				addInvUseLine(mbo, invBalancesSet);
			}
		}
		invUseSplitBean.reloadTable();
		invUseLineBean.reloadTable();
		return 1;

	}

	private void addInvUseLine(MboRemote owner, MboSetRemote invBalancesSet) throws RemoteException, MXException {

		MboSetRemote mboSet = owner.getMboSet("UDINVUSESPLIT");
		if (!mboSet.isEmpty() && mboSet.count() > 0) {
			MboSetRemote lineSet = owner.getMboSet("INVUSELINE");
			for (int i = 0; mboSet.getMbo(i) != null; i++) {
				MboRemote mbo = mboSet.getMbo(i);
				int linenum = mbo.getInt("linenum");
				String itemnum = mbo.getString("itemnum");
				String assetnum = mbo.getString("assetnum");
				double quantitySum = mbo.getDouble("quantity");
				String ordertype = mbo.getString("ordertype");

				invBalancesSet.setWhere("itemnum='" + itemnum + "'");
				invBalancesSet.setOrderBy("itemnum,physcntdate");
				invBalancesSet.reset();
				double curbalSum = invBalancesSet.sum("curbal");

				if ((curbalSum - quantitySum) >= 0) {
					if (!invBalancesSet.isEmpty() && invBalancesSet.count() > 0) {
						double curbalNew = 0;
						for (int j = 0; invBalancesSet.getMbo(j) != null; j++) {
							MboRemote invbal = invBalancesSet.getMbo(j);
							itemnum = invbal.getString("itemnum");
							String binnum = invbal.getString("binnum");
							String lotnum = invbal.getString("lotnum");
							double curbal = invbal.getDouble("curbal");
							double surplusQty = quantitySum - curbalNew;// 剩余数量
							double quantity;
							if (curbal >= (surplusQty)) {
								quantity = surplusQty;
							} else {
								quantity = curbal;
							}
							UDInvUseLine line = (UDInvUseLine) lineSet.addAtEnd();
							line.setValue("itemnum", itemnum, 2L);
							line.setValue("assetnum", assetnum, 2L);
							line.setValue("frombin", binnum, 2L);
							line.setValue("fromlot", lotnum, 2L);
							line.setValue("quantity", quantity, 2L);
							line.setValue("udordertype", ordertype, 11L);
							line.setValue("remark", "", 11L);
							curbalNew += quantity;
						}
					}
				} else {
					Object[] obj = { linenum };
					throw new MXApplicationException("guide", "1094", obj);
				}
			}
		}

	}
}
