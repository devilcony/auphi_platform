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

package com.aofei.kettle.repository.delegate.metastore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aofei.kettle.repository.delegate.KettleDataSourceRepositoryMetaStoreDelegate;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.metastore.api.IMetaStoreAttribute;

public class KDBRMetaStoreAttribute implements IMetaStoreAttribute {

  protected KettleDataSourceRepositoryMetaStoreDelegate delegate;
  private LongObjectId objectId;

  private String id;
  private Object value;

  private List<IMetaStoreAttribute> children;

  public KDBRMetaStoreAttribute( KettleDataSourceRepositoryMetaStoreDelegate delegate ) {
    this.children = new ArrayList<IMetaStoreAttribute>();
    this.delegate = delegate;
  }

  public KDBRMetaStoreAttribute( KettleDataSourceRepositoryMetaStoreDelegate delegate, String id, Object value ) {
    this( delegate );
    this.id = id;
    this.value = value;
  }

  public void setObjectId( LongObjectId objectId ) {
    this.objectId = objectId;
  }

  public LongObjectId getObjectId() {
    return objectId;
  }

  public KettleDataSourceRepositoryMetaStoreDelegate getDelegate() {
    return delegate;
  }

  public void setDelegate( KettleDataSourceRepositoryMetaStoreDelegate delegate ) {
    this.delegate = delegate;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = value;
  }

  public List<IMetaStoreAttribute> getChildren() {
    return children;
  }

  public void setChildren( List<IMetaStoreAttribute> children ) {
    this.children = children;
  }

  @Override
  public void addChild( IMetaStoreAttribute attribute ) {
    children.add( attribute );
  }

  @Override
  public void clearChildren() {
    children.clear();
  }

  @Override
  public void deleteChild( String attributeId ) {
    Iterator<IMetaStoreAttribute> iterator = children.iterator();
    while ( iterator.hasNext() ) {
      IMetaStoreAttribute attribute = iterator.next();
      if ( attribute.getId().equals( attributeId ) ) {
        iterator.remove();
        return;
      }
    }
  }

  @Override
  public IMetaStoreAttribute getChild( String id ) {
    Iterator<IMetaStoreAttribute> iterator = children.iterator();
    while ( iterator.hasNext() ) {
      IMetaStoreAttribute attribute = iterator.next();
      if ( attribute.getId().equals( id ) ) {
        return attribute;
      }
    }

    return null;
  }

}
