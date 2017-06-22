package com.news.sdk.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.reflect.TypeToken;
import com.news.sdk.entity.AdDetailEntity;
import com.news.sdk.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/9.
 */
public class SplashADRequestPost<T> extends JsonRequest<T> {

    private HashMap mHeader = new HashMap();
    public SplashADRequestPost(String url, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url,requestBody, listener, errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            JSONObject jsonObject = new JSONObject(jsonString);
            String code = jsonObject.optString("code");
            if ("2000".equals(code)){
                AdDetailEntity result = GsonUtil.deSerializedByType(jsonObject.getJSONObject("data").toString(), new TypeToken<AdDetailEntity>() {
                }.getType());
                return (Response<T>) Response.success(result,
                        HttpHeaderParser.parseCacheHeaders(response));
            }else {
                return Response.error(new VolleyError("服务器异常!" + code));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }
    public void setRequestHeaders(HashMap headers) {
        this.mHeader = headers;
    }
}
