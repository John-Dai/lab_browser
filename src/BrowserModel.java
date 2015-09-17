import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * This represents the heart of the browser: the collections
 * that organize all the URLs into useful structures.
 * 
 * @author Robert C. Duvall
 */
public class BrowserModel {
    // constants
    public static final String PROTOCOL_PREFIX = "http://";
    // state
    private URL myHome;
    private URL myCurrentURL;
    private int myCurrentIndex;
    private List<URL> myHistory;
    private Map<String, URL> myFavorites;
    private ResourceBundle myErrorResources; 
    public static final String DEFAULT_RESOURCE_PACKAGE = "resources/";

    /**
     * Creates an empty model.
     */
    public BrowserModel () {
        myHome = null;
        myCurrentURL = null;
        myCurrentIndex = -1;
        myHistory = new ArrayList<>();
        myFavorites = new HashMap<>();
        myErrorResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + "Error");
    }

    /**
     * Returns the first page in next history, null if next history is empty.
     * @throws BrowserException
     */
    public URL next () throws BrowserException {
        if (hasNext()) {
            myCurrentIndex++;
            return myHistory.get(myCurrentIndex);
        }
        
        throw new BrowserException(myErrorResources.getString("noNextURL"));
        
    }

    /**
     * Returns the first page in back history, null if back history is empty.
     */
    public URL back () throws BrowserException {
        if (hasPrevious()) {
            myCurrentIndex--;
            return myHistory.get(myCurrentIndex);
        }
        
        throw new BrowserException(myErrorResources.getString("noPreviousURL"));
    }

    /**
     * Changes current page to given URL, removing next history.
     */
    public URL go (String url) {
        try{
            myCurrentURL = completeURL(url);
                if (hasNext()) {
                    myHistory = myHistory.subList(0, myCurrentIndex + 1);
                }
                myHistory.add(myCurrentURL);
                myCurrentIndex++;
            return myCurrentURL;
        }
        catch (BrowserException e){
            System.out.println("SDfds");

            System.out.printf("Could not resolve URL %s", url);
            return null; // need to change later 
        }
    }

    /**
     * Returns true if there is a next URL available
     */
    public boolean hasNext () {
        return myCurrentIndex < (myHistory.size() - 1);
    }

    /**
     * Returns true if there is a previous URL available
     */
    public boolean hasPrevious () {
        return myCurrentIndex > 0;
    }

    /**
     * Returns URL of the current home page or null if none is set.
     */
    public URL getHome () {
        return myHome;
    }

    /**
     * Sets current home page to the current URL being viewed.
     */
    public void setHome () {
        // just in case, might be called before a page is visited
        if (myCurrentURL != null) {
            myHome = myCurrentURL;
        }
    }

    /**
     * Adds current URL being viewed to favorites collection with given name.
     */
    public void addFavorite (String name) {
        // just in case, might be called before a page is visited
        if (name != null && !name.equals("") && myCurrentURL != null) {
            myFavorites.put(name, myCurrentURL);
        }
    }

    /**
     * Returns URL from favorites associated with given name, null if none set.
     * @throws BrowserException 
     */
    public URL getFavorite (String name) throws BrowserException {
        if (name != null && !name.equals("") && myFavorites.containsKey(name)) {
            return myFavorites.get(name);
        }
        throw new BrowserException(myErrorResources.getString("noFavoriteFound"));
    }


    // deal with a potentially incomplete URL
    private URL completeURL (String possible) throws BrowserException {
        try {
            System.out.println("00");

            // try it as is
            return new URL(possible);
        } catch (MalformedURLException e) {
            System.out.println("aa");

            try {
                // try it as a relative link
                // BUGBUG: need to generalize this :(
                return new URL(myCurrentURL.toString() + "/" + possible);
            } catch (MalformedURLException ee) {
                System.out.println("bb");

                try {
                    // e.g., let user leave off initial protocol
                    return new URL(PROTOCOL_PREFIX + possible);
                } catch (MalformedURLException eee) {
                    System.out.println("cc");
                    throw new BrowserException(myErrorResources.getString("incomplete"));
                }
            }
        }
    }
}
