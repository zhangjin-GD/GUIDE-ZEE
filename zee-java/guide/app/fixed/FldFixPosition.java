package guide.app.fixed;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldFixPosition extends MAXTableDomain {

	public FldFixPosition(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = this.getMboValue().getMbo();
		String position = mbo.getString("position");
		String udcompany = mbo.getString("udcompany");
		if(!udcompany.isEmpty() && udcompany.equalsIgnoreCase("GR02PCT")){
			if (!position.isEmpty() && position.equalsIgnoreCase("instore")) {
				mbo.setFieldFlag("itemnum",128L,true);
			} else {
				mbo.setFieldFlag("itemnum",128L,false);
			}	
		}
		
	}
	
	
}
