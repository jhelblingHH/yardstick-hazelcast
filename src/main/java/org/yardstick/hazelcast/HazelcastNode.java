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

package org.yardstick.hazelcast;

import com.hazelcast.client.*;
import com.hazelcast.client.config.*;
import com.hazelcast.config.*;
import com.hazelcast.core.*;
import org.yardstick.*;
import org.yardstick.impl.util.*;

/**
 * Standalone Hazelcast node.
 */
public class HazelcastNode implements BenchmarkServer {
    /** */
    private HazelcastInstance hz;

    /** {@inheritDoc} */
    @Override public void start(BenchmarkConfiguration cfg) throws Exception {
        HazelcastBenchmarkArguments args = new HazelcastBenchmarkArguments();

        BenchmarkUtils.jcommander(cfg.commandLineArguments(), args, "<hazelcast-node>");

        if (args.clientMode()) {
            ClientConfig clientCfg = new XmlClientConfigBuilder(args.configuration()).build();

            hz = HazelcastClient.newHazelcastClient(clientCfg);

            cfg.output().println("Hazelcast client started.");
        }
        else {
            Config hzCfg = new XmlConfigBuilder(args.configuration()).build();

            configure(args, hzCfg, "map", false);
            configure(args, hzCfg, "query", true);

            hz = Hazelcast.newHazelcastInstance(hzCfg);

            cfg.output().println("Hazelcast member started.");
        }

        assert hz != null;
    }

    /**
     * Configure Hazelcast map.
     *
     * @param args Arguments.
     * @param cfg Hazelcast config.
     * @param name Map name.
     * @param idx Flag to index or not.
     * @return Map configuration.
     */
    private MapConfig configure(HazelcastBenchmarkArguments args, Config cfg, String name, boolean idx) {
        MapConfig mapCfg = cfg.getMapConfig(name);

        if (idx) {
            mapCfg.addMapIndexConfig(new MapIndexConfig("id", false));
            mapCfg.addMapIndexConfig(new MapIndexConfig("orgId", false));
            mapCfg.addMapIndexConfig(new MapIndexConfig("salary", true));
        }

        if (args.syncBackups()) {
            mapCfg.setBackupCount(args.backups());
            mapCfg.setAsyncBackupCount(0);
        }
        else {
            mapCfg.setBackupCount(0);
            mapCfg.setAsyncBackupCount(args.backups());
        }

        return mapCfg;
    }

    /** {@inheritDoc} */
    @Override public void stop() throws Exception {
        Hazelcast.shutdownAll();
        HazelcastClient.shutdownAll();
    }

    /** {@inheritDoc} */
    @Override public String usage() {
        return BenchmarkUtils.usage(new HazelcastBenchmarkArguments());
    }

    /**
     * @return Hazelcast instance.
     */
    public HazelcastInstance hazelcast() {
        return hz;
    }
}