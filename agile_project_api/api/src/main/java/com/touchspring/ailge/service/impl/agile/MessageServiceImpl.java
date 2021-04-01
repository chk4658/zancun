package com.touchspring.ailge.service.impl.agile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchspring.ailge.dao.agile.MessageDao;
import com.touchspring.ailge.entity.agile.Message;
import com.touchspring.ailge.service.agile.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements MessageService {

}
