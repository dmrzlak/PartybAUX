package HowdyIO.PartybAUX.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import HowdyIO.PartybAUX.Activities.MainActivity;
import HowdyIO.PartybAUX.Activities.PartyActivity;
import HowdyIO.PartybAUX.Fragments.SearchFragment;
import HowdyIO.PartybAUX.Model.ClientSimple;
import HowdyIO.PartybAUX.Model.ListCallback;
import HowdyIO.PartybAUX.Tools.QuickTools;
import HowdyIO.PartybAUX.Utils.DataProvider;
import HowdyIO.PartybAUX.Utils.SocketProvider;
import PartyBauxUI.R;
import ua.naiksoftware.stomp.StompClient;


/**
 * Created by Chris on 1/26/2019.
 */

public class PartyMembersView extends RelativeLayout implements PartyActivity.MemberObserver {

    Context context;
    PartyClosedListener partyClosedListener;

    private View root;
    private View bg;
    private LinearLayout drawer;
    private RecyclerView recyclerView;

    private ImageView closeButton;


    int startX;
    public static boolean isOpen = false;
    private OnCloseListener closeListener;
    private OnItemClickListener itemClickListener;


    public PartyMembersView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PartyMembersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PartyMembersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        root = inflate(getContext(), R.layout.view_party_members, this);
        bg = root.findViewById(R.id.background);
        drawer = root.findViewById(R.id.drawer);
        recyclerView = root.findViewById(R.id.recycler_view);
        bg.setClickable(false);
        bg.setFocusable(false);

        setUpDrawableAnim();
        setUpOnClicks();
        setUpRecyclerView();
        setUpWebSocket();

        root.post(new Runnable() {
            @Override
            public void run() {
                startX = (int) drawer.getX();
                drawer.setX(getWidth());

            }
        });
        //End Setup
    }

    public void connect() {
        setUpWebSocket();
    }

    private void setUpWebSocket() {
        if (QuickTools.partyID < 0) return;
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        ArrayList<String> users = new ArrayList<>();

        recyclerView.setAdapter(new MembersAdapter(users));
    }

    TwoWayAnimatedDrawable twoWayAnimatedDrawable;

    private void setUpDrawableAnim() {
        /*twoWayAnimatedDrawable = new TwoWayAnimatedDrawable(addToCheckmark,
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.add_to_checkmark),
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.checkmark_to_add));*/

    }

    private void setUpOnClicks() {
        bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    public void show() {
        isOpen = true;
        clearAnimation();
        root.setVisibility(VISIBLE);
        drawer.animate().x(startX).setDuration(QuickTools.ANIM_TIME_DEFAULT).setInterpolator(new DecelerateInterpolator()).start();
        bg.setAlpha(0f);
        bg.animate().alpha(1).setDuration(QuickTools.ANIM_TIME_DEFAULT).start();
        bg.setClickable(true);
        bg.setFocusable(true);
    }

    public void hide() {
        isOpen = false;
        drawer.animate().x(getWidth()).setDuration(QuickTools.ANIM_TIME_DEFAULT).setInterpolator(new DecelerateInterpolator()).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isOpen) root.setVisibility(GONE);
            }
        }, QuickTools.ANIM_TIME_DEFAULT);

        bg.setAlpha(1f);
        bg.animate().alpha(0).setDuration(QuickTools.ANIM_TIME_DEFAULT).start();
        bg.setClickable(false);
        bg.setFocusable(false);

        if (closeListener != null) closeListener.onClose();
    }

    public void setOnCloseListener(OnCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    @Override
    public void onMembersUpdated(ArrayList<ClientSimple> members) {
        if (members == null) return;
        if(members.isEmpty())
            closeParty();

        ArrayList<ClientSimple> users = new ArrayList<ClientSimple>(members);
        ArrayList<String> usernames = new ArrayList<>();
        for(ClientSimple client: users)
            usernames.add(client.getUsername());

        if(!usernames.contains(QuickTools.userName))
            closeParty();

        for(ClientSimple client: users) {
            if(client.getUsername().equals(QuickTools.userName))
                updateMyUserType(client.getUserType());
        }

        recyclerView.setAdapter(new MembersAdapter(usernames));
    }

    private void updateMyUserType(int userType){
        QuickTools.userType = userType;
        QuickTools.sharedPrefs(context).edit().putInt(QuickTools.SHARED_PREFS_USERTYPE, userType).commit();
    }

    private void closeParty(){
        if(partyClosedListener != null)
            partyClosedListener.onClose();
    }

    public interface OnCloseListener {
        public void onClose();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(String username, int index);
    }

    public class MembersAdapter extends RecyclerView.Adapter<VHMember> {

        ArrayList<String> userIds = new ArrayList<>();

        public MembersAdapter(ArrayList<String> userIds) {
            this.userIds = userIds;
        }

        @NonNull
        @Override
        public VHMember onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_partymember, parent, false);
            return new VHMember(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VHMember holder, int position) {
            String userID = userIds.get(holder.getAdapterPosition());
            holder.textViewUsername.setText(userID);
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(userID, holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return userIds.size();
        }
    }

    public class VHMember extends RecyclerView.ViewHolder {
        public String username;
        public String profilePicUrl;
        public TextView textViewUsername;
        public ImageView imageViewProfilePic;

        public VHMember(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.textview_username);
            imageViewProfilePic = view.findViewById(R.id.imageview_profile_picture);
        }
    }

    public void setPartyClosedListener(PartyClosedListener partyClosedListener) {
        this.partyClosedListener = partyClosedListener;
    }

    public interface PartyClosedListener {
        void onClose();
    }
}
