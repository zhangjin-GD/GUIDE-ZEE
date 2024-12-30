package guide.app.workorder;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 * 
 * @function:
 * @author:DJY
 * @date:2023-07-24 14:20:51
 * @modify:
 */
public class UDFldWOtypeZEE extends MboValueAdapter {

    public UDFldWOtypeZEE(MboValue mbv) throws MXException {
        super(mbv);
    }


    @Override
    public void action() throws MXException, RemoteException {
        super.action();
        MboRemote mbo = getMboValue().getMbo();
        String udworktype2 = mbo.getString("udworktype2");//定义工单类型
        if (udworktype2 == null) {
			return;
		}
        if (udworktype2.equalsIgnoreCase("PM") || udworktype2.equalsIgnoreCase("CM")) {
            mbo.setValue("status","PLANNED", 2L); //设定初始状态：若为PM，CM则Planned
            mbo.setFieldFlag("udhandler", 128L, false);
        } else {
            mbo.setValue("status","INPROG", 2L);//设定初始状态：若为其他则In progress
            mbo.setFieldFlag("udhandler", 128L, true);
        }
    }

}
