package stolksdorf.media.entropy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

//Creates and handles all events for the sub-menus of entropy

public class Menus{
	
	//private AlertDialog.Builder alertBuilder;
	//private Context alertContext;
	
	private AlertDialog sortAlert;
	private AlertDialog orderAlert;
	private AlertDialog viewAlert;
	
	int CURRENT_ORDER;
	int CURRENT_VIEW;
	int CURRENT_SORT=-1;
	
	
	//Move to Settings? Make a Constants class?
	final CharSequence[] SORT_OPTIONS = {"By Title", "By Artist"};	
	final CharSequence[] ORDER_OPTIONS = {"Shuffle", "Random", "Single Loop"};	
	final CharSequence[] VIEW_OPTIONS = {"Tiny", "Normal", "Big", "Biblical"};
	
	
	
	public Menus(Context context){
		//alertContext = context;	
		BuildMenus(context);
	}
	
	public void BuildMenus(Context alertContext){
     	//Sort Dialog
     	AlertDialog.Builder sort_builder = new AlertDialog.Builder(alertContext);
     	sort_builder.setTitle("Choose a sort order");
     	sort_builder.setSingleChoiceItems(SORT_OPTIONS, CURRENT_SORT, new DialogInterface.OnClickListener() {
     	    public void onClick(DialogInterface dialog, int item) { 
     	    /*	sortSongs(item); 
     	    	writePrefs(Settings.SORT_PREF, item); */
     	    	dialog.dismiss(); 
     	    	} 
     	});
     	sortAlert = sort_builder.create();
     	
     	//Order Dialog
     	AlertDialog.Builder order_builder = new AlertDialog.Builder(alertContext);
     	order_builder.setTitle("Choose play order");
     	order_builder.setSingleChoiceItems(ORDER_OPTIONS, CURRENT_ORDER, new DialogInterface.OnClickListener() {
     	    public void onClick(DialogInterface dialog, int item) { 
     	    	/*CURRENT_ORDER = item; 
     	    	writePrefs(Settings.ORDER_PREF, item); */
     	    	dialog.dismiss(); 
     	    } 
     	});
     	orderAlert = order_builder.create();
     	
     	//View Dialog
     	AlertDialog.Builder view_builder = new AlertDialog.Builder(alertContext);
     	view_builder.setTitle("Choose a list size order");
     	view_builder.setSingleChoiceItems(VIEW_OPTIONS, CURRENT_VIEW, new DialogInterface.OnClickListener() {
     	    public void onClick(DialogInterface dialog, int item) { 
     	    	/*changeView(item); 
     	    	writePrefs(Settings.VIEW_PREF, item); */
     	    	dialog.dismiss(); 
     	    } 
     	});
     	viewAlert = view_builder.create(); 	
		
	}
	
	
    public boolean createMenu(Menu menu) {
        menu.add(0, Settings.MENU_SORT, 0, "Sort Songs"); //Make into constants
        menu.add(0, Settings.MENU_ORDER, 0, "Play Order");
        menu.add(0, Settings.MENU_VIEW, 0, "View");
        return true;
    }

    public boolean itemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case Settings.MENU_SORT:
            	sortAlert.show();             
                return true;
            case Settings.MENU_ORDER:
            	orderAlert.show();
            	return true;
            case Settings.MENU_VIEW:
            	viewAlert.show();
            	return true;
        }
        return false;
    }
	
	
	
}