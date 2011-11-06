package stolksdorf.media.entropy;

import stolksdorf.media.entropy.R;
import android.app.AlertDialog;
import android.content.Context;
import android.os.RemoteException;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

//Creates and handles all events for the buttons of entropy

public class Buttons{
	
	private Context alertBuilder;

	private ImageButton btnPrevious;
	private ImageButton btnPlay;
	private ImageButton btnNext;
	
	private Vibrator hapticFeedback;	
	
	public Buttons(ImageButton prev, ImageButton next, ImageButton play, Vibrator feedback, Context temp ){  	
	  	btnPrevious = prev;
	  	btnPlay = next;
	  	btnNext = play; 
	  	
	  	alertBuilder = temp;
	  	
	  	
	  	hapticFeedback = feedback;
	  	//setupListeners();
	}
	
	public OnClickListener testListener = new OnClickListener() {
	    public void onClick(View v) {
	    	hapticFeedback.vibrate(50);
	    }
	};
	
	public void setupListeners(){
		btnPrevious.setOnClickListener(new ImageButton.OnClickListener() { 
        	public void onClick (View v){ 
        		hapticFeedback.vibrate(50);
        		//eInterface.previous();
				btnPlay.setBackgroundResource(R.drawable.pause); 
				
				new AlertDialog.Builder(alertBuilder)
	            .setMessage("Temp")
	            .setPositiveButton("OK", null)
	            .show();
        		
        	}
        }); 
        
    	btnPlay.setOnClickListener(new ImageButton.OnClickListener() { 
        	public void onClick (View v){         		
        		hapticFeedback.vibrate(50);
        	//	try {
					/*if(eInterface.isPlaying()){
						eInterface.pause();	
						btnPlay.setBackgroundResource(R.drawable.play);
					}else{
						eInterface.resume();
						btnPlay.setBackgroundResource(R.drawable.pause);
					} */
        			btnPlay.setBackgroundResource(R.drawable.pause);
			/*	} catch (RemoteException e) {
					e.printStackTrace();
				} */
        	}
        }); 
        
    	btnNext.setOnClickListener(new ImageButton.OnClickListener() { 
        	public void onClick (View v){ 
        	//	try {
					//eInterface.next();
					btnPlay.setBackgroundResource(R.drawable.pause);
			/*	} catch (RemoteException e) {
					e.printStackTrace();
				}*/
        		hapticFeedback.vibrate(50);
        	}
        }); 
		
	}
	
    public void checkPlayButton(){
    	/*try {
			if(eInterface.isPlaying())
				btnPlay.setBackgroundResource(R.drawable.pause);
			else
				btnPlay.setBackgroundResource(R.drawable.play);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
    }
	
	
	
	
}