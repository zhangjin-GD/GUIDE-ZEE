package guide.app.matsafe;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.mbo.custapp.NonPersistentCustomMbo;
import psdi.util.MXException;

public class VmatsafeLine extends NonPersistentCustomMbo {

	public VmatsafeLine(MboSet ms) throws MXException, RemoteException {
		super(ms);
	}
	
}
