package pagecontenttester.fetcher;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.jsoup.Connection.Method.POST;
import static pagecontenttester.annotations.Fetch.Protocol.HTTPS;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import pagecontenttester.annotations.Cookie;
import pagecontenttester.annotations.Fetch;
import pagecontenttester.annotations.GetFetchedPageException;
import pagecontenttester.runner.PageContentTester;

@Fetch(url = "localhost/example", port = "8089")
public class FetchedPageTest extends PageContentTester {

    private static final String URL1 = "localhost:8089/example";
    private static final String URL2 = "localhost:8089/example2";
    private static final String URL3 = "localhost:8089/example3";
    private static final String MOBILE_USER_AGENT = "Mozilla/5.0 (iPhone\\\\; CPU iPhone OS 6_1_4 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10B350 Safari/8536.25";
    private static final String USER_AGENT = "Mozilla/5.0 (X11\\\\; Ubuntu\\\\; Linux x86_64\\\\; rv\\\\:25.0)";

    @Test
    public void can_fetch_from_class_annotation() {
        assertThat(page.get().getUrl()).endsWith("example");
    }

    @Test
    public void fetcher_should_return_referrer() {
        assertThat(page.get().getConfig().getReferrer()).isEqualTo("http://www.google.com");
    }

    @Test
    @Fetch(url = "www.idealo.de")
    public void fetcher_should_return_cookie_value() {
        assertThat(page.get().getCookieValue("ipcuid")).isNotEmpty();
    }

    @Test
    public void fetcher_should_return_cookies() {
        assertThat(page.get().getCookies(), either(hasEntry("logged_in", "no")).or(is(Collections.emptyMap())));
    }

    @Test
    @Fetch(url = "www.idealo.de")
    public void fetcher_should_return_cookie() {
        assertThat(page.get().hasCookie("ipcuid")).isTrue();
    }

    @Test
    public void fetcher_should_return_content_type() {
        assertThat(page.get().getContentType()).contains("text/html");
    }

    @Test
    public void fetcher_should_return_element() {
        assertThat(page.get().getElement("h1").hasText()).isTrue();
    }

    @Test
    @Fetch(url = "stackoverflow.com/")
    public void fetcher_should_return_page_body() {
        assertThat(page.get().getPageBody()).contains("<!DOCTYPE html>");
    }

    @Test
    @Fetch(url = URL2)
    public void fetcher_should_return_status_message() {
        assertThat(page.get().getStatusMessage()).isEqualTo("OK");
    }

    @Test
    @Fetch(url = URL2)
    public void fetcher_should_return_status_code() {
        assertThat(page.get().getStatusCode()).isEqualTo(200);
    }

    @Test
    @Fetch(url = "www.idealo.de", followRedirects = false)
    public void fetcher_should_not_redirect() {
        assertThat(page.get().getStatusCode()).isEqualTo(301);
    }

    @Test
    public void should_return_true_if_certain_element_is_present() {
        assertThat(page.get().isElementPresent("h1")).isTrue();
    }

    @Test
    public void should_return_false_if_certain_element_is_not_present() {
        assertThat(page.get().isElementPresent("dgfhkdgs")).isFalse();
    }

    @Test
    public void fetcher_should_return_elements_by_selector() {
        assertThat(page.get().getElements("h1").size()).isEqualTo(1);
    }

    @Test
    public void fetcher_should_return_last_matching_element_by_selector() {
        assertThat(page.get().getElementLastOf("p").text()).contains("second paragraph");
    }

    @Test
    public void fetcher_should_return_nth_matching_element_by_selector() {
        assertThat(page.get().getElement("p", 0).text()).isEqualTo("i'm a paragraph");
    }

    @Test
    public void fetcher_should_return_count_of_certain_element() {
        assertThat(page.get().getElementCount("p"), is(2));
    }

    @Test
    public void fetcher_should_return_headers() {
        assertThat(page.get().getHeaders(), hasEntry("Custom-Header", "custom value"));
    }

    @Test(expected = NullPointerException.class)
    public void fetcher_should_return_null_for_non_existing_location_header() {
        String location = page.get().getLocation();
        assertThat(location, is(null));
    }

    @Test
    public void fetcher_should_return_certain_header() {
        assertThat(page.get().getHeader("Custom-Header"), equalTo("custom value"));
    }

    @Test
    public void fetcher_should_check_is_certain_header_is_present() {
        assertThat(page.get().hasHeader("Custom-Header"), is(true));
    }

    @Test
    @Fetch(url = URL1)
    @Fetch(url = URL2)
    public void fetch_multiple_pages_via_annotation_and_get_pages_by_index() {
        assertThat(page.get(0).getUrl(), equalTo("http://" + URL1));
        assertThat(page.get(1).getUrl(), equalTo("http://" + URL2));
    }

    @Test
    @Fetch(url = URL2)
    @Fetch(url = URL3)
    public void fetch_multiple_pages_via_annotation_and_get_pages_by_url_snippet() {
        assertThat(page.get("example2").getTitle()).isEqualTo("i'm the title2");
        assertThat(page.get("example3").getTitle()).isEqualTo("i'm the title3");
    }

    @Test(expected = GetFetchedPageException.class)
    public void fetch_page_via_annotation_and_try_to_get_fetched_page_by_unknown_url_snippet() {
        page.get("unknown");
    }

    @Test
    @Fetch(protocol = HTTPS, urlPrefix = "en", url = "wikipedia.org/proxy")
    public void fetch_page_via_annotation_and_build_url() {
        assertThat(page.get().getUrl(), equalTo("https://en.wikipedia.org/proxy"));
        assertThat(page.get().getUrlPrefix(), equalTo("en"));
    }

    @Test(expected = GetFetchedPageException.class)
    public void fetch_page_via_annotation_and_try_to_get_fetched_page_by_invalid_index() {
        page.get(2);
    }


    @Test
    public void get_name_of_test() {
        String testName = page.get().getTestName();
        assertThat(testName, equalTo("get_name_of_test(pagecontenttester.fetcher.FetchedPageTest)"));
    }

    @Test
    public void get_name_of_other_test() {
        assertThat(page.get().getTestName(), equalTo("get_name_of_other_test(pagecontenttester.fetcher.FetchedPageTest)"));
    }

    @Test
    @Fetch(url = URL1)
    public void can_store_page_body() throws IOException, InterruptedException {
        page.get().storePageBody();
        File file = new File("target/paco/stored/can_store_page_body(pagecontenttester.fetcher.FetchedPageTest).html");
        assertFileContent(file, "<title>i'm the title</title>");
    }

    @Test
    public void store_page_body_if_element_not_present() throws IOException {
        page.get().getElements("dfghfjhg");
        File file = new File("target/paco/not-found/store_page_body_if_element_not_present(pagecontenttester.fetcher.FetchedPageTest).html");
        assertFileContent(file, "<title>i'm the title</title>");
    }

    @Test
    @Fetch(url = "whatsmyuseragent.org/")
    public void fetch_as_default_user_agent_by_annotation() {
        String ua = page.get().getElement("p.intro-text").text();
        assertThat(ua).contains(USER_AGENT);
    }

    @Test
    @Fetch(url = "whatsmyuseragent.org/", userAgent = MOBILE_USER_AGENT)
    public void fetch_as_mobile_user_agent_by_annotation() {
        String ua = page.get().getElement("p.intro-text").text();
        assertThat(ua).contains(MOBILE_USER_AGENT);
    }

    @Test
    @Fetch(url = URL1, referrer = "my.custom.referrer")
    public void fetch_page_by_setting_custom_referrer() {
        String referrer = page.get().getReferrer();
        assertThat(referrer).isEqualTo("my.custom.referrer");
    }

    @Test
    @Fetch(url = "www.whatismyreferer.com/")
    public void fetch_page_should_use_referrer_from_properties_by_default() {
        String referrer = page.get().getElement("strong").text();
        assertThat(referrer).isEqualTo(config.getReferrer());
    }

    @Ignore("figure out how to configure qiremock to replay cookies")
    @Test
    @Fetch( url = URL1,
            setCookies = @Cookie(name = "page-content-tester", value = "wtf-666"))
    public void can_set_cookie_via_annotation() throws Exception {
        assertThat(page.get().getCookieValue("page-content-tester")).isEqualTo("wtf-666");
    }

    @Ignore("figure out how to configure qiremock to replay cookies")
    @Test
    @Fetch( url = URL1,
            setCookies = {  @Cookie(name = "page-content-tester", value = "wtf-666"),
                            @Cookie(name = "some-other-cookie", value = "666-wtf") })
    public void can_set__multiple_cookies_via_annotation() throws Exception {
        assertThat(page.get().getCookieValue("page-content-tester")).isEqualTo("wtf-666");
        assertThat(page.get().getCookieValue("some-other-cookie")).isEqualTo("666-wtf");
    }

    @Test
    @Fetch(url = URL1, method = POST)
    public void do_post_request_and_check_response() throws Exception {
        assertThat(page.get().getJsonResponse().get("data"), equalTo("some value"));
    }

    @Test
    public void should_return_true_for_certain_count_of_certain_element() {
        assertThat(page.get().isElementPresentNthTimes("h1", 1), is(true));
    }

    @Test
    public void should_return_false_for_invalid_count_of_certain_element() {
        assertThat(page.get().isElementPresentNthTimes("h1", 100), is(false));
    }

    private void assertFileContent(File file, String contains) {
        await().atMost(5, SECONDS).untilAsserted(() ->
                assertThat(FileUtils.readFileToString(file).contains(contains)));
    }
}
