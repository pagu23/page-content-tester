package pagecontenttester.fetcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import pagecontenttester.annotations.Fetch;
import pagecontenttester.runner.PageContentTester;

@Fetch(url = "www.idealo.de")
public class FetchSingleFromClassTest extends PageContentTester {

    @Test
    public void can_fetch_from_class_annotation() {
        assertThat(page.get().getTitle(), containsString("IDEALO"));
    }

}