package HowdyIO.PartybAUX.Model;

import java.util.ArrayList;
import java.util.Collection;

import kaaes.spotify.webapi.android.models.Track;

public class Queue {

    private ArrayList<String> q;
    private ArrayList<String> removed;

    /**
     * Constructor for the Queue
     * Generates a pair of empty array lists
     * for the Queue
     */
    public Queue(){
        q = new ArrayList<>();
        removed = new ArrayList<>();
    }

    /**
     * <pre>
     * Push adds a song to the end of the Queue
     * Examples:
     * {@code
     *     Queue q = new Queue;
     *     q.push(";alsdkjfldsj")
     *     //Returns true and queue looks like:
     *     //-->{";alsdkjfldsj"}
     * }
     * </pre>
     * @param song
     * @return true if the string was added to the queue successfully
     */
    public boolean push(String song){
        q.add(song);
        return true;
    }

    /**
     * <pre>
     * Push adds a song to the end of the Queue
     * Examples:
     * {@code
     * Queue q = new Queue;
     * q.push({";alsdkjfldsj",
     *          "ds;lkjdfosijf"
     *        })
     *      //Returns true and queue looks like:
     *     //-->{";alsdkjfldsj",
     *           "ds;lkjdfosijf"
     *           }
     * }
     * </pre>
     * @param c
     * @return true if the collection was added to the queue successfully
     */
    public boolean pushAll(Collection<String> c){
        q.addAll(c);
        return true;
    }

    /**
     * <pre>
     * Removes and Returns the first string and shifts all elements forward
     * Example:
     * {@code
     *       Queue q = new Queue;
     *       q.push(";alsdkjfldsj")
     *       q.pop();
     *          //returns ";alsdkjfldsj"
     * }
     * </pre>
     * @return First String in queue, null if the queue is empty
     */
    public String pop(){
        if(q.size() == 0)
            return null;

        removed.add(peek());
        return q.remove(0);
    }

    public ArrayList<String> popAll(int amtToPop) {
        ArrayList<String> result = new ArrayList<>();

        if(q.size() == 0)
            return result;

        for(int i = 0; i < amtToPop; i++){
            result.add(pop());
        }

        return result;
    }

    /**
     * <pre>
     * Returns the first string and shifts all elements forward
     * Example:
     * {@code
     *       Queue q = new Queue;
     *       q.push(";alsdkjfldsj")
     *       q.peek();
     *          //returns ";alsdkjfldsj"
     * }
     * </pre>
     * @return First String in queue, null if the queue is empty
     */
    public String peek(){
        if(q.size() == 0)
            return null;

        return q.get(0);
    }
    /**
     * <pre>
     * Removes and Returns the ith string and shifts all elements forward
     * Example:
     * {@code
     *       Queue q = new Queue;
     *       q.push(";alsdkjfldsj")
     *       q.remove(0);
     *          //returns ";alsdkjfldsj"
     * }
     * </pre>
     * @return First ith in queue
     */
    public String removeIndex(int i){
        removed.add(q.get(i));

        return q.remove(i);
    }

    /**
     * <pre>
     *     Shifts an element to the index specified, moves other elements accordingly
     *     Example:
     *    {@code
     *
     *             Queue q = new Queue();
     *             q.add({ 
     *                 "a;lkjfa;dslk",
     *                 ";lasdkjfaosd;lkfj" 
     *                 "a;lfkjfdas;l" 
     *             }); 
     *             q.shift(2, 0); 
     *                 //q looks like 
     *                 //{ 
     *                 //    "a;lfkjfdas;l", 
     *                 //    "a;lkjfa;dslk", 
     *                 //    ";lasdkjfaosd;lkfj" 
     *                 //}
     *     }
     *</pre>
     * @param from Index of item to be moved
     * @param to Index of where the item will be moved to
     * @return true on successful shift
     */
    public boolean shift(int from, int to){
        if(     q.size() == 0 ||
                from >= q.size() || from < 0 ||
                to >= q.size() || to < 0){
            return false;
        }
        String temp = q.remove(from);
        int min = Math.min(from, to);
        ArrayList<String> shift = new ArrayList<>();
        int i = 0;
        for( ; i < to; i++){
            shift.add(q.get(i));
        }

        shift.add(temp);
        

        for( ;i < q.size(); i++){
            shift.add(q.get(i));
        }

        q = shift;

        return true;
    }

    /**
     * <pre>
     *     Whether an element is in the queue
     * </pre>
     * @param track
     * @return
     */
    public boolean contains(Track track){
        return track != null && contains(track.uri);
    }

    /**
     * <pre>
     *     Whether an element is in the queue
     * </pre>
     * @param uri
     * @return
     */
    public boolean contains(String uri){
        if(uri.isEmpty()) return false;

        for(String myUri: q)
            if(myUri.equals(uri)) return true;

        return false;
    }

    /**
     * Returns Size of the queue
     * @return Size of the queue
     */
    public int size(){
        return q.size();
    }


    /**
     * Returns true if the queue is empty
     * @return true if the queue is empty
     */
    public boolean isEmpty(){
        return size() == 0;
    }


    /**
     * Returns an array list of the queue
     * @return array list of the queue
     */
    public ArrayList<String> toArray(){
        return q;
    }

    public void update(ArrayList<String> trackUris) {
        for(int i = 0; i < removed.size(); i++){
            String lostUri = removed.get(i);
            if(trackUris.contains(lostUri))
                trackUris.remove(lostUri);
            else
                removed.remove(i == 0? i: i--);
        }
        q.clear();
        q.addAll(trackUris);
    }

    public void clear(){
        q.clear();
    }

}
