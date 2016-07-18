# Dependency License Checker (Work in Progress)

[![Build Status](https://travis-ci.org/patrickpilch/dependency-license-checker.svg?branch=master)](https://travis-ci.org/patrickpilch/dependency-license-checker)
[![Coverage Status](https://coveralls.io/repos/github/patrickpilch/dependency-license-checker/badge.svg?branch=master)](https://coveralls.io/github/patrickpilch/dependency-license-checker?branch=master)

## Usage

### Sample Configuration

**Configuration in POM**
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
**Configuration external to POM**

This is the recommended approach for larger projects with many licenses, in order to not pollute the pom.
Please see `TextFileSupplier` javadoc for details.

```xml
<configuration>
    <rules>
        <myCustomRule implementation="io.github.patrickpilch.dependencylicensechecker.plugin.enforcer.LicenseEnforcerRule">
            <licenseSupplier implementation="io.github.patrickpilch.dependencylicensechecker.suppliers.TextFileSupplier">
                <filePath>licenses.txt</filePath>
            </licenseSupplier>
        </myCustomRule>
    </rules>
</configuration>
```

**Note**
- License names are leading/trailing whitespace and case sensitive.
- Both internal and external configurations will be respected if defined simultaneously.


### Sample Output
```
[WARNING] Rule 0: io.github.patrickpilch.dependencylicensechecker.plugin.enforcer.LicenseEnforcerRule failed with message:
Artifact "junit:junit:jar:3.8.1" with license "Common Public License Version 1.0" does not have an allowed license or is not excluded.
patrick.test.sandbox:Sandbox:jar:1.0-SNAPSHOT
└─ io.github.patrickpilch:license-enforcer:jar:0.0.1-SNAPSHOT
   └─ org.apache.maven.enforcer:enforcer-api:jar:1.4.1
      └─ org.codehaus.plexus:plexus-container-default:jar:1.0-alpha-9
         └─ junit:junit:jar:3.8.1
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

## Limitations

This plugin only inspects project dependencies, e.g. not build and reporting plugins. It is a future goal to support
this, but in the meantime a mitigation is to place the plugin into the `<dependencies>` section to be scanned.

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