package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDGwoTask extends Mbo implements MboRemote {

	public UDGwoTask(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof UDWO) {
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("linenum", linenum, 11L);
			this.setValue("wonum", parent.getString("wonum"), 11L);
			if(getUserInfo().getLangCode().equalsIgnoreCase("en")){
				this.setValue("result", "OK", 11L);
				this.setValue("content", parent.getString("description"), 11L);
			}else{
				this.setValue("result", "正常", 11L);
			}
		}
	}

}
