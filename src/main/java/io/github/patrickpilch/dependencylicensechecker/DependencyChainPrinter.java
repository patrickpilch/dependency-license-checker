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

import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Prints a visual representation of the path through the dependency graph from the root node to the node provided.
 */
public class DependencyChainPrinter {
    private static final String BRANCH  = "└─ ";
    private static final String EMPTY   = "   ";

    private DependencyChainPrinter() {
    }

    /**
     * Returns a multi-lined string representation of the path from the project root dependency to the provided node.
     * @param node The dependency to draw the path to.
     * @return The dependency tree path to the provided node from the root node. E.g.
     * <pre>
     *     some.group.id:some.artifact.id:jar:1.0-SNAPSHOT
     *     └─ io.github.patrickpilch:some-module:jar:2.1.3
     *        └─ node.group.id:node.artifact.id:jar:node.version
     * </pre>
     */
    public static String printTree(final DependencyNode node) {
        final List<DependencyNode> dependencyChain = createDependencyChain(node);
        final StringBuilder treeBuilder = new StringBuilder();

        treeBuilder.append(dependencyChain.get(0).getArtifact().getId());

        for (int i = 1; i < dependencyChain.size(); i++) {
            treeBuilder.append('\n');

            for (int spaceRepetitions = 1; spaceRepetitions < i; spaceRepetitions++) {
                treeBuilder.append(EMPTY);
            }

            treeBuilder.append(BRANCH)
                    .append(dependencyChain.get(i).getArtifact().getId());
        }

        return treeBuilder.toString();
    }

    private static List<DependencyNode> createDependencyChain(final DependencyNode rootNode) {
        final List<DependencyNode> dependencyChain = new ArrayList<>();

        DependencyNode node = rootNode;
        do {
            dependencyChain.add(node);
            node = node.getParent();
        } while (node != null);

        Collections.reverse(dependencyChain);
        return dependencyChain;
    }
}
