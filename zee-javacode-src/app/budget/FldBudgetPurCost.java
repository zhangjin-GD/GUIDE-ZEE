package guide.app.budget;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldBudgetPurCost extends MboValueAdapter {

	public FldBudgetPurCost(MboValue mbv) {

		super(mbv);
	}

	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		double prIngcost = 0.00d;
		double rfqIngcost = 0.00d;
		double poIngcost = 0.00d;
		double recIngcost = 0.00d;
		double purIngcost = 0.00d;
		double purCompcost = 0.00d;
		MboRemote mbo = this.getMboValue().getMbo();
		
		// 申请 不取消 并 未生成 RFQ PO
		MboSetRemote prIngSet = mbo.getMboSet("PRING"); //PRLINE-prnum in(select prnum from pr where status!='CAN') and rfqnum is null and ponum is null and udbudgetnum=:budgetnum
		if (prIngSet != null && !prIngSet.isEmpty()) {
			prIngcost = prIngSet.sum("linecost");
		}
		mbo.setValue("pringcost", prIngcost, 11L); //在途申请
		
		// 询价 授予前取PR金额
		MboSetRemote rfqIng1Set = mbo.getMboSet("RFQING1"); //PRLINE-exists(select 1 from rfqline where rfqline.rfqlineid=prline.rfqlineid and rfqline.vendor is null and rfqline.udbudgetnum=:budgetnum and exists(select 1 from rfq where rfq.rfqnum=rfqline.rfqnum and rfq.status!='CANCEL'))
		if (rfqIng1Set != null && !rfqIng1Set.isEmpty()) {
			rfqIngcost = rfqIng1Set.sum("linecost");
		}
		
		// 询价 授予后取RFQ金额
		MboSetRemote rfqIngSet = mbo.getMboSet("RFQING"); //QUOTATIONLINE-quotationlineid in(select quotationlineid from quotationline,rfqline where quotationline.rfqnum=rfqline.rfqnum and quotationline.rfqlinenum=rfqline.rfqlinenum and quotationline.isawarded=1 and quotationline.rfqnum in(select rfqnum from rfq where status!='CANCEL') and ponum is null and rfqline.udbudgetnum=:budgetnum)
		if (rfqIngSet != null && !rfqIngSet.isEmpty()) {
			rfqIngcost = rfqIngSet.sum("linecost") + rfqIngcost;
		}
		mbo.setValue("rfqingcost", rfqIngcost, 11L); //在途询价
		
		// 订单 未批准
		MboSetRemote poIngSet = mbo.getMboSet("POING"); //POLINE-ponum in(select ponum from po where status not in('CAN','CLOSE','APPR')) and udbudgetnum=:budgetnum
		if (poIngSet != null && !poIngSet.isEmpty()) {
			poIngcost = poIngSet.sum("linecost");
		}
		mbo.setValue("poingcost", poIngcost, 11L); //在途订单
		
		// 订单已批准
		MboSetRemote recIngSet = mbo.getMboSet("RECING"); //POLINE-ponum in(select ponum from po where status in('APPR')) and udbudgetnum=:budgetnum
		if (recIngSet != null && !recIngSet.isEmpty()) {
			recIngcost = recIngSet.sum("linecost");
		}
		
		// 拒收
		MboSetRemote recIng1Set = mbo.getMboSet("RECING1"); //POLINEUNACCEPTED-udbudgetnum=:budgetnum and exists (select 1 from po where po.ponum=polineunaccepted.ponum and po.status not in('CAN')) and exists (select 1 from poline where polineid=polineunaccepted.polineid)
		if (recIng1Set != null && !recIng1Set.isEmpty()) {
			recIngcost = recIngcost - recIng1Set.sum("linecost");
		}
		
		// 接收完成
		MboSetRemote recCompSet = mbo.getMboSet("RECCOMP"); //MATRECTRANS-udbudgetnum=:budgetnum
		if (recCompSet != null && !recCompSet.isEmpty()) {
			purCompcost = recCompSet.sum("loadedcost");
			recIngcost = recIngcost - purCompcost;
		}
		// 订单金额如果 小于拒收 或 接收金额
		// 汇率影响 比如总成本1，订单汇率7， 接收汇率8， 7-8=-1
		if (recIngcost < 0) {
			recIngcost = 0;
		}

		mbo.setValue("recingcost", recIngcost, 11L); //在途接收

		purIngcost = prIngcost + rfqIngcost + poIngcost + recIngcost;
		mbo.setValue("puringcost", purIngcost, 11L);// 在途采购
		mbo.setValue("purcompcost", purCompcost, 11L);// 接收完成
		mbo.setValue("puringcost", purIngcost + purCompcost, 11L);// 采购已用
		mbo.setValue("purrecost", mbo.getDouble("budgetcost") - purIngcost - purCompcost, 11L);// 采购剩余
		
	    /**
		 * ZEE-显示项目父预算、项目占用预算、项目剩余预算
		 * 2025-02-21 14:35:47
		 *144-177
		 */
		if(mbo.getString("udcompany").equalsIgnoreCase("ZEE")){	
			// 项目父预算
			double udproparentcost = 0.00d;
			// 项目父预算占用 
			double udproparentocu = 0.00d;
			MboSetRemote udprojectSet = mbo.getMboSet("UDPROJECT");
			if (udprojectSet != null && !udprojectSet.isEmpty()) {
				udproparentcost = udprojectSet.sum("budgetcost");
				for(int i = 0; i<udprojectSet.count(); i++){
					MboRemote udproject = udprojectSet.getMbo(i);
					MboSetRemote woSet = udproject.getMboSet("UDWORKORDER");
					if (woSet != null && !woSet.isEmpty()) {
						for(int j = 0; j<woSet.count(); j++){
							MboSetRemote wouseSet = woSet.getMbo(j).getMboSet("MATUSETRANS");
							if (wouseSet != null && !wouseSet.isEmpty()) {
								udproparentocu = udproparentocu + wouseSet.sum("linecost");
							}
						}
					}
					MboSetRemote matrecSet = udproject.getMboSet("UDMATRECTRANS");
					if (matrecSet != null && !matrecSet.isEmpty()) {
						udproparentocu = udproparentocu + matrecSet.sum("linecost");
					}
				}			
				mbo.setValue("udproparentcost", udproparentcost, 11L);
				mbo.setValue("udproparentocu", udproparentocu, 11L);
				mbo.setValue("udproparentleft", udproparentcost - udproparentocu, 11L);	// 项目父预算剩余
			}
		}
	}
}
