package com.blemobi.payment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.library.util.ReslutUtil;
import com.blemobi.payment.dao.BillDao;
import com.blemobi.payment.model.Bill;
import com.blemobi.payment.service.BillService;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PBill;
import com.blemobi.sep.probuf.PaymentProtos.PBillInfo;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

/**
 * 账单业务实现类
 * 
 * @author zhaoyong
 *
 */
@Service("billService")
public class BillServiceImpl implements BillService {

	@Autowired
	private BillDao billDao;

	@Override
	public PMessage list(String uuid, int type, int idx, int count) throws IOException {
		PBill.Builder billBuilder = PBill.newBuilder();
		if (idx <= 0) {
			buildIncomeTotal(uuid, billBuilder);// 获得总收入
			buildExpendTotal(uuid, billBuilder);// 获得总支出
			idx = Integer.MAX_VALUE;// 默认从从最新数据开始
		}

		// 获得账单
		List<Bill> billList = selectBill(uuid, type, idx, count);
		List<PBillInfo> billInfoList = buildBillInfo(type, billList);

		PBill bill = billBuilder.addAllBillInfo(billInfoList).build();
		return ReslutUtil.createReslutMessage(bill);
	}

	/**
	 * 获取总收入
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param billBuilder
	 */
	private void buildIncomeTotal(String uuid, PBill.Builder billBuilder) {
		Map<String, Object> map = billDao.selectTotalMoney(uuid, 1);
		int income_money = Integer.parseInt(map.get("total").toString());
		int income_number = Integer.parseInt(map.get("count").toString());
		billBuilder.setIncomeMoney(income_money).setIncomeNumber(income_number);
	}

	/**
	 * 获得总支出
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param billBuilder
	 */
	private void buildExpendTotal(String uuid, PBill.Builder billBuilder) {
		Map<String, Object> map = billDao.selectTotalMoney(uuid, 0);
		int expend_money = Integer.parseInt(map.get("total").toString());
		int expend_number = Integer.parseInt(map.get("count").toString());
		billBuilder.setExpendMoney(expend_money).setExpendNumber(expend_number);
	}

	/**
	 * 查询账单
	 * 
	 * @param uuid
	 * @param type
	 * @param last_id
	 * @param count
	 * @return
	 */
	private List<Bill> selectBill(String uuid, int type, int last_id, int count) {
		if (type == 0) // 收入
			return billDao.selectByPage(uuid, 1, last_id, count);
		else if (type == 1) // 支出
			return billDao.selectByPage(uuid, 0, last_id, count);
		return null;
	}

	/**
	 * 获得账单
	 * 
	 * @param type
	 *            类型（0-收入 1-支出）
	 * @param billList
	 * @return
	 * @throws IOException
	 */
	private List<PBillInfo> buildBillInfo(int type, List<Bill> billList) throws IOException {
		List<PBillInfo> billInfoList = new ArrayList<PBillInfo>();
		for (Bill bill : billList) {
			PBillInfo billInfo = dsaf(type, bill);
			billInfoList.add(billInfo);
		}
		return billInfoList;
	}

	/**
	 * 构建PBillInfo对象
	 * 
	 * @param type
	 *            类型（0-收入 1-支出）
	 * @param bill
	 *            账单信息
	 * @return
	 * @throws IOException
	 */
	private PBillInfo dsaf(int type, Bill bill) throws IOException {
		PBillInfo.Builder billInfoBuilder = PBillInfo.newBuilder().setId(bill.getId()).setOrdNo(bill.getOrd_no())
				.setMoney(bill.getMoney()).setTime(bill.getTime()).setType(bill.getType());
		if (type == 0) {// 收入账单需要发送用户信息
			PUserBase userBase = UserBaseCache.get(bill.getUuid());
			billInfoBuilder.setUserBase(userBase);
		}
		return billInfoBuilder.build();
	}
}
