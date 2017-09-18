SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `KDI_T_FAST_CONFIG`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_FAST_CONFIG`;
CREATE TABLE `KDI_T_FAST_CONFIG` (
  `ID_CONFIG` int(11) NOT NULL default '0',
  `ID_SOURCE_TYPE` int(11) default NULL COMMENT '1 databsae, 2 ftp, 3 hadoop, 4 datamart',
  `ID_SOURCE_DATABASE` int(11) default NULL,
  `ID_SOURCE_FTP` int(11) default NULL,
  `ID_SOURCE_HADOOP` int(11) default NULL,
  `SOURCE_SCHEMA_NAME` varchar(50) default NULL,
  `SOURCE_TABLE_NAME` varchar(50) default NULL,
  `SOURCE_CONDITION` varchar(500) default NULL,
  `SOURCE_FILE_PATH` varchar(500) default NULL,
  `SOURCE_FILE_NAME` varchar(50) default NULL,
  `SOURCE_SEPERATOR` varchar(1) default NULL,
  `ID_DEST_TYPE` int(11) default NULL COMMENT '1 databsae, 2 ftp, 3 hadoop',
  `ID_DEST_DATABASE` int(11) default NULL,
  `ID_DEST_FTP` int(11) default NULL,
  `ID_DEST_HADOOP` int(11) default NULL,
  `DEST_SCHEMA_NAME` varchar(50) default NULL,
  `DEST_TABLE_NAME` varchar(50) default NULL,
  `DEST_FILE_PATH` varchar(500) default NULL,
  `DEST_FILE_NAME` varchar(50) default NULL,
  `LOAD_TYPE` int(11) default NULL COMMENT '1 全量  2 增量',
  PRIMARY KEY  (`ID_CONFIG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_FAST_CONFIG
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_FAST_CONFIG_ITEM`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_FAST_CONFIG_ITEM`;
CREATE TABLE `KDI_T_FAST_CONFIG_ITEM` (
  `ID_CONFIG_ITEM` int(11) NOT NULL auto_increment,
  `ID_CONFIG` int(11) NOT NULL default '0',
  `SOURCE_COLUMN_NAME` varchar(50) default NULL,
  `SOURCE_COLUMN_TYPE` varchar(500) default NULL,
  `DEST_COLUMN_NAME` varchar(50) default NULL,
  `DEST_COLUMN_TYPE` varchar(500) default NULL,
  `DEST_LENGTH` int(11) default NULL,
  `IS_PRIMARY` varchar(1) default NULL,
  `IS_NULLABLE` varchar(1) default NULL,
  `START_INDEX` int(50) default NULL,
  `END_INDEX` int(11) default NULL COMMENT '1 databsae, 2 ftp, 3 hadoop',
  PRIMARY KEY  (`ID_CONFIG_ITEM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_FAST_CONFIG_ITEM
-- ----------------------------

-- ----------------------------
-- Table structure for `kdi_t_ftp`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_FTP`;
CREATE TABLE `KDI_T_FTP` (
  `ID_FTP` int(11) default NULL,
  `NAME` varchar(30) default NULL,
  `HOST_NAME` varchar(30) default NULL,
  `PORT` int(11) default NULL,
  `USERNAME` varchar(20) default NULL,
  `PASSWORD` varchar(20) default NULL,
  `ORGANIZER_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `KDI_T_HA_CLUSTER`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_HA_CLUSTER`;
CREATE TABLE `KDI_T_HA_CLUSTER` (
  `ID_CLUSTER` bigint(20) NOT NULL,
  `NAME` varchar(255) default NULL,
  `BASE_PORT` varchar(255) default NULL,
  `SOCKETS_BUFFER_SIZE` varchar(255) default NULL,
  `SOCKETS_FLUSH_INTERVAL` varchar(255) default NULL,
  `SOCKETS_COMPRESSED` char(1) default NULL,
  `DYNAMIC_CLUSTER` char(1) default NULL,
  PRIMARY KEY  (`ID_CLUSTER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_HA_CLUSTER
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_HA_CLUSTER_SLAVE`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_HA_CLUSTER_SLAVE`;
CREATE TABLE `KDI_T_HA_CLUSTER_SLAVE` (
  `ID_CLUSTER_SLAVE` bigint(20) NOT NULL,
  `ID_CLUSTER` int(11) default NULL,
  `ID_SLAVE` int(11) default NULL,
  PRIMARY KEY  (`ID_CLUSTER_SLAVE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_HA_CLUSTER_SLAVE
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_HA_SLAVE`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_HA_SLAVE`;
CREATE TABLE `KDI_T_HA_SLAVE` (
  `ID_SLAVE` bigint(20) NOT NULL,
  `NAME` varchar(255) default NULL,
  `HOST_NAME` varchar(255) default NULL,
  `PORT` varchar(255) default NULL,
  `WEB_APP_NAME` varchar(255) default NULL,
  `USERNAME` varchar(255) default NULL,
  `PASSWORD` varchar(255) default NULL,
  `PROXY_HOST_NAME` varchar(255) default NULL,
  `PROXY_PORT` varchar(255) default NULL,
  `NON_PROXY_HOSTS` varchar(255) default NULL,
  `MASTER` char(1) default NULL,
  PRIMARY KEY  (`ID_SLAVE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of kdi_t_ha_slave
-- ----------------------------

-- ----------------------------
-- Table structure for `kdi_t_ha_slave_status`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_HA_SLAVE_STATUS`;
CREATE TABLE `KDI_T_HA_SLAVE_STATUS` (
  `ID_STATUS` int(11) default NULL,
  `ID_SLAVE` int(11) default NULL,
  `IS_RUNING` int(11) default NULL,
  `CPU_USAGE` float default NULL,
  `MEMORY_USAGE` float default NULL,
  `RUNING_JOBS_NUM` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_HA_SLAVE_STATUS
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_HADOOP`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_HADOOP`;
CREATE TABLE `KDI_T_HADOOP` (
  `ID` int(11) default NULL,
  `SERVER` varchar(50) default NULL,
  `PORT` int(11) default NULL,
  `USERID` varchar(50) default NULL,
  `PASSWORD` varchar(50) default NULL,
  `ORGANIZER_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `KDI_T_IMPACTLINEAGE`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_IMPACTLINEAGE`;
CREATE TABLE `KDI_T_IMPACTLINEAGE` (
  `COLUMN_ID` int(11) default NULL,
  `DATABASE_NAME` varchar(80) default NULL,
  `SCHEMA_NAME` varchar(80) default NULL,
  `TABLE_NAME` varchar(80) default NULL,
  `COLUMN_NAME` varchar(80) default NULL,
  `COLUMN_LABEL` varchar(80) default NULL,
  `COLUMN_TYPE` int(11) default NULL,
  `COLUMN_PRECISION` int(11) default NULL,
  `COLUMN_SCALE` int(11) default NULL,
  `COLUMN_TYPE_NAME` varchar(80) default NULL,
  `COLUMN_LENGTH` int(11) default NULL,
  `REPOSITORY_NAME` varchar(80) default NULL,
  `TRANS_PATH` varchar(80) default NULL,
  `TRANS_ID` int(11) default NULL,
  `TRANS_NAME` varchar(80) default NULL,
  `ORIGIN_STEP_NAME` varchar(80) default NULL,
  `OPERATION` varchar(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_IMPACTLINEAGE
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_INTERFACE_HOST`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_INTERFACE_HOST`;
CREATE TABLE `KDI_T_INTERFACE_HOST` (
  `HOST_ID` int(11) NOT NULL default '0',
  `IP` varchar(50) default NULL,
  `PORT` varchar(50) default NULL,
  PRIMARY KEY  (`HOST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_INTERFACE_HOST
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_LOGIN_LOG`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_LOGIN_LOG`;
CREATE TABLE `KDI_T_LOGIN_LOG` (
  `C_USER_ID` int(11) default NULL,
  `C_LOGIN_TIME` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_LOGIN_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_MAIL`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_MAIL`;
CREATE TABLE `KDI_T_MAIL` (
  `SMTP_SERVER` varchar(200) default NULL,
  `SMTP_PORT` int(11) default NULL,
  `USER_NAME` varchar(200) default NULL,
  `PASSWD` varchar(200) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into KDI_T_MAIL values('smtp.ym.163.com',25,'service@pentahochina.com','p@ssw0rd0');

-- ----------------------------
-- Records of KDI_T_MAIL
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_MONITOR`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_MONITOR`;
CREATE TABLE `KDI_T_MONITOR` (
  `ID` int(11) NOT NULL,
  `JOBNAME` varchar(80) NOT NULL,
  `JOBGROUP` varchar(80) default NULL,
  `JOBFILE` varchar(200) default NULL,
  `JOBSTATUS` varchar(50) default NULL,
  `START_TIME` timestamp NULL default NULL,
  `END_TIME` timestamp NULL default NULL,
  `CONTINUED_TIME` decimal(13,1) default NULL,
  `LOGMSG` longtext,
  `ERRMSG` varchar(4000) default NULL,
  `LINES_READ` int(11) default NULL,
  `LINES_WRITTEN` int(11) default NULL,
  `LINES_UPDATED` int(11) default NULL,
  `LINES_INPUT` int(11) default NULL,
  `LINES_OUTPUT` int(11) default NULL,
  `LINES_ERROR` int(11) default NULL,
  `LINES_DELETED` int(11) default NULL,
  `USERID` varchar(20) default NULL,
  `ID_CLUSTER` int(11) default NULL,
  `ID_SLAVE` int(11) default NULL,
  `ID_BATCH` int(11) default NULL,
  `ID_LOGCHANNEL` varchar(100) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_MONITOR
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_OPERATION`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_OPERATION`;
CREATE TABLE `KDI_T_OPERATION` (
  `C_OPERATION_ID` int(11) NOT NULL,
  `C_OPERATION_NAME` varchar(32) default NULL,
  PRIMARY KEY  (`C_OPERATION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of kdi_t_operation
-- ----------------------------
INSERT INTO `KDI_T_OPERATION` VALUES ('1', '新建');
INSERT INTO `KDI_T_OPERATION` VALUES ('2', '删除');
INSERT INTO `KDI_T_OPERATION` VALUES ('4', '修改');
INSERT INTO `KDI_T_OPERATION` VALUES ('8', '执行');
INSERT INTO `KDI_T_OPERATION` VALUES ('16', '浏览');

-- ----------------------------
-- Table structure for `KDI_T_PARAMETER`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_PARAMETER`;
CREATE TABLE `KDI_T_PARAMETER` (
  `ID` int(11) NOT NULL auto_increment,
  `KEY` varchar(100) default NULL,
  `VALUE` varchar(500) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_PARAMETER
-- ----------------------------
INSERT INTO `KDI_T_PARAMETER` VALUES ('1', 'INTERFACE_HOST_IP', '10.243.216.80');
INSERT INTO `KDI_T_PARAMETER` VALUES ('2', 'RECEIVE_FILE_PATH', '/usr/local/tomcat_bk/webapps/etl_platform/temp/receive');
INSERT INTO `KDI_T_PARAMETER` VALUES ('3', 'INTERFACE_HOST_PORT', '8099');
INSERT INTO `KDI_T_PARAMETER` VALUES ('4', 'UPLOAD_FTP_IP', '10.243.216.185');
INSERT INTO `KDI_T_PARAMETER` VALUES ('5', 'UPLOAD_FTP_PORT', '21');
INSERT INTO `KDI_T_PARAMETER` VALUES ('6', 'UPLOAD_FTP_USER', 'dmfrnt');
INSERT INTO `KDI_T_PARAMETER` VALUES ('7', 'UPLOAD_FTP_PASS', '5tgbhu876y');
INSERT INTO `KDI_T_PARAMETER` VALUES ('8', 'UPLOAD_FTP_FILE_PATH', '/st_temp/st_upload/dh');
INSERT INTO `KDI_T_PARAMETER` VALUES ('9', 'SEND_FILE_PATH', '/usr/local/tomcat_bk/webapps/etl_platform/temp/send');

-- ----------------------------
-- Table structure for `KDI_T_PRIVILEDGE`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_PRIVILEDGE`;
CREATE TABLE `KDI_T_PRIVILEDGE` (
  `C_PRIVILEDGE_ID` bigint(20) NOT NULL,
  `C_RESOURCE_TYPE_ID` int(11) default NULL,
  `C_OPERATION_ID` int(11) default NULL,
  PRIMARY KEY  (`c_priviledge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_PRIVILEDGE
-- ----------------------------
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('1', '1', '1');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('2', '2', '1');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('4', '4', '1');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('8', '8', '1');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('16', '16', '1');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('32', '1', '2');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('64', '2', '2');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('128', '4', '2');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('256', '8', '2');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('512', '16', '2');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('1024', '1', '4');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('2048', '2', '4');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('4096', '4', '4');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('8192', '8', '4');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('16384', '16', '4');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('32768', '1', '8');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('65536', '1', '16');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('131072', '2', '16');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('262144', '4', '16');
INSERT INTO `KDI_T_PRIVILEDGE` VALUES ('524288', '8', '16');

-- ----------------------------
-- Table structure for `KDI_T_RELATIONSHIP`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_RELATIONSHIP`;
CREATE TABLE `KDI_T_RELATIONSHIP` (
  `JOB_ID` int(11) default NULL,
  `JOB_SUB_ID` int(11) default NULL,
  `TRANS_ID` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_RELATIONSHIP
-- ----------------------------
INSERT INTO `KDI_T_RELATIONSHIP` VALUES ('1', '-1', '2');

-- ----------------------------
-- Table structure for `KDI_T_REPOSITORY`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_REPOSITORY`;
CREATE TABLE `KDI_T_REPOSITORY` (
  `C_REPOSITORY_ID` int(11) NOT NULL,
  `C_REPOSITORY_NAME` varchar(32) default NULL,
  `C_USER_NAME` varchar(32) default NULL,
  `C_PASSWORD` varchar(32) default NULL,
  `C_VERSION` varchar(16) default NULL,
  `C_DB_HOST` varchar(32) default NULL,
  `C_DB_PORT` varchar(8) default NULL,
  `C_DB_NAME` varchar(64) default NULL,
  `C_DB_TYPE` varchar(32) default NULL,
  `C_DB_ACCESS` varchar(16) default NULL,
  `ORGANIZER_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY  (`C_REPOSITORY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_REPOSITORY
-- ----------------------------

-- ----------------------------
-- Table structure for `KDI_T_RESOURCE_TYPE`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_RESOURCE_TYPE`;
CREATE TABLE `KDI_T_RESOURCE_TYPE` (
  `C_RESOURCE_TYPE_ID` int(11) NOT NULL,
  `C_RESOURCE_TYPE_NAME` varchar(32) default NULL,
  PRIMARY KEY  (`C_RESOURCE_TYPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_RESOURCE_TYPE
-- ----------------------------
INSERT INTO `KDI_T_RESOURCE_TYPE` VALUES ('1', '文件');
INSERT INTO `KDI_T_RESOURCE_TYPE` VALUES ('2', '目录');
INSERT INTO `KDI_T_RESOURCE_TYPE` VALUES ('4', '用户');
INSERT INTO `KDI_T_RESOURCE_TYPE` VALUES ('8', '角色');
INSERT INTO `KDI_T_RESOURCE_TYPE` VALUES ('16', '集群');

-- ----------------------------
-- Table structure for `KDI_T_ROLE`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_ROLE`;
CREATE TABLE `KDI_T_ROLE` (
  `C_ROLE_ID` int(11) NOT NULL,
  `C_ROLE_NAME` varchar(32) default NULL,
  `C_DESCRIPTION` varchar(255) default NULL,
  `C_PRIVILEDGES` bigint(20) default NULL,
  `C_ISSYSTEMROLE` int(11) default NULL,
  PRIMARY KEY  (`C_ROLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_ROLE
-- ----------------------------
INSERT INTO `KDI_T_ROLE` VALUES ('0', 'Administrator', '系统管理员角色,拥有所有的权限', '-1', '1');
INSERT INTO `KDI_T_ROLE` VALUES ('1', 'Developer', '系统开发者角色,对目录、文件具有新建、执行、修改、删除权限', '29491', '1');
INSERT INTO `KDI_T_ROLE` VALUES ('2', 'Operator', '系统执行者角色,对目录、文件具有浏览、执行权限', '28672', '1');
INSERT INTO `KDI_T_ROLE` VALUES ('3', 'Guest', '系统访客角色,对目录、文件具有浏览权限', '24576', '1');

-- ----------------------------
-- Table structure for `KDI_T_USER`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_USER`;
CREATE TABLE `KDI_T_USER` (
  `C_USER_ID` int(11) NOT NULL,
  `C_USER_NAME` varchar(32) default NULL,
  `C_PASSWORD` varchar(32) default NULL,
  `C_NICK_NAME` varchar(32) default NULL,
  `C_EMAIL` varchar(32) default NULL,
  `C_MOBILEPHONE` varchar(32) default NULL,
  `C_DESCRIPTION` varchar(255) default NULL,
  `C_IS_SYSTEM_USER` int(11) default NULL,
  `ORGANIZER_ID` bigint(20) DEFAULT NULL,
  `C_USER_STATUS` int(11) DEFAULT NULL,
  PRIMARY KEY  (`C_USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_USER
-- ----------------------------
INSERT INTO `KDI_T_USER` VALUES ('0', 'admin', 'admin', '管理员', '', '', '系统管理员用户,拥有所有资源授权和所有操作权限', '1',1,1);

-- ----------------------------
-- Table structure for `kdi_t_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `KDI_T_USER_ROLE`;
CREATE TABLE `KDI_T_USER_ROLE` (
  `C_USER_ID` int(11) NOT NULL default '0',
  `C_ROLE_ID` int(11) NOT NULL default '0',
  PRIMARY KEY  (`C_USER_ID`,`C_ROLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of KDI_T_USER_ROLE
-- ----------------------------
INSERT INTO `KDI_T_USER_ROLE` VALUES ('0', '0');

-- ----------------------------
-- Table structure for `LOG_CHANNEL`
-- ----------------------------
DROP TABLE IF EXISTS `LOG_CHANNEL`;
CREATE TABLE `LOG_CHANNEL` (
  `ID_BATCH` int(11) default NULL,
  `CHANNEL_ID` varchar(255) default NULL,
  `LOG_DATE` datetime default NULL,
  `LOGGING_OBJECT_TYPE` varchar(255) default NULL,
  `OBJECT_NAME` varchar(255) default NULL,
  `OBJECT_COPY` varchar(255) default NULL,
  `REPOSITORY_DIRECTORY` varchar(255) default NULL,
  `FILENAME` varchar(255) default NULL,
  `OBJECT_ID` varchar(255) default NULL,
  `OBJECT_REVISION` varchar(255) default NULL,
  `PARENT_CHANNEL_ID` varchar(255) default NULL,
  `ROOT_CHANNEL_ID` varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_CHANNEL
-- ----------------------------

-- ----------------------------
-- Table structure for `LOG_JOB`
-- ----------------------------
DROP TABLE IF EXISTS `LOG_JOB`;
CREATE TABLE `LOG_JOB` (
  `ID_JOB` int(11) default NULL,
  `CHANNEL_ID` varchar(255) default NULL,
  `JOBNAME` varchar(255) default NULL,
  `STATUS` varchar(15) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `STARTDATE` datetime default NULL,
  `ENDDATE` datetime default NULL,
  `LOGDATE` datetime default NULL,
  `DEPDATE` datetime default NULL,
  `REPLAYDATE` datetime default NULL,
  `LOG_FIELD` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_JOB
-- ----------------------------

-- ----------------------------
-- Table structure for `LOG_JOBENTRY`
-- ----------------------------
DROP TABLE IF EXISTS `LOG_JOBENTRY`;
CREATE TABLE `LOG_JOBENTRY` (
  `ID_BATCH` int(11) default NULL,
  `CHANNEL_ID` varchar(255) default NULL,
  `LOG_DATE` datetime default NULL,
  `TRANSNAME` varchar(255) default NULL,
  `STEPNAME` varchar(255) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `RESULT` char(1) default NULL,
  `NR_RESULT_ROWS` bigint(20) default NULL,
  `NR_RESULT_FILES` bigint(20) default NULL,
  `LOG_FIELD` mediumtext,
  `COPY_NR` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_JOBENTRY
-- ----------------------------

-- ----------------------------
-- Table structure for `LOG_STEP`
-- ----------------------------
DROP TABLE IF EXISTS `LOG_STEP`;
CREATE TABLE `LOG_STEP` (
  `ID_BATCH` int(11) default NULL,
  `CHANNEL_ID` varchar(255) default NULL,
  `LOG_DATE` datetime default NULL,
  `TRANSNAME` varchar(255) default NULL,
  `STEPNAME` varchar(255) default NULL,
  `STEP_COPY` int(11) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `LOG_FIELD` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_STEP
-- ----------------------------

-- ----------------------------
-- Table structure for `LOG_TRANS`
-- ----------------------------
DROP TABLE IF EXISTS `LOG_TRANS`;
CREATE TABLE `LOG_TRANS` (
  `ID_BATCH` int(11) default NULL,
  `CHANNEL_ID` varchar(255) default NULL,
  `TRANSNAME` varchar(255) default NULL,
  `STATUS` varchar(15) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `STARTDATE` datetime default NULL,
  `ENDDATE` datetime default NULL,
  `LOGDATE` datetime default NULL,
  `DEPDATE` datetime default NULL,
  `REPLAYDATE` datetime default NULL,
  `LOG_FIELD` mediumtext,
  KEY `IDX_log_trans_1` (`ID_BATCH`),
  KEY `IDX_log_trans_2` (`ERRORS`,`STATUS`,`TRANSNAME`),
  KEY `IDX_log_trans_3` (`ID_BATCH`),
  KEY `IDX_log_trans_4` (`ERRORS`,`STATUS`,`TRANSNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of LOG_TRANS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_BLOB_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_BLOB_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_CALENDARS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS` (
  `CALENDAR_NAME` varchar(80) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY  (`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_CALENDARS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_CRON_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `CRON_EXPRESSION` varchar(80) NOT NULL,
  `TIME_ZONE_ID` varchar(80) default NULL,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_CRON_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_FIRED_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `INSTANCE_NAME` varchar(80) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(80) default NULL,
  `JOB_GROUP` varchar(80) default NULL,
  `IS_STATEFUL` varchar(1) default NULL,
  `REQUESTS_RECOVERY` varchar(1) default NULL,
  PRIMARY KEY  (`ENTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_FIRED_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_JOB_DETAILS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `JOB_NAME` varchar(80) NOT NULL,
  `JOB_GROUP` varchar(80) NOT NULL,
  `DESCRIPTION` varchar(120) default NULL,
  `JOB_CLASS_NAME` varchar(128) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `IS_STATEFUL` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` longblob,
  PRIMARY KEY  (`JOB_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_JOB_DETAILS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_JOB_LISTENERS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_LISTENERS`;
CREATE TABLE `QRTZ_JOB_LISTENERS` (
  `JOB_NAME` varchar(80) NOT NULL,
  `JOB_GROUP` varchar(80) NOT NULL,
  `JOB_LISTENER` varchar(80) NOT NULL,
  PRIMARY KEY  (`JOB_NAME`,`JOB_GROUP`,`JOB_LISTENER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_JOB_LISTENERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_LOCKS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS` (
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY  (`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_LOCKS
-- ----------------------------
INSERT INTO `QRTZ_LOCKS` VALUES ('CALENDAR_ACCESS');
INSERT INTO `QRTZ_LOCKS` VALUES ('JOB_ACCESS');
INSERT INTO `QRTZ_LOCKS` VALUES ('MISFIRE_ACCESS');
INSERT INTO `QRTZ_LOCKS` VALUES ('STATE_ACCESS');
INSERT INTO `QRTZ_LOCKS` VALUES ('TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for `QRTZ_PAUSED_TRIGGER_GRPS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  PRIMARY KEY  (`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_SCHEDULER_STATE`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `INSTANCE_NAME` varchar(80) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  `RECOVERER` varchar(80) default NULL,
  PRIMARY KEY  (`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_SCHEDULER_STATE
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_SIMPLE_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(7) NOT NULL,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_SIMPLE_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_TRIGGER_LISTENERS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGER_LISTENERS`;
CREATE TABLE `QRTZ_TRIGGER_LISTENERS` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `TRIGGER_LISTENER` varchar(80) NOT NULL,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_LISTENER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_TRIGGER_LISTENERS
-- ----------------------------

-- ----------------------------
-- Table structure for `QRTZ_TRIGGERS`
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS` (
  `TRIGGER_NAME` varchar(80) NOT NULL,
  `TRIGGER_GROUP` varchar(80) NOT NULL,
  `JOB_NAME` varchar(80) NOT NULL,
  `JOB_GROUP` varchar(80) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `DESCRIPTION` varchar(120) default NULL,
  `NEXT_FIRE_TIME` bigint(13) default NULL,
  `PREV_FIRE_TIME` bigint(13) default NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) default NULL,
  `CALENDAR_NAME` varchar(80) default NULL,
  `MISFIRE_INSTR` smallint(2) default NULL,
  `JOB_DATA` blob,
  PRIMARY KEY  (`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for `T_DATA_EXPORT_CONFIG`
-- ----------------------------
DROP TABLE IF EXISTS `T_DATA_EXPORT_CONFIG`;
CREATE TABLE `T_DATA_EXPORT_CONFIG` (
  `CONFIG_ID` int(11) NOT NULL auto_increment,
  `TASK_NAME` varchar(100) NOT NULL,
  `TABLE_NAME` varchar(100) NOT NULL,
  `FIELDS` varchar(1000) default NULL COMMENT '字段之间用逗号隔开，默认查询全部字段',
  `RESULT_SEP` varchar(1) NOT NULL,
  `CONDITIONS` varchar(200) default NULL,
  `IS_INCREMENT` tinyint(4) default '0',
  `CREATETIME` datetime default NULL,
  `INCREMENTFIELD` varchar(20) default NULL,
  `TASKTYPE` tinyint(4) default NULL,
  `STARTTIME` datetime default NULL,
  PRIMARY KEY  (`CONFIG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_DATA_EXPORT_CONFIG
-- ----------------------------

-- ----------------------------
-- Table structure for `T_DATASOURCE`
-- ----------------------------
DROP TABLE IF EXISTS `T_DATASOURCE`;
CREATE TABLE `T_DATASOURCE` (
  `SOURCEID` int(11) NOT NULL auto_increment,
  `SOURCENAME` varchar(50) NOT NULL,
  `SOURCEIP` varchar(50) NOT NULL,
  `SOURCEUSERNAME` varchar(50) NOT NULL,
  `SOURCEPASSWORD` varchar(100) NOT NULL,
  `SOURCETYPE` int(11) NOT NULL,
  `SOURCEDATABASENAME` varchar(50) NOT NULL,
  `SOURCEPORT` varchar(10) NOT NULL,
  PRIMARY KEY  (`SOURCEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_DATASOURCE
-- ----------------------------

-- ----------------------------
-- Table structure for `T_EXPORT_METADATA`
-- ----------------------------
DROP TABLE IF EXISTS `T_EXPORT_METADATA`;
CREATE TABLE `T_EXPORT_METADATA` (
  `TABLE_ID` int(11) NOT NULL auto_increment,
  `TABLENAME` varchar(50) default NULL,
  `TABLEDESC` varchar(200) default NULL,
  PRIMARY KEY  (`TABLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_EXPORT_METADATA
-- ----------------------------

-- ----------------------------
-- Table structure for `T_EXPORT_MONITOR`
-- ----------------------------
DROP TABLE IF EXISTS `T_EXPORT_MONITOR`;
CREATE TABLE `T_EXPORT_MONITOR` (
  `MONITOR_ID` int(11) NOT NULL auto_increment,
  `CONFIG_ID` int(11) default NULL,
  `STARTTIME` datetime default NULL,
  `EXPORT_COUNT` bigint(20) default NULL,
  `EXPORT_BITE_SIZE` bigint(20) default NULL,
  `END_TIME` datetime default NULL,
  `STATUS` varchar(10) default NULL,
  `DATA_PATH` varchar(255) default NULL,
  PRIMARY KEY  (`MONITOR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_EXPORT_MONITOR
-- ----------------------------

-- ----------------------------
-- Table structure for `T_INCREMENT_INFO`
-- ----------------------------
DROP TABLE IF EXISTS `T_INCREMENT_INFO`;
CREATE TABLE `T_INCREMENT_INFO` (
  `INCREMENT_ID` int(11) NOT NULL auto_increment,
  `CONFIG_ID` int(11) default NULL,
  `ROWNUM` varchar(10) default NULL,
  `LAST_DATE` datetime default NULL,
  PRIMARY KEY  (`INCREMENT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_INCREMENT_INFO
-- ----------------------------

-- ----------------------------
-- Table structure for `T_INTERFACE_USER`
-- ----------------------------
DROP TABLE IF EXISTS `T_INTERFACE_USER`;
CREATE TABLE `T_INTERFACE_USER` (
  `USER_ID` int(11) NOT NULL auto_increment,
  `USERNAME` varchar(20) default NULL,
  `PASSWORD` varchar(100) default NULL,
  `SYSTEM_NAME` varchar(50) default NULL,
  `SYSTEM_IP` varchar(50) default NULL,
  `SYSTEM_DESC` varchar(400) default NULL,
  PRIMARY KEY  (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_INTERFACE_USER
-- ----------------------------

-- ----------------------------
-- Table structure for `T_JOB_LOG`
-- ----------------------------
DROP TABLE IF EXISTS `T_JOB_LOG`;
CREATE TABLE `T_JOB_LOG` (
  `JOB_ID` int(11) NOT NULL auto_increment,
  `JOB_CONFIG_ID` int(11) default NULL COMMENT '对应r_job表中的ID_JOB主键',
  `CHANNEL_ID` varchar(255) default NULL COMMENT '唯一，通UUID表示\r\n            ',
  `JOB_NAME` varchar(255) default NULL,
  `JOB_CN_NAME` varchar(255) default NULL,
  `STATUS` varchar(50) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `STARTDATE` datetime default NULL,
  `ENDDATE` datetime default NULL,
  `LOGDATE` datetime default NULL,
  `DEPDATE` datetime default NULL,
  `REPLAYDATE` datetime default NULL,
  `LOG_FIELD` mediumtext,
  `EXECUTING_SERVER` varchar(255) default NULL,
  `EXECUTING_USER` varchar(255) default NULL,
  `EXCUTOR_TYPE` tinyint(4) default NULL COMMENT '1:表示本地，2表示远程，3表示集群',
  `JOB_LOG` text,
  PRIMARY KEY  (`JOB_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_JOB_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for `T_JOB_STEP_LOG`
-- ----------------------------
DROP TABLE IF EXISTS `T_JOB_STEP_LOG`;
CREATE TABLE `T_JOB_STEP_LOG` (
  `JOB_STEP_ID` int(11) NOT NULL auto_increment,
  `JOB_ID` int(11) default NULL,
  `CHANNEL_ID` varchar(50) NOT NULL,
  `LOG_DATE` datetime default NULL,
  `JOBNAME` varchar(100) default NULL COMMENT '父JOB的名称',
  `STEPNAME` varchar(100) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `RESULT` bigint(20) default NULL,
  `NR_RESULT_ROWS` tinyint(4) default NULL,
  `NR_RESULT_FILES` bigint(20) default NULL,
  `LOG_FIELD` bigint(20) default NULL COMMENT '日志字段为这个特定的工作条目包含错误日志日志LOG_FIELD',
  `COPY_NR` bigint(20) default NULL,
  `SETP_LOG` text,
  PRIMARY KEY  (`JOB_STEP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_JOB_STEP_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for `T_QUARTZ_TASK`
-- ----------------------------
DROP TABLE IF EXISTS `T_QUARTZ_TASK`;
CREATE TABLE `T_QUARTZ_TASK` (
  `TASK_ID` int(11) NOT NULL auto_increment,
  `TASK_CN_NAME` varchar(255) NOT NULL,
  `TRIGGER_NAME` varchar(255) NOT NULL,
  `TASK_TYPE` int(11) NOT NULL,
  `TASK_CONFIG_ID` varchar(255) NOT NULL,
  `TASK_NAME` varchar(255) NOT NULL,
  `TASK_SLAVE_SERVER` varchar(255) default NULL,
  `TASK_PARAMS` varchar(500) default NULL,
  `RUNTYPE` int(11) default NULL,
  `FTPPATH` varchar(255) default NULL,
  `SCHEDULETYPE` int(11) default NULL,
  PRIMARY KEY  (`TASK_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_QUARTZ_TASK
-- ----------------------------

-- ----------------------------
-- Table structure for `T_SERVICE_AUTH`
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE_AUTH`;
CREATE TABLE `T_SERVICE_AUTH` (
  `AUTH_ID` int(11) NOT NULL auto_increment,
  `USER_ID` int(11) default NULL,
  `SERVICE_ID` int(11) default NULL,
  `AUTH_IP` varchar(50) default NULL,
  `USE_DESC` varchar(200) default NULL,
  `USE_DEPT` varchar(50) default NULL,
  `USER_NAME` varchar(50) default NULL,
  PRIMARY KEY  (`AUTH_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_SERVICE_AUTH
-- ----------------------------

-- ----------------------------
-- Table structure for `T_SERVICE_INTERFACE`
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE_INTERFACE`;
CREATE TABLE `T_SERVICE_INTERFACE` (
  `SERVICE_ID` int(11) NOT NULL auto_increment,
  `SERVICE_NAME` varchar(100) default NULL,
  `SERVICE_IDENTIFY` varchar(100) default NULL COMMENT 'Client调用时唯一识别的标示',
  `SERVICE_URL` varchar(200) default NULL,
  `JOB_TYPE` int(11) default NULL COMMENT '1表示job，2表示trans，3表示自定义',
  `TRANS_NAME` varchar(20) default NULL,
  `RETURN_TYPE` int(11) default NULL COMMENT '用户可以自己选的，只支持FTP和Webservice\r\n            1表示FTP，2表示Webservice\r\n            ',
  `DATASOURCE` varchar(100) default NULL,
  `TIMEOUT` int(11) default NULL COMMENT '服务接口生成的结果数据超时时间，超过这个时间就要删除数据，单位分钟',
  `IS_COMPRESS` int(11) default NULL COMMENT '1表示压缩，0表示不压缩',
  `TABLENAME` varchar(100) default NULL,
  `DELIMITER` varchar(5) default NULL,
  `FIELDS` varchar(200) default NULL,
  `CONDITIONS` varchar(200) default NULL,
  `CREATEDATE` datetime default NULL,
  `INTERFACE_DESC` varchar(1000) default NULL,
  `ID_DATABASE` int(11) default NULL,
  `job_Config_Id` int(11) default NULL,
  PRIMARY KEY  (`SERVICE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_SERVICE_INTERFACE
-- ----------------------------

-- ----------------------------
-- Table structure for `T_SERVICE_JOB_LOG`
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE_JOB_LOG`;
CREATE TABLE `T_SERVICE_JOB_LOG` (
  `ID_JOB` int(11) NOT NULL auto_increment,
  `MONITOR_ID` int(11) default NULL,
  `JOB_CONFIG_ID` int(11) default NULL COMMENT '对应r_job表中的ID_JOB主键',
  `CHANNEL_ID` varchar(255) default NULL COMMENT '唯一，通UUID表示\r\n            ',
  `JOBName` varchar(255) default NULL,
  `JOB_CN_NAME` varchar(255) default NULL,
  `STATUS` varchar(100) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `STARTDATE` datetime default NULL,
  `ENDDATE` datetime default NULL,
  `LOGDATE` datetime default NULL,
  `DEPDATE` datetime default NULL,
  `REPLAYDATE` datetime default NULL,
  `LOG_FIELD` mediumtext,
  `EXECUTING_SERVER` varchar(255) default NULL,
  `EXECUTING_USER` varchar(255) default NULL,
  `EXCUTOR_TYPE` tinyint(4) default NULL COMMENT '1:表示本地，2表示远程，3表示集群',
  `JOB_LOG` text,
  PRIMARY KEY  (`ID_JOB`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_SERVICE_JOB_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for `T_SERVICE_MONITOR`
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE_MONITOR`;
CREATE TABLE `T_SERVICE_MONITOR` (
  `MONITOR_ID` int(11) NOT NULL auto_increment,
  `SERVICE_ID` int(11) default NULL,
  `START_TIME` datetime default NULL,
  `END_TIME` datetime default NULL,
  `STATUS` varchar(20) default NULL,
  `userName` varchar(50) default NULL,
  `systemName` varchar(100) default NULL,
  PRIMARY KEY  (`MONITOR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_SERVICE_MONITOR
-- ----------------------------

-- ----------------------------
-- Table structure for `T_SERVICE_MONITOR_STEP_INFO`
-- ----------------------------
DROP TABLE IF EXISTS `T_SERVICE_MONITOR_STEP_INFO`;
CREATE TABLE `T_SERVICE_MONITOR_STEP_INFO` (
  `STEP_ID` int(11) NOT NULL auto_increment,
  `MONITOR_ID` int(11) default NULL,
  `STEPNAME` varchar(50) default NULL,
  `READRECORDCOUNT` varchar(10) default NULL,
  `RETURNRECORDCOUNT` bit(20) default NULL,
  `STARTDATE` datetime default NULL,
  `ENDDATE` datetime default NULL,
  `COSTTIME` int(11) default NULL,
  `STATUS` int(11) default NULL,
  `LOGINFO` varchar(500) default NULL,
  PRIMARY KEY  (`STEP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_SERVICE_MONITOR_STEP_INFO
-- ----------------------------

-- ----------------------------
-- Table structure for `T_SYSTEM`
-- ----------------------------
DROP TABLE IF EXISTS `T_SYSTEM`;
CREATE TABLE `T_SYSTEM` (
  `SYSTEM_ID` int(11) NOT NULL auto_increment,
  `SYSTEM_NAME` varchar(50) default NULL,
  `SYSTEM_IP` varchar(50) default NULL,
  `SYSTEM_DESC` varchar(400) default NULL,
  PRIMARY KEY  (`SYSTEM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_SYSTEM
-- ----------------------------

-- ----------------------------
-- Table structure for `T_TABLE_STRUCTURE`
-- ----------------------------
DROP TABLE IF EXISTS `T_TABLE_STRUCTURE`;
CREATE TABLE `T_TABLE_STRUCTURE` (
  `ID` int(11) NOT NULL auto_increment,
  `table_id` int(11) default NULL,
  `COLUMN_NAME` varchar(50) default NULL,
  `DATA_TYPE` varchar(50) default NULL,
  `COMMENTS` varchar(200) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_TABLE_STRUCTURE
-- ----------------------------

-- ----------------------------
-- Table structure for `T_TRANS_LOG`
-- ----------------------------
DROP TABLE IF EXISTS `T_TRANS_LOG`;
CREATE TABLE `T_TRANS_LOG` (
  `TRANS_ID` int(11) NOT NULL auto_increment,
  `TRANS_CONFIG_ID` varchar(10) NOT NULL COMMENT '对应转换表 R_TRANSFORMATION 中的ID_TRANSFORMATION 字段',
  `CHANNEL_ID` varchar(255) NOT NULL,
  `TRANSNAME` varchar(255) NOT NULL,
  `STATUS` varchar(20) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `STARTDATE` datetime default NULL,
  `ENDDATE` datetime default NULL,
  `LOGDATE` datetime default NULL,
  `DEPDATE` datetime default NULL,
  `REPLAYDATE` datetime default NULL,
  `LOG_FIELD` mediumtext,
  `EXECUTING_SERVER` varchar(255) default NULL,
  `EXECUTING_USER` varchar(255) default NULL,
  `EXCUTOR_TYPE` tinyint(4) default NULL COMMENT '1:表示本地，2表示远程，3表示集群',
  `LOGINFO` text,
  `TRANS_CN_NAME` varchar(255) NOT NULL,
  PRIMARY KEY  (`TRANS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_TRANS_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for `T_TRANS_STEP_LOG`
-- ----------------------------
DROP TABLE IF EXISTS `T_TRANS_STEP_LOG`;
CREATE TABLE `T_TRANS_STEP_LOG` (
  `TRANS_ID` int(11) default NULL,
  `STEP_ID` int(11) NOT NULL auto_increment,
  `CHANNEL_ID` varchar(50) NOT NULL,
  `LOG_DATE` datetime default NULL,
  `TRANSNAME` varchar(100) default NULL,
  `STEPNAME` varchar(100) default NULL,
  `STEP_COPY` int(11) default NULL,
  `LINES_READ` bigint(20) default NULL,
  `LINES_WRITTEN` bigint(20) default NULL,
  `LINES_UPDATED` bigint(20) default NULL,
  `LINES_INPUT` bigint(20) default NULL,
  `LINES_OUTPUT` bigint(20) default NULL,
  `LINES_REJECTED` bigint(20) default NULL,
  `ERRORS` bigint(20) default NULL,
  `SETP_LOG` text,
  `COSTTIME` varchar(10) default NULL,
  `SPEED` varchar(50) default NULL,
  `STATUS` varchar(50) default NULL,
  PRIMARY KEY  (`STEP_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of T_TRANS_STEP_LOG
-- ----------------------------

-- ----------------------------
-- Table structure for `TEMP_KDI_T_PARAMETER`
-- ----------------------------
DROP TABLE IF EXISTS `TEMP_KDI_T_PARAMETER`;
CREATE TABLE `TEMP_KDI_T_PARAMETER` (
  `ID` int(11) NOT NULL default '0',
  `KEY` varchar(100) character set utf8 default NULL,
  `VALUE` varchar(500) character set utf8 default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of TEMP_KDI_T_PARAMETER
-- ----------------------------
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('1', 'INTERFACE_HOST_IP', '10.243.216.80');
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('2', 'RECEIVE_FILE_PATH', '/temp');
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('3', 'INTERFACE_HOST_PORT', '8099');
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('4', 'UPLOAD_FTP_IP', '10.243.216.185');
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('5', 'UPLOAD_FTP_PORT', '21');
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('6', 'UPLOAD_FTP_USER', 'dmfrnt');
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('7', 'UPLOAD_FTP_PASS', '5tgbhu876y');
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('8', 'UPLOAD_FTP_FILE_PATH', '/st_temp/st_upload/dh');
INSERT INTO `TEMP_KDI_T_PARAMETER` VALUES ('9', 'SEND_FILE_PATH', '/temp');

-- ----------------------------
-- Table structure for `XDUAL`
-- ----------------------------
DROP TABLE IF EXISTS `XDUAL`;
CREATE TABLE `XDUAL` (
  `ID` int(11) default NULL,
  `GMT` datetime default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of XDUAL
-- ----------------------------
INSERT INTO `XDUAL` VALUES ('1', '2015-03-13 21:04:44');

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `eaparam`
-- ----------------------------
DROP TABLE IF EXISTS `EAPARAM`;
CREATE TABLE `EAPARAM` (
  `PARAMID` varchar(8) NOT NULL COMMENT '参数编号',
  `PARAMKEY` varchar(50) NOT NULL COMMENT '参数键名',
  `PARAMVALUE` varchar(100) NOT NULL COMMENT '参数键值',
  `REMARK` varchar(200) default NULL COMMENT '备注',
  PRIMARY KEY  (`PARAMID`),
  UNIQUE KEY `UK_EAPARAM` (`PARAMKEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='全局参数表';

-- ----------------------------
-- Records of eaparam
-- ----------------------------
INSERT INTO `EAPARAM` VALUES ('10000008', 'SYS_DEFAULT_THEME', 'default', '系统默认主题皮肤');
INSERT INTO `EAPARAM` VALUES ('10000009', 'DEFAULT_ADMIN_ACCOUNT', 'super', '默认超级管理员帐户');
INSERT INTO `EAPARAM` VALUES ('10000010', 'DEFAULT_DEVELOP_ACCOUNT', 'developer', '系统默认开发专用帐户');
INSERT INTO `EAPARAM` VALUES ('10000017', 'SYS_TITLE', '傲飞数据整合平台', '系统标题');
INSERT INTO `EAPARAM` VALUES ('10000018', 'LOGIN_WINDOW_TITLE', '傲飞数据整合平台', '登录窗口标题');
INSERT INTO `EAPARAM` VALUES ('10000019', 'LOGIN_WINDOW_BANNER', '/resource/image/login_banner.png', '登录窗口的Banner图片。尺寸规格:450 X 70');
INSERT INTO `EAPARAM` VALUES ('10000020', 'WEST_NAVIGATE_TITLE', '系统导航', '左边菜单导航栏标题');
INSERT INTO `EAPARAM` VALUES ('10000021', 'BOTTOM_COPYRIGHT', 'Copyright&copy 2012-2015 傲飞商智 中国.北京', '右下角的版权信息');
INSERT INTO `EAPARAM` VALUES ('10000022', 'MENU_FIRST', '傲飞数据整合平台', '中心面板导航菜单的第一个节点名');
INSERT INTO `EAPARAM` VALUES ('10000023', 'WELCOME_PAGE_TITLE', '我的工作台', '缺省欢迎页面的标题');
INSERT INTO `EAPARAM` VALUES ('10000024', 'INDEX_BANNER', '/resource/image/skyform_Title_2.png', '首页Banner图片,必须为透明的PNG图片,建议尺寸:600 X 58');
INSERT INTO `EAPARAM` VALUES ('10000025', 'PAGE_LOAD_MSG', '模板引擎组件正在驱动页面,请稍等...', '页面加载等待提示信息');
INSERT INTO `EAPARAM` VALUES ('10000027', 'MULTI_SESSION', '1', '是否允许同一个帐户建立多个会话连接。1:允许;0:不允许');
INSERT INTO `EAPARAM` VALUES ('10000028', 'WEST_CARDMENU_ACTIVEONTOP', '1', '左侧卡片树菜单当前活动卡片是否置顶1:置顶;0:不置顶');
INSERT INTO `EAPARAM` VALUES ('10000029', 'TITLE_ICON', 'kettle.ico', '显示在浏览器标题前面的小图标(注:必须为.ico格式)');

DROP TABLE IF EXISTS `KDI_T_JOB_DEPENDENCIES`;
create table `KDI_T_JOB_DEPENDENCIES`(
`ID`             int(11)  not null AUTO_INCREMENT  
,`JOB_NAME`       varchar(80)
,`JOB_GROUP`      varchar(80)
,`JOB_FULLNAME`   varchar(160)
,`DJOB_FULLNAME`  varchar(160),
PRIMARY KEY (ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `kdi_t_organizer`;
CREATE TABLE `kdi_t_organizer` (
  `organizer_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `organizer_name` varchar(500) DEFAULT NULL,
  `organizer_contact` varchar(255) DEFAULT NULL,
  `organizer_email` varchar(255) DEFAULT NULL,
  `organizer_telphone` varchar(255) DEFAULT NULL,
  `organizer_mobile` varchar(255) DEFAULT NULL,
  `organizer_address` varchar(500) DEFAULT NULL,
  `organizer_verify_code` varchar(255) DEFAULT NULL,
  `organizer_status` int(11) DEFAULT '0' COMMENT '0 已注册未验证通过，1已注册并验证通过， 2 注销',
  PRIMARY KEY (`organizer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into kdi_t_organizer values(1,'Default','admin','','','','','',1);

DROP TABLE IF EXISTS `MDM_MODEL`;
CREATE TABLE `MDM_MODEL` (
  `ID_MODEL` int(11) NOT NULL DEFAULT '0',
  `MODEL_NAME` varchar(50) DEFAULT NULL,
  `MODEL_DESC` varchar(300) DEFAULT NULL,
  `MODEL_STATUS` varchar(1) DEFAULT NULL,
  `MODEL_AUTHOR` varchar(50) DEFAULT NULL,
  `MODEL_NOTE` varchar(500) DEFAULT NULL,
  `MODEL_CODE` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID_MODEL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `MDM_TABLE`;
CREATE TABLE `MDM_TABLE` (
  `ID_TABLE` int(11) NOT NULL DEFAULT '0',
  `ID_MODEL` int(11) DEFAULT NULL COMMENT 'ID in model table',
  `ID_DATABASE` int(11) DEFAULT NULL COMMENT 'ID in r_databsae table',
  `SCHEMA_NAME` varchar(100) DEFAULT NULL,
  `TABLE_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID_TABLE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `MDM_DATA_CLEAN`;
CREATE TABLE `MDM_DATA_CLEAN` (
  `ID` int(11) NOT NULL,
  `ID_MODEL` int(11) DEFAULT NULL,
  `ATTRIBUTE_MODEL` varchar(50) DEFAULT NULL,
  `MDM_ID_DATABASE` int(11) DEFAULT NULL,
  `MDM_SCHEMA_NAME` varchar(50) DEFAULT NULL,
  `MDM_TABLE_NAME` varchar(50) DEFAULT NULL,
  `MDM_PRIMARY_KEY` varchar(50) DEFAULT NULL,
  `MDM_COLUMN_NAME` varchar(50) DEFAULT NULL,
  `MDM_WHERE_CONDITION` varchar(255) DEFAULT NULL,
  `MAPING_MODE` int(11) DEFAULT NULL,
  `MAPING_ID_DATABASE` int(11) DEFAULT NULL,
  `MAPING_SCHEMA_NAME` varchar(50) DEFAULT NULL,
  `MAPING_TABLE_NAME` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `MDM_MODEL_ATTRIBUTE`;
CREATE TABLE `MDM_MODEL_ATTRIBUTE` (
  `ID_ATTRIBUTE` int(11) NOT NULL DEFAULT '0',
  `ID_MODEL` int(11) DEFAULT NULL,
  `ATTRIBUTE_ORDER` int(11) DEFAULT NULL,
  `ATTRIBUTE_NAME` varchar(50) DEFAULT NULL,
  `STATISTIC_TYPE` int(11) DEFAULT NULL COMMENT '1.枚举，2.计算数值 3非结构化文本 4其它',
  `FIELD_NAME` varchar(100) DEFAULT NULL COMMENT '字段名称',
  `FIELD_TYPE` int(11) DEFAULT NULL COMMENT 'kettle 里的数据类型编码',
  `FIELD_LENGTH` int(11) DEFAULT NULL COMMENT '字段长度',
  `IS_PRIMARY` char(1) DEFAULT NULL,
  `FIELD_PRECISION` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_ATTRIBUTE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `MDM_MODEL_CONSTAINT`;
CREATE TABLE `MDM_MODEL_CONSTAINT` (
  `ID_CONSTAINT` int(11) NOT NULL DEFAULT '0',
  `CONSTAINT_ORDER` int(11) DEFAULT NULL,
  `CONSTAINT_TYPE` int(11) DEFAULT NULL COMMENT '1 唯一  2 非空  3外键',
  `CONSTAINT_NAME` varchar(100) DEFAULT NULL COMMENT '约束名称',
  `ID_ATTRIBUTE` int(11) DEFAULT NULL,
  `REFERENCE_ID_MODEL` int(11) DEFAULT NULL,
  `REFERENCE_ID_ATTRIBUTE` int(11) DEFAULT NULL,
  `ALIAS_TABLE_FLAG` int(11) DEFAULT '0' COMMENT '是否为字符类型的唯一约束数据，创建别名表（0否   1 是）',
  PRIMARY KEY (`ID_CONSTAINT`),
  KEY `mdm_model_constaint1` (`ID_ATTRIBUTE`),
  CONSTRAINT `mdm_model_constaint1` FOREIGN KEY (`ID_ATTRIBUTE`) REFERENCES `MDM_MODEL_ATTRIBUTE` (`ID_ATTRIBUTE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `MDM_REL_CONS_ATTR`;
CREATE TABLE `MDM_REL_CONS_ATTR` (
  `ID_REL_CONS_ATTR` int(11) NOT NULL AUTO_INCREMENT,
  `ID_CONSTAINT` int(11) DEFAULT NULL,
  `ID_ATTRIBUTE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_REL_CONS_ATTR`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `COMPARE_SQL`
-- ----------------------------
DROP TABLE IF EXISTS `COMPARE_SQL`;
CREATE TABLE `COMPARE_SQL` (
  `ID_COMPARE_SQL` int(11) NOT NULL AUTO_INCREMENT,
  `ID_DATABASE` int(11) DEFAULT NULL COMMENT 'ID in r_databsae table',
  `ID_REFERENCE_DB` int(11) DEFAULT NULL COMMENT '参考sql数据库ID',
  `ID_COMPARE_TABLE_GROUP` int(11) DEFAULT NULL COMMENT 'ID in profile_table_group',
  `COMPARE_NAME` varchar(100) DEFAULT NULL COMMENT 'profile_name',
  `COMPARE_DESC` varchar(1000) DEFAULT NULL COMMENT 'compare desc',
  `COMPARE_TYPE` int(11) DEFAULT '1' COMMENT '1 for one value compare, 2 for multi-value compare, default 1',
  `SQL` longtext,
  `REFERENCE_SQL` longtext,
  `CREATE_TIME` datetime DEFAULT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_COMPARE_SQL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `COMPARE_SQL_COLUMN`
-- ----------------------------
DROP TABLE IF EXISTS `COMPARE_SQL_COLUMN`;
CREATE TABLE `COMPARE_SQL_COLUMN` (
  `ID_COMPARE_SQL_COLUMN` int(11) NOT NULL AUTO_INCREMENT,
  `COLUMN_NAME` varchar(100) DEFAULT NULL,
  `COLUMN_TYPE` varchar(100) DEFAULT NULL,
  `REFERENCE_COLUMN_NAME` varchar(100) DEFAULT NULL,
  `COLUMN_DESC` varchar(300) DEFAULT NULL,
  `ID_COMPARE_SQL` int(11) NOT NULL DEFAULT '0',
  `COMPARE_STYLE` int(11) DEFAULT NULL COMMENT '0 等值比较，1 范围比较',
  `MIN_RATIO` decimal(20,5) DEFAULT NULL,
  `MAX_RATIO` decimal(20,5) DEFAULT NULL,
  PRIMARY KEY (`ID_COMPARE_SQL_COLUMN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `COMPARE_SQL_RESULT`
-- ----------------------------
DROP TABLE IF EXISTS `COMPARE_SQL_RESULT`;
CREATE TABLE `COMPARE_SQL_RESULT` (
  `ID_COMPARE_SQL_RESULT` int(11) NOT NULL AUTO_INCREMENT,
  `ID_COMPARE_SQL_COLUMN` int(11) DEFAULT NULL,
  `COLUMN_VALUE` varchar(300) DEFAULT NULL,
  `REFERENCE_COLUMN_VALUE` varchar(300) DEFAULT NULL,
  `COMPARE_RESULT` int(11) DEFAULT NULL COMMENT '1 equals,   0  not equals',
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`ID_COMPARE_SQL_RESULT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `PROFILE_TABLE`
-- ----------------------------
DROP TABLE IF EXISTS `PROFILE_TABLE`;
CREATE TABLE `PROFILE_TABLE` (
  `ID_PROFILE_TABLE` int(11) NOT NULL AUTO_INCREMENT,
  `ID_DATABASE` int(11) DEFAULT NULL COMMENT 'ID in r_databsae table',
  `ID_PROFIEL_TABLE_GROUP` int(11) DEFAULT NULL COMMENT 'ID in profile_table_group',
  `PROFIEL_NAME` varchar(100) DEFAULT NULL COMMENT 'profile_name',
  `PROFIEL_DESC` varchar(1000) DEFAULT NULL COMMENT 'profile_name',
  `SCHEMA_NAME` varchar(100) DEFAULT NULL,
  `TABLE_NAME` longtext,
  `CONDITION` varchar(1000) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  `TABLE_NAME_TAG` int(11) DEFAULT NULL COMMENT '1:表示TABLE_NAME为表名 2：TABLE_NAME为sql',
  PRIMARY KEY (`ID_PROFILE_TABLE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `PROFILE_TABLE_COLUMN`
-- ----------------------------
DROP TABLE IF EXISTS `PROFILE_TABLE_COLUMN`;
CREATE TABLE `PROFILE_TABLE_COLUMN` (
  `ID_PROFILE_TABLE_COLUMN` int(11) NOT NULL AUTO_INCREMENT,
  `PROFILE_TABLE_COLUMN_NAME` varchar(300) DEFAULT NULL,
  `ID_PROFILE_TABLE` int(11) NOT NULL DEFAULT '0',
  `PROFILE_TABLE_COLUMN_DESC` varchar(300) DEFAULT NULL,
  `PROFILE_TABLE_COLUMN_ORDER` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_PROFILE_TABLE_COLUMN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `PROFILE_TABLE_GROUP`
-- ----------------------------
DROP TABLE IF EXISTS `PROFILE_TABLE_GROUP`;
CREATE TABLE `PROFILE_TABLE_GROUP` (
  `ID_PROFIEL_TABLE_GROUP` int(11) NOT NULL AUTO_INCREMENT,
  `PROFIEL_TABLE_GROUP_NAME` varchar(300) DEFAULT NULL,
  `PROFIEL_TABLE_GROUP_DESC` varchar(300) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID_PROFIEL_TABLE_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `PROFILE_TABLE_RESULT`
-- ----------------------------
DROP TABLE IF EXISTS `PROFILE_TABLE_RESULT`;
CREATE TABLE `PROFILE_TABLE_RESULT` (
  `ID_PROFILE_TABLE_RESULT` int(11) NOT NULL AUTO_INCREMENT,
  `ID_PROFILE_TABLE_COLUMN` int(11) NOT NULL COMMENT 'COLUMN ID',
  `INDICATOR_DATA_TYPE` varchar(100) DEFAULT NULL,
  `INDICATOR_DATA_LENGTH` int(11) DEFAULT NULL,
  `INDICATOR_DATA_PRECISION` int(11) DEFAULT NULL,
  `INDICATOR_DATA_SCALE` int(11) DEFAULT NULL,
  `INDICATOR_ALL_COUNT` int(11) DEFAULT NULL,
  `INDICATOR_DISTINCT_COUNT` int(11) DEFAULT NULL,
  `INDICATOR_NULL_COUNT` int(11) DEFAULT NULL,
  `INDICATOR_ZERO_COUNT` int(11) DEFAULT NULL,
  `INDICATOR_AGG_AVG` varchar(200) DEFAULT NULL,
  `INDICATOR_AGG_MAX` varchar(200) DEFAULT NULL,
  `INDICATOR_AGG_MIN` varchar(200) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `EXECUTE_SQL` longtext,
  PRIMARY KEY (`ID_PROFILE_TABLE_RESULT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
