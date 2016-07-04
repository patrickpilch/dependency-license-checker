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

package io.github.patrickpilch.dependencylicensechecker.plugin.enforcer;

import io.github.patrickpilch.dependencylicensechecker.ArtifactExclusion;
import io.github.patrickpilch.dependencylicensechecker.DependencyChecker;
import io.github.patrickpilch.dependencylicensechecker.DependencyException;
import org.apache.maven.enforcer.rule.api.*;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.RepositorySystemSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.patrickpilch.dependencylicensechecker.DependencyChainPrinter.printTree;

/**
 * This wraps the core dependency checking logic up into a Maven Enforcer plugin that can be used as part of
 * existing enforcements.
 * @see DependencyChecker
 */
public class LicenseEnforcerRule implements EnforcerRule2 {

    //// External parameters ////
    @SuppressWarnings("unused")
    private String[] licenseWhitelist;
    @SuppressWarnings("unused")
    private ArtifactExclusion[] exclusions;


    private Log log;
    private DependencyGraphBuilder dependencyGraphBuilder;

    @Override
    public void execute(EnforcerRuleHelper enforcerRuleHelper) throws EnforcerRuleException {
        try {
            injectDependencies(enforcerRuleHelper);
            log.debug("Starting dependency-license-checker execute");

            final DependencyChecker dependencyChecker = createDependencyEnforcer(enforcerRuleHelper);

            if (log.isDebugEnabled()) {
                log.debug("Excluded artifacts: " + dependencyChecker.getExcludedArtifacts());
                log.debug("License whitelist: " + dependencyChecker.getWhitelistedLicenses());
            }

            MavenProject project = (MavenProject) enforcerRuleHelper.evaluate("${project}");

            DependencyNode rootDependency = dependencyGraphBuilder.buildDependencyGraph(project, null);
            dependencyChecker.scanDependencies(rootDependency);

            log.debug("Ending dependency-license-checker execute");
        } catch (DependencyException e) {
            final String errorMessage = e.getMessage() + "\n" + printTree(e.getOffendingDependency());
            throw new EnforcerRuleException(errorMessage, e);
        } catch (Exception e) {
            log.error(e);
            throw new EnforcerRuleException("Unexpected error occurred", e);
        }
    }

    private Set<String> createWhitelistedLicensesSet(final String[] configuredLicenseWhitelist) {
        if (log.isDebugEnabled()) {
            log.debug("Provided license whitelist: " + Arrays.toString(configuredLicenseWhitelist));
        }
        if (configuredLicenseWhitelist != null) {
            return Arrays.stream(configuredLicenseWhitelist)
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @SuppressWarnings("unchecked")
    private DependencyChecker createDependencyEnforcer(final EnforcerRuleHelper enforcerRuleHelper)
            throws ComponentLookupException, ExpressionEvaluationException {

        return new DependencyChecker(log,
                (RepositorySystem) enforcerRuleHelper.getComponent(RepositorySystem.class),
                (ProjectBuilder) enforcerRuleHelper.getComponent(ProjectBuilder.class),
                (RepositorySystemSession) enforcerRuleHelper.evaluate("${session.repositorySession}"),
                exclusions == null ? Collections.emptySet() : new HashSet<>(Arrays.asList(exclusions)),
                createWhitelistedLicensesSet(licenseWhitelist));
    }

    private void injectDependencies(EnforcerRuleHelper enforcerRuleHelper) throws ComponentLookupException {
        this.log = enforcerRuleHelper.getLog();
        this.dependencyGraphBuilder =
                (DependencyGraphBuilder) enforcerRuleHelper.getComponent(DependencyGraphBuilder.class);
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public boolean isResultValid(EnforcerRule enforcerRule) {
        return false;
    }

    @Override
    public String getCacheId() {
        return null;
    }

    @Override
    public EnforcerLevel getLevel() {
        return EnforcerLevel.ERROR;
    }
}
