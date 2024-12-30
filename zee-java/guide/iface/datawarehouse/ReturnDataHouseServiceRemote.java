package guide.iface.datawarehouse;

import java.rmi.RemoteException;

import psdi.server.AppServiceRemote;
import psdi.util.MXException;

public interface ReturnDataHouseServiceRemote extends AppServiceRemote {
	
	public String UdReturnDataHouseService(String datakey) throws RemoteException, MXException;
	
}
