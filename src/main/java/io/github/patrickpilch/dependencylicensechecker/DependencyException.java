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

/**
 * Base exception class that contains information about the dependency that caused the exception, and its
 * position in the dependency graph.
 */
public abstract class DependencyException extends Exception {
    private static final long serialVersionUID = -4324257055788944337L;

    final transient DependencyNode offendingDependency;

    DependencyException(DependencyNode offendingDependency) {
        this.offendingDependency = offendingDependency;
    }

    /**
     * Obtain the DependencyNode corresponding to the artifact that fails enforcement rules.
     * Must also be part of a valid dependency graph to ascertain the dependency tree leading
     * to it.
     * @return The offending artifact's dependency tree node.
     */
    public DependencyNode getOffendingDependency() {
        return offendingDependency;
    }
}
