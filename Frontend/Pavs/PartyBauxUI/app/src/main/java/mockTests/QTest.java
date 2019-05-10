//package mockTests;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.stubbing.OngoingStubbing;
//
//import java.util.ArrayList;
//
//import HowdyIO.PartybAUX.Model.Callback;
//import HowdyIO.PartybAUX.Model.Queue;
//import HowdyIO.PartybAUX.Model.User;
//import HowdyIO.PartybAUX.Tools.uriValidator;
//import HowdyIO.PartybAUX.Utils.AppController;
//import HowdyIO.PartybAUX.Utils.DataProvider;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class QTest {
//
//
//    /*
//    *
//    *URI Tests
//    *
//     */
//    @Mock
//    Queue mQueue = mock(Queue.class);
//    Queue q = new Queue();
//
//
//    ArrayList<String> uris = new ArrayList<>();
//    ArrayList<String> uriExpected = new ArrayList<>();
//    ArrayList<String> swapExpected = new ArrayList<>();
//    ArrayList<String> manyPushExpected = new ArrayList<>();
//    ArrayList<String> manyPopExpected = new ArrayList<>();
//
//    ArrayList<String> actual = new ArrayList<>();
//
//
//    @Before
//    public void init() {
//        uris.add("spotify:track:40v0UzCgsJf9uj4R9NvM3t^^1");
//        uris.add("spotiy:track:40v0UzCgsJf9uj4R9NvM3t^^2");
//        uris.add("spotify:trck:40v0UzCgsJf9uj4R9NvM3t^^3");
//        uris.add("spotify:track:40v0UzCgsJf9uj4R9NvM3t^^4");
//        uris.add("spotify:track:40v0UzCgsJf9uj4R9NvM3t^^5");
//        uris.add("spotify:track:40v0UzCgsJf9uj4R9NvM3t^^6");
//
//
//        uriExpected.add("spotify:track:40v0UzCgsJf9uj4R9NvM3t^^1");
//        uriExpected.add("spotify:track:40v0UzCgsJf9uj4R9NvM3t^^4");
//        uriExpected.add("spotify:track:40v0UzCgsJf9uj4R9NvM3t^^5");
//        uriExpected.add("spotify:track:40v0UzCgsJf9uj4R9NvM3t^^6");
//
//
//        for(int i=1; i<=100; i++){
//            manyPushExpected.add(""+i);
//        }
//
//        swapExpected.add("0");
//        swapExpected.add("1");
//        swapExpected.add("2");
//        swapExpected.add("7");
//        swapExpected.add("3");
//        swapExpected.add("4");
//        swapExpected.add("5");
//        swapExpected.add("6");
//        swapExpected.add("8");\
//        swapExpected.add("9");
//        swapExpected.add("10");
//        swapExpected.add("11");
//        swapExpected.add("12");
//    }
//
//    @Test
//    public void uriCheckTest() {
//        int i = 0;
//        for (i = 0; i < uris.size(); i++) {
//            if (uriValidator.isURI(uris.get(i))) {
//                when(mQueue.push(uris.get(i))).thenReturn((actual.add(uris.get(i))));
//                mQueue.push(uris.get(i));
//            }
//        }
//
//        assertEquals(uriExpected, actual);
//    }
//
//    @Test
//    public void popIsStilluri() {
//        ArrayList<String> temp= new ArrayList<>();
//        for(int i=0; i<uris.size(); i++){
//            temp.add(uris.get(i));
//        }
//
//        while (temp.size() != 0) {
//            when(mQueue.pop()).thenReturn(temp.remove(0));
//            String popped = mQueue.pop();
//            if(uriValidator.isURI(popped)){
//                actual.add(popped);
//            }
//        }
//
//
//        assertEquals(uriExpected, actual);
//    }
//
//    @Test
//    public void toArrayOnlyURIS(){
//        int i = 0;
//        for (i = 0; i < uris.size(); i++) {
//            if (uriValidator.isURI(uris.get(i))) {
//                when(mQueue.push(uris.get(i))).thenReturn((actual.add(uris.get(i))));
//                mQueue.push(uris.get(i));
//            }
//        }
//        when(mQueue.toArray()).thenReturn(actual);
//        assertEquals(uriExpected, mQueue.toArray());
//    }
//
//
//    /*
//    *
//    * Queue Junit
//    *
//     */
//    @Test
//    public void manyPush(){
//        for(int i = 1; i<=100; i++){
//            q.push(""+i);
//        }
//        assertEquals(manyPushExpected, q.toArray());
//    }
//    @Test
//    public void manyPop(){
//        for(int i = 1; i<=100; i++){
//            q.push(""+i);
//        }
//        for(int i = 1; i<=100; i++){
//            q.pop();
//        }
//        assertEquals(manyPopExpected, q.toArray());
//    }
//    @Test
//    public void swap(){
//        for(int i = 0; i<=12; i++){
//            q.push(""+i);
//        }
//        q.shift(7, 3);
//        assertEquals(swapExpected, q.toArray());
//    }
//
//    @Test
//    public void shiftOutofBounds1(){
//        q.pushAll(uris);
//        assertFalse(q.shift(-1, 2));
//    }
//    @Test
//    public void shiftOutofBounds2(){
//        q.pushAll(uris);
//        assertFalse(q.shift(15, 2));
//    }
//
//    @Test
//    public void shiftOutofBounds3(){
//        q.pushAll(uris);
//        assertFalse(q.shift(0, -1));
//    }
//
//    @Test
//    public void shiftOutofBounds4(){
//        q.pushAll(uris);
//        assertFalse(q.shift(0, 15));
//    }
//
//    @Test
//    public void shiftEmptyQ(){
//        assertFalse(q.shift(0, -1));
//    }
//
//    @Test
//    public void addAll(){
//        q.pushAll(uris);
//        assertEquals(uris, q.toArray());
//    }
//
//
//}