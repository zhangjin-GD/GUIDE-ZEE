package guide.app.inventory;

import guide.app.po.UDPO;

import java.rmi.RemoteException;

import psdi.app.inventory.MatRecTrans;
import psdi.app.inventory.MatRecTransRemote;
import psdi.app.po.POLineRemote;
import psdi.app.po.PORemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;

public class UDMatRecTrans extends MatRecTrans implements MatRecTransRemote {

	public UDMatRecTrans(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}
	
	public void init() throws MXException {
		super.init();
		/**
		 * 前提：物资台账的costtype会自动代入PRLINE，POLINE，则costtype取POLINE的集合较好
		 * 前提：库房、货位字段会自动代入PRLINE, POLINE和RECEIPTLINE，则库房取RECEIPTLINE较好
		 * ZEE-接收时，如果属于库存物资（poline的costtype<4000且不是即收即发），库房非空时，库房和货位都必填；
		 * 如果不是库房物资（poline的costtype>4000且即收即发），库房和货位都只读 161-235行
		 */
		try {
			String ponum = getString("ponum");
			Integer polinenum = getInt("polinenum");
			MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE",MXServer.getMXServer().getSystemUserInfo());
			polineSet.setWhere(" ponum = '" + ponum + "' and polinenum = '"+ polinenum + "' ");
			polineSet.reset();
			if (!polineSet.isEmpty() && polineSet.count() > 0) {
				MboRemote poline = polineSet.getMbo(0);
				String udcosttype = poline.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格
				String udstoreroom = getString("udstoreroom");
				String issue = poline.getString("issue");
				if(!udcosttype.equalsIgnoreCase("") && Long.parseLong(udcosttype) < 4000 && !udstoreroom.equalsIgnoreCase("")&& issue.equalsIgnoreCase("N")){
					// 该物资costtype<4000，且库房不为空，为非即收即发
					this.setFieldFlag("udstoreroom", 7L, false);// 取消只读（库房）
					this.setFieldFlag("udbinlocation", 7L, false);// 取消只读（货位）
					this.setFieldFlag("udstoreroom", 128L, true);// 设置必填（库房）
					this.setFieldFlag("udbinlocation", 128L, true);// 设置必填（货位）
				}else if(!udcosttype.equalsIgnoreCase("")  && Long.parseLong(udcosttype)  >= 4000 &&  issue.equalsIgnoreCase("Y")){
					// 该物资costtype>=4000，为即收即发，库房、货位为空
					this.setFieldFlag("udstoreroom", 128L, false);// 取消必填（库房）
					this.setFieldFlag("udbinlocation", 128L, false);// 取消必填（货位）
					this.setFieldFlag("udstoreroom", 7L, true);// 设置只读（库房）
					this.setFieldFlag("udbinlocation", 7L, true);// 设置只读（货位）
					this.setValue("udstoreroom", "", 2L);
					this.setValue("udbinlocation", "", 2L);
				}
				/** 
				 * ZEE - 采购申请capex&project-code
				 * 2025-1-24  13:17  
				 * 58-76
				 */
				String udcapex = getString("udcapex");
				if (udcapex.equalsIgnoreCase("N")) {
					setValue("udprojectnum", "", 11L);
					setFieldFlag("udprojectnum", 7L, false); // 取消只读
					setFieldFlag("udprojectnum", 128L, false); // 取消必填
				}else if(udcapex.equalsIgnoreCase("Y") && isNumeric(udcosttype) &&  Long.parseLong(udcosttype) < 4000){
					setValue("udprojectnum", "", 11L);
					setFieldFlag("udprojectnum", 128L, false); // 设置非必填
					setFieldFlag("udprojectnum", 7L, true); // 设置只读
				}else if(udcapex.equalsIgnoreCase("Y") && isNumeric(udcosttype) && Long.parseLong(udcosttype) >= 4000){
					setFieldFlag("udprojectnum", 7L, false); // 取消只读
					setFieldFlag("udprojectnum", 128L, true); // 设置必填
					setValue("issue", "Y", 2L);
				}
			}
			polineSet.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	@Override
	public void save() throws MXException, RemoteException {

		String status = MXServer.getMXServer().getProperty("guide.exrate.status");
		double udukurs = 1.0;
		PORemote po = getPO();
		
		//ZEE-2023-11-27 17:17:50--增加了&& !getString("tostoreloc").contains("ZEE")
		if (status != null && status.equalsIgnoreCase("ACTIVE") && !getString("tostoreloc").contains("ZEE")) {
			if (po != null && !po.isNull("udukurs")) {
				udukurs = po.getDouble("udukurs");
			}
			double linecost = this.getDouble("linecost");
			double loadedcost = linecost * udukurs;
			setValue("loadedcost", loadedcost, 11L);
		}
		
        /**
         * djy
         * ZEE-入库时如果被勾选了即收即发，则必填移动类型
         * 2024-04-09 11:35:47
         */
		MboRemote owner1 = getOwner();
		String udcompany1 = "";
		if (owner1 != null && owner1 instanceof UDPO) {
			udcompany1 = owner1.getString("udcompany");
		}
//		String issue = getString("issue");
//		if (udcompany1.equalsIgnoreCase("ZEE") && issue.equalsIgnoreCase("Y") && getString("udmovementtype").equalsIgnoreCase("")) {
//			Object params[] = { "Please select movement type!" };
//			throw new MXApplicationException("instantmessaging","tsdimexception", params);
//		}
		
		/**
		 * djy
		 * ZEE-入库时如果被勾选了即收即发，退货时必须整单退（针对入库行）
		 * 2024-04-11 13:35:47
		 */
		int receiptref = getInt("receiptref");
		if (udcompany1.equalsIgnoreCase("ZEE")){
			MboSetRemote matrecSet = owner1.getMboSet("MATRECTRANS");
			matrecSet.setWhere("matrectransid = '" + receiptref +"' ");
			matrecSet.reset();
			if (!matrecSet.isEmpty() && matrecSet.count() > 0) {
				MboRemote matrec = matrecSet.getMbo(0);
				Double receiptqty = matrec.getDouble("quantity");
				Double returntqty = Math.abs(getDouble("quantity"));
				if(Math.abs(receiptqty - returntqty) > 0){
				Object params[] = { "Please notice, due to limitations in the SAP interface, for 'issue on receipt' materials, the return quantity should equal to receipt quantity : " + receiptqty + " ! "};
				throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
			}
		}
        
		super.save();
		setValue("udukurs", udukurs, 11L);
		POLineRemote poline = getPOLine();
		if (poline != null) {
			if (getDouble("linecost") == poline.getDouble("linecost")) {
				setValue("uddmbtr4", poline.getDouble("tax1"), 11L);
			} else if (-getDouble("linecost") == poline.getDouble("linecost")) {
				setValue("uddmbtr4", -poline.getDouble("tax1"), 11L);
			} else {
				setValue("uddmbtr4", getDouble("tax1"), 11L);
			}
		}
		
		/**
		 * djy
		 * ZEE-入库时将正在盘点的明细行标记为DELETE
		 * 2024-01-30 9:35:47
		 */
		String status1 = getString("status");
		MboRemote owner = getOwner();
		String udcompany = "";
		if (owner!=null) {
			udcompany = owner.getString("udcompany");
		}
		if (status1 != null && status1.equalsIgnoreCase("COMP") && udcompany.equalsIgnoreCase("ZEE")) {
			String itemnum = getString("itemnum");
			MboSetRemote udinvstocklineSet = MXServer.getMXServer().getMboSet("UDINVSTOCKLINE",MXServer.getMXServer().getSystemUserInfo());
			udinvstocklineSet.setWhere(" itemnum ='"+ itemnum+ "' and invstocknum in (select invstocknum from udinvstock where status not in ('APPR','CAN','CLOSE')) ");
			udinvstocklineSet.reset();
			if (!udinvstocklineSet.isEmpty() && udinvstocklineSet.count() > 0) {
				for (int i = 0; i < udinvstocklineSet.count(); i++) {
					MboRemote udinvstockline = udinvstocklineSet.getMbo(i);
					udinvstockline.setValue("remark", "DELETE", 11L);
				}
			}
			udinvstocklineSet.save();
			udinvstocklineSet.close();
		}
		
	}
	
    /**
     * 检查字符串是否为数字
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
