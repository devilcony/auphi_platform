-- Thanks to Patrick Lightbody for submitting this...
--
-- In your Quartz properties file, you'll need to set 
-- org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.PostreSQLDelegate

--drop table qrtz_job_listeners;
--drop table qrtz_trigger_listeners;
--drop table qrtz_fired_triggers;
--DROP TABLE QRTZ_PAUSED_TRIGGER_GRPS;
--DROP TABLE QRTZ_SCHEDULER_STATE;
--DROP TABLE QRTZ_LOCKS;
--drop table qrtz_simple_triggers;
--drop table qrtz_cron_triggers;
--DROP TABLE QRTZ_BLOB_TRIGGERS;
--drop table qrtz_triggers;
--drop table qrtz_job_details;
--drop table qrtz_calendars;

CREATE TABLE qrtz_job_details
(
    JOB_NAME  VARCHAR(80) NOT NULL,
    JOB_GROUP VARCHAR(80) NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    JOB_CLASS_NAME   VARCHAR(128) NOT NULL, 
    IS_DURABLE BOOL NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    IS_STATEFUL BOOL NOT NULL,
    REQUESTS_RECOVERY BOOL NOT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);

CREATE TABLE qrtz_job_listeners
  (
    JOB_NAME  VARCHAR(80) NOT NULL, 
    JOB_GROUP VARCHAR(80) NOT NULL,
    JOB_LISTENER VARCHAR(80) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP) 
);

CREATE TABLE qrtz_triggers
  (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    JOB_NAME  VARCHAR(80) NOT NULL, 
    JOB_GROUP VARCHAR(80) NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    DESCRIPTION VARCHAR(120) NULL,
    NEXT_FIRE_TIME BIGINT NULL,
    PREV_FIRE_TIME BIGINT NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT NOT NULL,
    END_TIME BIGINT NULL,
    CALENDAR_NAME VARCHAR(80) NULL,
    MISFIRE_INSTR SMALLINT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP) 
);

CREATE TABLE qrtz_simple_triggers
  (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    REPEAT_COUNT BIGINT NOT NULL,
    REPEAT_INTERVAL BIGINT NOT NULL,
    TIMES_TRIGGERED BIGINT NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_cron_triggers
  (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    CRON_EXPRESSION VARCHAR(80) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_blob_triggers
  (
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    BLOB_DATA BYTEA NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE qrtz_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR(80) NOT NULL, 
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    TRIGGER_LISTENER VARCHAR(80) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);


CREATE TABLE qrtz_calendars
  (
    CALENDAR_NAME  VARCHAR(80) NOT NULL, 
    CALENDAR BYTEA NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);


CREATE TABLE qrtz_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR(80) NOT NULL, 
    PRIMARY KEY (TRIGGER_GROUP)
);

CREATE TABLE qrtz_fired_triggers 
  (
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(80) NOT NULL,
    TRIGGER_GROUP VARCHAR(80) NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    FIRED_TIME BIGINT NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(80) NULL,
    JOB_GROUP VARCHAR(80) NULL,
    IS_STATEFUL BOOL NULL,
    REQUESTS_RECOVERY BOOL NULL,
    PRIMARY KEY (ENTRY_ID)
);

CREATE TABLE qrtz_scheduler_state 
  (
    INSTANCE_NAME VARCHAR(80) NOT NULL,
    LAST_CHECKIN_TIME BIGINT NOT NULL,
    CHECKIN_INTERVAL BIGINT NOT NULL,
    RECOVERER VARCHAR(80) NULL,
    PRIMARY KEY (INSTANCE_NAME)
);

CREATE TABLE qrtz_locks
  (
    LOCK_NAME  VARCHAR(40) NOT NULL, 
    PRIMARY KEY (LOCK_NAME)
);
INSERT INTO qrtz_locks values('TRIGGER_ACCESS');
INSERT INTO qrtz_locks values('JOB_ACCESS');
INSERT INTO qrtz_locks values('CALENDAR_ACCESS');
INSERT INTO qrtz_locks values('STATE_ACCESS');
INSERT INTO qrtz_locks values('MISFIRE_ACCESS');

-- 作业监控
CREATE TABLE KDI_T_MONITOR(
	ID INTEGER NOT NULL PRIMARY KEY,
	JOBNAME VARCHAR(80) NOT NULL ,
	JOBGROUP VARCHAR(80),
	JOBFILE VARCHAR(200),
	JOBSTATUS VARCHAR(50),
	START_TIME TIMESTAMP,
	END_TIME TIMESTAMP,
	CONTINUED_TIME NUMERIC(13,1),
	LOGMSG TEXT,
	ERRMSG VARCHAR(4000),
	LINES_READ INTEGER,
	LINES_WRITTEN INTEGER,
	LINES_UPDATED INTEGER,
	LINES_INPUT INTEGER,
	LINES_OUTPUT INTEGER,
	LINES_ERROR INTEGER,
	LINES_DELETED INTEGER,
	USERID VARCHAR(20),
	ID_CLUSTER INTEGER,
	ID_SLAVE INTEGER,
	ID_BATCH INTEGER
);

-- 登陆日志
CREATE TABLE  kdi_t_login_log (
  c_user_id integer ,
  c_login_time timestamp 
);

-- 操作类型
CREATE TABLE  kdi_t_operation (
  c_operation_id int NOT NULL,
  c_operation_name varchar(32) default NULL,
  PRIMARY KEY  (c_operation_id)
);
INSERT INTO  kdi_t_operation VALUES (1,'新建');
INSERT INTO  kdi_t_operation VALUES (2,'删除');
INSERT INTO  kdi_t_operation VALUES (4,'修改');
INSERT INTO  kdi_t_operation VALUES (8,'执行');
INSERT INTO  kdi_t_operation VALUES (16,'浏览');

-- 权限
CREATE TABLE  kdi_t_priviledge (
  c_priviledge_id bigint NOT NULL,
  c_resource_type_id int default NULL,
  c_operation_id int default NULL,
  PRIMARY KEY  (c_priviledge_id)
);

CREATE TABLE KDI_T_IMPACTLINEAGE (
  COLUMN_ID INTEGER,
  DATABASE_NAME VARCHAR(80),
  SCHEMA_NAME VARCHAR(80),
  TABLE_NAME VARCHAR(80),
  COLUMN_NAME VARCHAR(80),
  COLUMN_LABEL VARCHAR(80),
  COLUMN_TYPE INTEGER,
  COLUMN_PRECISION INTEGER,
  COLUMN_SCALE INTEGER,
  COLUMN_TYPE_NAME VARCHAR(80),
  COLUMN_LENGTH INTEGER,
  REPOSITORY_NAME VARCHAR(80),
  TRANS_PATH VARCHAR(80),
  TRANS_ID INTEGER,
  TRANS_NAME VARCHAR(80),
  ORIGIN_STEP_NAME VARCHAR(80),
  OPERATION VARCHAR(20)
);

CREATE TABLE KDI_T_RELATIONSHIP (
  JOB_ID INTEGER,
  JOB_SUB_ID INTEGER,
  TRANS_ID INTEGER
);

INSERT INTO kdi_t_priviledge VALUES(1,1,1);
INSERT INTO kdi_t_priviledge VALUES(2,2,1);
INSERT INTO kdi_t_priviledge VALUES(4,4,1);
INSERT INTO kdi_t_priviledge VALUES(8,8,1);
INSERT INTO kdi_t_priviledge VALUES(16,16,1);
INSERT INTO kdi_t_priviledge VALUES(32,1,2);
INSERT INTO kdi_t_priviledge VALUES(64,2,2);
INSERT INTO kdi_t_priviledge VALUES(128,4,2);
INSERT INTO kdi_t_priviledge VALUES(256,8,2);
INSERT INTO kdi_t_priviledge VALUES(512,16,2);
INSERT INTO kdi_t_priviledge VALUES(1024,1,4);
INSERT INTO kdi_t_priviledge VALUES(2048,2,4);
INSERT INTO kdi_t_priviledge VALUES(4096,4,4);
INSERT INTO kdi_t_priviledge VALUES(8192,8,4);
INSERT INTO kdi_t_priviledge VALUES(16384,16,4);
INSERT INTO kdi_t_priviledge VALUES(32768,1,8);
INSERT INTO kdi_t_priviledge VALUES(65536,1,16);
INSERT INTO kdi_t_priviledge VALUES(131072,2,16);
INSERT INTO kdi_t_priviledge VALUES(262144,4,16);
INSERT INTO kdi_t_priviledge VALUES(524288,8,16);

-- 资源库
CREATE TABLE kdi_t_repository (
  c_repository_id integer NOT NULL,
  c_repository_name varchar(32) default NULL,
  c_user_name varchar(32) default NULL,
  c_password varchar(32) default NULL,
  c_version varchar(16) default NULL,
  c_db_host varchar(32) default NULL,
  c_db_port varchar(8) default NULL,
  c_db_name varchar(64) default NULL,
  c_db_type varchar(32) default NULL,
  c_db_access varchar(16) default NULL,
  PRIMARY KEY  (c_repository_id)
);

-- 资源类型
CREATE TABLE  kdi_t_resource_type (
  c_resource_type_id int NOT NULL,
  c_resource_type_name varchar(32) default NULL,
  PRIMARY KEY  (c_resource_type_id)
);
INSERT INTO kdi_t_resource_type VALUES(1,'文件');
INSERT INTO kdi_t_resource_type VALUES(2,'目录');
INSERT INTO kdi_t_resource_type VALUES(4,'用户');
INSERT INTO kdi_t_resource_type VALUES(8,'角色');
INSERT INTO kdi_t_resource_type VALUES(16,'集群');

-- 角色
CREATE TABLE  kdi_t_role (
  c_role_id int NOT NULL,
  c_role_name varchar(32) unique,
  c_description varchar(255) default NULL,
  c_priviledges bigint default NULL,
  c_isSystemRole int default NULL,
  PRIMARY KEY  (c_role_id)
) ;
INSERT INTO  kdi_t_role VALUES (0,'Administrator','系统管理员角色,拥有所有的权限',-1,1);
INSERT INTO  kdi_t_role VALUES (1,'Developer','系统开发者角色,对目录、文件具有新建、执行、修改、删除权限',29491,1);
INSERT INTO  kdi_t_role VALUES (2,'Operator','系统执行者角色,对目录、文件具有浏览、执行权限',28672,1);
INSERT INTO  kdi_t_role VALUES (3,'Guest','系统访客角色,对目录、文件具有浏览权限',24576,1);

-- 用户
CREATE TABLE  kdi_t_user (
  c_user_id int NOT NULL,
  c_user_name varchar(32) unique,
  c_password varchar(32) default NULL,
  c_nick_name varchar(32) default NULL,
  c_email varchar(32) default NULL,
  c_mobilephone varchar(32) default NULL,
  c_description varchar(255) default NULL,
  c_is_system_user int default NULL,
  PRIMARY KEY  (c_user_id)
) ;
INSERT INTO  kdi_t_user VALUES (0,'admin','admin','管理员','','','系统管理员用户,拥有所有资源授权和所有操作权限',1);
INSERT INTO  kdi_t_user VALUES (1,'developer','developer','开发者','','','系统开发者用户,拥有Guest目录的资源授权和目录文件的增、删、改、执行、浏览权限',1);
INSERT INTO  kdi_t_user VALUES (2,'operator','operator','执行者','','','系统执行者用户,拥有Guest目录的资源授权和目录文件的浏览、执行权限',1);
INSERT INTO  kdi_t_user VALUES (3,'guest','guest','访客','','','系统访客用户,拥有Guest目录的资源授权和目录文件的浏览权限',1);


-- 用户角色
CREATE TABLE  kdi_t_user_role (
  c_user_id int ,
  c_role_id int ,
  PRIMARY KEY  (c_user_id,c_role_id)
) ;
INSERT INTO  kdi_t_user_role VALUES (0,0);
INSERT INTO  kdi_t_user_role VALUES (1,1);
INSERT INTO  kdi_t_user_role VALUES (2,2);
INSERT INTO  kdi_t_user_role VALUES (3,3);

-- HA集群表
CREATE TABLE kdi_t_ha_cluster (
  ID_CLUSTER integer NOT NULL,
  NAME varchar(255) default NULL,
  BASE_PORT varchar(255) default NULL,
  SOCKETS_BUFFER_SIZE varchar(255) default NULL,
  SOCKETS_FLUSH_INTERVAL varchar(255) default NULL,
  SOCKETS_COMPRESSED char(1) default NULL,
  DYNAMIC_CLUSTER char(1) default NULL,
  PRIMARY KEY  (ID_CLUSTER)
) ;

-- 集群与远程ETL服务器关联表
CREATE TABLE kdi_t_ha_cluster_slave (
  ID_CLUSTER_SLAVE integer NOT NULL,
  ID_CLUSTER integer default NULL,
  ID_SLAVE integer default NULL,
  PRIMARY KEY  (ID_CLUSTER_SLAVE)
) ;

-- 远程ETL服务器表
CREATE TABLE kdi_t_ha_slave (
  ID_SLAVE integer NOT NULL,
  NAME varchar(255) default NULL,
  HOST_NAME varchar(255) default NULL,
  PORT varchar(255) default NULL,
  WEB_APP_NAME varchar(255) default NULL,
  USERNAME varchar(255) default NULL,
  PASSWORD varchar(255) default NULL,
  PROXY_HOST_NAME varchar(255) default NULL,
  PROXY_PORT varchar(255) default NULL,
  NON_PROXY_HOSTS varchar(255) default NULL,
  MASTER char(1) default NULL,
  PRIMARY KEY  (ID_SLAVE)
) ;

-- 远程ETL服务器状态表
CREATE TABLE kdi_t_ha_slave_status (
  ID_STATUS integer default NULL,
  ID_SLAVE integer default NULL,
  IS_RUNING integer default NULL,
  CPU_USAGE float default NULL,
  MEMORY_USAGE float default NULL,
  RUNING_JOBS_NUM integer default NULL
) ;

-- 平台邮件服务器配置表
CREATE TABLE kdi_t_mail (
  SMTP_SERVER varchar(200) default NULL,
  SMTP_PORT integer default NULL,
  USER_NAME varchar(200) default NULL,
  PASSWD varchar(200) default NULL
) ;

-- 平台数据源配置表
CREATE TABLE kdi_t_fast_config (
  ID_CONFIG integer NOT NULL default '0',
  ID_SOURCE_TYP integer default NULL COMMENT '1 databsae, 2 ftp, 3 hadoop',
  ID_SOURCE_DATABASE integer default NULL,
  ID_SOURCE_FTP integer default NULL,
  ID_SOURCE_HADOOP integer default NULL,
  SOURCE_TABLE_NAME varchar(50) default NULL,
  SOURCE_CONDITION varchar(500) default NULL,
  SOURCE_FILE_PATH varchar(500) default NULL,
  SOURCE_FILE_NAME varchar(50) default NULL,
  SOURCE_SEPERATOR varchar(1) default NULL,
  ID_DEST_TYPE integer default NULL COMMENT '1 databsae, 2 ftp, 3 hadoop',
  ID_DEST_DATABASE integer default NULL,
  ID_DEST_FTP integer default NULL,
  ID_DEST_HADOOP integer default NULL,
  DEST_TABLE_NAME varchar(50) default NULL,
  DEST_FILE_PATH varchar(500) default NULL,
  DEST_FILE_NAME varchar(50) default NULL,
  LOAD_TYPE integer default NULL COMMENT '1 全量  2 增量',
  PRIMARY KEY  (ID_CONFIG)
);

CREATE TABLE kdi_t_fast_config_item (
  ID_CONFIG_ITEM integer NOT NULL auto_increment,
  ID_CONFIG integer NOT NULL default '0',
  SOURCE_COLUMN_NAME varchar(50) default NULL,
  SOURCE_COLUMN_TYPE varchar(500) default NULL,
  DEST_COLUMN_NAME varchar(50) default NULL,
  DEST_COLUMN_TYPE varchar(500) default NULL,
  DEST_LENGTH integer default NULL,
  IS_PRIMARY char(1) default NULL,
  IS_NULLABLE char(1) default NULL,
  START_INDEX integer default NULL,
  END_INDEX integer default NULL COMMENT '1 databsae, 2 ftp, 3 hadoop',
  PRIMARY KEY  (ID_CONFIG_ITEM)
);

CREATE TABLE kdi_t_ftp (
  ID_FTP integer default NULL,
  NAME varchar(30) default NULL,
  HOST_NAME varchar(30) default NULL,
  PORT integer default NULL,
  USERNAME varchar(20) default NULL,
  PASSWORD varchar(20) default NULL
);

CREATE TABLE kdi_t_hadoop (
  ID integer default NULL,
  SERVER varchar(50) default NULL,
  PORT integer default NULL,
  USERID varchar(50) default NULL,
  PASSWORD varchar(50) default NULL
);

CREATE TABLE kdi_t_parameter (
  ID int(11) NOT NULL auto_increment,
  "KEY" varchar(100) default NULL,
  "VALUE" varchar(500) default NULL,
  PRIMARY KEY  (ID)
);

commit;

