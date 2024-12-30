package guide.app.itemreq;

import java.rmi.RemoteException;

import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.util.MXException;

public class ItemReqLine extends Mbo implements MboRemote {

	public ItemReqLine(MboSet ms) throws RemoteException {
		super(ms);
	}
	
	public void init() throws MXException {
		super.init();
		try {
			// ZEE以1开头的类型设置默认库房必填，4开头的类型设置默认库房只读；
			String udcompany = getString("udcompany");
			if (!udcompany.isEmpty() && udcompany.equalsIgnoreCase("ZEE")) {
				String classificationid = getString("CLASSSTRUCTURE.classificationid");
				String classificationidfirst = classificationid.substring(0, 1);
				if (classificationidfirst.equalsIgnoreCase("1")) {
					setFieldFlag("storeloc", 128L, true);// 设置必填
					setFieldFlag("storeloc", 7L, false);// 取消设置只读
				}
				if (classificationidfirst.equalsIgnoreCase("4")) {
					setFieldFlag("storeloc", 128L, false);// 取消设置必填
					setFieldFlag("storeloc", 7L, true);// 设置只读
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void add() throws MXException, RemoteException {
		super.add();
		MboRemote parent = getOwner();
		if ((parent != null) && (parent instanceof ItemReq)) {
			String itemreqnum = parent.getString("itemreqnum");
			String udcompany = parent.getString("udcompany");
			int linenum = (int) getThisMboSet().max("linenum") + 1;
			this.setValue("itemreqnum", itemreqnum, 11L);
			this.setValue("linenum", linenum, 11L);
			this.setValue("udcompany", udcompany, 11L);
		}
	}
}
