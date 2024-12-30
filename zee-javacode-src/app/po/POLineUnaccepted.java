package guide.app.po;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

import java.rmi.RemoteException;

public class POLineUnaccepted extends Mbo implements MboRemote {

	public POLineUnaccepted(MboSet ms) throws RemoteException {
		super(ms);
	}

	@Override
	public void modify() throws MXException, RemoteException {
		super.modify();
		setValue("changeby", getUserInfo().getPersonId(), 11L);
		setValue("changetime", MXServer.getMXServer().getDate(), 11L);
	}
}
