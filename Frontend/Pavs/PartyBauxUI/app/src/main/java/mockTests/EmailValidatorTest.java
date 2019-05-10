package mockTests;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import HowdyIO.PartybAUX.Model.User;
import HowdyIO.PartybAUX.Tools.EmailValidator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Chris on 4/7/2019.
 */

public class EmailValidatorTest {
    @Mock
    User mUser = mock(User.class);

    @Test
    public void ValidEmail(){
        when(mUser.getEmail()).thenReturn("allAmericansApplyAlliterationsAnally@dumbExtension.com");
        String email = mUser.getEmail();
        boolean wasValid = EmailValidator.isEmail(email);
        Assert.assertEquals(true, wasValid);
    }

    @Test
    public void InvalidEmailTwoAts(){
        when(mUser.getEmail()).thenReturn("thisSucks@real@ly.com");
        String email = mUser.getEmail();
        boolean wasValid = EmailValidator.isEmail(email);
        Assert.assertEquals(false, wasValid);
    }

    @Test
    public void InValidEmailTwoSubsequentPeriods(){
        when(mUser.getEmail()).thenReturn("thisSucks@really..com");
        String email = mUser.getEmail();
        boolean wasValid = EmailValidator.isEmail(email);
        Assert.assertEquals(false, wasValid);
    }

    @Test
    public void InvalidEmailWithSpace(){
        when(mUser.getEmail()).thenReturn("thisSucks@real ly.com");
        String email = mUser.getEmail();
        boolean wasValid = EmailValidator.isEmail(email);
        Assert.assertEquals(false, wasValid);
    }

    @Test
    public void ValidEmailVeryLong(){
        when(mUser.getEmail()).thenReturn("allAmericansApplyAlliterationsAnally@dumbExtension.com");
        String email = mUser.getEmail();
        boolean wasValid = EmailValidator.isEmail(email);
        Assert.assertEquals(true, wasValid);
    }

    @Test
    public void InValidEmailVeryLong(){
        when(mUser.getEmail()).thenReturn("letsgetthisbreadboyitsgettingbetterandbetteroutherebecauseweputintheWORKweputintheTIMEweputinthatSHMONEYandthistimetheyregonnaseewhotherealch@mpsareouthereletsGOOOO@dumbExtension.com");
        String email = mUser.getEmail();
        boolean wasValid = EmailValidator.isEmail(email);
        Assert.assertEquals(false, wasValid);
    }

    @Test
    public void isAlphaTest1(){
        Assert.assertFalse(isAlpha("ABCD_1"));
    }


    @Test
    public void isAlphaTest2(){
        Assert.assertFalse(isAlpha("ABCD&1"));
    }

    @Test
    public void isAlphaTest4(){
        Assert.assertFalse(isAlpha("ABCD=1"));
    }

    @Test
    public void isAlphaTest5(){
        Assert.assertFalse(isAlpha("ABCD/1"));

    }

    @Test
    public void isAlphaTest6(){
        Assert.assertFalse(isAlpha("ABCD!1"));
    }

    @Test
    public void isAlphaTest7(){
        Assert.assertFalse(isAlpha("ABCD-1"));
    }

    @Test
    public void isAlphaTest8(){
        Assert.assertTrue(isAlpha("ABCD1"));

    }



    private boolean isAlpha(String text) {
        for(int i = 0; i < text.length(); i++){
            char cur = text.charAt(i);
            if(  (cur < '0' || cur > '9') && (cur < 'A' || cur > 'Z')&& (cur < 'a' || cur > 'z')){
                return false;
            }
        }
        return true;
    }




}
