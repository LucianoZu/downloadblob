package it.zu.downloadblob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DownloadblobApplication implements CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(DownloadblobApplication.class);
	
	@Value("${connection.url}")
	private String connectionURL;

	@Value("${connection.user}")
	private String connectionUser;

	@Value("${connection.password}")
	private String connectionPassword;
	
	@Value("${download.folder}")
	private String folder;

	@Value("${sql.filename}")
	private String sqlFileName;

	public static void main(String[] args) {
		SpringApplication.run(DownloadblobApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		downloadBlob();
		
	}
	
	private void downloadBlob() {
		Connection conn = getConnection(); // Initialize connection / get connection
		PreparedStatement pst = null;
		try {
			
			String query = getQuery();
			pst = conn.prepareStatement(query);
			
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				InputStream is = rs.getBinaryStream(1);
				String fileName = rs.getString(2);
				String partner = rs.getString(3);
				String messageref = rs.getString(4);
				String status = rs.getString(5);
				Date timestamp = rs.getDate(6);
				Date date = rs.getDate(7);

				logger.info(String.format("Download file: %s for partner %s reference %s status %s timestamp %s date %s"
						,fileName, partner, messageref, status, timestamp, date));
				
				fileName =  partner + "_" + messageref + "_" + fileName;

				File file = new File(folder, fileName);
				FileOutputStream outputStream = new FileOutputStream(file);
				
				byte[] buffer = new byte[1024];
				int bytesRead = is.read(buffer);
				while(bytesRead != -1) {
					outputStream.write(buffer, 0, bytesRead);
					bytesRead = is.read(buffer);
				}
				outputStream.close();
				is.close();

				logger.info(String.format("Downloaded file: %s", fileName));
			}
			logger.info("Job DONE.");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(pst);
			close(conn);
		}
	}

	private Connection getConnection() {

		Connection retvalue = null;
		try {
			logger.info("Searching JDBC Oracle driver...");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			logger.info("Driver JDBC Oracle found");
			
			logger.info(String.format("Oracle connection with user %s. Connection URL: %s", connectionUser, connectionURL));
			retvalue = DriverManager.getConnection(connectionURL, connectionUser, connectionPassword);
			retvalue.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Connection error! Connection URL: " + connectionURL, e);
			close(retvalue);
			retvalue = null;
		} catch (ClassNotFoundException e) {
			logger.error("Driver JDBC Oracle not found", e);
		}
		return retvalue;
	}

	private void close(AutoCloseable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (Exception e) {
			logger.warn("Close doesn't work.", e);
		}
	}

	private String getQuery() {

		StringBuffer retvalue = new StringBuffer();
		FileInputStream is = null;
		InputStreamReader isReader = null;
		BufferedReader reader = null;
		try {
			File file = new File(sqlFileName);
			is = new FileInputStream(file);
			isReader = new InputStreamReader(is);
			reader = new BufferedReader(isReader);
			String line = reader.readLine();
			while (line != null) {
				retvalue.append(line);
				retvalue.append("\n");
				line = reader.readLine();
			}
		} catch (Exception e) {
			logger.error("Error reading SQL query.", e);
		} finally {
			close(is);
			close(isReader);
			close(reader);
		}
		
		return retvalue.toString();
	}

}
