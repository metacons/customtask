package com.metacons.customtaskapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import com.metacons.customtaskapi.R;

public class CustomTask extends AsyncTask<Object, String, String> implements
		DialogInterface.OnCancelListener {

	private CustomTaskFinishedListener callback;

	private Context cnx = null;
	public ProgressDialog progres = null;
	public Message msg = null;
	public boolean showProgressBar = true;

	public CustomTask(Context context, CustomTaskFinishedListener clb) {
		cnx = context;
		this.callback = clb;
	}

	public CustomTask(Context context, CustomTaskFinishedListener clb,
			boolean showProgressBar) {
		cnx = context;
		this.callback = clb;
		this.showProgressBar = showProgressBar;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		try {
			if (showProgressBar) {
				progres = ProgressDialog.show(cnx, "Bağlanıyor",
						"Lütfen Bekleyiniz");
				setCommonProcess();
			}
		} catch (Exception ex) {
			Log.e("Custom Task", ex.getMessage());
		}
	}

	/***
	 * @param 1 URL Address
	 * @param 2 Post
	 */
	@Override
	protected String doInBackground(Object... params) {

		msg = new Message();
		msg.what = FCodes.STATUS_OK;
		try {
			msg.what = FCodes.STATUS_OK;
			if (params.length == 2) {
				// Post işlemidir
				msg = callPostService(params[0].toString(),
						params[1].toString());
			} else {
				// get işlemidir
				msg = callService(params[0].toString());
			}

		} catch (Exception ex) {
			Log.e("ex", ex.toString());
			msg.what = FCodes.STATUS_ERROR;
			msg.obj = ex.toString();
		}

		return "";
	}

	private Message callPostService(String urlAddress, String post) {
		Message msg = new Message();
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(convertUTF8(urlAddress));

		try {
			Log.e("Post Address", convertUTF8(urlAddress));
			Log.e("Request", post);
			StringEntity se = new StringEntity(post, HTTP.UTF_8);
			se.setContentType("application/json");
			httppost.setEntity(se);
			HttpResponse httpresponse = httpclient.execute(httppost);
			if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity resEntity = httpresponse.getEntity();
				String strResponse = EntityUtils.toString(resEntity);
				Log.d("Response", strResponse);

				msg.what = FCodes.STATUS_OK;
				msg.obj = strResponse;

			} else {
				Log.e("Error", "Dönüş Yok");
				msg = setMessage(FCodes.STATUS_ERROR,
						"Servis URL Cevap Vermedi. Error Code: "
								+ httpresponse.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException ex) {
			Log.e("Error", ex.getMessage());
			msg = setMessage(FCodes.STATUS_IOEXCEPTION, ex.toString());
		} catch (IOException ex) {
			Log.e("Error", ex.getMessage());
			msg = setMessage(FCodes.STATUS_IOEXCEPTION, ex.toString());
		}
		return msg;
	}

	private Message callService(String xmlAddress) {

		Message msg = new Message();
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			Log.e("request", convertUTF8(xmlAddress));

			request.setURI(new URI(convertUTF8(xmlAddress)));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			Log.e("response", sb.toString());

			msg.what = FCodes.STATUS_OK;
			msg.obj = sb.toString();

		} catch (Exception ex) {
			msg.what = FCodes.STATUS_ERROR;
			msg.obj = ex.toString();
			Log.e("Error", ex.toString());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return msg;
	}

	private Message setMessage(int what, Object obj) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		return msg;
	}

	private String convertUTF8(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if (ch == ' ') {
				sbuf.append("%20");
			} else {
				sbuf.append((char) ch);
			}
		}
		return sbuf.toString();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		progres.setTitle(values[0]);
		progres.setMessage(values[1]);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		callback.taskFinished(msg);
		if (progres.isShowing())
			progres.dismiss();
	}

	private void setCommonProcess() {
		progres.setCanceledOnTouchOutside(true);
		progres.setOnCancelListener(this);
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		this.cancel(true);
		if (msg == null) {
			msg = new Message();
		}
		msg.what = FCodes.STATUS_CANCELLED;
		callback.taskFinished(msg);
	}
}
