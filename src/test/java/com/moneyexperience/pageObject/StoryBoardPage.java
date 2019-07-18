package com.moneyexperience.pageObject;

import java.util.List;

import org.openqa.selenium.By;
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

import config.ScenarioSession;

/**
 * 
 * @author jimbonica
 *
 * @date May-01-2019
 */

@Component
public class StoryBoardPage extends AbstractPage {

	@Autowired
	EventFiringWebDriver driver;

	@Autowired
	ScenarioSession scenarioSession;

	@Autowired
	ChatPage chatPage;

	@FindAll(@FindBy(css = "nav > button[class*='styles__textButton']"))
	private List<WebElement> navigationLinkList;

	@FindBy(css = "nav > button[class*='styles__textButton']")
	private WebElement nextLinkWhenTheresNoGoingBack;

	@FindBy(css = "nav > button[class*='styles__textButton']:nth-child(1)")
	private WebElement previousOrBackLink;

	@FindBy(css = "nav > button[class*='styles__textButton']:nth-child(2)")
	private WebElement nextLinkWhenThereIsABackOrPreviousLink;

	@FindBy(css = "p[class*='speechBubble']")
	private WebElement tessSpeechBubble;

	@FindBy(css = "figure + nav[class] > button:nth-child(2)")
	private WebElement nextLinkForStoryPanels;

	@FindAll(@FindBy(css = "figure + nav[class] > button"))
	private List<WebElement> navLinksForStoryPanelsList;

	@FindAll(@FindBy(css = "div[class*='modal']"))
	private List<WebElement> newMessageFromTessModalList;

	@FindBy(css = "div[class*='modal'] > button[class*='primaryButton']")
	private WebElement goButtonOnChatWithTessModal;

	@FindBy(css = "div[class*='storyboard'] > figure > img")
	private WebElement storyBoardImage;
	
	@FindAll(@FindBy(css = "div[class*='storyboard'] > figure > img"))
	private List<WebElement> storyBoardImageList;

	public StoryBoardPage(EventFiringWebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public StoryBoardPage clickNextLink() {
		waitForElement(htmlColorDefinedElement);
		waitForElement(nextLinkWhenTheresNoGoingBack);
		// check if there is two-way or one-way navigation on the page
		int navigationLinks = navigationLinkList.size();

		if (navigationLinks > 1) {
			nextLinkWhenThereIsABackOrPreviousLink.click();
		} else {
			nextLinkWhenTheresNoGoingBack.click();
		}

		return this;
	}

	public String getTessSpeechBubble() {
		WebElement element = driver.findElement(By.cssSelector("p[class*='speechBubble']"));
		waitForElement(element);
		return element.getText().trim();
	}

	public void waitForTessDialogToUpdate(String currentText) {
		int counter = 1;

		while (counter < 10) {
			if (!tessSpeechBubble.getText().trim().equals(currentText)) {
				break;
			} else {
				pause(.25);
				counter++;
			}
		}

	}

	public StoryBoardPage clicknextLinkForStoryPanels() {
		waitForElement(nextLinkForStoryPanels);
		try {
		nextLinkForStoryPanels.click();
		} catch (ElementClickInterceptedException e) {
			nextLinkForStoryPanels.click();	
		}
		return this;
	}

	/*
	 * This method checks if a loader element is present first which would indicate
	 * the storyBoards are done and the simulator is going to a Chat or the ON
	 * Dashboard. If that isn't present then it will check every .25 seconds for a
	 * total of 2.5 seconds to see if the src attribute has changed which means a
	 * new storyboard has loaded and the user should click Next again or if the Chat
	 * with Tess modal has appeared which stops the user from trying to click on the
	 * Next link -- jb
	 */

	public boolean moveOnToNextStoryBoard() {
		boolean clickNext = false;
		if (loaderPresent()) {
			System.out.println("============================= loader present");
			return clickNext;
		}

		// We need something here because this is occasionally failing on SauceLabs with
		// the
		// screenshot showing the user is on the Chat but the test thinks it's still on
		// the storyboards until it fails -- jb 6/18/19
		
		// Check if storyboard or footer is present?
		int counter = 0;

		while (counter < 10) {
			String newSrc = "";
			
			try {
			newSrc = storyBoardImage.getAttribute("src");
			} catch (NoSuchElementException n) {
				System.out.println("footer present " + (chatPage.footerElementPresent()));
				System.out.println("test saved?!");
				break;
			}
			
			String oldSrc = scenarioSession.getStoryBoardSrc();

			if (!newSrc.equals(oldSrc)) {
				scenarioSession.setStoryBoardSrc(newSrc);
				// System.out.println("SRC changes");
				clickNext = true;
				break;
			} else if (newMessageFromTessModalList.size() > 0) {
				System.out.println("Tess interrupts!!!");
				break;
			}

			pause(.25);
			counter++;
		}

		return clickNext;
	}

	public void clickGoToChatWithTess() {
		waitForElement(goButtonOnChatWithTessModal);
		try {
			goButtonOnChatWithTessModal.click();
		} catch (ElementClickInterceptedException e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", goButtonOnChatWithTessModal);

		}
	}

	public boolean storyBoardLinkPresent() {
		return navLinksForStoryPanelsList.size() > 0;
	}

	public boolean loaderPresent() {
		return driver.findElements(By.cssSelector("div[class*='loader']")).size() > 0;
	}

}
