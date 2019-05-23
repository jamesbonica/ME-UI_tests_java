package com.moneyexperience.pageObject;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author jimbonica
 *
 * @date May-02-2019
 */

@Component
public class ChatPage extends AbstractPage {

	@Autowired
	EventFiringWebDriver driver;

	@FindAll(@FindBy(css = "li[style^='order:']"))
	private List<WebElement> carouselOptionList;

	@FindBy(css = "li[style = 'order: 2;'] > button")
	private WebElement textCarouselCenterOption;

	@FindBy(css = "li[style = 'order: 2;'] h3")
	private WebElement imageCarouselCenterOption;

	@FindAll(@FindBy(css = "ul > li > button[id^='user_response']"))
	private List<WebElement> textOptionList;

	@FindBy(css = "ul > li > button[id^='user_response_0']")
	private WebElement firstTextOption;

	@FindBy(css = "button[type='left']")
	private WebElement leftNavArrow;

	@FindBy(css = "button[type='right']")
	private WebElement rightNavArrow;

	@FindBy(css = "input[aria-label = 'amount-slider']")
	private WebElement inputSlider;

	@FindBy(css = "input[aria-label = 'amount-slider'] + * + button")
	private WebElement inputSliderPlusButton;

	@FindBy(css = "div > button[class^='primary']:not([disabled])")
	private WebElement sendButton;

	public ChatPage(EventFiringWebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public ChatPage selectOptionInTextCarousel(String choice, String navigationDirection) {
		selectOptionInCarousel(choice, navigationDirection, carouselOptionList, textCarouselCenterOption);

		return this;

	}

	public ChatPage selectOptionInImageCarousel(String choice, String navigationDirection) {
		selectOptionInCarousel(choice, navigationDirection, carouselOptionList, imageCarouselCenterOption);
		return this;
	}

	private void selectOptionInCarousel(String choice, String navigationDirection, List<WebElement> carouselOptionList,
			WebElement imageCarouselCenterOption) {
		// Get the total amount of options
		waitForElementInChat(imageCarouselCenterOption);
		int textCarouselOptions = carouselOptionList.size();
		int count = 0;
		do {

			waitUntilElementReturnsString(imageCarouselCenterOption);
			if (imageCarouselCenterOption.getText().equalsIgnoreCase(choice)) {
				try {
				imageCarouselCenterOption.click();
				break;
				} catch(ElementClickInterceptedException e) {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", imageCarouselCenterOption);
					break;
				}
			} else {
				if (navigationDirection.equalsIgnoreCase("left")) {
					leftNavArrow.click();
				} else {
					rightNavArrow.click();
				}
			}
			count++;

		} while (count < textCarouselOptions);

		if (count == textCarouselOptions) {

			throw new NoSuchElementException(choice + " is not an option.");
		}

	}

	private void waitUntilElementReturnsString(WebElement imageCarouselCenterOption) {
		int counter = 0;
		while (counter < 10) {
			if (!imageCarouselCenterOption.getText().equals("")) {
				break;
			}

			pause(.1);
			counter++;
		}

	}

	public ChatPage clickSendButton() {
		waitForElement(sendButton);
		sendButton.click();
		return this;
	}

	public ChatPage selectOption(String choice) {
		waitForElementInChat(firstTextOption);
		for (WebElement element : textOptionList) {
			// System.out.println("TEST!!!!! " + element.getText());
			if (element.getText().trim().equalsIgnoreCase(choice)) {
				element.click();
			}
		}
		return this;

	}

	public ChatPage selectSlider(String choice) {
		waitForElementInChat(inputSliderPlusButton);
		String cleanNumberString = StringUtils.replace(choice, ",", "");
		cleanNumberString = StringUtils.replace(cleanNumberString, "%", "");
		int convertedChoice = Integer.valueOf(cleanNumberString);

		if (convertedChoice > Integer.valueOf(inputSlider.getAttribute("max"))) {
			throw new NoSuchElementException("The value entered is more than the max value of the slider");
		}

		int clicks = convertedChoice / Integer.valueOf(inputSlider.getAttribute("step"));

		for (int i = 1; i <= clicks; i++) {
			inputSliderPlusButton.click();
		}

		return this;
	}

}