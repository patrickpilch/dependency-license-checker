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

/**
 * This exception is thrown when the maven model for a project could not be constructed to extract the license
 * information.
 */
class ProjectResolutionException extends Exception {
    private static final long serialVersionUID = -1815052799981670080L;

    /**
     * Constructor
     * @param artifact The artifact that the maven project could not have been generated for.
     * @param cause The exception that occurred during project resolution.
     */
    ProjectResolutionException(Artifact artifact, Throwable cause) {
        super("Could not create project model for artifact \"" + artifact.getId() + "\". Please manually investigate " +
                "and add this artifact to the exclusions list if applicable.", cause);
    }
}
