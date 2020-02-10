package fg;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class b
 */
@WebServlet("/b")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }
    private static Connection connection;
	private static String sqlName = "com.mysql.cj.jdbc.Driver",
			url = "jdbc:mysql://localhost:3306/share?serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false",
			userName = "ljrtest",
			password = "asdfzxcv123",
			loginstr = "select * from user where username = ?";
	private static PreparedStatement login;
	static {
		connection = getDBConnection(sqlName, url, userName, password);
	}public static Connection getDBConnection(String sqlName,String url,String user,String password) {
		try {
			loadDriver(sqlName);
		} catch (ClassNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			System.out.println("��������ʧ��");
			return null;
		}
		Connection connection;
		try {
			connection = DriverManager.getConnection(url,user,password);
		} catch (SQLException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			System.out.println("�������ݿ�ʧ��");
			return null;
		}
		System.out.println("�������ݿ�ɹ�");
		return connection;
	}
	public static void loadDriver(String sqlName) throws ClassNotFoundException {
		Class.forName(sqlName);
	}
	public static boolean closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO �Զ����ɵ� catch ��
			System.out.println("�ر�����ʧ��");
			return false;
		}
		return true;
	}
	public static boolean isEmpty(String username,String password) {
    	if( (username!=null) && (password!=null)) {
    		if((username.length()>0) && (password.length()>0))
    			return false;
    	}
    	return true;
    }
	public static byte[] EncryptionStrBytes(String str, String algorithm) {
        // ����֮�������ֽ�����
        byte[] bytes = null;
        try {
            // ��ȡMD5�㷨ʵ�� �õ�һ��md5����ϢժҪ
            MessageDigest md = MessageDigest.getInstance(algorithm);
            //���Ҫ���м���ժҪ����Ϣ
            md.update(str.getBytes());
            //�õ���ժҪ
            bytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("�����㷨: "+ algorithm +" ������: ");
        }
        return null==bytes?null:bytes;
    }
    /**
     * ���ֽ�����ת�����ַ�������
     * @param bytes
     * @return
     */
    public static String BytesConvertToHexString(byte [] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte aByte : bytes) {
          String s=Integer.toHexString(0xff & aByte);
            if(s.length()==1){
                sb.append("0"+s);
            }else{
                sb.append(s);
            }
        }
        return sb.toString();
    }
    /**
     * ���ü����㷨�����ַ�������
     * @param str   ��Ҫ���ܵ�����
     * @param algorithm ���õļ����㷨
     * @return �ֽ�����
     */
    public static String EncryptionStr(String str, String algorithm) {
        // ����֮�������ֽ�����
        byte[] bytes = EncryptionStrBytes(str,algorithm);
        return BytesConvertToHexString(bytes);
    }
	public static boolean check(String my,String correct) {
		return my.equals(correct);
	}
	public static boolean validateData(String username,String usernameT,String password,String passwordT) {
		if( (check(username, usernameT)) && (check(password, passwordT)))
			return true;
		return false;
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		PrintWriter writer = response.getWriter();
		if(!isEmpty( username, password)) {
			System.out.println(username+password);
			try {
				login = connection.prepareStatement(loginstr);
				login.setString(1, username);
				ResultSet set = login.executeQuery();
				String usernameT = null,passwordT = null;
				if(set.next()) {
					
					usernameT = set.getString("username");
					passwordT = set.getString("password");
				}
				password = EncryptionStr(password, "MD5");
				boolean flag = validateData( username, usernameT, password, passwordT);
				if(flag) {
					writer.print("success");
					System.out.println("success login");
				}
				else {
					writer.print("fail");
					System.out.println("fail login");
				}
				writer.flush();
				writer.close();
				
			} catch (SQLException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
