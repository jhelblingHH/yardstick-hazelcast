/*
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.yardstickframework.hazelcast;

import java.util.Map;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionConfig.MaxSizePolicy;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import org.yardstickframework.BenchmarkConfiguration;

/**
 * Hazelcast benchmark that performs put and get operations.
 */
public class HazelcastPutGetJcacheHDBenchmark extends HazelcastAbstractJcacheBenchmark {
    private static final int MAX_BYTES = 75_000;
    private static final byte[][] byteArrays = new byte[10][];

    public HazelcastPutGetJcacheHDBenchmark() {
        super(null);
    }

    @Override public void setUp(final BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        for (int i = 0, s = MAX_BYTES; i < byteArrays.length; i++, s /= 2) {
            byteArrays[i] = new byte[s];
        }
    }

        /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        int key = nextRandom(args.range());

        Object val = cache.get(key);

        if (val != null)
            key = nextRandom(args.range());

        cache.put(key, new BigValue(key, byteArrays[nextRandom(byteArrays.length)]));

        return true;
    }
}
