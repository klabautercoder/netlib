/*
    MIT License

    Copyright (c) 2018

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */

package de.klabauter.netlib;

import lombok.Getter;
import lombok.Setter;

public class NetLibException extends Exception {

    @Setter
    @Getter
    private String url;

    @Setter
    @Getter
    private int errorCode = -1;

    public NetLibException() {
    }

    public NetLibException(String exp) {
        super(exp);
    }

    @Override
    public String toString() {
        StringBuilder error = new StringBuilder();

        error.append("Error Calling: ").append(url).append(". <br/>\n");

        if (errorCode > -1) {
            error.append("Response Code: ").append(errorCode).append(" <br/>\n");
        }
        error.append("Error Message:").append(this.getMessage());

        return error.toString();
    }
}
