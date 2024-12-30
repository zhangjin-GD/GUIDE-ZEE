package guide.app.inventory;

import java.rmi.RemoteException;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;

/**
 *@function:
 *@author:zj
 *@date:下午3:38:46
 *@modify:
 */
public class UDQTYREQUESTED extends MboValueAdapter {

	public UDQTYREQUESTED() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UDQTYREQUESTED(MboValue mbv) {
		super(mbv);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() throws MXException, RemoteException {
		super.init();
		MboRemote mbo = getMboValue().getMbo();
		System.out.println("\n----siteid--"+mbo.getString("siteid"));
        System.out.println("\n----ponum--"+mbo.getString("ponum"));
        System.out.println("\n----polinenum--"+mbo.getInt("polinenum"));
		String issue = "";
		MboSetRemote polineSet = mbo.getMboSet("POLINE");
		System.out.println("\n----countpoline---" + polineSet.count());
		if (!polineSet.isEmpty() && polineSet.count() > 0) {
			MboRemote poline = polineSet.getMbo(0);
			issue = poline.getString("issue");
			System.out.println("\n----666----issue----" + issue);
			if (issue.equalsIgnoreCase("Y")) {
				System.out.println("init456777777777???");
				mbo.setFieldFlag("qtyrequested", 7L, true);// 设置只读，不可更改数量
			}
		}
	}
    
}	
