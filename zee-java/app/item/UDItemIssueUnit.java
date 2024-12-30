package guide.app.item;

import java.rmi.RemoteException;

import psdi.app.assetcatalog.FldMeasureUnitId;
import psdi.app.item.Item;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
/**
 * DJY
 * ZEE - 在系统中，需限制物资 'to measure unit' = 'issue unit' 
 * 2024-11-28 14:20
 * */
public class UDItemIssueUnit extends FldMeasureUnitId{

	public UDItemIssueUnit(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
		// TODO Auto-generated constructor stub
	}

	public void action() throws MXException, RemoteException{
		super.action();
		MboRemote mbo = getMboValue().getMbo(); //ITEM
		if ((mbo != null) && (mbo instanceof Item)){
			//mbo-ITEM
			String issueunit = mbo.getString("issueunit");
			String itemnum = mbo.getString("itemnum");
			MboSetRemote udconversionSet = MXServer.getMXServer().getMboSet("UDCONVERSION", MXServer.getMXServer().getSystemUserInfo());
			udconversionSet.setWhere("itemnum ='" + itemnum + "' ");
			udconversionSet.reset();
			if (udconversionSet != null && !udconversionSet.isEmpty()){
				String tomeasureunit = udconversionSet.getMbo(0).getString("tomeasureunit");
				if(!issueunit.equalsIgnoreCase(tomeasureunit)){
					Object params[] = { "Please notice the item's issue unit should be equal to tomeasure unit! " };
					throw new MXApplicationException("instantmessaging", "tsdimexception",params);
				}
			}
		}	
	}
}
