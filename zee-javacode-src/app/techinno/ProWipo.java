package guide.app.techinno;

import java.rmi.RemoteException;

import guide.app.project.Project;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class ProWipo extends Mbo implements MboRemote {

	public ProWipo(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null && parent instanceof Project) {
			String projectnum = parent.getString("projectnum");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("projectnum", projectnum, 11L);
			this.setValue("linenum", linenum, 11L);
		}
	}
}
