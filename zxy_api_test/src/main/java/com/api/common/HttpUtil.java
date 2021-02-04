package com.api.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.api.utils.FileUtil;
import com.api.utils.PropertiesUtil;

public class HttpUtil {
	private static Logger log = Logger.getLogger(HttpUtil.class);
	private HttpURLConnection connection;
	private BufferedReader br;
	private Map<String, String> resultMap = new HashMap<String, String>();
	private Map<String, String> map = PropertiesUtil.getAll("headers.properties");
	private StringBuffer sbf = new StringBuffer();
//	private final static String BOUNDARY = UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");// 边界标识
	private final static String PREFIX = "--";
	private static String BOUNDARY = "---------------------------244122806316696";
	private final static String LINE_END = "\r\n";

	public Map<String, String> request(String httpUrl, String body, String uploadFilePath, String method,
			String useHeaders) {
		if (!uploadFilePath.contains("{{") && !uploadFilePath.contains("}}") && !uploadFilePath.equals("空")) {
			downLoadFileRequest(httpUrl, body, uploadFilePath, method, useHeaders);
		} else if (!uploadFilePath.equals("空")) {
			uploadFileRequest(httpUrl, body, uploadFilePath, method, useHeaders);
		} else {
			normalRequest(httpUrl, body, method, useHeaders);
		}
		return resultMap;
	}

	private void uploadFileRequest(String httpUrl, String body, String uploadFilePath, String method,
			String useHeaders) {
		if (uploadFilePath.contains("f_upload") || uploadFilePath.contains("uploadClassPhoto")) {
			BOUNDARY = "----moxieboundary1574606481977";
		}
		connection = getConnection(httpUrl, body, method, useHeaders);
		String uploadMethod = uploadFilePath.substring(0, uploadFilePath.indexOf(":"));
		String filePath = uploadFilePath.substring(uploadFilePath.indexOf(":") + 1).replace("{{", "").replace("}}", "");
//		connection = CookiesManager.useCookies(connection, cookieKeys);
		if (!filePath.equals("空")) {
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
			long fileSize = FileUtil.getFileSize(filePath);
			try {
				connection.connect();
				OutputStream os = new DataOutputStream(connection.getOutputStream());
				os.write((PREFIX + BOUNDARY + LINE_END).getBytes());
				if (uploadMethod.equalsIgnoreCase("upload")) {
					// 写上传课程课件开头标识
					os.write(
							("Content-Disposition: form-data; name=\"file\"; filename=\"blob\"" + LINE_END).getBytes());
					os.write(("Content-Type: application/octet-stream" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
				} else if (uploadMethod.equalsIgnoreCase("f_upload")) {
					// 上传附件开头标识
					os.write(("Content-Disposition: form-data; name=\"name\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write((fileName + LINE_END).getBytes("UTF-8"));
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					os.write(("Content-Disposition: form-data; name=\"chunk\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("0" + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					os.write(("Content-Disposition: form-data; name=\"chunks\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("1" + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					os.write(("Content-Disposition: form-data; name=\"access_token\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					String temp_access_token = map.get("Authorization");
					String access_token = temp_access_token.substring(temp_access_token.indexOf("__") + 2);
					os.write((access_token + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINE_END)
							.getBytes("UTF-8"));

					os.write(("Content-Type: application/octet-stream" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
				} else if (uploadMethod.equalsIgnoreCase("uploadClassPhoto")) {
					// 上传班级照片开头标识
					os.write(("Content-Disposition: form-data; name=\"name\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write((fileName + LINE_END).getBytes("UTF-8"));
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());
					os.write(("Content-Disposition: form-data; name=\"chunk\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("0" + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());
					os.write(("Content-Disposition: form-data; name=\"chunks\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("1" + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());
					os.write(("Content-Disposition: form-data; name=\"cutSize\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("200,200;200,300" + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());
					os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINE_END)
							.getBytes("UTF-8"));
					os.write(("Content-Type: image/jpeg" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
				} else if (uploadMethod.equalsIgnoreCase("import")) {
					// 导入文件开头标识
					os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINE_END)
							.getBytes("UTF-8"));
					os.write(("Content-Type: application/octet-stream" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
				} else if (uploadMethod.equalsIgnoreCase("stufile_upload")) {
					// 写上传外派培训学员个人附件/banner封面
					os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINE_END)
							.getBytes());
					os.write(("Content-Type: image/jpeg" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
				} else if (uploadMethod.equalsIgnoreCase("plain_upload")) {
					// 上传课程/专题/培训班/商品等的封面image文件
					os.write(("Content-Disposition: form-data; name=\"picSize\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("" + fileSize + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());
					os.write(("Content-Disposition: form-data; name=\"access_token\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					String temp_access_token = map.get("Authorization");
					String access_token = temp_access_token.substring(temp_access_token.indexOf("__") + 2);
					os.write((access_token + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());
					os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINE_END)
							.getBytes("UTF-8"));
					os.write(("Content-Type: image/jpeg" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
				} else {
					log.error("上传文件的格式不正确！");
				}
				// 传输文件
				File file = FileUtil.getUploadFile(filePath);
				if (file.isFile()) {
					DataInputStream dis = new DataInputStream(new FileInputStream(file));
					byte[] arr = new byte[1024];
					int len = 0;
					while ((len = dis.read(arr)) != -1) {
						os.write(arr, 0, len);
					}
					dis.close();
				} else {
					log.error("【" + filePath + "】不是一个文件！");
				}
				if (uploadMethod.equalsIgnoreCase("upload")) {
					os.write(LINE_END.getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					// 写入fileKey
					os.write(("Content-Disposition: form-data; name=\"fileKey\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					String fileKey = map.get("fileKey");
					os.write((fileKey + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					// 写入fileOrder
					os.write(("Content-Disposition: form-data; name=\"fileOrder\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("1" + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					// 写入flag
					os.write(("Content-Disposition: form-data; name=\"flag\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("2" + LINE_END).getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					// 写入fileName
					os.write(("Content-Disposition: form-data; name=\"fileName\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write((fileName + LINE_END).getBytes("UTF-8"));
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					// 写入contentType
					os.write(("Content-Disposition: form-data; name=\"contentType\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					if (filePath.endsWith(".png")) {
						os.write("image/png".getBytes());
					} else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
						os.write("image/jpeg".getBytes());
					} else if (filePath.endsWith(".mpeg")) {
						os.write("video/mpeg".getBytes());
					} else if (filePath.endsWith(".mp3")) {
						os.write("audio/mpeg".getBytes());
					} else if (filePath.endsWith(".mp4")) {
						os.write("audio/mp4".getBytes());
					} else if (filePath.endsWith(".mpg") || filePath.endsWith(".mpeg") || filePath.endsWith(".mpe")) {
						os.write("video/mpeg".getBytes());
					} else if (filePath.endsWith(".avi")) {
						os.write("video/avi".getBytes());
					} else if (filePath.endsWith(".3gp")) {
						os.write("video/3gpp".getBytes());
					} else if (filePath.endsWith(".rm") || filePath.endsWith(".rmvb")) {
						os.write("application/octet-stream".getBytes());
					} else if (filePath.endsWith(".wmv")) {
						os.write("video/x-ms-wmv".getBytes());
					} else if (filePath.endsWith(".mov")) {
						os.write("video/quicktime".getBytes());
					} else if (filePath.endsWith(".flv")) {
						os.write("video/x-flv".getBytes());
					} else if (filePath.endsWith(".mkv")) {
						os.write("video/x-matroska".getBytes());
					} else if (filePath.endsWith(".pdf")) {
						os.write("application/pdf".getBytes());
					} else if (filePath.endsWith(".ppt")) {
						os.write("application/vnd.ms-powerpoint".getBytes());
					} else if (filePath.endsWith(".pptx")) {
						os.write(
								"application/vnd.openxmlformats-officedocument.presentationml.presentation".getBytes());
					} else if (filePath.endsWith(".xls")) {
						os.write("application/vnd.ms-excel".getBytes());
					} else if (filePath.endsWith(".xlsx")) {
						os.write("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".getBytes());
					} else if (filePath.endsWith(".doc")) {
						os.write("application/msword".getBytes());
					} else if (filePath.endsWith(".docx")) {
						os.write("application/vnd.openxmlformats-officedocument.wordprocessingml.document".getBytes());
					} else if (filePath.endsWith(".epub")) {
						os.write("application/epub+zip".getBytes());
					} else if (filePath.endsWith(".zip")) {
						os.write("application/x-zip-compressed".getBytes());
					} else if (filePath.endsWith(".txt")) {
						os.write("text/plain".getBytes());
					} else {
						log.error("该文件[" + filePath + "]无效的扩展名！");
					}
					os.write(LINE_END.getBytes());
					os.write((PREFIX + BOUNDARY + LINE_END).getBytes());

					// 写入fileSize
					os.write(("Content-Disposition: form-data; name=\"fileSize\"" + LINE_END).getBytes());
					os.write(LINE_END.getBytes());
					os.write(("" + fileSize).getBytes());
				}
				os.write(LINE_END.getBytes());
				os.write((PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes());

				os.flush();
				os.close();
				handleResponse();
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				if (connection != null) {
					connection.disconnect();
					connection = null;
				}
			}
		}
	}

	// 后期调整下载文件
	private void downLoadFileRequest(String httpUrl, String body, String filePath, String method, String useHeaders) {
		connection = getConnection(httpUrl, body, method, useHeaders);
		connection.setRequestProperty("Content-Type", "multipart/form-data");
//		connection = CookiesManager.useCookies(connection, cookieKeys);
		try {
			connection.connect();
			if (!filePath.equals("空")) {
				filePath = filePath.replace("{{", "").replace("}}", "");
				FileUtil.checkParentFile(filePath);
				File file = FileUtil.getUploadFile(filePath);
				int fileLength = connection.getContentLength();
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				OutputStream os = new FileOutputStream(file);
				int loadSize = 0;
				byte[] arr = new byte[1024];
				int len = 0;
				while ((len = bis.read(arr)) != -1) {
					loadSize += len;
					os.write(arr, 0, len);
					log.info("下载已完成-----> " + loadSize * 100 / fileLength + "%\n");
				}
				bis.close();
				os.close();
				resultMap.put("responseCode", "200");
				resultMap.put("response", "文件下载已全部完成！");
				log.info("文件已经下载完成，请检查" + filePath + "是否下载正确！");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
	}

	private void normalRequest(String httpUrl, String body, String method, String useHeaders) {
		try {
			connection = getConnection(httpUrl, body, method, useHeaders);
			connection.connect();
			if (!body.equals("空") && !method.equals("GET")) {
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				dos.write(body.getBytes());
				dos.flush();
				dos.close();
			}
			handleResponse();
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
	}

	private void handleResponse() {
		try {
			int responseCode = connection.getResponseCode();
			resultMap.put("responseCode", ((Integer) responseCode).toString());
			if (responseCode >= 200 && responseCode < 300) {
				InputStream is = connection.getInputStream();
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String strRead;
				while ((strRead = br.readLine()) != null) {
					sbf.append(strRead);
				}
				// 保存Cookies
				HeadersManager.saveCookiesToFile(connection);
				br.close();
				resultMap.put("response", sbf.toString());
			} else {
				InputStream es = connection.getErrorStream();
				br = new BufferedReader(new InputStreamReader(es, "UTF-8"));
				String strRead;
				while ((strRead = br.readLine()) != null) {
					sbf.append(strRead);
				}
				br.close();
				log.error("请求出现异常，状态码为：" + responseCode);
				resultMap.put("response", sbf.toString());
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private HttpURLConnection getConnection(String httpUrl, String body, String method, String useHeaders) {
		try {
			URL url = new URL(httpUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(60 * 1000);
			connection.setReadTimeout(60 * 1000);
			connection.setInstanceFollowRedirects(true);
			connection.setDefaultUseCaches(false);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Accept", "*/*");
			connection.setDoInput(true);
			connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

			if (useHeaders != "空") {
				String[] headArr = useHeaders.split("\n");
				List<String> headerKeys = new ArrayList<String>();
				for (int i = 0; i < headArr.length; i++) {
					if (!headArr[i].contains(":")) {
						headerKeys.add(headArr[i].replace(";", ""));
					} else {
						if (headArr[i].toLowerCase().startsWith("cookie")) {
							headerKeys.add(headArr[i]);
						} else if (!headArr[i].contains("{{")) {
							headerKeys.add(headArr[i].substring(0, headArr[i].indexOf(":")));
						}
					}
				}
				for (String headKey : headerKeys) {
					if (headKey.toLowerCase().startsWith("cookie")) {
						Map<String, String> cookies = HeadersManager.readFileCookiesToMap();
						String[] cookieMemArr = headKey.substring(headKey.indexOf("(") + 1, headKey.indexOf(")"))
								.replace(" ", "").split(";");
						String cookieValue = "";
						for (int i = 0; i < cookieMemArr.length; i++) {
							if (i == cookieMemArr.length - 1) {
								if (cookieMemArr[i].contains("=")) {
									cookieValue += cookieMemArr[i];
								} else {
									cookieValue += cookieMemArr[i] + "=" + cookies.get(cookieMemArr[i]);
								}
							} else {
								if (cookieMemArr[i].contains("=")) {
									cookieValue += cookieMemArr[i] + "; ";
								} else {
									cookieValue += cookieMemArr[i] + "=" + cookies.get(cookieMemArr[i]) + "; ";
								}
							}
						}
//						log.info(cookieValue);
						connection.setRequestProperty("Cookie", cookieValue);
					} else {
						connection.setRequestProperty(headKey, map.get(headKey));
					}
				}
			}
			method = method.toUpperCase();
			if (method.equals("POST") || method.equals("DELETE") || method.equals("PUT")) {
				connection.setRequestMethod(method);
				if (body.startsWith("{")) {
					connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				} else {
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				}
				connection.setDoOutput(true);
				String contentLength = body.length() + "";
				connection.setRequestProperty("Content-Length", contentLength);
			} else {
				connection.setRequestMethod("GET");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return connection;
	}
}
