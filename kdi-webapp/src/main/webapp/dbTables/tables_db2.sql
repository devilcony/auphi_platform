#
# Thanks to Horia Muntean for submitting this....
#
# .. known to work with DB2 7.1 and the JDBC driver "COM.ibm.db2.jdbc.net.DB2Driver"
# .. likely to work with others...
#
# In your Quartz properties file, you'll need to set 
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
#
# If you're using DB2 6.x you'll want to set this property to
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.DB2v6Delegate
#
# Note that the blob column size (e.g. blob(2000)) dictates the amount of data that can be stored in 
# that blob - i.e. limits the amount of data you can put into your JobDataMap 
#


create table qrtz_job_details (
  job_name varchar(80) not null,
  job_group varchar(80) not null,
  description varchar(120),
  job_class_name varchar(128) not null,
  is_durable varchar(1) not null,
  is_volatile varchar(1) not null,
  is_stateful varchar(1) not null,
  requests_recovery varchar(1) not null,
  job_data blob(2000),
    primary key (job_name,job_group)
);

create table qrtz_job_listeners(
  job_name varchar(80) not null,
  job_group varchar(80) not null,
  job_listener varchar(80) not null,
    primary key (job_name,job_group,job_listener),
    foreign key (job_name,job_group) references qrtz_job_details(job_name,job_group)
);

create table qrtz_triggers(
  trigger_name varchar(80) not null,
  trigger_group varchar(80) not null,
  job_name varchar(80) not null,
  job_group varchar(80) not null,
  is_volatile varchar(1) not null,
  description varchar(120),
  next_fire_time bigint,
  prev_fire_time bigint,
  trigger_state varchar(16) not null,
  trigger_type varchar(8) not null,
  start_time bigint not null,
  end_time bigint,
  calendar_name varchar(80),
  misfire_instr smallint,
  job_data blob(2000),
    primary key (trigger_name,trigger_group),
    foreign key (job_name,job_group) references qrtz_job_details(job_name,job_group)
);

create table qrtz_simple_triggers(
  trigger_name varchar(80) not null,
  trigger_group varchar(80) not null,
  repeat_count bigint not null,
  repeat_interval bigint not null,
  times_triggered bigint not null,
    primary key (trigger_name,trigger_group),
    foreign key (trigger_name,trigger_group) references qrtz_triggers(trigger_name,trigger_group)
);

create table qrtz_cron_triggers(
  trigger_name varchar(80) not null,
  trigger_group varchar(80) not null,
  cron_expression varchar(80) not null,
  time_zone_id varchar(80),
    primary key (trigger_name,trigger_group),
    foreign key (trigger_name,trigger_group) references qrtz_triggers(trigger_name,trigger_group)
);

create table qrtz_blob_triggers(
  trigger_name varchar(80) not null,
  trigger_group varchar(80) not null,
  blob_data blob(2000),
    primary key (trigger_name,trigger_group),
    foreign key (trigger_name,trigger_group) references qrtz_triggers(trigger_name,trigger_group)
);

create table qrtz_trigger_listeners(
  trigger_name varchar(80) not null,
  trigger_group varchar(80) not null,
  trigger_listener varchar(80) not null,
    primary key (trigger_name,trigger_group,trigger_listener),
    foreign key (trigger_name,trigger_group) references qrtz_triggers(trigger_name,trigger_group)
);

create table qrtz_calendars(
  calendar_name varchar(80) not null,
  calendar blob(2000) not null,
    primary key (calendar_name)
);

create table qrtz_fired_triggers(
  entry_id varchar(95) not null,
  trigger_name varchar(80) not null,
  trigger_group varchar(80) not null,
  is_volatile varchar(1) not null,
  instance_name varchar(80) not null,
  fired_time bigint not null,
  state varchar(16) not null,
  job_name varchar(80),
  job_group varchar(80),
  is_stateful varchar(1),
  requests_recovery varchar(1),
    primary key (entry_id)
);


create table qrtz_paused_trigger_grps(
  trigger_group  varchar(80) not null, 
    primary key (trigger_group)
);

create table qrtz_scheduler_state (
  instance_name varchar(80) not null,
  last_checkin_time bigint not null,
  checkin_interval bigint not null,
  recoverer varchar(80),
    primary key (instance_name)
);

create table qrtz_locks
  (
    lock_name  varchar(40) not null, 
      primary key (lock_name)
);

insert into qrtz_locks values('TRIGGER_ACCESS');
insert into qrtz_locks values('JOB_ACCESS');
insert into qrtz_locks values('CALENDAR_ACCESS');
insert into qrtz_locks values('STATE_ACCESS');
insert into qrtz_locks values('MISFIRE_ACCESS');

CREATE TABLE kdi_t_monitor(
	id int not null,
	jobname varchar(80) NOT NULL ,
	jobgroup varchar(80),
	jobfile varchar(200),
	jobstatus varchar(50),
	start_time timestamp,
	end_time timestamp,
	continued_time numeric(13,1),
	logmsg clob,
	errmsg varchar(4000),
	lines_read int,
	lines_written int,
	lines_updated int,
	lines_input int,
	lines_output int,
	lines_error int,
	lines_deleted int,
	userid varchar(20),
	id_cluster int,
	id_server int,
	id_batch int,
	primary key (id)
);

CREATE TABLE kdi_t_impactlineage (
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

CREATE TABLE kdi_t_relationship (
  JOB_ID INTEGER,
  JOB_SUB_ID INTEGER,
  TRANS_ID INTEGER
);

-- 登陆日志
CREATE TABLE kdi_t_login_log (
  c_user_id integer ,
  c_login_time timestamp 
);


-- 操作类型
CREATE TABLE kdi_t_operation (
  c_operation_id integer NOT NULL,
  c_operation_name varchar(32) default NULL,
  PRIMARY KEY  (c_operation_id)
);
INSERT INTO kdi_t_operation VALUES (1,'新建');
INSERT INTO kdi_t_operation VALUES (2,'删除');
INSERT INTO kdi_t_operation VALUES (4,'修改');
INSERT INTO kdi_t_operation VALUES (8,'执行');
INSERT INTO kdi_t_operation VALUES (16,'浏览');


-- 权限
CREATE TABLE kdi_t_priviledge (
  c_priviledge_id bigint NOT NULL,
  c_resource_type_id integer default NULL,
  c_operation_id integer default NULL,
  PRIMARY KEY  (c_priviledge_id)
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
CREATE TABLE kdi_t_resource_type (
  c_resource_type_id integer NOT NULL,
  c_resource_type_name varchar(32) default NULL,
  PRIMARY KEY  (c_resource_type_id)
);

INSERT INTO kdi_t_resource_type VALUES(1,'文件');
INSERT INTO kdi_t_resource_type VALUES(2,'目录');
INSERT INTO kdi_t_resource_type VALUES(4,'用户');
INSERT INTO kdi_t_resource_type VALUES(8,'角色');
INSERT INTO kdi_t_resource_type VALUES(16,'集群');

-- 角色
CREATE TABLE kdi_t_role (
  c_role_id integer NOT NULL,
  c_role_name varchar(32) unique not null,
  c_description varchar(255) default NULL,
  c_priviledges bigint default NULL,
  c_isSystemRole integer default NULL,
  PRIMARY KEY  (c_role_id)
) ;
INSERT INTO kdi_t_role VALUES (0,'Administrator','系统管理员角色,拥有所有的权限',-1,1);
INSERT INTO kdi_t_role VALUES (1,'Developer','系统开发者角色,对目录、文件具有新建、执行、修改、删除权限',29491,1);
INSERT INTO kdi_t_role VALUES (2,'Operator','系统执行者角色,对目录、文件具有浏览、执行权限',28672,1);
INSERT INTO kdi_t_role VALUES (3,'Guest','系统访客角色,对目录、文件具有浏览权限',24576,1);

-- 用户
CREATE TABLE kdi_t_user (
  c_user_id integer NOT NULL,
  c_user_name varchar(32) unique not null,
  c_password varchar(32) default NULL,
  c_nick_name varchar(32) default NULL,
  c_email varchar(32) default NULL,
  c_mobilephone varchar(32) default NULL,
  c_description varchar(255) default NULL,
  c_is_system_user integer default NULL,
  PRIMARY KEY  (c_user_id)
) ;
INSERT INTO kdi_t_user VALUES (0,'admin','admin','管理员','','','系统管理员用户,拥有所有资源授权和所有操作权限',1);
INSERT INTO kdi_t_user VALUES (1,'developer','developer','开发者','','','系统开发者用户,拥有Guest目录的资源授权和目录文件的增、删、改、执行、浏览权限',1);
INSERT INTO kdi_t_user VALUES (2,'operator','operator','执行者','','','系统执行者用户,拥有Guest目录的资源授权和目录文件的浏览、执行权限',1);
INSERT INTO kdi_t_user VALUES (3,'guest','guest','访客','','','系统访客用户,拥有Guest目录的资源授权和目录文件的浏览权限',1);


-- 用户角色
CREATE TABLE kdi_t_user_role (
  c_user_id integer not null,
  c_role_id integer not null,
  PRIMARY KEY  (c_user_id,c_role_id)
) ;
INSERT INTO kdi_t_user_role VALUES (0,0);
INSERT INTO kdi_t_user_role VALUES (1,1);
INSERT INTO kdi_t_user_role VALUES (2,2);
INSERT INTO kdi_t_user_role VALUES (3,3);

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