error id: jar:file:///run/user/17608/.cache/coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/UnionListWriter.java
jar:file:///run/user/17608/.cache/coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/UnionListWriter.java
### java.lang.Exception: Unexpected symbol '#' at word pos: '35' Line: '<#list  ["List", "LargeList"] as listName>'

Java indexer failed with and exception.
```Java
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.vector.complex.writer.DecimalWriter;
import org.apache.arrow.vector.holders.DecimalHolder;

import java.lang.UnsupportedOperationException;
import java.math.BigDecimal;

<@pp.dropOutputFile />
<#list ["List", "LargeList"] as listName>

<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/Union${listName}Writer.java" />

<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

import static org.apache.arrow.memory.util.LargeMemoryUtil.checkedCastToInt;
<#include "/@includes/vv_imports.ftl" />

/*
 * This class is generated using freemarker and the ${.template_name} template.
 */

@SuppressWarnings("unused")
public class Union${listName}Writer extends AbstractFieldWriter {

  protected ${listName}Vector vector;
  protected PromotableWriter writer;
  private boolean inStruct = false;
  private String structName;
  <#if listName == "LargeList">
  private static final long OFFSET_WIDTH = 8;
  <#else>
  private static final int OFFSET_WIDTH = 4;
  </#if>

  public Union${listName}Writer(${listName}Vector vector) {
    this(vector, NullableStructWriterFactory.getNullableStructWriterFactoryInstance());
  }

  public Union${listName}Writer(${listName}Vector vector, NullableStructWriterFactory nullableStructWriterFactory) {
    this.vector = vector;
    this.writer = new PromotableWriter(vector.getDataVector(), vector, nullableStructWriterFactory);
  }

  public Union${listName}Writer(${listName}Vector vector, AbstractFieldWriter parent) {
    this(vector);
  }

  @Override
  public void allocate() {
    vector.allocateNew();
  }

  @Override
  public void clear() {
    vector.clear();
  }

  @Override
  public Field getField() {
    return vector.getField();
  }

  public void setValueCount(int count) {
    vector.setValueCount(count);
  }

  @Override
  public int getValueCapacity() {
    return vector.getValueCapacity();
  }

  @Override
  public void close() throws Exception {
    vector.close();
    writer.close();
  }

  @Override
  public void setPosition(int index) {
    super.setPosition(index);
  }

  <#list vv.types as type><#list type.minor as minor><#assign name = minor.class?cap_first />
  <#assign fields = minor.fields!type.fields />
  <#assign uncappedName = name?uncap_first/>
  <#if uncappedName == "int" ><#assign uncappedName = "integer" /></#if>
  <#if !minor.typeParams?? >

  @Override
  public ${name}Writer ${uncappedName}() {
    return this;
  }

  @Override
  public ${name}Writer ${uncappedName}(String name) {
    structName = name;
    return writer.${uncappedName}(name);
  }
  </#if>
  </#list></#list>

  @Override
  public DecimalWriter decimal() {
    return this;
  }

  @Override
  public DecimalWriter decimal(String name, int scale, int precision) {
    return writer.decimal(name, scale, precision);
  }

  @Override
  public DecimalWriter decimal(String name) {
    return writer.decimal(name);
  }

  @Override
  public StructWriter struct() {
    inStruct = true;
    return this;
  }

  @Override
  public ListWriter list() {
    return writer;
  }

  @Override
  public ListWriter list(String name) {
    ListWriter listWriter = writer.list(name);
    return listWriter;
  }

  @Override
  public StructWriter struct(String name) {
    StructWriter structWriter = writer.struct(name);
    return structWriter;
  }

  <#if listName == "LargeList">
  @Override
  public void startList() {
    vector.startNewValue(idx());
    writer.setPosition(checkedCastToInt(vector.getOffsetBuffer().getLong(((long) idx() + 1L) * OFFSET_WIDTH)));
  }

  @Override
  public void endList() {
    vector.getOffsetBuffer().setLong(((long) idx() + 1L) * OFFSET_WIDTH, writer.idx());
    setPosition(idx() + 1);
  }
  <#else>
  @Override
  public void startList() {
    vector.startNewValue(idx());
    writer.setPosition(vector.getOffsetBuffer().getInt((idx() + 1) * OFFSET_WIDTH));
  }

  @Override
  public void endList() {
    vector.getOffsetBuffer().setInt((idx() + 1) * OFFSET_WIDTH, writer.idx());
    setPosition(idx() + 1);
  }
  </#if>

  @Override
  public void start() {
    writer.start();
  }

  @Override
  public void end() {
    writer.end();
    inStruct = false;
  }

  @Override
  public void write(DecimalHolder holder) {
    writer.write(holder);
    writer.setPosition(writer.idx()+1);
  }

  @Override
  public void writeNull() {
    writer.writeNull();
  }

  public void writeDecimal(int start, ArrowBuf buffer, ArrowType arrowType) {
    writer.writeDecimal(start, buffer, arrowType);
    writer.setPosition(writer.idx()+1);
  }

  public void writeDecimal(int start, ArrowBuf buffer) {
    writer.writeDecimal(start, buffer);
    writer.setPosition(writer.idx()+1);
  }

  public void writeDecimal(BigDecimal value) {
    writer.writeDecimal(value);
    writer.setPosition(writer.idx()+1);
  }

  public void writeBigEndianBytesToDecimal(byte[] value, ArrowType arrowType){
    writer.writeBigEndianBytesToDecimal(value, arrowType);
    writer.setPosition(writer.idx() + 1);
  }

  <#list vv.types as type>
    <#list type.minor as minor>
      <#assign name = minor.class?cap_first />
      <#assign fields = minor.fields!type.fields />
      <#assign uncappedName = name?uncap_first/>
      <#if !minor.typeParams?? >
  @Override
  public void write${name}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>) {
    writer.write${name}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>);
    writer.setPosition(writer.idx()+1);
  }

  public void write(${name}Holder holder) {
    writer.write${name}(<#list fields as field>holder.${field.name}<#if field_has_next>, </#if></#list>);
    writer.setPosition(writer.idx()+1);
  }

      </#if>
    </#list>
  </#list>
}
</#list>

```


#### Error stacktrace:

```
scala.meta.internal.mtags.JavaToplevelMtags.unexpectedCharacter(JavaToplevelMtags.scala:349)
	scala.meta.internal.mtags.JavaToplevelMtags.parseToken$1(JavaToplevelMtags.scala:250)
	scala.meta.internal.mtags.JavaToplevelMtags.fetchToken(JavaToplevelMtags.scala:259)
	scala.meta.internal.mtags.JavaToplevelMtags.loop(JavaToplevelMtags.scala:70)
	scala.meta.internal.mtags.JavaToplevelMtags.indexRoot(JavaToplevelMtags.scala:39)
	scala.meta.internal.mtags.MtagsIndexer.index(MtagsIndexer.scala:21)
	scala.meta.internal.mtags.MtagsIndexer.index$(MtagsIndexer.scala:20)
	scala.meta.internal.mtags.JavaToplevelMtags.index(JavaToplevelMtags.scala:15)
	scala.meta.internal.mtags.Mtags.indexWithOverrides(Mtags.scala:74)
	scala.meta.internal.mtags.SymbolIndexBucket.indexSource(SymbolIndexBucket.scala:124)
	scala.meta.internal.mtags.SymbolIndexBucket.addSourceFile(SymbolIndexBucket.scala:107)
	scala.meta.internal.mtags.SymbolIndexBucket.$anonfun$addSourceJar$2(SymbolIndexBucket.scala:73)
	scala.collection.immutable.List.flatMap(List.scala:294)
	scala.meta.internal.mtags.SymbolIndexBucket.$anonfun$addSourceJar$1(SymbolIndexBucket.scala:69)
	scala.meta.internal.io.PlatformFileIO$.withJarFileSystem(PlatformFileIO.scala:77)
	scala.meta.internal.io.FileIO$.withJarFileSystem(FileIO.scala:33)
	scala.meta.internal.mtags.SymbolIndexBucket.addSourceJar(SymbolIndexBucket.scala:67)
	scala.meta.internal.mtags.OnDemandSymbolIndex.$anonfun$addSourceJar$2(OnDemandSymbolIndex.scala:85)
	scala.meta.internal.mtags.OnDemandSymbolIndex.tryRun(OnDemandSymbolIndex.scala:131)
	scala.meta.internal.mtags.OnDemandSymbolIndex.addSourceJar(OnDemandSymbolIndex.scala:84)
	scala.meta.internal.metals.Indexer.indexJar(Indexer.scala:671)
	scala.meta.internal.metals.Indexer.addSourceJarSymbols(Indexer.scala:665)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$5(Indexer.scala:491)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.collection.IterableOps$WithFilter.foreach(Iterable.scala:905)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$1(Indexer.scala:482)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$1$adapted(Indexer.scala:481)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.meta.internal.metals.Indexer.indexDependencySources(Indexer.scala:481)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$17(Indexer.scala:302)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.meta.internal.metals.TimerProvider.timedThunk(TimerProvider.scala:25)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$16(Indexer.scala:295)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$16$adapted(Indexer.scala:291)
	scala.collection.immutable.List.foreach(List.scala:334)
	scala.meta.internal.metals.Indexer.indexWorkspace(Indexer.scala:291)
	scala.meta.internal.metals.Indexer.$anonfun$profiledIndexWorkspace$2(Indexer.scala:167)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.meta.internal.metals.TimerProvider.timedThunk(TimerProvider.scala:25)
	scala.meta.internal.metals.Indexer.$anonfun$profiledIndexWorkspace$1(Indexer.scala:167)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:687)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:467)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	java.base/java.lang.Thread.run(Thread.java:833)
```
#### Short summary: 

Java indexer failed with and exception.