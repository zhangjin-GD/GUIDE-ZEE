package guide.app.gjobplan;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDGjobTask extends Mbo implements MboRemote {

	public UDGjobTask(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDGjobPlan) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("linenum", linenum, 11L);
			this.setValue("gjpnum", parent.getString("gjpnum"), 11L);
			if(getUserInfo().getLangCode().equalsIgnoreCase("en")){
				this.setValue("result", "OK", 11L);
			}else{
				this.setValue("result", "正常", 11L);
			}
		}
	}

}
