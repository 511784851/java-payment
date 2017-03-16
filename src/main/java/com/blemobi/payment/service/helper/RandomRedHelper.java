package com.blemobi.payment.service.helper;

import java.util.Random;

import com.blemobi.payment.util.Constants;

/**
 * 随机红包分配算法（将分配好的随机金额存储在Redis的队列中，当有用户领取红宝时，从队列顶部弹出一个金额）
 * 
 * 单个随机红包金额需求如下：
 * 
 * 1.范围不超过0.01-200元，
 * 
 * 2.单个随机红包的金额额度在0.01和剩余平均值×2之间， 保证剩余用户能拿到最低1分钱即可。
 * 例如：发100块钱，总共10个红包，那么平均值是10块钱一个，那么发出来的红包的额度在0.01元～20元之间波动。
 * 当前面3个红包总共被领了40块钱时，剩下60块钱，总共7个红包，那么这7个红包的额度在：0.01～（60/7×2）=17.14之间。
 * 注意：这里的算法是每被抢一个后，剩下的会再次执行上面的这样的算法。
 * 
 * @author zhaoyong
 *
 */
public class RandomRedHelper {

	/** 随机红包总金额 */
	private int tota_money;

	/** 随机红包总数量 */
	private int tota_number;

	/** 生成的随机金额数据 */
	private int[] random_money_array;

	/**
	 * 构造方法
	 * 
	 * @param tota_money
	 * @param tota_number
	 */
	public RandomRedHelper(int tota_money, int tota_number) {
		this.tota_money = tota_money;
		this.tota_number = tota_number;
		random_money_array = new int[tota_number];
	}

	/**
	 * 随机金额分配
	 * 
	 * @return
	 */
	public int[] distribution() {
		calculation(0, tota_money);
		check();
		return random_money_array;
	}

	/**
	 * 递归方法
	 * 
	 * @param idx_number
	 *            第几个红包（0开始）
	 * @param surplus_money
	 *            剩余总金额
	 */
	private void calculation(int idx_number, int surplus_money) {
		// 本次随机金额
		int each_money = doRandomMoney(idx_number, surplus_money);
		// 记录下来
		random_money_array[idx_number] = each_money;

		if (++idx_number == tota_number)
			return;
		calculation(idx_number, surplus_money - each_money);
	}

	/**
	 * 生成本次随机金额
	 * 
	 * @param idx_number
	 * @param money
	 * @return
	 */
	private int doRandomMoney(int idx_number, int surplus_money) {
		// 本次剩余的红包数量
		int surplus_number = tota_number - idx_number;
		// 如果是最后一次，随机金额就是剩余金额
		if (surplus_number == 1)
			return surplus_money;
		// 领完本次剩余的红包数量
		int next_surplus_number = surplus_number - 1;
		// 计算出随机金额最小值
		int min_random_money = get_min_random_money(surplus_money, next_surplus_number);
		// 计算出随机金额最大值
		int max_random_money = get_max_random_money(surplus_money, surplus_number, next_surplus_number);
		// 产生一个从min_random_money到max_random_money之间的随机数
		Random random = new Random();
		return random.nextInt(max_random_money - min_random_money + 1) + min_random_money;
	}

	/**
	 * 计算出随机金额最小值
	 * 
	 * @param surplus_money
	 * @param next_surplus_number
	 * @return
	 */
	private int get_min_random_money(int surplus_money, int next_surplus_number) {
		// 为了保证领完本次剩余的红包单个金额不超过max_each_money，计算出本次领取后最多剩余金额
		int max_surplus_money = next_surplus_number * Constants.max_each_money;
		// 根据上一步计算出本次最少要领取的金额
		int min_random_money = surplus_money - max_surplus_money;
		if (min_random_money < Constants.min_each_money)
			min_random_money = Constants.min_each_money;
		return min_random_money;
	}

	/**
	 * 计算出随机金额最大值
	 * 
	 * @param surplus_money
	 * @param surplus_number
	 * @param next_surplus_number
	 * @return
	 */
	private int get_max_random_money(int surplus_money, int surplus_number, int next_surplus_number) {
		// 本次红包平均金额
		int avg_money = surplus_money / surplus_number;

		// 初步计算出本次最多可领取的金额 （需求：随机金额在0.1到剩余平均值×2之间）
		int max_random_money = avg_money * 2;
		if (max_random_money > Constants.max_each_money)
			max_random_money = Constants.max_each_money;

		// 为了保证领完本次剩余的红包单个金额不不低于min_each_money，计算出本次领取后最少剩余金额为
		int min_surplus_money = next_surplus_number * Constants.min_each_money;
		// 根据上一步计算出本次最多可领取的金额
		if (max_random_money > surplus_money - min_surplus_money)
			max_random_money = surplus_money - min_surplus_money;
		return max_random_money;
	}

	/**
	 * 验证分配是否正确
	 */
	private void check() {
		int random_tota_money = 0;
		for (int i = 0; i < random_money_array.length; i++) {
			int random_money = random_money_array[i];
			if (random_money < Constants.min_each_money || random_money > Constants.max_each_money)
				throw new RuntimeException("随机红包单个金额不符合规则：" + random_money);
			random_tota_money += random_money;
		}
		if (random_tota_money != tota_money)
			throw new RuntimeException("随机红包分配金额总和错误：" + random_tota_money);
	}

	/**
	 * 测试一下 ok
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 10000; i++) {
			RandomRedHelper rdh1 = new RandomRedHelper(3, 2);
			int[] r1 = rdh1.distribution();
			for (int r = 0; r < r1.length; r++)
				System.out.print(r1[r] + ",");
			System.out.println();
		}
	}

}