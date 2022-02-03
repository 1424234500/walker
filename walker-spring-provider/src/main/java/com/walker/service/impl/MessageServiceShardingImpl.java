package com.walker.service.impl;

import com.walker.core.database.SqlUtil;
import com.walker.dao.JdbcTemplateDao;
import com.walker.dao.MessageRepository;
import com.walker.dao.MessageUserRepository;
import com.walker.dao.RedisDao;
import com.walker.mode.Key;
import com.walker.mode.Message;
import com.walker.mode.MessageUser;
import com.walker.mode.Msg;
import com.walker.service.MessageService;
import com.walker.util.LangUtil;
import com.walker.util.TimeUtil;
import com.walker.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 离线消息积压队列
 * 上线后 先拉离线 清空 再接收新消息
 * <p>
 * 修改		jpa-sharding
 * 查询1	jpa-sharding
 * 查询2	jpa-sharding
 */
@Service("messageServiceSharding")
public class MessageServiceShardingImpl implements MessageService {
	final static String TABLE_MSG = "W_SHARDING_MSG";
	final static String TABLE_MSG_USER = "W_SHARDING_MSG_USER";
	final static String TABLE_MSG_ = TABLE_MSG + "_";
	final static String TABLE_MSG_USER_ = TABLE_MSG_USER + "_";
	final static int COUNT_MSG = 2;
	final static int COUNT_MSG_USER = 4;
	Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	RedisDao redisDao;
	@Autowired
	JdbcTemplateDao jdbcTemplateDao;
	@Autowired
	RedisTemplate redisTemplate;
	@Autowired
	MessageRepository messageRepository;
	@Autowired
	MessageUserRepository messageUserRepository;

	/**
	 * 消息每张分表数量
	 */
	public List<Integer> sizeMsg() {
		List<Integer> res = new ArrayList<>();

		res.add((int) messageRepository.count());

		log.info(res.toString());
		return res;
	}

	/**
	 * 消息关联用户每张分表数量
	 */
	public List<Integer> sizeMsgUser() {
		List<Integer> res = new ArrayList<>();
		res.add((int) messageUserRepository.count());
		log.info(res.toString());
		return res;
	}

	@Override
	public Long save(final List<String> toIds, final Msg msg) {
		try {
			//insert into W_MSG_0 values()
			//insert into W_MSG_1 values()
			//保存消息实体
			msg.setId(LangUtil.getGenerateId());
			String msgId = msg.getId();
//			jdbcTemplateDao.executeSql("insert into " + TABLE_MSG_ + SqlUtil.makeTableCount(COUNT_MSG,msgId) + " values(?,?)",  msgId, msg.toString() );
			Message message = new Message().setId(msgId).setText(msg.toString());
			messageRepository.save(message);
			List<MessageUser> dbLines = new ArrayList<>();
			String fromId = msg.getFromUser().getId();
			for (String toId : toIds) {
				String id = SqlUtil.makeTableKey(fromId, toId);
				String tableName = TABLE_MSG_USER_ + SqlUtil.makeTableCount(COUNT_MSG_USER, id);

				MessageUser line = new MessageUser();
				line.setId(id).setUserFrom(fromId).setUserTo(toId).setMsgId(msgId).setSmtime("" + msg.getTimeServerDo());
				dbLines.add(line);
			}

			if (dbLines.size() > 0) {
//				jdbcTemplateDao.executeSql("INSERT INTO " + db + " VALUES(?,?,?,?,?) ", lines);
				messageUserRepository.saveAll(dbLines);
			}
//-消息 分表ID 消息id - 消息json串
//CREATE TABLE  IF NOT EXISTS  W_MSG_0 (ID VARCHAR(40), TEXT TEXT);
//--消息映射用户 分表ID 用户会话a:b - 消息id - 时间
//CREATE TABLE  IF NOT EXISTS  W_MSG_USER_0 (ID VARCHAR(200), USER_FROM VARCHAR(40), USER_TO VARCHAR(40), MSG_ID VARCHAR(40), TIME VARCHAR(20) );

//{"TD":1562058992216,"ST":"000","SF":"223.104.213.72:61511",
//	"DATA":
//			{"STA":"","TEXT":"11111111","ID":"34268913073696_76VkEy","FILE":"","TYPE":"TEXT"}
//,"TO":"91","FROM":{"ID":"000","PWD":"","NAME":"傻子就是你"}
//,"WS":0,"TYPE":"message","TR":1562058992197,"TC":1562058991442}]

		} catch (Exception e) {
			log.error("save msg error " + msg.toString(), e);
		}

		ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
		long score = msg.getTimeServerDo();
		for (String toId : toIds) {
			String key = Key.getKeyOffline(toId);
			zSetOperations.add(key, msg.toString(), score);
			log.info(key + " save " + score);

			if (zSetOperations.zCard(key) > redisDao.getConfig("max:offline", 500)) {
				//).zremrangeByRank
				long res = zSetOperations.removeRange(key, 0, 0);
				log.info(key + " rem " + res);
			}
		}
		return score;
	}


	/**
	 * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表
	 * //		mysql> select * from W_MSG_1;
	 * //		+-----------------------+----------------------------------------------------------------------------+
	 * //		| ID                    | TEXT                                                                       |
	 * //		+-----------------------+----------------------------------------------------------------------------+
	 * //		| 35689868912060_Prd_iu | {"TD":1562069257362,"DATA":{"count":0},"TO":"001,002,003","TYPE":"message" |
	 * //
	 * //		mysql> select * from W_MSG_USER_3;
	 * //		+----------+-----------+---------+-----------------------+---------------+
	 * //		| ID       | USER_FROM | USER_TO | MSG_ID                | TIME          |
	 * //		+----------+-----------+---------+-----------------------+---------------+
	 * //		| 000:003: | 000       | 003     | 35689868912060_Prd_iu | 1562069257362 |
	 *
	 * @param userId
	 * @param before
	 * @param count
	 */
	@Override
	public List<Msg> findBefore(String userId, String toId, String before, int count) {
		return findBeforeByMerge(userId, toId, before, count);
	}

	/**
	 * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表
	 *
	 * @param userId
	 * @param before
	 * @param count
	 */
	@Override
	public List<Msg> findBeforeByMerge(String userId, String toId, String before, int count) {
		log.info(Tools.objects2string("findBefore", userId, toId, before, count));
		List<Msg> list = new ArrayList<>();
		String id = SqlUtil.makeTableKey(userId, toId);
		String tableName = TABLE_MSG_USER_ + SqlUtil.makeTableCount(COUNT_MSG_USER, id);
		Long time = TimeUtil.format(before, "yyyy-MM-dd HH:mm:ss:SSS");

//		List<Map<String, Object>> ids = jdbcTemplateDao.findPage("SELECT * FROM " + tableName + " WHERE ID=? AND TIME < ? order by TIME desc ", 1, count, id, time);
		//分页信息

//		Sort sort = new Sort(Sort.Direction.ASC, "ID");
		Pageable pageable = PageRequest.of(0, count);

		log.info(pageable.toString());

		Page<MessageUser> pageIds = messageUserRepository.selfFindPageOnceJpql(id, time, pageable);
		List<MessageUser> messageUsers = pageIds.getContent();
		long cc = pageIds.getTotalElements();

		List<String> ids = new ArrayList<>();
		for (MessageUser map : messageUsers) {
			String userFrom = String.valueOf(map.getUserFrom());
			String userTo = String.valueOf(map.getUserTo());
			String msgId = String.valueOf(map.getMsgId());
			ids.add(msgId);
//			Msg msg = this.findMsg(msgId);
//			if(msg != null) {
//				list.add(msg);
//			}else {
//				log.warn("msg null ? " + userFrom + " " + userTo + " " + msgId);
//			}
		}
		List<Message> messages = messageRepository.findAllById(ids);
		for (Message message : messages) {
			String text = message.getText();
			list.add(Msg.parse(text));
		}

		return list;
	}

	@Override
	public List<Msg> findAfter(String userId, String after, int count) {
		ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

		List<Msg> list = new ArrayList<>();
		String key = Key.getKeyOffline(userId);
		Long b = TimeUtil.format(after, "yyyy-MM-dd HH:mm:ss:SSS");
//				Set<String> set = jedis.zrevrangeByScore(key, b - 1, 0, 0, count);//获取上面的 旧的
		Set<String> set = zSetOperations.rangeByScore(key, b + 1, Double.MAX_VALUE, 0, count);
//				jedis.zrangeByScore(key, b+1, Double.MAX_VALUE, 0, count);	//获取下面的 新的
		log.info("findAfter " + key + " " + after + " " + b + " " + Double.MAX_VALUE + " " + set.size());
		for (String str : set) {
			list.add(Msg.parse(str));
		}
		return list;

	}

	@Override
	public Msg findMsg(String msgId) {
//		Map<String,Object> map = jdbcTemplateDao.findOne("SELECT * FROM " + TABLE_MSG_ + SqlUtil.makeTableCount(COUNT_MSG,msgId) + " WHERE ID=? ", msgId);
//		if(map == null) {
//			return null;
//		}
//		String text = String.valueOf(map.get("TEXT"));
//		return Msg.parse(text);
		return findMsgByMerge(msgId);
	}

	@Override
	public Msg findMsgByMerge(String msgId) {
//		Map<String,Object> map = jdbcTemplateDao.findOne("SELECT * FROM " + TABLE_MSG + " WHERE ID=? ", msgId);
//		if(map == null) {
//			return null;
//		}
//		String text = String.valueOf(map.get("TEXT"));
//		return Msg.parse(text);

		Optional<Message> o = messageRepository.findById(msgId);
		Message message = o.get();
		String text = message.getText();
		return Msg.parse(text);
	}


}
