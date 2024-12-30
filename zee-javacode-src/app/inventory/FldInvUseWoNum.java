package guide.app.inventory;

import psdi.app.workorder.FldWonum;
import psdi.mbo.*;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class FldInvUseWoNum extends FldWonum {

	public FldInvUseWoNum(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		String[] target = new String[] { thisAttr, "siteid" };
		String[] source = new String[] { "wonum", "siteid" };
		this.setLookupKeyMapInOrder(target, source);
		this.setRelationship("WORKORDER", "wonum = :" + thisAttr + " and siteid=:siteid");
		UDInvUse invuseline = (UDInvUse) this.getMboValue().getMbo();
		Translate tr = invuseline.getTranslator();
		this.clearConditionalListWhere();

		String wapprCancelSyns = tr.toExternalList("WOSTATUS", new String[] { "CAN", "WAPPR", "CLOSE" });
		String criteriaNotCancelled = "status not in (" + wapprCancelSyns + ") and siteid =:siteid";
		this.clearConditionalListWhere();
		this.setListCriteria(criteriaNotCancelled);
		this.setErrorMessage("workorder", "NotValidNotCancelledWo");

	}

	@Override
	public void validate() throws MXException, RemoteException {
		super.validate();
		Mbo mbo = this.getMboValue().getMbo();
		if (!this.getMboValue().isNull()) {
			String udcostcenter = mbo.getString("udwo.asset.udcostcenter");// 成本中心
			String uddept = mbo.getString("udwo.uddept");
			String personDept = mbo.getString("udcreateby.uddept");
			// asset表的成本中心为空
			if (udcostcenter == null || udcostcenter.isEmpty()) {
				throw new MXApplicationException("guide", "1018");
			}

			// 工单部门与登陆人部门不同
			if (!uddept.equalsIgnoreCase(personDept)) {
				throw new MXApplicationException("guide", "1054");
			}
		}

	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		MboSetRemote listSet = super.getList();

		String sql = "1=2";
		MboRemote mbo = this.getMboValue().getMbo();
		String movementType = mbo.getString("udmovementtype");
		String dept = mbo.getString("uddept");
		if (movementType != null && movementType.equalsIgnoreCase("204")) {
			sql = "exists (select 1 from asset where asset.assetnum=workorder.assetnum and udcostcenter is not null and udassettypecode='FAC' and udassettypecode1='BLDG') ";
		} else if (movementType != null && movementType.equalsIgnoreCase("205")) {
			sql = "exists (select 1 from asset where asset.assetnum=workorder.assetnum and udcostcenter is not null and udassettypecode!='FAC') ";
		} else if (movementType != null && movementType.equalsIgnoreCase("206")) {
			sql = "exists (select 1 from asset where asset.assetnum=workorder.assetnum and udcostcenter is not null and udassettypecode='FAC' and nvl(udassettypecode1,'NA') not in ('BLDG','CNTN'))";
		} else if (movementType != null && movementType.equalsIgnoreCase("221")) {
			sql = "exists (select 1 from asset where asset.assetnum=workorder.assetnum and udcostcenter is not null and udassettypecode='FAC' and udassettypecode1='CNTN')";
		}
		UDInvUse invuseline = (UDInvUse) this.getMboValue().getMbo();
		Translate tr = invuseline.getTranslator();
		String wapprCancelSyns = tr.toExternalList("WOSTATUS", new String[] { "CAN", "WAPPR", "CLOSE" });
		sql += " and status not in (" + wapprCancelSyns
				+ ") and assetnum in (select assetnum from asset where udcostcenter is not null) ";
		sql += " and (uddept='" + dept + "' or worktype='FM')";
		listSet.setWhere(sql);
		// 排序
		listSet.setOrderBy("decode(udofs,'" + mbo.getString("UDOFS") + "',1),REPORTDATE desc");

		return listSet;
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		MboSetRemote invuselineSet = mbo.getMboSet("INVUSELINE");
		MboSetRemote woSet = mbo.getMboSet("UDWO");
		if (!woSet.isEmpty() && woSet.count() > 0) {
			MboRemote wo = woSet.getMbo(0);
			mbo.setValue("description", wo.getString("ASSET.description") + wo.getString("WORKTYPE.wtypedesc") + "领料",
					11l);
			mbo.setValue("udworktype", wo.getString("worktype"), 11l);

			MboSetRemote gjobMatSet = wo.getMboSet("UDGJOBMATERIAL");
			if (!gjobMatSet.isEmpty() && gjobMatSet.count() > 0) {
				for (int i = 0; gjobMatSet.getMbo(i) != null; i++) {
					MboRemote gjobMat = gjobMatSet.getMbo(i);
					String itemnum = gjobMat.getString("itemnum");
					double orderqty = gjobMat.getDouble("orderqty");
					MboSetRemote invbalSet = mbo.getMboSet("INVBALANCESOUTWO");
					invbalSet.setWhere("itemnum='" + itemnum + "'");
					invbalSet.setOrderBy("itemnum,physcntdate");
					invbalSet.reset();
					double curbalSum = invbalSet.sum("curbal");
					if ((curbalSum - orderqty) >= 0) {
						if (!invbalSet.isEmpty() && invbalSet.count() > 0) {
							double curbalNew = 0;
							for (int j = 0; invbalSet.getMbo(j) != null; j++) {
								MboRemote invbal = invbalSet.getMbo(j);
								String binnum = invbal.getString("binnum");
								String lotnum = invbal.getString("lotnum");
								double curbal = invbal.getDouble("curbal");
								double surplusQty = orderqty - curbalNew;// 剩余数量
								double quantity;
								if (curbal >= surplusQty) {
									quantity = surplusQty;
								} else {
									quantity = curbal;
								}
								if (quantity > 0) {
									MboRemote line = invuselineSet.addAtEnd();
									line.setValue("itemnum", itemnum, 2L);
									line.setValue("quantity", quantity, 2L);
									line.setValue("frombin", binnum, 2L);
									line.setValue("fromlot", lotnum, 2L);
								}
								curbalNew += quantity;
							}
						}
					}
				}
			}
		}

		if (!this.getMboValue().isNull()) {
			String wonum = this.getMboValue().getString();
			if (invuselineSet != null && !invuselineSet.isEmpty()) {
				for (int i = 0; invuselineSet.getMbo(i) != null; i++) {
					MboRemote invuseline = invuselineSet.getMbo(i);
					invuseline.setValue("wonum", wonum, 11L);
					invuseline.setValue("refwo", wonum, 11L);
				}
			}
		} else {
			if (invuselineSet != null && !invuselineSet.isEmpty()) {
				for (int i = 0; invuselineSet.getMbo(i) != null; i++) {
					MboRemote invuseline = invuselineSet.getMbo(i);
					invuseline.setValueNull("wonum", 11L);
					invuseline.setValueNull("refwo", 11L);
				}
			}
		}
	}

	public String[] getAppLink() throws MXException, RemoteException {
		MboRemote mbo = this.getMboValue().getMbo();
		String appType = mbo.getString("udworktype");
		if (appType == null || appType.equalsIgnoreCase("")) {
			return new String[] { "" };
		} else if (appType.equalsIgnoreCase("PM") || appType.equalsIgnoreCase("IM")) {
			return new String[] { "UDWOPM" };
		} else if (appType.equalsIgnoreCase("CM")) {
			return new String[] { "UDWOCM" };
		} else if (appType.equalsIgnoreCase("EM")) {
			return new String[] { "UDWOEM" };
		} else if (appType.equalsIgnoreCase("FM")) {
			return new String[] { "UDWOFM" };
		}
		return super.getAppLink();
	}

}
