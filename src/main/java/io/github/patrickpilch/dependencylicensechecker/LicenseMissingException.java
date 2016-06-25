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

class LicenseMissingException extends DependencyException {
    private static final long serialVersionUID = 4931039060650965031L;

    LicenseMissingException(DependencyNode offendingDependency) {
        super(offendingDependency);
    }

    @Override
    public String getMessage() {
        return "Artifact \"" + offendingDependency.getArtifact().getId() + "\" is missing a license.";
    }
}
