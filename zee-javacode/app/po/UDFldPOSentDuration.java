package guide.app.po;



import java.rmi.RemoteException;

import java.text.DecimalFormat;

import java.util.Date;

import psdi.mbo.MboRemote;

import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

/**
 * ZEE - 若SENT时间为空则计算PRINT时间差，若PRINT时间为空则计算SENT时间差
 * @author DJY
 *2024-06-03 10:11:01
 */
public class UDFldPOSentDuration extends MboValueAdapter{
	public UDFldPOSentDuration() {
		super();
	}
	
	public UDFldPOSentDuration(MboValue mbv) {
		super(mbv);
	}
	
	@Override
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = getMboValue().getMbo();	
        Date sysdate = MXServer.getMXServer().getDate();
        Date udfinaldate = mbo.getDate("udfinaldate");
        Date udposent = mbo.getDate("udposent");
		Double udposentdur = 0.0;
		if(udfinaldate != null && udposent == null && sysdate.after(udfinaldate)){
			udposentdur = gettime(udfinaldate,sysdate);
			mbo.setValue("udposentdur", udposentdur, 11L);
		}
		if(udposent != null && udfinaldate == null && sysdate.after(udposent)){
			udposentdur= gettime(udposent,sysdate);
			mbo.setValue("udposentdur", udposentdur, 11L);
		}
		if(udposent!= null && udfinaldate != null && sysdate.after(udposent)){ //若SENT和PRINT都有，则以SENT为准
			udposentdur = gettime(udposent,sysdate);
			 mbo.setValue("udposentdur", udposentdur, 11L);
		}
	}
	
	public double gettime(java.util.Date qrtime1,java.util.Date qrtime2){
        DecimalFormat df = new DecimalFormat("######0.00");
        double t1 = qrtime1.getTime();
        double t2 = qrtime2.getTime();
        String t = df.format((t2-t1)/(60*60*1000)/24);
        return Double.parseDouble(t);
       }
  
}
