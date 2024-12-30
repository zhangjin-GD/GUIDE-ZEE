package guide.webclient.controls;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import psdi.app.inbxconfig.InbxConfigSetRemote;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetInfo;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValueData;
import psdi.mbo.SqlFormat;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.BidiUtils;
import psdi.util.MXException;
import psdi.util.MXFormat;
import psdi.webclient.system.session.WebClientSession;
import psdi.workflow.WFAssignmentSetRemote;
import psdi.workflow.WorkflowDirector;

/**
 * 
 * @功能描述:启动中心收件箱查询过滤
 * @创建人:PR
 * @创建时间:2023-04-25
 * @修改时间:
 */
public class CuInboxPortlet extends psdi.webclient.controls.InboxPortlet
{
    private ArrayList<Hashtable> keyTable;
    private UserInfo userinfo;
	private WebClientSession clientSession = null;
	private MboValueData keyAttribData[][];
	private int noOfAssignments;
	private String latestDate;
	private String inboxDescTitle;

	/*
	 * 描述：构造函数，初始化userinfo、keyTable变量的值
	 */
  public CuInboxPortlet() throws RemoteException, MXException
  {
	  userinfo = MXServer.getMXServer().getSystemUserInfo();
	  keyTable = null;
  }

  /*
   * 描述：获取应用程序名称
   */
	public MboValueData[][] getAppInfo() throws MXException, RemoteException {
		MboValueData[][] appData = null;
		String keyColumns[] = { "app", "description" };
		String keyColumns_zh[] = { "ownerid", "description" };
		String personid = getWebClientSession().getUserInfo().getPersonId();
		String language = getWebClientSession().getUserLanguageCode();
		System.out.println("当前登录人: " + personid + "  系统使用语言：" + language);
		if (language.equalsIgnoreCase("en")) {
			MboSetRemote apps = MXServer.getMXServer().getMboSet("maxapps", userinfo);
			SqlFormat s = new SqlFormat(" app in (select app from wfassignment where assignstatus in('ACTIVE','活动') and assigncode='"
							+ personid + "')");
			apps.setWhere(s.format());
			apps.setOrderBy("description asc");
			appData = apps.getMboValueData(0, 50, keyColumns);
		} else if (language.equalsIgnoreCase("NL")) { //ZEE新增2023-12-08 11:24:40
			MboSetRemote apps = MXServer.getMXServer().getMboSet("L_maxapps", userinfo);
			SqlFormat s = new SqlFormat(" langcode='NL' and ownerid in (select maxappsid  from maxapps  where app in (select app from wfassignment where assignstatus in('ACTIVE','活动') and assigncode='"
							+ personid + "') ) ");
			apps.setWhere(s.format());
			apps.setOrderBy("description asc");
			appData = apps.getMboValueData(0, 50, keyColumns_zh);
		} else if (language.equalsIgnoreCase("ZH")) { //ZEE新增2023-12-08 11:24:40
			MboSetRemote apps = MXServer.getMXServer().getMboSet("L_maxapps", userinfo);
			SqlFormat s = new SqlFormat(" langcode='ZH' and ownerid in (select maxappsid  from maxapps  where app in (select app from wfassignment where assignstatus in('ACTIVE','活动') and assigncode='"
							+ personid + "') ) ");
			apps.setWhere(s.format());
			apps.setOrderBy("description asc");
			appData = apps.getMboValueData(0, 50, keyColumns_zh);
		} else {
			MboSetRemote apps = MXServer.getMXServer().getMboSet("maxapps", userinfo);
			SqlFormat s = new SqlFormat(" app in (select app from wfassignment where assignstatus in('ACTIVE','活动') and assigncode='"
							+ personid + "')");
			apps.setWhere(s.format());
			apps.setOrderBy("description asc");
			appData = apps.getMboValueData(0, 50, keyColumns);

		}

		return appData;
	}
	
	
	/*
	 * 描述：获取待办任务集合
	 */
  public MboValueData[][] getAssignments()
    throws MXException, RemoteException
  {
    String sortAttribute = "";
    String sortType = "";
    int start = 0;

    int rowcount = Integer.parseInt(getProperty("rowstodisplay"));

    String portletCacheId = getPortletCacheId();

    start = Integer.parseInt(checkOrStoreCachedProperty(portletCacheId, "start"));

    sortAttribute = checkOrStoreCachedProperty(portletCacheId, "sortattribute");

    sortType = checkOrStoreCachedProperty(portletCacheId, "sorttype");

    if (!(sortAttribute.equals(""))) {
      sortAttribute = sortAttribute + " " + sortType;
    }
    
    InbxConfigSetRemote inbox = (InbxConfigSetRemote)getDataBean().getMboSet();
   if (inbox != null) {
      return getAssignments(start, rowcount, sortAttribute);
    }
    return ((MboValueData[][])null);

  }
  
  /*
	 * 描述：获取待办
	 * 参数： 
	 * start--开始,
	 * rowcount--行数,
	 * sortBy--排序条件
	 */
	public MboValueData[][] getAssignments(int start, int rowcount, String sortBy)
		    throws RemoteException, MXException
		  {
		
		InbxConfigSetRemote inbox = (InbxConfigSetRemote)getDataBean().getMboSet();
		    Vector columns = getInboxColumns();
		    String[] arrColumns = new String[columns.size()];

		    String[] keyColumns = { "description", "app", "assignid", "ownerid", "isownerlocked", "ownerlockedby" };

		    for (int i = 0; i < columns.size(); i++) {
		      arrColumns[i] = ((String[])(String[])columns.elementAt(i))[0];
		    }

		    WFAssignmentSetRemote wf = (WFAssignmentSetRemote)MXServer.getMXServer().getMboSet("WFASSIGNMENT", inbox.getUserInfo());
		    if (!sortBy.equals("")) {
		      wf.setOrderBy(sortBy);
		    }
	
		   getInboxAssignments(wf);

		    this.inboxDescTitle = wf.getMboValueInfoStatic("description").getTitle();

		    Date date = wf.latestDate("duedate");
		    if (date != null) {
		      this.latestDate = MXFormat.dateTimeToString(date, inbox.getUserInfo().getLocale(), inbox.getUserInfo().getTimeZone());
		    }
		    this.noOfAssignments = wf.count();
           System.out.println("--getAssignments带参: "+wf.count()+"  start: "+start+"  rowcount: "+rowcount+"  sortBy: "+sortBy);

		    MboValueData[][] keyData = wf.getMboValueData(start, rowcount, keyColumns);
		    this.keyAttribData = keyData;

		    MboValueData[][] mvd = wf.getMboValueData(start, rowcount, arrColumns);
		    return mvd;
		  }

  
    /*
	 * 描述：收件箱刷新事件
	 */
	public int filterinbox() {
		clientSession = getWebClientSession();
		HttpServletRequest request = clientSession.getRequest();
		String value = request.getParameter("value");
		String[] skeys = value.split("\\|\\|\\|", -1);
		if (skeys.length >= 3) {
			setProperty("skeyapp", skeys[0]);
			setProperty("skeyword", skeys[1]);
			setProperty("skeyuser", skeys[2]);
			System.out.println("收件箱刷新事件获取参数0："+skeys[0]+" [1]: "+skeys[1]+" [2]: "+skeys[2]);
		} else {
			setProperty("skeyapp", "");
			setProperty("skeyword", "");
			setProperty("skeyuser", "");
		}
		setChangedFlag();
		return 1;
	}

	/*
	 * 描述：根据条件获取待办
	 */
	public void getInboxAssignments(WFAssignmentSetRemote wf)
			throws RemoteException, MXException {
		String wfwhere = " ";
		if (!getProperty("skeyapp").equals("")){
			String language = getWebClientSession().getUserLanguageCode();
			if (language.equalsIgnoreCase("en")) {
				
				wfwhere += " and app='" + getProperty("skeyapp") + "'";
				
			}else{
				String skeyapp=getProperty("skeyapp").replace(",","");
				wfwhere += " and app=(select max(app) from maxapps where maxappsid='" + skeyapp + "') ";
			}
			
		}
			
		
		if (!getProperty("skeyword").equals("")){
			wfwhere += " and description like '%" + getProperty("skeyword")+ "%'";
		}
		if (!getProperty("skeyuser").equals("")){
			wfwhere += " and wfid in (select wfid from wftransaction where personid in (select personid from person where personid like '%"
					+ getProperty("skeyuser")
					+ "%' or displayname like '%"
					+ getProperty("skeyuser")
					+ "%') and transtype in ('WFSTART','开始的 WF'))";
		}
		
		
		
		SqlFormat s = new SqlFormat(wf.getUserInfo(),"assigncode = :1 and assignstatus in ('ACTIVE','活动')" + wfwhere);
		s.setObject(1, "WFASSIGNMENT", "ASSIGNCODE", wf.getUserInfo().getPersonId());
		
		wf.setWhere(s.format());
		wf.reset();
		
		System.out.println("获取记录数： "+wf.count()+"  &执行sql: "+s.format());
		
		if (!wf.isEmpty()) {
			MboRemote person = wf.getMbo(0).getMboSet("ASSIGNEE").getMbo(0);
			String wfMailElection = person.getString("WFMAILELECTION");
			if (wfMailElection.equalsIgnoreCase("CONDITIONAL")
					&& !person.getBoolean("ACCEPTINGWFMAIL")) {
				person.setValue("ACCEPTINGWFMAIL", true, 11L);
				person.getThisMboSet().save();
			}
		}
		
	}
	
	/*
	 * 描述：根据条件获取待办
	 */
  public MboSetInfo[] getSetsInfo()
    throws MXException, RemoteException
  {

	  String wfwhere = "1=1";
		
		if (!getProperty("skeyapp").equals("")){
			
			String language = getWebClientSession().getUserLanguageCode();
			if (language.equalsIgnoreCase("en")) {
				
				wfwhere += " and app='" + getProperty("skeyapp") + "'";
				
			}else{
				String skeyapp=getProperty("skeyapp").replace(",","");
				wfwhere += " and app=(select max(app) from maxapps where maxappsid='" + skeyapp + "') ";
			}
			
		}
		
		if (!getProperty("skeyword").equals(""))
			
			wfwhere += " and description like '%" + getProperty("skeyword")
					+ "%'";
			
		if (!getProperty("skeyuser").equals(""))
			wfwhere += " and wfid in (select wfid from wftransaction where personid in (select personid from person where personid like '%"
					+ getProperty("skeyuser")
					+ "%' or displayname like '%"
					+ getProperty("skeyuser")
					+ "%') and transtype in ('WFSTART','开始的 WF'))";
		
	System.out.println("根据条件获取待办getSetsInfo()执行条件： "+wfwhere);
    String portletCacheId = getPortletCacheId() + "_inbxsetsinfo";
    MboSetInfo[] setArray = { null, null };
  
    if (getWebClientSession().hasStartCenterCache(portletCacheId))
    {
      setArray = (MboSetInfo[])(MboSetInfo[])getWebClientSession().getStartCenterCache(portletCacheId);
    }
    else
    {
      InbxConfigSetRemote inbox = (InbxConfigSetRemote)getDataBean().getMboSet();
      setArray[0] = inbox.getMboSetInfo();
      WFAssignmentSetRemote wf = (WFAssignmentSetRemote)MXServer.getMXServer().getMboSet("WFASSIGNMENT", inbox.getUserInfo());
      wf.setWhere(wfwhere);
	  wf.setOrderBy("duedate asc");
      setArray[1] = wf.getMboSetInfo();
      //getWebClientSession().setStartCenterCache(portletCacheId, setArray);
     
		
    }

    return setArray;
  }

	/*
	 * 描述：获取待办
	 */
  public int getNoOfAssignments()
    throws MXException, RemoteException
  {
    InbxConfigSetRemote inbox = (InbxConfigSetRemote)getDataBean().getMboSet();
    System.out.println("筛选记录数noOfAssignments: "+noOfAssignments);
	if (noOfAssignments == 0) {
		WFAssignmentSetRemote wf = (WFAssignmentSetRemote) MXServer
				.getMXServer().getMboSet("WFASSIGNMENT",
						inbox.getUserInfo());
		getInboxAssignments(wf);
		noOfAssignments = wf.count();
		return noOfAssignments;
	} else {
		return noOfAssignments;
	}
  }

    /*
	 * 描述：获取相关字段值
	 */
  public MboValueData[][] getKeyAttributeData()
    throws MXException, RemoteException
  {
	  MboValueData keyAttributeData[][] = keyAttribData;
 
    if ((this.keyTable == null) || (hasChanged()))
    {
      this.keyTable = new ArrayList();

      System.out.println(" 加载记录数："+keyAttributeData.length);
      for (int j = 0; j < keyAttributeData.length; ++j)
      {
        Hashtable keys = new Hashtable();

        keys.put("app", keyAttributeData[j][1].getData());
        keys.put("assignid", Integer.valueOf(keyAttributeData[j][2].getDataAsInt()));

        keys.put("ownerid", Long.valueOf(keyAttributeData[j][3].getDataAsLong()));

        this.keyTable.add(j, keys);
        System.out.println("---1: "+keyAttributeData[j][1].getData()+"---2: "+Integer.valueOf(keyAttributeData[j][2].getDataAsInt())+"---3: "+Integer.valueOf(keyAttributeData[j][3].getDataAsInt()));
		
	
      }
    }
    return keyAttributeData;
  }
  

  public Vector getInboxColumns()
    throws MXException, RemoteException
  {
    String portletCacheId = getPortletCacheId() + "_inbxcolumns";
    Vector columns = null;

    if (getWebClientSession().hasStartCenterCache(portletCacheId))
    {
      columns = (Vector)getWebClientSession().getStartCenterCache(portletCacheId);
    }
    else
    {
      InbxConfigSetRemote inbox = (InbxConfigSetRemote)getDataBean().getMboSet();
      inbox.resetInboxColumns();
      columns = inbox.getInboxColumns();

      getWebClientSession().setStartCenterCache(portletCacheId, columns);
    }

    return columns;
  }

  public String getSortAttribute(String sortIndex)
  {
    try
    {
      int index = Integer.parseInt(sortIndex);
      if (index == -1)
      {
        return "DESCRIPTION";
      }
      Vector attributes = getInboxColumns();
      String[] attribute = (String[])(String[])attributes.elementAt(index);
      return attribute[0];
    }
    catch (Exception e) {
    }
    return "";
  }

  public ArrayList getNonPersistentColumns()
    throws MXException, RemoteException
  {
    String portletCacheId = getPortletCacheId() + "_inbxnpcolumns";
    ArrayList columns = null;

    if (getWebClientSession().hasStartCenterCache(portletCacheId))
    {
      columns = (ArrayList)getWebClientSession().getStartCenterCache(portletCacheId);
    }
    else
    {
      InbxConfigSetRemote inbox = (InbxConfigSetRemote)getDataBean().getMboSet();
      columns = inbox.getNonPersistentAttributes();

      getWebClientSession().setStartCenterCache(portletCacheId, columns);
    }

    return columns;
  }

  public String getUserName()
  {
    String userName = getWebClientSession().getUserInfo().getDisplayName();
    if (BidiUtils.isBidiEnabled())
      userName = BidiUtils.enforceBidiDirection(userName, BidiUtils.getMboTextDirection("PERSON", "DISPLAYNAME", true));
    return userName;
  }

  public String getLatestDate()
    throws RemoteException, MXException
  {
    InbxConfigSetRemote inbox = (InbxConfigSetRemote)getDataBean().getMboSet();
    return inbox.latestDate();
  }

    /*
	 * 描述：获取描述标题
	 */
  public String getDescTitle()
    throws RemoteException, MXException
  {
    InbxConfigSetRemote inbox = (InbxConfigSetRemote)getDataBean().getMboSet();
    return inbox.getInboxDescTitle();
  }

    /*
	 * 描述：latestDate值
	 */
  public String[] getLabels()
  {
    return super.getLabels("startcntr", new String[] { "noinbxmessages", "previouspagelbl", "nextpagelbl", "scrolltolbl", "inbxreckeylbl", "inbxroutelbl", "refreshlbl", "nextassignmentlbl", "scrolloflbl" });
  }

   /*
	 * 描述：点击链接,打开任务待办
	 */
  public int openassignment()
  {
    String index = getWebClientSession().getCurrentEvent().getValueString();
    Hashtable keys = getKeys(Integer.parseInt(index));

    //getWebClientSession().setLockMboOnEntry(true);

    if (keys == null) {
      return 1;
    }
    String appToGo = keys.get("app").toString();
    if (!(getWebClientSession().getUserApps().contains(appToGo)))
    {
      String[] params = { getWebClientSession().getAppDesc(appToGo) };
      if (BidiUtils.isBidiEnabled())
        params[0] = BidiUtils.applyBidiAttributes("", "", params[0], getWebClientSession().getUserInfo().getLangCode());
      getWebClientSession().showMessageBox(getWebClientSession().getCurrentEvent(), "jspmessages", "noaccesstoapp", params);
      return 1;
    }

    String queryString = "?event=loadapp&value=" + appToGo + "&additionalevent=inboxwf&uniqueid=" + keys.get("ownerid");

    getWebClientSession().gotoApplink(queryString);

    return 1;
  }

    /*
	 * 描述：打开任务待办
	 */
  public int routeassignment()
  {
    String index = getWebClientSession().getCurrentEvent().getValueString();
    Hashtable keys = getKeys(Integer.parseInt(index));

    if (keys == null) {
      return 1;
    }
    //getWebClientSession().setLockMboOnEntry(true);
    String appToGo = keys.get("app").toString();
    if (!(getWebClientSession().getUserApps().contains(appToGo)))
    {
      String[] params = { getWebClientSession().getAppDesc(appToGo) };
      if (BidiUtils.isBidiEnabled())
        params[0] = BidiUtils.applyBidiAttributes("", "", params[0], getWebClientSession().getUserInfo().getLangCode());
      getWebClientSession().showMessageBox(getWebClientSession().getCurrentEvent(), "jspmessages", "noaccesstoapp", params);
      return 1;
    }

    WorkflowDirector director = getWebClientSession().getWorkflowDirector();
    director.setAssignID(((Integer)keys.get("assignid")).intValue());

    String queryString = "?event=loadapp&value=" + appToGo + "&additionalevent=launchwf&uniqueid=" + keys.get("ownerid");
    getWebClientSession().gotoApplink(queryString);

    return 1;
  }

    /*
	 * 描述：获取值
	 */
  private Hashtable getKeys(int index)
  {
    if (this.keyTable == null) {
      return null;
    }
    return ((Hashtable)this.keyTable.get(index));
  }
  

}