package postpc.musica;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

public class getSearchList extends AsyncTask<String, Void, String> {

	protected ArrayAdapter<youTubeEntery> listAdapter;

	public getSearchList(ArrayAdapter<youTubeEntery> adpt) {
		this.listAdapter = adpt;
	}

	protected String doInBackground(String... urls) {
		System.out.println("hi:"+urls[0]);
		URL jsonURL;
		try {
			jsonURL = new URL(urls[0]);
			URLConnection jc = jsonURL.openConnection(); 
			InputStream is = jc.getInputStream(); 
			String jsonTxt = IOUtils.toString(is);
			return jsonTxt;
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} 
		return "";
	}

	protected void onPostExecute(String jsonTxt) {
		try {
			JSONObject jj = new JSONObject(jsonTxt); 
			JSONObject jdata = jj.getJSONObject("feed"); 
			JSONArray aitems = jdata.getJSONArray("entry"); 
			for(int i=0; i< aitems.length()  ; i++){
				JSONObject item = aitems.getJSONObject(i); 
				JSONObject id = item.getJSONObject("id"); 
				String videoId = id.getString("$t"); 
				JSONObject titleJ = item.getJSONObject("title"); 
				String title = titleJ.getString("$t"); 
				JSONObject mGroup = item.getJSONObject("media$group"); 
				JSONArray tumbJ = mGroup.getJSONArray("media$thumbnail"); 
				JSONObject tumbJ0 = tumbJ.getJSONObject(0);
				String tumb = tumbJ0.getString("url");
				listAdapter.add(new youTubeEntery(title, tumb, videoId));
			}
		} catch (Exception e) {
			System.out.println("error: " + e.toString());
		}
	}
}
