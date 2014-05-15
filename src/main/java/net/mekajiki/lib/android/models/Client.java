package net.mekajiki.lib.android.models;

import android.os.AsyncTask;

import net.mekajiki.lib.android.R;
import net.mekajiki.lib.android.utils.AsyncTaskResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client<M extends IRootModel> extends AsyncTask<BasicNameValuePair, Integer, AsyncTaskResult<List<M>>> {
    private INetworkListener<M> listener;
    private Class<M> klass;
    private IParser<M> parser;

    public Client(INetworkListener<M> listener, Class<M> klass) {
        this(listener, klass, new DefaultParser(klass));
    }

    public Client(INetworkListener<M> listener, Class<M> klass, IParser parser) {
        this.listener = listener;
        this.klass = klass;
        this.parser = parser;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onProgressUpdate(Integer... values) {
    }

    @Override
    public void onPostExecute(AsyncTaskResult<List<M>> result) {
        if (result.isError) {
            listener.onFailure(result.resourceId);
        }
        else {
            listener.onSuccess(result.content);
        }
    }

    @Override
    public AsyncTaskResult<List<M>> doInBackground(BasicNameValuePair... params) {
        try {
            M model = getInstance();
            List<BasicNameValuePair> paramArray = Arrays.asList(params);

            String url = model.getBaseUrl();

            if (paramArray.size() > 0) {
                url += "?"
                    + URLEncodedUtils.format(paramArray, "utf-8");
            }
            HttpGet getRequest = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(getRequest);

            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    break;
                case HttpStatus.SC_NOT_FOUND:
                    return AsyncTaskResult.createFailureResult(R.string.server_not_found);
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    return AsyncTaskResult.createFailureResult(R.string.server_internal_server_error);
                default:
                    return AsyncTaskResult.createFailureResult(R.string.server_other_error);
            }

            OutputStream stream = new ByteArrayOutputStream();
            response.getEntity().writeTo(stream);
            String jsonString = stream.toString();

            Object json;
            if (model.getJsonRootClass() == JSONObject.class) {
                json = new JSONObject(jsonString);
            }
            else {
                json = new JSONArray(jsonString);
            }
            List<M> models = parser.parseJsonObject(json);

            return AsyncTaskResult.createSuccessResult(models);
        }
        catch (ClientProtocolException e) {
            return AsyncTaskResult.createFailureResult(R.string.server_protocol_error);
        }
        catch (IOException e) {
            return AsyncTaskResult.createFailureResult(R.string.server_io_error);
        }
        catch (JSONException e) {
            return AsyncTaskResult.createFailureResult(R.string.server_json_error);
        }
        catch (InstantiationException e) {
            return AsyncTaskResult.createFailureResult(R.string.application_error);
        }
        catch (IllegalAccessException e) {
            return AsyncTaskResult.createFailureResult(R.string.application_error);
        }
    }

    private M getInstance() throws InstantiationException, IllegalAccessException {
        return klass.newInstance();
    }

    static class DefaultParser<M extends AModel & IRootModel> implements IParser<M> {
        private Class<M> klass;

        public DefaultParser(Class<M> klass) {
            this.klass = klass;
        }

        @Override
        public List<M> parseJsonObject(Object json) throws IllegalAccessException, InstantiationException, JSONException {
            M model = getInstance();
            JSONArray attrsArray;
            if (json.getClass() == JSONObject.class) {
                attrsArray = ((JSONObject)json).getJSONArray(model.getJsonRoot());
            }
            else {
                attrsArray = (JSONArray)json;
            }

            List<M> models = new ArrayList<M>(attrsArray.length());
            for (int i = 0; i < attrsArray.length(); i++) {
                model = getInstance();
                JSONObject attrs = attrsArray.getJSONObject(i);
                model.setAttributes(attrs);
                models.add(model);
            }
            return models;
        }

        private M getInstance() throws InstantiationException, IllegalAccessException {
            return klass.newInstance();
        }
    }
}