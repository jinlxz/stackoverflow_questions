package org.example;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.element;
import static java.lang.System.currentTimeMillis;

/**
 * Created with IntelliJ IDEA.
 * User: xialiu8b
 * Date: 2/25/2023
 * Time: 10:15 PM
 */
@ParametersAreNonnullByDefault
public class MainTest {

    @Test
    public void verifySubscriptionItemsForOrder() {
        Configuration.pageLoadTimeout = 50000;
        Selenide.open("http://127.0.0.1:8080/my_account4/My%20Account.html");

        long start = currentTimeMillis();
        LoggerUtils.logMethodTime("find all subscriptions");
        ElementsCollection divSubscriptionItems = $$("div.subs-expands").snapshot();

        LoggerUtils.logMethodTime("find all subscriptions related to the order");
        //find the subscription items for an order in subscription list, basically it retrieve all subscription items and filter by order id.
        ElementsCollection subscriptions = findSubscriptionItemsByOrderId(divSubscriptionItems, "O-0000017865");
        LoggerUtils.logMethodTime("find the subscription related to the product");
        //find the subscription for the product SIMATIEDL, it uses the method `ElementCollection.filter()` to find the target subscription.
        SelenideElement target = this.findSubscriptionItem(subscriptions, "SIMATIEDL", 1);
        LoggerUtils.logMethodTime("verify product name.");
        target.find(By.cssSelector("span.cc_col_subscription_prod_name a")).shouldHave(exactOwnText("Industrial Edge Management License"));
        LoggerUtils.logMethodTime("verify subscription id");
        target.find(By.cssSelector("span.cc_goto_subscription_details")).shouldHave(matchText("^SB-.*"));
        LoggerUtils.logMethodTime("verify subscription recurring price.");
        target.find(By.cssSelector("span.cc_col_subscription_recurring_price")).shouldHave(ownText("USD $"));

        long end = currentTimeMillis();
        LoggerUtils.logMethodTime("finished to verify subscription recurring price in " + (end - start) + " ms.");
    }

    public ElementsCollection findSubscriptionItemsByOrderId(ElementsCollection divSubscriptionItems, String orderNumber) {
        ElementsCollection subscriptions = divSubscriptionItems.filter(childExactText(
                By.xpath(".//div[contains(@class,'subscription-data')]//div[contains(@class,'sub-heading')]"), orderNumber
        ));
        return subscriptions.snapshot();
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
            @Nonnull
            public CheckResult check(Driver driver, WebElement node) {
                String childText = element(node).find(childBy).getOwnText().trim();
//                    childText=node.findElement(childBy).getText().trim();
                return new CheckResult(childText.equals(text_), childText);
            }
        };
    }

    public static Condition childAttributeValue(By childBy, String key, String value) {
        return new Condition("childAttributeValue") {
            @Override
            @Nonnull
            public CheckResult check(Driver driver, WebElement node) {
                String childText = node.findElement(childBy).getAttribute(key).trim();
                return new CheckResult(childText.equals(value), childText);
            }
        };
    }
}