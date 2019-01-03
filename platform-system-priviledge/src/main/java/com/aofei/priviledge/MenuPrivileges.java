package com.aofei.priviledge;

public class MenuPrivileges {
	//权限掩码的含义 1表示有权限，0表示没有权限
	//权限的顺序依次是：createFile,editFile,deleteFile,readFile,execute
	/**
	 * 主界面菜单项
	 */
	//文件(File)
	public static final String FILE_NEW = "10000";
	public static final String FILE_OPEN = "01011";
	public static final String FILE_SAVE = "11000";
	public static final String FILE_SAVE_AS = "10000";
	public static final String FILE_PRINT = "01000";
	public static final String FILE_IMPORT_FROM_XML = "10000";
	public static final String FILE_EXPORT = "01000";
	public static final String FILE_QUIT = "11111";
	public static final String FILE_OPEN_RECENT = "01010";
	//编辑(Edit)
	public static final String EDIT_UNDO = "01000";
	public static final String EDIT_REDO = "01000";
	public static final String EDIT_CUT_STEPS = "01000";
	public static final String EDIT_COPY_STEPS = "01000";
	public static final String EDIT_COPY_FILE = "01000";
	public static final String EDIT_PASTE_STEPS = "01000";
	public static final String TRANS_COPY_IMAGE = "01011";
	public static final String EDIT_CLEAR_SELECTION = "01111";
	public static final String EDIT_SELECT_ALL = "01111";
	public static final String EDIT_SEARCH_METADATA = "01010";
	public static final String EDIT_SET_ENV_VARS = "01000";
	public static final String EDIT_SHOW_ENV_VARS = "01000";
	public static final String EDIT_KETTLE_PROPERTIES = "01000";
	public static final String EDIT_SHOW_ARGUMENTS = "01000";
	public static final String EDIT_SETTINGS = "01000";
	//视图(View)
	//暂时不需要控制
	
	//执行(Action)
	public static final String PROCESS_RUN= "00001";
	public static final String TRANS_PREVIEW= "00001";
	public static final String TRANS_DEBUG= "00001";
	public static final String TRANS_REPLAY= "00001";
	public static final String TRANS_VERIFY= "01001";
	public static final String TRANS_IMPACT= "01001";
	public static final String TRANS_GET_SQL= "00001";
	public static final String TRANS_LAST= "11111";
	
	//工具(Tools)
	public static final String TOOLS_DATABASE_EXPLORER= "01000";
	public static final String TOOLS_DATA_CLEAR_CACHE= "00001";
	public static final String REPOSITORY_CONNECT= "11111";
	public static final String REPOSITORY_DISCONNECT= "11111";
	public static final String REPOSITORY_EXPLORE= "11111";
	public static final String REPOSITORY_CLEAR_SHARED_OBJECT_CACHE= "00001";
	public static final String REPOSITORY_EXPORT_ALL= "01000";
	public static final String REPOSITORY_IMPORT_DIRECTORY= "10000";
	public static final String WIZARD_CONNECTION= "01000";
	public static final String WIZARD_COPY_TABLE= "01000";
	public static final String WIZARD_COPY_TABLES= "01000";
	public static final String EDIT_OPTIONS= "11111";
	
	//帮助(Help)
	//暂时不需要控制
	
	/**
	 * 主对象树，右键菜单
	 */
	//转换
	public static final String TRANS_CLASS_NEW= "10000";
	public static final String TRANS_INST_SETTINGS= "01000";
	public static final String TRANS_INST_LOGGING= "11111";
	public static final String TRANS_INST_HISTORY= "11111";
	
	public static final String JOB_CLASS_NEW= "10000";
	public static final String JOB_INST_SETTINGS= "10000";
	public static final String JOB_INST_LOGGING= "11111";
	public static final String JOB_INST_HISTORY= "11111";
	
	public static final String JOB_ENTRY_COPY_EDIT= "01000";
	public static final String JOB_ENTRY_COPY_DUPLICATE= "01000";
	public static final String JOB_ENTRY_COPY_DELETE= "01000";
	
	public static final String TRANS_HOP_CLASS_NEW= "01000";
	public static final String TRANS_HOP_CLASS_SORT= "01000";
	public static final String TRANS_HOP_INST_EDIT= "01000";
	public static final String TRANS_HOP_INST_DELETE= "01000";
	
	public static final String DATABASE_CLASS_NEW= "01000";
	public static final String DATABASE_CLASS_CONNECTION_WIZARD= "01000";
	public static final String DATABASE_CLASS_CLEAR_CACHE= "00001";
	public static final String DATABASE_INST_NEW= "01000";
	public static final String DATABASE_INST_EDIT= "01000";
	public static final String DATABASE_INST_DUPLICATE= "01000";
	public static final String DATABASE_INST_COPY= "01000";
	public static final String DATABASE_INST_DELETE= "01000";
	public static final String DATABASE_INST_SQL_EDIT= "01000";
	public static final String DATABASE_INST_CLEAR_CACHE= "01000";
	public static final String DATABASE_INST_SHARE= "01000";
	public static final String DATABASE_INST_EXPLORE= "01000";
	public static final String DATABASE_INST_DEPENDANCY= "01000";
	
	public static final String STEP_INST_EDIT= "01000";
	public static final String STEP_INST_DUPLICATE= "01000";
	public static final String STEP_INST_DELETE= "01000";
	public static final String STEP_INST_SHARE= "01000";
	
	public static final String PARTITION_SCHEMA_CLASS_NEW= "01000";
	public static final String PARTITION_SCHEMA_INST_EDIT= "01000";
	public static final String PARTITION_SCHEMA_INST_DELETE= "01000";
	public static final String PARTITION_SCHEMA_INST_SHARE= "01000";
	
	public static final String CLUSTER_SCHEMA_CLASS_NEW= "01000";
	public static final String CLUSTER_SCHEMA_INST_EDIT= "01000";
	public static final String CLUSTER_SCHEMA_INST_DELETE= "01000";
	public static final String CLUSTER_SCHEMA_INST_SHARE= "01000";
	public static final String CLUSTER_SCHEMA_INST_MONITOR= "00001";
	
	public static final String SLAVE_CLUSTER_CLASS_NEW= "01000";
	public static final String SLAVE_SERVER_INST_EDIT= "01000";
	public static final String SLAVE_SERVER_INST_DELETE= "01000";
	public static final String SLAVE_SERVER_INST_SHARE= "01000";
	public static final String SLAVE_SERVER_INST_MONITOR= "00001";
	
	public static final String CACHE_FILE_NEW= "01000";
	public static final String CACHE_FILE_INST_EDIT= "01000";
	public static final String CACHE_FILE_INST_DELETE= "01000";
	
	//工具栏
	public static final String TOOLBAR_FILE_NEW= "10000";
	public static final String TOOLBAR_FILE_OPEN= "01011";
	public static final String TOOLBAR_EXPLORE_REPOSITORY= "11111";
	public static final String TOOLBAR_FILE_SAVE= "11000";
	public static final String TOOLBAR_FILE_SAVE_AS= "10000";
	
	/**
	 * 转换工具栏按钮及右键菜单
	 */
	//转换工具栏按钮
	public static final String BUTTON_TRANS_RUN = "00001";
	public static final String BUTTON_TRANS_PAUSE = "00001";
	public static final String BUTTON_TRANS_STOP = "00001";
	public static final String BUTTON_TRANS_PREVIEW = "00001";
	public static final String BUTTON_TRANS_DEBUG = "00001";
	public static final String BUTTON_TRANS_REPLAY = "00001";
	public static final String BUTTON_TRANS_VERIFY = "01000";
	public static final String BUTTON_TRANS_IMPACT = "00001";
	public static final String BUTTON_TRANS_GET_SQL = "00001";
	public static final String BUTTON_TRANS_EXPLORE_DATABASE = "00001";
	public static final String BUTTON_TRANS_SHOW_RESULTS = "11111";
	//转换步骤右键菜单
	public static final String TRANS_GRAPH_ENTRY_NEWHOP = "01000";
	public static final String TRANS_GRAPH_ENTRY_OPEN_MAPPING = "01010";
	public static final String TRANS_GRAPH_ENTRY_EDIT = "01000";
	public static final String TRANS_GRAPH_ENTRY_EDIT_DESCRIPTION = "01000";
	public static final String TRANS_GRAPH_ENTRY_DATA_MOVEMENT = "01000";
	public static final String TRANS_GRAPH_ENTRY_COPIES = "01000";
	public static final String TRANS_GRAPH_ENTRY_COPY = "01000";
	public static final String TRANS_GRAPH_ENTRY_DUPLICATE = "01000";
	public static final String TRANS_GRAPH_ENTRY_DELETE = "01000";
	public static final String TRANS_GRAPH_ENTRY_HIDE = "01000";
	public static final String TRANS_GRAPH_ENTRY_DETACH = "01000";
	public static final String TRANS_GRAPH_ENTRY_INPUTS = "01000";
	public static final String TRANS_GRAPH_ENTRY_OUTPUTS = "01000";
	public static final String TRANS_GRAPH_ENTRY_SNIFF = "01000";
	public static final String TRANS_GRAPH_ENTRY_ALIGN = "01000";
	public static final String TRANS_GRAPH_ENTRY_VERIFY = "01000";
	public static final String TRANS_GRAPH_ENTRY_MAPPING = "01000";
	public static final String TRANS_GRAPH_ENTRY_PARTITIONING = "01000";
	public static final String TRANS_GRAPH_ENTRY_CLUSTERING = "01000";
	public static final String TRANS_GRAPH_ENTRY_ERRORS = "01000";
	public static final String TRANS_GRAPH_ENTRY_PREVIEW = "01000";
	//连接右键菜单
	public static final String TRANS_GRAPH_HOP_EDIT = "01000";
	public static final String TRANS_GRAPH_HOP_FLIP = "01000";
	public static final String TRANS_GRAPH_HOP_ENABLED = "01000";
	public static final String TRANS_GRAPH_HOP_DELETE = "01000";
	public static final String TRANS_GRAPH_HOP_BULK = "01000";
	//注释右键菜单
	public static final String TRANS_GRAPH_NOTE_EDIT = "01000";
	public static final String TRANS_GRAPH_NOTE_DELETE = "01000";
	public static final String TRANS_GRAPH_NOTE_RAISE = "01000";
	public static final String TRANS_GRAPH_NOTE_LOWER = "01000";
	//转换设计区右键菜单
	public static final String TRANS_GRAPH_BACKGROUND_NEW_NOTE = "01000";
	public static final String TRANS_GRAPH_BACKGROUND_PAUSE = "01000";
	public static final String TRANS_GRAPH_BACKGROUND_SELECT_ALL = "01111";
	public static final String TRANS_GRAPH_BACKGROUND_CLEAR_SELECTION = "01111";
	public static final String TRANS_GRAPH_BACKGROUND_SETTIONGS = "01000";
	
	/**
	 * 作业工具栏按钮及右键菜单
	 */
	//作业工具栏按钮
	public static final String JOB_RUN = "00001";
	public static final String JOB_STOP = "00001";
	public static final String JOB_REPLAY = "00001";
	public static final String JOB_GET_SQL = "00001";
	public static final String JOB_EXPLORE_DATABASE = "01000";
	public static final String JOB_SHOW_RESULT = "11111";
	//作业步骤右键菜单
	public static final String JOB_GRAPH_ENTRY_NEWHOP = "01000";
	public static final String JOB_GRAPH_ENTRY_LAUNTH = "01010";
	public static final String JOB_GRAPH_ENTRY_EDIT = "01000";
	public static final String JOB_GRAPH_ENTRY_EDIT_DESCRIPTION = "00001";
	public static final String JOB_GRAPH_ENTRY_PARALLEL = "01000";
	public static final String JOB_GRAPH_ENTRY_DUPLICATE = "01000";
	public static final String JOB_GRAPH_ENTRY_COPY = "01000";
	public static final String JOB_GRAPH_ENTRY_ALIGN = "01000";
	public static final String JOB_GRAPH_ENTRY_DETACH = "01000";
	public static final String JOB_GRAPH_ENTRY_HIDE = "01000";
	public static final String JOB_GRAPH_ENTRY_DELETE = "01000";
	//连接右键菜单
	public static final String JOB_GRAPH_HOP_EVALUATION = "01000";
	public static final String JOB_GRAPH_HOP_FLIP = "01000";
	public static final String JOB_GRAPH_HOP_ENABLED = "01000";
	public static final String JOB_GRAPH_HOP_DELETE = "01000";
	public static final String JOB_GRAPH_HOP_BULK = "01000";
	//注释右键菜单
	public static final String JOB_GRAPH_NOTE_EDIT = "01000";
	public static final String JOB_GRAPH_NOTE_DELETE = "01000";
	//转换设计区右键菜单
	public static final String JOB_GRAPH_BACKGROUND_NOTE_NEW = "01000";
	public static final String JOB_GRAPH_BACKGROUND_NOTE_PAUSE = "01000";
	public static final String JOB_GRAPH_BACKGROUND_SELECT_ALL = "01111";
	public static final String JOB_GRAPH_BACKGROUND_CLEAR_SELECTION = "01111";
	public static final String JOB_GRAPH_BACKGROUND_SETTIONGS = "01000";
	//浏览资源库右键菜单
	public static final String FOLDER_CONTEXT_CREATE = "10000";
	public static final String FOLDER_CONTEXT_OPEN = "01011";
	public static final String FOLDER_CONTEXT_RENAME = "01000";
	public static final String FOLDER_CONTEXT_DELETE = "00100";
	public static final String FOLDER_CONTEXT_EXPORT = "01000";
	public static final String FILE_CONTEXT_CREATE_FILESIDE = "10000";
	public static final String FILE_CONTEXT_OPEN = "01011";
	public static final String FILE_CONTEXT_RENAME = "01000";
	public static final String FILE_CONTEXT_DELETE = "00100";
	
	/**
	 * 将权限码转换成long值
	 * @param privilege
	 * @return
	 */
	public static long priviligesToLong(String privilege){
		Long val=0L;
		for(int i=0;i<privilege.length();i++){
			val=(val<<1)+privilege.charAt(i)-'0';			
		}
			return val;
	}
}
