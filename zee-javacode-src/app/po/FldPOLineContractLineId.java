package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPOLineContractLineId extends MAXTableDomain {

	public FldPOLineContractLineId(MboValue mbv) throws MXException {
		super(mbv);
		String thisAttr = getMboValue().getAttributeName();
		setRelationship("UDCONTRACTLINE", "udcontractlineid =:" + thisAttr);
		String[] FromStr = { "udcontractlineid" };
		String[] ToStr = { thisAttr };
		setLookupKeyMapInOrder(ToStr, FromStr);
	}

	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		if (parent != null && parent instanceof UDPO) {
			String apptype = parent.getString("udapptype");
			String udpurplat = parent.getString("udpurplat");
			if ("POFIX".equalsIgnoreCase(apptype) || "POMAT".equalsIgnoreCase(apptype)
					|| "POSER".equalsIgnoreCase(apptype)) {
				if ("CON".equalsIgnoreCase(udpurplat)) {
					mbo.setFieldFlag("udcontractlineid", 128L, true);
				} else {
					mbo.setFieldFlag("udcontractlineid", 128L, false);
				}
			}
		}
		
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {

		MboRemote mbo = this.getMboValue().getMbo();
		MboRemote parent = mbo.getOwner();
		if (parent != null && parent instanceof UDPO) {
			String vendor = parent.getString("vendor");
			String classstructureid = mbo.getString("item.classstructureid");
			String linetype = mbo.getString("linetype");
			String sql = "linetype ='" + linetype + "'";
			if ("ITEM".equalsIgnoreCase(linetype)) {
				sql += " and exists(select 1 from item where status='ACTIVE' and itemnum=udcontractline.itemnum and classstructureid='"+classstructureid+"')";
			}
			sql += " and exists(select 1 from udcontract where status ='APPR'"
					+ " and udcontract.gconnum=udcontractline.gconnum and vendor ='" + vendor + "'"
					+ " and to_char(sysdate,'yyyy-mm-dd')>= to_char(startdate,'yyyy-mm-dd')"
					+ " and to_char(sysdate,'yyyy-mm-dd')<= to_char(enddate,'yyyy-mm-dd'))";
			setListCriteria(sql);
		}
		return super.getList();
	}

	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String[] attrs1 = { "udtotalprice", "udtotalcost", "unitcost", "linecost" };
		String[] attrs2 = { "udpredicttaxprice", "udpredictprice" };
		String[] attrs3 = { "udtotalprice", "udtotalcost", "unitcost", "linecost", "udpredicttaxprice",
				"udpredictprice" };

		boolean ischangecost = false, idsap = false, iscon = false, isLocaiton = false;

		MboSetRemote locationsSet = mbo.getMboSet("LOCATIONS");
		if (!locationsSet.isEmpty() && locationsSet.count() > 0) {
			MboRemote locations = locationsSet.getMbo(0);
			idsap = locations.getBoolean("udissap");
			iscon = locations.getBoolean("udisconsignment");
			isLocaiton = true;
		}

		MboSetRemote conLineSet = mbo.getMboSet("UDCONTRACTLINE");
		if (!conLineSet.isEmpty() && conLineSet.count() > 0) {
			MboRemote conLine = conLineSet.getMbo(0);
			ischangecost = conLine.getBoolean("ischangecost");
			/**
			 * 	ZEE-服务订单带入合同金额
			 * 2024-02-02 15:39:13
			 */
			MboRemote owner = mbo.getOwner();
			if (owner!=null) {
				String udcompany = owner.getString("udcompany");
				if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
					mbo.setValue("unitcost", conLine.getDouble("uddiscountprice"), 2L);
					mbo.setValue("orderqty", conLine.getDouble("orderqty"), 2L);
					mbo.setValue("tax1code", conLine.getString("tax1code"), 2L);
					mbo.setValue("udcosttype",conLine.getString("udcosttype"), 2L);
				}
			}
		}

		MboSetRemote matConLineSet = mbo.getMboSet("UDMATCONLINE");
		if (!matConLineSet.isEmpty() && matConLineSet.count() > 0) {
			MboRemote matConLine = matConLineSet.getMbo(0);
			String tax1code = mbo.getString("tax1code");
			double totalunitcost = matConLine.getDouble("totalunitcost");// 含税单价
			mbo.setValue("tax1code", tax1code, 11L);
			if (isLocaiton) {
				if (!idsap || iscon) {
					mbo.setValue("udtotalcost", 0, 2L);
					mbo.setValue("udpredicttaxprice", totalunitcost, 2L);
				} else {
					mbo.setValue("udtotalprice", totalunitcost, 2L);
				}
			} else {
				mbo.setValue("udtotalprice", totalunitcost, 2L);
			}
		}

		if (ischangecost) {
			mbo.setFieldFlag(attrs3, 7L, true);
		} else {
			if (isLocaiton) {
				if (!idsap || iscon) {
					mbo.setFieldFlag(attrs1, 7L, true);
					mbo.setFieldFlag(attrs2, 7L, false);
				} else {
					mbo.setFieldFlag(attrs1, 7L, false);
					mbo.setFieldFlag(attrs2, 7L, true);
				}
			} else {
				mbo.setFieldFlag(attrs3, 7L, false);
			}
		}
		
		/**
		 * ZEE-服务订单带入合同金额只读 2024-02-06 10:39:13
		 */
		MboRemote owner = mbo.getOwner();
		if (owner == null) {
			return;
		}
		String udcompany = owner.getString("udcompany");
		if (udcompany != null && udcompany.equalsIgnoreCase("ZEE")) {
			String[] attrsreadonly = { "udtotalprice", "udtotalcost", "unitcost", "linecost" };
			if (mbo.getString("udcontractlineid")!=null && !mbo.getString("udcontractlineid").equalsIgnoreCase("")) {
				mbo.setFieldFlag(attrsreadonly, 7L, true);
			} else {
				mbo.setFieldFlag(attrsreadonly, 7L, false);
			}
		}
	}
}
