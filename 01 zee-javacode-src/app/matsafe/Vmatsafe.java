package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.util.MXException;

public class Vmatsafe extends NonPersistentCustomMbo {

	public Vmatsafe(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}
	
}
