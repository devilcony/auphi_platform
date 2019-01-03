package com.aofei.kettle.repository.delegate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aofei.kettle.core.database.Database;
import com.aofei.kettle.repository.KettleDataSourceRepository;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.Counters;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class KettleDataSourceRepositoryConnectionDelegate extends KettleDataSourceRepositoryBaseDelegate {
  private static Class<?> PKG = Repository.class;

  public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject(
    "DataSource repository", LoggingObjectType.REPOSITORY, null );

  public static final int REQUIRED_MAJOR_VERSION = 5;
  public static final int REQUIRED_MINOR_VERSION = 0;

  protected static final int[] KEY_POSITIONS = new int[] { 0, 1, 2 };

  protected Database database;
  protected DatabaseMeta databaseMeta;

  protected int majorVersion;
  protected int minorVersion;

  protected PreparedStatement psStepAttributesInsert;
  protected PreparedStatement psTransAttributesInsert;
  protected PreparedStatement psJobAttributesInsert;

  protected List<Object[]> stepAttributesBuffer;
  protected RowMetaInterface stepAttributesRowMeta;

  protected PreparedStatement pstmt_entry_attributes;

  protected boolean useBatchProcessing;

  protected Map<String, PreparedStatement> sqlMap;

  private class StepAttributeComparator implements Comparator<Object[]> {

    public int compare( Object[] r1, Object[] r2 ) {
      try {
        return stepAttributesRowMeta.compare( r1, r2, KEY_POSITIONS );
      } catch ( KettleValueException e ) {
        return 0; // conversion errors
      }
    }
  }

  public KettleDataSourceRepositoryConnectionDelegate( KettleDataSourceRepository repository, Database database ) {
	  super(repository);
    this.databaseMeta = database.getDatabaseMeta();
    this.database = database;

    sqlMap = new HashMap<String, PreparedStatement>();

    useBatchProcessing = true; // defaults to true;

    psStepAttributesInsert = null;
    pstmt_entry_attributes = null;

    this.majorVersion = REQUIRED_MAJOR_VERSION;
    this.minorVersion = REQUIRED_MINOR_VERSION;
  }

  /**
   * Connect to the repository
   */
//  public synchronized void connect() throws KettleException {
//    connect( false, false );
//  }
//
//  public synchronized void connect( boolean no_lookup ) throws KettleException {
//    connect( no_lookup, false );
//  }
//
//  public synchronized void connect( boolean no_lookup, boolean ignoreVersion ) throws KettleException {
//
//  }

  /**
   * Get the required repository version for this version of Kettle.
   *
   * @return the required repository version for this version of Kettle.
   */
  public static final String getRequiredVersion() {
    return REQUIRED_MAJOR_VERSION + "." + REQUIRED_MINOR_VERSION;
  }

  protected void verifyVersion() throws KettleException {
    RowMetaAndData lastUpgrade = null;
    String versionTable =
      databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_VERSION );
    try {
      lastUpgrade =
        database.getOneRow( "SELECT "
          + quote( KettleDatabaseRepository.FIELD_VERSION_MAJOR_VERSION ) + ", "
          + quote( KettleDatabaseRepository.FIELD_VERSION_MINOR_VERSION ) + ", "
          + quote( KettleDatabaseRepository.FIELD_VERSION_UPGRADE_DATE ) + " FROM " + versionTable
          + " ORDER BY " + quote( KettleDatabaseRepository.FIELD_VERSION_UPGRADE_DATE ) + " DESC" );
    } catch ( Exception e ) {
      try {
        // See if the repository exists at all. For this we verify table R_USER.
        //
        String userTable =
          databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_USER );
        database.getOneRow( "SELECT * FROM " + userTable );

        // Still here? That means we have a repository...
        //
        // If we can't retrieve the last available upgrade date:
        // this means the R_VERSION table doesn't exist.
        // This table was introduced in version 2.3.0
        //
        if ( log.isBasic() ) {
          log.logBasic( BaseMessages.getString( PKG, "Repository.Error.GettingInfoVersionTable", versionTable ) );
          log.logBasic( BaseMessages.getString( PKG, "Repository.Error.NewTable" ) );
          log.logBasic( "Stack trace: " + Const.getStackTracker( e ) );
        }
        majorVersion = 2;
        minorVersion = 2;

        lastUpgrade = null;
      } catch ( Exception ex ) {
        throw new KettleException( BaseMessages.getString( PKG, "Repository.NoRepositoryExists.Messages" ) );
      }
    }

    if ( lastUpgrade != null ) {
      majorVersion = (int) lastUpgrade.getInteger( KettleDatabaseRepository.FIELD_VERSION_MAJOR_VERSION, -1 );
      minorVersion = (int) lastUpgrade.getInteger( KettleDatabaseRepository.FIELD_VERSION_MINOR_VERSION, -1 );
    }

    if ( majorVersion < REQUIRED_MAJOR_VERSION
      || ( majorVersion == REQUIRED_MAJOR_VERSION && minorVersion < REQUIRED_MINOR_VERSION ) ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "Repository.UpgradeRequired.Message", getVersion(), getRequiredVersion() ) );
    }

    if ( majorVersion == 3 && minorVersion == 0 ) {
      // The exception: someone upgraded the repository to version 3.0.0
      // In that version, one column got named incorrectly.
      // Another upgrade to 3.0.1 or later will fix that.
      // However, since we don't have point versions in here, we'll have to look
      // at the column in question...
      //
      String tableName =
        databaseMeta.getQuotedSchemaTableCombination(
          null, KettleDatabaseRepository.TABLE_R_TRANS_PARTITION_SCHEMA );
      String errorColumn = "TRANSFORMATION";
      RowMetaInterface tableFields = database.getTableFields( tableName );
      if ( tableFields.indexOfValue( errorColumn ) >= 0 ) {
        throw new KettleException( BaseMessages.getString( PKG, "Repository.FixFor300Required.Message" ) );
      }
    }
  }

  public synchronized void disconnect() {

  }

  public synchronized void setAutoCommit( boolean autocommit ) {
    if ( !autocommit ) {
      database.setCommit( 99999999 );
    } else {
      database.setCommit( 0 );
    }
  }

  public synchronized void commit() throws KettleException {
    try {
      closeJobAttributeInsertPreparedStatement();
      closeStepAttributeInsertPreparedStatement();
      closeTransAttributeInsertPreparedStatement();

      if ( !database.isAutoCommit() ) {
        database.commit();
      }

      // Also, clear the counters, reducing the risk of collisions!
      //
      Counters.getInstance().clear();
    } catch ( KettleException dbe ) {
      throw new KettleException( "Unable to commit repository connection", dbe );
    }
  }

  public synchronized void rollback() {
    try {
      database.rollback();

      // Also, clear the counters, reducing the risk of collisions!
      //
      Counters.getInstance().clear();
    } catch ( KettleException dbe ) {
      log.logError( "Error rolling back repository." );
    }
  }

  /**
   * @return the database
   */
  public Database getDatabase() {
    return database;
  }

  /**
   * @param database
   *          the database to set
   */
  public void setDatabase( Database database ) {
    this.database = database;
  }

  /**
   * @return the databaseMeta
   */
  public DatabaseMeta getDatabaseMeta() {
    return databaseMeta;
  }

  /**
   * @param databaseMeta
   *          the databaseMeta to set
   */
  public void setDatabaseMeta( DatabaseMeta databaseMeta ) {
    this.databaseMeta = databaseMeta;
  }

  /**
   * @return the majorVersion
   */
  public int getMajorVersion() {
    return majorVersion;
  }

  /**
   * @param majorVersion
   *          the majorVersion to set
   */
  public void setMajorVersion( int majorVersion ) {
    this.majorVersion = majorVersion;
  }

  /**
   * @return the minorVersion
   */
  public int getMinorVersion() {
    return minorVersion;
  }

  /**
   * @param minorVersion
   *          the minorVersion to set
   */
  public void setMinorVersion( int minorVersion ) {
    this.minorVersion = minorVersion;
  }

  /**
   * Get the repository version.
   *
   * @return The repository version as major version + "." + minor version
   */
  public String getVersion() {
    return majorVersion + "." + minorVersion;
  }

  public synchronized void fillStepAttributesBuffer( ObjectId id_transformation ) throws KettleException {
    String sql =
      "SELECT "
        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP ) + ", "
        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_CODE ) + ", "
        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_NR ) + ", "
        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_NUM ) + ", "
        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_STR ) + " " + "FROM "
        + databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_STEP_ATTRIBUTE )
        + " " + "WHERE " + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_TRANSFORMATION ) + " = ? "
        + "ORDER BY " + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP ) + ", "
        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_CODE ) + ", "
        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_NR );


    getJdbcTemplate().query(sql, new Object[] {id_transformation.getId()}, new ResultSetExtractor<Object> () {

		@Override
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			ArrayList<Object[]> rows = new ArrayList<Object[]>();
			if ( rs != null ) {
				try {
					RowMetaInterface rowMeta = database.getRowInfo(rs.getMetaData(), false, false);

					Object[] data = database.getRow(rs, null, rowMeta);
					while (data != null) {
						rows.add(data);
						data = database.getRow(rs, null, rowMeta);
					}

					stepAttributesBuffer = rows;
					stepAttributesRowMeta = rowMeta;

					Collections.sort( stepAttributesBuffer, new StepAttributeComparator() ); //
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			return rows;
		}

    });


//    PreparedStatement ps = sqlMap.get( sql );
//    if ( ps == null ) {
//      ps = database.prepareSQL( sql );
//      sqlMap.put( sql, ps );
//    }
//
//    RowMetaAndData parameter = getParameterMetaData( id_transformation );
//    ResultSet resultSet = database.openQuery( ps, parameter.getRowMeta(), parameter.getData() );
//    stepAttributesBuffer = database.getRows( resultSet, -1, null );
//    stepAttributesRowMeta = database.getReturnRowMeta();
//
//    Collections.sort( stepAttributesBuffer, new StepAttributeComparator() ); //
  }

  /**
   * @return Returns the stepAttributesBuffer.
   */
  public List<Object[]> getStepAttributesBuffer() {
    return stepAttributesBuffer;
  }

  /**
   * @param stepAttributesBuffer
   *          The stepAttributesBuffer to set.
   */
  public void setStepAttributesBuffer( List<Object[]> stepAttributesBuffer ) {
    this.stepAttributesBuffer = stepAttributesBuffer;
  }

  private synchronized RowMetaAndData searchStepAttributeInBuffer( ObjectId id_step, String code, long nr ) throws KettleValueException {
    int index = searchStepAttributeIndexInBuffer( id_step, code, nr );
    if ( index < 0 ) {
      return null;
    }

    // Get the row
    //
    Object[] r = stepAttributesBuffer.get( index );

    // and remove it from the list...
    // stepAttributesBuffer.remove(index);

    return new RowMetaAndData( stepAttributesRowMeta, r );
  }

  private synchronized int searchStepAttributeIndexInBuffer( ObjectId id_step, String code, long nr ) throws KettleValueException {
    Object[] key = new Object[] { new LongObjectId( id_step ).longValue(), // ID_STEP
      code, // CODE
      new Long( nr ), // NR
    };

    int index = Collections.binarySearch( stepAttributesBuffer, key, new StepAttributeComparator() );

    if ( index >= stepAttributesBuffer.size() || index < 0 ) {
      return -1;
    }

    //
    // Check this... If it is not in there, we didn't find it!
    // stepAttributesRowMeta.compare returns 0 when there are conversion issues
    // so the binarySearch could have 'found' a match when there really isn't
    // one
    //
    Object[] look = stepAttributesBuffer.get( index );

    if ( stepAttributesRowMeta.compare( look, key, KEY_POSITIONS ) == 0 ) {
      return index;
    }

    return -1;
  }

  private synchronized int searchNrStepAttributes( ObjectId id_step, String code ) throws KettleValueException {
    // Search the index of the first step attribute with the specified code...
    //
    int idx = searchStepAttributeIndexInBuffer( id_step, code, 0L );
    if ( idx < 0 ) {
      return 0;
    }

    int nr = 1;
    int offset = 1;

    if ( idx + offset >= stepAttributesBuffer.size() ) {
      // Only 1, the last of the attributes buffer.
      //
      return 1;
    }
    Object[] look = stepAttributesBuffer.get( idx + offset );
    RowMetaInterface rowMeta = stepAttributesRowMeta;

    long lookID = rowMeta.getInteger( look, 0 );
    String lookCode = rowMeta.getString( look, 1 );

    while ( lookID == new LongObjectId( id_step ).longValue() && code.equalsIgnoreCase( lookCode ) ) {
      // Find the maximum
      //
      nr = rowMeta.getInteger( look, 2 ).intValue() + 1;
      offset++;
      if ( idx + offset < stepAttributesBuffer.size() ) {
        look = stepAttributesBuffer.get( idx + offset );

        lookID = rowMeta.getInteger( look, 0 );
        lookCode = rowMeta.getString( look, 1 );
      } else {
        return nr;
      }
    }
    return nr;
  }

  public synchronized void closeStepAttributeInsertPreparedStatement() throws KettleException {
    if ( psStepAttributesInsert != null ) {
      database.emptyAndCommit( psStepAttributesInsert, useBatchProcessing, 1 ); // batch
                                                                                // mode!
      psStepAttributesInsert = null;
    }
  }

  public synchronized void closeTransAttributeInsertPreparedStatement() throws KettleException {
    if ( psTransAttributesInsert != null ) {
      database.emptyAndCommit( psTransAttributesInsert, useBatchProcessing, 1 ); // batch
                                                                                 // mode!
      psTransAttributesInsert = null;
    }
  }

  public synchronized void closeJobAttributeInsertPreparedStatement() throws KettleException {
    if ( psJobAttributesInsert != null ) {
      database.emptyAndCommit( psJobAttributesInsert, useBatchProcessing, 1 ); // batch
                                                                               // mode!
      psJobAttributesInsert = null;
    }
  }

  private RowMetaAndData getStepAttributeRow( ObjectId id_step, int nr, String code ) throws KettleException {

	  String sql =
		      "SELECT "
		        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_STR ) + ", "
		        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_NUM ) + " FROM "
		        + databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_STEP_ATTRIBUTE )
		        + " WHERE " + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP ) + " = ?  AND "
		        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_CODE ) + " = ?  AND "
		        + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_NR ) + " = ? ";

	  Object[] args = new Object[] {Integer.parseInt(id_step.getId()), code, nr};
	  return getJdbcTemplate().query(sql, args, new ResultSetExtractor<RowMetaAndData> () {

		@Override
		public RowMetaAndData extractData(ResultSet rs) throws SQLException, DataAccessException {
			try {
				RowMetaInterface rowInfo = database.getRowInfo(rs.getMetaData(), false, false);
				Object[] result = database.getRow(rs, null, rowInfo);

				if (result == null) {
					return new RowMetaAndData(rowInfo, RowDataUtil.allocateRowData(rowInfo.size()));
				}

				RowMetaAndData rmad = new RowMetaAndData(rowInfo, result);
				return rmad;
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	  });
  }

  public RowMetaAndData getTransAttributeRow( ObjectId id_transformation, int nr, String code ) throws KettleException {
	  String sql =
		      "SELECT "
		        + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_STR )
		        + ", "
		        + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_NUM )
		        + " FROM "
		        + databaseMeta
		          .getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_TRANS_ATTRIBUTE )
		        + " WHERE " + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANSFORMATION ) + " = ?  AND "
		        + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_CODE ) + " = ? AND "
		        + KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_NR + " = ? ";

	  Object[] args = new Object[] {id_transformation == null ? null : Integer.parseInt(id_transformation.getId()), code, nr};
	  return getJdbcTemplate().query(sql, args, new ResultSetExtractor<RowMetaAndData> () {

		@Override
		public RowMetaAndData extractData(ResultSet rs) throws SQLException, DataAccessException {
			try {
				RowMetaInterface rowInfo = database.getRowInfo(rs.getMetaData(), false, false);
				Object[] result = database.getRow(rs, null, rowInfo);

				if (result == null) {
					return new RowMetaAndData(rowInfo, RowDataUtil.allocateRowData(rowInfo.size()));
				}

				RowMetaAndData rmad = new RowMetaAndData(rowInfo, result);
				return rmad;
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	  });
  }

  public RowMetaAndData getJobAttributeRow( ObjectId id_job, int nr, String code ) throws KettleException {
	  String sql =
		      "SELECT "
		        + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_STR ) + ", "
		        + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_NUM ) + " FROM "
		        + databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_JOB_ATTRIBUTE )
		        + " WHERE " + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB ) + " = ?  AND "
		        + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_CODE ) + " = ? AND "
		        + KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_NR + " = ? ";

	  Object[] args = new Object[] {Integer.parseInt(id_job.getId()), code, nr};
	  return getJdbcTemplate().query(sql, args, new ResultSetExtractor<RowMetaAndData> () {

		@Override
		public RowMetaAndData extractData(ResultSet rs) throws SQLException, DataAccessException {
			try {
				RowMetaInterface rowInfo = database.getRowInfo(rs.getMetaData(), false, false);
				Object[] result = database.getRow(rs, null, rowInfo);

				if (result == null) {
					return new RowMetaAndData(rowInfo, RowDataUtil.allocateRowData(rowInfo.size()));
				}

				RowMetaAndData rmad = new RowMetaAndData(rowInfo, result);
				return rmad;
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	  });



//    RowMetaAndData par = new RowMetaAndData();
//    par.addValue( new ValueMeta(
//      KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB, ValueMetaInterface.TYPE_INTEGER ), id_job );
//    par.addValue(
//      new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
//    par.addValue(
//      new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_NR, ValueMetaInterface.TYPE_INTEGER ),
//      new Long( nr ) );
//
//    if ( psJobAttributesLookup == null ) {
//      setLookupJobAttribute();
//    }
//    database.setValues( par, psJobAttributesLookup );
//    Object[] r = database.getLookup( psJobAttributesLookup );
//    if ( r == null ) {
//      return null;
//    }
//    return new RowMetaAndData( database.getReturnRowMeta(), r );
  }

  public synchronized long getStepAttributeInteger( ObjectId id_step, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    if ( stepAttributesBuffer != null ) {
      r = searchStepAttributeInBuffer( id_step, code, nr );
    } else {
      r = getStepAttributeRow( id_step, nr, code );
    }
    if ( r == null ) {
      return -1L;
    }
    long id = r.getInteger( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_NUM, -1L );
    return id;
  }

  public synchronized ObjectId findStepAttributeID( ObjectId id_step, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    if ( stepAttributesBuffer != null ) {
      r = searchStepAttributeInBuffer( id_step, code, nr );
    } else {
      r = getStepAttributeRow( id_step, nr, code );
    }
    if ( r == null ) {
      return null;
    }

    long id = r.getInteger( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP, -1L );
    if ( id < 0 ) {
      return null;
    }

    return new LongObjectId( id );
  }

  public synchronized String getStepAttributeString( ObjectId id_step, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    if ( stepAttributesBuffer != null ) {
      r = searchStepAttributeInBuffer( id_step, code, nr );
    } else {
      r = getStepAttributeRow( id_step, nr, code );
    }
    if ( r == null ) {
      return null;
    }
    return r.getString( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_STR, null );
  }

  public synchronized boolean getStepAttributeBoolean( ObjectId id_step, int nr, String code, boolean def ) throws KettleException {
    RowMetaAndData r = null;
    if ( stepAttributesBuffer != null ) {
      r = searchStepAttributeInBuffer( id_step, code, nr );
    } else {
      r = getStepAttributeRow( id_step, nr, code );
    }

    if ( r == null ) {
      return def;
    }
    String v = r.getString( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_STR, null );
    if ( v == null || Const.isEmpty( v ) ) {
      return def;
    }
    return ValueMeta.convertStringToBoolean( v ).booleanValue();
  }

  public ObjectId saveStepAttribute( ObjectId id_transformation, ObjectId id_step, long nr, String code,
    String value ) throws KettleException {
    return saveStepAttribute( code, nr, id_transformation, id_step, 0.0, value );
  }

  public ObjectId saveStepAttribute( ObjectId id_transformation, ObjectId id_step, long nr, String code,
    double value ) throws KettleException {
    return saveStepAttribute( code, nr, id_transformation, id_step, value, null );
  }

  public ObjectId saveStepAttribute( ObjectId id_transformation, ObjectId id_step, long nr, String code,
    boolean value ) throws KettleException {
    return saveStepAttribute( code, nr, id_transformation, id_step, 0.0, value ? "Y" : "N" );
  }

  private ObjectId saveStepAttribute( String code, long nr, ObjectId id_transformation, ObjectId id_step,
    double value_num, String value_str ) throws KettleException {
    return insertStepAttribute( id_transformation, id_step, nr, code, value_num, value_str );
  }

  public synchronized int countNrStepAttributes( ObjectId id_step, String code ) throws KettleException {
    if ( stepAttributesBuffer != null ) {
      // see if we can do this in memory...

      int nr = searchNrStepAttributes( id_step, code );
      return nr;
    } else {
      String sql =
        "SELECT COUNT(*) FROM "
          + databaseMeta.getQuotedSchemaTableCombination(
            null, KettleDatabaseRepository.TABLE_R_STEP_ATTRIBUTE ) + " WHERE "
          + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP ) + " = ? AND "
          + quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_CODE ) + " = ?";
      RowMetaAndData table = new RowMetaAndData();
      table.addValue( new ValueMeta(
        KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP, ValueMetaInterface.TYPE_INTEGER ), id_step );
      table.addValue( new ValueMeta(
        KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
      RowMetaAndData r = database.getOneRow( sql, table.getRowMeta(), table.getData() );
      if ( r == null || r.getData() == null ) {
        return 0;
      }
      return (int) r.getInteger( 0, 0L );
    }
  }

  // TRANS ATTRIBUTES: get

  public synchronized String getTransAttributeString( ObjectId id_transformation, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    r = getTransAttributeRow( id_transformation, nr, code );
    if ( r == null ) {
      return null;
    }
    return r.getString( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_STR, null );
  }

  public synchronized boolean getTransAttributeBoolean( ObjectId id_transformation, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    r = getTransAttributeRow( id_transformation, nr, code );
    if ( r == null ) {
      return false;
    }
    return r.getBoolean( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_STR, false );
  }

  public synchronized double getTransAttributeNumber( ObjectId id_transformation, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    r = getTransAttributeRow( id_transformation, nr, code );
    if ( r == null ) {
      return 0.0;
    }
    return r.getNumber( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_NUM, 0.0 );
  }

  public synchronized long getTransAttributeInteger( ObjectId id_transformation, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    r = getTransAttributeRow( id_transformation, nr, code );
    if ( r == null ) {
      return 0L;
    }
    return r.getInteger( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_NUM, 0L );
  }

  public synchronized int countNrTransAttributes( ObjectId id_transformation, String code ) throws KettleException {
    String sql =
      "SELECT COUNT(*) FROM "
        + databaseMeta
          .getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_TRANS_ATTRIBUTE )
        + " WHERE " + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANSFORMATION ) + " = ? AND "
        + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_CODE ) + " = ?";
    RowMetaAndData table = new RowMetaAndData();
    table.addValue(
      new ValueMeta(
        KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANSFORMATION, ValueMetaInterface.TYPE_INTEGER ),
      id_transformation );
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    RowMetaAndData r = database.getOneRow( sql, table.getRowMeta(), table.getData() );
    if ( r == null || r.getData() == null ) {
      return 0;
    }

    return (int) r.getInteger( 0, 0L );
  }

  public synchronized List<Object[]> getTransAttributes( ObjectId id_transformation, String code, long nr ) throws KettleException {
    String sql =
      "SELECT *"
        + " FROM "
        + databaseMeta
          .getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_TRANS_ATTRIBUTE )
        + " WHERE " + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANSFORMATION ) + " = ? AND "
        + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_CODE ) + " = ? AND "
        + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_NR ) + " = ?" + " ORDER BY "
        + quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_NUM );

    RowMetaAndData table = new RowMetaAndData();
    table.addValue(
      new ValueMeta(
        KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANSFORMATION, ValueMetaInterface.TYPE_INTEGER ),
      new LongObjectId( id_transformation ) );
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_NR, ValueMetaInterface.TYPE_INTEGER ), new Long( nr ) );

    return database.getRows( sql, table.getRowMeta(), table.getData(), ResultSet.FETCH_FORWARD, false, 0, null );
  }

  public synchronized List<Object[]> getTransAttributesWithPrefix( ObjectId id_transformation, String codePrefix ) throws KettleException {
	  long start = System.currentTimeMillis();

		String sql = "SELECT *" + " FROM "
				+ databaseMeta.getQuotedSchemaTableCombination(null, KettleDatabaseRepository.TABLE_R_TRANS_ATTRIBUTE)
				+ " WHERE " + quote(KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANSFORMATION) + " = ?" + " AND "
				+ quote(KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_CODE) + " LIKE '" + codePrefix + "%'";

//    RowMetaAndData table = new RowMetaAndData();
//    table.addValue(
//      new ValueMeta(
//        KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANSFORMATION, ValueMetaInterface.TYPE_INTEGER ),
//      new LongObjectId( id_transformation ) );
//    return database.getRows( sql, table.getRowMeta(), table.getData(), ResultSet.FETCH_FORWARD, false, 0, null );
		Object[] args = new Object[] { id_transformation != null ? Long.parseLong(id_transformation.getId()) : null };
		List<Object[]> list = getJdbcTemplate().query(sql, args, new ResultSetExtractor<List<Object[]>>() {
			@Override
			public List<Object[]> extractData(ResultSet rs) throws SQLException, DataAccessException {
				try {
					RowMetaInterface rowMeta = database.getRowInfo(rs.getMetaData(), false, false);
					ArrayList<Object[]> rows = new ArrayList<Object[]>();

					Object[] data = database.getRow(rs, null, rowMeta);
					while (data != null) {
						rows.add(data);
						data = database.getRow(rs, null, rowMeta);
					}

					return rows;
				} catch(Exception e) {
					e.printStackTrace();
					return null;
				}
			}

		});

//		log.logBasic("getTransAttributesWithPrefix - sql["+ sql +  "]: " +   + (System.currentTimeMillis() - start) );

		return list;
  }

  // JOB ATTRIBUTES: get

  public synchronized String getJobAttributeString( ObjectId id_job, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    r = getJobAttributeRow( id_job, nr, code );
    if ( r == null ) {
      return null;
    }
    return r.getString( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_STR, null );
  }

  public synchronized boolean getJobAttributeBoolean( ObjectId id_job, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    r = getJobAttributeRow( id_job, nr, code );
    if ( r == null ) {
      return false;
    }
    return r.getBoolean( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_STR, false );
  }

  public synchronized double getJobAttributeNumber( ObjectId id_job, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    r = getJobAttributeRow( id_job, nr, code );
    if ( r == null ) {
      return 0.0;
    }
    return r.getNumber( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_NUM, 0.0 );
  }

  public synchronized long getJobAttributeInteger( ObjectId id_job, int nr, String code ) throws KettleException {
    RowMetaAndData r = null;
    r = getJobAttributeRow( id_job, nr, code );
    if ( r == null ) {
      return 0L;
    }
    return r.getInteger( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_NUM, 0L );
  }

  public synchronized int countNrJobAttributes( ObjectId id_job, String code ) throws KettleException {
    String sql =
      "SELECT COUNT(*) FROM "
        + databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_JOB_ATTRIBUTE )
        + " WHERE " + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB ) + " = ? AND "
        + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_CODE ) + " = ?";
    RowMetaAndData table = new RowMetaAndData();
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB, ValueMetaInterface.TYPE_INTEGER ), id_job );
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    RowMetaAndData r = database.getOneRow( sql, table.getRowMeta(), table.getData() );
    if ( r == null || r.getData() == null ) {
      return 0;
    }

    return (int) r.getInteger( 0, 0L );
  }

  public synchronized List<Object[]> getJobAttributes( ObjectId id_job, String code, long nr ) throws KettleException {
    String sql =
      "SELECT *"
        + " FROM "
        + databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_JOB_ATTRIBUTE )
        + " WHERE " + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB ) + " = ? AND "
        + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_CODE ) + " = ? AND "
        + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_NR ) + " = ?" + " ORDER BY "
        + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_NUM );

    RowMetaAndData table = new RowMetaAndData();
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB, ValueMetaInterface.TYPE_INTEGER ), id_job );
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_NR, ValueMetaInterface.TYPE_INTEGER ), new Long( nr ) );

    return database.getRows( sql, table.getRowMeta(), table.getData(), ResultSet.FETCH_FORWARD, false, 0, null );
  }

  public synchronized List<Object[]> getJobAttributesWithPrefix( ObjectId jobId, String codePrefix ) throws KettleException {
    String sql =
      "SELECT *"
        + " FROM "
        + databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_JOB_ATTRIBUTE )
        + " WHERE " + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB ) + " = ?" + " AND "
        + quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_CODE ) + " LIKE '" + codePrefix + "%'";

    RowMetaAndData table = new RowMetaAndData();
    table.addValue(
      new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB, ValueMetaInterface.TYPE_INTEGER ),
      new LongObjectId( jobId ) );

    return database.getRows( sql, table.getRowMeta(), table.getData(), ResultSet.FETCH_FORWARD, false, 0, null );
  }

  // JOBENTRY ATTRIBUTES: SAVE

  // WANTED: throw extra exceptions to locate storage problems (strings too long
  // etc)
  //

  public ObjectId saveJobEntryAttribute( ObjectId id_job, ObjectId id_jobentry,
    long nr, String code, String value ) throws KettleException {
    return saveJobEntryAttribute( code, nr, id_job, id_jobentry, 0.0, value );
  }

  public ObjectId saveJobEntryAttribute( ObjectId id_job, ObjectId id_jobentry,
    long nr, String code, double value ) throws KettleException {
    return saveJobEntryAttribute( code, nr, id_job, id_jobentry, value, null );
  }

  public ObjectId saveJobEntryAttribute( ObjectId id_job, ObjectId id_jobentry,
    long nr, String code, boolean value ) throws KettleException {
    return saveJobEntryAttribute( code, nr, id_job, id_jobentry, 0.0, value ? "Y" : "N" );
  }

  private ObjectId saveJobEntryAttribute( String code, long nr, ObjectId id_job, ObjectId id_jobentry,
    double value_num, String value_str ) throws KettleException {
    return insertJobEntryAttribute( id_job, id_jobentry, nr, code, value_num, value_str );
  }

  public synchronized ObjectId insertJobEntryAttribute( ObjectId id_job, ObjectId id_jobentry, long nr,
    String code, double value_num, String value_str ) throws KettleException {
    ObjectId id = getNextJobEntryAttributeID();

    RowMetaAndData table = new RowMetaAndData();

    //CHECKSTYLE:LineLength:OFF
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY_ATTRIBUTE, ValueMetaInterface.TYPE_INTEGER ), id );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOB, ValueMetaInterface.TYPE_INTEGER ), id_job );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY, ValueMetaInterface.TYPE_INTEGER ), id_jobentry );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_NR, ValueMetaInterface.TYPE_INTEGER ), new Long( nr ) );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_NUM, ValueMetaInterface.TYPE_NUMBER ), new Double( value_num ) );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_STR, ValueMetaInterface.TYPE_STRING ), value_str );

//    database.prepareInsert( table.getRowMeta(), KettleDatabaseRepository.TABLE_R_JOBENTRY_ATTRIBUTE );
//    database.setValuesInsert( table );
//    database.insertRow();
//    database.closeInsert();
    database.insertTableRow(KettleDatabaseRepository.TABLE_R_JOBENTRY_ATTRIBUTE, table);

    return id;
  }

  public synchronized LongObjectId getNextJobEntryAttributeID() throws KettleException {
    return getNextID(
      databaseMeta.getQuotedSchemaTableCombination( null, KettleDatabaseRepository.TABLE_R_JOBENTRY_ATTRIBUTE ),
      quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY_ATTRIBUTE ) );
  }

  public synchronized LongObjectId getNextID( String tableName, String fieldName ) throws KettleException {
    String counterName = tableName + "." + fieldName;
    Counter counter = Counters.getInstance().getCounter( counterName );
    if ( counter == null ) {
      LongObjectId id = getNextTableID( tableName, fieldName );
      counter = new Counter( id.longValue() );
      Counters.getInstance().setCounter( counterName, counter );
      return new LongObjectId( counter.next() );
    } else {
      return new LongObjectId( counter.next() );
    }
  }

  private synchronized LongObjectId getNextTableID( String tablename, String idfield ) throws KettleException {
    LongObjectId retval = null;

    RowMetaAndData r = database.getOneRow( "SELECT MAX(" + idfield + ") FROM " + tablename );
    if ( r != null ) {
      Long id = r.getInteger( 0 );

      if ( id == null ) {
        if ( log.isDebug() ) {
          log.logDebug( "no max(" + idfield + ") found in table " + tablename );
        }
        retval = new LongObjectId( 1 );
      } else {
        if ( log.isDebug() ) {
          log.logDebug( "max(" + idfield + ") found in table " + tablename + " --> " + idfield + " number: " + id );
        }
        retval = new LongObjectId( id.longValue() + 1L );
      }
    }
    return retval;
  }

  // JOBENTRY ATTRIBUTES: GET

//  public synchronized void setLookupJobEntryAttribute() throws KettleException {
//    String sql =
//      "SELECT "
//        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_STR )
//        + ", "
//        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_NUM )
//        + " FROM "
//        + databaseMeta.getQuotedSchemaTableCombination(
//          null, KettleDatabaseRepository.TABLE_R_JOBENTRY_ATTRIBUTE ) + " WHERE "
//        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY ) + " = ? AND "
//        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_CODE ) + " = ?  AND "
//        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_NR ) + " = ? ";
//
//    pstmt_entry_attributes = database.prepareSQL( sql );
//  }

  public synchronized void closeLookupJobEntryAttribute() throws KettleException {
    database.closePreparedStatement( pstmt_entry_attributes );
    pstmt_entry_attributes = null;
  }

  private RowMetaAndData getJobEntryAttributeRow( ObjectId id_jobentry, int nr, String code ) throws KettleException {
//    RowMetaAndData par = new RowMetaAndData();
//    par.addValue(
//      new ValueMeta(
//        KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY, ValueMetaInterface.TYPE_INTEGER ),
//      id_jobentry );
//    par.addValue( new ValueMeta(
//      KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
//    par.addValue( new ValueMeta(
//      KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_NR, ValueMetaInterface.TYPE_INTEGER ), new Long( nr ) );

	  String sql =
		      "SELECT "
		        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_STR )
		        + ", "
		        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_NUM )
		        + " FROM "
		        + databaseMeta.getQuotedSchemaTableCombination(
		          null, KettleDatabaseRepository.TABLE_R_JOBENTRY_ATTRIBUTE ) + " WHERE "
		        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY ) + " = ? AND "
		        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_CODE ) + " = ?  AND "
		        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_NR ) + " = ? ";


	return getJdbcTemplate().query(sql, new Object[] {id_jobentry.getId(), code, nr}, new ResultSetExtractor<RowMetaAndData>() {

		@Override
		public RowMetaAndData extractData(ResultSet rs) throws SQLException, DataAccessException {
			RowMetaAndData rmad = null;
			if ( rs != null ) {
				try {
					RowMetaInterface rowInfo = database.getRowInfo(rs.getMetaData(), false, false);
					Object[] result = database.getRow(rs, null, rowInfo);

					rmad = new RowMetaAndData( rowInfo, result );
				} catch(Exception e) {
					e.printStackTrace();
				}
			}


			return rmad;
		}

	});


//    if ( pstmt_entry_attributes == null ) {
//      setLookupJobEntryAttribute();
//    }
//    database.setValues( par.getRowMeta(), par.getData(), pstmt_entry_attributes );
//    Object[] rowData = database.getLookup( pstmt_entry_attributes );
//    return new RowMetaAndData( database.getReturnRowMeta(), rowData );
  }

  public synchronized long getJobEntryAttributeInteger( ObjectId id_jobentry, int nr, String code ) throws KettleException {
    RowMetaAndData r = getJobEntryAttributeRow( id_jobentry, nr, code );
    if ( r == null ) {
      return 0;
    }
    return r.getInteger( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_NUM, 0L );
  }

  public synchronized double getJobEntryAttributeNumber( ObjectId id_jobentry, int nr, String code ) throws KettleException {
    RowMetaAndData r = getJobEntryAttributeRow( id_jobentry, nr, code );
    if ( r == null ) {
      return 0.0;
    }
    return r.getNumber( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_NUM, 0.0 );
  }

  public synchronized String getJobEntryAttributeString( ObjectId id_jobentry, int nr, String code ) throws KettleException {
    RowMetaAndData r = getJobEntryAttributeRow( id_jobentry, nr, code );
    if ( r == null ) {
      return null;
    }
    return r.getString( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_STR, null );
  }

  public synchronized boolean getJobEntryAttributeBoolean( ObjectId id_jobentry, int nr, String code, boolean def ) throws KettleException {
    RowMetaAndData r = getJobEntryAttributeRow( id_jobentry, nr, code );
    if ( r == null ) {
      return def;
    }
    String v = r.getString( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_VALUE_STR, null );
    if ( v == null || Const.isEmpty( v ) ) {
      return def;
    }
    return ValueMeta.convertStringToBoolean( v ).booleanValue();
  }

  public synchronized int countNrJobEntryAttributes( ObjectId id_jobentry, String code ) throws KettleException {
    String sql =
      "SELECT COUNT(*) FROM "
        + databaseMeta.getQuotedSchemaTableCombination(
          null, KettleDatabaseRepository.TABLE_R_JOBENTRY_ATTRIBUTE ) + " WHERE "
        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY ) + " = ? AND "
        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_CODE ) + " = ?";
    RowMetaAndData table = new RowMetaAndData();
    table.addValue(
      new ValueMeta(
        KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY, ValueMetaInterface.TYPE_INTEGER ),
      id_jobentry );
    table.addValue( new ValueMeta(
      KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    RowMetaAndData r = database.getOneRow( sql, table.getRowMeta(), table.getData() );
    if ( r == null || r.getData() == null ) {
      return 0;
    }
    return (int) r.getInteger( 0, 0L );
  }

  public synchronized List<Object[]> getJobEntryAttributesWithPrefix( ObjectId jobId, ObjectId jobEntryId,
    String codePrefix ) throws KettleException {
    String sql =
      "SELECT *"
        + " FROM "
        + databaseMeta.getQuotedSchemaTableCombination(
          null, KettleDatabaseRepository.TABLE_R_JOBENTRY_ATTRIBUTE ) + " WHERE "
        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOB ) + " = ?" + " AND "
        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY ) + " = ?" + " AND "
        + quote( KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_CODE ) + " LIKE '" + codePrefix + "%'";

    RowMetaAndData table = new RowMetaAndData();
    table
      .addValue(
        new ValueMeta(
          KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOB, ValueMetaInterface.TYPE_INTEGER ),
        new LongObjectId( jobId ) );
    table.addValue(
      new ValueMeta(
        KettleDatabaseRepository.FIELD_JOBENTRY_ATTRIBUTE_ID_JOBENTRY, ValueMetaInterface.TYPE_INTEGER ),
      new LongObjectId( jobEntryId ) );

    return database.getRows( sql, table.getRowMeta(), table.getData(), ResultSet.FETCH_FORWARD, false, 0, null );
  }

  // ///////////////////////////////////////////////////////////////////////////////////
  // GET NEW IDS
  // ///////////////////////////////////////////////////////////////////////////////////

  public synchronized ObjectId getNextTransformationID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_TRANSFORMATION ),
      quote( KettleDatabaseRepository.FIELD_TRANSFORMATION_ID_TRANSFORMATION ) );
  }

  public synchronized ObjectId getNextJobID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_JOB ), quote( KettleDatabaseRepository.FIELD_JOB_ID_JOB ) );
  }

  public synchronized ObjectId getNextNoteID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_NOTE ), quote( KettleDatabaseRepository.FIELD_NOTE_ID_NOTE ) );
  }

  public synchronized ObjectId getNextLogID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_REPOSITORY_LOG ),
      quote( KettleDatabaseRepository.FIELD_REPOSITORY_LOG_ID_REPOSITORY_LOG ) );
  }

  public synchronized ObjectId getNextDatabaseID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_DATABASE ),
      quote( KettleDatabaseRepository.FIELD_DATABASE_ID_DATABASE ) );
  }

  public synchronized ObjectId getNextDatabaseTypeID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_DATABASE_TYPE ),
      quote( KettleDatabaseRepository.FIELD_DATABASE_TYPE_ID_DATABASE_TYPE ) );
  }

  public synchronized ObjectId getNextDatabaseConnectionTypeID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_DATABASE_CONTYPE ),
      quote( KettleDatabaseRepository.FIELD_DATABASE_CONTYPE_ID_DATABASE_CONTYPE ) );
  }

  public synchronized ObjectId getNextLoglevelID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_LOGLEVEL ),
      quote( KettleDatabaseRepository.FIELD_LOGLEVEL_ID_LOGLEVEL ) );
  }

  public synchronized ObjectId getNextStepTypeID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_STEP_TYPE ),
      quote( KettleDatabaseRepository.FIELD_STEP_TYPE_ID_STEP_TYPE ) );
  }

  public synchronized ObjectId getNextStepID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_STEP ), quote( KettleDatabaseRepository.FIELD_STEP_ID_STEP ) );
  }

  public synchronized ObjectId getNextJobEntryID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_JOBENTRY ),
      quote( KettleDatabaseRepository.FIELD_JOBENTRY_ID_JOBENTRY ) );
  }

  public synchronized ObjectId getNextJobEntryTypeID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_JOBENTRY_TYPE ),
      quote( KettleDatabaseRepository.FIELD_JOBENTRY_TYPE_ID_JOBENTRY_TYPE ) );
  }

  public synchronized LongObjectId getNextJobEntryCopyID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_JOBENTRY_COPY ),
      quote( KettleDatabaseRepository.FIELD_JOBENTRY_COPY_ID_JOBENTRY_COPY ) );
  }

  public synchronized LongObjectId getNextStepAttributeID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_STEP_ATTRIBUTE ),
      quote( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP_ATTRIBUTE ) );
  }

  public synchronized LongObjectId getNextTransAttributeID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_TRANS_ATTRIBUTE ),
      quote( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANS_ATTRIBUTE ) );
  }

  public synchronized LongObjectId getNextJobAttributeID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_JOB_ATTRIBUTE ),
      quote( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB_ATTRIBUTE ) );
  }

  public synchronized LongObjectId getNextDatabaseAttributeID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_DATABASE_ATTRIBUTE ),
      quote( KettleDatabaseRepository.FIELD_DATABASE_ATTRIBUTE_ID_DATABASE_ATTRIBUTE ) );
  }

  public synchronized ObjectId getNextTransHopID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_TRANS_HOP ),
      quote( KettleDatabaseRepository.FIELD_TRANS_HOP_ID_TRANS_HOP ) );
  }

  public synchronized ObjectId getNextJobHopID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_JOB_HOP ),
      quote( KettleDatabaseRepository.FIELD_JOB_HOP_ID_JOB_HOP ) );
  }

  public synchronized ObjectId getNextDepencencyID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_DEPENDENCY ),
      quote( KettleDatabaseRepository.FIELD_DEPENDENCY_ID_DEPENDENCY ) );
  }

  public synchronized ObjectId getNextPartitionSchemaID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_PARTITION_SCHEMA ),
      quote( KettleDatabaseRepository.FIELD_PARTITION_SCHEMA_ID_PARTITION_SCHEMA ) );
  }

  public synchronized ObjectId getNextPartitionID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_PARTITION ),
      quote( KettleDatabaseRepository.FIELD_PARTITION_ID_PARTITION ) );
  }

  public synchronized ObjectId getNextTransformationPartitionSchemaID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_TRANS_PARTITION_SCHEMA ),
      quote( KettleDatabaseRepository.FIELD_TRANS_PARTITION_SCHEMA_ID_TRANS_PARTITION_SCHEMA ) );
  }

  public synchronized ObjectId getNextClusterID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_CLUSTER ),
      quote( KettleDatabaseRepository.FIELD_CLUSTER_ID_CLUSTER ) );
  }

  public synchronized ObjectId getNextSlaveServerID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_SLAVE ),
      quote( KettleDatabaseRepository.FIELD_SLAVE_ID_SLAVE ) );
  }

  public synchronized ObjectId getNextClusterSlaveID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_CLUSTER_SLAVE ),
      quote( KettleDatabaseRepository.FIELD_CLUSTER_SLAVE_ID_CLUSTER_SLAVE ) );
  }

  public synchronized ObjectId getNextTransformationSlaveID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_TRANS_SLAVE ),
      quote( KettleDatabaseRepository.FIELD_TRANS_SLAVE_ID_TRANS_SLAVE ) );
  }

  public synchronized ObjectId getNextTransformationClusterID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_TRANS_CLUSTER ),
      quote( KettleDatabaseRepository.FIELD_TRANS_CLUSTER_ID_TRANS_CLUSTER ) );
  }

  public synchronized ObjectId getNextConditionID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_CONDITION ),
      quote( KettleDatabaseRepository.FIELD_CONDITION_ID_CONDITION ) );
  }

  public synchronized ObjectId getNextValueID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_VALUE ),
      quote( KettleDatabaseRepository.FIELD_VALUE_ID_VALUE ) );
  }

  public synchronized ObjectId getNextUserID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_USER ), quote( KettleDatabaseRepository.FIELD_USER_ID_USER ) );
  }

  public synchronized void clearNextIDCounters() {
    Counters.getInstance().clear();
  }

  public synchronized ObjectId getNextDirectoryID() throws KettleException {
    return getNextID(
      quoteTable( KettleDatabaseRepository.TABLE_R_DIRECTORY ),
      quote( KettleDatabaseRepository.FIELD_DIRECTORY_ID_DIRECTORY ) );
  }

  public synchronized ObjectId insertStepAttribute( ObjectId id_transformation, ObjectId id_step, long nr,
    String code, double value_num, String value_str ) throws KettleException {
    ObjectId id = getNextStepAttributeID();

    RowMetaAndData table = new RowMetaAndData();

    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP_ATTRIBUTE, ValueMetaInterface.TYPE_INTEGER ), id );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_TRANSFORMATION, ValueMetaInterface.TYPE_INTEGER ), id_transformation );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_ID_STEP, ValueMetaInterface.TYPE_INTEGER ), id_step );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_NR, ValueMetaInterface.TYPE_INTEGER ), new Long( nr ) );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_NUM, ValueMetaInterface.TYPE_NUMBER ), new Double( value_num ) );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_STEP_ATTRIBUTE_VALUE_STR, ValueMetaInterface.TYPE_STRING ), value_str );

    getDatabase().insertTableRow( KettleDatabaseRepository.TABLE_R_STEP_ATTRIBUTE, table);

//    if ( psStepAttributesInsert == null ) {
//      String sql =
//        database.getInsertStatement( KettleDatabaseRepository.TABLE_R_STEP_ATTRIBUTE, table.getRowMeta() );
//      psStepAttributesInsert = database.prepareSQL( sql );
//    }
//    database.setValues( table, psStepAttributesInsert );
//    database.insertRow( psStepAttributesInsert, useBatchProcessing );
//
//    if ( log.isDebug() ) {
//      log.logDebug( "saved attribute [" + code + "]" );
//    }

    return id;
  }

  public synchronized ObjectId insertTransAttribute( ObjectId id_transformation, long nr, String code,
    long value_num, String value_str ) throws KettleException {
    ObjectId id = getNextTransAttributeID();

    RowMetaAndData table = new RowMetaAndData();

    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANS_ATTRIBUTE, ValueMetaInterface.TYPE_INTEGER ), id );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_ID_TRANSFORMATION, ValueMetaInterface.TYPE_INTEGER ), id_transformation );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_NR, ValueMetaInterface.TYPE_INTEGER ), new Long( nr ) );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_NUM, ValueMetaInterface.TYPE_INTEGER ), new Long( value_num ) );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_TRANS_ATTRIBUTE_VALUE_STR, ValueMetaInterface.TYPE_STRING ), value_str );


    getDatabase().insertTableRow(KettleDatabaseRepository.TABLE_R_TRANS_ATTRIBUTE, table);

    if ( log.isDebug() ) {
      log.logDebug( "saved transformation attribute [" + code + "]" );
    }

    return id;
  }

  public synchronized ObjectId insertJobAttribute( ObjectId id_job, long nr, String code, long value_num,
    String value_str ) throws KettleException {
    ObjectId id = getNextJobAttributeID();

    // System.out.println("Insert job attribute : id_job="+id_job+", code="+code+", value_str="+value_str);

    RowMetaAndData table = new RowMetaAndData();

    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB_ATTRIBUTE, ValueMetaInterface.TYPE_INTEGER ), id );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_ID_JOB, ValueMetaInterface.TYPE_INTEGER ), id_job );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_NR, ValueMetaInterface.TYPE_INTEGER ), new Long( nr ) );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_CODE, ValueMetaInterface.TYPE_STRING ), code );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_NUM, ValueMetaInterface.TYPE_INTEGER ), new Long( value_num ) );
    table.addValue( new ValueMeta( KettleDatabaseRepository.FIELD_JOB_ATTRIBUTE_VALUE_STR, ValueMetaInterface.TYPE_STRING ), value_str );

    getDatabase().insertTableRow(KettleDatabaseRepository.TABLE_R_JOB_ATTRIBUTE, table);

    if ( log.isDebug() ) {
      log.logDebug( "saved job attribute [" + code + "]" );
    }

    return id;
  }

  public void updateTableRow( String tablename, String idfield, RowMetaAndData values, ObjectId id ) throws KettleException {
		String[] sets = new String[values.size()];
		for (int i = 0; i < values.size(); i++) {
			sets[i] = values.getValueMeta(i).getName();
		}
		String[] codes = new String[] { idfield };
		String[] condition = new String[] { "=" };


		StringBuffer sql = new StringBuffer(128);
		String schemaTable = databaseMeta.getQuotedSchemaTableCombination(null, tablename);
		sql.append("UPDATE ").append(schemaTable).append(Const.CR).append("SET ");
		for (int i = 0; i < sets.length; i++) {
			if (i != 0) {
				sql.append(",   ");
			}
			sql.append(databaseMeta.quoteField(sets[i]));
			sql.append(" = ?").append(Const.CR);
		}
		sql.append("WHERE ");
		for (int i = 0; i < codes.length; i++) {
			if (i != 0) {
				sql.append("AND   ");
			}
			sql.append(databaseMeta.quoteField(codes[i]));
			if ("BETWEEN".equalsIgnoreCase(condition[i])) {
				sql.append(" BETWEEN ? AND ? ");
			} else if ("IS NULL".equalsIgnoreCase(condition[i]) || "IS NOT NULL".equalsIgnoreCase(condition[i])) {
				sql.append(' ').append(condition[i]).append(' ');
			} else {
				sql.append(' ').append(condition[i]).append(" ? ");
			}
		}

		String s = sql.toString();
		if (log.isDetailed()) {
			log.logDetailed("Setting update preparedStatement to [" + s + "]");
		}
		String updateSql = databaseMeta.stripCR(s);

		Object[] data = values.getData(), args = new Object[values.getRowMeta().size() + 1];
		for(int i=0; i<values.getRowMeta().size(); i++)
			args[i] = data[i];
		args[values.getRowMeta().size()] = id.getId();
		getJdbcTemplate().update(updateSql, args);





//    database.prepareUpdate( tablename, codes, condition, sets );
//
//    values.addValue( new ValueMeta( idfield, ValueMetaInterface.TYPE_INTEGER ), id );
//
//    database.setValuesUpdate( values.getRowMeta(), values.getData() );
//    database.updateRow();
//    database.closeUpdate();
  }

	public synchronized void updateTableRow(String tablename, String idfield, RowMetaAndData values) throws KettleException {
		long id = values.getInteger(idfield, 0L);
		values.removeValue(idfield);

		this.updateTableRow(tablename, idfield, values, new LongObjectId(id));

	}

  /**
   * @param id_directory
   * @return A list of RepositoryObjects
   *
   * @throws KettleException
   */
	public synchronized List<RepositoryElementMetaInterface> getRepositoryObjects(String tableName,
			RepositoryObjectType objectType, ObjectId id_directory) throws KettleException {
		long start = System.currentTimeMillis();
		String idField;
		if (RepositoryObjectType.TRANSFORMATION.equals(objectType)) {
			idField = KettleDatabaseRepository.FIELD_TRANSFORMATION_ID_TRANSFORMATION;
		} else {
			idField = KettleDatabaseRepository.FIELD_JOB_ID_JOB;
		}
		if (id_directory == null) {
			id_directory = new LongObjectId(0L);
		}

		RepositoryDirectoryInterface repositoryDirectory = repository.directoryDelegate.loadPathToRoot(id_directory);

		String sql = "SELECT " + quote(KettleDatabaseRepository.FIELD_TRANSFORMATION_NAME) + ", "
				+ quote(KettleDatabaseRepository.FIELD_TRANSFORMATION_MODIFIED_USER) + ", "
				+ quote(KettleDatabaseRepository.FIELD_TRANSFORMATION_MODIFIED_DATE) + ", "
				+ quote(KettleDatabaseRepository.FIELD_TRANSFORMATION_DESCRIPTION) + ", " + quote(idField) + " "
				+ "FROM " + tableName + " " + "WHERE "
				+ quote(KettleDatabaseRepository.FIELD_TRANSFORMATION_ID_DIRECTORY) + " = ? ";

		Object[] args = { Long.valueOf(id_directory.getId()) };
		List<RepositoryElementMetaInterface> list = getJdbcTemplate().query(sql, args, new ResultSetExtractor<List<RepositoryElementMetaInterface>>() {

			@Override
			public List<RepositoryElementMetaInterface> extractData(ResultSet rs)
					throws SQLException, DataAccessException {
				List<RepositoryElementMetaInterface> repositoryObjects = new ArrayList<RepositoryElementMetaInterface>();
				try {

					RowMetaInterface rowMeta = database.getRowInfo(rs.getMetaData(), false, false);
					ArrayList<Object[]> rows = new ArrayList<Object[]>();

					Object[] data = database.getRow(rs, null, rowMeta);
					while (data != null) {
						rows.add(data);
						data = database.getRow(rs, null, rowMeta);
					}

					for (Object[] r : rows) {
						ObjectId id = new LongObjectId(rowMeta.getInteger(r, 4));
						RepositoryObject repositoryObject = new RepositoryObject(id, rowMeta.getString(r, 0),
								repositoryDirectory, rowMeta.getString(r, 1), rowMeta.getDate(r, 2), objectType,
								rowMeta.getString(r, 3), false);
						repositoryObjects.add(repositoryObject);
					}

					return repositoryObjects;
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
					return null;
				} catch (KettleValueException e) {
					e.printStackTrace();
					return null;
				}
			}

		});

//		log.logBasic("getRepositoryObjects - sql["+ sql +  "]: " + (System.currentTimeMillis() - start) );
		return list;
	}

	public ObjectId[] getIDs(String sql, ObjectId... objectId) throws KettleException {
//		long start = System.currentTimeMillis();

		Object[] args = new Object[objectId.length];
		for (int i = 0; i < objectId.length; i++) {
			args[i] = ((LongObjectId) objectId[i]).longValue();
		}

		ObjectId[] ids = getJdbcTemplate().query(sql, args, new ResultSetExtractor<ObjectId[]>() {
			@Override
			public ObjectId[] extractData(ResultSet rs) throws SQLException, DataAccessException {
				try {

					RowMetaInterface rowMeta = database.getRowInfo(rs.getMetaData(), false, false);
					ArrayList<Object[]> rows = new ArrayList<Object[]>();

					Object[] data = database.getRow(rs, null, rowMeta);
					while (data != null) {
						rows.add(data);
						data = database.getRow(rs, null, rowMeta);
					}

					if (Const.isEmpty(rows)) {
						return new ObjectId[0];
					}

					ObjectId[] ids = new ObjectId[rows.size()];
					for (int i = 0; i < ids.length; i++) {
						Object[] row = rows.get(i);
						ids[i] = new LongObjectId(rowMeta.getInteger(row, 0));
					}

					return ids;
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
					return null;
				} catch (KettleValueException e) {
					e.printStackTrace();
					return null;
				}
			}

		});

//		log.logBasic("getIDs - sql["+ sql +  "]: " + (System.currentTimeMillis() - start) );
		return ids;

	}

  public String[] getStrings( String sql, ObjectId... objectId ) throws KettleException {

		Object[] args = new Object[objectId.length];
		for (int i = 0; i < objectId.length; i++) {
			args[i] = ((LongObjectId) objectId[i]).longValue();
		}

	  return getJdbcTemplate().query(sql, args, new ResultSetExtractor<String[]>() {

			@Override
			public String[] extractData(ResultSet rs) throws SQLException, DataAccessException {
				String[] result = null;

				if (rs != null) {
					try {

						RowMetaInterface rowMeta = database.getRowInfo(rs.getMetaData(), false, false);
						ArrayList<Object[]> rows = new ArrayList<Object[]>();

						Object[] data = database.getRow(rs, null, rowMeta);
						while (data != null) {
							rows.add(data);
							data = database.getRow(rs, null, rowMeta);
						}

						int count = 0;
						result = new String[rows.size()];
						for(Object[] row : rows) {
							result[count++] = rowMeta.getString( row, 0 );
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					result = new String[0];
				}

				return result;
			}

		});
  }

  public static final ObjectId[] convertLongList( List<Long> list ) {
    ObjectId[] ids = new ObjectId[list.size()];
    for ( int i = 0; i < ids.length; i++ ) {
      ids[i] = new LongObjectId( list.get( i ) );
    }
    return ids;
  }

  private String[] getQuotedSchemaTablenames( String[] tables ) {
    String[] quoted = new String[tables.length];
    for ( int i = 0; i < quoted.length; i++ ) {
      quoted[i] = database.getDatabaseMeta().getQuotedSchemaTableCombination( null, tables[i] );
    }
    return quoted;
  }

//  public synchronized void lockRepository() throws KettleException {
//    if ( database.getDatabaseMeta().needsToLockAllTables() ) {
//      database.lockTables( getQuotedSchemaTablenames( KettleDatabaseRepository.repositoryTableNames ) );
//    } else {
//      database
//        .lockTables( getQuotedSchemaTablenames( new String[] { KettleDatabaseRepository.TABLE_R_REPOSITORY_LOG, } ) );
//    }
//  }
//
//  public synchronized void unlockRepository() throws KettleException {
//    if ( database.getDatabaseMeta().needsToLockAllTables() ) {
//      database.unlockTables( KettleDatabaseRepository.repositoryTableNames );
//    } else {
//      database.unlockTables( new String[] { KettleDatabaseRepository.TABLE_R_REPOSITORY_LOG, } );
//    }
//  }

  /**
   * @return the stepAttributesRowMeta
   */
  public RowMetaInterface getStepAttributesRowMeta() {
    return stepAttributesRowMeta;
  }

  public boolean isUseBatchProcessing() {
    return useBatchProcessing;
  }

  /**
   * @param stepAttributesRowMeta
   *          the stepAttributesRowMeta to set
   */
  public void setStepAttributesRowMeta( RowMetaInterface stepAttributesRowMeta ) {
    this.stepAttributesRowMeta = stepAttributesRowMeta;
  }

  public synchronized LongObjectId getIDWithValue( String tablename, String idfield, String lookupfield,
    String value ) throws KettleException {
    RowMetaAndData par = new RowMetaAndData();
    par.addValue( new ValueMeta( "value", ValueMetaInterface.TYPE_STRING ), value );
    RowMetaAndData result =
      getOneRow(
        "SELECT " + idfield + " FROM " + tablename + " WHERE " + lookupfield + " = ?", par.getRowMeta(), par
          .getData() );

    if ( result != null && result.getRowMeta() != null && result.getData() != null && result.isNumeric( 0 ) ) {
      return new LongObjectId( result.getInteger( 0, 0 ) );
    }
    return null;
  }

  static String createIdsWithValuesQuery( String tablename, String idfield, String lookupfield, int amount ) {
    StringBuilder sb = new StringBuilder( 128 );
    sb.append( "SELECT " ).append( idfield )
      .append( " FROM " ).append( tablename )
      .append( " WHERE " ).append( lookupfield )
      .append( " IN (" );
    for ( int i = 0; i < amount; i++ ) {
      sb.append( '?' ).append( ',' );
    }
    sb.setCharAt( sb.length() - 1, ')' );
    return sb.toString();
  }

  public Map<String, LongObjectId> getValueToIdMap( String tablename, String idfield, String lookupfield ) throws KettleException {
    String sql = new StringBuilder( "SELECT " ).append( lookupfield ).append( ", " ).append( idfield )
      .append( " FROM " ).append( tablename ).toString();

    Map<String, LongObjectId> result = new HashMap<String, LongObjectId>();
    for ( Object[] row : database.getRows( sql, new RowMeta(), new Object[]{}, ResultSet.FETCH_FORWARD, false, -1, null ) ) {
      result.put( String.valueOf( row[0] ), new LongObjectId( ( (Number) row[ 1 ] ).longValue() ) );
    }
    return result;
  }

  public LongObjectId[] getIDsWithValues( String tablename, String idfield, String lookupfield,
                                          String[] values ) throws KettleException {
    String sql = createIdsWithValuesQuery( tablename, idfield, lookupfield, values.length );

    RowMeta params = new RowMeta();
    for ( int i = 0; i < values.length; i++ ) {
      ValueMeta value = new ValueMeta( Integer.toString( i ), ValueMetaInterface.TYPE_STRING );
      params.addValueMeta( value );
    }

    List<Object[]> rows = database.getRows( sql, params, values, ResultSet.FETCH_FORWARD, false, -1, null );

    LongObjectId[] result = new LongObjectId[ rows.size() ];
    int i = 0;
    for ( Object[] row : rows ) {
      result[ i++ ] = new LongObjectId( ( (Number) row[ 0 ] ).longValue() );
    }
    return result;
  }

  public synchronized ObjectId getIDWithValue( String tablename, String idfield, String lookupfield, String value,
    String lookupkey, ObjectId key ) throws KettleException {


	  long start = System.currentTimeMillis();
	  String sql = "SELECT " + idfield + " FROM " + tablename + " WHERE " + lookupfield + " = ? AND " + lookupkey + " = ?";
//		String sql = "SELECT * FROM " + schemaAndTable + " WHERE " + keyfield + " = ?";
		Object[] args = new Object[] { value, Integer.parseInt(key.getId()) };
		ObjectId id = getJdbcTemplate().query(sql, args, new ResultSetExtractor<ObjectId>() {

			@Override
			public ObjectId extractData(ResultSet rs) throws SQLException, DataAccessException {
				try {
					RowMetaInterface rowInfo = database.getRowInfo(rs.getMetaData(), false, false);
					Object[] result = database.getRow(rs, null, rowInfo);
					if(result == null) {
						System.err.println("sql： " + sql + " - [value: " + value + ", key: " + key + "].");
						return null;
					}

					RowMetaAndData rmad = new RowMetaAndData(rowInfo, result);
					Long i = rmad.getInteger(0);
					return new LongObjectId(i);
				} catch (KettleException e) {
					e.printStackTrace();
					return null;
				}
			}

		});
//		log.logBasic("getIDWithValue - sql["+ sql +  "]: " +   + (System.currentTimeMillis() - start) );

	  return id;
//    RowMetaAndData par = new RowMetaAndData();
//    par.addValue( new ValueMeta( "value", ValueMetaInterface.TYPE_STRING ), value );
//    par.addValue( new ValueMeta( "key", ValueMetaInterface.TYPE_INTEGER ), new LongObjectId( key ) );
//    RowMetaAndData result =
//      getOneRow( "SELECT "
//        + idfield + " FROM " + tablename + " WHERE " + lookupfield + " = ? AND " + lookupkey + " = ?", par
//        .getRowMeta(), par.getData() );
//
//    if ( result != null && result.getRowMeta() != null && result.getData() != null && result.isNumeric( 0 ) ) {
//      return new LongObjectId( result.getInteger( 0, 0 ) );
//    }
//
//    return null;
  }

  public synchronized ObjectId getIDWithValue( String tablename, String idfield, String[] lookupkey, ObjectId[] key ) throws KettleException {
    RowMetaAndData par = new RowMetaAndData();
    String sql = "SELECT " + idfield + " FROM " + tablename + " ";

    for ( int i = 0; i < lookupkey.length; i++ ) {
      if ( i == 0 ) {
        sql += "WHERE ";
      } else {
        sql += "AND   ";
      }
      par.addValue( new ValueMeta( lookupkey[i], ValueMetaInterface.TYPE_INTEGER ), new LongObjectId( key[i] ) );
      sql += lookupkey[i] + " = ? ";
    }
    RowMetaAndData result = getOneRow( sql, par.getRowMeta(), par.getData() );
    if ( result != null && result.getRowMeta() != null && result.getData() != null && result.isNumeric( 0 ) ) {
      return new LongObjectId( result.getInteger( 0, 0 ) );
    }
    return null;
  }

  public synchronized LongObjectId getIDWithValue( String tablename, String idfield, String lookupfield,
    String value, String[] lookupkey, ObjectId[] key ) throws KettleException {
    RowMetaAndData par = new RowMetaAndData();
    par.addValue( new ValueMeta( lookupfield, ValueMetaInterface.TYPE_STRING ), value );

    String sql = "SELECT " + idfield + " FROM " + tablename + " WHERE " + lookupfield + " = ? ";

    for ( int i = 0; i < lookupkey.length; i++ ) {
      par.addValue( new ValueMeta( lookupkey[i], ValueMetaInterface.TYPE_INTEGER ), new LongObjectId( key[i] ) );
      sql += "AND " + lookupkey[i] + " = ? ";
    }

    RowMetaAndData result = getOneRow( sql, par.getRowMeta(), par.getData() );
    if ( result != null && result.getRowMeta() != null && result.getData() != null && result.isNumeric( 0 ) ) {
      return new LongObjectId( result.getInteger( 0, 0 ) );
    }
    return null;
  }

  /**
   * This method should be called WITH AN ALREADY QUOTED schema and table
   */
	public RowMetaAndData getOneRow(String schemaAndTable, String keyfield, ObjectId id) throws KettleException {
		long start = System.currentTimeMillis();
		String sql = "SELECT * FROM " + schemaAndTable + " WHERE " + keyfield + " = ?";
		Object[] args = new Object[] { id != null ? Long.parseLong(id.getId()) : null };
		RowMetaAndData rmad = getJdbcTemplate().query(sql, args, new ResultSetExtractor<RowMetaAndData>() {

			@Override
			public RowMetaAndData extractData(ResultSet rs) throws SQLException, DataAccessException {
				try {

					RowMetaInterface rowInfo = database.getRowInfo(rs.getMetaData(), false, false);
					Object[] result = database.getRow(rs, null, rowInfo);

					if (result == null) {
						return new RowMetaAndData(rowInfo, RowDataUtil.allocateRowData(rowInfo.size()));
					}

					RowMetaAndData rmad = new RowMetaAndData(rowInfo, result);
					return rmad;
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
					return null;
				}
			}

		});
//		log.logBasic("getOneRow - sql["+ sql +  "]: " +   + (System.currentTimeMillis() - start) );
		return rmad;
	}

  public RowMetaAndData getOneRow( String sql ) throws KettleDatabaseException {
    return database.getOneRow( sql );
  }

  public RowMetaAndData getOneRow( String sql, RowMetaInterface rowMeta, Object[] rowData ) throws KettleDatabaseException {
    return database.getOneRow( sql, rowMeta, rowData );
  }

  public synchronized String getStringWithID( String tablename, String keyfield, ObjectId id, String fieldname ) throws KettleException {
    String sql = "SELECT " + fieldname + " FROM " + tablename + " WHERE " + keyfield + " = ?";
    RowMetaAndData par = new RowMetaAndData();
    par.addValue( new ValueMeta( keyfield, ValueMetaInterface.TYPE_INTEGER ), id );
    RowMetaAndData result = getOneRow( sql, par.getRowMeta(), par.getData() );
    if ( result != null && result.getData() != null ) {
      return result.getString( 0, null );
    }
    return null;
  }

  public List<Object[]> getRows( String sql, int limit ) throws KettleDatabaseException {
    return database.getRows( sql, limit );
  }

  public RowMetaInterface getReturnRowMeta() throws KettleDatabaseException {
    return database.getReturnRowMeta();
  }

  public Collection<RowMetaAndData> getDatabaseAttributes( ObjectId id_database ) throws KettleDatabaseException,  KettleValueException {
	  	long start = System.currentTimeMillis();
		String sql = "SELECT * FROM " + quoteTable(KettleDatabaseRepository.TABLE_R_DATABASE_ATTRIBUTE) + " WHERE "
				+ quote(KettleDatabaseRepository.FIELD_DATABASE_ID_DATABASE) + " = ?";

    Object[] args = new Object[] { ((LongObjectId)id_database).longValue() };

    Collection<RowMetaAndData> list = getJdbcTemplate().query(sql, args, new ResultSetExtractor<Collection<RowMetaAndData>>() {
		@Override
		public Collection<RowMetaAndData> extractData(ResultSet rs) throws SQLException, DataAccessException {
			try {
				RowMetaInterface rowMeta = database.getRowInfo(rs.getMetaData(), false, false);
				ArrayList<Object[]> rows = new ArrayList<Object[]>();

				Object[] data = database.getRow(rs, null, rowMeta);
				while (data != null) {
					rows.add(data);
					data = database.getRow(rs, null, rowMeta);
				}

				List<RowMetaAndData> attrs = new ArrayList<RowMetaAndData>();
				for (Object[] row : rows) {
					RowMetaAndData rowWithMeta = new RowMetaAndData(rowMeta, row);
					long id = rowWithMeta.getInteger(quote(KettleDatabaseRepository.FIELD_DATABASE_ATTRIBUTE_ID_DATABASE_ATTRIBUTE), 0);
					if (id > 0) {
						attrs.add(rowWithMeta);
					}
				}

				return attrs;
			} catch(Exception e) {
				e.printStackTrace();
			}

			return null;
		}
    });

//    log.logBasic("getDatabaseAttributes - sql["+ sql +  "]: " +   + (System.currentTimeMillis() - start) );

    return list;
  }

  public RowMetaAndData getParameterMetaData( ObjectId... ids ) throws KettleException {
    RowMetaInterface parameterMeta = new RowMeta();
    Object[] parameterData = new Object[ids.length];
    for ( int i = 0; i < ids.length; i++ ) {
      parameterMeta.addValueMeta( new ValueMeta( "id" + ( i + 1 ), ValueMetaInterface.TYPE_INTEGER ) );
      parameterData[i] = Long.valueOf( ids[i].getId() );
    }
    return new RowMetaAndData( parameterMeta, parameterData );
  }

  public void performDelete( String sql, ObjectId... ids ) throws KettleException {
//    try {
//      PreparedStatement ps = sqlMap.get( sql );
//      if ( ps == null ) {
//        ps = database.prepareSQL( sql );
//        sqlMap.put( sql, ps );
//      }
//
//      RowMetaAndData param = getParameterMetaData( ids );
//      database.setValues( param, ps );
//      ps.execute();
//    } catch ( SQLException e ) {
//      throw new KettleException( "Unable to perform delete with SQL: " + sql + ", ids=" + ids.toString(), e );
//    }

	  Object[] args = new Object[ids.length];
	  for(int i=0; i<ids.length; i++)
		  args[i] = ids[i].getId();

	  getJdbcTemplate().update(sql, args);

  }

  public void closeAttributeLookupPreparedStatements() throws KettleException {
    closeLookupJobEntryAttribute();
  }

  /**
   * A MySQL InnoDB hack really... Doesn't like a lock in case there's been a read in another session. It considers it
   * an open transaction.
   *
   * @throws KettleDatabaseException
   */
  public void closeReadTransaction() throws KettleDatabaseException {
//    if ( databaseMeta.isMySQLVariant() ) {
//      database.commit();
//    }
  }
}
