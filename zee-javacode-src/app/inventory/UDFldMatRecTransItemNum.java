package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.inventory.FldMatRecTransItemNum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDFldMatRecTransItemNum extends FldMatRecTransItemNum {

	public UDFldMatRecTransItemNum(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		// 油固定批次为INIT
		MboSetRemote itemCpSet = mbo.getMboSet("ITEMOIL");
		if (!itemCpSet.isEmpty() && itemCpSet.count() > 0) {
			mbo.setValue("tolot", "INIT", 2L);
		}

		/**
		 * ZEE-接收时，如果INVENTORY.BINNUM有值则取
		 * ZEE-接收时，如果INVENTORY.STORELOC有值则取
		 * DJY
		 * 2024-05-16 10:35:47
		 * 27-50行
		 */
		String itemnum = mbo.getString("itemnum");
		String udbin = "";
		String storeloc = "";
			MboSetRemote uditemcpSet = MXServer.getMXServer().getMboSet("UDITEMCP", MXServer.getMXServer().getSystemUserInfo());
			uditemcpSet.setWhere(" itemnum = '" + itemnum + "' and udcompany = 'ZEE' ");
			uditemcpSet.reset();
			if(!uditemcpSet.isEmpty() && uditemcpSet.count() > 0){
				MboRemote uditemcp = uditemcpSet.getMbo(0);
				storeloc = uditemcp.getString("storeloc");
				udbin = uditemcp.getString("udbin");
				//ZEE - 如果台账有默认库房、货位，接收时代入，如果台账没有，代pr，po的 44-50
				MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
				polineSet.setWhere(" itemnum = '" + itemnum + "' and ponum in (select ponum from po where status = 'APPR') order by changedate desc ");
				polineSet.reset();
				MboSetRemote prlineSet = MXServer.getMXServer().getMboSet("PRLINE", MXServer.getMXServer().getSystemUserInfo());
				prlineSet.setWhere(" itemnum = '" + itemnum + "' and prnum in (select prnum from pr where status = 'APPR') order by enterdate desc ");
				prlineSet.reset();
				if(!storeloc.equalsIgnoreCase("")){
				mbo.setValue("udstoreroom", storeloc, 2L);
				MboSetRemote matrecSet = MXServer.getMXServer().getMboSet("MATRECTRANS", MXServer.getMXServer().getSystemUserInfo());
				matrecSet.setWhere(" itemnum = '" + itemnum + "' and tostoreloc='"+storeloc+"'  order by matrectransid desc ");
				matrecSet.reset();
				if (!matrecSet.isEmpty() && matrecSet.count() > 0) {
					mbo.setValue("udbinlocation", matrecSet.getMbo(0).getString("tobin"), 2L);
				}
				matrecSet.close();
				}else if(storeloc.equalsIgnoreCase("")){
					//ZEE - 如果台账有默认库房、货位，接收时代入，如果台账没有，代pr，po的 53-68
					if(!polineSet.isEmpty() && polineSet.count() > 0){
						MboRemote poline = polineSet.getMbo(0);
						if(!poline.getString("storeloc").equalsIgnoreCase("")){
							mbo.setValue("udstoreroom", poline.getString("storeloc"), 2L);
						}
					}else if(!prlineSet.isEmpty() && prlineSet.count() > 0){
						MboRemote prline = prlineSet.getMbo(0);
						if(!prline.getString("storeloc").equalsIgnoreCase("")){
							mbo.setValue("udstoreroom", prline.getString("storeloc"), 2L);
						}
					}
					polineSet.close();
					prlineSet.close();
				}
				if(!udbin.equalsIgnoreCase("")){
				mbo.setValue("udbinlocation", udbin, 2L);
				}
			}
			uditemcpSet.close();
			
			/**前提：物资台账的costtype会自动代入PRLINE，POLINE，则costtype取POLINE的集合较好
			 * 前提：库房、货位字段会自动代入PRLINE, POLINE和RECEIPTLINE，则库房取RECEIPTLINE较好
			 * ZEE-接收时，如果属于库存物资（poline的costtype<4000且不是即收即发），库房非空时，库房和货位都必填；
			 * 如果不是库房物资（poline的costtype>4000且即收即发），库房和货位都只读
			 * DJY
			 * 2024-06-13 15:35:47
			 * 52-87行
			 */
			String ponum = mbo.getString("ponum");
			Integer polinenum = mbo.getInt("polinenum");
			MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
			polineSet.setWhere(" ponum = '" + ponum + "' and polinenum = '"+ polinenum+"' ");
			polineSet.reset();
			if(!polineSet.isEmpty() && polineSet.count() > 0){
				MboRemote poline = polineSet.getMbo(0);
				String udcosttype = poline.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格
				String udstoreroom = mbo.getString("udstoreroom");
				String issue = poline.getString("issue");
				if(!udcosttype.equalsIgnoreCase("") && Long.parseLong(udcosttype) < 4000 && !udstoreroom.equalsIgnoreCase("")&& issue.equalsIgnoreCase("N")){
					//该物资costtype<4000，且库房不为空，为非即收即发
					mbo.setFieldFlag("udstoreroom", 7L, false);//取消只读（库房）
					mbo.setFieldFlag("udbinlocation", 7L, false);//取消只读（货位）
					mbo.setFieldFlag("udstoreroom", 128L, true);//设置必填（库房）
					mbo.setFieldFlag("udbinlocation", 128L, true);//设置必填（货位）
				}else if(!udcosttype.equalsIgnoreCase("") && Long.parseLong(udcosttype)  >= 4000 &&  issue.equalsIgnoreCase("Y")){
					//该物资costtype>=4000，为即收即发，库房、货位为空
					mbo.setFieldFlag("udstoreroom", 128L, false);//取消必填（库房）
					mbo.setFieldFlag("udbinlocation", 128L, false);//取消必填（货位）
					mbo.setFieldFlag("udstoreroom", 7L, true);//设置只读（库房）
					mbo.setFieldFlag("udbinlocation", 7L, true);//设置只读（货位）
					mbo.setValue("udstoreroom", "",2L);
					mbo.setValue("udbinlocation", "",2L);
				}
			}
				polineSet.close();
			
	}
}
