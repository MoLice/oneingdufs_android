/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.molice.oneingdufs.androidpn;

import org.jivesoftware.smack.packet.IQ;

import android.util.Log;

/** 
 * This class represents a notification IQ packet.
 * 由{@link NotificationIQProvider#parseIQ(org.xmlpull.v1.XmlPullParser) parseIQ(XmlPullParser)}将接收到的packet转化为{@link NotificationIQ}对象
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationIQ extends IQ {

    private String id;

    private String apiKey;

    private String title;

    private String message;

    private String uri;

    public NotificationIQ() {
    }

    /**
     * 该方法用于产生一个空白的XML标签，调用者可将内容插入到该空白标签中。因此协议规定的消息体结构在这里定义，且必须和服务端的一致。
     * @see org.jivesoftware.smack.packet.IQ#getChildElementXML()
     */
    @Override
    public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append("notification").append(" xmlns=\"").append(
                "androidpn:iq:notification").append("\">");
        if (id != null) {
            buf.append("<id>").append(id).append("</id>");
        }
        buf.append("</").append("notification").append("> ");
        Log.d("【MoLice】", "NotificationIQ.getChildElementXML, 空白XML标签：" + buf.toString());
        return buf.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String url) {
        this.uri = url;
    }

}
