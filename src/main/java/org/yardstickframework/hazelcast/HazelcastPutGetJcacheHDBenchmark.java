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

/**
 * Hazelcast benchmark that performs put and get operations.
 */
public class HazelcastPutGetJcacheHDBenchmark extends HazelcastAbstractJcacheBenchmark {
    /** */
    public HazelcastPutGetJcacheHDBenchmark() {
        super("jcache-hd");
    }

    @Override
    protected void init(com.hazelcast.config.CacheConfig conf) {
        conf.setInMemoryFormat(InMemoryFormat.NATIVE);
        EvictionConfig eviction = conf.getEvictionConfig();
        eviction
                .setEvictionPolicy(EvictionPolicy.RANDOM)
                .setMaximumSizePolicy(MaxSizePolicy.FREE_NATIVE_MEMORY_PERCENTAGE)
                .setSize(95);

    }

    /** {@inheritDoc} */
    @Override
    public boolean test(Map<Object, Object> ctx) throws Exception {
        int key = nextRandom(args.range());

        Object val = cache.get(key);

        if (val != null)
            key = nextRandom(args.range());

        byte[] bytes = new byte[key / 20];
        cache.put(key, new BigValue(key, bytes));

        return true;
    }
}
