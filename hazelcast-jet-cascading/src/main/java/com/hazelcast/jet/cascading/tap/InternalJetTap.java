/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.cascading.tap;

import cascading.scheme.Scheme;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.util.CloseableIterator;
import com.hazelcast.jet.Sink;
import com.hazelcast.jet.Source;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.io.Pair;
import com.hazelcast.jet.runtime.OutputCollector;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public abstract class InternalJetTap extends Tap<JobConfig, Iterator<Pair>, OutputCollector<Pair>> {

    protected InternalJetTap(Scheme<JobConfig, Iterator<Pair>, OutputCollector<Pair>, ?, ?> scheme,
                             SinkMode sinkMode) {
        super(scheme, sinkMode);
    }

    public abstract Source getSource();

    public abstract Sink getSink();

    protected static <V> CloseableIterator<V> makeCloseable(final Iterator<V> iterator) {
        return new CloseableIterator<V>() {
            @Override
            public void close() throws IOException {
                if (iterator instanceof Closeable) {
                    ((Closeable) iterator).close();
                }
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public V next() {
                return iterator.next();
            }
        };
    }


}