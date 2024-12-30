package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.app.location.FldLocation;
import psdi.mbo.Mbo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
/**
 * ZEE - 库存转移，批量更新库房、货位，库房必须只能选择和原始库房保持一致
 * 2024-12-16 15：00
 * */
public class UDFldLocation extends FldLocation{
	
	public UDFldLocation(MboValue mbv) throws MXException {
		super(mbv);
		// TODO Auto-generated constructor stub
	}
	
	public MboSetRemote getList() throws MXException, RemoteException {
		Mbo mbo = this.getMboValue().getMbo();
		String apptype = mbo.getString("udapptype");
		if("transferzee".equalsIgnoreCase(apptype) && mbo.getString("udcompany").equalsIgnoreCase("ZEE")){
			setListCriteria("location = '"+mbo.getString("fromstoreloc")+"' ");
		}
		return super.getList();
	}

}
