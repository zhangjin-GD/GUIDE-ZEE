package guide.app.po;

import java.rmi.RemoteException;

import guide.app.inventory.UDMatRecTrans;
import psdi.app.common.purchasing.FldPurStoreloc;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPurStoreloc extends FldPurStoreloc {

	public UDFldPurStoreloc(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		MboRemote mbo = getMboValue().getMbo();
		double udpredicttaxprice = mbo.getDouble("udpredicttaxprice");// 寄售含税单价
		super.action();
		boolean ischangecost = false, idsap = false, iscon = false, isLocaiton = false;
		String[] attrs1 = { "udtotalprice", "udtotalcost", "unitcost", "linecost" };
		String[] attrs2 = { "udpredicttaxprice", "udpredictprice" };
		String[] attrs3 = { "udtotalprice", "udtotalcost", "unitcost", "linecost", "udpredicttaxprice",
				"udpredictprice" };
		if (!this.getMboValue().isNull()) {
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
			}
//			if (!idsap || iscon) {
//				mbo.setValue("udtotalprice", 0, 2L);// 含税单价
//			} else {
//				mbo.setValue("udtotalprice", udpredicttaxprice, 2L);
//			}
			//ZEE-poline生产库、寄售库金额逻辑，ZEE-01库房：寄售金额为0，ZEE-02库房：生产金额为0；ZEE-03库房所有金额为0，47-77
			MboRemote owner = mbo.getOwner();
			String udcompany = owner.getString("udcompany");		
			if(!udcompany.equalsIgnoreCase("ZEE")){
				if (!idsap || iscon) {
					mbo.setValue("udtotalprice", 0, 2L);// 含税单价
				} else {
					mbo.setValue("udtotalprice", udpredicttaxprice, 2L);
				}
			}else if(udcompany.equalsIgnoreCase("ZEE")){
				String storeloc = mbo.getString("storeloc");
				if(storeloc.equalsIgnoreCase("ZEE-01")){	
					mbo.setValue("udtotalprice", udpredicttaxprice, 2L);//非寄售含税单价
					mbo.setValue("udpredictprice", "0", 2L);// 寄售不含税单价
					mbo.setValue("udpredicttaxprice", "0", 2L);// 寄售含税单价
					mbo.setValue("udpredicttaxlineprice", "0", 2L);// 寄售含税总价
				}else if(storeloc.equalsIgnoreCase("ZEE-02")){
					double unitcost = mbo.getDouble("unitcost");
					double udtotalprice = mbo.getDouble("udtotalprice");
					double udtotalcost = mbo.getDouble("udtotalcost");
					mbo.setValue("udtotalprice", "0", 2L);//非寄售含税单价
					mbo.setValue("udpredictprice", unitcost, 2L);// 寄售不含税单价
					mbo.setValue("udpredicttaxprice", udtotalprice, 2L);// 寄售含税单价
					mbo.setValue("udpredicttaxlineprice", udtotalcost, 2L);// 寄售含税总价	
				}else if(storeloc.equalsIgnoreCase("ZEE-03")){
					mbo.setValue("udtotalprice", "0", 2L);//非寄售含税单价
					mbo.setValue("udpredictprice", "0", 2L);// 寄售不含税单价
					mbo.setValue("udpredicttaxprice", "0", 2L);// 寄售含税单价
					mbo.setValue("udpredicttaxlineprice", "0", 2L);// 寄售含税总价
				}
			}

			mbo.setValue("udjs", iscon, 11L);

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
		} else {
			mbo.setFieldFlag(attrs3, 7L, false);
		}
	}

	@Override
	public MboSetRemote getList() throws MXException, RemoteException {
		MboSetRemote listSet = super.getList();
		MboRemote mbo = getMboValue().getMbo();
		MboRemote owner = mbo.getOwner();
		if (owner != null) {
			String udcompany = "";
			String udapptype = "";
			if (owner instanceof UDPO) {
				udcompany = owner.getString("udcompany");
				udapptype = owner.getString("udapptype");
			}
			if (owner instanceof UDMatRecTrans) {
				owner = owner.getOwner();
				udcompany = owner.getString("udcompany");
				udapptype = owner.getString("udapptype");
			}
			if (udapptype != null && udapptype.equalsIgnoreCase("POOT")) {
				String sql = "udismthly =1 and udissap = 0";
				listSet.setWhere(sql);
			}
			if ("GR02PCT".equalsIgnoreCase(udcompany) && !mbo.isNull("itemnum")) {
				String itemnum = mbo.getString("itemnum");
				MboSetRemote uditemcpSet = mbo.getMboSet("$UDITEMCP", "UDITEMCP",
						"udcompany='" + udcompany + "' and itemnum='" + itemnum + "'");
				if (!uditemcpSet.isEmpty() && uditemcpSet.count() > 0) {
					MboRemote uditemcp = uditemcpSet.getMbo(0);
					boolean isstock = uditemcp.getBoolean("isstock");
					if (isstock) {
						String sql = "udcompany='GR02PCT' and location !='PCT-01'";
						listSet.setWhere(sql);
					} else {
						String sql = "udcompany='GR02PCT' and location !='PCT-07'";
						listSet.setWhere(sql);
					}
				}
			}
		}
		return listSet;
	}
}
