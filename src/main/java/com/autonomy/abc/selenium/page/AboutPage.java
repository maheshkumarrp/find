package com.autonomy.abc.selenium.page;

import com.autonomy.abc.selenium.AppElement;
import com.autonomy.abc.selenium.menubar.NavBarTabId;
import com.autonomy.abc.selenium.menubar.SideNavBar;
import com.autonomy.abc.selenium.menubar.TopNavBar;
import com.autonomy.abc.selenium.util.AbstractMainPagePlaceholder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AboutPage extends AppElement implements AppPage {

	public AboutPage(final TopNavBar topNavBar, final WebElement $el) {
		super($el, topNavBar.getDriver());
	}

	@Override
	public void navigateToPage() { getDriver().get("about"); }

	public void setTableSize(final String tableSize) {
		findElement(By.cssSelector("[name='DataTables_Table_0_length'] option[value='" + tableSize + "']")).click();
	}

	public WebElement nextButton() {
		return findElement(By.cssSelector(".next a"));
	}

	public WebElement previousButton() {
		return findElement(By.cssSelector(".previous a"));
	}

	public boolean isPreviousDisabled() {
		return findElement(By.cssSelector(".previous")).getAttribute("class").contains("disabled");
	}

	public boolean isNextDisabled() {
		return findElement(By.cssSelector(".next")).getAttribute("class").contains("disabled");
	}

	public boolean isPageinateNumberActive(final int pageinateNumber) {
		return findElement(By.xpath(".//li[contains(@class, 'paginate_button active')]/a")).getText().equals(String.valueOf(pageinateNumber));
	}

	public WebElement pageinateNumber(final int pageinateNumber) {
		return findElement(By.xpath(".//a[text()='" + String.valueOf(pageinateNumber) + "']"));
	}

	public void searchInSearchBox(final String searchTerm) {
		findElement(By.cssSelector(".ibox-content [type='search']")).clear();
		findElement(By.cssSelector(".ibox-content [type='search']")).sendKeys(searchTerm);
	}

	public static class Placeholder extends AbstractMainPagePlaceholder<AboutPage> {

		public Placeholder(final AppBody body, final SideNavBar sideNavBar, final TopNavBar topNavBar) {
			super(body, sideNavBar, topNavBar, "about", NavBarTabId.ABOUT_PAGE, false);
		}

		@Override
		protected AboutPage convertToActualType(final WebElement element) {
			return new AboutPage(topNavBar, element);
		}

	}
}
