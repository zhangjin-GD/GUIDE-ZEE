package guide.app.po;

import java.rmi.RemoteException;

import org.springframework.beans.factory.config.SetFactoryBean;

import psdi.app.po.POLine;
import psdi.app.po.POLineRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDPOLine extends POLine implements POLineRemote {

	public UDPOLine(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();

		try {
			String[] attrs1 = { "udtotalprice", "udtotalcost", "unitcost", "linecost" };
			String[] attrs2 = { "udpredicttaxprice", "udpredictprice" };
			String[] attrs3 = { "udtotalprice", "udtotalcost", "unitcost", "linecost", "udpredicttaxprice",
					"udpredictprice" };

			boolean ischangecost = false, idsap = false, iscon = false, isLocaiton = false;

			MboSetRemote conLineSet = this.getMboSet("UDCONTRACTLINE");
			if (!conLineSet.isEmpty() && conLineSet.count() > 0) {
				MboRemote conLine = conLineSet.getMbo(0);
				ischangecost = conLine.getBoolean("ischangecost");
			}

			MboSetRemote locationsSet = this.getMboSet("LOCATIONS");
			if (!locationsSet.isEmpty() && locationsSet.count() > 0) {
				MboRemote locations = locationsSet.getMbo(0);
				idsap = locations.getBoolean("udissap");
				iscon = locations.getBoolean("udisconsignment");
				isLocaiton = true;
			}

			if (ischangecost) {
				this.setFieldFlag(attrs3, 7L, true);
			} else {
				if (isLocaiton) {
					if (idsap && !iscon) {
						this.setFieldFlag(attrs1, 7L, false);
						this.setFieldFlag(attrs2, 7L, true);
					} else {
						this.setFieldFlag(attrs1, 7L, true);
						this.setFieldFlag(attrs2, 7L, false);
					}
				} else {
					this.setFieldFlag(attrs3, 7L, false);
				}
			}
			
			/**
			 * 	ZEE-服务订单带入合同金额只读
			 * 2024-02-06 13:39:13
			 */
			MboRemote owner = getOwner();
			if (owner != null && owner instanceof UDPO) {
				String udcompany = owner.getString("udcompany");
				if (udcompany != null && udcompany.equalsIgnoreCase("ZEE")) {
					String[] attrsreadonly = { "udtotalprice", "udtotalcost","unitcost", "linecost" };
					if (getString("udcontractlineid") != null&& !getString("udcontractlineid").equalsIgnoreCase("")) {
						setFieldFlag(attrsreadonly, 7L, true);
					} else {
						setFieldFlag(attrsreadonly, 7L, false);
					}
					
					/**
					 * ZEE - PO预算list，可见当前单据的所用预算总额（不含税）
					 * 2024-9-29  16:17
					 * 109 - 128
					 * 
					 */
					MboSetRemote udbudgetzeeSet = getMboSet("UDBUDGETZEE");
					if (!udbudgetzeeSet.isEmpty() && udbudgetzeeSet.count() > 0) {
						MboRemote udbudgetzee = udbudgetzeeSet.getMbo(0);
						MboSetRemote pringcostSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
						String ponum = getString("ponum");
						String udbudgetnum = getString("udbudgetnum");
						pringcostSet.setWhere("ponum = '" + ponum+ "' and udbudgetnum='" + udbudgetnum + "' ");
						pringcostSet.reset();
						if (!pringcostSet.isEmpty() && pringcostSet.count() > 0) {
							udbudgetzee.setValue("udthispobudget",pringcostSet.sum("linecost"), 11L);
						}
						pringcostSet.close();
					}
				}
			}
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MXException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote owner = this.getOwner();
		if (owner != null) {
			String udapptype = owner.getString("udapptype");
			if ("POMAT".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "ITEM", 2L);
			}
			if ("POFIX".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "ITEM", 2L);
				this.setValue("gldebitacct", "COSCO", 2L);
				this.setValue("issue", 1, 2L);
			}
			if ("POOT".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "ITEM", 2L);
			}
			if ("POSER".equalsIgnoreCase(udapptype)) {
				this.setValue("linetype", "SERVICE", 2L);
				this.setValue("gldebitacct", "COSCO", 2L);
				this.setValue("issue", 1, 2L);
			}
            if ("POZEE".equalsIgnoreCase(udapptype)) {
                this.setValue("conversion", 1.0, 2L);
                this.setValue("gldebitacct", "COSCO", 11L);
            }
		}
	}

	@Override
	public void save() throws MXException, RemoteException {
		super.save();
		if (this.toBeAdded()) {
			MboSetRemote locationsSet = this.getMboSet("LOCATIONS");
			if (!locationsSet.isEmpty() && locationsSet.count() > 0) {
				MboRemote locations = locationsSet.getMbo(0);
				boolean idsap = locations.getBoolean("udissap");
				boolean iscon = locations.getBoolean("udisconsignment");
				if (!idsap || iscon) {
					this.setValue("udtotalcost", 0, 2L);
				}
				this.setValue("udjs", iscon, 11L);
			}
		}
		// 即售即发
		MboRemote owner = getOwner();
		if (owner!=null && owner instanceof UDPO) {
			String udcompany = owner.getString("UDCOMPANY");
			if (udcompany.equals("GR02PCT")) {
				String uddept = owner.getString("UDDEPT");
				if (!uddept.equals("GR02120002") && !uddept.equals("GR02120010")) {
					setValue("ISSUE", 1, 2L);
				}
			}
			/**
			 * ZEE-保存时给标准GL字段赋值
			 * DJY 2024-03-19 15:54:58
			 */
			if (udcompany.equalsIgnoreCase("ZEE")) {
				setValue("gldebitacct", "COSCO", 11L);
				// 即收即发必填工单/位置/设备，否则无法保存
				if (getString("issue").equalsIgnoreCase("Y") && getString("wonum").equalsIgnoreCase("") && getString("location").equalsIgnoreCase("") && getString("assetnum").equalsIgnoreCase("")) {
					Object params[] = { "One of work order / location / asset must be selected, or you can not save!" };
					throw new MXApplicationException("instantmessaging","tsdimexception", params);
				}
			}
		}
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
		setValue("changeby", getUserInfo().getPersonId(), 11L);
		setValue("changedate", MXServer.getMXServer().getDate(), 11L);
	}
}
