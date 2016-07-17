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

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Reads in an input file and returns each line in the file as an element in the output list.
 */
public class TextFileSupplier implements Supplier<List<String>> {
    ///// Mojo Parameters /////
    @SuppressWarnings("unused")
    private String filePath;

    private InputStream textFileInputStream;
    private final AtomicReference<List<String>> lines = new AtomicReference<>();

    @SuppressWarnings({"unused", "WeakerAccess"})
    public TextFileSupplier() {
        // Required for Mojo to instantiate this when parsing the xml configuration
    }

    TextFileSupplier(InputStream textFileInputStream) {
        this.textFileInputStream = textFileInputStream;
    }

    @Override
    public List<String> get() {
        if (lines.get() == null) {
            synchronized(this) {
                if (lines.get() == null) {
                    initializeInputStream();
                    lines.getAndSet(readNonEmptyLines(textFileInputStream));
                }
            }
        }
        return lines.get();
    }

    private void initializeInputStream() {
        try {
            if (textFileInputStream == null) {
                if (filePath == null) {
                    throw new SupplierException("Must provide text file");
                } else {
                    textFileInputStream = new FileInputStream(filePath);
                }
            }
        } catch (FileNotFoundException e) {
            throw new SupplierException(e);
        }
    }

    /**
     * Returns non empty lines of a file while preserving whitespace of populated lines.
     * @param fileInputStream Input stream of the file to read
     * @return A list of strings representing the non-empty lines in the file.
     */
    private static List<String> readNonEmptyLines(final InputStream fileInputStream) {
        return new BufferedReader(new InputStreamReader(fileInputStream))
                .lines()
                .filter(s -> !s.trim().isEmpty())
                .collect(Collectors.toList());
    }
}