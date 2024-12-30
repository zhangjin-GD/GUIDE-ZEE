package guide.app.workplan;

import java.rmi.RemoteException;
import java.util.Date;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.MXServer;
import psdi.util.MXException;

public class FldWorkTaskStatus extends MboValueAdapter {

	public FldWorkTaskStatus(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		
		MboRemote mbo = this.getMboValue().getMbo();
		String status = mbo.getString("status");
		Date curDate = MXServer.getMXServer().getDate();
		if(status != null && status.equalsIgnoreCase("E")){
			mbo.setValue("etime", curDate, 11L);
		}
		
	}
}
