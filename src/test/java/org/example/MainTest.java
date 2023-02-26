package org.example;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.element;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Created with IntelliJ IDEA.
 * User: xialiu8b
 * Date: 2/25/2023
 * Time: 10:15 PM
 */
public class MainTest {

    @Test
    public void verifySubscriptionItemsForOrder() throws Exception {
        Configuration.pageLoadTimeout = 50000;
        Selenide.open("http://127.0.0.1:8080/my_account4/My%20Account.html");
        LoggerUtils.logMethodTime("find all subscriptions related to the order");
        //find the subscription items for an order in subscription list, basically it retrieve all subscription items and filter by order id.
        ElementsCollection subscriptions = this.findSubscriptionItemsByOrderId("O-0000017865");
        LoggerUtils.logMethodTime("find the subscription related to the product");
        //find the subscription for the product SIMATIEDL, it uses the method `ElementCollection.filter()` to find the target subscription.
        SelenideElement target = this.findSubscriptionItem(subscriptions, "SIMATIEDL", 1);
        LoggerUtils.logMethodTime("verify product name.");
        target.find(By.cssSelector("span.cc_col_subscription_prod_name a")).shouldHave(exactOwnText("Industrial Edge Management License"));
        LoggerUtils.logMethodTime("verify subscription id");
        target.find(By.cssSelector("span.cc_goto_subscription_details")).shouldHave(matchText("^SB-.*"));
        LoggerUtils.logMethodTime("verify subscription recurring price.");
        target.find(By.cssSelector("span.cc_col_subscription_recurring_price")).shouldHave(ownText("USD $"));
        LoggerUtils.logMethodTime("finished to verify subscription recurring price.");
    }

    private ElementsCollection divSubscriptionItems = $$("div.subs-expands");

    public ElementsCollection findSubscriptionItemsByOrderId(String orderNumber) {
        ElementsCollection subscriptions = divSubscriptionItems.filter(childExactText(
                By.xpath(".//div[contains(@class,'subscription-data')]//div[contains(@class,'sub-heading')]"), orderNumber
        )).shouldBe(sizeGreaterThan(0));
        return subscriptions;
    }

    public SelenideElement findSubscriptionItem(ElementsCollection subscriptionItems, String sku, int inCartQuantity) {
        SelenideElement rawTarget = subscriptionItems.filter(childAttributeValue(By.cssSelector("span.cc_col_subscription_prod_name a"), "data-id", sku))
                .find(childExactText(By.cssSelector("div.subscription-data div.cc_col_subscription_status"), String.valueOf(inCartQuantity)))
                .should(exist);

        // TODO We could introduce method like $.cached() or $.freeze() in Selenide
        SelenideElement target = $(rawTarget.getWrappedElement());

        LoggerUtils.logMethodTime("the target subscription item is found in the list.");
        target.scrollIntoView(true).shouldBe(visible);
        LoggerUtils.logMethodTime("the target subscription item is scrolled into view");
        return target;
    }

    public static Condition childExactText(By childBy, String text_) {
        return new Condition("childExactText") {
            @Override
            public CheckResult check(Driver driver, WebElement node) {
                String childText = null;
                try {
                    childText = element(node).find(childBy).getOwnText().trim();
//                    childText=node.findElement(childBy).getText().trim();
                } catch (ElementNotFound e) {
                }
                if (childText == null)
                    return new CheckResult(false, "the exception NoSuchElementException is occurred.");
                return new CheckResult(childText.equals(text_), childText);
            }
        };
    }

    public static Condition childAttributeValue(By childBy, String key, String value) {
        return new Condition("childAttributeValue") {
            @Override
            public CheckResult check(Driver driver, WebElement node) {
                String childText = null;
                try {
//                    childText=element(node).find(childBy).getAttribute(key).trim();
                    childText = node.findElement(childBy).getAttribute(key).trim();
                } catch (ElementNotFound e) {
                }
                if (childText == null)
                    return new CheckResult(false, "the exception NoSuchElementException is occurred.");
                return new CheckResult(childText.equals(value), childText);
            }
        };
    }
}