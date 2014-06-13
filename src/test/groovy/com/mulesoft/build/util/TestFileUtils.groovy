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

import org.junit.Test

import static org.junit.Assert.*;


/**
 * Created by juancavallotti on 13/06/14.
 */
class TestFileUtils {

    @Test
    void testCopyStream() {

        Random random = new Random()

        //prepare a byte array bigger than the buffer
        byte[] input = new byte[6000]

        //generate test data
        for (int i = 0; i < input.length; i++) {
            input[i] = (byte) random.nextInt()
        }

        //create the input and output stream
        ByteArrayInputStream bis = new ByteArrayInputStream(input)
        ByteArrayOutputStream bos = new ByteArrayOutputStream()

        long count = FileUtils.copyStream(bis, bos)

        assertEquals('Should have copied the input size', input.length, count)

        //assert each byte of the input is the same as the output
        byte[] output = bos.toByteArray()

        for(int i = 0; i < input.length; i++) {
            assertEquals('bytes do not match', input[i], output[i])
        }
    }

}
