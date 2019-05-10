package HowdyIO.PartybAUX.Tools;

public class uriValidator {

    public static boolean isURI(String s){
        //returns starts with spotify:track:40v0UzCgsJf9uj4R9NvM3t
        return s.startsWith("spotify:track:") && s.length()>"spotify:track:".length();
    }
}
