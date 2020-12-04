package com.oracle.javafx.scenebuilder.api.util;

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

import io.reactivex.subjects.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


	@NoArgsConstructor
	@AllArgsConstructor
	private static class DataEvent {
		private @Getter @Setter Long timecode;
		private @Getter String subjectClass;
		private @Getter String subjectName;
		private @Getter String subjectEventClass;

		@JsonTypeInfo(use = Id.CLASS, property = "subjectEventClass", include = As.EXTERNAL_PROPERTY)
		private @Getter Object subjectEventValue;
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
