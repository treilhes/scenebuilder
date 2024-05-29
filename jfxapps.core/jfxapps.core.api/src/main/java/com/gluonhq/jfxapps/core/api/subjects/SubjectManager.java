/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.subjects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.reactivex.rxjava3.subjects.Subject;

public class SubjectManager {
    private static Logger logger = LoggerFactory.getLogger(SubjectManager.class);

    public static Recorder RECORDER = new Recorder();
    public static Player PLAYER = new Player();

    @SuppressWarnings("rawtypes")
    private static Map<String, Map<String, Subject>> subjects = new ConcurrentHashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    public SubjectManager() {
    }

    @SuppressWarnings("rawtypes")
    protected <T extends Subject<?>> T wrap(Class<?> subjectClass, String subjectName, T rsubject) {
        String clsName = subjectClass.getName();
        if (!subjects.containsKey(clsName)) {
            subjects.put(clsName, new ConcurrentHashMap<String, Subject>());
        }
        subjects.get(clsName).put(subjectName, rsubject);

        rsubject.subscribe(o -> {
//			if (RECORDER.isRecording()) {
//				RECORDER.record(new DataEvent(null, clsName, subjectName, o.getClass().getName(), o));
//			}

            if (logger.isInfoEnabled()) {
                try {
                    logger.info(String.format("Event emitted %s", mapper.writeValueAsString(
                            new DataEvent(new Date().getTime(), clsName, subjectName, o.getClass().getName(), o))));
                } catch (Exception e) {
                    logger.info(String.format("Event emitted %s",
                            mapper.writeValueAsString(new DataEvent(new Date().getTime(), clsName, subjectName,
                                    o.getClass().getName(), o.getClass().getName()))));
                }

            }

        });

//		rsubject.doOnSubscribe(d -> {
//			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx");
//			if (logger.isInfoEnabled()) {
//				logger.info(String.format("Subscription registered %s", mapper.writeValueAsString(
//						new DataEvent(new Date().getTime(), clsName, subjectName, d.getClass().getName(), d))));
//			}
//		});

        return rsubject;
    }

    private static class DataEvent {
        private Long timecode;
        private String subjectClass;
        private String subjectName;
        private String subjectEventClass;

        @JsonTypeInfo(use = Id.CLASS, property = "subjectEventClass", include = As.EXTERNAL_PROPERTY)
        private Object subjectEventValue;

        public DataEvent() {
            super();
        }

        public DataEvent(Long timecode, String subjectClass, String subjectName, String subjectEventClass,
                Object subjectEventValue) {
            super();
            this.timecode = timecode;
            this.subjectClass = subjectClass;
            this.subjectName = subjectName;
            this.subjectEventClass = subjectEventClass;
            this.subjectEventValue = subjectEventValue;
        }

        public Long getTimecode() {
            return timecode;
        }

        public void setTimecode(Long timecode) {
            this.timecode = timecode;
        }

        public String getSubjectClass() {
            return subjectClass;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public String getSubjectEventClass() {
            return subjectEventClass;
        }

        public Object getSubjectEventValue() {
            return subjectEventValue;
        }

    }

    public static class Recorder {
        private long startTimecode;
        private OutputStream recordStream;
        private ObjectMapper mapper;

        protected Recorder() {
            this.mapper = new ObjectMapper();
        }

        public boolean isRecording() {
            return recordStream != null;
        }

        public void startRecord(OutputStream recordStream) {
            this.recordStream = recordStream;
            this.startTimecode = new Date().getTime();
        }

        public void stopRecord() {
            if (this.recordStream != null) {
                try {
                    this.recordStream.flush();
                    this.recordStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                this.recordStream = null;
            }
        }

        private void record(DataEvent event) {
            if (isRecording()) {
                try {
                    long timecode = new Date().getTime() - this.startTimecode;
                    event.setTimecode(timecode);
                    byte[] jsonString = mapper.writeValueAsBytes(event);
                    this.recordStream.write(jsonString);
                    this.recordStream.write('\n');
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Player {
        private long startTimecode;
        private long timecode;
        private InputStream recordStream;
        private Thread playingThread;

        public boolean isPlaying() {
            return recordStream != null;
        }

        @SuppressWarnings("unchecked")
        public void startPlay(InputStream recordStream) {
            this.recordStream = recordStream;
            this.startTimecode = new Date().getTime();
            this.timecode = 0;

            playingThread = new Thread(() -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    InputStreamReader isr = new InputStreamReader(recordStream);
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    long start = new Date().getTime();
                    long now = 0;
                    while ((line = br.readLine()) != null) {
                        DataEvent evt = mapper.readValue(line.getBytes(), DataEvent.class);

                        now = evt.getTimecode() - (new Date().getTime() - start);

                        if (now > 0) {
                            synchronized (Thread.currentThread()) {
                                try {
                                    Thread.currentThread().wait(now);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        SubjectManager.subjects.get(evt.getSubjectClass()).get(evt.getSubjectName())
                                .onNext(evt.getSubjectEventValue());
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

            playingThread.start();
        }

        public void waitPlay() {
            if (this.playingThread != null) {
                try {
                    playingThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopPlay() {
            if (this.recordStream != null) {
                try {
                    playingThread = null;
                    this.recordStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.recordStream = null;
            }
        }

        private void processEvent(DataEvent event) {
            long timecode = new Date().getTime() - this.startTimecode;
            event.setTimecode(timecode);
        }
    }
}
