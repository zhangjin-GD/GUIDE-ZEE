package guide.iface.webservice;

import java.rmi.RemoteException;

import javax.jws.WebMethod;

import psdi.server.AppServiceRemote;
import psdi.util.MXException;

public interface MobileServiceRemote extends AppServiceRemote {

	@WebMethod
	public String WebServ(String userId, String token, String langCode, String option, String data)
			throws RemoteException, MXException;

}