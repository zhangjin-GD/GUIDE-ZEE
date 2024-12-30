package guide.app.po;

import java.rmi.RemoteException;

import guide.app.common.FldProjectNum;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPoProjectNum extends FldProjectNum {

    public UDFldPoProjectNum(MboValue mbv) {
        super(mbv);
    }

    @Override
    public void action() throws MXException, RemoteException {
        super.action();
		MboRemote mbo = getMboValue().getMbo();
		String udcapex = mbo.getString("udcapex");
		String udprojectnum = mbo.getString("udprojectnum");
		MboSetRemote lineSet = mbo.getMboSet("POLINE");

		if (!lineSet.isEmpty() && lineSet.count() > 0) {
			for (int i = 0; i < lineSet.count(); i++) {
				MboRemote line = lineSet.getMbo(i);
				line.setValue("udprojectnum", udprojectnum, 11L);
				line.setValue("udcapex", udcapex, 11L);
			}
		}
//        Mbo mbo = this.getMboValue().getMbo();
//        String udprojectnum = this.getMboValue().getString();
//        MboSetRemote lineSet = mbo.getMboSet("POLINE");
//        if (lineSet != null && !lineSet.isEmpty()) {
//            for (int i = 0; lineSet.getMbo(i) != null; i++) {
//                MboRemote line = lineSet.getMbo(i);
//                line.setValue("udprojectnum", udprojectnum, 11L);
//            }
//        }
//
//        /**
//         * ZEE-选择项目编号时将CAPEX带入POLINE
//         * 2023-07-26 DJY
//         */
//        String udcompany = mbo.getString("udcompany");
//
//        if (udcompany!=null && udcompany.equalsIgnoreCase("ZEE")) {
//            if (udprojectnum != null && !udprojectnum.equalsIgnoreCase("")) { //若有project code则勾选capex
//                mbo.setValue("udcapex", "1", 11l);
//            } else {
//                mbo.setValue("udcapex", "0", 11l);
//            }
//            String udcapex = mbo.getString("udcapex");
//            if (lineSet != null && !lineSet.isEmpty()) {
//                for (int i = 0; lineSet.getMbo(i) != null; i++) {
//                    MboRemote line = lineSet.getMbo(i);
//                    line.setValue("udcapex", udcapex, 11L);
//                }
//            }
//        }
    }
}
