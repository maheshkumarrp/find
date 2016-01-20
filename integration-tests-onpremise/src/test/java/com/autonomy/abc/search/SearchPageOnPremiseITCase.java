package com.autonomy.abc.search;

import com.autonomy.abc.config.ABCTestBase;
import com.autonomy.abc.config.TestConfig;
import com.autonomy.abc.selenium.language.Language;
import com.autonomy.abc.selenium.page.search.SearchBase;
import com.autonomy.abc.selenium.page.search.SearchPage;
import com.autonomy.abc.selenium.promotions.Promotion;
import com.autonomy.abc.selenium.promotions.PromotionService;
import com.autonomy.abc.selenium.promotions.SpotlightPromotion;
import com.autonomy.abc.selenium.search.*;
import com.autonomy.abc.selenium.util.Errors;
import com.autonomy.abc.selenium.util.Waits;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;

import static com.autonomy.abc.framework.ABCAssert.assertThat;
import static com.autonomy.abc.matchers.ElementMatchers.containsText;
import static org.hamcrest.Matchers.*;
import static org.openqa.selenium.lift.Matchers.displayed;

public class SearchPageOnPremiseITCase extends ABCTestBase{

    private SearchService searchService;
    private SearchPage searchPage;

    public SearchPageOnPremiseITCase(TestConfig config) {
        super(config);
    }

    @Before
    public void setUp(){
        searchService = getApplication().createSearchService(getElementFactory());
        searchPage = searchService.search("text");

    }

    @Test
    public void testFieldTextFilter() {
        final String searchResultTitle = searchPage.getSearchResultTitle(1);
        final String firstWord = getFirstWord(searchResultTitle);

        final int comparisonResult = searchResultNotStarting(firstWord);
        final String comparisonString = searchPage.getSearchResultTitle(comparisonResult);

        searchPage.expand(SearchBase.Facet.FIELD_TEXT);
        searchPage.fieldTextAddButton().click();
        Waits.loadOrFadeWait();
        assertThat("input visible", searchPage.fieldTextInput().getElement(), displayed());
        assertThat("confirm button visible", searchPage.fieldTextTickConfirm(), displayed());

        searchPage.filterBy(new FieldTextFilter("WILD{" + firstWord + "*}:DRETITLE"));
        assertThat(searchPage, not(containsText(Errors.Search.HOD)));

        assertThat("edit button visible", searchPage.fieldTextEditButton(), displayed());
        assertThat("remove button visible", searchPage.fieldTextRemoveButton(), displayed());
        assertThat(searchPage.getSearchResultTitle(1), is(searchResultTitle));

        try {
            assertThat(searchPage.getSearchResultTitle(comparisonResult), not(comparisonString));
        } catch (final NoSuchElementException e) {
            // The comparison document is not present
        }

        searchPage.fieldTextRemoveButton().click();
        Waits.loadOrFadeWait();
        assertThat(searchPage.getSearchResultTitle(comparisonResult), is(comparisonString));
        assertThat("Field text add button not visible", searchPage.fieldTextAddButton().isDisplayed());
        assertThat(searchPage.getSearchResultTitle(1), is(searchResultTitle));
    }

    private String getFirstWord(String string) {
        return string.substring(0, string.indexOf(' '));
    }

    private int searchResultNotStarting(String prefix) {
        for (int result = 1; result <= SearchPage.RESULTS_PER_PAGE; result++) {
            String comparisonString = searchPage.getSearchResultTitle(result);
            if (!comparisonString.startsWith(prefix)) {
                return result;
            }
        }
        throw new IllegalStateException("Cannot test field text filter with this search");
    }


    @Test
    public void testEditFieldText() {
        searchService.search(new SearchQuery("boer").withFilter(IndexFilter.ALL));

        searchPage.selectLanguage(Language.AFRIKAANS);

        searchPage.clearFieldText();

        final String firstSearchResult = searchPage.getSearchResultTitle(1);
        final String secondSearchResult = searchPage.getSearchResultTitle(2);

        searchPage.filterBy(new FieldTextFilter("MATCH{" + firstSearchResult + "}:DRETITLE"));
        assertThat("Field Text should not have caused an error", searchPage.getText(), not(containsString(Errors.Search.HOD)));
        assertThat(searchPage.getText(), not(containsString("No results found")));
        assertThat(searchPage.getSearchResultTitle(1), is(firstSearchResult));

        searchPage.filterBy(new FieldTextFilter("MATCH{" + secondSearchResult + "}:DRETITLE"));
        assertThat("Field Text should not have caused an error", searchPage.getText(), not(containsString(Errors.Search.HOD)));
        assertThat(searchPage.getSearchResultTitle(1), is(secondSearchResult));
    }

    //TODO
    @Test
    public void testFieldTextRestrictionOnPromotions(){
        PromotionService promotionService = getApplication().createPromotionService(getElementFactory());
        promotionService.deleteAll();

        promotionService.setUpPromotion(new SpotlightPromotion(Promotion.SpotlightType.SPONSORED, "boat"), "darth", 2);
        searchPage = getElementFactory().getSearchPage();
        searchPage.waitForPromotionsLoadIndicatorToDisappear();
        Waits.loadOrFadeWait();

        assertThat(searchPage.getPromotionSummarySize(), is(2));

        final List<String> initialPromotionsSummary = searchPage.getPromotedDocumentTitles(false);
        searchPage.filterBy(new FieldTextFilter("MATCH{" + initialPromotionsSummary.get(0) + "}:DRETITLE"));

        assertThat(searchPage.getPromotionSummarySize(), is(1));
        assertThat(searchPage.getPromotedDocumentTitles(false).get(0), is(initialPromotionsSummary.get(0)));

        searchPage.filterBy(new FieldTextFilter("MATCH{" + initialPromotionsSummary.get(1) + "}:DRETITLE"));

        assertThat(searchPage.getPromotionSummarySize(), is(1));
        assertThat(searchPage.getPromotedDocumentTitles(false).get(0), is(initialPromotionsSummary.get(1)));
    }

    @Test
    public void testFieldTextRestrictionOnPinToPositionPromotions(){
        PromotionService promotionService = getApplication().createPromotionService(getElementFactory());
        promotionService.deleteAll();
        List<String> promotedDocs = promotionService.setUpPromotion(new SpotlightPromotion("duck"), new SearchQuery("horse").withFilter(new LanguageFilter(Language.ENGLISH)), 2);

        searchPage.waitForPromotionsLoadIndicatorToDisappear();

        assertThat(promotedDocs.get(0) + " should be visible", searchPage.getText(), containsString(promotedDocs.get(0)));
        assertThat(promotedDocs.get(1) + " should be visible", searchPage.getText(), containsString(promotedDocs.get(1)));

        searchPage.filterBy(new FieldTextFilter("WILD{*horse*}:DRETITLE"));

        searchPage.waitForSearchLoadIndicatorToDisappear();
        Waits.loadOrFadeWait();

        assertThat(promotedDocs.get(0) + " should be visible", searchPage.getText(), containsString(promotedDocs.get(0)));
        assertThat(promotedDocs.get(1) + " should be visible", searchPage.getText(), containsString(promotedDocs.get(1)));	//TODO Seems like this shouldn't be visible
        assertThat("Wrong number of results displayed", searchPage.getHeadingResultsCount(), is(2));
        assertThat("Wrong number of pin to position labels displayed", searchPage.countPinToPositionLabels(), is(2));

        searchPage.filterBy(new FieldTextFilter("MATCH{" + promotedDocs.get(0) + "}:DRETITLE"));

        assertThat(searchPage.getSearchResultTitle(1), is(promotedDocs.get(0)));
        assertThat(searchPage.getHeadingResultsCount(), is(1));
        assertThat(searchPage.countPinToPositionLabels(), is(1));

        searchPage.filterBy(new FieldTextFilter("MATCH{" + promotedDocs.get(1) + "}:DRETITLE"));

        assertThat(promotedDocs.get(1) + " not visible in the search title", searchPage.getSearchResultTitle(1), is(promotedDocs.get(1)));
        assertThat("Wrong number of search results", searchPage.getHeadingResultsCount(), is(1));
        assertThat("Wrong number of pin to position labels", searchPage.countPinToPositionLabels(), is(1));
    }
}
