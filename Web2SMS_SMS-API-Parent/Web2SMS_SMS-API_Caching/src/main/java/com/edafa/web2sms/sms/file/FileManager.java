package com.edafa.web2sms.sms.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

public class FileManager {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("ddMMyyyy");

	private Logger logger;

	private String baseDir;
	private String fileName;
	private String extension;
	private String movedExtension;

	private int maxCount;
	private int chunkSize;
	private int totalWriteCount;

	private FileWriterThread fileWriterThread;
	private FileReaderThread fileReaderThread;

	private String name = null;
	private String logId;

	public FileManager(String baseDir, String fileName, String extension, String movedExtension, Logger logger)
			throws IOException {
		if (name == null) {
			name = "FileManager";
			logId = name + " | ";
		}

		//null check
		//Objects.requireNonNull(baseDir, "Base Dir must not be null");
		Objects.requireNonNull(fileName, "File Name must not be null");
		Objects.requireNonNull(extension, "Extension must not be null");
		Objects.requireNonNull(movedExtension, "Moved Extension must not be null");
		Objects.requireNonNull(logger, "Logger must not be null");

		this.baseDir = baseDir;
		this.fileName = fileName;
		this.extension = extension;
		this.movedExtension = movedExtension;
		this.logger = logger;

		if (this.baseDir == null || this.baseDir.equals("")) {
			this.baseDir = System.getProperty("user.dir");
		}

		//initialize threads
		fileWriterThread = new FileWriterThread(this);
		fileReaderThread = new FileReaderThread(this);

		//start threads
		logger.info(logId + "threads start ");
		fileWriterThread.start();
		fileReaderThread.start();
	}

	public void stop() {
		logger.info(logId + "signaling stop signal to managed threads ");
		fileWriterThread.setStop(true);
		fileReaderThread.setStop(true);
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getMovedExtension() {
		return movedExtension;
	}

	public void setMovedExtension(String movedExtension) {
		this.movedExtension = movedExtension;
	}

	public static SimpleDateFormat getSdf() {
		return SDF;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public int getTotalWriteCount() {
		return totalWriteCount;
	}

	public boolean isMaxWriteReatched() {
		return (totalWriteCount == maxCount);
	}

	public boolean addToQueue(Object value) throws WritingCountMaxReachedException {
		if ((totalWriteCount + 1) > maxCount) {
			throw new WritingCountMaxReachedException("maxCount=" + maxCount + ", totalWriteCount=" + totalWriteCount);
		}

		totalWriteCount++;
		return fileWriterThread.queue.add(value);
	}

	public boolean addAllToQueue(Collection<? extends Object> value) throws WritingCountMaxReachedException {
		boolean returnValue = false;

		for (Iterator iterator = value.iterator(); iterator.hasNext();) {
			Object object = iterator.next();

			returnValue = addToQueue(object);
		}

		return returnValue;
	}

	public void setDataReadChunkSize(int chunckSize) {
		fileReaderThread.setReadChunkSize(chunckSize);
	}

	public Object[] getReadData() {
		return fileReaderThread.getData();
	}

	public ThreadState getFileReaderThreadState() {
		return ThreadState.getThreadState(fileReaderThread.isRunning(), fileReaderThread.isStop());
	}

	public ThreadState getFileWriterThreadState() {
		return ThreadState.getThreadState(fileWriterThread.isRunning(), fileWriterThread.isStop());
	}

	public boolean isRunning() {
		return (fileReaderThread.isRunning() || fileWriterThread.isRunning());
	}

	class FileWriterThread extends Thread {

		private static final String THREAD_NAME = "writerThread";

		private String name = null;
		private String logThreadId;

		private FileManager manager;

		private Calendar fileStartDate;

		private BufferedOutputStream bos;
		private ObjectOutputStream oos;

		private BlockingQueue<Object> queue;

		private boolean stop = false;
		private boolean running = false;

		private int currentFileCount;
		private int writeCountPerFile;

		public FileWriterThread(FileManager fm) {
			if (name == null) {
				name = "FileWriterThread";
				logThreadId = name + " | ";
			}
			this.manager = fm;

			queue = new LinkedBlockingQueue<>();

			fileStartDate = Calendar.getInstance();
			fileStartDate.clear(Calendar.HOUR);
			fileStartDate.clear(Calendar.MINUTE);
			fileStartDate.clear(Calendar.SECOND);
			fileStartDate.clear(Calendar.MILLISECOND);

			currentFileCount = findFileCount();
			writeCountPerFile = 0;

			setName(THREAD_NAME);
		}

		private int findFileCount() {
			final String dateFormat = FileManager.SDF.format(fileStartDate.getTime());
			File dir = new File(manager.baseDir + File.separator);
			File[] files = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(manager.fileName) && name.contains(dateFormat);
				}
			});

			return (files != null) ? files.length : 0;
		}

		@Override
		public void run() {
			setRunning(true);
			logger.info(logThreadId + manager.fileName + " : started");

			while (!isStop()) {
				try {
					openStream();

					while (!isStop() || !queue.isEmpty()) {
						Object obj = queue.poll(100l, TimeUnit.MILLISECONDS);

						if (obj != null) {
							checkTime();

							if ((writeCountPerFile + 1) > manager.chunkSize) {
								rollover();
								writeCountPerFile = 0;
							}

							oos.writeObject(obj);
							writeCountPerFile++;
						}
					}

				} catch (InterruptedException e) {
					logger.error(logThreadId + manager.fileName + " : InterruptedException ", e);
				} catch (FileNotFoundException | SecurityException e) {
					logger.error(logThreadId + manager.fileName + " : OpenFileException", e);
					--currentFileCount;
				} catch (IOException e) {
					logger.error(logThreadId + manager.fileName + " : IOException", e);
				} catch (Exception e) {
					logger.error(logThreadId + manager.fileName + " : Exception", e);
				} finally {
					try {
						closeStream();
					} catch (IOException e) {
						logger.error(logThreadId + manager.fileName + " : IOException", e);
					}
				}

				try {
					Thread.sleep(300l);
				} catch (InterruptedException e) {
				}
			}

			setRunning(false);
			logger.info(logThreadId + manager.fileName + " : stopped");
		}

		private void checkTime() throws IOException {
			Calendar currentDate = Calendar.getInstance();
			currentDate.clear(Calendar.HOUR);
			currentDate.clear(Calendar.MINUTE);
			currentDate.clear(Calendar.SECOND);
			currentDate.clear(Calendar.MILLISECOND);

			int dateCompare = currentDate.compareTo(fileStartDate);

			if (dateCompare == 0) {
				logger.debug(logThreadId + manager.fileName + " : dateCompare=0");
			} else if (dateCompare > 0) {
				logger.info(logThreadId + manager.fileName + " : reset + rollover");
				reset(currentDate);
				rollover();
			} else {
				logger.warn(logThreadId + manager.fileName + " : dateCompare=" + dateCompare);
			}
		}

		private void reset(Calendar date) {
			this.fileStartDate = date;

			currentFileCount = 0;
			writeCountPerFile = 0;
			manager.totalWriteCount = 0;
		}

		private void openStream() throws IOException {
			String newFileName = new StringBuilder(50).append(manager.baseDir).append(File.separator)
					.append(manager.fileName).append("-").append(FileManager.SDF.format(fileStartDate.getTime()))
					.append("-").append(String.format("%03d", ++currentFileCount)).append(".").append(manager.extension)
					.toString();

			logger.info(logThreadId + manager.fileName + " : opening file=" + newFileName);
			FileOutputStream fos = new FileOutputStream(newFileName, true);
			bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);
			logger.info(logThreadId + manager.fileName + " : file=" + newFileName + " opened successfully");
		}

		private void closeStream() throws IOException {
			String fileName = new StringBuilder(50).append(manager.baseDir).append(File.separator)
					.append(manager.fileName).append("-").append(FileManager.SDF.format(fileStartDate.getTime()))
					.append("-").append(String.format("%03d", currentFileCount)).append(".").append(manager.extension)
					.toString();

			if (oos != null)
				oos.close();

			if (bos != null)
				bos.close();

			if (writeCountPerFile == 0) {
				logger.info(logThreadId + manager.fileName + " : removeing file=" + fileName);
				File toBeRemoveFile = new File(fileName);
				toBeRemoveFile.delete();
				logger.info(logThreadId + manager.fileName + " : file=" + fileName + " removed successfully");
			} else {
				logger.info(logThreadId + manager.fileName + " : closing file=" + fileName);
			}
		}

		private void rollover() throws IOException {
			logger.info(logThreadId + manager.fileName + " : rollover, currentFileCount=" + currentFileCount);
			closeStream();
			openStream();
		}

		public boolean isStop() {
			return stop;
		}

		public synchronized void setStop(boolean stop) {
			this.stop = stop;
		}

		public boolean isRunning() {
			return running;
		}

		public synchronized void setRunning(boolean running) {
			this.running = running;
		}

		public Calendar getFileStartDate() {
			return fileStartDate;
		}

		public BlockingQueue<Object> getQueue() {
			return queue;
		}

		public int getCurrentFileCount() {
			return currentFileCount;
		}

		public int getWriteCountPerFile() {
			return writeCountPerFile;
		}

	}

	class FileReaderThread extends Thread {

		private static final String FILE_PARTIALLY_READ_PREFIX = ".dpart.";

		private static final String FILE_READ_PREFIX = ".part";

		private static final String THREAD_NAME = "readerThread";

		private String name = null;
		private String logThreadId;

		private FileManager manager;

		private boolean stop = false;
		private boolean running = false;
		private boolean dataAvailable = false;

		private int readChunkSize;
		private List<Object> dataList;

		public FileReaderThread(FileManager fm) {
			if (name == null) {
				name = "FileReaderThread";
				logThreadId = name + " | ";
			}
			this.readChunkSize = fm.chunkSize;
			this.manager = fm;

			dataList = Collections.synchronizedList(new ArrayList<>(readChunkSize));

			setName(THREAD_NAME);
		}

		public FileReaderThread(int readChunkSize, FileManager fm) {
			if (name == null) {
				name = "FileReaderThread";
				logThreadId = name + " | ";
			}
			this.readChunkSize = readChunkSize;
			this.manager = fm;

			dataList = Collections.synchronizedList(new ArrayList<>(readChunkSize));

			setName(THREAD_NAME);
		}

		@Override
		public void run() {
			setRunning(true);
			logger.info(logThreadId + manager.fileName + " : started");

			while (!isStop()) {
				//file declaration
				File file = getFirstAvailableFile();
				String originalFileName = (file != null) ? file.getName() : null;

				//stream declaration
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				ObjectInputStream ois = null;
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				ObjectOutputStream oos = null;
				try {
					do {
						if (file != null) {
							//rename file to partly processed
							String suffix = "";
							String newFileName = originalFileName + FILE_READ_PREFIX;

							logger.info(logThreadId + manager.fileName + " : rename file=" + originalFileName + ", to="
									+ newFileName);
							File newFile = new File(file.getParentFile(), newFileName);
							file.renameTo(newFile);
							file = newFile;

							logger.info(logThreadId + manager.fileName + " : opening file=" + file);
							fis = new FileInputStream(file);
							bis = new BufferedInputStream(fis);
							ois = new ObjectInputStream(bis);
							logger.info(logThreadId + manager.fileName + " : file=" + file + " opened successfully");

							int readCount = 0;

							mainLoop:
							while (bis.available() != 0) {
								dataList.add(ois.readObject());
								readCount++;

								if (readCount == readChunkSize) {
									setDataAvailable(true);

									//wait till data read done
									while (dataList.size() == readChunkSize) {
										Thread.sleep(300l);

										//if stop called for this thread rewrite data
										if (isStop()) {
											setDataAvailable(false);

											logger.info(logThreadId + manager.fileName
													+ " : rewrite avilable data to file=" + originalFileName);
											logger.info(logThreadId + manager.fileName + " : opening file="
													+ originalFileName);
											fos = new FileOutputStream(originalFileName, false);
											bos = new BufferedOutputStream(fos);
											oos = new ObjectOutputStream(bos);
											logger.info(logThreadId + manager.fileName + " : file=" + originalFileName
													+ " opened successfully");

											for (Object object : dataList) {
												oos.writeObject(object);
											}

											dataList.clear();

											while (bis.available() != 0) {
												oos.writeObject(ois.readObject());
											}

											readCount = 0;

											logger.info(logThreadId + manager.fileName + " : closing file="
													+ originalFileName);
											oos.close();
											bos.close();
											fos.close();
											logger.info(logThreadId + manager.fileName + " : file=" + originalFileName
													+ " closed successfully");

											int firstDash = originalFileName.indexOf("-");
											int secondDash = originalFileName.indexOf("-", firstDash);
											String datePart = originalFileName.substring(firstDash, secondDash);

											suffix = FILE_PARTIALLY_READ_PREFIX + (getPartFilesCount(datePart) + 1);

											readCount = 0;
											break mainLoop;
										}
									}

									readCount = 0;
								}
							}

							//wait till this file data is processed by processing threads
							if (readCount > 0) {
								setDataAvailable(true);

								while (dataList.size() > 0) {
									Thread.sleep(300l);

									//if stop called for this thread rewrite data
									if (isStop()) {
										setDataAvailable(false);
										logger.info(logThreadId + manager.fileName + " : rewrite avilable data to file="
												+ originalFileName);
										logger.info(
												logThreadId + manager.fileName + " : opening file=" + originalFileName);
										fos = new FileOutputStream(originalFileName, false);
										bos = new BufferedOutputStream(fos);
										oos = new ObjectOutputStream(bos);
										logger.info(logThreadId + manager.fileName + " : file=" + originalFileName
												+ " opened successfully");

										for (Object object : dataList) {
											oos.writeObject(object);
										}

										dataList.clear();

										logger.info(
												logThreadId + manager.fileName + " : closing file=" + originalFileName);
										oos.close();
										bos.close();
										fos.close();
										logger.info(logThreadId + manager.fileName + " : file=" + originalFileName
												+ " closed successfully");

										int firstDash = originalFileName.indexOf("-");
										int secondDash = originalFileName.indexOf("-", firstDash);
										String datePart = originalFileName.substring(firstDash, secondDash);

										suffix = FILE_PARTIALLY_READ_PREFIX + (getPartFilesCount(datePart) + 1);

										break;
									}
								}

								readCount = 0;
							}

							//close file input stream
							logger.info(logThreadId + manager.fileName + " : closing file=" + file);
							ois.close();
							bis.close();
							fis.close();
							logger.info(logThreadId + manager.fileName + " : file=" + file + " closed successfully");

							//rename file
							newFileName = originalFileName + "." + movedExtension + suffix;
							logger.info(logThreadId + manager.fileName + " : rename file" + file.getName() + " to="
									+ newFileName);
							newFile = new File(file.getParentFile(), newFileName);
							file.renameTo(newFile);

							//clearing
							file = null;
							ois = null;
							bis = null;
							fis = null;
							oos = null;
							bos = null;
							fos = null;
						} else {
							logger.trace(logThreadId + manager.fileName + " : No file to open sleep for 500 Millis");
							Thread.sleep(500l);
						}

						//start next file
						file = getFirstAvailableFile();
                                                originalFileName = (file != null) ? file.getName() : null;
					} while (!isStop());

				} catch (EOFException e) {
					//rename zero length file to stop processing it into done
					String newFileName = originalFileName + "." + movedExtension;

					file.renameTo(new File(file.getParentFile(), newFileName));

					logger.info(logThreadId + manager.fileName + " : rename file" + originalFileName + " to="
							+ newFileName);
				} catch (IOException e) {
					logger.error(logThreadId + manager.fileName + " : IOException", e);
				} catch (ClassNotFoundException e) {
					logger.error(logThreadId + manager.fileName + " : ClassNotFoundException", e);
				} catch (InterruptedException e) {
					logger.error(logThreadId + manager.fileName + " : InterruptedException", e);
				} catch (Exception e) {
					logger.error(logThreadId + manager.fileName + " : Exception", e);
				} finally {
					// more detailed handling
					try {
						//close file input stream
						logger.info(logThreadId + manager.fileName + " : closing file=" + file);
						if (ois != null)
							ois.close();
						if (bis != null)
							bis.close();
						if (fis != null)
							fis.close();
						if (oos != null)
							oos.close();
						if (bos != null)
							bos.close();
						if (fos != null)
							fos.close();
						logger.info(logThreadId + manager.fileName + " : file=" + file + " closed successfully");
					} catch (IOException e) {
						logger.error(logThreadId + manager.fileName + " : IOException", e);
					}

				}

				try {
					Thread.sleep(300l);
				} catch (InterruptedException e) {
				}
			}

			setRunning(false);
			logger.info(logThreadId + manager.fileName + " : stopped");
		}

		private int getPartFilesCount(final String date) {
			File baseDir = new File(manager.baseDir + File.separator);

			File[] files = baseDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.contains(date) && name.contains(FILE_PARTIALLY_READ_PREFIX);
				}
			});

			return files.length;
		}

		private File[] getFilesList() {
			File baseDir = new File(manager.baseDir + File.separator);

			File[] files = baseDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith(manager.fileName) && name.endsWith(manager.extension);
				}
			});

                        if(logger.isTraceEnabled()) {
                                logger.trace(logThreadId + manager.fileName + " : manager.baseDir=" + manager.baseDir + File.separator
					+ ", files=" + Arrays.toString(files));
                        }

			if (files != null) {
				Arrays.sort(files, new Comparator<File>() {

					@Override
					public int compare(File o1, File o2) {
						return o1.getPath().compareTo(o2.getPath());
					}
				});

				return files;
			}

			return new File[1];
		}

		private File getFirstAvailableFile() {
			File[] files = getFilesList();

			return (files.length > 1) ? files[0] : null;
		}

		public boolean isStop() {
			return stop;
		}

		public synchronized void setStop(boolean stop) {
			this.stop = stop;
		}

		public boolean isRunning() {
			return running;
		}

		public synchronized void setRunning(boolean running) {
			this.running = running;
		}

		public synchronized boolean isDataAvailable() {
			return dataAvailable;
		}

		public synchronized void setDataAvailable(boolean dataAvailable) {
			this.dataAvailable = dataAvailable;
		}

		public int getReadChunkSize() {
			return readChunkSize;
		}

		public void setReadChunkSize(int readChunkSize) {
			this.readChunkSize = readChunkSize;
		}

		public Object[] getData() {
			if (isDataAvailable()) {
				setDataAvailable(false);
				Object[] data = dataList.toArray();
				dataList.clear();

				return data;
			} else {
				return null;
			}
		}
	}

	public enum ThreadState {
		RUNNING,
		STOPPING,
		STOPPED;

		public static ThreadState getThreadState(boolean running, boolean stop) {
			if (running && !stop) {
				return ThreadState.RUNNING;
			} else if (running && stop) {
				return ThreadState.STOPPING;
			} else {
				return ThreadState.STOPPED;
			}
		}
	}
}
