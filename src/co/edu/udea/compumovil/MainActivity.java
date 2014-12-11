package co.edu.udea.compumovil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

	private TextView ciudad,temperatura,descripcion; 
	private EditText lbldato;
	private Button btnRequest;
	private ImageView icono;
	private Bitmap imagen;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ciudad = (TextView)findViewById(R.id.lblCiudad);
		temperatura = (TextView)findViewById(R.id.lblTemperatura);
		descripcion = (TextView)findViewById(R.id.lblDescripcion);
		lbldato = (EditText)findViewById(R.id.obtenerCiudad);
		icono = (ImageView)findViewById(R.id.icono);
		btnRequest = (Button)findViewById(R.id.btnRequest);
		btnRequest.setOnClickListener(this);
		
	}
	
	
	@Override
	public void onClick(View v) {
		if(v.getId() == btnRequest.getId()){
			String buscar = lbldato.getText().toString();
			WeatherTask weather = new WeatherTask();
			weather.execute(buscar);
			lbldato.setText("");
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class WeatherTask extends AsyncTask<String, Void, Void> {
		private static final String TAG = "WeatherTask";
		private String Content;
		private String Error = null;
		private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
		String data = "";

		@Override
		protected void onPreExecute() {
			// super.onPreExecute();
			// Start Progress Dialog (Message)
			Dialog.setMessage("Please wait..");
			Dialog.show();
		}

		@Override
		// Call after onPreExecute method
		protected Void doInBackground(String... params) {
			/************ Make Post Call To Web Server ***********/
			// BufferedReader reader=null;
			// Send data
			try {
				data = ((new WeatherHttpClient()).getWeatherData(params[0]));
				
				/*------ Para cargar los iconos---------------*/
				JSONObject jsonResponse = new JSONObject(data);
				String OutputData = jsonResponse.getJSONArray("weather")
						.getJSONObject(0).optString("icon").toString();
				imagen = ((new WeatherHttpClient()).retornaIcono(OutputData));
				//-----------------------------------------------
			} catch (Exception ex) {
				Error = ex.getMessage();
			}
			/*****************************************************/
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			// NOTE: You can call UI Element here.
			// Close progress dialog
			Dialog.dismiss();
			if (Error != null) {
			} else {
				String OutputData = "";
				JSONObject jsonResponse;
				try {
					jsonResponse = new JSONObject(data);
					jsonResponse.optJSONArray("Android");
					OutputData = jsonResponse.optString("name").toString();
					ciudad.setText(OutputData);
					OutputData = jsonResponse.getJSONObject("main")
							.optString("temp").toString();
					temperatura.setText(OutputData +"°C");
					OutputData = jsonResponse.getJSONArray("weather")
							.getJSONObject(0).optString("main").toString();
					descripcion.setText( OutputData );
					icono.setImageBitmap(imagen);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
