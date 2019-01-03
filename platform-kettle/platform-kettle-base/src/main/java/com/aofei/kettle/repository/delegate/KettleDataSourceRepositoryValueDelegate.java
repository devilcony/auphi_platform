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

package com.aofei.kettle.repository.delegate;

import com.aofei.kettle.repository.KettleDataSourceRepository;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;

public class KettleDataSourceRepositoryValueDelegate extends KettleDataSourceRepositoryBaseDelegate {

  // private static Class<?> PKG = ValueMetaAndData.class; // for i18n purposes, needed by Translator2!!

  public KettleDataSourceRepositoryValueDelegate( KettleDataSourceRepository repository ) {
    super( repository );
  }

  public RowMetaAndData getValue( ObjectId id_value ) throws KettleException {
    return repository.connectionDelegate.getOneRow(
      quoteTable( KettleDatabaseRepository.TABLE_R_VALUE ),
      quote( KettleDatabaseRepository.FIELD_VALUE_ID_VALUE ), id_value );
  }

  public ValueMetaAndData loadValueMetaAndData( ObjectId id_value ) throws KettleException {
    ValueMetaAndData valueMetaAndData = new ValueMetaAndData();
    try {
      RowMetaAndData r = getValue( id_value );
      if ( r != null ) {
        String name = r.getString( KettleDatabaseRepository.FIELD_VALUE_NAME, null );
        int valtype = ValueMeta.getType( r.getString( KettleDatabaseRepository.FIELD_VALUE_VALUE_TYPE, null ) );
        boolean isNull = r.getBoolean( KettleDatabaseRepository.FIELD_VALUE_IS_NULL, false );
        valueMetaAndData.setValueMeta( new ValueMeta( name, valtype ) );

        if ( isNull ) {
          valueMetaAndData.setValueData( null );
        } else {
          ValueMetaInterface stringValueMeta = new ValueMeta( name, ValueMetaInterface.TYPE_STRING );
          ValueMetaInterface valueMeta = valueMetaAndData.getValueMeta();
          stringValueMeta.setConversionMetadata( valueMeta );

          valueMeta.setDecimalSymbol( ValueMetaAndData.VALUE_REPOSITORY_DECIMAL_SYMBOL );
          valueMeta.setGroupingSymbol( ValueMetaAndData.VALUE_REPOSITORY_GROUPING_SYMBOL );

          switch ( valueMeta.getType() ) {
            case ValueMetaInterface.TYPE_NUMBER:
              valueMeta.setConversionMask( ValueMetaAndData.VALUE_REPOSITORY_NUMBER_CONVERSION_MASK );
              break;
            case ValueMetaInterface.TYPE_INTEGER:
              valueMeta.setConversionMask( ValueMetaAndData.VALUE_REPOSITORY_INTEGER_CONVERSION_MASK );
              break;
            default:
              break;
          }

          String string = r.getString( "VALUE_STR", null );
          valueMetaAndData.setValueData( stringValueMeta.convertDataUsingConversionMetaData( string ) );

          // OK, now comes the dirty part...
          // We want the defaults back on there...
          //
          valueMeta = new ValueMeta( name, valueMeta.getType() );
        }
      }

      return valueMetaAndData;
    } catch ( KettleException dbe ) {
      throw new KettleException( "Unable to load Value from repository with id_value=" + id_value, dbe );
    }
  }

}
