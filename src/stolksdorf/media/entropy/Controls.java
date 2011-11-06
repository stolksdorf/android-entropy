package stolksdorf.media.entropy;

import stolksdorf.media.entropy.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class Controls extends LinearLayout {

	public Controls(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater layout = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout.inflate(R.layout.controls, this, true);
		
		//TODO: this.findViewById  and add event handlers here
		//	I suggest making static methods in whatever class is actually _running_
		//	the music like 'public static stopSong()' etc. that you can call from
		//	the event handlers
		//	ie: if the main class is Entropy:  Entropy.stopSong();
		//		(then you will also need to make 'currentSong' vars and such static)
	}

}