package com.autonomy.abc.config;

public enum HSODTearDown implements TearDown<HostedTestBase> {
    INDEXES {
        @Override
        public void tearDown(HostedTestBase test) {
            if (test.hasSetUp()) {
                test.getApplication().connectionService().deleteAllConnections(true);
                test.getApplication().indexService().deleteAllIndexes();
            }
        }
    },
}
