package guide.webclient.beans.invoice;


import guide.app.common.CommonUtil;
import guide.iface.sap.webservice.SecondHearBean;
import guide.iface.sap.webservice.SecondWebService;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.webclient.beans.invoice.InvoiceAppBean;
import psdi.webclient.system.controller.WebClientEvent;

public class UDInvoiceAppBean extends InvoiceAppBean{
	
	public void sendSap() throws MXException, RemoteException, SQLException, ParseException, JSONException{
		MboRemote mbo = app.getAppBean().getMbo();
		if (mbo.toBeSaved()) {
			throw new MXApplicationException("guide", "1041");
		}
		String message = "提示，不满足SAP同步条件！";
		String sapnum = mbo.getString("udsapnum");
		String sapStatus = MXServer.getMXServer().getProperty("guide.sap.status");
		String sapDebug = MXServer.getMXServer().getProperty("guide.sap.debug");
		System.out.println("\nINVOICE-----------status" + sapStatus + "-----------debug" + sapDebug);
		if ((sapnum == null || sapnum.equalsIgnoreCase("")) && sapStatus != null && sapStatus.equalsIgnoreCase("ACTIVE")) {
			MboSetRemote invoicelineSet = mbo.getMboSet("INVOICELINE");
			if(!invoicelineSet.isEmpty() && invoicelineSet.count() > 0){
				JSONObject Header = new JSONObject();
				Header = CommonUtil.getIvoHeader(mbo);
				Header.put("item", CommonUtil.getIvoItem(mbo));
				if (CommonUtil.getString(Header, "item").toString().length() > 2) {
					if (sapDebug != null && sapDebug.equalsIgnoreCase("yes")) {
						Object params[] = { "提示，XML:" + Header.toString() + "!" };
						throw new MXApplicationException("instantmessaging", "tsdimexception", params);
					}
					String num = "";
					String status = "";
					try {
						SecondHearBean result = SecondWebService.itemRequestWebService1(Header.toString());
						num = result.getZRETURN_CODE();
						status = result.getZHEADMSG();
						CommonUtil.ifaceLog(Header.toString(), mbo.getUserInfo().getPersonId(), "INVOICE", Header.getString("UNIQUEID"), num, status);
						if (num == null || !status.toLowerCase().startsWith("success")) {
							Object params[] = { "提示：" + status + "!" };
							throw new MXApplicationException("instantmessaging", "tsdimexception", params);
						}
						message = "提示，该发票信息已同步至SAP，凭证号"+num+"！";
					} catch (Exception e) {
						message = e.toString();
					}
					mbo.setValue("udsapnum", num, 11L);
					mbo.setValue("udsapstatus", status, 11L);
					app.getAppBean().save();
				}
			}
		}
		clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", message, 1);
	}
	
	public synchronized void save() throws MXException {
		try {
			MboRemote mbo = this.app.getAppBean().getMbo();
			String appname = mbo.getThisMboSet().getApp();
			if (appname == null) {
				return;
			}
			if (appname.equalsIgnoreCase("UDINVOICE")) {
				String udcompany = mbo.getString("udcompany");
				if (udcompany!=null && !udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")) {
					double NetAmount = 0.0D;
					double VATAmount = 0.0D;
					double sumlinecost = 0.0D;
					double sumtax1 = 0.0D;
					
					MboSetRemote invoicelineSet = mbo.getMboSet("INVOICELINE");
					
					if (!invoicelineSet.isEmpty() && invoicelineSet.count() > 0) {
						//INVOICE不含税总金额、总税额、含税总金额
						NetAmount = mbo.getDouble("udsaplinecost");
						VATAmount = mbo.getDouble("udsaptax");
						
						//INVOICELINE表不含税总金额、总税额、含税总金额
						sumlinecost = invoicelineSet.sum("udsaplinecost");
						sumtax1 = invoicelineSet.sum("tax1");
						
						if (NetAmount!=sumlinecost) {
							clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", "Net Amount is："+NetAmount+", the total number of invoice lines is："+sumlinecost+",\nPlease manually adjust.", 1);
							refreshTable();
							reloadTable();
							this.app.getAppBean().refreshTable();
							this.app.getAppBean().reloadTable();
							return;
						} else if (VATAmount!=sumtax1) {
							clientSession.showMessageBox(clientSession.getCurrentEvent(), "提示", "VAT Amount is："+VATAmount+", the total number of invoice lines is："+sumtax1+",\nPlease manually adjust.", 1);
							refreshTable();
							reloadTable();
							this.app.getAppBean().refreshTable();
							this.app.getAppBean().reloadTable();
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.save();
		try {
			MboRemote mbo = this.app.getAppBean().getMbo();
			String appname = mbo.getThisMboSet().getApp();
			String status = mbo.getString("status");
			if (appname == null) {
				return;
			}
			if (appname.equalsIgnoreCase("UDINVOICE")) {
				/**
				 * ZEE-APPR后,将POLINE里的状态进行赋值
				 * 2023-07-21 13:57:24
				 */
				String udcompany = mbo.getString("udcompany");
				System.out.println("\n---721----udcompany----"+udcompany);
				if (udcompany!=null && !udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")) {
					System.out.println("\n---721----status----"+status);
					if (status!=null && status.equalsIgnoreCase("WAPPR")) {
						MboSetRemote invoicelineSet = mbo.getMboSet("INVOICELINE");
						if (!invoicelineSet.isEmpty() && invoicelineSet.count() > 0) {
							for (int i = 0; i < invoicelineSet.count(); i++) {
								MboRemote invoiceline = invoicelineSet.getMbo(i);
								double invoiceqty = invoiceline.getDouble("invoiceqty");
								double polineqty = invoiceline.getDouble("POLINE.orderqty");
								System.out.println("\n---721----invoiceqty----"+invoiceqty);
								System.out.println("\n---721----polineqty----"+polineqty);
								if (invoiceqty == polineqty) {
									MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
									polineSet.setWhere(" ponum='"+invoiceline.getString("ponum")+"' and polinenum='"+invoiceline.getInt("polinenum")+"' ");
									polineSet.reset();
									if (!polineSet.isEmpty() && polineSet.count() > 0) {
										MboRemote poline1 = polineSet.getMbo(0);
										poline1.setValue("udstatus", "FINISH", 11L);
										polineSet.save();
										System.out.println("\n---721----INVOICE--SAVE----");
									}
									polineSet.close();
								} else if (invoiceqty < polineqty) {
									double existqty = 0.0D;
									MboSetRemote exinvoicelineSet = MXServer.getMXServer().getMboSet("INVOICELINE", MXServer.getMXServer().getSystemUserInfo());
									exinvoicelineSet.setWhere(" invoicenum in (select invoicenum from invoice where status='APPR') and ponum='"+invoiceline.getString("ponum")+"' and polinenum='"+invoiceline.getInt("polinenum")+"' ");
									exinvoicelineSet.reset();
									if (!exinvoicelineSet.isEmpty() && exinvoicelineSet.count() > 0) {
										existqty = exinvoicelineSet.sum("invoiceqty") - invoiceqty;
									}
									exinvoicelineSet.close();
									
									double sumrecqty = invoiceqty + existqty;
									
									if (sumrecqty == polineqty) {
										MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
										polineSet.setWhere(" ponum='"+invoiceline.getString("ponum")+"' and polinenum='"+invoiceline.getInt("polinenum")+"' ");
										polineSet.reset();
										if (!polineSet.isEmpty() && polineSet.count() > 0) {
											MboRemote poline1 = polineSet.getMbo(0);
											poline1.setValue("udstatus", "FINISH", 11L);
											polineSet.save();
										}
										polineSet.close();
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public int UDCHECKINVOICE() throws MXException, RemoteException {
		WebClientEvent event = clientSession.getCurrentEvent();
		String eventValue = event.getValue().toString();
		if (eventValue == null || eventValue.equalsIgnoreCase("")) {
//			eventValue = "http: //221.234.36.40:9999/03.htm1?id=";
			eventValue = "https:";
		}
		MboRemote mbo = app.getAppBean().getMbo();
		String url = eventValue + mbo.getString("udkey");
//		String url = "http://www.chrome.com";
		this.app.openURL(url, true) ;
		return 1;
	}
	
}
