package model;

import java.sql.Connection;
import java.sql.DriverManager;
public class Connector {
    public Connection connect() {
		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");
													
                        final Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:admin", "Hossam", "root");
			return con;
                        
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}