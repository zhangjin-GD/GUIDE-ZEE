package guide.app.pr;

import java.rmi.RemoteException;

import guide.app.common.FldProjectNum;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class FldPrProjectNum extends FldProjectNum {

	public FldPrProjectNum(MboValue mbv) {
		super(mbv);
	}

	@Override
	public void action() throws MXException, RemoteException {
		super.action();
		Mbo mbo = this.getMboValue().getMbo();
		String udprojectnum = this.getMboValue().getString();
		MboSetRemote lineSet = mbo.getMboSet("PRLINE");
		if (lineSet != null && !lineSet.isEmpty()) {
			for (int i = 0; lineSet.getMbo(i) != null; i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("udprojectnum", udprojectnum, 11L);
			}
		}
		
        /**
         * ZEE-选择项目编号时将CAPEX带入PRLINE
         * 2023-07-25 16:55:37 DJY
         */
        String udcompany = mbo.getString("udcompany");

        if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
            if (udprojectnum != null && !udprojectnum.equalsIgnoreCase("")) { //若有project code则勾选capex
                mbo.setValue("udcapex", "1", 11l);
            } else {
                mbo.setValue("udcapex", "0", 11l);
            }
            String udcapex = mbo.getString("udcapex");
            if (lineSet != null && !lineSet.isEmpty()) {
                for (int i = 0; lineSet.getMbo(i) != null; i++) {
                    MboRemote line = lineSet.getMbo(i);
                    line.setValue("udcapex", udcapex, 11L);
                }
            }
        }
	}
}
