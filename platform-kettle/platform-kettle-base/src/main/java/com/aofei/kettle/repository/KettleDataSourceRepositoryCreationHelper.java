// CHECKSTYLE:FileLength:OFF
/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2013 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.aofei.kettle.repository;

import java.util.List;
import java.util.Map;

import com.aofei.kettle.core.database.Database;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.JobEntryPluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;

public class KettleDataSourceRepositoryCreationHelper {

  private final KettleDataSourceRepository repository;
  private final LogChannelInterface log;
  private final DatabaseMeta databaseMeta;
  private final Database database;

  private final PluginRegistry pluginRegistry;

  public KettleDataSourceRepositoryCreationHelper( KettleDataSourceRepository repository, Database database ) {
    this.repository = repository;
    this.databaseMeta = this.repository.getDatabaseMeta();
    this.database = database;

    this.log = repository.getLog();
    this.pluginRegistry = PluginRegistry.getInstance();
  }

  /**
   * Returns max VARCHAR length depending on db interface
   */
  protected int getRepoStringLength() {
    return database.getDatabaseMeta().getDatabaseInterface().getMaxVARCHARLength() - 1 > 0 ? database.getDatabaseMeta()
        .getDatabaseInterface().getMaxVARCHARLength() - 1 : KettleDatabaseRepository.REP_ORACLE_STRING_LENGTH;
  }

  /**
   * Update the list in R_STEP_TYPE using the StepLoader StepPlugin entries
   *
   * @throws KettleException
   *           if the update didn't go as planned.
   */
  public List<String> updateStepTypes( List<String> statements, boolean dryrun, boolean create ) throws KettleException {
    synchronized ( repository ) {

      // We should only do an update if something has changed...
      //
      List<PluginInterface> plugins = pluginRegistry.getPlugins( StepPluginType.class );
      ObjectId[] ids = loadPluginsIds( plugins, create );

      for ( int i = 0, idsLength = ids.length; i < idsLength; i++ ) {
        ObjectId id = ids[ i ];
        if ( id == null ) {
          // Not found, we need to add this one...

          if ( !create ) {
            id = repository.connectionDelegate.getNextStepTypeID();
          } else {
            id = new LongObjectId( i + 1 );
          }

          PluginInterface sp = plugins.get( i );

          RowMetaAndData table = new RowMetaAndData();
          table.addValue( new ValueMeta(
            KettleDatabaseRepository.FIELD_STEP_TYPE_ID_STEP_TYPE, ValueMetaInterface.TYPE_INTEGER ), id );
          table.addValue( new ValueMeta(
            KettleDatabaseRepository.FIELD_STEP_TYPE_CODE, ValueMetaInterface.TYPE_STRING ), sp.getIds()[0] );
          table
            .addValue( new ValueMeta(
              KettleDatabaseRepository.FIELD_STEP_TYPE_DESCRIPTION, ValueMetaInterface.TYPE_STRING ), sp
              .getName() );
          table.addValue( new ValueMeta(
            KettleDatabaseRepository.FIELD_STEP_TYPE_HELPTEXT, ValueMetaInterface.TYPE_STRING ), sp
            .getDescription() );

          if ( dryrun ) {
            String sql =
              database.getSQLOutput( null, KettleDatabaseRepository.TABLE_R_STEP_TYPE, table.getRowMeta(), table
                .getData(), null );
            statements.add( sql );
          } else {
//            database.prepareInsert( table.getRowMeta(), null, KettleDatabaseRepository.TABLE_R_STEP_TYPE );
//            database.setValuesInsert( table );
//            database.insertRow();
//            database.closeInsert();

            database.insertTableRow(KettleDatabaseRepository.TABLE_R_STEP_TYPE, table);
          }
        }
      }
    }
    return statements;
  }

  private ObjectId[] loadPluginsIds( List<PluginInterface> plugins, boolean create ) throws KettleException {
    ObjectId[] ids = new ObjectId[ plugins.size() ];
    if ( create ) {
      return ids;
    }

    Map<String, LongObjectId> stepTypeCodeToIdMap = repository.stepDelegate.getStepTypeCodeToIdMap();
    int index = 0;
    for ( PluginInterface sp : plugins ) {
      ids[index++] = stepTypeCodeToIdMap.get( sp.getIds()[0] );
    }

    return ids;
  }

  /**
   * Update the list in R_DATABASE_TYPE using the database plugin entries
   *
   * @throws KettleException
   *           if the update didn't go as planned.
   */
  public List<String> updateDatabaseTypes( List<String> statements, boolean dryrun, boolean create ) throws KettleException {
    synchronized ( repository ) {

      // We should only do an update if something has changed...
      //
      List<PluginInterface> plugins = pluginRegistry.getPlugins( DatabasePluginType.class );
      for ( int i = 0; i < plugins.size(); i++ ) {
        PluginInterface plugin = plugins.get( i );
        ObjectId id = null;
        if ( !create ) {
          id = repository.databaseDelegate.getDatabaseTypeID( plugin.getIds()[0] );
        }
        if ( id == null ) {
          // Not found, we need to add this one...

          // We need to add this one ...
          id = new LongObjectId( i + 1 );
          if ( !create ) {
            id = repository.connectionDelegate.getNextDatabaseTypeID();
          }

          RowMetaAndData table = new RowMetaAndData();
          table
            .addValue(
              new ValueMeta(
                KettleDatabaseRepository.FIELD_DATABASE_TYPE_ID_DATABASE_TYPE,
                ValueMetaInterface.TYPE_INTEGER ), id );
          table.addValue(
            new ValueMeta( KettleDatabaseRepository.FIELD_DATABASE_TYPE_CODE, ValueMetaInterface.TYPE_STRING ),
            plugin.getIds()[0] );
          table.addValue( new ValueMeta(
            KettleDatabaseRepository.FIELD_DATABASE_TYPE_DESCRIPTION, ValueMetaInterface.TYPE_STRING ), plugin
            .getName() );

          if ( dryrun ) {
            String sql =
              database.getSQLOutput(
                null, KettleDatabaseRepository.TABLE_R_DATABASE_TYPE, table.getRowMeta(), table.getData(),
                null );
            statements.add( sql );
          } else {
//            database.prepareInsert( table.getRowMeta(), null, KettleDatabaseRepository.TABLE_R_DATABASE_TYPE );
//            database.setValuesInsert( table );
//            database.insertRow();
//            database.closeInsert();
        	  database.insertTableRow(KettleDatabaseRepository.TABLE_R_DATABASE_TYPE, table);
          }
        }
      }
    }
    return statements;
  }

  /**
   * Update the list in R_JOBENTRY_TYPE
   *
   * @param create
   *
   * @exception KettleException
   *              if something went wrong during the update.
   */
  public void updateJobEntryTypes( List<String> statements, boolean dryrun, boolean create ) throws KettleException {
    synchronized ( repository ) {

      // We should only do an update if something has changed...
      PluginRegistry registry = PluginRegistry.getInstance();
      List<PluginInterface> jobPlugins = registry.getPlugins( JobEntryPluginType.class );

      for ( int i = 0; i < jobPlugins.size(); i++ ) {
        PluginInterface jobPlugin = jobPlugins.get( i );
        String type_desc = jobPlugin.getIds()[0];
        String type_desc_long = jobPlugin.getName();
        ObjectId id = null;
        if ( !create ) {
          id = repository.jobEntryDelegate.getJobEntryTypeID( type_desc );
        }
        if ( id == null ) {
          // Not found, we need to add this one...

          // We need to add this one ...
          id = new LongObjectId( i + 1 );
          if ( !create ) {
            id = repository.connectionDelegate.getNextJobEntryTypeID();
          }

          RowMetaAndData table = new RowMetaAndData();
          table
            .addValue(
              new ValueMeta(
                KettleDatabaseRepository.FIELD_JOBENTRY_TYPE_ID_JOBENTRY_TYPE,
                ValueMetaInterface.TYPE_INTEGER ), id );
          table.addValue( new ValueMeta(
            KettleDatabaseRepository.FIELD_JOBENTRY_TYPE_CODE, ValueMetaInterface.TYPE_STRING ), type_desc );
          table.addValue(
            new ValueMeta(
              KettleDatabaseRepository.FIELD_JOBENTRY_TYPE_DESCRIPTION, ValueMetaInterface.TYPE_STRING ),
            type_desc_long );

          if ( dryrun ) {
            String sql =
              database.getSQLOutput(
                null, KettleDatabaseRepository.TABLE_R_JOBENTRY_TYPE, table.getRowMeta(), table.getData(),
                null );
            statements.add( sql );
          } else {
        	  database.insertTableRow(KettleDatabaseRepository.TABLE_R_JOBENTRY_TYPE, table);
          }
        }
      }
    }
  }

}
