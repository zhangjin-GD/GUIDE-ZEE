package guide.app.project;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import guide.app.common.UDMbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class Project extends UDMbo implements MboRemote {

	public Project(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		Date currentDate = MXServer.getMXServer().getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
		String year = sdf.format(currentDate);
		this.setValue("budgetcost", 0, 11L);
		this.setValue("yearcost", 0, 11L);
		this.setValue("year", year, 11L);
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
	}

	@Override
	protected void save() throws MXException, RemoteException {
		super.save();
	}
}
