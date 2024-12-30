package guide.app.pr;

import guide.app.po.UDPO;

import java.rmi.RemoteException;

import psdi.mbo.MAXTableDomain;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

public class UDFldPrlineCosttype extends MAXTableDomain {
    /**
     * ZEE - cost type
     * DJY
     * 2024-9-6 15:35:47
     */    
    public UDFldPrlineCosttype(MboValue mbv) {
        super(mbv);
        String thisAttr = getMboValue().getAttributeName();
        setRelationship("UDCOSTTYPE", "UDKOSTENSOORT=:" + thisAttr);
        String[] FromStr = { "UDKOSTENSOORT" };
        String[] ToStr = { thisAttr };
        setLookupKeyMapInOrder(ToStr, FromStr);
    }

    @Override
    public MboSetRemote getList() throws MXException, RemoteException {
        setListCriteria("1 = 1");        
        return super.getList();
    }    

    public void action() throws MXException, RemoteException {
        super.action();
        MboRemote mbo = getMboValue().getMbo();
        MboRemote owner = mbo.getOwner();
        if (owner != null ) {
            String appname = owner.getThisMboSet().getApp();
            String udcosttype = mbo.getString("udcosttype").replaceAll("\\s", ""); // 去除所有空格
            if (owner instanceof UDPR && owner.getString("udapptype").equalsIgnoreCase("PRZEE")){
            // 确认udcosttype是数字字符串
            if (isNumeric(udcosttype)) {
                long udcosttypeValue = Long.parseLong(udcosttype);
                if (udcosttypeValue < 4000) {
                    // PRLINE选择costtype4000以下
                    mbo.setValue("issue", "N", 2L); // 非即收即发
                    mbo.setFieldFlag("issue", 128L, false); // 设置即收即发字段为非必填
                    mbo.setFieldFlag("issue", 7L, true); // 设置即收即发字段只读
                } else {
                    // PRLINE选择costtype4000及以上
                    mbo.setValue("issue", "Y", 2L); // 即收即发
                    mbo.setFieldFlag("issue", 128L, true); // 取消即收即发字段非必填
                    mbo.setFieldFlag("issue", 7L, true); // 设置即收即发字段只读
                    mbo.setValue("storeloc", "", 2L); // 设置库房为空
                    mbo.setFieldFlag("storeloc", 7L, true); // 设置库房只读
                }
            }
            }
            if (appname != null && appname.equalsIgnoreCase("UDRFQZEE")){
            // 确认udcosttype是数字字符串
            if (isNumeric(udcosttype)) {
                long udcosttypeValue = Long.parseLong(udcosttype);         
                if (udcosttypeValue>=4000) {
                    // PRLINE选择costtype4000及以上
                	mbo.setFieldFlag("storeloc",  128L, false); // 取消设置库房必填
                    mbo.setValue("storeloc", "", 2L); // 设置库房为空
                    mbo.setFieldFlag("storeloc", 7L, true); // 设置库房只读
                }else{
                    mbo.setFieldFlag("storeloc", 7L, false); // 取消设置库房只读
                    mbo.setFieldFlag("storeloc",  128L, true); // 设置库房必填
                }
            }
            }
            if (owner instanceof  UDPO && owner.getString("udapptype").equalsIgnoreCase("POZEE")) {
                // 确认udcosttype是数字字符串
                if ( isNumeric(udcosttype)) {
                    long udcosttypeValue = Long.parseLong(udcosttype);
                    
                    if (udcosttypeValue < 4000) {
                        // PRLINE选择costtype4000以下
                        mbo.setValue("issue", "N", 2L); // 非即收即发
                        mbo.setFieldFlag("issue", 128L, false); // 设置即收即发字段为非必填
                        mbo.setFieldFlag("issue", 7L, true); // 设置即收即发字段只读
                    } else {
                        // PRLINE选择costtype4000及以上
                        mbo.setValue("issue", "Y", 2L); // 即收即发
                        mbo.setFieldFlag("issue", 128L, true); // 取消即收即发字段非必填
                        mbo.setFieldFlag("issue", 7L, true); // 设置即收即发字段只读
                        mbo.setValue("storeloc", "", 2L); // 设置库房为空
                        mbo.setFieldFlag("storeloc", 7L, true); // 设置库房只读
                    }
                }
            
            }
            
            // 如果cost type（库存物资4000以下），库房、货位必填
            if (appname != null && appname.equalsIgnoreCase("UDITEM")) {

                if (!mbo.getString("udcompany").equalsIgnoreCase("") && mbo.getString("udcompany").equalsIgnoreCase("ZEE") && !udcosttype.isEmpty() && isNumeric(udcosttype)) {
                    long udcosttypeValue = Long.parseLong(udcosttype);
                    
                    if (udcosttypeValue < 4000) {
                        mbo.setFieldFlag("storeloc", 128L, true); // 设置必填
                        mbo.setFieldFlag("udbin", 128L, true); // 设置必填
                    } else {
                        mbo.setFieldFlag("storeloc", 128L, false); // 设置非必填
                        mbo.setFieldFlag("udbin", 128L, false); // 设置非必填
                    }
                }
            }
            
        }
        
    }

    /**
     * 检查字符串是否为数字
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
