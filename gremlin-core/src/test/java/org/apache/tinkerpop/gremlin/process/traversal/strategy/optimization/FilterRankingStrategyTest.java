/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process.traversal.strategy.optimization;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalEngine;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.util.DefaultTraversalStrategies;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@RunWith(Enclosed.class)
public class FilterRankingStrategyTest {

    @RunWith(Parameterized.class)
    public static class StandardTest extends AbstractDedupBijectionStrategyTest {

        @Parameterized.Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return generateTestParameters();
        }

        @Parameterized.Parameter(value = 0)
        public Traversal original;

        @Parameterized.Parameter(value = 1)
        public Traversal optimized;

        @Before
        public void setup() {
            this.traversalEngine = mock(TraversalEngine.class);
            when(this.traversalEngine.getType()).thenReturn(TraversalEngine.Type.STANDARD);
        }

        @Test
        public void shouldApplyStrategy() {
            doTest(original, optimized);
        }
    }

    @RunWith(Parameterized.class)
    public static class ComputerTest extends AbstractDedupBijectionStrategyTest {

        @Parameterized.Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return generateTestParameters();
        }

        @Parameterized.Parameter(value = 0)
        public Traversal original;

        @Parameterized.Parameter(value = 1)
        public Traversal optimized;

        @Before
        public void setup() {
            this.traversalEngine = mock(TraversalEngine.class);
            when(this.traversalEngine.getType()).thenReturn(TraversalEngine.Type.COMPUTER);
        }

        @Test
        public void shouldApplyStrategy() {
            doTest(original, optimized);
        }
    }

    private static abstract class AbstractDedupBijectionStrategyTest {

        protected TraversalEngine traversalEngine;

        void applyDedupBijectionStrategy(final Traversal traversal) {
            final TraversalStrategies strategies = new DefaultTraversalStrategies();
            strategies.addStrategies(FilterRankingStrategy.instance(), IdentityRemovalStrategy.instance());

            traversal.asAdmin().setStrategies(strategies);
            traversal.asAdmin().setEngine(this.traversalEngine);
            traversal.asAdmin().applyStrategies();
        }

        public void doTest(final Traversal traversal, final Traversal optimized) {
            applyDedupBijectionStrategy(traversal);
            assertEquals(optimized, traversal);
        }

        static Iterable<Object[]> generateTestParameters() {

            return Arrays.asList(new Traversal[][]{
                    {__.dedup().order(), __.dedup().order()},
                    {__.order().dedup(), __.dedup().order()},
                    {__.identity().order().dedup(), __.dedup().order()},
                    {__.order().identity().dedup(), __.dedup().order()},
                    {__.order().out().dedup(), __.order().out().dedup()},
                    {__.has("value", 0).filter(__.out()).dedup(), __.has("value", 0).filter(__.out()).dedup()},
                    {__.dedup().filter(__.out()).has("value", 0), __.has("value", 0).filter(__.out()).dedup()},
                    {__.filter(__.out()).dedup().has("value", 0), __.has("value", 0).filter(__.out()).dedup()},
                    {__.has("value", 0).filter(__.out()).dedup(), __.has("value", 0).filter(__.out()).dedup()},
            });
        }
    }
}

