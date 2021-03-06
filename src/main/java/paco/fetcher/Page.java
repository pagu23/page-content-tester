package paco.fetcher;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

public interface Page {

    /**
     * get DOM Element of first CSS-selector match
     * @param cssSelector to pick DOM-element
     * @return Element
     */
    Element getElement(String cssSelector);

    /**
     * get DOM Element of CSS-selector match by index
     * @param cssSelector to pick DOM-element
     * @param index - number of element, starting from 0 and from top to bottom of the DOM
     * @return Element
     */
    Element getElement(String cssSelector, int index);

    /**
     * get DOM Elements of matching CSS-selectors
     * @param cssSelector to pick DOM-element
     * @return Elements
     */
    Elements getElements(String cssSelector);

    /**
     * get last DOM Element of matching CSS-selector
     * @param cssSelector to pick DOM-element
     * @return Element
     */
    Element getElementLastOf(String cssSelector);

    /**
     * @param cssSelector to pick DOM-element
     * @return number of matching CSS-selectors
     */
    int getElementCount(String cssSelector);

    /**
     * @param cssSelector to pick DOM-element
     * @return true if Element is present in DOM
     */
    boolean isElementPresent(String cssSelector);

    /**
     * @param cssSelector to pick DOM-element
     * @param numberOfOccurrences - number of CSS-selector matches in the DOM
     * @return true if Element is present nth times in DOM
     */
    boolean isElementPresentNthTimes(String cssSelector, int numberOfOccurrences);

    /**
     * @return name of test that requested fetched page
     */
    String getTestName();

    /**
     * stores the responses page body as html file in target directory.
     * files can be found under target/paco/stored/$nameOfTest(path).html
     */
    void storePageBody();

    /**
     * stores the responses page body as html file in target directory under target/paco/$folder.
     * @param folder - name of folder
     */
    void store(String folder);

    /**
     * @return HTTP status code of fetched page
     */
    int getStatusCode();

    /**
     * @return HTTP status message of fetched page
     */
    String getStatusMessage();

    /**
     * @return the raw Document object including all DOM specific data
     */
    Document getDocument();

    /**
     * @return the fetched page's body as String
     */
    String getPageBody();

    /**
     * @return the requested url. this will not be updated if redirects occur
     */
    String getUrl();

    /**
     * Get the response content type (e.g. "text/html");
     * @return the response content type
     */
    String getContentType();

    /**
     * Get the value of a header. This is a simplified header model, where a header may only have one value.
     * Header names are case insensitive.
     * @param header name (case insensitive)
     * @return value of header, or null if not set.
     */
    String getHeader(String header);

    /**
     * Retrieve all of the request/response headers as a map
     * @return headers
     */
    Map<String, String> getHeaders();

    /**
     * @return Location header
     */
    String getLocation();

    /**
     * @return User-Agent header
     */
    String getUserAgent();

    /**
     * @return Referer header
     */
    String getReferrer();

    /**
     * Check if a header is present
     * @param header name of header (case insensitive)
     * @return if the header is present in this request/response
     */
    boolean hasHeader(String header);

    boolean hasHeaderWithValue(String header, String value);

    /**
     * Check if a cookie is present
     * @param cookieName name of cookie
     * @return if the cookie is present in this request/response
     */
    boolean hasCookie(String cookieName);

    /**
     * Get a cookie value by name from this request/response.
     * Response objects have a simplified cookie model. Each cookie set in the response is added to the response
     * object's cookie key=value map. The cookie's path, domain, and expiry date are ignored.
     * @param cookieName name of cookie to retrieve.
     * @return value of cookie, or null if not set
     */
    String getCookieValue(String cookieName);

    /**
     * Retrieve all of the request/response cookies as a map
     * @return cookies
     */
    Map<String, String> getCookies();

    /**
     * @return value of title tag
     */
    String getTitle();

    /**
     * @return the response body in JSON format
     */
    JSONObject getJsonResponse();

    /**
     * Check if response is valid xml and matches xsd.
     * This will also check for namespace with schema language W3C_XML_SCHEMA_NS_URI
     */
    void validateXml(String xsdPath);

    /**
     * Check if response is valid xml and matches xsd.
     * Additional switch if the validation should be aware of namespace.
     */
    void validateXml(String xsdPath, boolean namespaceAware);

    /**
     * Check if response is valid xml and matches xsd.
     * Additional switch if the validation should be aware of namespace and with
     * possibility to change the schema language by string or use String values from
     * the javax.xml.XMLConstants interface
     */
    void validateXml(String xsdPath, boolean namespaceAware, String schemaLanguage);
}
