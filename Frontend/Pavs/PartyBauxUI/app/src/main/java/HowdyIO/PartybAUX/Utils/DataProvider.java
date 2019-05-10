package HowdyIO.PartybAUX.Utils;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Model.User;

public class DataProvider {
    private static String cancelReq = "cancel_req";
    public static final String BASE_URL = "http://cs309-bs-6.misc.iastate.edu:8080/";
    private static String evansIP = "http://10.31.7.146:8080/";
    private static int responseCode;


    public final static String RESPONSE_ERROR = "-3";
    /**
     ************************
     ************************
     *                      *
     * User Sign-Up/Sign-In *
     *                      *
     ************************
     ************************
     */
    public static void addUser(String username, String email, String password, final Callback callback){
        String url = ParamBuilder.addUser(username, email, password);
        serverRequest(url, callback);
    }

    public static void logUserIn(String username, String password, final Callback callback) {
        String url = ParamBuilder.logUserIn(username, password);
        serverRequest(url, callback);
    }

    public static void getUserByUsername(String username, final Callback callback){
        String url = ParamBuilder.getUser(username);
        serverRequest(url, callback);
    }


    /**
     ************************
     ************************
     *                      *
     * Party Implementation *
     *                      *
     ************************
     ************************
     */

    public static void createParty(String username, final Callback callback){
        String url = ParamBuilder.createParty(username);
        serverRequest(url, callback);
    }

    public static void joinParty(String hostname, String username, final Callback callback){
        String url = ParamBuilder.joinParty(hostname, username);
        serverRequest(url, callback);
    }


    public static void leaveParty(String username, final Callback callback) {
        String url = ParamBuilder.exit(username);
        serverRequest(url, callback);
    }

    public static void getMembers(String userID, String partyID){

    }

    public static void addSong(String partyID, String songID, Callback callback){
        String url = ParamBuilder.addSong(partyID, songID);
        serverRequest(url, callback);
    }

    public static void refreshSongsForParty(String partyID, final Callback callback){
        String url= ParamBuilder.refresh(partyID);
        serverRequest(url, callback);
    }

    public static void removeSong(int partyID, String songID, final Callback callback){
        String url = ParamBuilder.removeSong(partyID+"", songID);
        serverRequest(url, callback);
    }

    public static void removeSongs(int partyID, String list, final Callback callback) {
        String url= ParamBuilder.removeSongs(partyID+"", list);
        serverRequest(url, callback);
    }

    public static void updateUserType(String host, String member ,int toType, final Callback callback) {
        String url = ParamBuilder.updateUserType(host, member, toType + "");
        serverRequest(url, callback);
    }
    public static void kickUser (String kicker, String kickee ,final Callback callback){
            String url = ParamBuilder.kickUser(kicker, kickee);
            serverRequest(url, callback);
    }

        /**
         ************************
         ************************
         *                      *
         *       Helpers +      *
         *      Helper Class    *
         *                      *
         ************************
         ************************
         */
        public static void serverRequest (String url,final Callback callback){
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Success", "Request Successful: " + response);
                                callback.onResult(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error", "Unable to do serverRequest: " + error.toString());
                                callback.onResult(RESPONSE_ERROR);
                            }
                        });
                AppController.getInstance().addToRequestQueue(stringRequest, cancelReq);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        public static void postRequest (String url,final Callback callback){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Success", "Request Successful: " + response);
                            callback.onResult(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "Unable to do serverRequest: " + error.toString());
                            callback.onResult(RESPONSE_ERROR);
                        }
                    });
            AppController.getInstance().addToRequestQueue(stringRequest, cancelReq);


        }

        public static String parseJson (String s){
            return "";
        }


        protected static class ParamBuilder {
            private static String BASE_URL = "http://cs309-bs-6.misc.iastate.edu:8080/";

            public ParamBuilder() {
            }

            public static String addUser(String... input) {
                String params = "";
                params += "signup?";
                params += "username=" + input[0].split(" ")[0];
                params += "&email=" + input[1].split(" ")[0];
                params += "&password=" + input[2].split(" ")[0];
                params += "&partyid=" + "-1";
                params += "&usertype=" + "0";
                return BASE_URL + params;
            }

            public static String logUserIn(String... input) {
                String params = "";
                params += "login?";
                params += "username=" + input[0];
                params += "&password=" + input[1];
                return BASE_URL + params;
            }

            public static String addSong(String... input) {
                String params = "addsong?partyid=" + input[0] + "&uri=" + input[1];
                //TODO
                return BASE_URL + params;
            }

            public static String refresh(String... input) {
                String params = "refresh?partyid=" + input[0];
                //TODO
                return BASE_URL + params;
            }


            public static String createParty(String... input) {
                String params = "";
                params += "create?";
                params += "hostname=" + input[0].split(" ")[0];
                return BASE_URL + params;
            }

            public static String joinParty(String... input) {
                String params = "";
                params += "join?";
                params += "hostname=" + input[0].split(" ")[0];
                params += "&username=" + input[1].split(" ")[0];
                return BASE_URL + params;
            }

            public static String exit(String... input) {
                String params = "";
                params += "exit?";
                params += "username=" + input[0].split(" ")[0];
                return BASE_URL + params;
            }

            public static String getMembers(String... input) {
                String params = "";
                //TODO
                return BASE_URL + params;
            }

            public static String getUser(String... input) {
                String params = "";
                params += "get/user?";
                params += "username=" + input[0].split(" ")[0];
                return BASE_URL + params;
            }


            public static String removeSong(String... input) {
                String params = "";
                params += "remove/song?";
                params += "partyid=" + input[0] + "&uri=" + input[1];
                return BASE_URL + params;
            }

            public static String updateUserType(String... input) {
                String params = "/change?user="+input [0]+"&change="+input[1]+"&id="+input[2];
                return BASE_URL + params;
            }

            public static String removeSongs(String... input) {
                String params = "remove/songs?" + "partyid=" + input[0] + "&songs=" + input[1];
                return BASE_URL + params;
            }

            public static String kickUser(String... input) {
                String params = "kick?user=" + input[0] + "&kick=" + input[1];
                return BASE_URL + params;
            }
        }
    }

