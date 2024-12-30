package guide.app.common;


import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import psdi.server.MXServer;
import psdi.util.MXException;

public class ComExecute {

	public static void executeSql(String sql) throws MXException, RemoteException, SQLException{
		Connection connection = null;
	  	Statement stmt = null;
		try {
			connection = MXServer.getMXServer().getDBManager().getConnection(MXServer.getMXServer().getSystemUserInfo().getConnectionKey());
			stmt = connection.createStatement();
			try {
				stmt.execute(sql);
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				System.out.println(sql+e);
			}			
		} catch (RemoteException e) {
		} catch (Exception e) {
		}
		finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e1) {
				}
			}
			try {
				MXServer.getMXServer().getDBManager().freeConnection(MXServer.getMXServer().getSystemUserInfo().getConnectionKey());
				if (connection != null) {
					connection.close();
				}
			} catch (RemoteException e1) {
			} catch (Exception e1) {
			}
		}
		
	}
	
	public static Double Query(String sql) throws MXException, RemoteException, SQLException{
		double sumcost = 0.0;
		Connection connection = null;
	  	Statement stmt = null;
		try {
			connection = MXServer.getMXServer().getDBManager().getConnection(MXServer.getMXServer().getSystemUserInfo().getConnectionKey());
			stmt = connection.createStatement();
			try {
				ResultSet rset = stmt.executeQuery(sql);
				while (rset.next()) {
					sumcost = rset.getDouble("sumcost");
				}	
				rset.close();
			} catch (SQLException e) {
				connection.rollback();
				System.out.println(sql+e);
			}			
		} catch (RemoteException e) {
		} catch (Exception e) {
		}
		finally {
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
			try {
				MXServer.getMXServer().getDBManager().freeConnection(MXServer.getMXServer().getSystemUserInfo().getConnectionKey());
				if (connection != null) {
					connection.close();
				}
			} catch (RemoteException e1) {
			} catch (Exception e1) {
			}
		}
		return sumcost;
	}
	
	
}
