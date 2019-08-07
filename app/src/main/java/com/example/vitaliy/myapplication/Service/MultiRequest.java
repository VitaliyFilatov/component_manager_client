package com.example.vitaliy.myapplication.Service;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MultiRequest extends StringRequest {
    final String BOUNDARY = "myboundary";

    private final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
    HttpEntity entity = new MultipartEntity();

    private Response.Listener<String> mListener = null;
    private Response.ErrorListener mEListener;
    //
    private final File mFilePart;
    protected int complexId;
    protected String deviceId;

    public MultiRequest(String deviceId, int complexId, String url, Listener<String> rListener, ErrorListener eListener, File file) {
        super(Method.POST, url, rListener, eListener);
        setShouldCache(false);
        this.deviceId = deviceId;
        this.complexId = complexId;
        mListener = rListener;
        mEListener = eListener;
        mFilePart = file;
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        entityBuilder.setBoundary(BOUNDARY);

        ContentType contentType = ContentType.create("image/jpg");
        entityBuilder.addBinaryBody("image", file, contentType, "ARM_"+complexId+".jpg");
        entity = entityBuilder.build();
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    /**
     * Overrides the base class to add the Accept: application/json header
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        //headers.put("Content-Type", "multipart/form-data;boundary=" + BOUNDARY + "; charset=utf-8");
        headers.put("Connection", "Keep-Alive");
        headers.put("Accept", "text/plain , application/json");
        headers.put("ComplexId", ""+complexId);
        headers.put("DeviceId", deviceId);
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

}