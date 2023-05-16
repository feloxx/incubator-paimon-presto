/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.prestosql;

import io.prestosql.spi.connector.ConnectorSplit;
import io.prestosql.spi.connector.ConnectorSplitSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

/** PrestoSql {@link ConnectorSplitSource}. */
public abstract class PrestoSqlSplitSourceBase implements ConnectorSplitSource {

    private final Queue<PrestoSqlSplit> splits;

    public PrestoSqlSplitSourceBase(List<PrestoSqlSplit> splits) {
        this.splits = new LinkedList<>(splits);
    }

    protected CompletableFuture<ConnectorSplitBatch> innerGetNextBatch(int maxSize) {
        List<ConnectorSplit> batch = new ArrayList<>();
        for (int i = 0; i < maxSize; i++) {
            PrestoSqlSplit split = splits.poll();
            if (split == null) {
                break;
            }
            batch.add(split);
        }
        return CompletableFuture.completedFuture(new ConnectorSplitBatch(batch, isFinished()));
    }

    @Override
    public void close() {}

    @Override
    public boolean isFinished() {
        return splits.isEmpty();
    }
}
