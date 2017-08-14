package com.itheima.luence.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.itheima.luence.domain.Book;

public class BookDaoImpl implements BookDao {
	
	public List<Book> queryBookList() {
		// ���ݿ�����
		Connection connection = null;
		// Ԥ����statement
		PreparedStatement preparedStatement = null;
		// �����
		ResultSet resultSet = null;
		// ͼ���б�
		List<Book> list = new ArrayList<Book>();

		try {
			// �������ݿ�����
			Class.forName("com.mysql.jdbc.Driver");
			// �������ݿ�
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/luence_day01", "hushihai", "hushihai123");

			// SQL���
			String sql = "SELECT * FROM book";
			// ����preparedStatement
			preparedStatement = connection.prepareStatement(sql);
			// ��ȡ�����
			resultSet = preparedStatement.executeQuery();
			// ���������
			while (resultSet.next()) {
				Book book = new Book();
				book.setId(resultSet.getInt("id"));
				book.setName(resultSet.getString("name"));
				book.setPrice(resultSet.getFloat("price"));
				book.setPic(resultSet.getString("pic"));
				book.setDesc(resultSet.getString("description"));
				list.add(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
