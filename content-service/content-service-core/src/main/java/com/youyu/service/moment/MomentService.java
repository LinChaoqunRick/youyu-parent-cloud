package com.youyu.service.moment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.moment.MomentListInput;
import com.youyu.dto.moment.MomentListOutput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentUserOutput;

import java.util.List;

/**
 * (Moment)表服务接口
 *
 * @author makejava
 * @since 2023-05-21 23:22:12
 */
public interface MomentService extends IService<Moment> {
    MomentListOutput create(Moment input);

    boolean delete(Long momentId);

    PageOutput<MomentListOutput> getMomentList(MomentListInput input);

    PageOutput<MomentListOutput> getMomentListFollow(MomentListInput input);

    MomentListOutput getMoment(Long momentId);

    MomentUserOutput getMomentActor(Long actorId, int actorType, boolean enhance);

    boolean isMomentLike(Long momentId);

    List<MomentListOutput> momentListByIds(List<Long> momentIds);
}

