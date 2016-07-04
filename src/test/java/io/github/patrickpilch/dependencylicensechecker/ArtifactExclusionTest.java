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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ArtifactExclusionTest {
    @Test
    public void equals() throws Exception {
        assertThat(new ArtifactExclusion(), notNullValue());
        assertThat(new ArtifactExclusion(), is(not(new Object())));
        assertThat(new ArtifactExclusion(), is(new ArtifactExclusion()));

        ArtifactExclusion exclusion1 = new ArtifactExclusion("some.group", "some.artifact", "1.2.3");
        ArtifactExclusion exclusion2 = new ArtifactExclusion("some.group", "some.artifact", "1.2.3");

        assertThat(exclusion1, allOf(equalTo(exclusion1), equalTo(exclusion2)));
        assertThat(exclusion2, allOf(equalTo(exclusion1), equalTo(exclusion2)));
        assertThat(exclusion1.hashCode(), is(exclusion2.hashCode()));
        assertThat(exclusion2.hashCode(), is(exclusion1.hashCode()));


        ArtifactExclusion exclusion3 = new ArtifactExclusion("other.group", "some.artifact", "1.2.3");
        assertThat(exclusion3, not(anyOf(equalTo(exclusion1), equalTo(exclusion2))));

        ArtifactExclusion exclusion4 = new ArtifactExclusion("some.group", "other.artifact", "1.2.3");
        assertThat(exclusion4, not(anyOf(equalTo(exclusion1), equalTo(exclusion2))));

        ArtifactExclusion exclusion5 = new ArtifactExclusion("some.group", "some.artifact", "other.version");
        assertThat(exclusion5, not(anyOf(equalTo(exclusion1), equalTo(exclusion2))));
    }

    @Test
    public void testToString() {
        ArtifactExclusion exclusion = new ArtifactExclusion("foo", "bar", "baz");
        assertThat(exclusion.toString(), is("ArtifactExclusion{groupId='foo', artifactId='bar', version='baz'}"));
    }
}