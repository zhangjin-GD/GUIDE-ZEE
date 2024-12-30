package guide.app.invoice;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

/*
 * 2023-10-10
 * 发票中间表：状态为cancel时，1.cancel原因必填  2.cancel原因填写后，弹框
 * 修改人：DJY
 * 
 * */
public class UDFldInvoiceInitCancel extends MAXTableDomain {
	

	public UDFldInvoiceInitCancel(MboValue mbv) {
		super(mbv);
	}

	public void action() throws MXException, RemoteException {
		super.action();
		MboRemote mbo = getMboValue().getMbo();
		String status = mbo.getString("status");// 获取发票中间表的状态
		if (status != null && !status.equalsIgnoreCase("")) {
			if (status.equalsIgnoreCase("Draft")) {
				mbo.setFieldFlag("cancelreason", 128L, false);// 设置非必填
				mbo.setValue("cancelreason", "", 11L);// 清空该字段
				mbo.setFieldFlag("cancelreason", 7L, true);// 设置只读
			} 
			if (status.equalsIgnoreCase("Cancel")) {
				mbo.setFieldFlag("cancelreason", 7L, false);
				mbo.setFieldFlag("cancelreason", 128L, true);
			}
		}
	}

}
