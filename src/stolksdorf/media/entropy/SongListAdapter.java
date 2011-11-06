package stolksdorf.media.entropy;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class SongListAdapter extends BaseAdapter {

	 private LayoutInflater mInflater;	
	 ArrayList<Song> data;
	 float size;

	 public SongListAdapter(Context context,  ArrayList<Song> library, float text_size) {	
		 mInflater = LayoutInflater.from(context);
		 data = library;
		 size = text_size;
	 }
	 public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;		
			 if (convertView == null) {
				 convertView = mInflater.inflate(R.layout.song, null);
				 holder = new ViewHolder();
				 holder.title = (TextView) convertView.findViewById(R.id.title);			
				 holder.artist = (TextView) convertView.findViewById(R.id.artist);
				 		
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }		 
			 holder.title.setText(data.get(position).title);
			 holder.artist.setText(data.get(position).artist);	
			 holder.title.setTextSize(size); //TODO: Modify later with playcount, pass a hashmap of id to font size

			 return convertView;

	}
	public int getCount() {
		return data.size();
	}
	public Object getItem(int position) {
		return position;
	}
	public long getItemId(int position) {
		return position;
	}
}
	
class ViewHolder {
	TextView title;	
	 TextView artist;
}
