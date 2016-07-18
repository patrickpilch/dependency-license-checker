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
import org.apache.maven.model.License;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.*;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.eclipse.aether.RepositorySystemSession;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Scans a maven project dependency graph in order to determine whether any dependencies are using licenses
 * that are not explicitly allowed.
 */
public class DependencyChecker {

    private final Log log;
    private final RepositorySystem repositorySystem;
    private final ProjectBuilder projectBuilder;
    private final RepositorySystemSession repositorySystemSession;

    private final Set<ArtifactExclusion> excludedArtifacts;
    /** Lower case trim()'d {@link License#getName()} **/
    private final Set<String> whitelistedLicenses;

    public DependencyChecker(Log log,
                             RepositorySystem repositorySystem,
                             ProjectBuilder projectBuilder,
                             RepositorySystemSession repositorySystemSession,
                             Set<ArtifactExclusion> excludedArtifacts,
                             Set<String> whitelistedLicenses) {
        this.log = log;
        this.repositorySystem = repositorySystem;
        this.projectBuilder = projectBuilder;
        this.repositorySystemSession = repositorySystemSession;
        this.excludedArtifacts = excludedArtifacts;
        this.whitelistedLicenses = whitelistedLicenses;
    }

    /**
     * Iterates through the dependency graph and throws exceptions if there are non-whitelisted dependencies present
     * @param dependencyNode The root node in the graph to start inspecting.
     * @throws ProjectResolutionException Thrown if project pom files are not read properly, such as in the case of
     * invalid configurations present in a pom file that do not conform to the pom specification.
     * @throws DependencyException Thrown when a dependency does not meet the license whitelist criteria.
     */
    public void scanDependencies(DependencyNode dependencyNode) throws ProjectResolutionException, DependencyException {
        if (dependencyNode.getChildren().isEmpty()) {
            checkDependency(dependencyNode);
        } else {
            for (DependencyNode dependency : dependencyNode.getChildren()) {
                scanDependencies(dependency);
            }
        }
    }

    private void checkDependency(final DependencyNode dependency) throws ProjectResolutionException, DependencyException {
        log.debug("Checking dependency: " + dependency.getArtifact().getId());
        if (!excludedArtifacts.contains(convertArtifactToExclusion(dependency.getArtifact()))) {
            final List<License> licenses = getArtifactLicenses(dependency.getArtifact());
            if (licenses.isEmpty()) {
                throw new LicenseMissingException(dependency);
            } else {
                for (final License license : licenses) {
                    log.debug("Inspecting license - " + license.getName());
                    if (!whitelistedLicenses.contains(license.getName())) {
                        throw new LicenseNotAllowedException(dependency, license);
                    } else if (log.isDebugEnabled()) {
                        log.debug("\"" + dependency.getArtifact().getId() + "\" with license \"" + license.getName() +
                                "\" is good!");
                    }
                }
            }
        } else if (log.isDebugEnabled()) {
            log.debug(dependency.getArtifact().getId() + " is in artifact exclusions");
        }
    }

    private static ArtifactExclusion convertArtifactToExclusion(Artifact artifact) {
        return new ArtifactExclusion(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }

    private List<License> getArtifactLicenses(final Artifact artifact) throws ProjectResolutionException {
        try {
            MavenProject projectArtifact = generateMavenProject(artifact);
            List<License> licenses = projectArtifact.getLicenses();
            if (licenses == null || licenses.isEmpty()) {
                Artifact parentArtifact = projectArtifact.getParentArtifact();
                return (parentArtifact == null) ? Collections.emptyList() : getArtifactLicenses(parentArtifact);
            } else {
                return licenses;
            }
        } catch (Exception e) {
            throw new ProjectResolutionException(artifact, e);
        }
    }

    private MavenProject generateMavenProject(final Artifact artifact) throws ProjectBuildingException {
        Objects.requireNonNull(artifact);
        final Artifact projectArtifact = repositorySystem.createProjectArtifact(artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getVersion());

        final ProjectBuildingRequest projectBuildingRequest = new DefaultProjectBuildingRequest();
        projectBuildingRequest.setRepositorySession(repositorySystemSession);
        projectBuildingRequest.setSystemProperties(System.getProperties());
        return projectBuilder.build(projectArtifact, projectBuildingRequest)
                .getProject();
    }

    public Set<ArtifactExclusion> getExcludedArtifacts() {
        return excludedArtifacts;
    }

    public Set<String> getWhitelistedLicenses() {
        return whitelistedLicenses;
    }
}
