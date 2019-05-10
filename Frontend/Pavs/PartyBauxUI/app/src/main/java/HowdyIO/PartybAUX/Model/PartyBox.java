package HowdyIO.PartybAUX.Model;

import java.util.ArrayList;

public class PartyBox {
    private ArrayList<ClientSimple> partyMembers;
    private ArrayList<Song> songs;

    public PartyBox() {
    }

    public ArrayList<ClientSimple> getPartyMembers() {
        return partyMembers;
    }

    public void setPartyMembers(ArrayList<ClientSimple> partyMembers) {
        this.partyMembers = partyMembers;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
