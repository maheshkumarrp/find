/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.find.idol.savedsearches.snapshot;

import com.hp.autonomy.frontend.find.core.savedsearches.AbstractSavedSearchTest;
import com.hp.autonomy.frontend.find.core.savedsearches.SavedSearch;
import com.hp.autonomy.frontend.find.core.savedsearches.snapshot.SavedSnapshot;

public class SavedSnapshotTest extends AbstractSavedSearchTest<SavedSnapshot, SavedSnapshot.Builder> {
    @Override
    protected SavedSearch.Builder<SavedSnapshot, SavedSnapshot.Builder> createBuilder() {
        return new SavedSnapshot.Builder();
    }
}
