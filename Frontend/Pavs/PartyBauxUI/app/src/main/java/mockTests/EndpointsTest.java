package mockTests;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import HowdyIO.PartybAUX.Model.Callback;
import HowdyIO.PartybAUX.Model.User;
import HowdyIO.PartybAUX.Utils.AppController;
import HowdyIO.PartybAUX.Utils.DataProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EndpointsTest {

    @Mock
    User mUser = mock(User.class);

    AppController appController = new AppController();

    int reqResult;


    @Test
    public void CreateExistingUser(){
        when(mUser.getUsername()).thenReturn("dylan");
        String name = mUser.getUsername();
        DataProvider.addUser(name, "a", "a", new Callback() {
            @Override
            public void onResult(String result) {
                reqResult = Integer.parseInt(result);
            }
        });
        Assert.assertEquals(-2, reqResult);
    }

    @Test
    public void CreateExistingEmailUser(){
        when(mUser.getUsername()).thenReturn("dylan@gmail");
        String name =mUser.getUsername();
        DataProvider.addUser("a", name, "a", new Callback() {
            @Override
            public void onResult(String result) {
                reqResult = Integer.parseInt(result);
            }
        });
        Assert.assertEquals(-1, reqResult);
    }

    @Test
    public void CreateNewUser(){
        when(mUser.getUsername()).thenReturn("evan@gmail");
        String name =mUser.getUsername();
        DataProvider.addUser("evan", name, "a", new Callback() {
            @Override
            public void onResult(String result) {
                reqResult = Integer.parseInt(result);
            }
        });
        Assert.assertEquals(0, reqResult);
    }
}
