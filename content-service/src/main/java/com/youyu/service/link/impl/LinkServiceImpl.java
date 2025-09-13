package com.youyu.service.link.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.mapper.link.LinkMapper;
import com.youyu.entity.link.Link;
import com.youyu.service.link.LinkService;
import org.springframework.stereotype.Service;

/**
 * (Link)表服务实现类
 *
 * @author makejava
 * @since 2025-09-11 15:27:01
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

}

