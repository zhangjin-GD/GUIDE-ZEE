package guide.app.po;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

public class FldPoUdDeliverydate extends MboValueAdapter {

	public FldPoUdDeliverydate(MboValue mbv) {
		super(mbv);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		
		MboRemote mbo = this.getMboValue().getMbo();
		MboSetRemote polineSet = mbo.getMboSet("POLINE");
		if (!polineSet.isEmpty() && polineSet.count() > 0) {
			Date poDeliveryDate = mbo.getDate("uddeliverydate");
			MboRemote poline = null;
			String deliveryDate = "";
			for (int i = 0; (poline = polineSet.getMbo(i)) != null; i++) {
				deliveryDate = poline.getString("uddeliverydate");
				if(deliveryDate == null || deliveryDate.equalsIgnoreCase("")){
					poline.setValue("uddeliverydate", poDeliveryDate, 11L);
				}
			}
		}
		
	}
	
	
}
