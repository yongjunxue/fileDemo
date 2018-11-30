package cn.com.testor.dnsmanager.v3.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JdbcUtil
{
  private static ThreadLocal<Connection> conns = new ThreadLocal();
  
  public static Connection getConnection()
  {
    Connection conn = (Connection)conns.get();
    if (conn == null)
    {
      InputStream in = JdbcUtil.class.getResourceAsStream("/DBConfig/DbConfig.properties");
      Properties p = new Properties();
      try
      {
        p.load(in);
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(p.getProperty("jdbc.jdbcUrl"), p.getProperty("jdbc.user"), p.getProperty("jdbc.testor"));
        conns.set(conn);
      }
      catch (Exception e)
      {
        throw new RuntimeException(e);
      }
    }
    return conn;
  }
  
  public static void closeResource(ResultSet rs, Statement stmt)
  {
    if (rs != null) {
      try
      {
        rs.close();
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
    if (stmt != null) {
      try
      {
        stmt.close();
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public static void closeConnection()
  {
    Connection conn = (Connection)conns.get();
    if (conn != null) {
      try
      {
        conn.close();
        conns.set(null);
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public static void main(String[] args)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      conn = getConnection();
      

      String sql = "select * from t_dms_user;";
      stmt = conn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        System.out.println(
          rs.getString(1) + 
          "|" + 
          rs.getString(2));
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    finally
    {
      closeResource(rs, stmt);
      closeConnection();
    }
  }
}
