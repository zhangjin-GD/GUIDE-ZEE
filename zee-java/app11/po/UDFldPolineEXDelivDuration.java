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
 * ZEE - 根据POLINE的期望交货时间，计算期望交货与当前系统时间差
 * @author DJY
 *2024-06-16 10:47:01
 */
public class UDFldPolineEXDelivDuration  extends MboValueAdapter{
	public UDFldPolineEXDelivDuration() {
		super();
	}
	
	public UDFldPolineEXDelivDuration(MboValue mbv) {
		super(mbv);
	}
	
	public void initValue() throws MXException, RemoteException {
		super.initValue();
		MboRemote mbo = getMboValue().getMbo();	
        Date sysdate = MXServer.getMXServer().getDate();
        Date udexdeliverydate = mbo.getDate("udexdeliverydate");
		Double uddeliverydur = 0.0;
		if(udexdeliverydate != null && sysdate.after(udexdeliverydate)){
			uddeliverydur = gettime(udexdeliverydate,sysdate);
			mbo.setValue("uddeliverydur", uddeliverydur, 11L);
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


