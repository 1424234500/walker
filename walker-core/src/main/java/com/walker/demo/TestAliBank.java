//
//实现转账系统，给外部系统提供账户开户，充值，转账rpc服务，要求如下，
//
//		- 账户类设计：类名：Account，包含属性账号，账户余额，持有人身份证账号
//		- 开户功能：
//		- 入参：开户人身份证账号
//		- 功能逻辑：创建对应的Account，并持久化
//		- 返回值：账号
//		- 要求：一个身份证只允许有一个Account，每个Account的账号唯一
//		- 充值功能：
//		- 入参：账号，充值金额，请求号（请求号唯一）
//		- 功能逻辑：将充值金额增加到对应账户的余额中去
//		- 返回值：余额
//		- 转账功能：
//		- 入参：转出账号，转入账号，转账金额，请求号（请求号唯一）
//		- 功能逻辑：转出账号余额减少，转入账号余额增加
//		- 返回值：转出账号余额
//		- 其他说明
//		- 上游系统在调用相关服务超时的情况下，会使用原请求发起重试
//		- 充值与充值，转账与转账，充值与转账之间存在并发情况
//		- 考虑性能
//		- 限定单机提供rpc服务(代码提供rpc接口和实现即可，不用考虑如何发布rpc)，账户持久化上到jvm内存中，不需要使用DB，基于内存的持久化默认有事务，不用特别考虑事务问题
//		- 直接基于jdk编写，不依赖其他框架，get set方法可省略


//		- 账户类设计：类名：Account，包含属性账号，账户余额，持有人身份证账号
//@Model
//class Account{
//	String idcard;
//	AtomicDouble money;
//	String id;
//
//}
//
//@Service
//class Center{
//
//	//  双索引 空间换时间
//	ConcrueentHashMap<String, Account> dataIdcard2Account = new ConcrueentHashMap<>();
//	ConcrueentHashMap<String, Account> dataId2Account = new ConcrueentHashMap<>();
//	//  票据使用记录 额外存储一些相关信息 如 当事人
//	ConcrueentHashMap<String, Double> seq2BeforeMoney = new ConcrueentHashMap<>();
//
//  - 开户功能：
//			- 入参：开户人身份证账号
//  - 功能逻辑：创建对应的Account，并持久化
//  - 返回值：账号
//  - 要求：一个身份证只允许有一个Account，每个Account的账号唯一
//	// 考虑开户量少 简单使用synchronized
//	synchronized String create(String idcard){
//		Assert.notNull(idcard);
//
//		if(data.containsKey(idcard){
//			throw new Exception("has exists account " +  idcard);
//		}
//		Account res = new Account(idcard, 0D, makeId(idcard) );
//		dataIdcard2Account.put(res.idcard, res);
//		dataId2Account.put(res.id, res);
//
//		return res.id;
//	}
//  - 充值功能：
//			- 入参：账号，充值金额，请求号（请求号唯一）
//			- 功能逻辑：将充值金额增加到对应账户的余额中去
//  - 返回值：余额
//	Double addMoney(String id, Double money, String seq){
//		Account account = dataId2Account.get(id);
//		Assert.notNull(id, money, seq);
//
//		//依靠seq获取是否已经执行过 保证幂等
//		Double lastMoney = seq2BeforeMoney.get(seq);
//		//内存存储 依赖atomic实现原子操作 db存储 依赖事务
//		AtomicDouble res = account.money;
//		if(lastMoney == null){
//			seq2BeforeMoney.put(seq, res);
//			res.compareAndAdd(money);
//		}else{
//			// throw new Exception("has consumer the seq " +  seq + " and lastMoney " + lastMoney);
//		}
//		return res;
//	}
//  - 转账功能：
//			- 入参：转出账号，转入账号，转账金额，请求号（请求号唯一）
//			- 功能逻辑：转出账号余额减少，转入账号余额增加
//  - 返回值：转出账号余额
//	Double moveMoney(String idFrom, String idTo, Double money, String seq){
//		Assert.notNull(idFrom, idTo, money, seq);
//		Account accountFrom = dataId2Account.get(idFrom);
//		Account accountTo = dataId2Account.get(idTo);
//		Assert.notNull(accountFrom, accountTo);
//		//依靠seq获取是否已经执行过 保证幂等
//		Double lastMoney = seq2BeforeMoney.get(seq);
//		//依赖atomic实现原子操作
//		AtomicDouble res = accountTo.money;
//
//		if(lastMoney == null){
//			//        内存存储 两步原子问题 补偿 最小锁区域 避免死锁   排序 依次锁定from to资源
//			// 单机情况 如果是db存储 多条sql修改  事务 transaction保证
//			lockObj1 = accountFrom.id.compareTo(accountTo.id) > 0 ? accountTo : accountFrom
//			lockObj2 = accountFrom.id.compareTo(accountTo.id) <= 0 ? accountTo : accountFrom
//
////             非单机 考虑分布式锁
//			trylock(lockObj1, 10秒)
//			try{
//				trylock(lockObj2, 10秒)
//				try{
//					seq2BeforeMoney.put(seq, res);
//
//					accountFrom.money.compareAndAdd(-1 * money);
//					accountTo.money.compareAndAdd(money);
//					res = accountTo.money;
//				}finally{
//					unlock(lockObj2)
//				}
//			}finally{
//				unlock(lockObj1)
//			}
//		}else{
//			// throw new Exception("has consumer the seq " +  seq + " and lastMoney " + lastMoney);
//		}
//		return res;
//	}
//
//
//}
