package guide.iface.sap.webservice;

import java.util.List;


public class RequestBean {

	private String ZSOURCE;

    private String BUKRS;

    private String ZSTOCKNO;

    private String BUDAT;

    private String ZDATE1;

    private String LIFNR;

    private String ZTRAN;
    
    public String getZEAMHEADFIELD3() {
		return ZEAMHEADFIELD3;
	}

	public void setZEAMHEADFIELD3(String zEAMHEADFIELD3) {
		ZEAMHEADFIELD3 = zEAMHEADFIELD3;
	}

	public String getZEAMHEADFIELD4() {
		return ZEAMHEADFIELD4;
	}

	public void setZEAMHEADFIELD4(String zEAMHEADFIELD4) {
		ZEAMHEADFIELD4 = zEAMHEADFIELD4;
	}

	public String getZEAMHEADFIELD5() {
		return ZEAMHEADFIELD5;
	}

	public void setZEAMHEADFIELD5(String zEAMHEADFIELD5) {
		ZEAMHEADFIELD5 = zEAMHEADFIELD5;
	}

	public String getZEAMHEADFIELD1() {
		return ZEAMHEADFIELD1;
	}

	public void setZEAMHEADFIELD1(String zEAMHEADFIELD1) {
		ZEAMHEADFIELD1 = zEAMHEADFIELD1;
	}

	public String getZEAMHEADFIELD2() {
		return ZEAMHEADFIELD2;
	}

	public void setZEAMHEADFIELD2(String zEAMHEADFIELD2) {
		ZEAMHEADFIELD2 = zEAMHEADFIELD2;
	}

	private String ZEAMHEADFIELD3;
    private String ZEAMHEADFIELD4;
    private String ZEAMHEADFIELD5;
    private String ZEAMHEADFIELD1;
    private String ZEAMHEADFIELD2;
    
    private List<DT_stock_return_requestIT_EAM_ITEMItem> item;

	public String getZSOURCE() {
		return ZSOURCE;
	}

	public void setZSOURCE(String zSOURCE) {
		ZSOURCE = zSOURCE;
	}

	public String getBUKRS() {
		return BUKRS;
	}

	public void setBUKRS(String bUKRS) {
		BUKRS = bUKRS;
	}

	public String getZSTOCKNO() {
		return ZSTOCKNO;
	}

	public void setZSTOCKNO(String zSTOCKNO) {
		ZSTOCKNO = zSTOCKNO;
	}

	public String getBUDAT() {
		return BUDAT;
	}

	public void setBUDAT(String bUDAT) {
		BUDAT = bUDAT;
	}

	public String getZDATE1() {
		return ZDATE1;
	}

	public void setZDATE1(String zDATE1) {
		ZDATE1 = zDATE1;
	}

	public String getLIFNR() {
		return LIFNR;
	}

	public void setLIFNR(String lIFNR) {
		LIFNR = lIFNR;
	}

	public String getZTRAN() {
		return ZTRAN;
	}

	public void setZTRAN(String zTRAN) {
		ZTRAN = zTRAN;
	}

	public List<DT_stock_return_requestIT_EAM_ITEMItem> getItem() {
		return item;
	}

	public void setItem(List<DT_stock_return_requestIT_EAM_ITEMItem> item) {
		this.item = item;
	}
}
