package karki.bipul.certificatepinning;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

public class ConnectionTask extends AsyncTask<Void, Void, Object> {
	private Context mContext;

	public ConnectionTask(Context context) {
		mContext = context;
	}

	@Override
	protected Object doInBackground(Void... params) {

		Object result = null;

		try {
			//URL url = new URL("https://www.facebook.com"); 
			URL url = new URL("https://www.google.com"); // trust only this
															// site
			InputStream in = makeRequest(mContext, url);
			copyInputStreamToOutputStream(in, System.out);

		} catch (Exception ex) {

			// Log error
			Log.e("doInBackground", ex.toString());

			// Prepare return value
			result = (Object) ex;
		}

		return result;
	}

	@Override
	protected void onPostExecute(Object result) {
		// MainActivity.myText.setText("Test");
		if (result instanceof Exception) {
			MainActivity.myText.setText("Untrusted Certificate :-( \n\n"
					+ result);
			return;
		}
		MainActivity.myText.setText(" Trusted Certificate :-)");
	}

	private void copyInputStreamToOutputStream(InputStream in, PrintStream out) {
		// TODO Auto-generated method stub
		byte[] buffer = new byte[1024]; // Adjust if you want
		int bytesRead = 0;
		try {
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("TAG", String.valueOf(bytesRead));

	}

	private InputStream makeRequest(Context context, URL url)
			throws IOException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException, KeyManagementException {
		AssetManager assetManager = context.getAssets();
		InputStream keyStoreInputStream = assetManager.open("keystore.bks");
		KeyStore trustStore = KeyStore.getInstance("BKS");

		trustStore.load(keyStoreInputStream, "testing".toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(trustStore);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tmf.getTrustManagers(), null);

		HttpsURLConnection urlConnection = (HttpsURLConnection) url
				.openConnection();
		urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

		return urlConnection.getInputStream();
	}

}
