package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.QuotationLine;
import psdi.app.rfq.QuotationLineRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDQuotationLine extends QuotationLine implements QuotationLineRemote {

	public UDQuotationLine(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}

	@Override
	public void copyThisQuotationLineToPOLine(MboRemote po, MboRemote rfqLineRemote, MboRemote prLineRemote)
			throws MXException, RemoteException {
		super.copyThisQuotationLineToPOLine(po, rfqLineRemote, prLineRemote);

		MboSetRemote poLineMboSet = po.getMboSet("POLINE");
		if (!poLineMboSet.isEmpty() && poLineMboSet.count() > 0) {
			for (int i = 0; poLineMboSet.getMbo(i) != null; i++) {
				MboRemote poline = poLineMboSet.getMbo(i);
				if (poline != null && poline.getInt("polineid") == rfqLineRemote.getInt("polineid")
						&& poline.getInt("polinenum") == rfqLineRemote.getInt("polinenum")
						&& poline.getString("ponum") != null
						&& poline.getString("ponum").equalsIgnoreCase(rfqLineRemote.getString("ponum"))
						&& poline.getInt("revisionnum") == rfqLineRemote.getInt("porevisionnum")
						&& poline.getString("siteid") != null
						&& poline.getString("siteid").equalsIgnoreCase(rfqLineRemote.getString("siteid"))) {
					String itemnum = poline.getString("itemnum");
					// 不会带入uditemcp中的成本中心 标准用11L
					poline.setValueNull("itemnum", 2L);
					poline.setValue("itemnum", itemnum, 2L);
					poline.setValue("udprojectnum", rfqLineRemote.getString("udprojectnum"), 11L);
					poline.setValue("udbudgetnum", rfqLineRemote.getString("udbudgetnum"), 11L);
					poline.setValue("gldebitacct", "COSCO", 2L);
					// int uddeliverytime =
					// rfqLineRemote.getInt("udquotationlineisawarded.deliverytime");
					// String udbidinfo =
					// rfqLineRemote.getString("udquotationlineisawarded.udbidinfo");
					// String udpl1 = rfqLineRemote.getString("udquotationlineisawarded.udpl1");
					int uddeliverytime = this.getInt("deliverytime");
					String udbidinfo = this.getString("udbidinfo");
					String udpl1 = this.getString("udpl1");
					poline.setValue("uddeliverytime", uddeliverytime, 11L);
					poline.setValue("udbidinfo", udbidinfo, 11L);
					poline.setValue("pl1", udpl1, 11L);
					poline.setValue("tax1code", this.getString("tax1code"), 2L);
					// double udtotalcost =
					// rfqLineRemote.getDouble("udquotationlineisawarded.udtotalcost");
					double udtotalcost = this.getDouble("udtotalcost");
					// 金额有0.01的误差
					poline.setValue("udtotalcost", 0, 2L);
					if (udtotalcost > 0) {
						poline.setValue("udtotalcost", udtotalcost, 2L);
						poline.setValue("tax1", poline.getDouble("udtotalcost") - poline.getDouble("linecost"), 2L);
					}
					
					/**
					 * ZEE-创建PO时,将rfqline里的带到poline和po
					 * 2023-08-02 11:23:02
					 */
					poline.setValue("udcapex", rfqLineRemote.getString("udcapex"), 11L);
					poline.setValue("udcosttype", rfqLineRemote.getString("udcosttype"), 2L);
					po.setValue("udprojectnum", rfqLineRemote.getString("udprojectnum"), 11L);
					po.setValue("udcapex", rfqLineRemote.getString("udcapex"), 11L);
                    poline.setValue("udcostcenterzee", rfqLineRemote.getString("PRLINE.udcostcenterzee"),11L);
                    poline.setValue("udglzee", rfqLineRemote.getString("PRLINE.udglzee"),11L);
                    poline.setValue("udcosttype", rfqLineRemote.getString("PRLINE.udcosttype"),2L);
                    poline.setValue("tax1code", rfqLineRemote.getString("PRLINE.tax1code"),11L);
                    
					/**
					 * ZEE - poline - udroundfactor,conversion，有询价就代入询价的，没有询价就代入pr的
					 * ZEE - 当从rfq创建po时，授予后自动更新UDITEMCPVEN表
					 * DJY
					 * 77 - 135, 141-146
					 * 2024/12/30 14:10
					 */
					String zeevenconverStatus = MXServer.getMXServer().getProperty("guide.zeevenconver.enabled");
					if (zeevenconverStatus != null && zeevenconverStatus.equalsIgnoreCase("ACTIVE")) {
						MboSetRemote quotationlineSet = rfqLineRemote.getMboSet("QUOTATIONLINE");
						if(!quotationlineSet.isEmpty() && quotationlineSet.count() > 0){
							MboRemote quotationline = quotationlineSet.getMbo(0);
                            if(!String.valueOf(quotationline.getDouble("udroundfactor")).equalsIgnoreCase("") && quotationline.getDouble("udroundfactor") != 0.0){
                            poline.setValue("udroundfactor", quotationline.getDouble("udroundfactor"),11L);
                            }
                            if(!String.valueOf(quotationline.getDouble("udconversion")).equalsIgnoreCase("") && quotationline.getDouble("udconversion") != 0.0){
                            poline.setValue("conversion", quotationline.getDouble("udconversion"),11L);
                            }
							String itemnum1 = quotationline.getString("itemnum");
							String orderunit = quotationline.getString("orderunit");
							Double udroundfactor = quotationline.getDouble("udroundfactor");
							Double udconversion = quotationline.getDouble("udconversion");
							String isawarded = quotationline.getString("isawarded");
							int maxLinenum = 0;
							MboSetRemote itemSet = MXServer.getMXServer().getMboSet("ITEM", MXServer.getMXServer().getSystemUserInfo());
							itemSet.setWhere(" itemnum = '"+itemnum1+"' ");
							itemSet.reset();
							String issueunit = itemSet.getMbo(0).getString("issueunit");
							if(isawarded.equalsIgnoreCase("Y")){
								String vendor = quotationline.getString("vendor");
								 MboSetRemote uditemcpvenSet = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
								 uditemcpvenSet.setWhere(" 1=2 ");
								 MboSetRemote uditemcpvenSet1 = getUditemcpvenSet(itemnum1,vendor);
									if (uditemcpvenSet1.isEmpty()) {
										 MboRemote newUditemcpven = uditemcpvenSet.add(11L);
										 MboSetRemote uditemcpvenSet2 = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
										 uditemcpvenSet2.setWhere(" itemnum = '"+itemnum1+"' ");
										 uditemcpvenSet2.reset();
							            if (!uditemcpvenSet2.isEmpty()) {
							                maxLinenum = uditemcpvenSet2.getMbo(0).getInt("linenum");
							                for(int j = 0; j<uditemcpvenSet2.count(); j++) {
							                    int currentLinenum = uditemcpvenSet2.getMbo(j).getInt("linenum");
							                    if (currentLinenum > maxLinenum) {
							                        maxLinenum = currentLinenum;
							                    }
							                }
							            }else{
							            	maxLinenum = 0;
							            }
									 newUditemcpven.setValue("itemnum", itemnum1,2L);
									 newUditemcpven.setValue("frommeasureunit", orderunit,11L);
									 newUditemcpven.setValue("tomeasureunit", issueunit,11L);
									 newUditemcpven.setValue("conversion", udconversion,11L);
									 newUditemcpven.setValue("roundfactor", udroundfactor,11L);
									 newUditemcpven.setValue("vendor", vendor,11L);
									 newUditemcpven.setValue("udcompany", "ZEE",11L);
									 newUditemcpven.setValue("linenum", maxLinenum+1,11L);
									 uditemcpvenSet.save();
									 uditemcpvenSet.close();
											}
									}
							}
					}
				}
			}
		}
	}
	
	private MboSetRemote getUditemcpvenSet(String itemnum, String uditemcpvendor) throws RemoteException, MXException {
	    MboSetRemote uditemcpvenSet = MXServer.getMXServer().getMboSet("UDITEMCPVEN", MXServer.getMXServer().getSystemUserInfo());
	    uditemcpvenSet.setWhere("udcompany = 'ZEE' and itemnum = '" + itemnum + "' and vendor = '" + uditemcpvendor + "'");
	    uditemcpvenSet.reset();
	    return uditemcpvenSet;
	}
}
