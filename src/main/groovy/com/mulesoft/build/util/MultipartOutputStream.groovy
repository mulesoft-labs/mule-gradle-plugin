/*
 * Copyright 2014 juancavallotti.
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

package com.mulesoft.build.util

/**
 * Inspired on org.mortbay.util.MultiPartOutputStream
 *
 * Created by juancavallotti on 04/06/14.
 */
class MultipartOutputStream extends FilterOutputStream {

    public static final String ISO88591 = 'ISO-8859-1'

    private static byte[] __CRLF = "\r\n".getBytes()
    private static byte[] __DASHDASH = "--".getBytes()

    private String boundary
    private byte[] boundaryBytes

    private boolean inPart=false

    /**
     * Create a new builder for this MultiPart output stream.
     * @return
     */
    static Builder builder() {
        return new Builder()
    }

    public MultipartOutputStream(String boundary, OutputStream out) {
        super(out)
        this.boundary = boundary
        boundaryBytes=boundary.getBytes(ISO88591)
        inPart=false
    }

    /**
     * End the current part.
     * @exception IOException IOException
     */
    public void close() throws IOException {
        if (inPart) {
            out.write(__CRLF)
        }

        out.write(__DASHDASH)
        out.write(boundaryBytes)
        out.write(__DASHDASH)
        out.write(__CRLF)
        inPart=false
        super.close()
    }

    public String getBoundary() {
        return boundary
    }

    /**
     * Start creation of the next Content.
     */
    public void startPart(String contentType) {
        if (inPart) {
            out.write(__CRLF)
        }

        inPart=true
        out.write(__DASHDASH)
        out.write(boundaryBytes)
        out.write(__CRLF)
        out.write(("Content-Type: "+contentType).getBytes(ISO88591))
        out.write(__CRLF)
        out.write(__CRLF)
    }

    /**
     * Start creation of the next Content.
     */
    public void startPart(String contentType, String[] headers) {
        if (inPart) {
            out.write(__CRLF)
        }

        inPart=true
        out.write(__DASHDASH)
        out.write(boundaryBytes)
        out.write(__CRLF)

        if (contentType) {
            out.write(("Content-Type: "+contentType).getBytes(ISO88591))
            out.write(__CRLF)
        }
        for (int i=0;headers!=null && i<headers.length;i++) {
            out.write(headers[i].getBytes(ISO88591))
            out.write(__CRLF)
        }
        out.write(__CRLF)
    }

    public void startPart(String[] headers) {
        this.startPart(null, headers)
    }

    /**
     * Utility class for helping with the creation of the stream.
     */
    static class Builder {
        String boundary
        Builder() {
            this.boundary = "gradle"+System.identityHashCode(this)+Long.toString(System.currentTimeMillis(),36)
        }

        MultipartOutputStream build(OutputStream os) {
            return new MultipartOutputStream(boundary, os)
        }
    }

}
