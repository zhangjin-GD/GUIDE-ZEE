package guide.app.workorder;

import guide.app.common.UDMbo;

import java.rmi.RemoteException;

import psdi.mbo.MboSet;
import psdi.server.MXServer;
import psdi.util.MXException;

public class UDWoHang extends UDMbo{

	private static final int KEYLEN = 2;

	public UDWoHang(MboSet ms) throws RemoteException, MXException {
		super(ms);
	}
	
	@Override
	public void init() throws MXException {
		super.init();
		try {
			setValue("vendtime", MXServer.getMXServer().getDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
