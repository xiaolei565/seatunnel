/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.engine.client;

import org.apache.seatunnel.api.source.Boundedness;
import org.apache.seatunnel.common.config.Common;
import org.apache.seatunnel.common.config.DeployMode;
import org.apache.seatunnel.engine.common.config.JobConfig;
import org.apache.seatunnel.engine.core.dag.logicaldag.LogicalDag;
import org.apache.seatunnel.engine.core.dag.logicaldag.LogicalDagGenerator;

import com.hazelcast.internal.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LogicalDagGeneratorTest {
    @Test
    public void testLogicalGenerator() {
        Common.setDeployMode(DeployMode.CLIENT);
        String filePath  = TestUtils.getResource("/fakesource_to_file_complex.conf");
        JobConfig jobConfig = new JobConfig();
        jobConfig.setBoundedness(Boundedness.BOUNDED);
        jobConfig.setName("fake_to_file");
        JobExecutionEnvironment jobExecutionEnv = new JobExecutionEnvironment(jobConfig, filePath);
        jobExecutionEnv.addAction(jobExecutionEnv.getJobConfigParser().parse());

        LogicalDagGenerator logicalDagGenerator = jobExecutionEnv.getLogicalDagGenerator();
        LogicalDag logicalDag = logicalDagGenerator.generate();
        JsonObject logicalDagJson = logicalDag.getLogicalDagAsJson();
        String result =
            "{\"vertices\":[{\"id\":2,\"name\":\"FakeSource(id=2)\",\"parallelism\":3},{\"id\":3,\"name\":\"FakeSource(id=3)\",\"parallelism\":3},{\"id\":1,\"name\":\"LocalFile(id=1)\",\"parallelism\":6}],\"edges\":[{\"leftVertex\":\"FakeSource\",\"rightVertex\":\"LocalFile\"},{\"leftVertex\":\"FakeSource\",\"rightVertex\":\"LocalFile\"}]}";
        Assert.assertEquals(result, logicalDagJson.toString());
    }
}