# Dependency License Enforcer (Work in Progress)

[![Build Status](https://travis-ci.org/patrickpilch/dependency-license-checker.svg?branch=master)](https://travis-ci.org/patrickpilch/dependency-license-checker)

## Usage

Add the plugin and relevant configuration to your project's pom file.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>1.4.1</version>
    <dependencies>
        <dependency>
            <groupId>io.github.patrickpilch.dependencylicensechecker</groupId>
            <artifactId>dependency-license-checker</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>enforce</id>
            <configuration>
                <rules>
                    <myCustomRule implementation="io.github.patrickpilch.dependencylicensechecker.plugin.enforcer.LicenseEnforcerRule">
                        <exclusions>
                            <exclusion>
                                <groupId>classworlds</groupId>
                                <artifactId>classworlds</artifactId>
                                <version>1.1-alpha-2</version>
                            </exclusion>
                        </exclusions>
                        <licenseWhitelist>
                            <license>The Apache Software License, Version 2.0</license>
                        </licenseWhitelist>
                    </myCustomRule>
                </rules>
            </configuration>
            <goals>
                <goal>enforce</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Design

_Hasn't this been done before?_

Yes, but there's very little competition in the space.

This project addresses some specific needs such as determining a dependency's license by inspecting its entire hierarchy
(the license often resides in a top level parent pom), and also scans all transitive dependencies and plugins. See
[here](http://www.gnu.org/licenses/gpl-faq.en.html#GPLWrapper) on why this is very important.

_Why a whitelist?! Constructing a blacklist would be so much easier!_

This does not support the project's goal of ensuring only approved licenses are utilized in a project. New licenses may
always arrive, and may not be compatible with you or your company's policies. Unless constantly and preemptively updated,
a blacklist would not stop an incompatible license being brought in by a new dependency.

_Why no regular expression support for licenses?_

This was another strategic choice made to address the risk of writing an overly permissive regex that would allow a new
license to sneak into the permitted list.

## TODOs before initial release
- Separate out maven-enforcer plugin into separate module and create a standard mojo plugin module as well, to allow
users flexibility.
- Accept license whitelist files in configuration
- Tests (100% coverage baby!)
- Release on maven central
- Perhaps a snazzier name?
