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

import com.hazelcast.query.*;
import org.yardstickframework.*;
import org.yardstickframework.hazelcast.querymodel.*;
import org.yardstickframework.hazelcast.util.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.yardstickframework.BenchmarkUtils.*;

/**
 * Hazelcast benchmark that performs query operations.
 */
public class HazelcastSqlQueryBenchmark extends HazelcastAbstractBenchmark {
    /** Number of threads that populate the cache for query test. */
    private static final int POPULATE_QUERY_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2;

    /** */
    public HazelcastSqlQueryBenchmark() {
        super("query");
    }

    /** {@inheritDoc} */
    @Override public void setUp(final BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        println("Populating query data...");

        long start = System.nanoTime();

        final AtomicInteger cnt = new AtomicInteger(0);

        // Populate persons.
        HazelcastBenchmarkUtils.runMultiThreaded(new HazelcastBenchmarkRunnable() {
            @Override public void run(int threadIdx) throws Exception {
                for (int i = threadIdx; i < args.range() && !Thread.currentThread().isInterrupted();
                     i += POPULATE_QUERY_THREAD_NUM) {
                    map.put(i, new Person(i, "firstName" + i, "lastName" + i, i * 1000));

                    int populatedPersons = cnt.incrementAndGet();

                    if (populatedPersons % 100000 == 0)
                        println("Populated persons: " + populatedPersons);
                }
            }
        }, POPULATE_QUERY_THREAD_NUM, "populate-query-person");

        println("Finished populating query data in " + ((System.nanoTime() - start) / 1_000_000) + "ms.");
    }

    /** {@inheritDoc} */
    @Override public void test() throws Exception {
        double salary = ThreadLocalRandom.current().nextDouble() * args.range() * 1000;

        double maxSalary = salary + 1000;

        Collection<Person> persons = executeQuery(salary, maxSalary);

        for (Person p : persons) {
            if (p.getSalary() < salary || p.getSalary() > maxSalary)
                throw new Exception("Invalid person retrieved [min=" + salary + ", max=" + maxSalary +
                    ", person=" + p + ']');
        }
    }

    /**
     * @param minSalary Min salary.
     * @param maxSalary Max salary.
     * @return Query results.
     * @throws Exception If failed.
     */
    private Collection<Person> executeQuery(double minSalary, double maxSalary) throws Exception {
        return (Collection<Person>)(Collection<?>)map.values(
            new SqlPredicate("salary >= " + minSalary + " and salary <= " + maxSalary));
    }
}
