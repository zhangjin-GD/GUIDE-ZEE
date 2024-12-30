package guide.iface.webservice;

import java.rmi.RemoteException;

import javax.jws.WebMethod;

import psdi.server.AppServiceRemote;
import psdi.util.MXException;

public interface CompanyServiceRemote extends AppServiceRemote {
	
	@WebMethod
	public String WebServ(String company, String langCode, String data) throws RemoteException, MXException;

}