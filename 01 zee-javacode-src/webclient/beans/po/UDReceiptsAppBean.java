package guide.webclient.beans.po;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.webclient.beans.receipts.ReceiptsAppBean;
import guide.app.common.CommonUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @function:订单接收,办公用品接收
 * @date:2023-07-20 23:04:24
 * @modify:
 */
public class UDReceiptsAppBean extends ReceiptsAppBean {
	
//	public synchronized void save() throws MXException {
//		super.save();
//		try {
//			MboRemote mbo = this.app.getAppBean().getMbo();
//			String appname = mbo.getThisMboSet().getApp();
//			String status = mbo.getString("status");
//			if (appname == null) {
//				return;
//			}
//
//			if (appname.equalsIgnoreCase("UDRECZEE")) {
//				/**
//				 * ZEE-入库后,将POLINE里的状态进行赋值
//				 * 2023-07-20 13:54:46
//				 */
//				String udcompany = mbo.getString("udcompany");
//				if (udcompany!=null && !udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")) {
//					MboSetRemote matSet = mbo.getMboSet("PARENTMATRECTRANS");
////					if (!matSet.isEmpty() && matSet.count() > 0) {
////						for (int i = 0; i < matSet.count(); i++) {
////							MboRemote mat = matSet.getMbo(i);
////							if (mat.getString("issuetype").equalsIgnoreCase("RECEIPT")) {
////								double quantity = mat.getDouble("quantity");
////								System.out.println("\n----720--quantity--"+quantity);
////								double polineqty = mat.getDouble("POLINE.orderqty");
////								System.out.println("\n----720--polineqty--"+polineqty);
////								if (quantity == polineqty) {
////									MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
////									polineSet.setWhere(" ponum='"+mat.getString("ponum")+"' and polinenum='"+mat.getInt("polinenum")+"' ");
////									polineSet.reset();
////									if (!polineSet.isEmpty() && polineSet.count() > 0) {
////										MboRemote poline1 = polineSet.getMbo(0);
////										poline1.setValue("udstatus", "FULL", 11L);
////										polineSet.save();
////									}
////									polineSet.close();
////								} else if (quantity < polineqty) {
////									double existqty = 0.0D;
////									MboSetRemote matrecSet = MXServer.getMXServer().getMboSet("MATRECTRANS", MXServer.getMXServer().getSystemUserInfo());
////									matrecSet.setWhere(" issuetype in ('RECEIPT','RETURN') and ponum='"+mat.getString("ponum")+"' and polinenum='"+mat.getInt("polinenum")+"' ");
////									matrecSet.reset();
////									if (!matrecSet.isEmpty() && matrecSet.count() > 0) {
////										existqty = matrecSet.sum("quantity") - quantity;
////									}
////									matrecSet.close();
////									System.out.println("\n----720--existqty--"+existqty);
////									double sumrecqty = quantity + existqty;
////									System.out.println("\n----720--sumrecqty--"+sumrecqty);
////									if (sumrecqty == polineqty) {
////										MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
////										polineSet.setWhere(" ponum='"+mat.getString("ponum")+"' and polinenum='"+mat.getInt("polinenum")+"' ");
////										polineSet.reset();
////										if (!polineSet.isEmpty() && polineSet.count() > 0) {
////											MboRemote poline1 = polineSet.getMbo(0);
////											poline1.setValue("udstatus", "FULL", 11L);
////											polineSet.save();
////										}
////										polineSet.close();
////									} else if (sumrecqty < polineqty) {
////										MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
////										polineSet.setWhere(" ponum='"+mat.getString("ponum")+"' and polinenum='"+mat.getInt("polinenum")+"' ");
////										polineSet.reset();
////										if (!polineSet.isEmpty() && polineSet.count() > 0) {
////											MboRemote poline1 = polineSet.getMbo(0);
////											poline1.setValue("udstatus", "PART", 11L);
////											polineSet.save();
////										}
////										polineSet.close();
////									}
////								}
////							}
////						}
////					}
//					
//					////////////////////////////////////////////////////////////////////////////////////
//                    /**
//                     * ZEE-入库后,将POLINE里的状态进行赋值
//                     * 2023-07-20 13:54:46
//                     * 83-120行
//                     */
////			          MboSetRemote matSet = mbo.getMboSet("PARENTMATRECTRANS");
//					String ponum = mbo.getString("ponum");
//					if (!matSet.isEmpty() && matSet.count() > 0) {
//						for (int i = 0; i < matSet.count(); i++) {
//							int polinenum = matSet.getMbo(i)
//									.getInt("polinenum");
//							String itemnum = matSet.getMbo(i).getString(
//									"itemnum");
//							MboSetRemote matrecSet = MXServer.getMXServer()
//									.getMboSet(
//											"MATRECTRANS",
//											MXServer.getMXServer()
//													.getSystemUserInfo());
//							matrecSet.setWhere(" ponum = '" + ponum
//									+ "'  and polinenum = '" + polinenum
//									+ "' and itemnum = '" + itemnum + "'  ");
//							matrecSet.reset();
//							Double quantity = matrecSet.sum("quantity");
//							matrecSet.close();
//
//							MboSetRemote polineSet0 = matSet.getMbo(i).getMboSet("POLINE");
//							if (!polineSet0.isEmpty() && polineSet0.count() > 0) {
//								MboRemote poline0 = polineSet0.getMbo(0);
//								Double orderqty = poline0.getDouble("orderqty");
//								if (Double.toString(orderqty).equals(
//										Double.toString(quantity))) {
//									poline0.setValue("udstatus", "FULL", 11L);
//									polineSet0.save();
//								} else if (!Double.toString(orderqty).equals(
//										Double.toString(quantity))
//										&& !Double.toString(quantity)
//												.equalsIgnoreCase("0.0")) {
//									poline0.setValue("udstatus", "PART", 11L);
//									polineSet0.save();
//								} else if (!Double.toString(orderqty).equals(
//										Double.toString(quantity))
//										&& Double.toString(quantity)
//												.equalsIgnoreCase("0.0")) {
//									poline0.setValue("udstatus", "APPROVED",11L);
//									polineSet0.save();
//								}
//							}
//						}
//					}
//					
////                  //////////////////////////////////////////////////////////////////////////////////
//					
//					/**
//					 * ZEE-展示接收明细行剩余数量
//					 * @author djy 2024-06-03 14:47:43 102-138行
//					 */
//					
//					MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE",MXServer.getMXServer().getSystemUserInfo());
//					polineSet.setWhere(" ponum = '" + ponum + "' ");
//					polineSet.reset();
//					if (!polineSet.isEmpty() && polineSet.count() > 0) {
//						for (int i = 0; i < polineSet.count(); i++) {
//							MboRemote poline = polineSet.getMbo(i);
//							String polinenum = poline.getString("polinenum");
//							String itemnum = poline.getString("itemnum");
//							Double orderqty = poline.getDouble("orderqty");
//							MboSetRemote mattransSet = MXServer.getMXServer().getMboSet("MATRECTRANS",MXServer.getMXServer().getSystemUserInfo());
//							mattransSet.setWhere(" ponum = '" + ponum+ "'  and polinenum = '" + polinenum+ "' and itemnum = '" + itemnum + "' ");
//							mattransSet.reset();
//							if (!mattransSet.isEmpty() && mattransSet.count() > 0) {
//								Double receiptqty = mattransSet.sum("quantity");
//								Double remainqty = orderqty - receiptqty;
//								MboSetRemote udreceiptSet = MXServer.getMXServer().getMboSet("UDRECEIPT",MXServer.getMXServer().getSystemUserInfo());
//								udreceiptSet.setWhere(" ponum = '" + ponum+ "'  and polinenum = '" + polinenum+ "' and itemnum = '" + itemnum + "' ");
//								udreceiptSet.reset();
//								if (!udreceiptSet.isEmpty() && udreceiptSet.count() > 0) {
//									MboRemote udreceipt = udreceiptSet.getMbo(0);
//									udreceipt.setValue("receiptqty",receiptqty, 11L);
//									udreceipt.setValue("remainqty", remainqty,11L);
//								}
//								udreceiptSet.save();
//								udreceiptSet.close();
//							}
//							mattransSet.close();
//						}
//					}
//					polineSet.close();
//				          
//				}
//
//
//			}
//
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		
//	}
	
	public synchronized void save() throws MXException {
		super.save();
		try {
			MboRemote mbo = this.app.getAppBean().getMbo();
			String appname = mbo.getThisMboSet().getApp();
			String status = mbo.getString("status");
			if (appname == null) {
				return;
			}

			if (appname.equalsIgnoreCase("UDRECZEE")) {
				String udcompany = mbo.getString("udcompany");
				if (udcompany!=null && !udcompany.equalsIgnoreCase("") && udcompany.equalsIgnoreCase("ZEE")) {              
					/**全部代码39-120行
					 * ZEE-展示接收明细行剩余数量
					 * @author djy
					 *2024-06-03 14:47:43
					 *39-124行
					 */
						 String ponum = mbo.getString("ponum");
				          MboSetRemote polineSet = MXServer.getMXServer().getMboSet("POLINE", MXServer.getMXServer().getSystemUserInfo());
				          polineSet.setWhere(" ponum = '" + ponum +"'  ");
				          polineSet.reset();
				          if(!polineSet.isEmpty() && polineSet.count() > 0){
				        	  for(int i = 0 ; i< polineSet.count() ; i++){
				        		  MboRemote poline = polineSet.getMbo(i);
				        		  int polinenum = poline.getInt("polinenum");
				        		  String itemnum = poline.getString("itemnum");
				        		  Double orderqty = poline.getDouble("orderqty");
				                MboSetRemote mattransSet = MXServer.getMXServer().getMboSet("MATRECTRANS", MXServer.getMXServer().getSystemUserInfo());
				                mattransSet.setWhere(" ponum = '" + ponum +"'  and polinenum = '" + polinenum + "' and itemnum = '" + itemnum +"' ");
				                mattransSet.reset();
				                if(!mattransSet.isEmpty() && mattransSet.count() > 0){
				        		 Double receiptqty = mattransSet.sum("quantity");
				        		 Double remainqty = orderqty-receiptqty;
				                MboSetRemote udreceiptSet = MXServer.getMXServer().getMboSet("UDRECEIPT", MXServer.getMXServer().getSystemUserInfo());;
				                udreceiptSet.setWhere(" ponum = '" + ponum +"'  and polinenum = '" + polinenum + "' and itemnum = '" + itemnum +"' ");
				                udreceiptSet.reset();
				                if(!udreceiptSet.isEmpty() && udreceiptSet.count() > 0){
				                	System.out.println("udreceiptline??  change???22222");
				                	MboRemote udreceipt = udreceiptSet.getMbo(0);
				                	udreceipt.setValue("receiptqty", receiptqty, 11L);
				                	udreceipt.setValue("remainqty", remainqty, 11L);
				                	 
				                }
				                udreceiptSet.save();
				                udreceiptSet.close();
				        	  }
				                mattransSet.close();
				        	  }
				          }
				          polineSet.save();
				          polineSet.close();
				          
		                    /**
		                     * ZEE-入库后,将POLINE里的状态进行赋值
		                     * 2023-07-20 13:54:46
		                     * 83-120行
		                     */
				          MboSetRemote matSet = mbo.getMboSet("PARENTMATRECTRANS");
                          if (!matSet.isEmpty() && matSet.count() > 0) {
                              for (int i = 0; i < matSet.count(); i++) {
                                      int polinenum = matSet.getMbo(i)
                                                      .getInt("polinenum");
                                      String itemnum = matSet.getMbo(i).getString(
                                                      "itemnum");
                                      MboSetRemote matrecSet = MXServer.getMXServer()
                                                      .getMboSet(
                                                                      "MATRECTRANS",
                                                                      MXServer.getMXServer()
                                                                                      .getSystemUserInfo());
                                      matrecSet.setWhere(" ponum = '" + ponum
                                                      + "'  and polinenum = '" + polinenum
                                                      + "' and itemnum = '" + itemnum + "'  ");
                                      matrecSet.reset();
                                      Double quantity = matrecSet.sum("quantity");
                                      matrecSet.close();

//                                      MboSetRemote polineSet0 = MXServer.getMXServer().getMboSet("POLINE",MXServer.getMXServer().getSystemUserInfo());
//                                      polineSet0.setWhere(" ponum = '" + ponum+ "'  and polinenum = '" + polinenum+ "' and itemnum = '" + itemnum + "'  ");
//                                      polineSet0.reset();
                                      MboSetRemote polineSet0 = matSet.getMbo(i).getMboSet("POLINE");
                                      if (!polineSet0.isEmpty() && polineSet0.count() > 0) {
                                              MboRemote poline0 = polineSet0.getMbo(0);
                                              Double orderqty = poline0.getDouble("orderqty");
                                              if (Double.toString(orderqty).equals(
                                                              Double.toString(quantity))) {
                                                      poline0.setValue("udstatus", "FULL", 11L);
                                                      polineSet0.save();
//                                                      polineSet0.close();
                                              } else if (!Double.toString(orderqty).equals(
                                                              Double.toString(quantity))
                                                              && !Double.toString(quantity)
                                                                              .equalsIgnoreCase("0.0")) {
                                                      poline0.setValue("udstatus", "PART", 11L);
                                                      polineSet0.save();
//                                                      polineSet0.close();
                                              } else if (!Double.toString(orderqty).equals(
                                                              Double.toString(quantity))
                                                              && Double.toString(quantity)
                                                                              .equalsIgnoreCase("0.0")) {
                                                      poline0.setValue("udstatus", "APPROVED",11L);
                                                      polineSet0.save();
//                                                      polineSet0.close();
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
	
	/**
	 * ZEE - 一键全部打印物资二维码
	 * DJY
	 * 229-296
	 * 2024-07-22 9:39:13
	 */
	public int UDZEEPRINTALL() throws RemoteException, MXException, JSONException{
		MboRemote mbo = this.app.getAppBean().getMbo();
		String udcompany = mbo.getString("udcompany");
		if (udcompany != null && udcompany.equalsIgnoreCase("ZEE")) {
			String printUrl = MXServer.getMXServer().getProperty("guide.udzeePrint.url");
			JSONArray ja = new JSONArray();
			MboSetRemote polineSet = mbo.getMboSet("POLINE");
			Integer ponum = mbo.getInt("ponum");
			if(!polineSet.isEmpty() && polineSet.count() > 0){
				for(int i = 0 ; i < polineSet.count() ; i++){
					MboRemote poline = polineSet.getMbo(i);
					Integer polinenum = poline.getInt("polinenum");
					MboSetRemote matSet = MXServer.getMXServer().getMboSet("MATRECTRANS", MXServer.getMXServer().getSystemUserInfo());
					matSet.setWhere("ponum = '"+ponum+"' and polinenum = '"+polinenum+"' order by transdate desc");
					matSet.reset();
					if(!matSet.isEmpty() && matSet.count() > 0){
		                double quantity = matSet.sum("quantity");
		                int printqty = (int) quantity;
						if(printqty > 0){
						MboRemote mat = matSet.getMbo(0);
						String receivedunit = mat.getString("receivedunit");
						String issueunit = mat.getString("inventory.issueunit");
						String tobin = mat.getString("udbinlocation");
						String itemnum = mat.getString("itemnum");
						String description = mat.getString("item.description");
						String udmatnum = mat.getString("uditemcp.udmatnum");		
						Date transdate = mat.getDate("transdate");
						String transdateStr = CommonUtil.getDateFormat(transdate, "yyyy-MM-dd");
						long invbalancesid = mat.getLong("invbalances.invbalancesid");// ID
						//ZEE条形码key
						String udapikey = mat.getString("uditemcp.udapikey");	
						for(int j = 0; j<printqty; j++){			
						JSONObject jo = new JSONObject();
						jo.put("ponum", ponum);// po单号
						jo.put("receivedunit", receivedunit);// 订购单位
						jo.put("issueunit", issueunit);// 发放单位
						jo.put("binnum", tobin);// 默认货位
						jo.put("itemnum", itemnum);// 物资编码
						jo.put("description", replaceSpecStr(description));// 物资长描述（物资名称、规格、型号）
						jo.put("udmatnum", udmatnum);// 原编码
						jo.put("actualdate", transdateStr);// 入库时间
						jo.put("udprintnum", 1);// 打印数量
						jo.put("id", invbalancesid);// 二维码
						jo.put("udapikey", udapikey);// 二维码							
						ja.put(jo);
						}
						}
					}
				}
			}
			String jsp_url = printUrl + ja.toString();
			clientSession.getCurrentApp().openURL(jsp_url, true);	
		}
		return 1;	
	}
	
	public String replaceSpecStr(String input) {
		if (input != null && !"".equals(input.trim())) {
			String regex = "[~`·；;：:？?，,、|。.!！@#￥$%^&*_+=《》！……&（）——“”''｛{}｝【】]";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(input);
			return matcher.replaceAll("");
		}
		return null;
	}
}
