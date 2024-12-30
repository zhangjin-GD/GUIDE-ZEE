package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.location.FldLocation;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldMatReturnLocation extends FldLocation {

	public FldMatReturnLocation(MboValue mbv) throws MXException {
		super(mbv);
	}

	@Override
	public void validate() throws MXException, RemoteException {
//		super.validate();
	}
}
