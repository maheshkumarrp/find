package com.autonomy.abc.indexes;

import com.autonomy.abc.config.HostedTestBase;
import com.autonomy.abc.config.TestConfig;
import com.autonomy.abc.selenium.config.ApplicationType;
import com.autonomy.abc.selenium.connections.ConnectionService;
import com.autonomy.abc.selenium.connections.Connector;
import com.autonomy.abc.selenium.connections.WebConnector;
import com.autonomy.abc.selenium.indexes.Index;
import com.autonomy.abc.selenium.indexes.IndexService;
import com.autonomy.abc.selenium.page.indexes.IndexesDetailPage;
import com.autonomy.abc.selenium.page.indexes.IndexesPage;
import com.hp.autonomy.frontend.selenium.util.AppElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.tools.JavaCompiler;
import java.util.List;

import static com.autonomy.abc.framework.ABCAssert.verifyThat;
import static com.autonomy.abc.matchers.ElementMatchers.containsText;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class IndexDetailsPageITCase extends HostedTestBase {

    private IndexService indexService;
    private IndexesPage indexesPage;
    private IndexesDetailPage indexesDetailPage;
    private Index indexOne = new Index("one");
    private Index indexTwo = new Index("two");

    public IndexDetailsPageITCase(TestConfig config, String browser, ApplicationType type, Platform platform) {
        super(config, browser, type, platform);
        // requires a separate account where indexes can safely be added and deleted
        setInitialUser(config.getUser("index_tests"));
    }

    @Before
    public void setUp(){
        indexService = getApplication().createIndexService(getElementFactory());
        indexesPage = indexService.setUpIndex(indexOne);
    }

    @Test
    //CSA-1643
    public void testAssociatedConnections(){
        ConnectionService connectionService = getApplication().createConnectionService(getElementFactory());
        Connector connector = new WebConnector("http://www.bbc.co.uk", "connector", indexOne).withDuration(60);

        indexService.setUpIndex(indexTwo);
        try {
            connectionService.setUpConnection(connector);

            indexesPage = indexService.goToIndexes();
            verifyThat(indexesPage.getNumberOfConnections(indexOne), is(1));
            verifyThat(indexesPage.getNumberOfConnections(indexTwo), is(0));
            
            indexesDetailPage = indexService.goToDetails(indexOne);
            List<String> associatedConnections = indexesDetailPage.getAssociatedConnectors();
            verifyThat(associatedConnections.size(), is(1));
            verifyThat(associatedConnections, hasItem(connector.getName()));

            connectionService.changeIndex(connector, indexTwo);

            verifyThat(getElementFactory().getConnectionsDetailPage().getIndexName(), is(indexTwo.getName()));

            indexesPage = indexService.goToIndexes();
            verifyThat(indexesPage.getNumberOfConnections(indexOne), is(0));
            verifyThat(indexesPage.getNumberOfConnections(indexTwo), is(1));

            indexesDetailPage = indexService.goToDetails(indexOne);
            verifyThat(indexesDetailPage.getAssociatedConnectors().size(), is(0));

            indexesDetailPage = indexService.goToDetails(indexTwo);
            associatedConnections = indexesDetailPage.getAssociatedConnectors();
            verifyThat(associatedConnections.size(), is(1));
            verifyThat(associatedConnections, hasItem(connector.getName()));
        } finally {
            connectionService.deleteAllConnections(true);
        }
    }

    @Test
    //CSA-1703
    public void testGraphNoDataMessage(){
        indexesDetailPage = indexService.goToDetails(indexOne);

        new WebDriverWait(getDriver(),20).until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadingIconSmall")));

        verifyThat(indexesDetailPage.filesIngestedGraph(), containsText("There is no data for this time period"));
    }

    @Test
    //CSA-1685
    public void testButtonsNotObscuredAfterScroll(){
        indexesDetailPage = indexService.goToDetails(indexOne);

        getDriver().manage().window().setSize(new Dimension(1100, 700));

        ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        indexesPage.loadOrFadeWait();

        ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0, -document.body.scrollHeight)");
        indexesPage.loadOrFadeWait();

        indexesDetailPage.backButton().click();

        verifyThat(getDriver().getCurrentUrl(), is("https://search.dev.idolondemand.com/search/#/indexes"));
    }

    @After
    public void tearDown(){
        getApplication().createConnectionService(getElementFactory()).deleteAllConnections(true);
        indexService.deleteAllIndexes();
    }
}
