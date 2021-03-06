package paco.fetcher;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import paco.annotations.*;
import paco.runner.Paco;

import java.io.File;
import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.awaitility.Awaitility.await;
import static org.jsoup.Connection.Method.POST;
import static paco.annotations.Fetch.Device.DESKTOP;
import static paco.annotations.Fetch.Device.MOBILE;
import static paco.annotations.Fetch.Protocol.HTTPS;

@Fetch(url = "localhost/example")
public class FetchedPageTest extends Paco {

    private static final String URL1 = "localhost/example";
    private static final String URL2 = "localhost/example2";
    private static final String URL3 = "localhost/example3";
    private static final String URL4 = "localhost/referrer";

    @Test
    public void can_fetch_from_class_annotation() {
        assertThat(page.get().getUrl()).endsWith("example");
    }

    @Test
    public void fetcher_should_return_cookie_value() {
        assertThat(page.get().getCookieValue("logged_in")).isEqualTo("no");
    }

    @Test
    public void fetcher_should_return_cookies() {
        assertThat(page.get().getCookies()).contains(entry("logged_in", "no"));
    }

    @Test
    public void fetcher_should_return_cookie() {
        assertThat(page.get().hasCookie("logged_in")).isTrue();
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
    @Fetch(url = "www.idealo.de", followRedirects = false, port = "80")
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
        assertThat(page.get().getElementCount("p")).isEqualTo(2);
    }

    @Test
    public void fetcher_should_return_headers() {
        assertThat(page.get().getHeaders()).contains(entry("Custom-Header", "custom value"));
    }

    @Test
    public void fetcher_should_return_null_for_non_existing_location_header() {
        String location = page.get().getLocation();
        assertThat(location).isNull();
    }

    @Test
    public void fetcher_should_return_certain_header() {
        assertThat(page.get().getHeader("Custom-Header")).isEqualTo("custom value");
    }

    @Test
    public void fetcher_should_check_is_certain_header_is_present() {
        assertThat(page.get().hasHeader("Custom-Header")).isTrue();
    }

    @Test
    @Fetch(protocol = HTTPS, url = URL2, port = "8090")
    @Fetch(url = URL3)
    public void fetch_multiple_pages_via_annotation_and_get_pages_by_url_snippet() {
        System.out.println(page.get(0).getUrl());
        assertThat(page.get("8090/example2").getTitle()).isEqualTo("i'm the title2");
        assertThat(page.get("example3").getTitle()).isEqualTo("i'm the title3");
    }

    @Test(expected = GetFetchedPageException.class)
    public void fetch_page_via_annotation_and_try_to_get_fetched_page_by_unknown_url_snippet() {
        page.get("unknown");
    }

    @Test
    @Fetch(urlPrefix = "en.", url = "wikipedia.org", port = "80")
    public void fetch_page_via_annotation_and_build_url() {
        assertThat(page.get().getUrl()).isEqualTo("http://en.wikipedia.org:80");
    }

    @Test
    @Fetch( urlPrefix = "en",
            urlPrefixSeparator = ".",
            url = "wikipedia.org",
            port = "80")
    public void fetch_page_via_annotation_and_build_url_with_separator() {
        assertThat(page.get().getUrl()).isEqualTo("http://en.wikipedia.org:80");
    }

    @Test(expected = GetFetchedPageException.class)
    public void fetch_page_via_annotation_and_try_to_get_fetched_page_by_invalid_index() {
        page.get(2);
    }

    @Test
    @Fetch(url = URL2)
    @Fetch(url = URL2, device = MOBILE)
    public void fetch_as_desktop_and_mobile_device_by_annotation_and_get_page_by_url_and_device() {
        assertThat(page.get(URL2, DESKTOP).getUserAgent()).isEqualTo(DESKTOP.value);
        assertThat(page.get(URL2, MOBILE).getUserAgent()).isEqualTo(MOBILE.value);
    }

    @Test
    @Fetch(url = URL2)
    @Fetch(url = URL2, device = MOBILE)
    public void fetch_as_desktop_and_mobile_device_by_annotation_and_get_by_device() {
        assertThat(page.get(DESKTOP).getUserAgent()).isEqualTo(DESKTOP.value);
        assertThat(page.get(MOBILE).getUserAgent()).isEqualTo(MOBILE.value);
    }

    @Test
    @Fetch(url = URL2)
    @Fetch(url = URL2, device = MOBILE)
    public void should_return_fetched_page_for_url_snippet_and_device() {
        assertThat(page.get("example2", DESKTOP).getUserAgent()).isEqualTo(DESKTOP.value);
        assertThat(page.get("example2", MOBILE).getUserAgent()).isEqualTo(MOBILE.value);
    }

    @Test
    @Fetch(url = URL1)
    @Fetch(url = URL1, device = MOBILE)
    public void should_return_fetched_page_for_url_snippet() {
        assertThat(page.get("example").getUserAgent()).isEqualTo(DESKTOP.value);
    }

    @Test(expected = GetFetchedPageException.class)
    @Fetch(url = URL2)
    @Fetch(url = URL2, device = MOBILE)
    public void should_throw_exception_if_page_can_not_get_by_url() {
        page.get("wrong-url", DESKTOP);
    }

    @Test
    public void get_name_of_test() {
        String testName = page.get().getTestName();
        assertThat(testName).isEqualTo("get_name_of_test(paco.fetcher.FetchedPageTest)");
    }

    @Test
    public void get_name_of_other_test() {
        assertThat(page.get().getTestName()).isEqualTo("get_name_of_other_test(paco.fetcher.FetchedPageTest)");
    }

    @Test
    @Fetch(url = URL1)
    public void can_store_page_body() throws IOException, InterruptedException {
        page.get().storePageBody();
        File file = new File("target/paco/stored/can_store_page_body(paco.fetcher.FetchedPageTest).html");
        assertTitle(file);
    }

    @Test
    public void store_page_body_if_element_not_present() throws IOException {
        page.get().getElements("dfghfjhg");
        File file = new File("target/paco/not-found/store_page_body_if_element_not_present(paco.fetcher.FetchedPageTest).html");
        assertTitle(file);
    }

    @Test
    @Fetch(url = URL2, device = MOBILE)
    public void fetch_as_mobile_device_by_annotation() {
        assertThat(page.get().getUserAgent()).isEqualTo(MOBILE.value);
    }

    @Test
    @Fetch(url = URL2)
    public void fetch_as_default_user_agent_by_annotation() {
        assertThat(page.get().getUserAgent()).isEqualTo(DESKTOP.value);
    }

    @Test
    @Fetch(url = URL2, userAgent = "my custom UserAgent")
    public void fetch_as_mobile_user_agent_by_annotation() {
        assertThat(page.get().getUserAgent()).isEqualTo("my custom UserAgent");
        assertThat(page.get().hasHeaderWithValue("User-Agent", "my custom UserAgent")).isTrue();
    }

    @Test
    @Fetch(url = "localhost:8089/referrer", referrer = "my.custom.referrer")
    public void fetch_page_by_setting_custom_referrer() {
        String referrer = page.get().getReferrer();
        assertThat(referrer).isEqualTo("my.custom.referrer");
    }

    @Test
    public void fetcher_should_return_referrer() {
        assertThat(page.get().getReferrer()).isEqualTo(globalConfig.getReferrer());
    }

    @Test
    @Fetch(url = "localhost:8089/referrer")
    public void fetch_page_should_use_referrer_from_properties_by_default() {
        assertThat(page.get().getReferrer()).isEqualTo(globalConfig.getReferrer());
    }

    @Test
    @Fetch( url = URL1, setCookies = @Cookie(name = "page-content-tester", value = "wtf-666"))
    public void can_set_cookie_via_annotation() throws Exception {
        // TODO: find out how to replay cookies from wiremock
        // assertThat(page.get().getCookieValue("page-content-tester")).isEqualTo("wtf-666");
    }

    @Test
    @Fetch( url = URL1,
            setCookies = {  @Cookie(name = "page-content-tester", value = "wtf-666"),
                            @Cookie(name = "some-other-cookie", value = "666-wtf") })
    public void can_set_multiple_cookies_via_annotation() throws Exception {
        // TODO: find out how to replay cookies from wiremock
        // assertThat(page.get().getCookieValue("page-content-tester")).isEqualTo("wtf-666");
        // assertThat(page.get().getCookieValue("some-other-cookie")).isEqualTo("666-wtf");
    }

    @Test
    @Fetch( url = URL4, header = @Header(name = "Custom-Header", value = "testHeader"))
    public void can_set_header_via_annotation() throws Exception {
        // TODO: find out how to replay custom header from wiremock
        // assertThat(page.get().getHeader("Custom-Header")).isEqualTo("testHeader");
    }

    @Ignore
    @Test
    @Fetch( url = URL4, proxy = @Proxy(host = "/proxy", port = 8089))
    public void can_set_proxy_via_annotation() throws Exception {
        // TODO: find out how to call url through proxy via wiremock
    }

    @Test
    @Fetch( url = URL4, header = {
                    @Header(name = "Custom-Header", value = "testHeader"),
                    @Header(name = "some-other-header", value = "other header") })
    public void can_set_multiple_headers_via_annotation() throws Exception {
        // TODO: find out how to replay custom header from wiremock
        // assertThat(page.get().getHeader("Custom-Header")).isEqualTo("testHeader");
    }

    @Test
    @Fetch(url = URL1, method = POST, retriesOnTimeout = 2, timeout = 5000)
    public void do_post_request_and_check_response() throws Exception {
        assertThat(page.get().getJsonResponse().get("data")).isEqualTo("some value");
    }

    @Test
    @Fetch(url = "localhost:8089/replay-post", method = POST, requestBody = "{\"data\":\"value\"}")
    public void do_post_request_with_body_and_check_response() throws Exception {
        assertThat(page.get().getJsonResponse().get("data")).isEqualTo("value");
    }

    @Test
    public void should_return_true_for_certain_count_of_certain_element() {
        assertThat(page.get().isElementPresentNthTimes("h1", 1)).isTrue();
    }

    @Test
    public void should_return_false_for_invalid_count_of_certain_element() {
        assertThat(page.get().isElementPresentNthTimes("h1", 100)).isFalse();
    }

    private void assertTitle(File file) {
        await().atMost(5, SECONDS).untilAsserted(() ->
                assertThat(FileUtils.readFileToString(file).contains("<title>i'm the title</title>")));
    }
}
