package com.blemobi.payment.test;

import org.apache.ibatis.session.SqlSession;

import com.blemobi.payment.mapper.UserMapper;
import com.blemobi.payment.model.User;

public class UserService {

	public static void main(String[] args) {
		insertUser();
		// deleteUser();
		// selectUserById();
		// selectAllUser();
	}

	/**
	 * 新增用户
	 */
	private static void insertUser() {
		SqlSession session = DBTools.getSession();
		UserMapper mapper = session.getMapper(UserMapper.class);
		User user = new User();
		user.setId("1");
		user.setName("张三".getBytes());
		user.setAge(1);
		try {
			mapper.insert(user);
			System.out.println(user.toString());
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
		}
	}
}