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

import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;
import org.yardstickframework.BenchmarkConfiguration;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;

/**
 * Hazelcast benchmark that performs get operations.
 */
public abstract class HazelcastAbstractJcacheBenchmark extends HazelcastAbstractBenchmark {

    private String jCacheName;
    protected Cache<Object, Object> cache;

    public HazelcastAbstractJcacheBenchmark(String jCacheName) {
        this.jCacheName = jCacheName;
    }

    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        HazelcastInstance hazelcast = this.hazelcast();
        CacheManager cacheManager = getCacheManager(hazelcast);

        CacheConfig config = new CacheConfig();
        config.setName(jCacheName);

        try {
            cacheManager.createCache(jCacheName, config);
        } catch (CacheException e) {}

        cache = cacheManager.getCache(jCacheName);
    }


    /** {@inheritDoc} */
    @Override public void tearDown() throws Exception {
        cache.clear();
        super.tearDown();
    }
}
