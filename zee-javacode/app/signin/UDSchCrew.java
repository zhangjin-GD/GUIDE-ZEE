package guide.app.signin;


import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class UDSchCrew extends Mbo implements MboRemote {
	public UDSchCrew(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void init() throws MXException {
		super.init();
		
	}
	
	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if (parent != null) {
			String plannum = parent.getString("schplannum");
			this.setValue("schplannum", plannum, 11L);
		}
	}
	
	@Override
	public void delete(long accessModifier) throws MXException, RemoteException {
		super.delete(accessModifier);
		this.getMboSet("UDSCHLABOR").deleteAll(2L);
	}

	
}