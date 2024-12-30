package guide.app.rfq;

import java.rmi.RemoteException;

import psdi.app.rfq.QuotationLine;
import psdi.app.rfq.QuotationLineRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
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
				}
			}
		}
	}
}
