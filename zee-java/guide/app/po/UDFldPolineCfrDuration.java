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
 * ZEE - 根据POLINE的confirmed时间，计算confirmed与当前时间差
 * @author DJY
 *2024-06-17 10:47:01
 */
public class UDFldPolineCfrDuration extends MboValueAdapter{
	public UDFldPolineCfrDuration() {
		super();
	}
	
	public UDFldPolineCfrDuration(MboValue mbv) {
		super(mbv);
	}
	
	public void initValue() throws MXException, RemoteException {
		// TODO Auto-generated method stub
		super.initValue();
		MboRemote mbo = getMboValue().getMbo();	
        Date sysdate = MXServer.getMXServer().getDate();
        Date udconfirmdate = mbo.getDate("udconfirmdate");
		Double udconfirmdur = 0.0;
		if(udconfirmdate != null && sysdate.after(udconfirmdate)){
			udconfirmdur = gettime(udconfirmdate,sysdate);
			mbo.setValue("udconfirmdur", udconfirmdur, 2L);
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
