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

package com.hazelcast.jet.dag.source;

import com.hazelcast.jet.container.ContainerDescriptor;
import com.hazelcast.jet.dag.Vertex;
import com.hazelcast.jet.impl.actor.ObjectProducer;
import com.hazelcast.jet.impl.dag.source.DataFileReader;
import com.hazelcast.jet.impl.util.JetUtil;

import java.io.File;

/**
 * A source which uses a file as the input.
 */
public class FileSource implements Source {

    private final String name;

    /**
     * Constructs a source with the given filename
     *
     * @param name name of the file to read
     */
    public FileSource(String name) {
        this.name = name;
    }

    @Override
    public ObjectProducer[] getReaders(ContainerDescriptor containerDescriptor, Vertex vertex) {
        File file = new File(this.name);
        int chunkCount = vertex.getDescriptor().getTaskCount();
        long[] chunks = JetUtil.splitFile(file, chunkCount);
        ObjectProducer[] readers = new ObjectProducer[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            long start = chunks[i];

            if (start < 0) {
                break;
            }

            long end = i < chunkCount - 1 ? chunks[i + 1] : file.length();

            int partitionId = i % containerDescriptor.getNodeEngine().getPartitionService().getPartitionCount();
            readers[i] = new DataFileReader(containerDescriptor, partitionId, name, start, end);
        }
        return readers;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FileSource{"
                + "name='" + name + '\''
                + '}';
    }
}