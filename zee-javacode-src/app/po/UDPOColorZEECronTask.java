package guide.app.po;

import java.text.DecimalFormat;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.server.SimpleCronTask;

/**
 *@function:ZEE-超期1周的PO被标记为红色
 *@author:zj
 *@date:2024-06-04 16:20:50
 *@modify:
 */
public class UDPOColorZEECronTask extends SimpleCronTask {
	
	public void cronAction() {
		try {
			MboSetRemote poSet1 = MXServer.getMXServer().getMboSet("PO",this.getRunasUserInfo());
			poSet1.setWhere(" udcompany='ZEE' and status='APPR' and (udfinaldate is not null or udposent is not null) ");
			poSet1.reset();
			if (!poSet1.isEmpty() && poSet1.count() > 0) {
				for (int i = 0; i < poSet1.count(); i++) {
					MboRemote po = poSet1.getMbo(i);
			        Date sysdate = MXServer.getMXServer().getDate();
			        Date udfinaldate = po.getDate("udfinaldate");
			        Date udposent = po.getDate("udposent");
					Double udposentdur = 0.0;
					if(udfinaldate != null && udposent == null && sysdate.after(udfinaldate)){
						udposentdur = gettime(udfinaldate,sysdate);
						po.setValue("udposentdur", udposentdur, 11L);
					} else if(udposent != null && udfinaldate == null && sysdate.after(udposent)){
						udposentdur= gettime(udposent,sysdate);
						po.setValue("udposentdur", udposentdur, 11L);
					} else if(udposent!= null && udfinaldate != null && sysdate.after(udposent)){ //若SENT和PRINT都有，则以SENT为准
						udposentdur = gettime(udposent,sysdate);
						po.setValue("udposentdur", udposentdur, 11L);
					}
					
					if (udposentdur > 168) { //大于1周
						po.setValue("udcolorzee", "A", 11L);
					}
					
				}
			}
			poSet1.save();
			poSet1.close();
			
			MboSetRemote poSet2 = MXServer.getMXServer().getMboSet("PO",this.getRunasUserInfo());
			poSet2.setWhere(" udcompany='ZEE' and status='CLOSE' and udcolorzee is not null ");
			poSet2.reset();
			if (!poSet2.isEmpty() && poSet2.count() > 0) {
				for (int i = 0; i < poSet2.count(); i++) {
					MboRemote po = poSet2.getMbo(i);
					po.setValue("udcolorzee", "", 11L);
				}
			}
			poSet2.save();
			poSet2.close();
			
			
			/**
			 * ZEE-ALL POLINE应用程序，展示所有POLINE的CONFIRMED的时间差，实际交货时间，期望交货时间，
			 * （当前时间-期望交货时间），如果没有CONFIRMED，且当前时间-期望交货时间>=7天，ALL POLINE列表标红
			 * 2024-06-18 10:47:01
			 * 63-106
			 */
			MboSetRemote polineSet1 = MXServer.getMXServer().getMboSet("POLINE",this.getRunasUserInfo());
			polineSet1.setWhere(" udstatus in ('APPROVED','SENT','CONFIRMED') and storeloc like 'ZEE%' ");
			polineSet1.reset();
			if (!polineSet1.isEmpty() && polineSet1.count() > 0){
				for (int j = 0; j < polineSet1.count(); j++) {
					MboRemote poline1 = polineSet1.getMbo(j);
					Date sysdate = MXServer.getMXServer().getDate();
			        Date deliverydate = poline1.getDate("deliverydate");
			        Double uddeliverydur = 0.0;
				if(deliverydate != null && sysdate.after(deliverydate)){
					uddeliverydur = gettime(deliverydate,sysdate);
					if(uddeliverydur >=168){
						poline1.setValue("udcolorzee", "A",11L);
						}
					}

				}
			}
			polineSet1.save();
			polineSet1.close();
			
			MboSetRemote polineSet2 = MXServer.getMXServer().getMboSet("POLINE",this.getRunasUserInfo());
			polineSet2.setWhere(" udstatus in ('PART','FULL','FINISH','CLOSE') and storeloc like 'ZEE%' ");
			polineSet2.reset();
			if (!polineSet2.isEmpty() && polineSet2.count() > 0){
				for (int j = 0; j < polineSet2.count(); j++) {
					MboRemote poline2 = polineSet2.getMbo(j);
			        poline2.setValue("udcolorzee", "",11L);
				}
			}
			polineSet2.save();
			polineSet2.close();
		} catch (Exception e) {
		}
	}
	
	//返回小时
	public double gettime(java.util.Date qrtime1,java.util.Date qrtime2){
        DecimalFormat df = new DecimalFormat("######0.00");
        double t1 = qrtime1.getTime();
        double t2 = qrtime2.getTime();
        String t = df.format((t2-t1)/(60*60*1000));
        return Double.parseDouble(t);
       }
}
