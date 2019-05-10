package HowdyIO.PartybAUX.Model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import PartyBauxUI.R;

/**
 * Created by Chris on 1/24/2019.
 */

public class VHSearchResultHeader extends RecyclerView.ViewHolder {

    TextView query;
    public VHSearchResultHeader(View itemView) {
        super(itemView);
        query = itemView.findViewById(R.id.textview_title);
    }
}
