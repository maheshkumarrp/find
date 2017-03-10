/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.savedsearches;

@FunctionalInterface
public interface FieldTextParser {
    String toFieldText(SavedSearch<?, ?> savedSearch);
}
