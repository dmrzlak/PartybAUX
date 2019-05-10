package HowdyIO.PartybAUX.Tools;

/**
 * Created by Chris on 4/7/2019.
 */

public class EmailValidator {
    /**
     * Determines if a given email follows the basic format of the average email
     * Examples:
     * 1) isEmail(d@d.com)
     *      returns true
     * 2)
     *    isEmail(d@m@gmail.com)
     *      returns false
     * @param email
     * @return true if the email follows basic format
     */
    public static boolean isEmail(String email){
        if(email.isEmpty())
            return false;

        int atIndex = 0;
        boolean hasAt = false;
        for(int i = 0; i < email.length(); i++){
            if(email.charAt(i) == ' ')
                return false;

            if(email.charAt(i) == '@')
                if(!hasAt) {
                    hasAt = true;
                    atIndex = i;
                }
                else
                    return false;
        }
        if(!hasAt)
            return false;

        boolean hasDot = false;
        for(int j = 0; j < email.length(); j++){ // Correct email: email@x.com
            if(email.charAt(j) == '.') {
                if (j < atIndex) // e.mail@x.com
                    return false;
                else if (j == atIndex + 1) // email@.com
                    return false;
                else if (j == email.length() - 1) // email@x.
                    return false;
                else if(email.charAt(j + 1) == '.')
                    return false;
                else
                    return true;
            }
        }

        return false;
    }
}
