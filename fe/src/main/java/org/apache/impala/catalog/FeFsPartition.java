// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.impala.catalog;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.hadoop.fs.Path;
import org.apache.impala.analysis.LiteralExpr;
import org.apache.impala.catalog.HdfsPartition.FileDescriptor;
import org.apache.impala.thrift.TAccessLevel;
import org.apache.impala.thrift.THdfsPartitionLocation;
import org.apache.impala.thrift.TPartitionStats;

/**
 * Frontend interface for interacting with a single filesystem-based partition.
 */
public interface FeFsPartition {
  /**
   * @return a partition name formed by concatenating partition keys and their values,
   * compatible with the way Hive names partitions
   */
  String getPartitionName();

  /**
   * @return the ID for this partition which identifies it within its parent table.
   */
  long getId();

  /**
   * @return the table that contains this partition
   */
  FeFsTable getTable();

  /**
   * @return the files that this partition contains
   */
  List<FileDescriptor> getFileDescriptors();

  /**
   * @return true if this partition contains any files
   */
  boolean hasFileDescriptors();

  /**
   * @return the number of files in this partition
   */
  int getNumFileDescriptors();

  /**
   * @return the location of this partition
   */
  String getLocation();

  /**
   * Return the location of this partition, serialized in Thrift.
   */
  THdfsPartitionLocation getLocationAsThrift();

  /**
   * @return the location of this partition as a Path
   */
  Path getLocationPath();

  /**
   * @return the HDFS permissions Impala has to this partition's directory - READ_ONLY,
   * READ_WRITE, etc.
   */
  TAccessLevel getAccessLevel();

  /**
   * @return true if the partition resides at a location which can be cached (e.g. HDFS).
   */
  boolean isCacheable();

  /**
   * @return true if this partition is marked cached
   */
  boolean isMarkedCached();

  /**
   * @return the file format information for this partition
   */
  HdfsStorageDescriptor getInputFormatDescriptor();

  /**
   * @return the file format within this partition as an HdfsFileFormat enum
   */
  HdfsFileFormat getFileFormat();

  /**
   * @return the stats for this partition, or null if no stats are available
   */
  @Nullable
  TPartitionStats getPartitionStats();

  /**
   * @return true if this partition has incremental stats available
   */
  boolean hasIncrementalStats();

  /**
   * @return the size (in bytes) of all the files inside this partition
   */
  long getSize();

  /**
   * @return the estimated number of rows in this partition (-1 if unknown)
   */
  long getNumRows();

  /**
   * Utility method which returns a string of conjuncts of equality exprs to exactly
   * select this partition (e.g. ((month=2009) AND (year=2012)).
   */
  String getConjunctSql();

  /**
   * @return a list of partition values as strings. If mapNullsToHiveKey is true, any NULL
   * value is returned as the table's default null partition key string value, otherwise
   * they are returned as 'NULL'.
   */
  List<String> getPartitionValuesAsStrings(boolean mapNullsToHiveKey);

  /**
   * @return an immutable list of partition key expressions
   */
  List<LiteralExpr> getPartitionValues();

  /**
   * @return the value of the given column 'pos' for this partition
   */
  LiteralExpr getPartitionValue(int pos);

  /**
   * @return the HMS parameters stored for this partition
   */
  Map<String, String> getParameters();

  /**
   * Returns the HMS partition parameters after filtering out all the partition
   * incremental stats information.
   *
   * TODO(todd): consider _always_ filtering the parameters to remove incremental
   * stats, and having a different getter which returns the already-decoded stats
   * which are more memory-efficient anyway.
   */
  Map<String, String> getFilteredHmsParameters();
}
