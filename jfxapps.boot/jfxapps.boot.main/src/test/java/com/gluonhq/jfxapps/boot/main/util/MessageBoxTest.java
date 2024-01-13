/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.jfxapps.boot.main.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageBoxTest {

    @TempDir
    File boxFolder;

    @Spy
    MessageBox.Delegate<MessageBoxMessage> delegate = new MessageBox.Delegate<MessageBoxMessage>() {

        @Override
        public void messageBoxDidGetMessage(MessageBoxMessage message) {
            System.out.println(message + "xx");
        }

        @Override
        public void messageBoxDidCatchException(Exception x) {
            System.out.println(x + "xx");
        }
    };

    @Mock
    MessageBox.Delegate<MessageBoxMessage> secondDelegate;

    @Test
    void must_lock_file() throws IOException {
        MessageBox<MessageBoxMessage> messageBox = new MessageBox<>(boxFolder, MessageBoxMessage.class, 1000 /* ms */);
        assertTrue(messageBox.grab(delegate));
        messageBox.release();
    }

    @Test
    void second_box_must_send_message() throws IOException, InterruptedException {
        MessageBox<MessageBoxMessage> messageBox = new MessageBox<>(boxFolder, MessageBoxMessage.class, 1000 /* ms */);
        assertTrue(messageBox.grab(delegate));

        try {
            MessageBox<MessageBoxMessage> secondMessageBox = new MessageBox<>(boxFolder, MessageBoxMessage.class, 1000 /* ms */);

            MessageBoxMessage mbm = new MessageBoxMessage(UUID.randomUUID(), List.of("message"));
            new Thread(() -> {
                try {
                    secondMessageBox.sendMessage(mbm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


            verify(delegate, timeout(1500)).messageBoxDidGetMessage(mbm);
        } finally {
            messageBox.release();
        }



    }
}
