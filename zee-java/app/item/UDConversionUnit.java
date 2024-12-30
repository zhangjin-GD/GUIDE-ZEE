package guide.app.item;

import java.rmi.RemoteException;

import psdi.app.inventory.FldConversionUnitsOfMeasure;
import psdi.app.item.Item;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
/**
 * DJY
 * ZEE - 在系统中，需限制物资 'to measure unit' = 'issue unit' 
 * 2024-11-28 14:00
 * */
public class UDConversionUnit extends FldConversionUnitsOfMeasure{

	public UDConversionUnit(MboValue mbv) throws MXException, RemoteException {
		super(mbv);
		// TODO Auto-generated constructor stub
	}
	
	public void action() throws MXException, RemoteException{
		super.action();
		MboRemote mbo = getMboValue().getMbo(); //UDCONVERSION
		MboRemote owner = mbo.getOwner();//ITEM
		if ((owner != null) && (owner instanceof Item)){
			//mbo-UDCONVERSION
			String issueunit = owner.getString("issueunit");
			String tomeasureunit = mbo.getString("tomeasureunit");
			if(!issueunit.equalsIgnoreCase(tomeasureunit)){
				Object params[] = { "Please notice the item's issue unit should be equal to tomeasure unit! " };
				throw new MXApplicationException("instantmessaging", "tsdimexception",params);
			}
		}	
	}
	
}
