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

package io.github.patrickpilch.dependencylicensechecker.suppliers;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class TextFileSupplierTest {
    @Test
    public void getWithInjectedStream() throws Exception {
        final TextFileSupplier textFileSupplier = new TextFileSupplier(
                TextFileSupplierTest.class.getResourceAsStream("test.txt"));
        final List<String> licenses = textFileSupplier.get();
        assertThat(licenses, contains("License One", "  License Two  ", "License Three"));
    }

    @Test(expected = SupplierException.class)
    public void testWithNoFilePath() {
        final TextFileSupplier textFileSupplier = new TextFileSupplier();
        textFileSupplier.get();
    }
}