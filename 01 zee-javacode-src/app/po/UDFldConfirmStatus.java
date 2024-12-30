package guide.app.po;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldConfirmStatus extends MAXTableDomain{

	public UDFldConfirmStatus(MboValue mbv) {
		super(mbv);
	}
	
	public void action() throws MXException, RemoteException{
		super.action();
		MboRemote mbo = getMboValue().getMbo(); //虚拟框
		if(mbo.getString("status").equalsIgnoreCase("CONFIRMED")){
			mbo.setFieldFlag("deliverydate", 128L, true);//设置必填
		}else{
			mbo.setFieldFlag("deliverydate", 128L, false);//设置非必填
		}
	}
}
