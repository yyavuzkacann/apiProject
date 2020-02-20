package apiRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class getRequest {
	static String baseURL = "http://www.omdbapi.com";

    static String firstRequestParam = "s";
    static String searchKey = "Harry Potter";
    static String secondRequestParam = "i";
    static String authParam = "apiKey";
    static String authKey = "13960ca0";
    static String expectedTitle = "Harry Potter and the Sorcerer's Stone";
    static String movieTitle = "Title";
    static String search = "Search";
    static String imdb = "imdbID";
    static String year = "Year";
    static String release = "Released";

	@Test
	public void requestFromServer() {
        RestAssured.baseURI = baseURL;
        
        //First request with search key 
        Response response = RestAssured.given()
                .param(firstRequestParam, searchKey)
                .param(authParam, authKey)
                .get();

        //Response is added to HashMap
        HashMap<String, Object> movieSearch = response.then()
                .assertThat()
                
                //Status code check
                .statusCode(200) 
                
                // Compare titles with expected title
                .body("Search.Title", hasItems(expectedTitle))
                
                //Extract response that equal expected movie title
                .extract()
                .path("%s.find {o -> o.%s == \"%s\"}", search, movieTitle, expectedTitle);
        
        //ImdbID null controller 
        assertNotNull(movieSearch.get(imdb), "imdbId is null!");
       
        //Second request with IMDB id
        RestAssured.given()
                .param(secondRequestParam, movieSearch.get(imdb))
                .param(authParam, authKey)
                .get()
                .then()
                .assertThat()
                
                //Status code check
                .statusCode(200)
                
                // Check year parameter not null
                .body(year, notNullValue())
                
                // Check title parameter not null
                .body(movieTitle, notNullValue())
                
                // Check Released parameter not null
                .body(release, notNullValue())
                
                //Logging second response
                .log().body()
                
                //Logging second response status
                .log().status();
    }
}

