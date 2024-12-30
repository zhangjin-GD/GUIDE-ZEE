package guide.webclient.controls;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import psdi.mbo.MaxMessage;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValueData;
import psdi.util.BitFlag;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.beans.DataBeanListener;
import psdi.webclient.system.beans.ResultsBean;
import psdi.webclient.system.controller.ComponentInstance;
import psdi.webclient.system.controller.ControlInstance;
import psdi.webclient.system.controller.DatasrcInstance;
import psdi.webclient.system.controller.RequestHandler;
import psdi.webclient.system.controller.WebClientEvent;
import psdi.webclient.system.runtime.WebClientRuntime;
import psdi.webclient.system.session.WebClientSession;

public class ImagesList extends DatasrcInstance implements DataBeanListener {
	boolean tableFilterStateChanged = false;
	boolean tableCollapseStateChanged = false;
	boolean tableDetailStateChanged = false;
	boolean tableSelectRowStateChanged = false;
	boolean tableFilterCleared = false;
	boolean tableRowChanged = false;
	boolean tablePageChanged = false;
	boolean tableShowHelp = false;
	boolean tableShowDownload = false;
	boolean hasDetails = false;
	int expandedRow = -5;
	DataBean tableBean = null;
	BitFlag tableFlags = null;
	ControlInstance rowSelect = null;
	ControlInstance rowSelectAll = null;
	boolean rowsChanged = true;
	boolean filterOpenPriorToSelectRecords = false;
	List columnControls = null;
	boolean filterable = true;
	int filterFields = 0;
	boolean detailsExpanded = false;
	boolean lastDetailsExpanded = false;
	boolean multiple = false;
	boolean hideSelectRows = false;
	final int FOCUSED_NONE = -1;
	final int FOCUSED_ROW = 0;
	final int FOCUSED_DETAILS = 1;
	int tableFocus = -1;
	public String focusedCellId = null;
	private int oldCurrentRow = 0;
	private int forcedFocusRow = -1;
	boolean multiSort = true;
	boolean collapsedState = false;

	public void initialize() {
		if (amIinitialize) {
			return;
		}
		amIinitialize = true;
		super.initialize();
		tableBean = getDataBean();
		tableBean.addListener(this);
		rowSelect = null;
		String collapsed = getProperty("collapsed");
		if (WebClientRuntime.isNull(collapsed))
			collapsed = "false";
		if (collapsed.equalsIgnoreCase("true"))
			togglecollapse();
		multiple = getMultiple();

		multiSort = Boolean.valueOf(RequestHandler.getWebClientProperty("webclient.multisorttables", "false")).booleanValue();

		Iterator childControls = getChildren().iterator();
		while (childControls.hasNext()) {
			ControlInstance child = (ControlInstance) childControls.next();
			if (child.getType().equalsIgnoreCase("sectionheader")) {
				labelParams = child.parseParamvalues();
				break;
			}
		}

		boolean detailsOpen = getProperty("rowdetailsexpanded").equalsIgnoreCase("true");
		if (detailsOpen) {
			setExpandedRow(0);
		}
		try {
			String orderBy = getProperty("orderby");
			if (!WebClientRuntime.isNull(orderBy))
				getDataBean().setOrderBy(orderBy);
		} catch (Exception rEx) {
			rEx.printStackTrace();
		}
		String emptyOnClear = getProperty("emptyonclear");
		if (!WebClientRuntime.isNull(emptyOnClear))
			getDataBean().setEmptyOnClear(emptyOnClear.equalsIgnoreCase("true"));
	}

	public boolean getHideSelectRows() {
		return hideSelectRows;
	}

	public void setHideSelectRows(boolean hide) {
		hideSelectRows = hide;
	}

	private boolean getMultiple() {
		boolean multiple = getProperty("selectmode").equalsIgnoreCase("multiple");
		if ((multiple != true) && (getDataBean() != null) && (getDataBean().getParent() != null)) {
			ComponentInstance returnComp = getDataBean().getParent().getReturnComponent();
			if ((returnComp != null) && (returnComp.getProperty("inputmode").equalsIgnoreCase("query"))) {
				if (!getProperty("selectmode").equalsIgnoreCase("single"))
					multiple = true;
			}
		}
		return multiple;
	}

	public void addSelectAllRowControl(ControlInstance selectRow) {
		rowSelectAll = selectRow;
	}

	public void addSelectRowControl(ControlInstance selectRow) {
		rowSelect = selectRow;
	}

	protected void setDetailsExpanded(boolean abool) {
		if (detailsExpanded != abool) {
			detailsExpanded = abool;
			tableDetailStateChanged = true;
		}
	}

	public boolean getFlagValue(int flag) {
		return tableBean.getTableStateFlags().isFlagSet(flag);
	}

	public Object needsRefresh() {
		return getFlagValue(2048) + "";
	}

	public String moreRowsBefore() {
		return ((!getFlagValue(8)) && (tableBean.hasPageRows())) + "";
	}

	public String moreRowsAfter() {
		return ((!getFlagValue(16)) && (tableBean.hasPageRows())) + "";
	}

	public String morePagesBefore() {
		return (!getFlagValue(2)) + "";
	}

	public String morePagesAfter() {
		return (!getFlagValue(4)) + "";
	}

	public String isFiltered() {
		return getFlagValue(256) + "";
	}

	public String isDetailsExpanded() {
		return getFlagValue(64) + "";
	}

	public String isTableStartEmpty() {
		return getFlagValue(8192) + "";
	}

	public String isTableAllSelected() {
		return getFlagValue(4096) + "";
	}

	public String isTableSelectRowsOpen() {
		return getFlagValue(32768) + "";
	}

	public int getCurrentRow() {
		DataBean bean = getDataBean();
		return bean.getCurrentRow();
	}

	public void setOldCurrentRow(int row) {
		oldCurrentRow = row;
	}

	public int getOldCurrentRow() {
		return oldCurrentRow;
	}

	public int setCurrentRow(int row) throws MXException {
		int currentRow = getCurrentRow();
		if (row != currentRow) {
			tableBean.highlightrow(row);
			tableRowChanged = true;
		}
		return 1;
	}

	public void defaultRowFocus(int row) {
		String focusId = (String) getPage().get("currentfocusid");
		boolean keepColumnFocus = false;
		if (!WebClientRuntime.isNull(focusId)) {
			ComponentInstance focusComp = getPage().getComponent(focusId);
			if (focusComp != null) {
				ControlInstance focusTable = focusComp.getControl().getTableControl();
				if (this == focusTable)
					keepColumnFocus = true;
			}
		}
	}

	public void setDefaultFocus() {
		setDefaultFocus(0);
	}

	public void setDefaultFocus(int row) {
		if (row < 0)
			return;
		if (tableFlags.isFlagSet(64L)) {
			detailsExpanded = true;
			defaultDetailFocus();
		} else {
			detailsExpanded = false;
			defaultRowFocus(row);
		}
	}

	public void defaultDetailFocus() {
		getPage().remove("currentfocusrow");
		getDetails().setFocus();
		tableFocus = 1;
	}

	public ControlInstance getDetails() {
		List children = getChildren();
		Iterator childrenIterator = children.iterator();
		ControlInstance child = null;
		if (children != null) {
			while (childrenIterator.hasNext()) {
				child = (ControlInstance) childrenIterator.next();
				if (child.getType().equalsIgnoreCase("tabledetails")) {
					return child;
				}
			}
		}
		return null;
	}

	public int getExpandedRow() {
		return expandedRow;
	}

	public int setExpandedRow(int row) {
		expandedRow = row;
		return 1;
	}

	public int filtertable() {
		sysOut("TABLE [" + getId() + "] - should filter");
		return 1;
	}

	public int clearfilter() throws MXException {
		tableBean.clearfilter();
		return 2;
	}

	void rowChangeFocus() {
		getDataBean().getTableStateFlags().setFlag(1024L, true);
		if (!detailsExpanded) {
			defaultRowFocus(getCurrentRow());
		} else {
			switch (tableFocus) {
			case -1:
			case 0:
				columnFocus();
				break;
			case 1:
				defaultDetailFocus();
			}
		}
	}

	public void columnFocus() {
		if (WebClientRuntime.isNull(focusedCellId)) {
			defaultRowFocus(getCurrentRow());
		} else {
		}
	}

	public void noRowFocus() {
		if (tableFocus == 0)
			tableFocus = -1;
		focusedCellId = null;
	}

	public void setFocusedCellId(String id) {
		focusedCellId = id;
	}

	public int tablehelp() {
		tableShowHelp = true;
		sysOut("TABLE [" + getId() + "] - help");
		return 1;
	}

	public int togglecollapse() {
		if ((tableFlags == null) || (tableFlags.isFlagSet(1L))) {
			tableBean.setTableFlag(1L, false);
			tableBean.setTableFlag(2048L, true);
			lastDetailsExpanded = detailsExpanded;
			sysOut("TABLE [" + getId() + "] - should toggle expand to " + false);
		} else {
			tableBean.setTableFlag(1L, true);
			tableBean.setTableFlag(2048L, true);
			setFocus();
			sysOut("TABLE [" + getId() + "] - toggle expand to " + true);
		}
		tableCollapseStateChanged = true;
		collapsedState = (!collapsedState);
		return 1;
	}

	public boolean getRowSelectVis() {
		if (rowSelect == null)
			return false;
		String vis = rowSelect.getProperty("visible");
		return "true".equals(vis);
	}

	void setRowSelectVis() {
		if (rowSelect != null) {
			boolean flag = (getFlagValue(32768))
					|| ((getProperty("selectmode").equalsIgnoreCase("multiple")) && (getDataBean() != getPage().getAppInstance().getResultsBean()) && (!getHideSelectRows()));

			String vis = rowSelect.getProperty("visible");
			if (!vis.equals(flag + "")) {
				rowSelect.setProperty("visible", flag + "");
				rowSelect.setProperty("visibilitychanged", "true");
				ControlInstance selectrow = (ControlInstance) rowSelect.getChildren().listIterator().next();
				selectrow.setProperty("visible", flag + "");
				selectrow.setProperty("visibilitychanged", "true");
				selectrow.setChangedFlag();
				rowSelectAll.setProperty("visible", flag + "");
				rowSelectAll.setProperty("visibilitychanged", "true");
				ControlInstance selectAllrow = (ControlInstance) rowSelectAll.getChildren().listIterator().next();
				selectAllrow.setProperty("visible", flag + "");
				selectAllrow.setProperty("visibilitychanged", "true");
				selectAllrow.setChangedFlag();
			}
		}
	}

	public boolean wasTableFilterStateChanged() {
		boolean state = tableFilterStateChanged;
		tableFilterStateChanged = false;
		return state;
	}

	public boolean wasTableDetailStateChanged() {
		boolean state = (tableDetailStateChanged) || (tableBean.getTableStateFlags().isFlagSet(64L) != detailsExpanded);
		return state;
	}

	public boolean wasTableCollapseStateChanged() {
		boolean state = tableCollapseStateChanged;
		tableCollapseStateChanged = false;
		return state;
	}

	public boolean wasTableFilterCleared() {
		boolean state = tableFilterCleared;
		tableFilterCleared = false;
		return state;
	}

	public boolean wasTableRowChanged() {
		boolean state = tableRowChanged;
		tableRowChanged = false;
		return state;
	}

	public boolean wasTablePageChanged() {
		boolean state = tablePageChanged;
		tablePageChanged = false;
		return state;
	}

	public boolean shouldTableShowHelp() {
		boolean state = tableShowHelp;
		tableShowHelp = false;
		return state;
	}

	public boolean wasTableSelectRowStateChanged() {
		boolean state = tableSelectRowStateChanged;
		tableSelectRowStateChanged = false;
		return state;
	}

	public void setHasDetails(boolean hasDetails) {
		this.hasDetails = hasDetails;
	}

	private void sysOut(String message) {
		getWebClientSession().printDebugMessage(2, "Table.java: " + message);
	}

	public void dataChangedEvent(DataBean bean) {
	}

	public void structureChangedEvent(DataBean bean) {
		rowsChanged = true;
	}

	// 全部数据
	public MboValueData[][] getAllImageData() throws MXException, RemoteException {
		String keyColumns[] = { getProperty("img_path"), getProperty("img_desc"), "" };
		MboSetRemote idata = getDataBean().getMboSet();
		MboValueData imagesData[][] = idata.getMboValueData(0, getDataBean().count(), keyColumns);
		return imagesData;
	}

	@SuppressWarnings("deprecation")
	public String reqid() {
		String curid = getPage().getAppInstance().getApp() + "_" + this.getId();
		return curid;
	}

	public int rescount() throws RemoteException, MXException {
		return tableBean.count();
	}

	public int openallimages() throws RemoteException, MXException {
		String relPath = getWebClientSession().getRequest().getContextPath() + "/webclient/guide/imageslist/";
		getPage().getAppInstance().openURL(relPath + "imageshow.jsp?curid=" + reqid(), true);
		return 1;
	}

	// 取部分数据
	public MboValueData[][] getImageData() throws MXException, RemoteException {
		String keyColumns[] = { getProperty("img_path"), getProperty("img_desc"), "" };

		int int_numsperrow = Integer.parseInt(getProperty("img_numsperrow"));
		int int_rowsperpage = Integer.parseInt(getProperty("img_rowsperpage"));

		String orderby = getProperty("orderby");
		MboSetRemote idata = tableBean.getMboSet();
		if (!orderby.equals(""))
			idata.setOrderBy(orderby);

		MboValueData imagesData[][] = idata.getMboValueData(getCurrentRow(), int_numsperrow * int_rowsperpage, keyColumns);
		return imagesData;
	}

	@SuppressWarnings("deprecation")
	public int gotoPage() throws MXException, RemoteException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		forceFocusRow(-1);
		// DataBean tableBean = getDataBean();
		// tableBean.addListener(this);
		HttpServletRequest request = getWebClientSession().getRequest();
		String value = request.getParameter("value");

		if (!value.equals("")) {
			int pagenum = Integer.parseInt(value);
			int numsperpage = Integer.parseInt(getProperty("tbl_displayrows"));

			tableBean.fetchTableData(pagenum * numsperpage);
			tableBean.getTableStateFlags().setFlag(2048L, true);
			tableBean.fireStructureChangedEvent();

			int currentdata = (pagenum - 1) * numsperpage;
			// setCurrentRow(tableBean.getTableOffset());
			setCurrentRow(currentdata);

			defaultRowFocus(getCurrentRow());
		}

		setChangedFlag(true);

		return 1;
	}

	public boolean validprop() {
		boolean res = false;
		if (!getProperty("img_path").equals("") && !getProperty("img_desc").equals(""))
			if (!getProperty("relationship").equals("") || (!getProperty("parentdatasrc").equals("") && !getProperty("mboname").equals("")))
				res = true;
		return res;
	}

	@SuppressWarnings("deprecation")
	public int render() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String spArray[] = { "1", "0", "0" };
		String plArray[] = { "0", "0", "0", "0", "0" };

		boolean showCount = false;
		if (!getWebClientSession().getCurrentApp().get("designmode").equals("true"))
			showCount = tableBean.getPageRowCount() != 0;
		if (tableBean != null) {
			try {
				if (!validprop()) {
					tableBean.setAppWhere("1=2");
					tableBean.reset();
				}

				if (tableBean.count() > 0) {
					// double pageEndRow = tableBean.getPageEndRow();
					double currentrow = getCurrentRow();
					// double pageRowCount = tableBean.getPageRowCount();
					double totalcount = tableBean.count();
					double currentdate = currentrow + 1.0;

					int img_numsperrow = Integer.parseInt(getProperty("img_numsperrow"));
					int img_rowsperpage = Integer.parseInt(getProperty("img_rowsperpage"));

					double displayrows = img_numsperrow * img_rowsperpage;

					int currentpage = (int) Math.ceil(currentdate / displayrows);
					int totalpages = (int) Math.ceil(totalcount / displayrows);

					setProperty("tbl_currentpage", String.valueOf(currentpage)); // 当前页码
					setProperty("tbl_totalpages", String.valueOf(totalpages)); // 总页数
					setProperty("tbl_displayrows", String.valueOf(img_numsperrow * img_rowsperpage)); // 每页条数
					setProperty("havedata", "true");

					spArray[0] = getProperty("tbl_currentpage");
					spArray[1] = getProperty("tbl_totalpages");
					spArray[2] = getProperty("tbl_displayrows");

					plArray[0] = String.valueOf(tableBean.getPageStartIndex() + 1);
					plArray[1] = String.valueOf(tableBean.getPageStartIndex() + tableBean.getPageRowCount());
					plArray[2] = String.valueOf(tableBean.count());
					plArray[3] = String.valueOf(currentpage);
					plArray[4] = String.valueOf(totalpages);

				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String spLabel = "", plLabel = "";
		WebClientSession wcs = getWebClientSession();
		if (showCount) {
			spLabel = wcs.getMessage("tableinfo", "HXQH_SplitPageLabel", spArray);
			plLabel = wcs.getMessage("tableinfo", "HXQH_TblPageLabel", plArray);
		}
		setProperty("lbl_tablepager", spLabel);
		setProperty("lbl_tablecounter", plLabel);

		preRender();
		int ret = super.render();

		setChangedFlag(true);
		if (tableBean != null)
			databean.resetJSPFlags();
		return ret;
	}

	public void sortColumn() throws MXException {
		WebClientSession wcs = getWebClientSession();
		WebClientEvent event = wcs.getCurrentEvent();
		HttpServletRequest request = wcs.getRequest();
		String newlyBuiltOrderBy = "";
		String property = request.getParameter("property");
		if (WebClientRuntime.isNull(property))
			property = "dataattribute";
		String columnid = (String) event.getValue();
		String orderBy = tableBean.getOrderBy() + "";
		StringTokenizer orderBys = new StringTokenizer(orderBy, ",");
		ControlInstance column = wcs.getControlInstance(columnid);
		String sortAttribute = column.getProperty(property);
		boolean found = false;
		while (orderBys.hasMoreTokens()) {
			String ob = orderBys.nextToken();
			if (ob != null) {
				ob = ob.toUpperCase().trim();
				sortAttribute = sortAttribute.toUpperCase();
				if (ob.indexOf(sortAttribute) >= 0) {
					int space = ob.indexOf(" ");
					if (space > 0) {
						if (ob.indexOf("ASC", space) > 0)
							ob = WebClientRuntime.replaceString(ob, "ASC", "DESC");
						else if (ob.indexOf("DESC", space) > 0) {
							ob = "";
						}
					} else {
						ob = ob + " DESC";
					}
					found = true;
				}
				if (!ob.equals("")) {
					if (!newlyBuiltOrderBy.trim().equals(""))
						newlyBuiltOrderBy = newlyBuiltOrderBy + ", ";
					if (multiSort)
						newlyBuiltOrderBy = newlyBuiltOrderBy + ob;
					else
						newlyBuiltOrderBy = ob;
				}
			}
		}
		if (!found) {
			if ((!newlyBuiltOrderBy.trim().equals("")) && (!sortAttribute.equals(""))) {
				newlyBuiltOrderBy = newlyBuiltOrderBy + ", ";
			}
			if (multiSort)
				newlyBuiltOrderBy = newlyBuiltOrderBy + sortAttribute + " ASC";
			else
				newlyBuiltOrderBy = sortAttribute + " ASC";
		}
		tableBean.sort(newlyBuiltOrderBy);
	}

	public String getSortLevel(String sortAttribute) {
		if (multiSort) {
			String orderBy = getDataBean().getOrderBy();
			if (orderBy != null) {
				StringTokenizer orderBys = new StringTokenizer(orderBy, ",");
				int count = 0;
				String ob = "";
				while (orderBys.hasMoreTokens()) {
					count++;
					ob = orderBys.nextToken();
					if (ob.toLowerCase().indexOf(sortAttribute.toLowerCase()) > -1)
						return count + "";
				}
			}
		}
		return "";
	}

	public String getSortOrder(String sortAttribute) {
		String orderBy = getDataBean().getOrderBy();
		if (orderBy != null) {
			StringTokenizer orderBys = new StringTokenizer(orderBy, ",");
			String checkString = (sortAttribute + " ").toLowerCase();
			while (orderBys.hasMoreTokens()) {
				String ob = orderBys.nextToken().trim();
				if (ob != null) {
					ob = ob.toLowerCase();
					if (ob.indexOf(checkString) == 0) {
						String ret = ob.substring(checkString.length());
						return ret;
					}
					if (ob.indexOf(checkString.trim()) == 0) {
						return "asc";
					}
				}
			}
		}

		return "";
	}

	public void increaseFilters() {
		filterFields += 1;
	}


	public void showMXException(MXException mxe) {
		WebClientSession wcs = getWebClientSession();
		MaxMessage message = wcs.getMaxMessage(mxe);
		Hashtable messageInfo = wcs.getMessageInfoForTable(message, mxe);
		wcs.generateMessageBoxForTable(messageInfo);
		if ((tableBean instanceof ResultsBean)) {
			try {
				((ResultsBean) tableBean).clearfilter();
			} catch (Exception e) {
			}
		}
	}

	public void showRemoteException(RemoteException rme) {
		WebClientSession wcs = getWebClientSession();
		MaxMessage message = wcs.getMaxMessage(rme);
		Object[] params = new Object[0];
		Hashtable messageInfo = wcs.getMessageInfoForTable(message, params);
		wcs.generateMessageBoxForTable(messageInfo);
		if ((tableBean instanceof ResultsBean)) {
			try {
				((ResultsBean) tableBean).clearfilter();
			} catch (Exception e) {
			}
		}
	}

	public void forceFocusRow(String row) {
		try {
			if (row != null)
				forceFocusRow(Integer.parseInt(row));
		} catch (Exception ex) {
		}
	}

	public void forceFocusRow(int row) {
		forcedFocusRow = row;
	}

}