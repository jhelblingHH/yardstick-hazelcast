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

import javax.cache.Cache;
import javax.cache.CacheManager;

import org.yardstickframework.BenchmarkConfiguration;

import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * Hazelcast benchmark that performs get operations.
 */
public abstract class HazelcastAbstractJcacheBenchmark extends HazelcastAbstractBenchmark {

    private String jCacheName = null;
    protected Cache<Object, Object> cache = null;

    public HazelcastAbstractJcacheBenchmark() {}

    public HazelcastAbstractJcacheBenchmark(String jCacheName) {
        this.jCacheName = jCacheName;
    }

    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        HazelcastInstance hazelcast = this.hazelcast();
        CacheManager cacheManager = getCacheManager(hazelcast);

        if (jCacheName == null){
            jCacheName = args.getCacheName();
        }


        cache = cacheManager.getCache(jCacheName);

        CacheConfig config = cache.getConfiguration(CacheConfig.class);

        System.out.println(this.getClass().getSimpleName() + " cache "+cache.getName()+" config "+config.getInMemoryFormat());
    }


    /** {@inheritDoc} */
    @Override public void tearDown() throws Exception {
        cache.clear();
        super.tearDown();
    }
}
