package guide.iface.sap.webservice;

public class SecondItemBean {
	private String  BUKRS;       
	public String getBUKRS() {
		return BUKRS;
	}
	public void setBUKRS(String bUKRS) {
		BUKRS = bUKRS;
	}
	public String getUNIQUEID() {
		return UNIQUEID;
	}
	public void setUNIQUEID(String uNIQUEID) {
		UNIQUEID = uNIQUEID;
	}
	public String getCONCEPTLINE() {
		return CONCEPTLINE;
	}
	public void setCONCEPTLINE(String cONCEPTLINE) {
		CONCEPTLINE = cONCEPTLINE;
	}
	public String getZRETURN_CODE() {
		return ZRETURN_CODE;
	}
	public void setZRETURN_CODE(String zRETURN_CODE) {
		ZRETURN_CODE = zRETURN_CODE;
	}
	public String getZITEMMSG() {
		return ZITEMMSG;
	}
	public void setZITEMMSG(String zITEMMSG) {
		ZITEMMSG = zITEMMSG;
	}
	private String  UNIQUEID;    
	private String  CONCEPTLINE; 
	private String  ZRETURN_CODE;
	private String  ZITEMMSG ;   

}
