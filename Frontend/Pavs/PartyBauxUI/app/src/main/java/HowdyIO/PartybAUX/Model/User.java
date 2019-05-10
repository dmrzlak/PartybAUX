package HowdyIO.PartybAUX.Model;


import org.json.JSONException;
import org.json.JSONObject;

public class User{
    private String username, token, email, password;
    private int userID, partyID , userType;
    boolean premium;

    public static final String SHARED_PREFS_USERNAME = "username";
    public static final String SHARED_PREFS_PASSWORD = "password";
    public static final String SHARED_PREFS_TOKEN = "token";


    /**
     * Default constructor
     */
    public User() {
        setDefaults();
    }

    /**
     * Creates a user from a JSON object
     * @param json object containing the information for the user
     */
    public User(JSONObject json) {
        setDefaults();
        createFromJSON(json);
    }

    /**
     * Constructor for the User
     * @param username User's name
     * @param password User's password
     * @param token Token for a user
     */
    public User(String username, String password, String token) {
        setDefaults();
        this.username = username;
        this.password = password;
        this.token = token;
    }

    /**
     * Sets default values for the user
     */
    private void setDefaults(){
        username = password = email = token = "";
        userID = userType = 0;
        partyID = -1;
    }

    /**
     * Gets and fills the info needed for a user
     * @param json JSON object containing the needed info
     */
    private void createFromJSON(JSONObject json){
        try {
            if (json.has(SHARED_PREFS_USERNAME)) username = json.getString(SHARED_PREFS_USERNAME);
            if (json.has(SHARED_PREFS_PASSWORD)) password = json.getString(SHARED_PREFS_PASSWORD);
            if (json.has(SHARED_PREFS_TOKEN)) token = json.getString(SHARED_PREFS_TOKEN);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Getters
     */
    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getUserID() {
        return userID;
    }

    public int getPartyID() {
        return partyID;
    }

    public int getUserType() {
        return userType;
    }



    /**
     * Setters
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setPartyID(int partyID) {
        this.partyID = partyID;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

}