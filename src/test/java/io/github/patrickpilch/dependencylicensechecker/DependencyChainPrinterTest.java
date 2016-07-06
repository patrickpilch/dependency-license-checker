/*
 *    Copyright 2016 Patrick Pilch. All Rights Reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.patrickpilch.dependencylicensechecker;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DependencyChainPrinterTest {

    @Test
    public void testPrivateConstructor()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // The things I do for coverage...
        Constructor<DependencyChainPrinter> constructor = DependencyChainPrinter.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertThat(constructor.newInstance(), isA(DependencyChainPrinter.class));
    }

    @Test
    public void printTree() throws Exception {
        DependencyNode node1 = createMockDependencyNode(createMockArtifact("node1"), null);
        DependencyNode node2 = createMockDependencyNode(createMockArtifact("node2"), node1);
        DependencyNode node3 = createMockDependencyNode(createMockArtifact("node3"), node2);
        DependencyNode node4 = createMockDependencyNode(createMockArtifact("node4"), node3);

        final String dependencyTree = DependencyChainPrinter.printTree(node4);
        assertThat(dependencyTree, is("node1\n" +
                "└─ node2\n" +
                "   └─ node3\n" +
                "      └─ node4"));
    }

    private DependencyNode createMockDependencyNode(final Artifact artifact, final DependencyNode parent) {
        DependencyNode mockDependencyNode = mock(DependencyNode.class);
        when(mockDependencyNode.getArtifact()).thenReturn(artifact);
        when(mockDependencyNode.getParent()).thenReturn(parent);
        return mockDependencyNode;
    }

    private Artifact createMockArtifact(final String artifactId) {
        Artifact mockArtifact = mock(Artifact.class);
        when(mockArtifact.getId()).thenReturn(artifactId);
        return mockArtifact;
    }
}