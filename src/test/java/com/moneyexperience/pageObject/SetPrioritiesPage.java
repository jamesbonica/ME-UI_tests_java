package com.moneyexperience.pageObject;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author jimbonica
 *
 * @date May-02-2019
 */

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class SetPrioritiesPage extends AbstractPage {

	@Autowired
	EventFiringWebDriver driver;

	@Autowired
	PageObjectFactory pageObjectFactory;

	@FindBy(css = "div[class*='nextButton'] > a")
	private WebElement nextButton;

	@FindAll(@FindBy(css = "button[draggable = 'true']"))
	private List<WebElement> draggablePriorities;

	@FindAll(@FindBy(css = "#wallet button[aria-label^= dropzone]"))
	private List<WebElement> dropzoneList;
	
	
	@FindBy(css = "button[data-testid = 'priority-forward-button']")
	private WebElement continueButton;

	public SetPrioritiesPage(EventFiringWebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public SetPrioritiesPage dragAndDropPriorities(List<String> prioritiesList) {
		WebElement priorityElement = null;
		// Iterate through List of Priorities specified for user
		for (int index = 0; index < prioritiesList.size(); index++) {
			String priority = prioritiesList.get(index);

			// Find the draggable element that equals the priority
			for (WebElement element : draggablePriorities) {
				if (element.getText().trim().equalsIgnoreCase(priority)) {
					priorityElement = element;
					break;
				}
			}

			priorityElement.click();
			dropzoneList.get(index).click();

		}

		return this;
	}

	public void clickNextButton() {
		waitForElement(nextButton);
		nextButton.click();
	}

	public SetPrioritiesPage dismissTessInstructions() {
		waitForElement(imReadyButton);
		imReadyButton.click(); 
		return this;

	}

	public void clickContinueButton() {
		waitForElement(continueButton);
		continueButton.click();
		
	}

}
