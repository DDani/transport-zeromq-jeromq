package org.elasticsearch.zeromq;

import java.nio.ByteBuffer;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.RestStatus;

/**
 * @author tlrx
 * 
 */
public class ZMQRestResponse extends RestResponse {

    private final RestStatus status;
    private ByteBuffer body;
    private String contentType;

    public ByteBuffer getBody() {
        return body;
    }

    public ZMQRestResponse(RestStatus status) {
        super();
        this.status = status;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    public ZMQRestResponse setBody(ByteBuffer body) {
        this.body = body;
        return this;
    }

    /*
     * @Override
     * public byte[] content() throws IOException {
     * if (body == null) {
     * return Bytes.EMPTY_ARRAY;
     * }
     * return body.array();
     * }
     * 
     * @Override
     * public int contentLength() throws IOException {
     * if (body == null) {
     * return 0;
     * }
     * return body.remaining();
     * }
     */

    @Override
    public RestStatus status() {
        return status;
    }

    @Override
    public boolean contentThreadSafe() {
        return false;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the payload to reply to the client
     * @throws IOException
     */
    public byte[] payload() {
        // TODO optimise & challenge thoses lines...
        ByteBuffer bStatusCode = ByteBuffer.wrap(Integer.toString(this.status.getStatus()).getBytes());
        ByteBuffer bStatusName = ByteBuffer.wrap(this.status.name().getBytes());
        ByteBuffer bSep1 = ByteBuffer.wrap(ZMQSocket.SEPARATOR.getBytes());
        ByteBuffer bSep2 = ByteBuffer.wrap(ZMQSocket.SEPARATOR.getBytes());
        ByteBuffer bContent = null;
        try {
            bContent = ByteBuffer.wrap(body.array());
        } catch (Exception e) {
            bContent = ByteBuffer.wrap(e.getMessage().getBytes());
        }
        ByteBuffer payload = ByteBuffer.allocate(bStatusCode.limit() + bSep1.limit() + bStatusName.limit() + bSep2.limit() + bContent.limit());
        payload.put(bStatusCode);
        payload.put(bSep1);
        payload.put(bStatusName);
        payload.put(bSep2);
        payload.put(bContent);
        return payload.array();
    }

    @Override
    public BytesReference content() {
        if (body == null) {
            return BytesArray.EMPTY;
        }
        return new BytesArray(body.array());
    }
}
